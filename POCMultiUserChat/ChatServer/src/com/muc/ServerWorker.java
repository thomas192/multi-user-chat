package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/** Thread that handles communication with client socket */
public class ServerWorker extends Thread {

    private final Socket clientSocket;

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
                } else {
                    String msg = "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        // Close connection
        clientSocket.close();
    }
}
