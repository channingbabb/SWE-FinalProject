import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private JTextField hostField;
    private JTextField portField;
    private JButton connectButton;
    private JButton disconnectButton;
    private PlayerClient client;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private InitialPanel initialPanel;
    private LoginPanel loginPanel;
    private CreateAccountPanel createAccountPanel;
    private DatabaseClass database;
    
    public ClientGUI() {
        setTitle("Poker Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));  
        
        JPanel connectionPanel = new JPanel();
        hostField = new JTextField("localhost", 15);
        portField = new JTextField("8300", 10);
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);
        
        connectionPanel.add(new JLabel("Host:"));
        connectionPanel.add(hostField);
        connectionPanel.add(new JLabel("Port:"));
        connectionPanel.add(portField);
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);
        
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        JPanel blankPanel = new JPanel();
        cardPanel.add(blankPanel, "BlankPanel");
        
        add(connectionPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        
        connectButton.addActionListener(e -> connect());
        disconnectButton.addActionListener(e -> disconnect());
        
        try {
            database = new DatabaseClass();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error connecting to database: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void connect() {
        try {
            String host = hostField.getText();
            int port = Integer.parseInt(portField.getText());
            
            client = new PlayerClient(host, port);
            
            initialPanel = new InitialPanel();
            loginPanel = new LoginPanel();
            createAccountPanel = new CreateAccountPanel();
            
            cardPanel.add(initialPanel, "InitialPanel");
            cardPanel.add(loginPanel, "LoginPanel");
            cardPanel.add(createAccountPanel, "CreateAccountPanel");
            
            InitialControl initialControl = new InitialControl(initialPanel, client, cardPanel);
            LoginControl loginControl = new LoginControl(loginPanel, client, cardPanel);
            loginPanel.setController(loginControl);
            CreateAccountControl createAccountControl = new CreateAccountControl(createAccountPanel, client, cardPanel);
            
            client.setLoginControl(loginControl);
            client.setCreateAccountControl(createAccountControl);
            
            client.openConnection();
            
            client.setContainer(cardPanel);
            
            JOptionPane.showMessageDialog(this,
                "Successfully connected to server at " + host + ":" + port,
                "Connection Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            hostField.setEnabled(false);
            portField.setEnabled(false);
            
            cardLayout.show(cardPanel, "InitialPanel");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error connecting to server: " + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void disconnect() {
        try {
            client.closeConnection();
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            hostField.setEnabled(true);
            portField.setEnabled(true);
            
            cardPanel.removeAll();
            JPanel blankPanel = new JPanel();
            cardPanel.add(blankPanel, "BlankPanel");
            cardLayout.show(cardPanel, "BlankPanel");
            
            initialPanel = null;
            loginPanel = null;
            createAccountPanel = null;
            
            revalidate();
            repaint();
            
        } catch (Exception e) {
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientGUI().setVisible(true);
        });
    }
}