package poc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePane extends JPanel implements MessageListener {

    /** Chat client (opened the message pane) */
    private final ChatClient client;

    /** List model of conversation's messages */
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    /** Stores the messages of the current conversation */
    private JList<String> messageList = new JList<>(listModel);

    /** Input field for messages */
    private JTextField inputField = new JTextField();

    /** Login of user chat client is going to send messages to */
    private final String login;

    public MessagePane(ChatClient client, String login) {
        this.client = client;
        this.login = login;

        // When the other user sends the client a message
        client.addMessageListener(this);

        setLayout(new BorderLayout());
        // Add the message list UI as the center component
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        // Add the input field as the bottom component
        add(inputField, BorderLayout.SOUTH);
        // Listener of the message field
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get the typed message
                    String message = inputField.getText();
                    // Send the message using client API
                    client.msg(login, message);
                    // Add the message to the conversation
                    listModel.addElement("You: " + message);
                    // Reset text field
                    inputField.setText("");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        // Check if the message is intended for this message pane instance
        if (login.equalsIgnoreCase(fromLogin)) {
            // Add received message to the conversation
            String message = fromLogin + ": " + msgBody;
            listModel.addElement(message);
        }
    }
}
