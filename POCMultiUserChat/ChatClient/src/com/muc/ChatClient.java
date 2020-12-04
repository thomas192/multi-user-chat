package com.muc;

import java.io.*;
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

    /** Stores the server inout stream */
    private BufferedReader bufferedIn;

    /** Constructor of the server client class */
    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        // Connect client instance to the server
        if (!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("Connection successful");
            if (client.login("guest", "guest")) {
                System.out.println("Login successful");
            } else {
                System.err.println("Login failed");
            }
        }
    }

    private boolean login(String login, String password) throws IOException {
        // Send login command to server
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        // Get response line
        String response = bufferedIn.readLine();
        System.out.println("Response line: " + response);
        // Check if login was successful
        if ("ok login".equalsIgnoreCase(response)) {
            return true;
        }
        return false;
    }

    /** Establishes the connection to the server */
    private boolean connect() {
        try {
            // Create socket
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            // Initialize local variables
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            // Create a buffer to read the server input stream line by line
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
