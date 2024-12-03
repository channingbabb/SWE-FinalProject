import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LobbyControl {
    private LobbyPanel view;
    private LeaderboardPanel leaderboardPanel;
    private PlayerClient client;
    private JPanel container;
    private static final int DEFAULT_GAME_SIZE = 4;
    private WaitingRoomPanel waitingRoomPanel;

    public LobbyControl(LobbyPanel view, PlayerClient client, JPanel container) {
        this.view = view;
        this.client = client;
        this.container = container;
        this.leaderboardPanel = new LeaderboardPanel();
        
        setupListeners();
        
        refreshGames();
    }

    private void setupListeners() {
        view.getCreateGameButton().addActionListener(e -> createGame());
        view.getJoinGameButton().addActionListener(e -> joinGame());
        view.getLeaderboardButton().addActionListener(e -> showLeaderboard());
        view.getLogoutButton().addActionListener(e -> logout());
        view.getRefreshButton().addActionListener(e -> refreshGames());
    }

    private void createGame() {
        System.out.println("Current user: " + client.getCurrentUser());
        
        if (!client.isConnected()) {
            JOptionPane.showMessageDialog(view,
                    "Error: Not connected to server",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User currentUser = client.getCurrentUser();
        if (currentUser == null) {
            System.out.println("getCurrentUser() returned null");
            JOptionPane.showMessageDialog(view,
                    "Error: Not logged in",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String defaultGameName = currentUser.getUsername() + "-poker";
        
        String gameName = (String) JOptionPane.showInputDialog(view,
                "Enter game name:",
                "Create Game",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                defaultGameName);

        if (gameName != null && !gameName.trim().isEmpty()) {
            try {
                client.sendToServer("CREATE_GAME:" + gameName + ":" + currentUser.getUsername());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(view,
                    "Game name cannot be empty.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
        }
    }


    private void joinGame() {
        if (!client.isConnected()) {
            JOptionPane.showMessageDialog(view,
                    "Error: Not connected to server",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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
        if (!client.isConnected()) {
            JOptionPane.showMessageDialog(view,
                    "Error: Not connected to server",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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

    private void refreshGames() {
        if (!client.isConnected()) {
            JOptionPane.showMessageDialog(view,
                    "Error: Not connected to server",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            client.sendToServer("REQUEST_GAMES");
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public void handleGameJoined(boolean success, String message, String gameName, boolean isCreator) {
        SwingUtilities.invokeLater(() -> {
            if (success) {
                waitingRoomPanel = new WaitingRoomPanel(gameName, isCreator);
                container.add(waitingRoomPanel, "WaitingRoomPanel");
                
                waitingRoomPanel.getLeaveButton().addActionListener(e -> leaveGame());
                if (isCreator) {
                    waitingRoomPanel.getKickButton().addActionListener(e -> kickPlayer());
                    waitingRoomPanel.getStartGameButton().addActionListener(e -> startGame());
                }
                
                CardLayout cardLayout = (CardLayout) container.getLayout();
                cardLayout.show(container, "WaitingRoomPanel");
                
                try {
                    client.sendToServer("REQUEST_PLAYERS:" + gameName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(view,
                    "Failed to join game: " + message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void leaveGame() {
        try {
            client.sendToServer("LEAVE_GAME");
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "LobbyPanel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void kickPlayer() {
        String selectedPlayer = waitingRoomPanel.getSelectedPlayer();
        if (selectedPlayer != null) {
            try {
                client.sendToServer("KICK_PLAYER:" + selectedPlayer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(waitingRoomPanel,
                "Please select a player to kick",
                "No Player Selected",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void startGame() {
        System.out.println("Start game button clicked");
        try {
            client.sendToServer("START_GAME");
            System.out.println("Sent START_GAME to server");
        } catch (Exception ex) {
            System.err.println("Error sending START_GAME to server");
            ex.printStackTrace();
            JOptionPane.showMessageDialog(waitingRoomPanel,
                "Error starting game",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateWaitingRoomPlayers(List<User> players) {
        if (waitingRoomPanel != null) {
            SwingUtilities.invokeLater(() -> {
                waitingRoomPanel.updatePlayersList(players);
            });
        }
    }
}
