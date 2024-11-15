import java.util.ArrayList;

public class Hand {
    private ArrayList<CardClass> cards;
    
    public Hand() {
        cards = new ArrayList<>();
    }
    
    public void addCard(CardClass card) {
        cards.add(card);
    }
    
    public ArrayList<CardClass> getCards() {
        return cards;
    }
    
    public void clear() {
        cards.clear();
    }
}
