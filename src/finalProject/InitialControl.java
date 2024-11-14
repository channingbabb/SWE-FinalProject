package finalProject;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class InitialControl {

    private JPanel container;
    private PlayerClient client;

    public InitialControl(JPanel container, PlayerClient client)
    {
        this.container = container;
        this.client = client;
    }

    public void actionPerformed(ActionEvent ae)
    {
        // Get the name of the button clicked.
        String command = ae.getActionCommand();

        if (command.equals("Login"))
        {
            LoginPanel loginPanel = (LoginPanel)container.getComponent(1);
            loginPanel.setError("");
            CardLayout cardLayout = (CardLayout)container.getLayout();
            cardLayout.show(container, "2");

        }

        /*else if (command.equals("Create"))
        {
            CreateAccountPanel createAccountPanel = (CreateAccountPanel)container.getComponent(2);
            createAccountPanel.setError("");
            CardLayout cardLayout = (CardLayout)container.getLayout();
            cardLayout.show(container, "3");
        }*/
    }
}
