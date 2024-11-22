import javax.swing.*;
import java.awt.event.ActionListener;

public class GameControl extends CardClass {
    private GamePanel gamePanel;
    private PlayerClient client;
    private Hand playerHand;
    private int balance;
    private int pot;
    
    public GameControl(PlayerClient client) {
        super();
        this.client = client;
        this.gamePanel = new GamePanel();
        this.playerHand = new Hand();
        this.balance = 1000; // Starting balance
        this.pot = 0;
        
        setupActionListeners();
    }
    
    private void setupActionListeners() {
        gamePanel.addActionListeners(e -> {
            switch(e.getActionCommand()) {
                case "Call":
                    handleCall();
                    break;
                case "Fold":
                    handleFold();
                    break;
                case "Raise":
                    handleRaise();
                    break;
                case "Check":
                    handleCheck();
                    break;
            }
        });
    }
    
    private void handleCall() {
        // Implement call logic
    }
    
    private void handleFold() {
        // Implement fold logic
    }
    
    private void handleRaise() {
        // Implement raise logic
    }
    
    private void handleCheck() {
        // Implement check logic
    }

    // Add getter for testing purposes--------------------
    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
