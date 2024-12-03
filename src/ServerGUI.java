package SWEFinalProject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.IOException;

public class ServerGUI extends JFrame {
    private JTextField portField;
    private JTextField serverNameField;
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
        serverNameField = new JTextField("Poker Server", 15);
        portField = new JTextField("8300", 10);
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);

        String[] columnNames = {"Client ID", "Username", "Balance", "Status", "Activity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        clientsTable = new JTable(tableModel);
        clientsTable.getTableHeader().setReorderingAllowed(false);

        topPanel.add(new JLabel("Server Name:"));
        topPanel.add(serverNameField);
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
                    client.isAuthenticated() ? "Authenticated" : "Unauthenticated",
                    client.getActivity() != null ? client.getActivity() : "Not logged in"
                });
            }
        }
    }

    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText());
            String serverName = serverNameField.getText().trim();
            
            if (serverName.isEmpty()) {
                logArea.append("Error: Server name cannot be empty\n");
                return;
            }

            if (server != null) {
                try {
                    server.close();
                } catch (Exception e) {
                }
            }
            
            server = new ServerClass(port, serverName);
            server.setLogArea(logArea);
            server.listen();
            
            InetAddress localHost = InetAddress.getLocalHost();
            logArea.append("Server '" + serverName + "' started\n");
            logArea.append("Local IPv4 Address: " + localHost.getHostAddress() + "\n");
            logArea.append("Port: " + port + "\n");
            
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            portField.setEnabled(false);
            serverNameField.setEnabled(false);
            
            startDiscoveryService(port, serverName);
        } catch (Exception e) {
            logArea.append("Error starting server: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

    private void startDiscoveryService(int gamePort, String serverName) {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(8301)) {
                byte[] receiveData = new byte[1024];
                while (!socket.isClosed() && server != null) {
                    try {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(receivePacket);
                        
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        if (message.equals("POKER_SERVER_DISCOVERY")) {
                            String response = gamePort + "|" + serverName;
                            byte[] sendData = response.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(
                                sendData,
                                sendData.length,
                                receivePacket.getAddress(),
                                receivePacket.getPort()
                            );
                            socket.send(sendPacket);
                        }
                    } catch (IOException e) {
                        if (!socket.isClosed()) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void stopServer() {
        try {
            server.close();
            logArea.append("Server '" + server.getServerName() + "' stopped\n");
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            portField.setEnabled(true);
            serverNameField.setEnabled(true);
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