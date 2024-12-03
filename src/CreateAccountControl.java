import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.CardLayout;

public class CreateAccountControl implements ActionListener {
    private CreateAccountPanel createAccountPanel;
    private PlayerClient client;
    private JPanel container;
    
    public CreateAccountControl(CreateAccountPanel panel, PlayerClient client, JPanel container) {
        this.createAccountPanel = panel;
        this.client = client;
        this.container = container;
        
        panel.getSubmitButton().addActionListener(this);
        panel.getCancelButton().addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createAccountPanel.getSubmitButton()) {
            if (!createAccountPanel.getPassword().equals(createAccountPanel.getConfirmPassword())) {
                JOptionPane.showMessageDialog(createAccountPanel, 
                    "Passwords do not match!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            CreateAccountData data = new CreateAccountData(
                createAccountPanel.getUsername(),
                createAccountPanel.getPassword()
            );
            
            try {
                client.sendToServer(data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == createAccountPanel.getCancelButton()) {
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "InitialPanel");
        }
    }
    
    public void handleCreateAccountResult(CreateAccountData result) {
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(createAccountPanel,
                "Account created successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            // Switch to login panel
        } else {
            JOptionPane.showMessageDialog(createAccountPanel,
                "Failed to create account. Username may already exist.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
