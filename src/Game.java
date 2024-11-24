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
    private DeckClass deck;

    public Game() {
        players = new ArrayList<>();
        communityCards = new ArrayList<>();
        dealer = new Dealer();
        pot = 0;
        currentBet = 0;
        currentPlayerIndex = 0;
        phase = GamePhase.PRE_FLOP;
        gameInProgress = false;
        deck = new DeckClass();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPot() {
    	return pot;
    }
    
    public int getCurrentBet() {
    	return currentBet;
    }
    
    public ArrayList<CardClass> getCommunityCards() {
    	return communityCards;
    }
    
    public User getCurrentPlayer() {
    	return players.get(currentPlayerIndex);
    }
    
    private void nextPlayer() {
    	do {
    		currentPlayerIndex = currentPlayerIndex++ % players.size();
    	} while(!players.get(currentPlayerIndex).isActive());
    }
    
    public void startGame() {
    	if(players.size() < 2) {
    		//display error message
    		return;
    	}
    	
    	gameInProgress = true;
    	phase = GamePhase.PRE_FLOP;
    	deck.shuffle();
    	dealer.dealInitialCards(players);
    	changePhases();
    }
    
    public void handleCall(User player) {
    	//calculate the call amount 
    	int amount = currentBet - player.getCurrentBet();
    	
    	//checking if the player has enough money to call
    	if (player.getBalance() < amount) {
    		throw new IllegalArgumentException ("Not enough balance.");
    	}
    	
    	player.updateBalance(amount);
    	player.setCurrentBet(currentBet);
    	pot += amount; //update the pot
    	
    }
    
    public void handleFold(User player) {
    	//player is inactive and move to the next player
    	player.fold();
    	nextPlayer();
    }
    
    public void handleCheck(User player) {
    	if (player.getCurrentBet() < currentBet) {
    		throw new IllegalStateException("Not enough money to check.");
    	}
    	
    	nextPlayer();
    }
    
    public void changePhases() {
    	switch(phase) {
    	case PRE_FLOP:
    		phase = GamePhase.FLOP;
    		break;
    	case FLOP:
    		phase = GamePhase.TURN;
    		break;
    	case TURN:
    		phase = GamePhase.RIVER;
    		break;
    	case RIVER:
    		phase = GamePhase.SHOWDOWN;
    		break;
    	default:
    		phase = GamePhase.PRE_FLOP;
    	}
    }
    
    private void resetBets() {
    	//bet resets for the next phase 
    	currentBet = 0;
    	for (User player : players) {
    		player.resetCurrentBet();
    	}
    }
    public void endGame() {
    	gameInProgress = false;
    }

}


