import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginControl implements ActionListener {
    private LoginPanel loginPanel;
    private PlayerClient client;
    private JPanel container;
    
    public LoginControl(LoginPanel loginPanel, PlayerClient client, JPanel container) {
        this.loginPanel = loginPanel;
        this.client = client;
        this.container = container;
        
        loginPanel.getSubmitButton().addActionListener(this);
        loginPanel.getCreateAccountButton().addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginPanel.getSubmitButton()) {
            if (loginPanel.getUsername().trim().isEmpty() || loginPanel.getPassword().trim().isEmpty()) {
                JOptionPane.showMessageDialog(loginPanel, 
                    "Please enter both username and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            LoginData data = new LoginData(
                loginPanel.getUsername(),
                loginPanel.getPassword()
            );
            try {
                client.sendToServer(data);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(loginPanel,
                    "Error connecting to server",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else if (e.getSource() == loginPanel.getCreateAccountButton()) {
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "InitialPanel");
        }
    }
    
    public void handleLoginResult(LoginData result) {
        System.out.println("Handling login result - Success: " + result.isSuccess());
        if (result.getUser() != null) {
            System.out.println("User info received: " + result.getUser().getUsername());
        } else {
            System.out.println("No user info received");
        }
        
        if (result.isSuccess()) {
            client.setCurrentUser(result.getUser());
            
            System.out.println("Setting up lobby panel...");
                    
            LobbyPanel lobbyPanel = new LobbyPanel();
            LobbyControl lobbyControl = new LobbyControl(lobbyPanel, client, container);
            client.setLobbyControl(lobbyControl);
            
            container.add(lobbyPanel, "LobbyPanel");
            
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "LobbyPanel");
            
            System.out.println("Switched to lobby panel");
            
            if (container.getTopLevelAncestor() instanceof ClientGUI) {
                ClientGUI gui = (ClientGUI) container.getTopLevelAncestor();
                gui.updateUserInfo(result.getUser().getUsername(), result.getUser().getBalance());
            }
        } else {
            JOptionPane.showMessageDialog(loginPanel, 
                "Invalid username or password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
