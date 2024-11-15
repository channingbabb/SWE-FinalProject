import java.util.ArrayList;
import java.util.Collections;

public class DeckClass {
    private ArrayList<CardClass> cards;
    
    public DeckClass() {
        cards = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        
        for (String suit : suits) {
            for (int value = 2; value <= 14; value++) {
                cards.add(new CardClass(suit, value));
            }
        }
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
}
