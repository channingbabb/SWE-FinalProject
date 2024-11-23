import java.util.List;

public class Message {
    private String type;
    private List<User> players;
    private String kickedPlayer;

    public Message(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public String getKickedPlayer() {
        return kickedPlayer;
    }

    public void setKickedPlayer(String kickedPlayer) {
        this.kickedPlayer = kickedPlayer;
    }
} 