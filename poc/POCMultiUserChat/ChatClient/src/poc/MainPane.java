package poc;

import org.apache.commons.lang3.StringUtils;
import poc.data.ClientDAO;
import poc.listener.MessageListener;
import poc.listener.TopicListener;
import poc.listener.UserStatusListener;
import poc.model.Message;
import redis.clients.jedis.Jedis;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

public class MainPane extends JPanel implements UserStatusListener, TopicListener, MessageListener {

    /** Chat client instance */
    private final ChatClient client;

    /** ClientDAO instance */
    private ClientDAO clientDAO = new ClientDAO();

    /** Shows the list of connected users */
    private JList<String> userList;
    /** List model of connected users */
    private DefaultListModel<String> userListModel;

    /** List of topics followed */
    private HashSet<String> topicsFollowed;
    /** Shows the list of topics */
    private JList<String> topicList;
    /** List model of topics */
    private DefaultListModel<String> topicListModel;

    /** List of past conversations */
    private List<String> conversationsHistory;
    /** Shows the list of past conversations */
    private JList<String> conversationsHistoryList;
    /** List model of past conversations */
    private DefaultListModel<String> conversationsHistoryListModel;

    /** Topic field */
    private JTextField topicField = new JTextField();

    /** Unfollow button */
    private JButton unfollowButton = new JButton("Unfollow");

    /** Stores the messages history of topics the user is following */
    Map<String, List<Message>> topicMessagesHistory = new HashMap<String, List<Message>>();
    /** Stores the messages history of private conversations */
    Map<String, List<Message>> privateMessagesHistory = new HashMap<String, List<Message>>();

    public MainPane(ChatClient client) {
        this.client = client;

        // Listeners
        this.client.addUserStatusListener(this);
        this.client.addTopicListener(this);
        this.client.addMessageListener(this);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        topicListModel = new DefaultListModel<>();
        topicList = new JList<>(topicListModel);
        conversationsHistoryListModel = new DefaultListModel<>();
        conversationsHistoryList = new JList<>(conversationsHistoryListModel);

        // Connected users panel
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.add(new JLabel("Connected users"), BorderLayout.NORTH);
        p1.add(new JScrollPane(userList), BorderLayout.CENTER);
        // Conversations history panel
        JPanel p4 = new JPanel();
        p4.setLayout(new BorderLayout());
        p4.add(new JLabel("Conversations history"), BorderLayout.NORTH);
        p4.add(new JScrollPane(conversationsHistoryList), BorderLayout.CENTER);

        JPanel p5 = new JPanel();
        p5.setLayout(new GridLayout(2, 1));
        p5.add(p1);
        p5.add(p4);

        // Topic list panel
        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(1,2));
        p2.add(new JLabel("Topics"));
        p2.add(topicField);
        JPanel p3 = new JPanel();
        p3.setLayout(new BorderLayout());
        p3.add(new JScrollPane(topicList), BorderLayout.CENTER);
        p3.add(p2, BorderLayout.NORTH);
        p3.add(unfollowButton, BorderLayout.SOUTH);

        setLayout(new GridLayout(1,2));
        add(p5);
        add(p3);

