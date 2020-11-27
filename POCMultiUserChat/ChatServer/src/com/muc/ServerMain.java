package com.muc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        // Set up port
        int port = 8818;
        try {
            // Set up server socket
            ServerSocket serverSocket = new ServerSocket(port);
            // Continuously accept the connections from clients
            while(true) {
                System.out.println("About to accept client connection...");
                // Create connection between server and client
                Socket clientSocket = serverSocket .accept();
                System.out.println("Accepted connection from" + clientSocket);
                // Prove it works by printing something to the client socket
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write("Hello World\n".getBytes());
                // Close connection
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
