public class Dealer {
    private DeckClass deck;
    private Game currentGame;
    
    public Dealer() {
        deck = new DeckClass();
    }
    
    public void setGame(Game game) {
        this.currentGame = game;
    }
    
    public void dealInitialCards() {
        deck.shuffle();
        // Deal two cards to each player
        for (int i = 0; i < 2; i++) {
            for (Hand playerHand : currentGame.getPlayerHands()) {
                playerHand.addCard(deck.drawCard());
            }
        }
    }
    
    public void dealFlop() {
        // Burn one card and deal three community cards
        deck.drawCard(); // burn
        for (int i = 0; i < 3; i++) {
            currentGame.addCommunityCard(deck.drawCard());
        }
    }
    
    public void dealTurnOrRiver() {
        // Burn one card and deal one community card
        deck.drawCard(); // burn
        currentGame.addCommunityCard(deck.drawCard());
    }
}
