import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardData implements Serializable {
    private List<User> users;
    
    public LeaderboardData() {
        this.users = new ArrayList<>();
    }
    
    public void fetchTopUsers(DatabaseClass database) {
        try {
            Connection conn = database.getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT username, balance FROM poker_users ORDER BY balance DESC";
            System.out.println("Executing query: " + query);
            ResultSet rs = stmt.executeQuery(query);
            
            users.clear();
            int count = 0;
            while (rs.next()) {
                String username = rs.getString("username");
                int balance = rs.getInt("balance");
                users.add(new User(username, balance));
                count++;
                System.out.println("Found user: " + username + " with balance: " + balance);
            }
            System.out.println("Total users found: " + count);
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println("Error in fetchTopUsers:");
            e.printStackTrace();
        }
    }
    
    public List<User> toUserList() {
        return new ArrayList<>(users);
    }
}
