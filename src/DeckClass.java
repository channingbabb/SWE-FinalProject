import java.util.ArrayList;
import java.util.Collections;

public class DeckClass {
    private ArrayList<CardClass> cards;
    
    public DeckClass() {
        cards = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        
        for (String suit : suits) {
            for (int rank = 2; rank <= 14; rank++) {
                cards.add(new CardClass(suit,rank));
            }
        }
    }

    public ArrayList<CardClass> getCards() {
        return new ArrayList<>(cards);
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    public CardClass drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public void reset() {
        cards.clear();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        for (String suit : suits) {
            for (int rank = 2; rank <= 14; rank++) {
                cards.add(new CardClass(suit, rank));
            }
        }
        shuffle();
    }


}
