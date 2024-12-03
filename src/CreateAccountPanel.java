 import javax.swing.*;
import java.awt.*;

//CreateAccountPanel is the GUI part for creating new account
public class CreateAccountPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton submitButton;
    private JButton cancelButton;
    
    public CreateAccountPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        submitButton = new JButton("Create Account");
        cancelButton = new JButton("Cancel");
        
        //adding username label and field
        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("Username: "), c);
        c.gridx = 1;
        add(usernameField, c);
        
        //adding password label and field
        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Password: "), c);
        c.gridx = 1;
        add(passwordField, c);
        
        //adding confirm password label and field
        c.gridx = 0;
        c.gridy = 2;
        add(new JLabel("Confirm Password: "), c);
        c.gridx = 1;
        add(confirmPasswordField, c);
        
        //creating th submit and cancel button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        add(buttonPanel, c);
    }
    
    public String getUsername() {
        return usernameField.getText();
    }
    
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }
    
    public JButton getSubmitButton() {
        return submitButton;
    }
    
    public JButton getCancelButton() {
        return cancelButton;
    }
}
