package Project.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Project.Common.Constants;
import Project.Common.Payload;
import Project.Common.User;


public abstract class BaseServerThread extends Thread {

    protected volatile boolean isRunning = false; // control variable to stop this thread
    private ObjectOutputStream out; // exposed here for send()
    private Socket client; // communication directly to "my" client
    private User user = new User();

    public BaseServerThread() {
    }

    public BaseServerThread(Socket client) {
        this.client = client;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setClientId(long clientId) {
        this.user.setClientId(clientId);
    }

    public long getClientId() {
        return this.user.getClientId();
    }

    protected void setClientName(String clientName) {
        this.user.setClientName(clientName);
        onInitialized();
    }

    public String getClientName() {
        return this.user.getClientName();
    }

    public String getDisplayName() {
        return this.user.getDisplayName();
    }

    protected abstract void info(String message);

    protected abstract void onInitialized();

    protected abstract void processPayload(Payload payload);

    protected boolean sendToClient(Payload payload) {
        if (!isRunning) {
            return true;
        }
        try {
            info("Sending to client: " + payload);
            out.writeObject(payload);
            out.flush();
            return true;
        } catch (IOException e) {
            info("Error sending message to client (most likely disconnected)");
            return false;
        }
    }

    protected void disconnect() {
        if (!isRunning) {
            return;
        }
        info("Thread being disconnected by server");
        isRunning = false;
        this.interrupt();
    }

    @Override
    public void run() {
        info("Thread starting");
        try (ObjectOutputStream out1 = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in1 = new ObjectInputStream(client.getInputStream())) {
            this.out = out1;
            isRunning = true;
            java.util.Timer nameCheckTimer1 = new java.util.Timer();
            nameCheckTimer1.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    if (getClientName() == null || getClientName().isBlank()) {
                        info("Client name not received. Disconnecting");
                        disconnect();
                    }
                    nameCheckTimer1.cancel();
                }
            }, 3000);

            while (isRunning) {
                try {
                    Payload fromClient1 = (Payload) in1.readObject();
                    if (fromClient1 == null) {
                        throw new IOException("Connection interrupted");
                    } else {
                        info("Received from client: " + fromClient1);
                        processPayload(fromClient1);
                    }
                } catch (ClassCastException | ClassNotFoundException cce) {
                    System.err.println("Error reading object as specified type: " + cce.getMessage());
                    cce.printStackTrace();
                } catch (IOException e) {
                    if (Thread.currentThread().isInterrupted()) {
                        info("Thread interrupted during read, disconnect() was called");
                        break;
                    }
                    info("IO exception while reading from client");
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            info("Unexpected exception outside read loop (stream setup may have failed)");
            e.printStackTrace();
            info("Client disconnected unexpectedly");
        } finally {
            if (getClientId() != Constants.DEFAULT_CLIENT_ID) {
                Server.INSTANCE.handleDisconnect((ServerThread) this);
            }
            isRunning = false;
            info("Exited thread loop. Cleaning up connection");
            cleanup();
        }
    }

    protected void cleanup() {
        info("ServerThread cleanup() start");
        try {
            client.close();
            user.reset();
            info("Closed server-side socket");
        } catch (IOException e) {
            info("Client already closed");
        }
        info("ServerThread cleanup() end");
    }
}