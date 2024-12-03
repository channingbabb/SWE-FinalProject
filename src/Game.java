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
    
    // initializes a new game
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
    		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    	} while(!players.get(currentPlayerIndex).isActive());
    }

    // start game if there are at least 2 players
    public void startGame() {
    	if(players.size() < 2) {
    		System.out.println("Cannot start game: Not enough players");
    		return;
    	}
    	
    	System.out.println("Starting game with " + players.size() + " players");
    	gameInProgress = true;
    	phase = GamePhase.PRE_FLOP;
    	deck.shuffle(); // make sure we shuffle the deck
    	currentPlayerIndex = 0;
    	pot = 0;

        // get buy in from each player
    	for (User player : players) {
    		player.clearHand();
    		System.out.println("Cleared hand for player: " + player.getUsername());
    		try {
    			player.updateBalance(-50);
    			player.setCurrentBet(50);
    			pot += 50;
    			System.out.println("Collected $50 from " + player.getUsername() + ". Current pot: $" + pot);
    		} catch (IllegalArgumentException e) {
    			System.err.println("Player " + player.getUsername() + " doesn't have enough funds for initial bet");
    			player.setActive(false);
    		}
    	}

        // deal two cards to each player
    	dealer.dealInitialCards(players);
    	System.out.println("Initial cards dealt to all players");
    	
    	for (User player : players) {
    		System.out.println("Player " + player.getUsername() + " cards: " + player.getHand().toString());
    	}
    	
    	System.out.println("First player to act: " + getCurrentPlayerUsername());
    	advancePhase();
    	System.out.println("After changePhases() have been called -> phase: " + phase);
    }

    // handles logic whenever a player calls
    public void handleCall(User player) {
        if (!player.getUsername().equals(getCurrentPlayerUsername())) {
            throw new IllegalStateException("Not this player's turn");
        }
        
        int callAmount = currentBet - player.getCurrentBet();
        if (callAmount > player.getBalance()) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        player.placeBet(callAmount);
        pot += callAmount;
        
        nextPlayer();
        checkPhaseCompletion();
    }

    // logic for when player folds
    public void handleFold(User player) {
    	if (player.getUsername().equals(getCurrentPlayerUsername())) {
    		player.setActive(false);
    		nextPlayer();
    	} else {
    		throw new IllegalStateException("Not this player's turn");
    	}
    }

    // logic for when players chekcs
    public void handleCheck(User player) {
        if (!player.getUsername().equals(getCurrentPlayerUsername())) {
            throw new IllegalStateException("Not this player's turn");
        }
        
        if (currentBet > player.getCurrentBet()) {
            throw new IllegalStateException("Cannot check when there's a bet to call");
        }
        
        nextPlayer();
        checkPhaseCompletion();
    }
    
    public void changePhases() {
    	switch(phase) {
    	case PRE_FLOP:
    		phase = GamePhase.FLOP;
    		System.out.println("Game phase switched to " + phase);
    		break;
    	case FLOP:
    		phase = GamePhase.TURN;
    		System.out.println("Game phase switched to " + phase);
    		break;
    	case TURN:
    		phase = GamePhase.RIVER;
    		System.out.println("Game phase switched to " + phase);
    		break;
    	case RIVER:
    		phase = GamePhase.SHOWDOWN;
    		System.out.println("Game phase switched to " + phase);
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

    // logic for when a player raises
    public void handleRaise(User player, int raiseAmount) {
        System.out.println("DEBUG: Starting handleRaise - Player: " + player.getUsername() + 
                          ", Amount: " + raiseAmount);
        System.out.println("DEBUG: Current player turn: " + getCurrentPlayerUsername());
        System.out.println("DEBUG: Current pot: " + pot + ", Current bet: " + currentBet);
        
        if (!player.getUsername().equals(getCurrentPlayerUsername())) {
            System.out.println("DEBUG: Not player's turn! Current turn: " + getCurrentPlayerUsername());
            throw new IllegalStateException("Not this player's turn");
        }
        
        int callAmount = currentBet - player.getCurrentBet();
        int totalAmount = callAmount + raiseAmount;
        
        System.out.println("DEBUG: Call amount: " + callAmount + ", Total amount: " + totalAmount);
        System.out.println("DEBUG: Player balance: " + player.getBalance());
        
        if (totalAmount > player.getBalance()) {
            System.out.println("DEBUG: Insufficient funds! Required: " + totalAmount);
            throw new IllegalStateException("Insufficient funds");
        }
        
        player.updateBalance(-totalAmount);
        player.setCurrentBet(player.getCurrentBet() + totalAmount);
        currentBet = player.getCurrentBet();
        pot += totalAmount;
        
        System.out.println("DEBUG: After raise - New pot: " + pot + ", New current bet: " + currentBet);
        System.out.println("DEBUG: Player " + player.getUsername() + " new balance: " + player.getBalance());
        
        nextPlayer();
        System.out.println("DEBUG: Next player: " + getCurrentPlayerUsername());
        
        checkPhaseCompletion();
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
        if (players == null || players.isEmpty()) {
            return "";
        }
        return players.get(currentPlayerIndex).getUsername();
    }

    private void checkPhaseCompletion() {
        boolean allPlayersActed = true;
        int activePlayers = 0;
        
        for (User player : players) {
            if (player.isActive()) {
                activePlayers++;
                if (player.getCurrentBet() != currentBet) {
                    allPlayersActed = false;
                    break;
                }
            }
        }
        
        if (allPlayersActed || activePlayers == 1) {
            if (activePlayers == 1) {
                handleWinner();
            } else {
                advancePhase();
            }
        }
    }

    private void advancePhase() {
        System.out.println("\n--- Advancing Game Phase ---");
        System.out.println("Current phase: " + phase);
        
        switch(phase) {
            case PRE_FLOP:
                phase = GamePhase.FLOP;
                System.out.println("Dealing flop...");
                dealer.dealFlop(communityCards);

                System.out.println("advancePhase() dealer dealt FLOP");

                System.out.println("Community cards after flop: " + communityCards.size());
                System.out.println("DEBUG: Community cards after dealing: " + communityCards.size());
                for (CardClass card : communityCards) {
                    System.out.println("DEBUG: Dealt card: " + card.toString());
                }

                break;
            case FLOP:
                phase = GamePhase.TURN;
                System.out.println("Dealing turn...");
                dealer.dealTurn(communityCards);
                System.out.println("Community cards after turn: " + communityCards.size());
                System.out.println("DEBUG: Community cards after dealing: " + communityCards.size());
                for (CardClass card : communityCards) {
                    System.out.println("DEBUG: Dealt card: " + card.toString());
                }
                break;
            case TURN:
                phase = GamePhase.RIVER;
                System.out.println("Dealing river...");
                dealer.dealRiver(communityCards);
                System.out.println("Community cards after river: " + communityCards.size());
                System.out.println("DEBUG: Community cards after dealing: " + communityCards.size());
                for (CardClass card : communityCards) {
                    System.out.println("DEBUG: Dealt card: " + card.toString());
                }
                break;
            case RIVER:
                phase = GamePhase.SHOWDOWN;
                System.out.println("Moving to showdown...");
                handleShowdown();
                break;
            default:
                break;
        }
        resetBets();
        currentPlayerIndex = 0;
    }

    // logic to handle winner
    private void handleWinner() {
        User winner = null;
        for (User player : players) {
            if (player.isActive()) {
                winner = player;
                break;
            }
        }
        
        if (winner != null) {
            winner.updateBalance(pot);
            pot = 0;
        }
        
        endGame();
    }

    private void handleShowdown() {
        User winner = null;
        int bestHandRank = -1;
        
        ArrayList<CardClass> tableCards = getCommunityCards();
        
        for (User player : players) {
            if (player.isActive()) {
                ArrayList<CardClass> playerCards = player.getHand().getCards();
                ArrayList<CardClass> allCards = new ArrayList<>(tableCards);
                allCards.addAll(playerCards);
                
                int handRank = evaluateHand(allCards);
                
                if (handRank > bestHandRank) {
                    bestHandRank = handRank;
                    winner = player;
                }
            }
        }
        
        if (winner != null) {
            winner.updateBalance(pot);
            System.out.println("Player " + winner.getUsername() + " wins pot of " + pot);
            pot = 0;
        }
        
        endGame();
    }

    // compare player hands
    private int evaluateHand(ArrayList<CardClass> cards) {
        int highestRank = 0;
        for (CardClass card : cards) {
            if (card.getRank() > highestRank) {
                highestRank = card.getRank();
            }
        }
        return highestRank;
    }

    private void updatePlayerCommunityCards() {
        System.out.println("Updating community cards for all players");
        for (User player : players) {
            if (player.isActive()) {
                player.getHand().clearCommunityCards();
                for (CardClass card : communityCards) {
                    player.getHand().addCommunityCard(card);
                    System.out.println("Added community card to " + player.getUsername() + ": " + card.toString());
                }
            }
        }
    }

}


