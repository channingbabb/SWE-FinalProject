package SWEFinalProject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class InitialControl extends LeaderboardData implements ActionListener {
    private InitialPanel initialPanel;
    private PlayerClient client;
    private JPanel container;
    
    public InitialControl(InitialPanel initialPanel, PlayerClient client, JPanel container) {
        this.initialPanel = initialPanel;
        this.client = client;
        this.container = container;
        
        this.initialPanel.getLoginButton().addActionListener(this);
        this.initialPanel.getCreateAccountButton().addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        CardLayout cardLayout = (CardLayout) container.getLayout();
        
        if (e.getSource() == initialPanel.getLoginButton()) {
            cardLayout.show(container, "LoginPanel");
        }
        else if (e.getSource() == initialPanel.getCreateAccountButton()) {
            cardLayout.show(container, "CreateAccountPanel");
        }
    }
    
    public void returnToInitial() {
        CardLayout cardLayout = (CardLayout) container.getLayout();
        cardLayout.show(container, "InitialPanel");
    }
    
    public void handleConnectionError() {
        javax.swing.JOptionPane.showMessageDialog(
            initialPanel,
            "Unable to connect to server. Please try again later.",
            "Connection Error",
            javax.swing.JOptionPane.ERROR_MESSAGE
        );
    }
    
    public void initializeConnection() {
        try {
            client.openConnection();
        } catch (Exception e) {
            handleConnectionError();
            e.printStackTrace();
        }
    }
}
