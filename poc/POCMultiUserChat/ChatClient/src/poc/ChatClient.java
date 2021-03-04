package poc;

import org.apache.commons.lang3.StringUtils;
import poc.listener.MessageListener;
import poc.listener.TopicListener;
import poc.listener.UserStatusListener;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient {
    /** The server name */
    private final String serverName;

    /** The server port */
    private final int serverPort;

    /** The server socket */
    private Socket socket;

    /** The output stream of the socket */
    private OutputStream serverOut;

    /** The input stream of the socket */
    private InputStream serverIn;

    /** Buffer that is used to read the server output stream */
    private BufferedReader bufferedIn;

    /** The client's login */
    private String login;

    /** Event listeners */
    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();
    private ArrayList<TopicListener> topicListeners = new ArrayList<>();

    /** Constructor of the server client class */
    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);

        // Register listeners
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }
            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });
        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("You got a message from " + fromLogin + ": " + msgBody);
            }
        });
        client.addTopicListener(new TopicListener() {
            @Override
            public void onJoin(String topic) {
                System.out.println("Joined topic " + topic);
            }
            @Override
            public void onLeave(String topic) {
                System.out.println("Left topic " + topic);
            }
        });

        // Connect client instance to the server
        if (!client.connect()) {
            System.err.println("Connection failed");
        } else {
            System.out.println("Connection successful");
            // Connect user
            if (client.login("guest", "guest")) {
                System.out.println("Login successful");

                client.msg("thomas", "Hello World!");

                client.join("welcome");

                client.msg("#welcome", "Hey!");
            } else {
                System.err.println("Login failed");
            }
            
            // client.logoff();
        }
    }

    /** Makes the user join a topic */
    public void join(String topic) throws IOException {
        String cmd = "join #" + topic + "\n";
        serverOut.write(cmd.getBytes());
    }

    /** Makes the user leave a topic */
    public void leave(String topic) throws IOException {
        String cmd = "leave " + topic + "\n";
        serverOut.write(cmd.getBytes());
    }

    /** Sends a message to a user */
    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    /** Logs the user in */
    public boolean login(String login, String password) throws IOException {
        // Send login command to server
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        // Get response line
        String response = bufferedIn.readLine();
        System.out.println("Response line: " + response);
        // Check if login was successful
        if ("ok login".equalsIgnoreCase(response)) {
            this.login = login;
            // Start reading responses from the server
            startMessageReader();
            return true;
        }
        return false;
    }

    /** Logs the user off */
    public void logoff() throws IOException {
        // Send logoff command to server
        String cmd = "logoff\n";
        serverOut.write(cmd.getBytes());
    }

    /** Reads responses from the server */
    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    readMessageLoop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    /** Infinite loop that reads line by line from the server output, which is going to be our client input */
    private void readMessageLoop() throws IOException {
        try {
            String line;
            while ( (line = bufferedIn.readLine()) != null) {
                // Split line into individual tokens based on whitespace character
                String[] tokens = StringUtils.split(line);
                // Check if the tokens list is valid
                if (tokens != null && tokens.length > 0) {
                    // Get first token of line
                    String cmd = tokens[0];
                    // Online presence message
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    // Offline presence message
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    // Message received
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String [] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
                    // Topic joined
                    } else if ("join".equalsIgnoreCase(cmd)) {
                        handleJoin(tokens);
                    // Topic left
                    } else if ("leave".equalsIgnoreCase((cmd))) {
                        handleLeave(tokens);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Close the socket
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /** Handles topic leave
     *  format: "leave" #topic */
    private void handleLeave(String[] tokens) {
        String topic = tokens[1];
        for (TopicListener listener : topicListeners) {
            listener.onLeave(topic);
        }
    }

    /** Handles topic join
     *  format: "join" #topic */
    private void handleJoin(String[] tokens) {
        String topic = tokens[1];
        for (TopicListener listener : topicListeners) {
            listener.onJoin(topic);
        }
    }

    /** Handles received message
     *  format: "msg" login msgBody */
    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];
        for (MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }
    }

    /** Handles the offline presence message
     *  format: "offline" login */
    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    /** Handles the online presence message
     * format: "online" login */
    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for (UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    /** Establishes the connection to the server */
    public boolean connect() {
        try {
            // Create socket
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            // Initialize server output and input streams
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            // Initialize buffer used to read the server output stream line by line
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Public getter of login */
    public String getLogin() {
        return login;
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }

    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }

    public void addTopicListener(TopicListener listener) {
        topicListeners.add(listener);
    }

    public void removeTopicListener(TopicListener listener) {
        topicListeners.remove(listener);
    }
}
