import javax.swing.*;
import java.awt.*;

public class LobbyPanel extends JPanel {
    private JButton createGameButton;
    private JButton joinGameButton;
    private JButton leaderboardButton;
    private JButton logoutButton;
    private JList<String> gamesList;
    private DefaultListModel<String> gamesModel;
    
    public LobbyPanel() {
        setLayout(new BorderLayout());
        
        createGameButton = new JButton("Create Game");
        joinGameButton = new JButton("Join Game");
        leaderboardButton = new JButton("Leaderboard");
        logoutButton = new JButton("Logout");
        
        gamesModel = new DefaultListModel<>();
        gamesList = new JList<>(gamesModel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createGameButton);
        buttonPanel.add(joinGameButton);
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(logoutButton);
        
        add(new JScrollPane(gamesList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public JButton getCreateGameButton() {
        return createGameButton;
    }
    
    public JButton getJoinGameButton() {
        return joinGameButton;
    }
    
    public JButton getLeaderboardButton() {
        return leaderboardButton;
    }
    
    public JButton getLogoutButton() {
        return logoutButton;
    }
    
    public void updateGamesList(String[] games) {
        gamesModel.clear();
        for (String game : games) {
            gamesModel.addElement(game);
        }
    }
    
    public String getSelectedGame() {
        return gamesList.getSelectedValue();  // Assuming you have a JList named gamesList
    }
}
