import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;

public class Server {
    public static final int PORT = 6969;
    public static final String STOP_STRING = ":quit";
    private ServerSocket server;
    private Map<Integer, ConnectedClient> clients;
    private int clientIndex;
    private Scanner serverScanner;
    private volatile boolean isRunning = true;

    public Server() {
        clients = new HashMap<>();
        clientIndex = 0;
        serverScanner = new Scanner(System.in);

        try {
            server = new ServerSocket(PORT);
            Logger.log(Level.INFO, "Server started. Waiting for clients...");

            // Server'dan mesaj gönderme thread'i
            new Thread(() -> {
                while (true) {
                    String message = serverScanner.nextLine();
                    if (message.equals(STOP_STRING)) {
                        isRunning = false;
                        try {
                            clients.values().forEach(ConnectedClient::Close);
                            server.close();
                            Logger.close();
                        } catch (IOException e) {
                            Logger.log(Level.SEVERE, "Shutdown error: " + e.getMessage());
                        }
                        System.exit(0);
                    }
                    broadcast("Server: " + message, 0);
                }
            }).start();

            while (true) {
                initConnections();
            }
        } catch (IOException e) {
            Logger.log(Level.SEVERE, "Error starting server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            Logger.close();
        }
    }

    private void initConnections() throws IOException {

        while (isRunning) {
            try {
                Socket clientSocket = server.accept();
                if (clientSocket.isConnected()) {
                    clientIndex++;
                    final int currentClientId = clientIndex; // Capture current ID
                    ConnectedClient client = new ConnectedClient(clientSocket, currentClientId, this);
                    clients.put(currentClientId, client);
                    Logger.log(Level.INFO, "Client " + currentClientId + " connected.");
                    new Thread(() -> {
                        client.readMessages();
                        clients.remove(currentClientId); // Remove by captured ID
                        client.Close();
                    }).start();
                }
            } catch (SocketException e) {
                if (!isRunning) {
                    Logger.log(Level.INFO, "Server socket closed gracefully");
                }
            } catch (IOException e) {
                Logger.log(Level.SEVERE, "Accept error: " + e.getMessage());
            }
        }   
    }

    public void broadcast(String message, int senderId) {
        for (ConnectedClient client : clients.values()) {
            if (client.id != senderId) {
                try {
                    client.out.writeUTF(message);
                } catch (IOException e) {
                    Logger.log(Level.WARNING, "Error broadcasting message to client: " + client.id + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        System.setProperty("app.type", "server");
        new Server();
    }
}
