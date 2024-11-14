package finalProject;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class InitialControl implements ActionListener {

    // Private data fields for storing the container and client.
    private JPanel container;
    private PlayerClient client;

    // Constructor for the initial controller.
    public InitialControl(JPanel container, PlayerClient client) {
        this.container = container;
        this.client = client;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        // Get the name of the button clicked.
        String command = ae.getActionCommand();

        // The Login button takes the user to the login panel.
        if (command.equals("Login")) {
            LoginPanel loginPanel = (LoginPanel) container.getComponent(1);
            loginPanel.setError("");
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "2");
        }

        // The Create button takes the user to the create account panel.
        else if (command.equals("Create")) {
            CreateAccountPanel createAccountPanel = (CreateAccountPanel) container.getComponent(2);
            createAccountPanel.setError("");
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "3");
        }
    }
}