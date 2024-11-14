package finalProject;

import java.awt.*;
import javax.swing.*;

public class CreateAccountPanel extends JPanel
{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField passwordVerifyField;
    private JLabel errorLabel;

    // Getter for username
    public String getUsername()
    {
        return usernameField.getText();
    }

    // Getter for password
    public String getPassword()
    {
        return new String(passwordField.getPassword());
    }

    // Getter for second password
    public String getPasswordVerify()
    {
        return new String(passwordVerifyField.getPassword());
    }

    // Setter
    public void setError(String error)
    {
        errorLabel.setText(error);
    }

    public CreateAccountPanel(CreateAccountControl cac)
    {
        JPanel labelPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        errorLabel = new JLabel("", JLabel.CENTER);
        errorLabel.setForeground(Color.RED);
        JLabel instructionLabel = new JLabel("Enter a username and password to create an account.", JLabel.CENTER);
        JLabel instructionLabel2 = new JLabel("Your password must be at least 6 characters.", JLabel.CENTER);
        labelPanel.add(errorLabel);
        labelPanel.add(instructionLabel);
        labelPanel.add(instructionLabel2);

        // Panel for the account information
        JPanel accountPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel usernameLabel = new JLabel("Username:", JLabel.RIGHT);
        usernameField = new JTextField(10);
        JLabel passwordLabel = new JLabel("Password:", JLabel.RIGHT);
        passwordField = new JPasswordField(10);
        JLabel passwordVerifyLabel = new JLabel("Verify Password:", JLabel.RIGHT);
        passwordVerifyField = new JPasswordField(10);
        accountPanel.add(usernameLabel);
        accountPanel.add(usernameField);
        accountPanel.add(passwordLabel);
        accountPanel.add(passwordField);
        accountPanel.add(passwordVerifyLabel);
        accountPanel.add(passwordVerifyField);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(cac);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(cac);
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // The three panels in a grid
        JPanel grid = new JPanel(new GridLayout(3, 1, 0, 10));
        grid.add(labelPanel);
        grid.add(accountPanel);
        grid.add(buttonPanel);
        this.add(grid);
    }
}
