import ocsf.client.AbstractClient;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;

public class PlayerClient extends AbstractClient {
    private LoginControl loginControl;
    private CreateAccountControl createAccountControl;
    private GameControl gameControl;
    private LobbyControl lobbyControl;
    private User currentUser;
    private JPanel container;
    private List<User> players;
    private String username;
    private Map<String, Consumer<Message>> messageHandlers;

    public PlayerClient(String host, int port) {
        super(host, port);
        this.players = new ArrayList<>();
        this.messageHandlers = new HashMap<>();
    }
    
    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof String) {
            String message = (String) msg;
            if (message.startsWith("GAME_LIST:")) {
                String[] games = message.substring(10).split(",");
                if (message.substring(10).isEmpty()) {
                    games = new String[0];
                }
                lobbyControl.updateGames(games);
            } else if (message.startsWith("GAME_CREATED:")) {
                String[] parts = message.split(":");
                if (parts.length >= 3) {
                    boolean success = Boolean.parseBoolean(parts[1]);
                    String resultMessage = parts[2];
                    lobbyControl.handleGameCreated(success, resultMessage);
                }
            } else if (message.startsWith("WAITING_ROOM_PLAYERS:")) {
                String[] parts = message.split(":");
                String gameName = parts[1];
                List<User> players = parsePlayersData(parts[2]);
                lobbyControl.updateWaitingRoomPlayers(players);
            } else if (message.equals("KICKED_FROM_GAME")) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                        "You have been kicked from the game",
                        "Kicked",
                        JOptionPane.WARNING_MESSAGE);
                    CardLayout cardLayout = (CardLayout) container.getLayout();
                    cardLayout.show(container, "LobbyPanel");
                });
            } else if (message.startsWith("GAME_JOINED:")) {
                String[] parts = message.split(":");
                boolean success = Boolean.parseBoolean(parts[1]);
                String resultMessage = parts[2];
                if (parts.length >= 5) {
                    String gameName = parts[3];
                    boolean isCreator = Boolean.parseBoolean(parts[4]);
                    lobbyControl.handleGameJoined(success, resultMessage, gameName, isCreator);
                } else {
                    lobbyControl.handleGameJoined(success, resultMessage, null, false);
                }
            } else if (message.startsWith("GAME_STARTED:")) {
                String gameName = message.split(":")[1];
                SwingUtilities.invokeLater(() -> {
                    GamePanel gamePanel = new GamePanel(players);
                    GameControl gameControl = new GameControl(this, "hearts", 1);
                    setGameControl(gameControl);
                    
                    if (container != null) {
                        container.add(gamePanel, "GamePanel");
                        CardLayout cardLayout = (CardLayout) container.getLayout();
                        cardLayout.show(container, "GamePanel");
                    } else {
                        System.err.println("Error: Container is null when trying to start game");
                    }
                });
            }
        } else if (msg instanceof LoginData) {
            loginControl.handleLoginResult((LoginData)msg);
        } else if (msg instanceof CreateAccountData) {
            createAccountControl.handleCreateAccountResult((CreateAccountData)msg);
        } else if (msg instanceof LeaderboardData) {
            lobbyControl.updateLeaderboard((LeaderboardData)msg);
        }
    }
    
    public void setLoginControl(LoginControl loginControl) {
        this.loginControl = loginControl;
    }
    
    public void setCreateAccountControl(CreateAccountControl createAccountControl) {
        this.createAccountControl = createAccountControl;
    }
    
    public void setGameControl(GameControl gameControl) {
        this.gameControl = gameControl;
    }
    
    public void setLobbyControl(LobbyControl lobbyControl) {
        this.lobbyControl = lobbyControl;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setContainer(JPanel container) {
        this.container = container;
    }
    
    private List<User> parsePlayersData(String playersString) {
        List<User> players = new ArrayList<>();
        System.out.println("Parsing players data: " + playersString);
        
        if (!playersString.isEmpty()) {
            String[] playerData = playersString.split(",");
            for (String playerInfo : playerData) {
                String[] info = playerInfo.split("\\|");
                if (info.length >= 2) {
                    try {
                        String username = info[0];
                        int balance = Integer.parseInt(info[1]);
                        User user = new User(username, balance);
                        players.add(user);
                        System.out.println("Added player: " + username + " with balance: " + balance);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing balance for player: " + playerInfo);
                    }
                }
            }
        }
        return players;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public String getUsername() {
        return username;
    }

    public void addMessageHandler(String type, Consumer<Message> handler) {
        messageHandlers.put(type, handler);
    }

    public void sendMessage(Message message) {
    }
}
