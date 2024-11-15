import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class LoginControl implements ActionListener {
    private LoginPanel loginPanel;
    private PlayerClient client;
    private User currentUser;
    private DatabaseClass database;
    
    public LoginControl(LoginPanel loginPanel, PlayerClient client, DatabaseClass database) {
        this.loginPanel = loginPanel;
        this.client = client;
        this.database = database;
        
        loginPanel.getSubmitButton().addActionListener(this);
        loginPanel.getCreateAccountButton().addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginPanel.getSubmitButton()) {
            LoginData data = new LoginData(
                loginPanel.getUsername(),
                loginPanel.getPassword()
            );
            try {
                client.sendToServer(data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void handleLoginResult(LoginData result) {
        if (result.isSuccess()) {
            currentUser = database.getUser(loginPanel.getUsername());
            if (currentUser != null) {
                // Switch to lobby panel with user data
                // You'll need to pass currentUser to LobbyControl
            }
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
