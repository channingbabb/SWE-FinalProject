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
        
        // Create connection panel components
        JPanel connectionPanel = new JPanel();
        hostField = new JTextField("localhost", 15);
        portField = new JTextField("8300", 10);
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);
        
        // Add connection components to panel
        connectionPanel.add(new JLabel("Host:"));
        connectionPanel.add(hostField);
        connectionPanel.add(new JLabel("Port:"));
        connectionPanel.add(portField);
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);
        
        // Create card panel for different screens
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        // Add a blank panel initially
        JPanel blankPanel = new JPanel();
        cardPanel.add(blankPanel, "BlankPanel");
        
        // Add panels to frame
        add(connectionPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        
        // Add action listeners
        connectButton.addActionListener(e -> connect());
        disconnectButton.addActionListener(e -> disconnect());
        
        // Initialize database
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
            
            // Initialize panels
            initialPanel = new InitialPanel();
            loginPanel = new LoginPanel();
            createAccountPanel = new CreateAccountPanel();
            
            // Add panels to card layout
            cardPanel.add(initialPanel, "InitialPanel");
            cardPanel.add(loginPanel, "LoginPanel");
            cardPanel.add(createAccountPanel, "CreateAccountPanel");
            
            // Set up controls
            InitialControl initialControl = new InitialControl(initialPanel, client, cardPanel);
            LoginControl loginControl = new LoginControl(loginPanel, client, database, cardPanel);
            loginPanel.setController(loginControl);
            CreateAccountControl createAccountControl = new CreateAccountControl(createAccountPanel, client, cardPanel);
            
            // Set controls in client
            client.setLoginControl(loginControl);
            client.setCreateAccountControl(createAccountControl);
            
            // Open connection
            client.openConnection();
            
            JOptionPane.showMessageDialog(this,
                "Successfully connected to server at " + host + ":" + port,
                "Connection Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            hostField.setEnabled(false);
            portField.setEnabled(false);
            
            // Show initial panel after successful connection
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
            
            // Remove all panels except blank panel
            cardPanel.removeAll();
            JPanel blankPanel = new JPanel();
            cardPanel.add(blankPanel, "BlankPanel");
            cardLayout.show(cardPanel, "BlankPanel");
            
            // Clear panel references
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