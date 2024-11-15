import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LobbyControl {
    private LobbyPanel view;
    private LeaderboardPanel leaderboardPanel;
    private PlayerClient client;
    private JPanel container;
    private static final int DEFAULT_GAME_SIZE = 4;

    public LobbyControl(LobbyPanel view, PlayerClient client, JPanel container) {
        this.view = view;
        this.client = client;
        this.container = container;
        this.leaderboardPanel = new LeaderboardPanel();
        
        setupListeners();
    }

    private void setupListeners() {
        view.getCreateGameButton().addActionListener(e -> createGame());
        view.getJoinGameButton().addActionListener(e -> joinGame());
        view.getLeaderboardButton().addActionListener(e -> showLeaderboard());
        view.getLogoutButton().addActionListener(e -> logout());
    }

    private void createGame() {
        String gameName = JOptionPane.showInputDialog(view, 
            "Enter game name:", 
            "Create Game", 
            JOptionPane.PLAIN_MESSAGE);
            
        if (gameName != null && !gameName.trim().isEmpty()) {
            Game game = new Game();
            game.addPlayer(gameName);
            try {
                client.sendToServer(game);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void joinGame() {
        String selectedGame = view.getSelectedGame();
        if (selectedGame != null) {
            try {
                client.sendToServer("JOIN_GAME:" + selectedGame);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(view,
                "Please select a game to join",
                "No Game Selected",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showLeaderboard() {
        try {
            client.sendToServer("REQUEST_LEADERBOARD");
            
            JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(view), 
                "Leaderboard", true);
            dialog.setContentPane(leaderboardPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(view);
            dialog.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(view,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                client.sendToServer("LOGOUT");
                CardLayout cardLayout = (CardLayout) container.getLayout();
                cardLayout.show(container, "LoginPanel");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateLeaderboard(LeaderboardData data) {
        List<User> users = data.toUserList();
        SwingUtilities.invokeLater(() -> {
            leaderboardPanel.updateLeaderboard(users);
        });
    }

    public void updateGames(String[] games) {
        SwingUtilities.invokeLater(() -> {
            view.updateGamesList(games);
        });
    }

    public void handleGameCreated(boolean success, String message) {
        SwingUtilities.invokeLater(() -> {
            if (success) {
                JOptionPane.showMessageDialog(view,
                    "Game created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view,
                    "Failed to create game: " + message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void handleGameJoined(boolean success, String message) {
        SwingUtilities.invokeLater(() -> {
            if (success) {
                CardLayout cardLayout = (CardLayout) container.getLayout();
                cardLayout.show(container, "GamePanel");
            } else {
                JOptionPane.showMessageDialog(view,
                    "Failed to join game: " + message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
