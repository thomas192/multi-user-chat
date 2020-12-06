package poc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginWindow extends JFrame {

    /** Instance of client API so that we can login */
    private final ChatClient client;

    /** Input field for login */
    JTextField loginField = new JTextField();

    /** Input field for password */
    JPasswordField passwordField = new JPasswordField();

    /** Login button */
    JButton loginButton = new JButton("Login");

    public LoginWindow() {
        super("Login");

        this.client = new ChatClient("localhost", 8818);
        // Connect to the server
        client.connect();

        // Set behavior on close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Create new layout
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        // Add fields and button to layout
        p.add(loginField);
        p.add(passwordField);
        p.add(loginButton);
        // When login button is pressed
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        // Add the panel as the center component
        getContentPane().add(p, BorderLayout.CENTER);
        // Resize window to fit all the different components
        pack();
        setVisible(true);
    }

    private void doLogin() {
        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            if (client.login(login, password)) {
                // Bring up the user list window
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
                // Remove login window
                setVisible(false);
            } else {
                // Show error message
                JOptionPane.showMessageDialog(this, "Invalid login/password");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
    }
}
