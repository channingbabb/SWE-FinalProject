import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginControl implements ActionListener {
    private LoginPanel loginPanel;
    private PlayerClient client;
    private User currentUser;
    private DatabaseClass database;
    private JPanel container;
    
    public LoginControl(LoginPanel loginPanel, PlayerClient client, DatabaseClass database, JPanel container) {
        this.loginPanel = loginPanel;
        this.client = client;
        this.database = database;
        this.container = container;
        
        loginPanel.getSubmitButton().addActionListener(this);
        loginPanel.getCreateAccountButton().addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginPanel.getSubmitButton()) {
            // Validate input fields aren't empty
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
                ex.printStackTrace();
            }
        } else if (e.getSource() == loginPanel.getCreateAccountButton()) {
            // Return to initial panel when cancel is clicked
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "InitialPanel");
        }
    }
    
    public void handleLoginResult(LoginData result) {
        if (result.isSuccess()) {
            User user = database.getUser(loginPanel.getUsername());
            client.setCurrentUser(user);
            currentUser = user;
            JOptionPane.showMessageDialog(loginPanel, 
                "You are now logged in as " + currentUser, 
                "Login Success", 
                JOptionPane.INFORMATION_MESSAGE);
                    
            // Create and setup LobbyPanel
            LobbyPanel lobbyPanel = new LobbyPanel();
            container.add(lobbyPanel, "LobbyPanel");
            LobbyControl lobbyControl = new LobbyControl(lobbyPanel, client, container);
            client.setLobbyControl(lobbyControl);
            
            // Switch to LobbyPanel
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "LobbyPanel");
        } else {
            JOptionPane.showMessageDialog(loginPanel, 
                "Invalid username or password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}
