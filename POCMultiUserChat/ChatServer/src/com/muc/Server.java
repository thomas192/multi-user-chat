package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/** Class that is basically a thread which handles the server */
public class Server extends Thread {

    /** The server port to listen to */
    private final int serverPort;

    /** List that stores the instantiated workers */
    private ArrayList<ServerWorker> workerList = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    /** Public getter of the worker list so that server workers can access all others server workers */
    public List<ServerWorker> getWorkerList() {
        return workerList;
    }

    @Override
    public void run() {
        try {
            // Create a server socket that listens on given port
            ServerSocket serverSocket = new ServerSocket(serverPort);
            // Continuously accept incoming connections from clients
            while(true) {
                System.out.println("About to accept client connection...");

                // Create connection between server and client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from" + clientSocket);

                // Create a new server worker instance each time we get a new connection from a client
                ServerWorker worker = new ServerWorker(this, clientSocket);
                // Add the new server worker to the list of all the server workers
                workerList.add(worker);
                // Start the worker's thread
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
