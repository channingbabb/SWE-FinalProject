package SWEFinalProject;

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
        
        leaderboardPanel.getBackButton().addActionListener(this);
        requestLeaderboardUpdate();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == leaderboardPanel.getBackButton()) {
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "LobbyPanel");
        }
    }
    
    private void requestLeaderboardUpdate() {
        try {
            client.sendToServer("REQUEST_LEADERBOARD");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateLeaderboard(LeaderboardData data) {
        List<User> userList = data.toUserList();
        leaderboardPanel.updateLeaderboard(userList);
    }
    
    public void refreshLeaderboard() {
        requestLeaderboardUpdate();
    }
}
