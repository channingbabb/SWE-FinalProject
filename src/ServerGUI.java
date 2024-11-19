import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ServerGUI extends JFrame {
    private JTextField portField;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea logArea;
    private ServerClass server;
    private JTable clientsTable;
    private DefaultTableModel tableModel;
    
    public ServerGUI() {
        setTitle("Poker Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        portField = new JTextField("8300", 10);
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);

        String[] columnNames = {"Client ID", "Username", "Balance", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientsTable = new JTable(tableModel);
        clientsTable.getTableHeader().setReorderingAllowed(false);

        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(startButton);
        topPanel.add(stopButton);
        
        splitPane.setTopComponent(new JScrollPane(logArea));
        splitPane.setBottomComponent(new JScrollPane(clientsTable));
        splitPane.setDividerLocation(200);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());

        Timer refreshTimer = new Timer(1000, e -> updateClientsTable());
        refreshTimer.start();
        
        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (server != null) {
                    try {
                        server.close();
                    } catch (Exception ex) {
                    }
                }
                System.exit(0);
            }
        });
    }

    private void updateClientsTable() {
        if (server != null) {
            List<ConnectedClient> clients = server.getConnectedClients();
            tableModel.setRowCount(0);
            for (ConnectedClient client : clients) {
                tableModel.addRow(new Object[]{
                    client.getClientId(),
                    client.getUsername() != null ? client.getUsername() : "Not logged in",
                    client.getBalance(),
                    client.isAuthenticated() ? "Authenticated" : "Unauthenticated"
                });
            }
        }
    }

    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText());

            if (server != null) {
                try {
                    server.close();
                } catch (Exception e) {
                }
            }
            
            server = new ServerClass(port);
            server.setLogArea(logArea);
            server.listen();
            
            logArea.append("Server started on port " + port + "\n");
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            portField.setEnabled(false);
        } catch (Exception e) {
            logArea.append("Error starting server: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void stopServer() {
        try {
            server.close();
            logArea.append("Server stopped\n");
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            portField.setEnabled(true);
        } catch (Exception e) {
            logArea.append("Error stopping server: " + e.getMessage() + "\n");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ServerGUI().setVisible(true);
        });
    }
}