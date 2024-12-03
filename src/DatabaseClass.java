package SWEFinalProject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseClass {
    private Connection conn; //connection object to interact with the database

    public DatabaseClass() {
        Properties props = new Properties(); // instantiate Properties object
        try (InputStream input = new FileInputStream("db.properties")) { // read from db.properties file
            props.load(input); // load properties from file
        } catch (IOException ex) { //print error if the file didnt open
            System.err.println("Error loading db.properties file."); 
            ex.printStackTrace();
            return; //exit the constructor if properties wont load
        }

        // define connection parameters
        String url = props.getProperty("url");
        String username = props.getProperty("user");
        String password = props.getProperty("password");

        try { //establish the connection to the database using the above parameters
            conn = DriverManager.getConnection(url, username, password); // establish connection
        } catch (SQLException e) {
            System.err.println("Error establishing database connection.");
            e.printStackTrace();
        }
    }

    // query method to execute select query and return results as a list of strings
    public ArrayList<String> query(String query) {
        ArrayList<String> results = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); // create Statement object
             ResultSet rs = stmt.executeQuery(query)) { // execute query and get result set

            if (!rs.isBeforeFirst()) { // check if ResultSet is empty
                return null; // return null if empty
            }

            ResultSetMetaData metaData = rs.getMetaData(); // get metadata from ResultSet
            int columnCount = metaData.getColumnCount();  // get column count

            // now iterate through the result set and build a string for each row
            // this will be used to build the arraylist of strings
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) row.append(", "); // add a comma and space if not the first column
                    row.append(rs.getString(i)); // append each column value to the row
                }
                results.add(row.toString()); //add the row to the results
            }
            return results; //return the populated list of query results
        } catch (SQLException e) {
            System.err.println("SQLException in query method.");
            e.printStackTrace();
            return null;
        }
    }

    //method to execute dml queries
    public void executeDML(String dml) throws SQLException {
        try (Statement stmt = conn.createStatement()) { //statement object is created for dml queries
            stmt.executeUpdate(dml); // execute dml
        }
    }

    //verify user account by checking username and encrypted password
    public boolean verifyAccount(String username, String password) {
    	//checking here if the username and password match in the database
        String query = "SELECT * FROM poker_users WHERE username = ? AND password = AES_ENCRYPT(?, 'ucabears!')";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) { //preparing query, setting username and password
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery(); //execute the query and get the result
            return rs.next(); //return true if its found
        } catch (SQLException e) {
            System.err.println("Error verifying account:");
            e.printStackTrace();
            return false;
        }
    }

    //create account check if the username already exists or not
    public boolean createNewAccount(String username, String password) {
        String checkQuery = "SELECT * FROM poker_users WHERE username = ?"; //preparing the select statement query
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery(); //execute the query and get the result
            if (rs.next()) {
                return false; // Username already exists so return false
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        //insert new user with given info
        String insertQuery = "INSERT INTO poker_users (username, password) VALUES (?, AES_ENCRYPT(?, 'ucabears!'))"; //insert query for the new user
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate(); //query execution 
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating account:");
            e.printStackTrace();
            return false;
        }
    }

    //get the user's data based on their username
    public User getUser(String username) {
        System.out.println("Attempting to get user data for: " + username);
        //gets user info stored in the database
        String query = "SELECT username, balance, wins, losses FROM poker_users WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery(); //get the result from the query
            
            //if its found, create new user obj with the result
            if (rs.next()) {
                User user = new User(
                    rs.getString("username"),
                    rs.getInt("balance")
                );
                System.out.println("Successfully retrieved user data: " + user.getUsername());
                return user;
            }
            System.out.println("No user found with username: " + username);
            return null; //didn't find the user, return null
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //return database connection
    public Connection getConnection() {
        return conn;
    }
}
