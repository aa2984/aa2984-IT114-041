package Project.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Project.Common.ConnectionPayload;
import Project.Common.Move;
import Project.Common.Payload;
import Project.Common.PayloadType;
import Project.Common.RPSPayload;
import Project.Common.TextFX;
import Project.Common.TextFX.Color;
import Project.Common.User;



//aa2984: 2026-July-14
//Summary: Added four new commands 
//rps takes a target id, the move downloads either rock , paper, scissors
//Accept and decline sends 'yes' or 'no' essentially

public enum Client {
    INSTANCE;

    private Socket server = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    final Pattern ipAddressPattern = Pattern
            .compile("/connect\\s+(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{3,5})");
    final Pattern localhostPattern = Pattern.compile("/connect\\s+(localhost:\\d{3,5})");
    private volatile boolean isRunning = true;
    private ConcurrentHashMap<Long, User> knownUsers = new ConcurrentHashMap<>();
    private User myUser = new User();

    private enum Command {
        CONNECT("/connect"),
        DISCONNECT("/disconnect"),
        QUIT("/quit"),
        USERS("/users"),
        REVERSE("/reverse"),
        SET_NAME("/name"),
        RPS_CHALLENGE("/rps"),
        MOVE("/move"),
        ACCEPT("/accept"),
        DECLINE("/decline");

        private final String trigger;

        Command(String trigger) {
            this.trigger = trigger;
        }

        public static Command fromText(String text) {
            if (text == null)
                return null;
            String lower1 = text.toLowerCase().trim();
            for (Command c1 : values()) {
                if (lower1.equals(c1.trigger) || lower1.startsWith(c1.trigger + " ")) {
                    return c1;
                }
            }
            return null;
        }
    }

    private Client() {
        System.out.println("Client Created");
    }

    public boolean isConnected() {
        if (server == null)
            return false;
        return server.isConnected() && !server.isClosed()
                && !server.isInputShutdown() && !server.isOutputShutdown();
    }

    private boolean connect(String address, int port) {
        try {
            server = new Socket(address, port);
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());
            System.out.println("Client connected");
            CompletableFuture.runAsync(this::listenToServer);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isConnected();
    }

    private boolean isConnection(String text) {
        Matcher ipMatcher1 = ipAddressPattern.matcher(text);
        Matcher localhostMatcher1 = localhostPattern.matcher(text);
        return ipMatcher1.matches() || localhostMatcher1.matches();
    }

    private boolean processClientCommand(String text) throws IOException {
        Command command1 = Command.fromText(text);
        System.out.println("Processing command: " + command1);
        if (command1 == null) {
            return false;
        }
        switch (command1) {
            case CONNECT:
                if (isConnection(text)) {
                    String myClientName1 = myUser.getClientName();
                    if (myClientName1 == null || myClientName1.isBlank()) {
                        System.out.println(TextFX.colorize("Set your name before connecting using `/name YourName`",
                                Color.YELLOW));
                        return true;
                    }
                    String[] parts1 = text.trim().replaceAll(" +", " ").split(" ")[1].split(":");
                    connect(parts1[0].trim(), Integer.parseInt(parts1[1].trim()));
                    sendConnectionData(myClientName1);
                } else {
                    System.out.println("Invalid format. Use: /connect localhost:3000 or /connect 192.168.1.x:3000");
                }
                return true;
            case QUIT:
                close();
                return true;
            case DISCONNECT:
                sendDisconnect();
                return true;
            case USERS:
                System.out.println(TextFX.colorize("Known clients:", Color.CYAN));
                knownUsers.forEach((key1, value1) -> {
                    System.out.println(TextFX.colorize(String.format("%s%s", value1.getDisplayName(),
                            key1 == myUser.getClientId() ? " (you)" : ""), Color.CYAN));
                });
                return true;
            case REVERSE:
                String reverseText1 = text.replace("/reverse", "").trim();
                sendReverse(reverseText1);
                return true;
            case SET_NAME:
                String name1 = text.replace("/name", "").trim();
                if (name1.isBlank()) {
                    System.out.println(TextFX.colorize("Name cannot be blank", Color.RED));
                } else {
                    myUser.setClientName(name1);
                    System.out.println(
                            TextFX.colorize("Name set to " + name1 + ".", Color.GREEN));
                }
                return true;
            case RPS_CHALLENGE:
                String challengeArgs1 = text.replace("/rps", "").trim();
                if (challengeArgs1.isBlank()) {
                    System.out.println(TextFX.colorize("Usage: /rps <target_id>", Color.YELLOW));
                    return true;
                }
                try {
                    long targetId1 = Long.parseLong(challengeArgs1);
                    sendRPSChallenge(targetId1);
                } catch (NumberFormatException e) {
                    System.out.println(TextFX.colorize("Target must be a numeric client id.", Color.RED));
                }
                return true;
            case MOVE:
                String moveArgs1 = text.replace("/move", "").trim();
                Move parsedMove1 = Move.fromText(moveArgs1);
                if (parsedMove1 == null) {
                    System.out.println(TextFX.colorize("Usage: /move <rock|paper|scissors>", Color.YELLOW));
                    return true;
                }
                sendRPSMove(parsedMove1);
                return true;
            case ACCEPT:
                sendRPSAccept(true);
                return true;
            case DECLINE:
                sendRPSAccept(false);
                return true;
            default:
                return false;
        }
    }

