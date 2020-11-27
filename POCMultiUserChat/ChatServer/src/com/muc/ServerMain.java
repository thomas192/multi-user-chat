package com.muc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ServerMain {
    public static void main(String[] args) {
        // Set up port
        int port = 8818;
        try {
            // Set up server socket
            ServerSocket serverSocket = new ServerSocket(port);
            // Continuously accept the connections from client
            while(true) {
                System.out.println("About to accept client connection...");
                // Create connection between server and client
                Socket clientSocket = serverSocket .accept();
                System.out.println("Accepted connection from" + clientSocket);
                // Create new thread everytime we get a connection from client
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            // Pass client socket tied to client connection to the socket handler
                            handleClientSocket(clientSocket);
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // Start new thread
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientSocket(Socket clientSocket) throws IOException, InterruptedException {
        // Get output stream of socket
        OutputStream outputStream = clientSocket.getOutputStream();
        // Simulate 10-second action
        for (int i=0; i < 10; i++) {
            outputStream.write(("Time now is " + new Date() + "\n").getBytes());
            Thread.sleep(1000);
        }
        // Close connection
        clientSocket.close();
    }
}
