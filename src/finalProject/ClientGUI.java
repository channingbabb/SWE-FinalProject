package finalProject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientGUI extends JFrame {

    public ClientGUI(){
        PlayerClient client = new PlayerClient();
        client.setPort(8300);
        client.setHost("localhost");

        /*try {
            client.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        // Name title
        this.setTitle("");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);

        LoginControl lc = new LoginControl(container, client);

        client.setLoginControl(lc);

        JPanel view2 = new LoginPanel(lc);

        container.add(view2, "2");

        cardLayout.show(container, "2");

        this.setLayout(new GridBagLayout());
        this.add(container);

        // Show the JFrame.
        this.setSize(550, 350);
        this.setVisible(true);

    }
}
