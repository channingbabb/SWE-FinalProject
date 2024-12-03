package SWEFinalProject;

import java.util.List;
import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private List<User> players;
    private String kickedPlayer;
    private int pot;
    private String action;
    private String turnPlayer;
    private boolean success;
    private String errorMessage;
    private int amount;

    public Message(String type, int amount) {
    	this.amount = amount;
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
    @Override
    public String toString() {
        return "Message{action='" + action + "', amount=" + amount + "}";
    }
} 