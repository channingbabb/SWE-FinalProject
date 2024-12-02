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
    private DeckClass deck;
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
    		System.out.println("Cannot start game: Not enough players");
    		return;
    	}
    	
    	System.out.println("Starting game with " + players.size() + " players");
    	gameInProgress = true;
    	phase = GamePhase.PRE_FLOP;
    	deck.shuffle();
    	
    	for (User player : players) {
    		player.clearHand();
    		System.out.println("Cleared hand for player: " + player.getUsername());
    	}
    	
    	dealer.dealInitialCards(players);
    	System.out.println("Initial cards dealt to all players");
    	
    	for (User player : players) {
    		System.out.println("Player " + player.getUsername() + " cards: " + player.getHand().toString());
    	}
    	
    	changePhases();
    	System.out.println("Game phase changed to: " + phase);
    }
    
    public void handleCall(User player) {
    	if (player.getUsername().equals(getCurrentPlayerUsername())) {
    		int callAmount = currentBet - player.getCurrentBet();
    		if (callAmount > player.getBalance()) {
    			throw new IllegalStateException("Insufficient funds");
    		}
    		player.updateBalance(-callAmount);
    		player.setCurrentBet(currentBet);
    		pot += callAmount;
    		nextPlayer();
    	} else {
    		throw new IllegalStateException("Not this player's turn");
    	}
    }
    
    public void handleFold(User player) {
    	if (player.getUsername().equals(getCurrentPlayerUsername())) {
    		player.setActive(false);
    		nextPlayer();
    	} else {
    		throw new IllegalStateException("Not this player's turn");
    	}
    }
    
    public void handleCheck(User player) {
    	if (player.getUsername().equals(getCurrentPlayerUsername())) {
    		nextPlayer();
    	} else {
    		throw new IllegalStateException("Not this player's turn");
    	}
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

    public void addPlayer(User player) {
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(player);
        player.setActive(true);
    }

    public void handleRaise(User player, int raiseAmount) {
        if (player.getUsername().equals(getCurrentPlayerUsername())) {
            if (raiseAmount > player.getBalance()) {
                throw new IllegalStateException("Insufficient funds");
            }
			player.updateBalance(-raiseAmount);
			player.setCurrentBet(currentBet + raiseAmount);
			pot += raiseAmount;
			nextPlayer();
        } else {
            throw new IllegalStateException("Not this player's turn");
        }
    }

    public User findPlayer(String username) {
        for (User player : players) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    public ArrayList<User> getPlayers() {
        return new ArrayList<>(players);
    }

    public String getCurrentPlayerUsername() {
        return players.get(currentPlayerIndex).getUsername();
    }

}


