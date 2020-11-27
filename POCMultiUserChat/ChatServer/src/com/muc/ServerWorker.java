package com.muc;

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
            // Read lines until client types quit
            if ("quit".equalsIgnoreCase(line)) {
                break;
            }
            // Echo back whatever was received from client
            String msg = "You typed: " + line + "\n";
            outputStream.write(msg.getBytes());
        }

        // Close connection
        clientSocket.close();
    }
}
