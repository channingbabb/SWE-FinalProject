import javax.swing.*;
import java.awt.event.ActionListener;

public class GameControl extends CardClass {
    private GamePanel gamePanel;
    private PlayerClient client;
    private Hand playerHand;
    private int balance;
    private int pot;
    
    public GameControl(PlayerClient client, String suit, int rank) {
        super(suit, rank);
        this.client = client;
        this.gamePanel = new GamePanel(client.getPlayers());
        this.playerHand = new Hand();
        this.balance = 1000; // Starting balance
        this.pot = 0;
        
        setupActionListeners();
    }
    
    private void setupActionListeners() {
        gamePanel.addActionListeners(e -> {
            String command = e.getActionCommand();
            try {
                switch(command) {
                    case "Call":
                        client.sendToServer("GAME_ACTION:CALL");
                        break;
                    case "Fold":
                        client.sendToServer("GAME_ACTION:FOLD");
                        break;
                    case "Raise":
                        String amount = JOptionPane.showInputDialog("Enter raise amount:");
                        if (amount != null && !amount.isEmpty()) {
                            client.sendToServer("GAME_ACTION:RAISE:" + amount);
                        }
                        break;
                    case "Check":
                        client.sendToServer("GAME_ACTION:CHECK");
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
