package com.muc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        /* Port to listen to */
        int port = 8818;
        try {
            // Create server socket that listens on given port
            ServerSocket serverSocket = new ServerSocket(port);
            // Continuously accept incoming connections from client
            while(true) {
                System.out.println("About to accept client connection...");
                // Create connection between server and client
                Socket clientSocket = serverSocket .accept();
                System.out.println("Accepted connection from" + clientSocket);
                // Create new server worker instance each we get a new connection from client
                ServerWorker worker = new ServerWorker(clientSocket);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
