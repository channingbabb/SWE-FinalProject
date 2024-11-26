import java.util.List;

public class Message {
    private String type;
    private List<User> players;
    private String kickedPlayer;
    private int pot;
    private String action;
    private String turnPlayer;
    private boolean success;
    private String errorMessage;
    private int amount;

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

    public int getPot() {
        return pot;
    }

    public void setPot(int pot) {
        this.pot = pot;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTurnPlayer() {
        return turnPlayer;
    }

    public void setTurnPlayer(String turnPlayer) {
        this.turnPlayer = turnPlayer;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
} 