import java.util.ArrayList;
public class Game {
    private ArrayList<User> players; // all players in the game
    private ArrayList<CardClass> communityCards;
    private Dealer dealer;
    private int pot;
    private int currentBet;
    private int currentPlayerIndex;
    private GamePhase phase; // which phase of the game we're in
    private boolean gameInProgress;
    private String name;

    public Game() {
        players = new ArrayList<>();
        communityCards = new ArrayList<>();
        dealer = new Dealer();
        pot = 0;
        currentBet = 0;
        currentPlayerIndex = 0;
        phase = GamePhase.PRE_FLOP;
        gameInProgress = false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // testing purposes -------------------------
    // Getter for pot
    public int getPot() {
        return pot;
    }
    // Setter for pot
    public void setPot(int pot) {
        this.pot = pot;
    }
    // Getter for currentBet
    public int getCurrentBet() {
        return currentBet;
    }
    // Setter for currentBet
    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }

}


