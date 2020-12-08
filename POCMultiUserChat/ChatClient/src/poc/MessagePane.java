package poc;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MessagePane extends JPanel implements MessageListener {

    /** Chat client (opened the message pane) */
    private final ChatClient client;

    /** List model of conversation's messages */
    private DefaultListModel<String> messageListModel = new DefaultListModel<>();
    /** Stores the messages of the current conversation */
    private JList<String> messageList = new JList<>(messageListModel);

    /** Input field for messages */
    private JTextField messageField = new JTextField();

    /** Login of user the chat client is going to send messages to */
    private final String login;

    public MessagePane(ChatClient client, String login) {
        this.client = client;
        this.login = login;

        // When the other user sends the client a message
        client.addMessageListener(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(messageField, BorderLayout.SOUTH);

        // Message sent
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get the typed message
                    String message = messageField.getText();
                    // Send the message using client API
                    client.msg(login, message);
                    // Add the message to the conversation
                    messageListModel.addElement("You: " + message);
                    // Reset text field
                    messageField.setText("");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onMessage(String fromLogin, String msgBody) {
        // Check if recipient is a topic
        if(fromLogin.charAt(0) == '#') {
            String[] tokens = StringUtils.split(fromLogin, ":");
            String fromTopic = tokens[0];
            fromLogin = tokens[1];
            // Check if the message is intended for this message pane instance
            if (login.equalsIgnoreCase(fromTopic)) {
                // Check if we are not displaying a message the user sent
                if (!fromLogin.equalsIgnoreCase(client.getLogin())) {
                    // Add received message to the conversation
                    String message = fromLogin + ": " + msgBody;
                    messageListModel.addElement(message);
                }
            }
        }
        // Check if the message is intended for this message pane instance
        if (login.equalsIgnoreCase(fromLogin)) {
            // Add received message to the conversation
            String message = fromLogin + ": " + msgBody;
            messageListModel.addElement(message);
        }
    }
}
