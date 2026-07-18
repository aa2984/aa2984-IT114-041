package Project.Server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


import Project.Common.Move;
import Project.Common.TextFX;
import Project.Common.TextFX.Color;




public enum Server {
    INSTANCE; 

    private int port = 3000;
    private ServerSocket serverSocket = null;
    private final ConcurrentHashMap<Long, ServerThread> connectedClients = new ConcurrentHashMap<>();
    private final List<ServerThread> disconnectedBuffer = new ArrayList<>();
    private boolean isRunning = true;

    private long nextClientId = 1;

    private final ConcurrentHashMap<Long, RPSGame> activeGames = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Long, Long> clientToGameMap = new ConcurrentHashMap<>();

    private long nextGameId = 1;


    private void info(String message) {
        System.out.println(TextFX.colorize(String.format("Server: %s", message), Color.YELLOW));
    }

    private void shutdown() {
        try {
            isRunning = false;
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            connectedClients.values().forEach(serverThread1 -> serverThread1.disconnect());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private Server() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            info("JVM is shutting down. Perform cleanup tasks.");
            shutdown();
        }));
    }

    private void start(int port) {
        this.port = port;
        info("Listening on port " + this.port);
        try (ServerSocket serverSocket1 = new ServerSocket(port)) {
            this.serverSocket = serverSocket1;
            while (isRunning) {
                info("Waiting for next client");
                Socket incomingClient1 = serverSocket1.accept();
                info("Client connected");
                ServerThread serverThread1 = new ServerThread(incomingClient1, this::onServerThreadInitialized);
                serverThread1.start();
            }
        } catch (IOException e) {
            info("Error accepting connection");
            e.printStackTrace();
        } finally {
            info("Server socket closed");
        }
    }

    private synchronized void onServerThreadInitialized(ServerThread serverThread) {
        serverThread.setClientId(nextClientId++);
        if (nextClientId < 0) {
            nextClientId = 1;
        }
        serverThread.sendClientId();
        connectedClients.put(serverThread.getClientId(), serverThread);
        unicastClientStatus(serverThread);
        broadcastClientStatus(serverThread, true, false);
    }

    private synchronized void disconnect(ServerThread serverThread) {
        if (!connectedClients.containsKey(serverThread.getClientId())) {
            return;
        }
        cleanupGameForClient(serverThread.getClientId());
        serverThread.sendDisconnectTrigger();
        System.out.println(TextFX.colorize("Client " + serverThread.getDisplayName() + " disconnected.", Color.RED));
        connectedClients.remove(serverThread.getClientId());
        broadcastClientStatus(serverThread, false, false);
    }

    protected synchronized void handleDisconnect(ServerThread sender) {
        disconnect(sender);
    }

    protected synchronized void handleReverseText(ServerThread sender, String text) {
        StringBuilder sb1 = new StringBuilder(text);
        sb1.reverse();
        broadcast(sender, sb1.toString());
    }

    protected synchronized void handleMessage(ServerThread sender, String text) {
        broadcast(sender, text);
    }

    protected synchronized void handleRPSChallenge(ServerThread sender, long targetId) {
        ServerThread target1 = connectedClients.get(targetId);

        if (target1 == null) {
            sender.sendMessage(String.format("User[%s] not found.", targetId));
            return;
        }

        if (targetId == sender.getClientId()) {
            sender.sendMessage("You can't challenge yourself.");
            return;
        }

        if (clientToGameMap.containsKey(sender.getClientId())) {
            sender.sendMessage("You're already in a game. Finish it first.");
            return;
        }
        if (clientToGameMap.containsKey(targetId)) {
            sender.sendMessage(String.format("%s is already in a game.", target1.getDisplayName()));
            return;
        }

        long gameId1 = nextGameId++;
        RPSGame game1 = new RPSGame(gameId1, sender.getClientId(), targetId);

        activeGames.put(gameId1, game1);
        clientToGameMap.put(sender.getClientId(), gameId1);
        clientToGameMap.put(targetId, gameId1);

        target1.sendMessage(String.format(
                "%s challenged you to Rock Paper Scissors. Type /accept or /decline",
                sender.getDisplayName()));
    }

  
    protected synchronized void handleRPSAccept(ServerThread sender, boolean accepted) {
        Long gameId1 = clientToGameMap.get(sender.getClientId());
        if (gameId1 == null) {
            sender.sendMessage("You have no pending challenge.");
            return;
        }

        RPSGame game1 = activeGames.get(gameId1);
        if (game1 == null) {
            clientToGameMap.remove(sender.getClientId());
            sender.sendMessage("You have no pending challenge.");
            return;
        }

        long opponentId1 = game1.getOpponentId(sender.getClientId());
        ServerThread opponent1 = connectedClients.get(opponentId1);

        if (accepted) {
            sender.sendMessage("Game on! Submit your move with /move <rock|paper|scissors>");
            if (opponent1 != null) {
                opponent1.sendMessage(String.format(
                        "%s accepted your challenge. Submit your move with /move <rock|paper|scissors>",
                        sender.getDisplayName()));
            }
            return;
        }

        sender.sendMessage("You declined the challenge.");
        if (opponent1 != null) {
            opponent1.sendMessage(String.format("%s declined your challenge.", sender.getDisplayName()));
        }
        removeGame(gameId1, sender.getClientId(), opponentId1);
    }

    
    protected synchronized void handleRPSMove(ServerThread sender, Move move) {
        Long gameId1 = clientToGameMap.get(sender.getClientId());
        if (gameId1 == null) {
            sender.sendMessage("You're not in a game. Challenge someone with /rps <target_id>");
            return;
        }

        RPSGame game1 = activeGames.get(gameId1);
        if (game1 == null) {
            clientToGameMap.remove(sender.getClientId());
            sender.sendMessage("You're not in a game. Challenge someone with /rps <target_id>");
            return;
        }

        boolean recorded1 = game1.updateMove(sender.getClientId(), move);

        if (!recorded1) {
            sender.sendMessage("You already submitted a move this round.");
            return;
        }

        sender.sendMessage(String.format("Move recorded: %s", move));

        if (!game1.isComplete()) {
            return;
        }

        long opponentId1 = game1.getOpponentId(sender.getClientId());
        ServerThread opponent1 = connectedClients.get(opponentId1);

        Move senderMove1 = game1.getMoveFor(sender.getClientId());
        Move opponentMove1 = game1.getMoveFor(opponentId1);

        if (game1.isTie()) {
            String tieText1 = String.format("Tie! Both threw %s.", senderMove1);
            sender.sendMessage(tieText1);
            if (opponent1 != null) {
                opponent1.sendMessage(tieText1);
            }
        } else {
            boolean senderWon1 = game1.playerWins(sender.getClientId());
            String winnerName1 = senderWon1
                    ? sender.getDisplayName()
                    : (opponent1 != null ? opponent1.getDisplayName() : "Opponent");
            String resultText1 = String.format("%s (%s) vs %s (%s) — %s wins!",
                    sender.getDisplayName(), senderMove1,
                    (opponent1 != null ? opponent1.getDisplayName() : "Opponent"), opponentMove1,
                    winnerName1);
            sender.sendMessage(resultText1);
            if (opponent1 != null) {
                opponent1.sendMessage(resultText1);
            }
        }

        removeGame(gameId1, sender.getClientId(), opponentId1);
    }

    
    private void removeGame(long gameId, long playerAId, long playerBId) {
        activeGames.remove(gameId);
        clientToGameMap.remove(playerAId);
        clientToGameMap.remove(playerBId);
    }


    //aa2984: 2026-July-18
    //Summary: Client leaving mid game needs the opponent to be available
    //cleanupGameforClient checks if disconnecting client was in a game
    //removeGame wipes both tracking once a game
    
    private void cleanupGameForClient(long clientId) {
        Long gameId1 = clientToGameMap.get(clientId);
        if (gameId1 == null) {
            return;
        }

        RPSGame game1 = activeGames.get(gameId1);
        if (game1 == null) {
            clientToGameMap.remove(clientId);
            return;
        }

        long opponentId1 = game1.getOpponentId(clientId);
        ServerThread opponent1 = connectedClients.get(opponentId1);
        if (opponent1 != null) {
            opponent1.sendMessage("Your opponent disconnected. The game has ended.");
        }

        removeGame(gameId1, clientId, opponentId1);
    }

    private void unicastClientStatus(ServerThread incomingServerThread) {
        for (ServerThread existingServerThread1 : connectedClients.values()) {
            boolean success1 = incomingServerThread.sendClientStatus(
                    existingServerThread1.getClientId(),
                    existingServerThread1.getClientName(),
                    true,
                    true);
            if (!success1) {
                disconnect(incomingServerThread);
                break;
            }
        }
    }

    private void broadcastClientStatus(ServerThread targetServerThread, boolean isJoin, boolean isSync) {
        sendOrDisconnect(serverThread1 -> serverThread1.sendClientStatus(
                targetServerThread.getClientId(),
                targetServerThread.getClientName(),
                isJoin,
                isSync));
    }

    private synchronized void broadcast(ServerThread sender, String message) {
        String senderLabel1 = sender == null ? "Server" : String.format("User[%s]", sender.getDisplayName());
        final String formatted1 = String.format("%s: %s", senderLabel1, message);
        sendOrDisconnect(serverThread1 -> serverThread1.sendMessage(formatted1));
    }

    private synchronized void sendOrDisconnect(Function<ServerThread, Boolean> sendAction) {
        connectedClients.values().removeIf(serverThread1 -> {
            boolean success1 = sendAction.apply(serverThread1);
            if (!success1) {
                System.out.println(TextFX.colorize("Failed to send message to client " + serverThread1.getDisplayName()
                        + ". Removing from connected clients.", Color.RED));
                disconnectedBuffer.add(serverThread1);
            }
            return !success1;
        });
        processDisconnectedBuffer();
    }

    private void processDisconnectedBuffer() {
        if (disconnectedBuffer.isEmpty())
            return;
        List<ServerThread> snapshot1 = new ArrayList<>(disconnectedBuffer);
        disconnectedBuffer.clear();
        snapshot1.forEach(st1 -> {
            cleanupGameForClient(st1.getClientId());
            broadcastClientStatus(st1, false, false);
        });
    }

    public static void main(String[] args) {
        System.out.println("Server Starting");
        Server server1 = Server.INSTANCE;
        int port1 = 3009;
        try {
            port1 = Integer.parseInt(args[0]);
        } catch (Exception e) {
            
        }
        server1.start(port1);
        System.out.println("Server Stopped");
    }
}


