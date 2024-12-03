package SWEFinalProject;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class WaitingRoomPanel extends JPanel {
    private JTable playersTable;
    private DefaultTableModel tableModel;
    private JButton kickButton;
    private JButton leaveButton;
    private JButton startGameButton;
    private String gameName;
    private boolean isCreator;
    
    public WaitingRoomPanel(String gameName, boolean isCreator) {
        this.gameName = gameName;
        this.isCreator = isCreator;
        setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Waiting Room: " + gameName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        String[] columnNames = {"Username", "Balance"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        playersTable = new JTable(tableModel);
        playersTable.getTableHeader().setReorderingAllowed(false);
        
        JPanel buttonPanel = new JPanel();
        leaveButton = new JButton("Leave Game");
        buttonPanel.add(leaveButton);
        
        if (isCreator) {
            kickButton = new JButton("Kick Player");
            startGameButton = new JButton("Start Game");
            startGameButton.setEnabled(false);
            buttonPanel.add(kickButton);
            buttonPanel.add(startGameButton);
        }
        
        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(playersTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void updatePlayersList(List<User> players) {
        tableModel.setRowCount(0);
        for (User player : players) {
            tableModel.addRow(new Object[]{
                player.getUsername(),
                "$" + player.getBalance()
            });
        }
        
        if (isCreator && startGameButton != null) {
            startGameButton.setEnabled(players.size() >= 2 && players.size() <= 8);
        }
    }
    
    public String getSelectedPlayer() {
        int selectedRow = playersTable.getSelectedRow();
        return selectedRow != -1 ? (String) tableModel.getValueAt(selectedRow, 0) : null;
    }
    
    public JButton getKickButton() {
        return kickButton;
    }
    
    public JButton getLeaveButton() {
        return leaveButton;
    }
    
    public JButton getStartGameButton() {
        return startGameButton;
    }
    
    public void setController(WaitingRoomControl controller) {
        if (startGameButton != null) {
            startGameButton.addActionListener(e -> controller.handleStartGame());
        }
        leaveButton.addActionListener(e -> controller.handleLeaveGame());
        if (kickButton != null) {
            kickButton.addActionListener(e -> controller.handleKickPlayer());
        }
    }
}