import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    private DataInputStream in;
    public static final int PORT = 6969;
    public static final String STOP_STRING = ":quit";

    public Server() {
        try {
            server = new ServerSocket(PORT);
            initConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initConnections() throws IOException {
        Socket clientSocket = server.accept();
        in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        readMessages();
        Close();
    }

    private void readMessages() throws IOException {
        String line = "";
        while (!line.equals(STOP_STRING)) {
            line = in.readUTF();
            System.out.println(line);
        }
    }

    private void Close() throws IOException {
            in.close();
            server.close();
    }

    public static void main(String[] args) {
        new Server();
    }

}
