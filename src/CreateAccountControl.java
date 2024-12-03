import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.CardLayout;

//CreateAccountControl class manages interactions between the 
//CreateAccountPanel, PlayerClient, and the container that holds different panels in GUI
public class CreateAccountControl implements ActionListener {
    private CreateAccountPanel createAccountPanel;
    private PlayerClient client;
    private JPanel container;
    
    public CreateAccountControl(CreateAccountPanel panel, PlayerClient client, JPanel container) {
        this.createAccountPanel = panel; //UI part of the creating account
        this.client = client; //client that communicates with the server
        this.container = container; //container that holds the different panels with CardLayout
        
        //action listeners for cancel, submit buttons
        panel.getSubmitButton().addActionListener(this);
        panel.getCancelButton().addActionListener(this);
    }
    
    //Handles action events from the CreateAccountPanel
    @Override
    public void actionPerformed(ActionEvent e) {
    	//handle submit button
        if (e.getSource() == createAccountPanel.getSubmitButton()) {
        	//validate that the password and confirm password fields match
            if (!createAccountPanel.getPassword().equals(createAccountPanel.getConfirmPassword())) {
                JOptionPane.showMessageDialog(createAccountPanel, 
                    "Passwords do not match!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return; //exit if the validation fails
            }
            
            //create data obj
            CreateAccountData data = new CreateAccountData(
                createAccountPanel.getUsername(),
                createAccountPanel.getPassword()
            );
            
            //send the account creation request to the server 
            try {
                client.sendToServer(data);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //handle cancel button
        } else if (e.getSource() == createAccountPanel.getCancelButton()) {
        	//switch back to initialPanel
            CardLayout cardLayout = (CardLayout) container.getLayout();
            cardLayout.show(container, "InitialPanel");
        }
    }
    
    //processes the result of the create account from server
    public void handleCreateAccountResult(CreateAccountData result) {
    	//if it was successful, display msg
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(createAccountPanel,
                "Account created successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            // Switch to login panel
        } else { //if creating account fails, display error msg
            JOptionPane.showMessageDialog(createAccountPanel,
                "Failed to create account. Username may already exist.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
