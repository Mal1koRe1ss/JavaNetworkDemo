import java.io.*;
import java.net.*;
import java.util.logging.Level;

public class ConnectedClient {
    private Socket clientSocket;
    private DataInputStream in;
    public DataOutputStream out;
    public int id;
    private Server server;

    public ConnectedClient(Socket clientSocket, int ID, Server server) {
        this.clientSocket = clientSocket;
        this.id = ID;
        this.server = server;
        try {
            Logger.log(Level.INFO, "Client " + id + " Connected");
            this.in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            this.out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            Logger.log(Level.SEVERE, "Error creating streams for client " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void readMessages() {
        try {
            String line;
            while (!(line = in.readUTF()).equals(Server.STOP_STRING)) {
                System.out.println("[CLIENT-" + id + "] " + line); // Server konsolunda görünür
                server.broadcast(line, id);
            }
        } catch (IOException e) {
            Logger.log(Level.WARNING, "Client " + id + " disconnected abruptly");
        } finally {
            Close();
        }
    }

    public void Close() {
        try {
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            Logger.log(Level.WARNING, "Error closing connection for client " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
