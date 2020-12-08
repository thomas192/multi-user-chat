package poc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        // When connected user clicked
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
