package SWEFinalProject;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

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
    private JTable serverTable;
    private DefaultTableModel serverTableModel;
    private JButton refreshButton;
    private JPanel statusPanel;
    private JTextField statusServerNameField;
    private JTextField statusAddressField;
    private JTextField statusPortField;
    private JLabel userInfoLabel;
    private JLabel balanceLabel;
    
    public ClientGUI() {
        setTitle("Poker Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 800));
        
        String[] columnNames = {"Server Name", "Address", "Port"};
        serverTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JPanel connectionPanel = new JPanel(new BorderLayout());
        JPanel manualConnectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel serverListPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        hostField = new JTextField("localhost", 15);
        portField = new JTextField("8300", 10);
        serverTable = new JTable(serverTableModel);
        serverTable.getTableHeader().setReorderingAllowed(false);
        serverTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);
        refreshButton = new JButton("Refresh");
        JButton joinButton = new JButton("Join Selected");
        
        connectButton.addActionListener(e -> connect());
        disconnectButton.addActionListener(e -> disconnect());
        refreshButton.addActionListener(e -> refreshServerList());
        joinButton.addActionListener(e -> joinSelectedServer());
        
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        
        JPanel blankPanel = new JPanel();
        cardPanel.add(blankPanel, "BlankPanel");
        
        manualConnectPanel.add(new JLabel("Host:"));
        manualConnectPanel.add(hostField);
        manualConnectPanel.add(new JLabel("Port:"));
        manualConnectPanel.add(portField);
        manualConnectPanel.add(connectButton);
        manualConnectPanel.add(disconnectButton);
        
        serverListPanel.add(new JLabel("Available Servers:"), BorderLayout.NORTH);
        serverListPanel.add(new JScrollPane(serverTable), BorderLayout.CENTER);
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(joinButton);
        serverListPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        connectionPanel.add(manualConnectPanel, BorderLayout.NORTH);
        connectionPanel.add(serverListPanel, BorderLayout.CENTER);
        
        JPanel topWrapper = new JPanel(new BorderLayout());
        
        userInfoLabel = new JLabel("User: Not Logged In");
        balanceLabel = new JLabel("Balance: $0.00");
        
        statusPanel = new JPanel(new GridBagLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);  // Small spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        statusServerNameField = new JTextField("Not Connected", 15);
        statusAddressField = new JTextField("", 15);
        statusPortField = new JTextField("", 6);
        
        statusServerNameField.setEditable(false);
        statusAddressField.setEditable(false);
        statusPortField.setEditable(false);
        
        Color bg = statusPanel.getBackground();
        statusServerNameField.setBackground(bg);
        statusAddressField.setBackground(bg);
        statusPortField.setBackground(bg);
        
        gbc.gridx = 0; gbc.gridy = 0;
        statusPanel.add(new JLabel("Server:"), gbc);
        
        gbc.gridx = 1;
        statusPanel.add(statusServerNameField, gbc);
        
        gbc.gridx = 2;
        statusPanel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 3;
        statusPanel.add(statusAddressField, gbc);
        
        gbc.gridx = 4;
        statusPanel.add(new JLabel("Port:"), gbc);
        
        gbc.gridx = 5;
        statusPanel.add(statusPortField, gbc);
        
        gbc.gridx = 6;
        statusPanel.add(userInfoLabel, gbc);
        
        gbc.gridx = 7;
        statusPanel.add(balanceLabel, gbc);
        
        gbc.gridx = 8;
        JButton statusDisconnectButton = new JButton("Disconnect");
        statusPanel.add(statusDisconnectButton, gbc);
        
        statusDisconnectButton.addActionListener(e -> disconnect());
        statusPanel.setVisible(false);
        
        topWrapper.add(statusPanel, BorderLayout.NORTH);
        topWrapper.add(connectionPanel, BorderLayout.CENTER);
        
        add(topWrapper, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
        
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
            
            statusServerNameField.setText(client.getServerName());
            statusAddressField.setText(host);
            statusPortField.setText(String.valueOf(port));
            statusPanel.setVisible(true);
            
            Component connectionPanel = ((JPanel)getContentPane().getComponent(0)).getComponent(1);
            connectionPanel.setVisible(false);
            
            cardPanel.setPreferredSize(new Dimension(800, 600));
            pack();
            
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
            
            statusServerNameField.setText("Not Connected");
            statusAddressField.setText("");
            statusPortField.setText("");
            userInfoLabel.setText("User: Not Logged In");
            balanceLabel.setText("Balance: $0.00");
            statusPanel.setVisible(false);
            
            Component connectionPanel = ((JPanel)getContentPane().getComponent(0)).getComponent(1);
            connectionPanel.setVisible(true);
            
            cardPanel.removeAll();
            JPanel blankPanel = new JPanel();
            cardPanel.add(blankPanel, "BlankPanel");
            cardLayout.show(cardPanel, "BlankPanel");
            
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            hostField.setEnabled(true);
            portField.setEnabled(true);
            
            initialPanel = null;
            loginPanel = null;
            createAccountPanel = null;
            
            pack();
            revalidate();
            repaint();
            
        } catch (Exception e) {
        }
    }
    
    private void refreshServerList() {
        new Thread(() -> {
            ServerDiscovery discovery = new ServerDiscovery();
            List<ServerDiscovery.ServerInfo> servers = discovery.discoverServers();
            SwingUtilities.invokeLater(() -> {
                serverTableModel.setRowCount(0);
                for (ServerDiscovery.ServerInfo server : servers) {
                    serverTableModel.addRow(new Object[]{
                        server.getName(),
                        server.getAddress(),
                        String.valueOf(server.getPort())
                    });
                }
            });
        }).start();
    }
    
    private void joinSelectedServer() {
        int selectedRow = serverTable.getSelectedRow();
        if (selectedRow != -1) {
            String address = (String) serverTable.getValueAt(selectedRow, 1);
            String port = (String) serverTable.getValueAt(selectedRow, 2);
            hostField.setText(address);
            portField.setText(port);
            connect();
        }
    }
    
    public void updateUserInfo(String username, double balance) {
        userInfoLabel.setText("User: " + username);
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
    }
    
    public void updateServerInfo(String serverName) {
        statusServerNameField.setText(serverName);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientGUI().setVisible(true);
        });
    }
}