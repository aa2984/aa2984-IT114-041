package M4.MCCS.Part1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private int port = 3000;
    // thread-safe map; multiple ServerThreads may call Server methods concurrently
    private final ConcurrentHashMap<Long, ServerThread> connectedClients = new ConcurrentHashMap<>();
    private boolean isRunning = true;

    private void start(int port) {
        this.port = port;
        System.out.println("Listening on port " + this.port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (isRunning) {
                System.out.println("Waiting for next client");
                Socket incomingClient = serverSocket.accept(); // blocks until a client connects
                System.out.println("Client connected");
                // third arg is a callback; ServerThread calls it once streams are open and it is ready
                ServerThread serverThread = new ServerThread(incomingClient, this, this::onServerThreadInitialized);
                serverThread.start();
                // not added to connectedClients here; that happens inside the callback after setup
            }
        } catch (IOException e) {
            System.err.println("Error accepting connection");
            e.printStackTrace();
        } finally {
            System.out.println("Server socket closed");
        }
    }

    //aa2984: 2026-June-26
    //Summary: Created handleCoinFlip function to generate random heads or tails Result
    //Used random function to pick the outcome and format the broadcast message
    //Finally broadcast Result from server to all connected clients using existing broadcast method

    //aa2984: 2026-July-1
    //Summary: Created handlePrivateMessage to send message to only sender + target
    //Looked up target by id in connectedClients map and sent formatted PM to both
    //Then if target not found, sender notified that user is not existing.
    /**
     * Callback from ServerThread once streams are open and it is ready to send/receive.
     * Registers the client and announces their arrival.
     */
    private synchronized void onServerThreadInitialized(ServerThread serverThread) {
        connectedClients.put(serverThread.getClientId(), serverThread);
        broadcast(null, String.format("*User[%s] connected*", serverThread.getClientId()));
    }

    /**
     * Internal disconnect: stops the thread, removes it from the map, and broadcasts a notice.
     * Used by handleDisconnect() and can be reused for server-side actions like kicks or timeouts.
     */
    private synchronized void disconnect(ServerThread serverThread) {
        serverThread.disconnect();
        connectedClients.remove(serverThread.getClientId());
        broadcast(null, String.format("User[%s] disconnected", serverThread.getClientId()));
    }

    /**
     * Sends a message to all connected clients.
     * Any client whose send fails is removed from the map.
     */
    private synchronized void broadcast(ServerThread sender, String message) {
        String senderLabel = sender == null ? "Server" : String.format("User[%s]", sender.getClientId());
        final String formatted = String.format("%s: %s", senderLabel, message);
        connectedClients.values().removeIf(serverThread -> !serverThread.sendToClient(formatted));
    }

    // handle* methods are the interface ServerThread uses to trigger Server actions

    /**
     * Called when a client requests to disconnect. Delegates to disconnect().
     */
    protected synchronized void handleDisconnect(ServerThread sender) {
        disconnect(sender);
    }

    /**
     * Sends the connected user list only to the requesting client.
     * The requester's entry is tagged with "(you)".
     */
    protected synchronized void handleGetUserList(ServerThread serverThread) {
        StringBuilder sb = new StringBuilder("Connected users:\n");
        connectedClients.forEach((id, st) -> {
            if (id == serverThread.getClientId()) {
                sb.append(String.format("  User[%s] (you)\n", id));
            } else {
                sb.append(String.format("  User[%s]\n", id));
            }
        });
        serverThread.sendToClient(sb.toString().trim());
    }


    /** Reverses the text and broadcasts the result. */
    protected synchronized void handleReverseText(ServerThread sender, String text) {
        StringBuilder sb = new StringBuilder(text);
        sb.reverse();
        broadcast(sender, sb.toString());
    }

    //aa2984: 2026-July-1
    //Summary: Created handleShuffleMessage to randomize characters 
    //Converted message to char array, and shuffled
    //The broadcast then shuffled result to said clients formatted properly
    protected synchronized void handleShuffleMessage(ServerThread sender, String message) {
        //split message up into characters
        java.util.List<Character> characters1 = new java.util.ArrayList<>();
        for (char character1 : message.toCharArray()) {
            characters1.add(character1);
        }
        java.util.Collections.shuffle(characters1);
        StringBuilder shuffledResult1 = new StringBuilder();
        for (char character1 : characters1) {
            shuffledResult1.append(character1);
        }

        String shuffleMessage1 = String.format("Shuffled from User[%s]: %s", sender.getClientId(), shuffledResult1.toString());
        broadcast(null, shuffleMessage1);
    }

    protected synchronized void handlePrivateMessage(ServerThread sender, long targetId, String message) {
        ServerThread targetClient1 = connectedClients.get(targetId);
        String pmFormatted1 = String.format("PM from User[%s]: %s", sender.getClientId(), message);
        if (targetClient1 == null) {
            sender.sendToClient("Server: User[" + targetId + "] not found");
            return;
        }
        //Last step to fulfill was to send to both sender and reciever 
        sender.sendToClient("Server: " + pmFormatted1);
        targetClient1.sendToClient("Server: " + pmFormatted1);
        }
    
        // will randomly pick head or tails, then properly format message, and send the Result
    protected synchronized void handleCoinFlip(ServerThread sender) {
        String flipResult1 = Math.random() < 0.5 ? "Heads" : "Tails";

        String flipMessage1 = String.format("User[%s] flipped a coin and got %s", sender.getClientId(), flipResult1);
        broadcast(null, flipMessage1);
    }

    /** Broadcasts a chat message from the sender to all clients. */
    protected synchronized void handleMessage(ServerThread sender, String text) {
        broadcast(sender, text);
    }

    public static void main(String[] args) {
        System.out.println("Server Starting");
        Server server = new Server();
        int port = 3000;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            // use default port
        }
        server.start(port);
        System.out.println("Server Stopped");
    }
}