package SWEFinalProject;

import java.awt.*;
import javax.swing.*;

public class LobbyPanel extends JPanel {
    private JButton createGameButton;
    private JButton joinGameButton;
    private JButton leaderboardButton;
    private JButton logoutButton;
    private JButton refreshButton;
    private JList<String> gamesList;
    private DefaultListModel<String> gamesModel;
    
    public LobbyPanel() {
        setLayout(new BorderLayout());
        
        createGameButton = new JButton("Create Game");
        joinGameButton = new JButton("Join Game");
        leaderboardButton = new JButton("Leaderboard");
        logoutButton = new JButton("Logout");
        refreshButton = new JButton("Refresh Games");
        
        gamesModel = new DefaultListModel<>();
        gamesList = new JList<>(gamesModel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createGameButton);
        buttonPanel.add(joinGameButton);
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(logoutButton);
        buttonPanel.add(refreshButton);
        
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
    
    public JButton getRefreshButton() {
        return refreshButton;
    }
    
    public void updateGamesList(String[] games) {
        gamesModel.clear();
        for (String game : games) {
            String gameName = game.contains("|") ? game.split("\\|")[0] : game;
            gamesModel.addElement(gameName);
        }
    }
    
    public String getSelectedGame() {
        return gamesList.getSelectedValue();
    }
}
