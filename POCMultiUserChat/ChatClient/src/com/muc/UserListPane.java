package com.muc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class UserListPane extends JPanel implements UserStatusListener {

    /** Chat client instance */
    private final ChatClient client;

    /** Shows the list of connected users */
    private JList<String> userListUI;

    /** List model of connected users */
    private DefaultListModel<String> userListModel;

    public UserListPane(ChatClient client) {
        this.client = client;

        // Add presence listener
        this.client.addUserStatusListener(this);

        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

        // Listener of the connected users list
        userListUI.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if it's a double click
                if (e.getClickCount() > 1) {
                    // Get clicked user's login
                    String login = userListUI.getSelectedValue();
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

        /*
        // Check if connection to server is successful
        if (client.connect()) {
            try {
                client.login("guest", "guest");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 8818);
        // Create user pane that takes the client
        UserListPane userListPane = new UserListPane(client);
        // Create window
        JFrame frame = new JFrame("User List");
        frame.setSize(400, 600);
        // Set behavior on close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Add the user list pane as the main component
        frame.getContentPane().add(userListPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }
}