    private void sendConnectionData(String clientName) throws IOException {
        ConnectionPayload payload1 = new ConnectionPayload();
        payload1.setPayloadType(PayloadType.CLIENT_CONNECT);
        payload1.setClientName(clientName);
        sendToServer(payload1);
    }

    private void sendDisconnect() throws IOException {
        Payload payload1 = new Payload();
        payload1.setPayloadType(PayloadType.DISCONNECT);
        sendToServer(payload1);
    }

    private void sendReverse(String text) throws IOException {
        Payload payload1 = new Payload();
        payload1.setPayloadType(PayloadType.REVERSE);
        payload1.setMessage(text);
        sendToServer(payload1);
    }

    private void sendMessage(String text) throws IOException {
        Payload payload1 = new Payload();
        payload1.setPayloadType(PayloadType.MESSAGE);
        payload1.setMessage(text);
        sendToServer(payload1);
    }

    private void sendRPSChallenge(long targetUser) throws IOException {
        RPSPayload payload1 = new RPSPayload();
        payload1.setPayloadType(PayloadType.RPS_CHALLENGE);
        payload1.setTargetUser(targetUser);
        sendToServer(payload1);
    }

    private void sendRPSAccept(boolean accepted) throws IOException {
        RPSPayload payload1 = new RPSPayload();
        payload1.setPayloadType(PayloadType.RPS_ACCEPT);
        payload1.setAccepted(accepted);
        sendToServer(payload1);
    }

    private void sendRPSMove(Move move) throws IOException {
        RPSPayload payload1 = new RPSPayload();
        payload1.setPayloadType(PayloadType.RPS_MOVE);
        payload1.setMove(move);
        sendToServer(payload1);
    }

    private void sendToServer(Payload outgoingPayload) throws IOException {
        if (isConnected()) {
            out.writeObject(outgoingPayload);
            out.flush();
        } else {
            System.out.println("Not connected to server (hint: type `/connect host:port`)");
        }
    }

    public void start() throws IOException {
        System.out.println("Client starting");
        CompletableFuture<Void> inputFuture1 = CompletableFuture.runAsync(this::listenToInput);
        inputFuture1.join();
    }

    private void listenToServer() {
        try {
            while (isRunning && isConnected()) {
                try {
                    Payload fromServer1 = (Payload) in.readObject();
                    if (fromServer1 != null) {
                        processPayload(fromServer1);
                    } else {
                        System.out.println("Server disconnected");
                        break;
                    }
                } catch (ClassCastException | ClassNotFoundException cce) {
                    System.err.println("Error reading object as specified type: " + cce.getMessage());
                    cce.printStackTrace();
                }
            }
        } catch (IOException e) {
            if (isRunning) {
                System.out.println("Connection dropped");
                e.printStackTrace();
            }
        } finally {
            closeServerConnection();
        }
        System.out.println("listenToServer thread stopped");
    }

