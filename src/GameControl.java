import java.util.List;
import javax.swing.*;

//GameControl class manages the interaction between game logic, server communication, and the GamePanel UI.
//It handles game action, updates game state
public class GameControl {
    private GamePanel gamePanel; //UI for the game
    private PlayerClient client; //client used for communicating with the server
    private User currentUser; //the user playing the game
    private int currentPot; //current pot value in the game
    
    //sets up initial state and message handlers
    public GameControl(PlayerClient client, String gameName) {
        this.client = client;
        this.currentUser = client.getCurrentUser();
        this.gamePanel = client.getGamePanel();
        
        setupActionListeners(); //setup UI action listeners
        setupMessageHandlers(); //set up message handlers for server responses
        
        //update the UI to reflect the user's initial balance
        gamePanel.updatePlayerBalance(currentUser.getBalance());
    }
    
    //setup handlers for messages that are sent from server
    private void setupMessageHandlers() {
        client.addMessageHandler("GAME_STATE", message -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    Message msg = (Message) message;
                    int pot = msg.getPot();
                    List<User> players = msg.getPlayers();
                    String currentPlayer = msg.getTurnPlayer();
                    updateGameState(pot, players); //update the UI with the new game state
                    gamePanel.updateTurnIndicator(currentPlayer); //highlight current turn
                    handlePlayerTurn(currentPlayer.equals(currentUser.getUsername())); //enables and disables controls
                } catch (Exception e) {
                    System.err.println("Error handling GAME_STATE: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        });
        
        //handle notification for player turn
        client.addMessageHandler("PLAYER_TURN", message -> {
            SwingUtilities.invokeLater(() -> {
                Message msg = (Message) message;
                String turnPlayer = msg.getTurnPlayer();
                boolean isYourTurn = turnPlayer.equals(currentUser.getUsername());
                handlePlayerTurn(isYourTurn); //enable disable controls based on who's turn it is
            });
        });
        
        //handle the result of a player's action
        client.addMessageHandler("GAME_ACTION_RESULT", message -> {
            SwingUtilities.invokeLater(() -> {
                Message msg = (Message) message;
                boolean success = msg.isSuccess();
                String errorMessage = msg.getErrorMessage();
                handleActionResult(success, errorMessage);
            });
        });
        
        //handle any game errors sent by the server
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
    
    //setup action listeners for game controls: Call, Raise, Fold, Check
    private void setupActionListeners() {
        gamePanel.addActionListeners(e -> {
            String command = e.getActionCommand();
            try {
                switch(command) {
                    case "Call":
                        int callAmount = currentPot - currentUser.getCurrentBet(); //determining the call amount for the current user
                        if (currentUser.getBalance() >= callAmount) { //user needs to have enough balance in order to proceed with call
                            Message callMessage = new Message("CALL", callAmount);
                            callMessage.setAction("CALL"); //saves that action is CALL in the message
                            System.out.println("CALL action sent to server with amount: " + callAmount);
                            client.sendMessage(callMessage); //uses sendMessage in PlayerClient in order to send to server
                        } else {
                            JOptionPane.showMessageDialog(gamePanel,
                                "Insufficient funds to call! Your balance: $" + currentUser.getBalance(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    case "Fold":
                        //client.sendToServer("GAME_ACTION:FOLD");
                    	Message foldMessage = new Message("FOLD", 0);
                    	foldMessage.setAction("FOLD");
                    	client.sendMessage(foldMessage);
                        break;
                    case "Raise":
                        String amount = JOptionPane.showInputDialog("Enter raise amount:"); //gets the amount from the user
                        if (amount != null && !amount.isEmpty()) {
                            try {
                                int raiseAmount = Integer.parseInt(amount);
                                if (raiseAmount > 0) {
                                    if (raiseAmount <= currentUser.getBalance()) { //also have to make sure user has enough balance
                                        System.out.println("Sending raise action: " + raiseAmount);
                                        client.sendToServer("GAME_ACTION:RAISE:" + raiseAmount);
                                        System.out.println("Raise action sent to server");
                                    } else {
                                        JOptionPane.showMessageDialog(gamePanel, 
                                            "Insufficient funds! Your balance: $" + currentUser.getBalance(), 
                                            "Error", 
                                            JOptionPane.ERROR_MESSAGE);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(gamePanel,
                                        "Raise amount must be greater than 0",
                                        "Invalid Input",
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
                    	System.out.println("Sending CHECK action to server");
                        //client.sendToServer("GAME_ACTION:CHECK");
                    	Message checkMessage = new Message("CHECK", 0);
                    	checkMessage.setAction("CHECK");
                    	client.sendMessage(checkMessage);
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(gamePanel,
                    "Error performing action: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    //updates the game state in the UI (game panel)
    private void updateGameState(int pot, List<User> players) {
        currentPot = pot; 
        gamePanel.updatePot(currentPot);
        gamePanel.updatePlayers(players);
        
        //update the current user's state based on updated info
        for (User player : players) {
            if (player.getUsername().equals(currentUser.getUsername())) {
                currentUser = player;
                gamePanel.updatePlayerBalance(player.getBalance()); //update balance display
                break;
            }
        }
    }
    
    //enable/disable buttons based on if its their turn or not
    private void handlePlayerTurn(boolean isYourTurn) {
        gamePanel.setButtonsEnabled(isYourTurn);
    }
    
    //handles result of an action sent to the server
    private void handleActionResult(boolean success, String errorMessage) {
        if (!success) {
            JOptionPane.showMessageDialog(gamePanel,
                errorMessage,
                "Action Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //gets GamePanel instance associated with this control
    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
