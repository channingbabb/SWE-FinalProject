package finalProject;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class LoginControl implements ActionListener{

    private JPanel container;
    private PlayerClient client;

    public LoginControl(JPanel container, PlayerClient client)
    {
        this.container = container;
        this.client = client;
    }

    public void actionPerformed(ActionEvent ae)
    {
        // Get the name of the button clicked.
        String command = ae.getActionCommand();

        // The Cancel button takes the user back to the initial panel.
        if (command == "Cancel")
        {
            CardLayout cardLayout = (CardLayout)container.getLayout();
            cardLayout.show(container, "1");
        }

        // The Submit button submits the login information to the server.
        else if (command == "Submit")
        {
            // Get the username and password the user entered.
            LoginPanel loginPanel = (LoginPanel)container.getComponent(1);
            LoginData data = new LoginData(loginPanel.getUsername(), loginPanel.getPassword());

            // Check the validity of the information locally first.
            if (data.getUsername().equals("") || data.getPassword().equals(""))
            {
                displayError("You must enter a username and password.");
                return;
            }

            // Submit the login information to the server.
            try
            {
                client.sendToServer(data);
            }
            catch (IOException e)
            {
                displayError("Error connecting to the server.");
            }
        }
    }

    // After the login is successful, set the User object and display the contacts screen.
    public void loginSuccess()
    {
        LoginPanel loginPanel = (LoginPanel)container.getComponent(1);

        CardLayout cardLayout = (CardLayout)container.getLayout();
        cardLayout.show(container, "4");
    }

    // Method that displays a message in the error label.
    public void displayError(String error)
    {
        LoginPanel loginPanel = (LoginPanel)container.getComponent(1);
        loginPanel.setError(error);
    }

}
