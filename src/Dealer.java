import java.util.ArrayList;

public class Dealer {
    private DeckClass deck;
    
    public Dealer() {
        deck = new DeckClass();
        deck.shuffle();
    }

    public void dealInitialCards(ArrayList<User> players) {
        for (int i = 0; i < 2; i++) {
            for (User player : players) {
                if (player.isActive() && player.getBalance() > 0) {
                    player.getHand().addCard(deck.drawCard());
                }
            }
        }
    }

    
    public void dealFlop(ArrayList<CardClass> communityCards) {
        // Burn one card and deal three community cards
        deck.drawCard(); // burn
        for (int i = 0; i < 3; i++) {
            communityCards.add(deck.drawCard());
        }
    }
    
    public void dealTurn(ArrayList<CardClass> communityCards) {
        // Burn one card and deal one community card
        deck.drawCard(); // burn
        communityCards.add(deck.drawCard());
    }

    public void dealRiver(ArrayList<CardClass> communityCards) {
        // Burn one card and deal one community card
        deck.drawCard(); // burn
        communityCards.add(deck.drawCard());
    }

    public void resetDeck(){
        deck.reset();
    }
}