        // Topic added
        topicField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get topic
                    String topic = topicField.getText();
                    if (!topic.equals("")) {
                        // Add topic
                        client.join(topic);
                        // Reset field
                        topicField.setText("");
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        // Click on a topic
        topicList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if it's a double click
                if (e.getClickCount() > 1) {
                    // Get clicked topic
                    String topic = topicList.getSelectedValue();
                    if (topic != null) {
                        // Create a message pane for that topic
                        MessagePane messagePane = new MessagePane(client, topic);
                        // Cache messages history
                        if (!topicMessagesHistory.containsKey(topic)) {
                            topicMessagesHistory.put(topic, clientDAO.fetchTopicMessagesHistory(topic));
                        }
                        messagePane.setMessagesHistory(topicMessagesHistory.get(topic));
                        messagePane.display();
                        // Show the message pane in a separate window
                        JFrame f = new JFrame(topic);
                        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        f.setSize(500, 500);
                        // Add the message pane as the center component
                        f.getContentPane().add(messagePane, BorderLayout.CENTER);
                        f.setVisible(true);
                    }
                }
            }
        });

        // Click on a connected user
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if it's a double click
                if (e.getClickCount() > 1) {
                    // Get clicked user's login
                    String login = userList.getSelectedValue();
                    if (login != null) {
                        // Create a message pane for that login
                        MessagePane messagePane = new MessagePane(client, login);
                        // Cache messages history
                        if (!privateMessagesHistory.containsKey(login)) {
                            privateMessagesHistory.put(login,
                                    clientDAO.fetchPrivateMessagesHistory(client.getLogin(), login));
                        }
                        messagePane.setMessagesHistory(privateMessagesHistory.get(login));
                        messagePane.display();
                        // Show the message pane in a separate window
                        JFrame f = new JFrame("Message: " + login);
                        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        f.setSize(500, 500);
                        // Add the message pane as the center component
                        f.getContentPane().add(messagePane, BorderLayout.CENTER);
                        f.setVisible(true);
                    }
                }
            }
        });

        // Click on a past conversation
        conversationsHistoryList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if it's a double click
                if (e.getClickCount() > 1) {
                    // Get clicked user's login
                    String login = conversationsHistoryList.getSelectedValue();
                    if (login != null) {
                        // Create a message pane for that login
                        MessagePane messagePane = new MessagePane(client, login);
                        // Cache messages history
                        if (!privateMessagesHistory.containsKey(login)) {
                            privateMessagesHistory.put(login,
                                    clientDAO.fetchPrivateMessagesHistory(client.getLogin(), login));
                        }
                        messagePane.setMessagesHistory(privateMessagesHistory.get(login));
                        messagePane.display();
                        // Show the message pane in a separate window
                        JFrame f = new JFrame("Message: " + login);
                        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        f.setSize(500, 500);
                        // Add the message pane as the center component
                        f.getContentPane().add(messagePane, BorderLayout.CENTER);
                        f.setVisible(true);
                    }
                }
            }
        });

        // Unfollow button is clicked
        unfollowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get selected topic
                String topic = topicList.getSelectedValue();
                if (topic != null) {
                    try {
                        // Leave topic
                        client.leave(topic);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

    public void display() {
        // Display topics followed
        for (String topic : topicsFollowed) {
            topicListModel.addElement(topic);
        }
        // Display conversation history
        for (String login : conversationsHistory) {
            conversationsHistoryListModel.addElement(login);
        }
    }

    public void setTopicsFollowed(HashSet<String> topicsFollowed) {
        this.topicsFollowed = topicsFollowed;
    }

    public void setConversationsHistory(List<String> conversationsHistory) {
        this.conversationsHistory = conversationsHistory;
    }

    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
        // Check if user had a conversation with the disconnected user
        if (clientDAO.hadAConversation(login, client.getLogin())) {
            boolean b = false;
            // Check if the disconnected user is not already in the conversations history
            for (int i=0; i < conversationsHistoryListModel.size(); i++) {
                if (conversationsHistoryListModel.getElementAt(i).equals(login)) {
                    b = true;
                }
            }
            if (!b) {
                Jedis jedis = new Jedis("localhost");
                jedis.sadd(client.getLogin(), login);
                jedis.disconnect();
                conversationsHistoryListModel.addElement(login);
            }
        }
    }

    @Override
    public void onJoin(String topic) {
        topicListModel.addElement(topic);
    }

    @Override
    public void onLeave(String topic) {
        topicListModel.removeElement(topic);
    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        // Check if recipient is a topic
        if(fromLogin.charAt(0) == '#') {
            String[] tokens = StringUtils.split(fromLogin, ":");
            String fromTopic = tokens[0];
            fromLogin = tokens[1];
            // Check if caching for this topic has been initialized
            if (topicMessagesHistory.containsKey(fromTopic)) {
                // Cache topic message
                Message msg = new Message();
                msg.setBody(msgBody);
                msg.setSender(fromLogin);
                topicMessagesHistory.get(fromTopic).add(msg);
            }
        } else {
            // Check if caching for this private conversation has been initialized
            if (privateMessagesHistory.containsKey(fromLogin)) {
                // Cache private message
                Message msg = new Message();
                msg.setBody(msgBody);
                msg.setSender(fromLogin);
                privateMessagesHistory.get(fromLogin).add(msg);
            }
        }
    }
}
