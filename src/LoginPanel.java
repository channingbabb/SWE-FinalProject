import java.awt.*;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton submitButton;
    private JButton createAccountButton;
    private LoginControl controller;
    
    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        usernameField = new JTextField(20);
        c.gridx = 1;
        c.gridy = 0;
        add(new JLabel("Username: "), c);
        c.gridx = 2;
        add(usernameField, c);
        
        passwordField = new JPasswordField(20);
        c.gridx = 1;
        c.gridy = 1;
        add(new JLabel("Password: "), c);
        c.gridx = 2;
        add(passwordField, c);
        
        submitButton = new JButton("Login");
        createAccountButton = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(createAccountButton);
        
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        add(buttonPanel, c);
    }
    
    public void setController(LoginControl controller) {
        this.controller = controller;
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
