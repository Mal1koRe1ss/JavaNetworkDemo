import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Scanner scanner;

    public Client() {
        try {
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            System.setProperty("client.id", uuid);
            socket = new Socket("31.57.154.63", Server.PORT);

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            scanner = new Scanner(System.in);

            Logger.log(Level.INFO, "Connected to server.");

            // Mesajları okuma thread'i
            new Thread(() -> {
                try {
                    readMessages();
                } catch (IOException e) {
                    Logger.log(Level.WARNING, "Error reading messages: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

            writeMessages();
        } catch (IOException e) {
            Logger.log(Level.SEVERE, "Error connecting to server: " + e.getMessage());
        } finally {
            Logger.close();
        }
    }

    private void readMessages() throws IOException {
        String message;
        while (true) {
            try {
                message = in.readUTF();
                Logger.log(Level.INFO, "Received: " + message);
                System.out.println(message);
            } catch (IOException e) {
                Logger.log(Level.WARNING, "Disconnected from server: " + e.getMessage());
                break; // Bağlantı koptuğunda döngüden çık
            }
        }
    }

    private void writeMessages() throws IOException {
        String line = "";
        while (!line.equals(Server.STOP_STRING)) {
            line = scanner.nextLine();
            out.writeUTF(line);
            Logger.log(Level.INFO, "Sent: " + line);
        }
        Close();
    }

    private void Close() throws IOException {
        socket.close();
        out.close();
        in.close();
        scanner.close();
        Logger.log(Level.INFO, "Connection closed.");
    }

    public static void main(String[] args) {
        System.setProperty("app.type", "client");
        new Client();
    }
}
