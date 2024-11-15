import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.List;

public class LeaderboardControl implements ActionListener {
    private LeaderboardPanel leaderboardPanel;
    private PlayerClient client;
    private JPanel container;
    
    public LeaderboardControl(LeaderboardPanel panel, PlayerClient client, JPanel container) {
        this.leaderboardPanel = panel;
        this.client = client;
        this.container = container;
        
        // Add action listener to back button
        leaderboardPanel.getBackButton().addActionListener(this);
        
        // Request leaderboard data from server
        requestLeaderboardUpdate();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle back button click
        if (e.getSource() == leaderboardPanel.getBackButton()) {
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "LobbyPanel");
        }
    }
    
    private void requestLeaderboardUpdate() {
        try {
            // Send request to server for updated leaderboard data
            client.sendToServer("REQUEST_LEADERBOARD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateLeaderboard(LeaderboardData data) {
        // Convert LeaderboardData to List<User>
        List<User> userList = data.toUserList(); // Assuming a method to convert exists
        // Update the leaderboard panel with new data
        leaderboardPanel.updateLeaderboard(userList);
    }
    
    // Method to refresh leaderboard data
    public void refreshLeaderboard() {
        requestLeaderboardUpdate();
    }
}
