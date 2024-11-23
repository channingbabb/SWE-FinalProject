import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseClass {
    private Connection conn;

    public DatabaseClass() {
        Properties props = new Properties(); // instantiate Properties object
        try (InputStream input = new FileInputStream("db.properties")) { // read from db.properties file
            props.load(input); // load properties from file
        } catch (IOException ex) {
            System.err.println("Error loading db.properties file.");
            ex.printStackTrace();
            return;
        }

        // define connection parameters
        String url = props.getProperty("url");
        String username = props.getProperty("user");
        String password = props.getProperty("password");

        try {
            conn = DriverManager.getConnection(url, username, password); // establish connection
        } catch (SQLException e) {
            System.err.println("Error establishing database connection.");
            e.printStackTrace();
        }
    }

    // query method
    public ArrayList<String> query(String query) {
        ArrayList<String> results = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); // create Statement object
             ResultSet rs = stmt.executeQuery(query)) { // execute query

            if (!rs.isBeforeFirst()) { // check if ResultSet is empty
                return null; // return null if empty
            }

            ResultSetMetaData metaData = rs.getMetaData(); // get metadata from ResultSet
            int columnCount = metaData.getColumnCount();  // get column count

            // now iterate through the resultset and build a string for each row
            // this will ibe used to build the arraylsit of strings
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) row.append(", "); // add a comma and space if not the first column
                    row.append(rs.getString(i)); // append each column value to the row
                }
                results.add(row.toString());
            }
            return results;
        } catch (SQLException e) {
            System.err.println("SQLException in query method.");
            e.printStackTrace();
            return null;
        }
    }

    public void executeDML(String dml) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(dml); // execute dml
        }
    }

    public boolean verifyAccount(String username, String password) {
        String query = "SELECT * FROM poker_users WHERE username = ? AND password = AES_ENCRYPT(?, 'ucabears!')";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error verifying account:");
            e.printStackTrace();
            return false;
        }
    }

    public boolean createNewAccount(String username, String password) {
        String checkQuery = "SELECT * FROM poker_users WHERE username = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // Username already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String insertQuery = "INSERT INTO poker_users (username, password) VALUES (?, AES_ENCRYPT(?, 'ucabears!'))";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating account:");
            e.printStackTrace();
            return false;
        }
    }

    public User getUser(String username) {
        System.out.println("Attempting to get user data for: " + username);
        String query = "SELECT username, balance, wins, losses FROM poker_users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User(
                    rs.getString("username"),
                    rs.getInt("balance")
                );
                System.out.println("Successfully retrieved user data: " + user.getUsername());
                return user;
            }
            System.out.println("No user found with username: " + username);
            return null;
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection() {
        return conn;
    }
}
