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
import java.util.HashMap;
import java.util.Map;
import java.net.InetAddress;

public class ServerClass extends AbstractServer {
    private DatabaseClass database;
    private ArrayList<Game> activeGames;
    private ChatServer chatServer;
    private JTextArea logArea;
    private ArrayList<ConnectedClient> connectedClients = new ArrayList<>();
    private ArrayList<String> activeGameNames = new ArrayList<>();
    private HashMap<String, String> gameCreators = new HashMap<>();
    private HashMap<String, ArrayList<User>> gameWaitingRooms = new HashMap<>();
    private String serverName;
    
    public ServerClass(int port, String serverName) {
        super(port);
        this.serverName = serverName;
        activeGames = new ArrayList<>();
        chatServer = new ChatServer();
        
        try {
            database = new DatabaseClass();
        } catch (Exception e) {
            System.out.println("Warning: Database initialization failed. Running without database.");
            e.printStackTrace();
        }
    }
    
    public String getServerName() {
        return serverName;
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
                if (success) {
                    User user = database.getUser(data.getUsername());
                    client.sendToClient(new LoginData(success, user));
                    client.setInfo("username", user.getUsername());
                    updateClientInfo(client, user.getUsername(), user.getBalance());
                    updateClientActivity(client, "Online");
                    logToServer("User '" + data.getUsername() + "' logged in successfully");
                } else {
                    client.sendToClient(new LoginData(false, null));
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
                    
                    if (hasActiveGame(username)) {
                        try {
                            client.sendToClient("GAME_CREATED:false:You already have an active game");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    
                    if (activeGameNames.contains(gameName)) {
                        try {
                            client.sendToClient("GAME_CREATED:false:Game name already exists");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    
                    Game newGame = new Game();
                    newGame.setName(gameName);
                    activeGames.add(newGame);
                    activeGameNames.add(gameName);
                    addGameCreator(gameName, username);
                    
                    logToServer("New game '" + gameName + "' created by user '" + username + "'");
                    
                    try {
                        client.sendToClient("GAME_CREATED:true:Game created successfully");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                    broadcastGameList();
                }
            } else if (message.equals("REQUEST_LEADERBOARD")) {
                logToServer("Leaderboard requested by client " + client.getId());
                updateClientActivity(client, "Viewing Leaderboards");
                handleLeaderboardRequest(client);
            } else if (message.equals("REQUEST_GAMES")) {
                updateClientActivity(client, "Browsing games");
                sendGameList(client);
            } else if (message.startsWith("REQUEST_PLAYERS:")) {
                String gameName = message.split(":")[1];
                ArrayList<User> players = gameWaitingRooms.getOrDefault(gameName, new ArrayList<>());
                try {
                    String username = (String) client.getInfo("username");
                    client.sendToClient("WAITING_ROOM_PLAYERS:" + gameName + ":" + 
                        convertPlayersToString(players, username));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (message.startsWith("KICK_PLAYER:")) {
                String playerToKick = message.split(":")[1];
                handleKickPlayer(playerToKick, client);
            } else if (message.equals("LEAVE_GAME")) {
                handlePlayerLeave(client);
            } else if (message.startsWith("JOIN_GAME:")) {
                String gameName = message.split(":")[1];
                String username = (String) client.getInfo("username");
                updateClientActivity(client, "Joining game: " + gameName);
                
                if (username == null) {
                    try {
                        client.sendToClient("GAME_JOINED:false:Please log in first");
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                
                if (!activeGameNames.contains(gameName)) {
                    try {
                        client.sendToClient("GAME_JOINED:false:Game no longer exists");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                
                User user = database.getUser(username);
                if (user == null) {
                    try {
                        client.sendToClient("GAME_JOINED:false:User data not found");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                
                ArrayList<User> players = gameWaitingRooms.getOrDefault(gameName, new ArrayList<>());
                players.add(user);
                gameWaitingRooms.put(gameName, players);
                
                boolean isCreator = gameCreators.get(gameName).equals(username);
                try {
                    client.sendToClient("GAME_JOINED:true:Successfully joined game:" + gameName + ":" + isCreator);
                    broadcastWaitingRoomUpdate(gameName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (message.equals("START_GAME")) {
                String username = (String) client.getInfo("username");
                String gameName = getGameNameForPlayer(username);
                System.out.println("START_GAME received from: " + username + " for game: " + gameName);
                
                if (gameName != null && gameCreators.get(gameName).equals(username)) {
                    ArrayList<User> players = gameWaitingRooms.get(gameName);
                    System.out.println("Found " + players.size() + " players in waiting room");
                    
                    if (players != null && players.size() >= 2) {
                        Game game = new Game();
                        game.setName(gameName);
                        for (User player : players) {
                            game.addPlayer(player);
                            System.out.println("Added player to game: " + player.getUsername());
                        }
                        game.startGame();
                        activeGames.add(game);
                        
                        broadcastGameState(game);
                        
                        for (User player : players) {
                            sendGameStartedToPlayer(player.getUsername(), gameName, players);
                        }
                        
                        gameWaitingRooms.remove(gameName);
                    }
                } else {
                    System.out.println("User not authorized to start game or game not found");
                }
            } else if (message.startsWith("GAME_ACTION:")) {
                handleGameAction(message.substring("GAME_ACTION:".length()), client);
            }
        }
    }

    private void sendGameStartedToPlayer(String username, String gameName, ArrayList<User> players) {
        System.out.println("Sending game started notification to player: " + username);
        
        for (ConnectedClient connectedClient : connectedClients) {
            if (connectedClient.getUsername().equals(username)) {
                try {
                    String playerData = convertPlayersToString(players, username);
                    String currentPlayer = players.get(0).getUsername();
                    String gameStartedMessage = String.format("GAME_STARTED:%s:%s:%s", 
                        gameName, playerData, currentPlayer);
                    connectedClient.getClient().sendToClient(gameStartedMessage);
                    updateClientActivity(connectedClient.getClient(), "Playing in " + gameName);
                } catch (IOException e) {
                    System.err.println("Error sending game started message to " + username);
                    e.printStackTrace();
                }
                break;
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
            false,
            client
        );
        connectedClients.add(newClient);
        logToServer("Client " + client.getId() + " connected");
        try {
            client.sendToClient("SERVER_NAME:" + this.serverName);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void broadcastGameList() {
        String gameList = "GAME_LIST:" + String.join(",", activeGameNames);
        try {
            sendToAllClients(gameList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendGameList(ConnectionToClient client) {
        try {
            StringBuilder gameListData = new StringBuilder();
            for (String gameName : activeGameNames) {
                ArrayList<User> players = gameWaitingRooms.getOrDefault(gameName, new ArrayList<>());
                gameListData.append(gameName).append(":")
                           .append(convertPlayersToString(players, (String)client.getInfo("username")))
                           .append(";");
            }
            client.sendToClient("GAME_LIST:" + gameListData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasActiveGame(String username) {
        return gameCreators.containsValue(username);
    }

    private void addGameCreator(String gameName, String username) {
        gameCreators.put(gameName, username);
    }

    public void removeGameCreator(String gameName) {
        gameCreators.remove(gameName);
    }

    private void handleKickPlayer(String playerToKick, ConnectionToClient client) {
        String gameName = getGameNameForPlayer(playerToKick);
        if (gameName != null && gameCreators.get(gameName).equals(client.getInfo("username"))) {
            for (ConnectedClient connectedClient : connectedClients) {
                if (connectedClient.getUsername().equals(playerToKick)) {
                    try {
                        connectedClient.getClient().sendToClient("KICKED_FROM_GAME");
                        updateClientActivity(connectedClient.getClient(), "Online");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            removePlayerFromGame(playerToKick, gameName);
            broadcastWaitingRoomUpdate(gameName);
        }
    }

    private void handlePlayerLeave(ConnectionToClient client) {
        String username = (String) client.getInfo("username");
        String gameName = getGameNameForPlayer(username);
        if (gameName != null) {
            removePlayerFromGame(username, gameName);
            updateClientActivity(client, "Online");
            broadcastWaitingRoomUpdate(gameName);
        }
    }

    private void broadcastWaitingRoomUpdate(String gameName) {
        ArrayList<User> players = gameWaitingRooms.get(gameName);
        System.out.println("Broadcasting waiting room update for game: " + gameName);
        
        for (User player : players) {
            try {
                for (ConnectedClient connectedClient : connectedClients) {
                    if (connectedClient.getUsername().equals(player.getUsername())) {
                        String playersData = convertPlayersToString(players, player.getUsername());
                        connectedClient.getClient().sendToClient(
                            "WAITING_ROOM_PLAYERS:" + gameName + ":" + playersData);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String convertPlayersToString(List<User> players, String targetUsername) {
        StringBuilder sb = new StringBuilder();
        for (User player : players) {
            sb.append(player.getUsername()).append("|")
              .append(player.getBalance()).append("|")
              .append(player.getCurrentBet()).append("|")
              .append(player.isActive());
              
            if (player.getUsername().equals(targetUsername)) {
                List<CardClass> cards = player.getHand().getCards();
                sb.append("|").append(cards.size());
                for (CardClass card : cards) {
                    sb.append("|").append(card.getSuit())
                      .append("|").append(card.getRank());
                }
            } else {
                sb.append("|0");
            }
            sb.append(",");
        }
        return sb.toString();
    }

    private String getGameNameForPlayer(String username) {
        for (Map.Entry<String, ArrayList<User>> entry : gameWaitingRooms.entrySet()) {
            for (User player : entry.getValue()) {
                if (player.getUsername().equals(username)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private void removePlayerFromGame(String username, String gameName) {
        ArrayList<User> players = gameWaitingRooms.get(gameName);
        if (players != null) {
            players.removeIf(player -> player.getUsername().equals(username));
            
            if (players.isEmpty()) {
                gameWaitingRooms.remove(gameName);
                activeGameNames.remove(gameName);
                removeGameCreator(gameName);
                broadcastGameList();
            }
        }
    }

    private void updateClientActivity(ConnectionToClient client, String activity) {
        for (ConnectedClient connectedClient : connectedClients) {
            if (connectedClient.getClientId() == client.getId()) {
                connectedClient.setActivity(activity);
                break;
            }
        }
    }

    private void handleGameAction(String action, ConnectionToClient client) {
        try {
            System.out.println("Received game action: " + action + " from client: " + client.getId());
            String username = (String) client.getInfo("username");
            System.out.println("Username: " + username);
            Game game = findGameForPlayer(username);
            System.out.println("Found game: " + game);
            
            System.out.println("Full action string before split: " + action);
            String[] actionParts = action.split(":");
            System.out.println("Action parts length: " + actionParts.length);
            for(int i = 0; i < actionParts.length; i++) {
                System.out.println("actionParts[" + i + "]: " + actionParts[i]);
            }
            
            String actionType = actionParts[0];
            System.out.println("Action type: " + actionType);
            
            User player = game.findPlayer(username);
            System.out.println("Found player: " + player);
            System.out.println("Action type: " + actionType);
            
            if (player == null) {
                client.sendToClient("GAME_ERROR:Player not found in game");
                return;
            }
            
            switch (actionType.toUpperCase()) {
                case "CHECK":
                    game.handleCheck(player);
                    break;
                    
                case "FOLD":
                    game.handleFold(player);
                    break;
                    
                case "CALL":
                    game.handleCall(player);
                    break;
                    
                case "RAISE":
                    System.out.println("Received RAISE action from player: " + username);
                                        System.out.println("Received raise action from player: " + username + ", raise amount: " + actionParts[1]);

                    if (actionParts.length < 2) {
                        System.out.println("No raise amount specified");
                        client.sendToClient("GAME_ERROR:No raise amount specified");
                        return;
                    }
                    try {
                        int raiseAmount = Integer.parseInt(actionParts[1]);
                        System.out.println("Raise amount: " + raiseAmount);
                        System.out.println("Processing raise of " + raiseAmount + " from player " + username);
                        game.handleRaise(player, raiseAmount);
                        System.out.println("Raise processed successfully");
                    } catch (NumberFormatException e) {
                        client.sendToClient("GAME_ERROR:Invalid raise amount");
                        return;
                    }
                    break;
                    
                default:
                    client.sendToClient("GAME_ERROR:Invalid action: " + actionType);
                    return;
            }
            
            broadcastGameState(game);
            
        } catch (Exception e) {
            System.err.println("Error handling game action: " + e.getMessage());
            e.printStackTrace();
            try {
                client.sendToClient("GAME_ERROR:" + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void broadcastGameState(Game game) {
        Thread[] clientThreads = getClientConnections();
        for (Thread thread : clientThreads) {
            if (thread instanceof ConnectionToClient) {
                ConnectionToClient client = (ConnectionToClient) thread;
                try {
                    String playerSpecificState = createGameStateMessage(game, (String)client.getInfo("username"));
                    client.sendToClient("GAME_STATE:" + playerSpecificState);
                } catch (IOException e) {
                    System.err.println("Error sending game state to client: " + e.getMessage());
                }
            }
        }
    }

    private String createGameStateMessage(Game game) {
        return createGameStateMessage(game, null);
    }

    private String createGameStateMessage(Game game, String targetPlayer) {
        StringBuilder sb = new StringBuilder();
        sb.append(game.getName()).append(":");
        sb.append(game.getPot()).append(":");
        sb.append(game.getCurrentBet()).append(":");
        sb.append(game.getCurrentPlayerUsername()).append(":");
        
        ArrayList<CardClass> communityCards = game.getCommunityCards();
        sb.append(communityCards.size()).append(":");
        for (CardClass card : communityCards) {
            sb.append(card.getSuit()).append(",")
              .append(card.getRank()).append("|");
        }
        sb.append(":");
        
        ArrayList<User> players = game.getPlayers();
        for (User player : players) {
            sb.append(player.getUsername()).append(",")
              .append(player.getBalance()).append(",")
              .append(player.getCurrentBet()).append(",")
              .append(player.isActive());
            
            if (player.getUsername().equals(targetPlayer)) {
                ArrayList<CardClass> playerCards = player.getHand().getCards();
                sb.append(",").append(playerCards.size());
                for (CardClass card : playerCards) {
                    sb.append(",").append(card.getSuit())
                      .append(",").append(card.getRank());
                }
            } else {
                sb.append(",0"); 
            }
            sb.append("|");
        }
        
        return sb.toString();
    }

    private Game findGameForPlayer(String username) {
        for (Game game : activeGames) {
            for (User player : game.getPlayers()) {
                if (player.getUsername().equals(username)) {
                    return game;
                }
            }
        }
        return null;
    }
}
