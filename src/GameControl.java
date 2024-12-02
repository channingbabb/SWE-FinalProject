import java.util.List;
import javax.swing.*;

public class GameControl {
    private GamePanel gamePanel;
    private PlayerClient client;
    private User currentUser;
    private int currentPot;
    
    public GameControl(PlayerClient client, String gameName) {
        this.client = client;
        this.currentUser = client.getCurrentUser();
        this.gamePanel = client.getGamePanel();
        
        setupActionListeners();
        setupMessageHandlers();
        
        gamePanel.updatePlayerBalance(currentUser.getBalance());
    }
    
    private void setupMessageHandlers() {
        client.addMessageHandler("GAME_STATE", message -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    Message msg = (Message) message;
                    int pot = msg.getPot();
                    List<User> players = msg.getPlayers();
                    String currentPlayer = msg.getTurnPlayer();
                    updateGameState(pot, players);
                    gamePanel.updateTurnIndicator(currentPlayer);
                    handlePlayerTurn(currentPlayer.equals(currentUser.getUsername()));
                } catch (Exception e) {
                    System.err.println("Error handling GAME_STATE: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        });
        
        client.addMessageHandler("PLAYER_TURN", message -> {
            SwingUtilities.invokeLater(() -> {
                Message msg = (Message) message;
                String turnPlayer = msg.getTurnPlayer();
                boolean isYourTurn = turnPlayer.equals(currentUser.getUsername());
                handlePlayerTurn(isYourTurn);
            });
        });
        
        client.addMessageHandler("GAME_ACTION_RESULT", message -> {
            SwingUtilities.invokeLater(() -> {
                Message msg = (Message) message;
                boolean success = msg.isSuccess();
                String errorMessage = msg.getErrorMessage();
                handleActionResult(success, errorMessage);
            });
        });
        
        client.addMessageHandler("GAME_ERROR", message -> {
            SwingUtilities.invokeLater(() -> {
                Message msg = (Message) message;
                JOptionPane.showMessageDialog(gamePanel,
                    msg.getErrorMessage(),
                    "Game Error",
                    JOptionPane.ERROR_MESSAGE);
            });
        });
    }
    
    private void setupActionListeners() {
        gamePanel.addActionListeners(e -> {
            String command = e.getActionCommand();
            try {
                Message msg = new Message("GAME_ACTION");
                switch(command) {
                    case "Call":
                        msg.setAction("CALL");
                        client.sendMessage(msg);
                        break;
                    case "Fold":
                        msg.setAction("FOLD");
                        client.sendMessage(msg);
                        break;
                    case "Raise":
                        String amount = JOptionPane.showInputDialog("Enter raise amount:");
                        if (amount != null && !amount.isEmpty()) {
                            try {
                                int raiseAmount = Integer.parseInt(amount);
                                if (raiseAmount <= currentUser.getBalance()) {
                                    msg.setAction("RAISE");
                                    msg.setAmount(raiseAmount);
                                    client.sendMessage(msg);
                                } else {
                                    JOptionPane.showMessageDialog(gamePanel, 
                                        "Insufficient funds!", 
                                        "Error", 
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(gamePanel,
                                    "Please enter a valid number",
                                    "Invalid Input",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        break;
                    case "Check":
                        msg.setAction("CHECK");
                        client.sendMessage(msg);
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
    
    private void updateGameState(int pot, List<User> players) {
        currentPot = pot;
        gamePanel.updatePot(currentPot);
        gamePanel.updatePlayers(players);
        
        for (User player : players) {
            if (player.getUsername().equals(currentUser.getUsername())) {
                currentUser = player;
                gamePanel.updatePlayerBalance(player.getBalance());
                break;
            }
        }
    }
    
    private void handlePlayerTurn(boolean isYourTurn) {
        gamePanel.setButtonsEnabled(isYourTurn);
    }
    
    private void handleActionResult(boolean success, String errorMessage) {
        if (!success) {
            JOptionPane.showMessageDialog(gamePanel,
                errorMessage,
                "Action Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
