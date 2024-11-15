import java.util.ArrayList;

public class Game {
    private ArrayList<String> players;
    private DeckClass deck;
    private ArrayList<Hand> playerHands;
    private ArrayList<CardClass> communityCards;
    private int pot;
    private int currentBet;
    private int currentPlayerIndex;
    private boolean gameInProgress;
    
    public Game() {
        players = new ArrayList<>();
        deck = new DeckClass();
        playerHands = new ArrayList<>();
        communityCards = new ArrayList<>();
        pot = 0;
        currentBet = 0;
        currentPlayerIndex = 0;
        gameInProgress = false;
    }
    
    public void addPlayer(String username) {
        if (!gameInProgress && players.size() < 6) {
            players.add(username);
            playerHands.add(new Hand());
        }
    }
    
    public void startGame() {
        if (players.size() >= 2) {
            gameInProgress = true;
            deck.shuffle();
            dealInitialCards();
        }
    }
    
    private void dealInitialCards() {
        for (Hand hand : playerHands) {
            hand.addCard(deck.drawCard());
            hand.addCard(deck.drawCard());
        }
    }
    
    public void dealCommunityCards(int count) {
        for (int i = 0; i < count; i++) {
            communityCards.add(deck.drawCard());
        }
    }

    public boolean placeBet(String username, int amount) {
        return false;
    }
    
    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Hand[] getPlayerHands() {
        return playerHands.toArray(new Hand[0]);
    }

    public void addCommunityCard(CardClass cardClass) {
    }
}
