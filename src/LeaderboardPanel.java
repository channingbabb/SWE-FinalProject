import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class LeaderboardPanel extends JPanel {
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    
    public LeaderboardPanel() {
        setLayout(new BorderLayout());
        
        String[] columnNames = {"Rank", "Username", "Score"};
        tableModel = new DefaultTableModel(columnNames, 0);
        leaderboardTable = new JTable(tableModel);
        
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
                user.getBalance()
            });
        }
    }
    
    public JButton getBackButton() {
        return backButton;
    }
}
