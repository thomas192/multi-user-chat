package com.muc;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

/** Class that is basically a thread which handles communication with client socket */
public class ServerWorker extends Thread {


    /** The server instance that we can pass to each server worker */
    private final Server server;

    /** Client socket */
    private final Socket clientSocket;

    /** The output stream of the client socket which allows us to write data for the client */
    private OutputStream outputStream;

    /** Login of the server worker */
    private String login = null;

    /** Stores the membership of the user to a topic */
    private HashSet<String> topicSet = new HashSet<>();

    /** Constructor of the server worker class */
    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    /** Public getter of the login so that other server workers know what the login is */
    public String getLogin() {
        return login;
    }

    // Overrides the thread run function
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
        // Store the input stream which allows us to read data from the client
        InputStream inputStream = clientSocket.getInputStream();

        // Initialize the output stream
        this.outputStream = clientSocket.getOutputStream();

        // Create a buffer to read the input stream line by line
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            // Split line into individual tokens based on whitespace character
            String[] tokens = StringUtils.split(line);
            // Check if the tokens list is valid
            if (tokens != null && tokens.length > 0) {
                // Get first token of line
                String cmd = tokens[0];
                // Read token and do something if it's a known command
                // Disconnection
                if ("logoff".equals(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                // Connection
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                // User to user messaging
                } else if ("msg".equalsIgnoreCase(cmd)) {
                    // Split line into 3 tokens (3rd token is the message)
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);
                // Join topic
                } else if ("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);
                // Leave topic
                } else if ("leave".equalsIgnoreCase((cmd))) {
                    handleLeave(tokens);
                // Unknown command
                } else {
                    String msg = "unknown command " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        // Close connection
        clientSocket.close();
    }

    private void handleLeave(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            // This connection has joined this topic
            topicSet.remove(topic);
        }
    }

    /** Checks if user is subscribed to given topic */
    public boolean isMemberOfTopic(String topic) {
        return topicSet.contains(topic);
    }

    /** Handles join command */
    private void handleJoin(String[] tokens) {
        if (tokens.length > 1) {
            String topic = tokens[1];
            // This connection has joined this topic
            topicSet.add(topic);
        }
    }

    /** Handles message command
     * format: "msg" "login" body
     * format: "msg" "#topic" body */
    private void handleMessage(String[] tokens) throws IOException {
        // Message recipient
        String sendTo = tokens[1];
        // Message to send
        String body = tokens[2];
        // Determine whether 2nd token is a topic
        boolean isTopic = sendTo.charAt(0) == '#';
        // Get the list of all the workers connected to the server
        List<ServerWorker> workerList = server.getWorkerList();
        for(ServerWorker worker : workerList) {
            // Check if message recipient is a topic
            if (isTopic) {
                // Check if user is subscribed to the topic
                if (worker.isMemberOfTopic(sendTo)) {
                    String outMsg = "msg " + sendTo + ":" + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            } else {
                // If worker matches recipient
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    // Display message
                    String outMsg = "msg " + login + " " + body + "\n";
                    worker.send(outMsg);
                }
            }
        }
    }

    /** Handles logoff command */
    private void handleLogoff() throws IOException {
        // Remove this worker from the worker list
        server.removeWorker(this);

        // Get the list of all the workers connected to the server
        List<ServerWorker> workerList = server.getWorkerList();

        // Send other online users current user's status
        for (ServerWorker worker : workerList) {
            // Check if we are not sending our own presence
            if (!login.equals(worker.getLogin())) {
                String msg = "offline " + worker.getLogin() + "\n";
                worker.send(msg);
            }
        }
        clientSocket.close();
    }

    /** Handles login */
    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        // Check if login command is valid
        if (tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            String msg;
            // Hard coded login
            if ((login.equals("guest") && password.equals("guest"))
                    || (login.equals("thomas") && password.equals("thomas"))) {
                msg = "Ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);

                // Get the list of all the workers connected to the server
                List<ServerWorker> workerList = server.getWorkerList();

                // Send current user all other online logins
                for (ServerWorker worker : workerList) {
                    // Check if we are not sending presence of not connected users yet
                    if (worker.getLogin() != null) {
                        if (!login.equals(worker.getLogin())) {
                            msg = "online " + worker.getLogin() + "\n";
                            send(msg);
                        }
                    }
                }

                // Send other online users current user's status
                for (ServerWorker worker : workerList) {
                    // Check if we are not sending our own presence
                    if (!login.equals(worker.getLogin())) {
                        msg = "online " + login + "\n";
                        worker.send(msg);
                    }
                }

            } else {
                msg = "Error login\n";
                outputStream.write(msg.getBytes());
                System.err.println("Login failed for " + login);
            }
        }
    }

    /** Sends a message to a client using his output stream */
    private void send(String msg) throws IOException {
        if (login != null) {
            outputStream.write(msg.getBytes());
        }
    }
}
