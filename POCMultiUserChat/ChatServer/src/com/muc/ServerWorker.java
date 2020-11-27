package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/** Thread that handles communication with client socket */
public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private String login = null;

    public ServerWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    // When thread runs
    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Communicates with client via client socket */
    private void handleClientSocket() throws IOException, InterruptedException {
        // Allows to read data from client
        InputStream inputStream = clientSocket.getInputStream();
        // Allows to write data for client
        OutputStream outputStream = clientSocket.getOutputStream();

        // Create buffer so we can read line by line
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ( (line = reader.readLine()) != null) {
            // Split line into individual tokens based on whitespace character
            String[] tokens = StringUtils.split(line);
            // Check if tokens is valid
            if (tokens != null && tokens.length > 0) {
                // Get first token of line
                String cmd = tokens[0];
                // Read token
                if ("quit".equalsIgnoreCase(cmd)) {
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else {
                    String msg = "unknown command " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        // Close connection
        clientSocket.close();
    }

    /** Handles login */
    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        // Check if login command is valid
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            String msg;
            // Hard code login
            if ((login.equals("guest") && password.equals("guest"))
                    || (login.equals("thomas") && password.equals("thomas"))) {
                msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);
            } else {
                msg = "error login\n";
                outputStream.write(msg.getBytes());
            }
        }
    }
}
