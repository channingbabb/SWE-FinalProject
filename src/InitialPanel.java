import javax.swing.*;
import java.awt.*;

public class InitialPanel extends JPanel {
    private JLabel titleLabel;
    private JButton loginButton;
    private JButton createAccountButton;
    
    public InitialPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        titleLabel = new JLabel("Poker Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        loginButton = new JButton("Login");
        createAccountButton = new JButton("Create Account");
        
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 30, 10);
        add(titleLabel, c);
        
        c.gridy = 1;
        c.insets = new Insets(10, 10, 10, 10);
        add(loginButton, c);
        
        c.gridy = 2;
        add(createAccountButton, c);
    }
    
    public JButton getLoginButton() {
        return loginButton;
    }
    
    public JButton getCreateAccountButton() {
        return createAccountButton;
    }
}