    private void processPayload(Payload payload) {
        if (payload == null || payload.getPayloadType() == null) {
            System.out.println("Received invalid payload: " + payload);
            return;
        }
        switch (payload.getPayloadType()) {
            case CLIENT_ID:
                processClientId(payload);
                break;
            case SERVER_JOIN:
            case SERVER_SYNC:
            case SERVER_LEAVE:
                processClientStatus(payload);
                break;
            case MESSAGE:
                processMessage(payload);
                break;
            case REVERSE:
                processReverse(payload);
                break;
            case DISCONNECT:
                System.out.println("Server acknowledged disconnect. Closing connection.");
                closeServerConnection();
                break;
            default:
                System.out.println("Received unhandled payload type: " + payload.getPayloadType());
        }
    }

    private void processReverse(Payload payload) {
        System.out.println(TextFX.colorize(payload.getMessage(), Color.PURPLE));
    }

    private void processMessage(Payload payload) {
        System.out.println(TextFX.colorize(payload.getMessage(), Color.BLUE));
    }

    private void processClientStatus(Payload payload) {
        if (!(payload instanceof ConnectionPayload)) {
            System.out.println(String.format("Expected ConnectionPayload for %s, got: %s", payload.getPayloadType(),
                    payload.getClass()));
            return;
        }
        PayloadType type1 = payload.getPayloadType();
        long clientId1 = payload.getClientId();
        String clientName1 = ((ConnectionPayload) payload).getClientName();
        User incomingUserData1 = new User(clientId1, clientName1);
        switch (type1) {
            case SERVER_JOIN:
                System.out.println(TextFX.colorize(incomingUserData1.getDisplayName() + " joined", Color.GREEN));
            case SERVER_SYNC:
                knownUsers.putIfAbsent(clientId1, incomingUserData1);
                break;
            case SERVER_LEAVE:
                User removedUser1 = knownUsers.remove(incomingUserData1.getClientId());
                if (removedUser1 != null) {
                    System.out.println(TextFX.colorize(removedUser1.getDisplayName() + " left", Color.RED));
                }
                break;
            default:
                System.out.println(TextFX.colorize("Unknown status type: " + type1, Color.YELLOW));
                break;
        }
    }

    private void processClientId(Payload payload) {
        if (!(payload instanceof ConnectionPayload)) {
            System.out.println("Expected ConnectionPayload for CLIENT_ID, got: " + payload.getClass());
            return;
        }
        long assignedId1 = payload.getClientId();
        String clientName1 = ((ConnectionPayload) payload).getClientName();
        myUser.setClientId(assignedId1);
        myUser.setClientName(clientName1);
        knownUsers.put(assignedId1, myUser);
        System.out.println(TextFX.colorize("Connected", Color.GREEN));
    }

    private void listenToInput() {
        try (Scanner si1 = new Scanner(System.in)) {
            System.out.println("Waiting for input");
            while (isRunning) {
                String userInput1 = si1.nextLine();
                if (!processClientCommand(userInput1)) {
                    sendMessage(userInput1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error in listenToInput(): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("listenToInput thread stopped");
    }

    private void close() {
        isRunning = false;
        closeServerConnection();
        System.out.println("Client terminated");
    }

    private void closeServerConnection() {
        knownUsers.clear();
        myUser.reset();
        try {
            if (out != null) {
                System.out.println("Closing output stream");
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (in != null) {
                System.out.println("Closing input stream");
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (server != null) {
                System.out.println("Closing connection");
                server.close();
                System.out.println("Closed socket");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client1 = Client.INSTANCE;
        try {
            client1.start();
        } catch (IOException e) {
            System.out.println("Exception from main()");
            e.printStackTrace();
        }
    }
}