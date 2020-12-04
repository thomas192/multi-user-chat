package com.muc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChatClient {
    /** Stores the server name */
    private final String serverName;

    /** Stores the server port */
    private final int serverPort;

    /** Stores the server socket */
    private Socket socket;

    /** Stores the output stream of the socket */
    private OutputStream serverOut;

    /** Stores the input stream of the socket */
    private InputStream serverIn;

    /** Constructor of the server client class */
    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 8818);
        // Connect client instance to the server
        if (!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("Connection successful");
        }
    }

    /** Establishes the connection to the server */
    private boolean connect() {
        try {
            // Create socket
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
