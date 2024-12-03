import ocsf.client.AbstractClient;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;
import java.io.IOException;
import java.util.Arrays;

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
    private String serverName;
    private GamePanel gamePanel;

    public PlayerClient(String host, int port) throws IOException {
        super(host, port);
        this.players = new ArrayList<>();
        this.messageHandlers = new HashMap<>();
        this.serverName = "Unknown Server";
    }
    
    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof String) {
            String message = (String) msg;
            if (message.startsWith("SERVER_NAME:")) {
                this.serverName = message.substring("SERVER_NAME:".length());
                if (container != null && container.getTopLevelAncestor() instanceof ClientGUI) {
                    ClientGUI gui = (ClientGUI) container.getTopLevelAncestor();
                    SwingUtilities.invokeLater(() -> {
                        gui.updateServerInfo(this.serverName);
                    });
                }
            } else if (message.startsWith("GAME_LIST:")) {
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
                System.out.println("Received GAME_STARTED message: " + message);
                String[] parts = message.split(":");
                if (parts.length >= 4) {
                    String gameName = parts[1];
                    String playersData = parts[2];
                    String currentPlayer = parts[3];
                    System.out.println("Game: " + gameName + ", Players data: " + playersData + ", Current player turn: " + currentPlayer);
                    handleGameStarted(gameName, playersData, currentPlayer);
                }
            } else if (message.startsWith("GAME_STATE:")) {
                handleGameState(message);
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
    
    private List<User> parsePlayersData(String playersData) {
        List<User> players = new ArrayList<>();
        if (playersData != null && !playersData.isEmpty()) {
            String[] playerStrings = playersData.split(",");
            for (String playerString : playerStrings) {
                String[] playerInfo = playerString.split("\\|");
                if (playerInfo.length >= 5) {
                    String username = playerInfo[0];
                    int balance = Integer.parseInt(playerInfo[1]);
                    int currentBet = Integer.parseInt(playerInfo[2]);
                    boolean isActive = Boolean.parseBoolean(playerInfo[3]);
                    int numCards = Integer.parseInt(playerInfo[4]);
                    
                    User player = new User(username, balance);
                    player.setCurrentBet(currentBet);
                    player.setActive(isActive);
                    
                    for (int i = 0; i < numCards; i++) {
                        String suit = playerInfo[5 + (i * 2)];
                        int rank = Integer.parseInt(playerInfo[6 + (i * 2)]);
                        player.getHand().addCard(new CardClass(suit, rank));
                    }
                    players.add(player);
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
        try {
        	System.out.println("PlayerClient is sending game action now: " + message.getAction());
            String actionMessage = "GAME_ACTION:" + message.getAction();
            /*if (message.getAmount() > 0) {
                actionMessage += ":" + message.getAmount();
            }*/
            
            sendToServer(actionMessage);
            System.out.println("Send to server from client: " + actionMessage);
        } catch (IOException e) {
            System.err.println("Error sending game action: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setGamePanel(GamePanel panel) {
        this.gamePanel = panel;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void handleGameStarted(String gameName, String playersData, String currentPlayer) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating game panel and control");
                
                List<User> players = new ArrayList<>();
                String[] playerInfos = playersData.split(",");
                
                for (String playerInfo : playerInfos) {
                    if (!playerInfo.isEmpty()) {
                        String[] parts = playerInfo.split("\\|");
                        if (parts.length >= 2) {
                            String username = parts[0];
                            int balance = Integer.parseInt(parts[1]);
                            int currentBet = Integer.parseInt(parts[2]);
                            boolean isActive = Boolean.parseBoolean(parts[3]);
                            int numCards = Integer.parseInt(parts[4]);
                            
                            User player = new User(username, balance);
                            player.setCurrentBet(currentBet);
                            player.setActive(isActive);
                            
                            for (int i = 0; i < numCards; i++) {
                                String suit = parts[5 + (i * 2)];
                                int rank = Integer.parseInt(parts[6 + (i * 2)]);
                                player.getHand().addCard(new CardClass(suit, rank));
                            }
                            
                            players.add(player);
                        }
                    }
                }
                
                GamePanel gamePanel = new GamePanel(players, this);
                setGamePanel(gamePanel);
                GameControl gameControl = new GameControl(this, gameName);
                setGameControl(gameControl);
                
                if (container != null) {
                    container.add(gamePanel, "GamePanel");
                    CardLayout cardLayout = (CardLayout) container.getLayout();
                    cardLayout.show(container, "GamePanel");
                    gamePanel.updateTurnIndicator(currentPlayer);
                    sendToServer("REQUEST_GAME_STATE:" + gameName);
                }
            } catch (Exception e) {
                System.err.println("Error initializing game UI:");
                e.printStackTrace();
            }
        });
    }

    //handles the game state sent from server
    private void handleGameState(String message) {
        System.out.println("Received game state update: " + message);
        //need to split the state into the right format
        String[] parts = message.split(":");
        //extracting the game details 
        String gameName = parts[1]; //game name
        int pot = Integer.parseInt(parts[2]);//pot
        int currentBet = Integer.parseInt(parts[3]); //current bet
        String currentPlayer = parts[4]; //current player's turn
        
        //extracting player's data after ::
        String[] playerData = parts[7].split("\\|");
        ArrayList<User> players = new ArrayList<>();
        
        for (String playerInfo : playerData) {
            if (!playerInfo.isEmpty()) {
            	//splitting the player info
                String[] playerParts = playerInfo.split(",");
              
                //this will be the user name and balance
                User player = new User(playerParts[0], Integer.parseInt(playerParts[1]));
                //current bet
                player.setCurrentBet(Integer.parseInt(playerParts[2]));
                //active status
                player.setActive(Boolean.parseBoolean(playerParts[3]));
                
                //add cards to player
                int numCards = Integer.parseInt(playerParts[4]);
                
                for (int i = 0; i < numCards; i++) {
                    String suit = playerParts[5 + (i *2)];
                    int rank = Integer.parseInt(playerParts[6 + (i * 2)]);
                    //add card to player's hand
                    player.getHand().addCard(new CardClass(suit, rank));
                }
                //add player to the list
                players.add(player);
            } else {
            	System.err.println("Error parsing player data: " + playerInfo);
            }
        }
        //updating the gamePanel with the new game state
        if (gamePanel != null) {
            gamePanel.updatePlayers(players); //update the player list
            gamePanel.updatePot(pot); //update the pot
            gamePanel.updateTurnIndicator(currentPlayer);//update the turn indicator
        
    }
}
}
