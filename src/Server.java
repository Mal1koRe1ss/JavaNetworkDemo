import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    public static final int PORT = 6969;
    public static final String STOP_STRING = ":quit";
    private int clientIndex;

    public Server() {
        try {
            server = new ServerSocket(PORT);
            while (true) initConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initConnections() throws IOException {
        Socket clientSocket = server.accept();

        if (clientSocket.isConnected()) {
            new Thread(()-> {
                clientIndex++;
                ConnectedClient client = new ConnectedClient(clientSocket, clientIndex);
                client.readMessages();
                client.Close();
            }).start();
        }
    }

    public static void main(String[] args) {
        new Server();
    }

}
