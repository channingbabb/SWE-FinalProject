import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GamePanel extends LoginPanel {
    private JLabel balanceLabel;
    private JLabel potLabel;
    private JButton callButton;
    private JButton foldButton;
    private JButton raiseButton;
    private JButton checkButton;
    private JTextField raiseAmount;
    
    public GamePanel() {
        setLayout(new BorderLayout());
        
        // Create game controls
        JPanel controlPanel = new JPanel();
        balanceLabel = new JLabel("Balance: $1000");
        potLabel = new JLabel("Pot: $0");
        callButton = new JButton("Call");
        foldButton = new JButton("Fold");
        raiseButton = new JButton("Raise");
        checkButton = new JButton("Check");
        raiseAmount = new JTextField(10);
        
        controlPanel.add(balanceLabel);
        controlPanel.add(potLabel);
        controlPanel.add(callButton);
        controlPanel.add(foldButton);
        controlPanel.add(raiseButton);
        controlPanel.add(checkButton);
        controlPanel.add(raiseAmount);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    public void setBalance(int balance) {
        balanceLabel.setText("Balance: $" + balance);
    }
    
    public void setPot(int pot) {
        potLabel.setText("Pot: $" + pot);
    }
    
    public void addActionListeners(ActionListener listener) {
        callButton.addActionListener(listener);
        foldButton.addActionListener(listener);
        raiseButton.addActionListener(listener);
        checkButton.addActionListener(listener);
    }
}
