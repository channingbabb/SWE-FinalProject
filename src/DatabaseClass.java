import java.sql.*;

public class DatabaseClass {
    private Connection conn;
    
    public DatabaseClass() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/poker_db",
                "username",
                "password"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean verifyLogin(LoginData data) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ? AND password = ?"
            );
            stmt.setString(1, data.getUsername());
            stmt.setString(2, data.getPassword());
            
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean createAccount(CreateAccountData data) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (username, password, balance) VALUES (?, ?, 1000)"
            );
            stmt.setString(1, data.getUsername());
            stmt.setString(2, data.getPassword());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public User getUser(String username) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT username, balance FROM users WHERE username = ?"
            );
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                    rs.getInt("balance")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean updateBalance(String username, int newBalance) {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE users SET balance = ? WHERE username = ?"
            );
            stmt.setInt(1, newBalance);
            stmt.setString(2, username);
            
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
