package com.muc;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;

public class UserListPane extends JPanel implements UserStatusListener {

    /** Chat client instance */
    private final ChatClient client;

    /** Shows the list of connected users */
    private JList<String> userListUI;

    private DefaultListModel<String> userListModel;

    public UserListPane(ChatClient client) {
        this.client = client;
        // Add presence listener
        this.client.addUserStatusListener(this);
        // Instantiate list model
        userListModel = new DefaultListModel<>();
        // Instantiate list component
        userListUI = new JList<>(userListModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(userListUI), BorderLayout.CENTER);

        // Check if connection to server is successful
        if (client.connect()) {
            try {
                client.login("guest", "guest");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 8818);

        UserListPane userListPane = new UserListPane(client);
        // Create window
        JFrame frame = new JFrame("User List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        // Set userListPane as the main component
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
