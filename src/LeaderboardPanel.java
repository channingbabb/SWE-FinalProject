import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LeaderboardPanel extends JPanel {
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    
    public LeaderboardPanel() {
        setLayout(new BorderLayout());
        
        String[] columnNames = {"Rank", "Username", "Balance"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.getTableHeader().setReorderingAllowed(false);
        
        backButton = new JButton("Back to Lobby");
        
        add(new JScrollPane(leaderboardTable), BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }
    
    public void updateLeaderboard(List<User> users) {
        tableModel.setRowCount(0);
        int rank = 1;
        for (User user : users) {
            tableModel.addRow(new Object[]{
                rank++,
                user.getUsername(),
                "$" + user.getBalance()
            });
        }
    }
    
    public JButton getBackButton() {
        return backButton;
    }
}
