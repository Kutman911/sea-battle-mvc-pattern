package networking;

import java.io.*;
import java.net.Socket;
public class ClientSeaBattle implements Runnable {

    private Socket clientSocket;
    private Thread thread;

    public ClientSeaBattle(Socket socket) {
        this.clientSocket = socket;
        thread = new Thread(this);
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            String message = in.readLine();
            System.out.println(message);

        } catch (IOException ioe) {
            System.out.println("Error into InputStream");
        }
    }

    public void go() {
        thread.start();
    }

}
