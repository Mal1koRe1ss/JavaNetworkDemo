import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private Scanner in;

    public Client() {
        try {
            socket = new Socket("127.0.0.1",Server.PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new Scanner(System.in);
            writeMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    private void writeMessages() throws IOException {
        String line = "";
        while (!line.equals(Server.STOP_STRING)) {
            line = in.nextLine();
            out.writeUTF(line);
        }
        Close();
    }

    private void Close() throws IOException {
        socket.close();
        out.close();
        in.close();
    }

    public static void main(String[] args) {
        new Client();
    }
}
