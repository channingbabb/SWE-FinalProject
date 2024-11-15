import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton submitButton;
    private JButton createAccountButton;
    
    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        // Username field
        usernameField = new JTextField(20);
        c.gridx = 1;
        c.gridy = 0;
        add(new JLabel("Username: "), c);
        c.gridx = 2;
        add(usernameField, c);
        
        // Password field
        passwordField = new JPasswordField(20);
        c.gridx = 1;
        c.gridy = 1;
        add(new JLabel("Password: "), c);
        c.gridx = 2;
        add(passwordField, c);
        
        // Buttons
        submitButton = new JButton("Login");
        createAccountButton = new JButton("Create Account");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(createAccountButton);
        
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        add(buttonPanel, c);
    }
    
    public String getUsername() {
        return usernameField.getText();
    }
    
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    public JButton getSubmitButton() {
        return submitButton;
    }
    
    public JButton getCreateAccountButton() {
        return createAccountButton;
    }
}