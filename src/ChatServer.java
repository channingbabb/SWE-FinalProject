package SWEFinalProject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatServer {
    private HashMap<String, ArrayList<String>> gameChats;
    
    public ChatServer() {
        gameChats = new HashMap<>();
    }
    
    public void createGameChat(String gameId) {
        gameChats.put(gameId, new ArrayList<>());
    }
    
    public void addMessage(String gameId, String username, String message) {
        if (gameChats.containsKey(gameId)) {
            gameChats.get(gameId).add(username + ": " + message);
        }
    }
    
    public ArrayList<String> getGameMessages(String gameId) {
        return gameChats.getOrDefault(gameId, new ArrayList<>());
    }
    
    public void removeGameChat(String gameId) {
        gameChats.remove(gameId);
    }
}
