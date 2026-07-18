package Project.Server;

import java.net.Socket;
import java.util.Objects;
import java.util.function.Consumer;

import Project.Common.ConnectionPayload;
import Project.Common.Constants;
import Project.Common.Move;
import Project.Common.Payload;
import Project.Common.PayloadType;
import Project.Common.RPSPayload;

public class ServerThread extends BaseServerThread {

    private final Consumer<ServerThread> onInitializationComplete;

    protected ServerThread(Socket myClient, Consumer<ServerThread> onInitializationComplete) {
        super(Objects.requireNonNull(myClient, "Client socket cannot be null"));
        this.onInitializationComplete = Objects.requireNonNull(onInitializationComplete, "Callback cannot be null");
        info("ServerThread created");
    }

    @Override
    protected void info(String message) {
        System.out.println(String.format("Thread[%s]: %s", this.getClientId(), message));
    }

    @Override
    protected void onInitialized() {
        if (onInitializationComplete == null) {
            info("Initialization complete but callback is null. This should not happen.");
            return;
        }
        onInitializationComplete.accept(this);
    }

    @Override
    protected void processPayload(Payload incoming) {
        switch (incoming.getPayloadType()) {
            case CLIENT_CONNECT:
                processClientConnect(incoming);
                break;
            case DISCONNECT:
                processDisconnect(incoming);
                break;
            case MESSAGE:
                processMessage(incoming);
                break;
            case REVERSE:
                processReverse(incoming);
                break;
            case RPS_CHALLENGE:
                processRPSChallenge(incoming);
                break;
            case RPS_ACCEPT:
                processRPSAccept(incoming);
                break;
            case RPS_MOVE:
                processRPSMove(incoming);
                break;
            default:
                info("Received unsupported payload type: " + incoming.getPayloadType());
        }
    }

    private void processDisconnect(Payload incoming) {
        info("Processing disconnect payload");
        Server.INSTANCE.handleDisconnect(this);
    }

    private void processReverse(Payload incoming) {
        info("Processing reverse payload");
        Server.INSTANCE.handleReverseText(this, incoming.getMessage());
    }

    private void processMessage(Payload incoming) {
        info("Processing message payload");
        Server.INSTANCE.handleMessage(this, incoming.getMessage());
    }

    private void processClientConnect(Payload incoming) {
        info("Processing client connect payload");
        if (!(incoming instanceof ConnectionPayload)) {
            info("Received invalid payload for client connect: " + incoming);
            return;
        }
        if (getClientId() != Constants.DEFAULT_CLIENT_ID) {
            info("Received client connect payload but client is already initialized. Ignoring.");
            return;
        }
        setClientName(((ConnectionPayload) incoming).getClientName());
    }

    private void processRPSChallenge(Payload incoming) {
        info("Processing rps challenge payload");
        if (!(incoming instanceof RPSPayload)) {
            info("Received invalid payload for rps challenge: " + incoming);
            return;
        }
        long targetId1 = ((RPSPayload) incoming).getTargetUser();
        Server.INSTANCE.handleRPSChallenge(this, targetId1);
    }

    private void processRPSAccept(Payload incoming) {
        info("Processing rps accept payload");
        if (!(incoming instanceof RPSPayload)) {
            info("Received invalid payload for rps accept: " + incoming);
            return;
        }
        boolean accepted1 = ((RPSPayload) incoming).isAccepted();
        Server.INSTANCE.handleRPSAccept(this, accepted1);
    }

    private void processRPSMove(Payload incoming) {
        info("Processing rps move payload");
        if (!(incoming instanceof RPSPayload)) {
            info("Received invalid payload for rps move: " + incoming);
            return;
        }
        Move move1 = ((RPSPayload) incoming).getMove();
        if (move1 == null) {
            sendMessage("Invalid move received. Use /move <rock|paper|scissors>");
            return;
        }
        Server.INSTANCE.handleRPSMove(this, move1);
    }

    protected void sendDisconnectTrigger() {
        Payload payload1 = new Payload();
        payload1.setPayloadType(PayloadType.DISCONNECT);
        sendToClient(payload1);
        disconnect();
    }

    protected boolean sendClientId() {
        ConnectionPayload payload1 = new ConnectionPayload();
        payload1.setPayloadType(PayloadType.CLIENT_ID);
        payload1.setClientId(getClientId());
        payload1.setClientName(getClientName());
        return sendToClient(payload1);
    }

    protected boolean sendClientStatus(long clientId, String clientName, boolean isJoin, boolean isSync) {
        ConnectionPayload payload1 = new ConnectionPayload();
        if (isSync) {
            payload1.setPayloadType(PayloadType.SERVER_SYNC);
        } else {
            payload1.setPayloadType(isJoin ? PayloadType.SERVER_JOIN : PayloadType.SERVER_LEAVE);
        }
        payload1.setClientId(clientId);
        payload1.setClientName(clientName);
        return sendToClient(payload1);
    }

    protected boolean sendMessage(String message) {
        Payload payload1 = new Payload();
        payload1.setPayloadType(PayloadType.MESSAGE);
        payload1.setMessage(message);
        return sendToClient(payload1);
    }
}