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


}


