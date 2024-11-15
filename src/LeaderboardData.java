import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardData implements Serializable {
    private List<User> users;

    public LeaderboardData() {
    }
    
    public List<String> getTopUsers() {
        List<String> topUsers = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:your_database_url", "username", "password");
            Statement statement = connection.createStatement();
            String query = "SELECT username FROM users ORDER BY money DESC LIMIT 10";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                topUsers.add(resultSet.getString("username"));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topUsers;
    }

    public List<User> toUserList() {
        return new ArrayList<>(users);
    }
}
