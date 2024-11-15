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
        
        // Add action listeners to buttons
        this.initialPanel.getLoginButton().addActionListener(this);
        this.initialPanel.getCreateAccountButton().addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Get the CardLayout for switching panels
        CardLayout cardLayout = (CardLayout) container.getLayout();
        
        // Handle button clicks
        if (e.getSource() == initialPanel.getLoginButton()) {
            // Switch to login panel
            cardLayout.show(container, "LoginPanel");
        }
        else if (e.getSource() == initialPanel.getCreateAccountButton()) {
            // Switch to create account panel
            cardLayout.show(container, "CreateAccountPanel");
        }
    }
    
    // Method to return to initial panel
    public void returnToInitial() {
        CardLayout cardLayout = (CardLayout) container.getLayout();
        cardLayout.show(container, "InitialPanel");
    }
    
    // Method to handle connection error
    public void handleConnectionError() {
        javax.swing.JOptionPane.showMessageDialog(
            initialPanel,
            "Unable to connect to server. Please try again later.",
            "Connection Error",
            javax.swing.JOptionPane.ERROR_MESSAGE
        );
    }
    
    // Method to initialize client connection
    public void initializeConnection() {
        try {
            client.openConnection();
        } catch (Exception e) {
            handleConnectionError();
            e.printStackTrace();
        }
    }
}
