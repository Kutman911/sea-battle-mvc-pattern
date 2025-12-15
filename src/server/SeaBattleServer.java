package server;

import client.ClientSeaBattle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SeaBattleServer implements Runnable {
    private ServerSocket serverSocket;
    private Object lock;
    private Thread thread;

    public SeaBattleServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            lock = new Object();
            thread = new Thread(this);
        } catch (IOException io) {
            System.out.println("Server start error");
        }
    }

    private void startServer() {
        while(true) {
            try {
                Socket socket = serverSocket.accept();
                synchronized (lock) {
                    ClientSeaBattle client = new ClientSeaBattle(socket);
                    client.go();
                }
            } catch (IOException ioe) {
                System.out.println("Socket error: " + ioe);
            }
        }
    }

    @Override
    public void run() {
        startServer();
    }

    public void go() {
        thread.start();
    }
}
