package finalProject;

import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {

    public ClientGUI(){
        PlayerClient client = new PlayerClient();
        client.setPort(8300);
        client.setHost("localhost");

        // NEED TO SET UP SERVER SIDE
        /*try {
            client.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        // NAME TITLE
        this.setTitle("");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);

        InitialControl ic = new InitialControl(container, client);
        LoginControl lc = new LoginControl(container, client);
        CreateAccountControl cac = new CreateAccountControl(container, client);
        GameControl gc = new GameControl(container, client);
        // TO DO
        LeaderboardControl lbc = new LeaderboardControl(container); // Needs all clients

        client.setLoginControl(lc);

        JPanel view1 = new InitialPanel(ic);
        JPanel view2 = new LoginPanel(lc);
        JPanel view3 = new CreateAccountPanel(cac);
        //JPanel view4 = new  GamePanel(gc);
        //JPanel view5 = new LeaderboardPanel(lbc);


        container.add(view1, "1");
        container.add(view2, "2");
        container.add(view3, "3");
        //container.add(view4, "4");
        //container.add(view5, "5");

        cardLayout.show(container, "1");

        this.setLayout(new GridBagLayout());
        this.add(container);

        // Uses full screen size
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);

    }
}
