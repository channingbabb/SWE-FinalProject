import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ServerClass extends AbstractServer {
    private DatabaseClass database;
    private ArrayList<Game> activeGames;
    private ChatServer chatServer;
    private JTextArea logArea;
    private ArrayList<ConnectedClient> connectedClients = new ArrayList<>();
    
    public ServerClass(int port) {
        super(port);
        activeGames = new ArrayList<>();
        chatServer = new ChatServer();
        
        try {
            database = new DatabaseClass();
        } catch (Exception e) {
            System.out.println("Warning: Database initialization failed. Running without database.");
            e.printStackTrace();
        }
    }
    
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (database == null) {
            logToServer("Error: Database not available");
            try {
                client.sendToClient(new Error("Database not available"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        
        if (msg instanceof LoginData) {
            LoginData data = (LoginData)msg;
            boolean success = database.verifyAccount(data.getUsername(), data.getPassword());
            try {
                client.sendToClient(new LoginData(success));
                if (success) {
                    User user = database.getUser(data.getUsername());
                    updateClientInfo(client, user.getUsername(), user.getBalance());
                    logToServer("User '" + data.getUsername() + "' logged in successfully");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (msg instanceof CreateAccountData) {
            CreateAccountData data = (CreateAccountData)msg;
            boolean success = database.createNewAccount(data.getUsername(), data.getPassword());
            try {
                client.sendToClient(new CreateAccountData(success));
                if (success) {
                    logToServer("New account created: " + data.getUsername());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (msg instanceof String) {
            String message = (String)msg;
            if (message.startsWith("CREATE_GAME:")) {
                String[] parts = message.split(":");
                if (parts.length >= 3) {
                    String gameName = parts[1];
                    String username = parts[2];
                    logToServer("New game '" + gameName + "' created by user '" + username + "'");
                }
            } else if (message.equals("REQUEST_LEADERBOARD")) {
                logToServer("Leaderboard requested by client " + client.getId());
                handleLeaderboardRequest(client);
            }
        }
    }
    
    private void handleLeaderboardRequest(ConnectionToClient client) {
        try {
            LeaderboardData leaderboardData = new LeaderboardData();
            leaderboardData.fetchTopUsers(database);
            client.sendToClient(leaderboardData);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.sendToClient(new Error("Failed to fetch leaderboard data"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void logToServer(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = timestamp + " - " + message + "\n";
        SwingUtilities.invokeLater(() -> {
            if (logArea != null) {
                logArea.append(logMessage);
                // Auto-scroll to bottom
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        });
    }

    public void setLogArea(JTextArea logArea) {
        this.logArea = logArea;
    }

    public List<ConnectedClient> getConnectedClients() {
        return new ArrayList<>(connectedClients);
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        ConnectedClient newClient = new ConnectedClient(
            client.getId(), 
            null, 
            0, 
            false
        );
        connectedClients.add(newClient);
        logToServer("Client " + client.getId() + " connected");
    }

    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        connectedClients.removeIf(c -> c.getClientId() == client.getId());
        logToServer("Client " + client.getId() + " disconnected");
    }

    private void updateClientInfo(ConnectionToClient client, String username, int balance) {
        for (ConnectedClient c : connectedClients) {
            if (c.getClientId() == client.getId()) {
                c.setUsername(username);
                c.setBalance(balance);
                c.setAuthenticated(true);
                break;
            }
        }
    }
}
