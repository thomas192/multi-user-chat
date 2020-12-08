package poc;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class UserListPane extends JPanel implements UserStatusListener, TopicListener {

    /** Chat client instance */
    private final ChatClient client;

    /** Shows the list of connected users */
    private JList<String> userList;
    /** List model of connected users */
    private DefaultListModel<String> userListModel;

    /** Shows the list of topics */
    private JList<String> topicList;
    /** List model of topics */
    private DefaultListModel<String> topicListModel;

    /** Input field for topic */
    private JTextField topicField = new JTextField();

    public UserListPane(ChatClient client) {
        this.client = client;

        // Listeners
        this.client.addUserStatusListener(this);
        this.client.addTopicListener(this);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        topicListModel = new DefaultListModel<>();
        topicList = new JList<>(topicListModel);
        // User list panel
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.add(new JLabel("Connected users"), BorderLayout.NORTH);
        p1.add(new JScrollPane(userList), BorderLayout.CENTER);
        // Topic list panel
        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(1,2));
        p2.add(new JLabel("Topics"));
        p2.add(topicField);
        JPanel p3 = new JPanel();
        p3.setLayout(new BorderLayout());
        p3.add(new JScrollPane(topicList), BorderLayout.CENTER);
        p3.add(p2, BorderLayout.NORTH);

        setLayout(new GridLayout(1,2));
        add(p1);
        add(p3);

        // When topic added
        topicField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get the topic
                    String topic = topicField.getText();
                    if (!topic.equals("")) {
                        // Add the topic using the client API
                        client.join(topic);
                        // Reset field
                        topicField.setText("");
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        // When click on a topic
        topicList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if it's a double click
                if (e.getClickCount() > 1) {
                    // Get clicked topic
                    String topic = topicList.getSelectedValue();
                    // Create a message pane for that topic
                    MessagePane messagePane = new MessagePane(client, topic);
                    // Show the message pane in a separate window
                    JFrame f = new JFrame(topic);
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.setSize(500, 500);
                    // Add the message pane as the center component
                    f.getContentPane().add(messagePane, BorderLayout.CENTER);
                    f.setVisible(true);
                }
            }
        });

        // When click on a connected user
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if it's a double click
                if (e.getClickCount() > 1) {
                    // Get clicked user's login
                    String login = userList.getSelectedValue();
                    // Create a message pane for that login
                    MessagePane messagePane = new MessagePane(client, login);
                    // Show the message pane in a separate window
                    JFrame f = new JFrame("Message: " + login);
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.setSize(500, 500);
                    // Add the message pane as the center component
                    f.getContentPane().add(messagePane, BorderLayout.CENTER);
                    f.setVisible(true);
                }
            }
        });
    }

    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }

    @Override
    public void onJoin(String topic) {
        topicListModel.addElement(topic);
    }

    @Override
    public void onLeave(String topic) {
        topicListModel.removeElement(topic);
    }
}
