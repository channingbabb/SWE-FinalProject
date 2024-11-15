import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateAccountControl implements ActionListener {
    private CreateAccountPanel createAccountPanel;
    private PlayerClient client;
    
    public CreateAccountControl(CreateAccountPanel panel, PlayerClient client) {
        this.createAccountPanel = panel;
        this.client = client;
        
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
