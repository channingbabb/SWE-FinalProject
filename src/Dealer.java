import java.util.ArrayList;

public class Dealer {
    private DeckClass deck;

    // set up dealer so that it shuffles the cards
    public Dealer() {
        deck = new DeckClass();
        deck.shuffle();
        System.out.println("Dealer: New deck created and shuffled");
    }

    // dealing 2 cards to each player when the game starts
    public void dealInitialCards(ArrayList<User> players) {
        System.out.println("\nDealer: Starting to deal initial cards to " + players.size() + " players");
        for (int i = 0; i < 2; i++) {
            System.out.println("Dealer: Dealing round " + (i + 1));
            for (User player : players) {
                if (player.isActive() && player.getBalance() > 0) {
                    CardClass card = deck.drawCard();
                    player.getHand().addCard(card);
                    System.out.println("Dealer: Dealt " + card.toString() + " to player " + player.getUsername());
                }
            }
        }
        System.out.println("Dealer: Finished dealing initial cards\n");
    }

    // deals the flop making sure to burn a card
    public void dealFlop(ArrayList<CardClass> communityCards) {
        System.out.println("\nDealer: Dealing the flop");
        CardClass burnCard = deck.drawCard();
        System.out.println("Dealer: Burned card: " + burnCard.toString());
        
        for (int i = 0; i < 3; i++) {
            CardClass card = deck.drawCard();
            communityCards.add(card);
            System.out.println("Dealer: Dealt flop card " + (i + 1) + ": " + card.toString());
        }
        System.out.println("Dealer: Flop dealing complete\n");
    }

    // deal card after betting round making sure to burn a card
    public void dealTurn(ArrayList<CardClass> communityCards) {
        System.out.println("\nDealer: Dealing the turn");
        CardClass burnCard = deck.drawCard();
        System.out.println("Dealer: Burned card: " + burnCard.toString());
        
        CardClass card = deck.drawCard();
        communityCards.add(card);
        System.out.println("Dealer: Dealt turn card: " + card.toString() + "\n");
    }

    // deal final card
    public void dealRiver(ArrayList<CardClass> communityCards) {
        System.out.println("\nDealer: Dealing the river");
        CardClass burnCard = deck.drawCard();
        System.out.println("Dealer: Burned card: " + burnCard.toString());
        
        CardClass card = deck.drawCard();
        communityCards.add(card);
        System.out.println("Dealer: Dealt river card: " + card.toString() + "\n");
    }

    public void resetDeck() {
        deck.reset();
        deck.shuffle();
        System.out.println("Dealer: Deck has been reset and shuffled");
    }
}
