import java.util.ArrayList;

public class Hand {
    private ArrayList<CardClass> cards;
    
    public Hand() {
        cards = new ArrayList<>();
    }
    
    public void addCard(CardClass card) {
        if (cards.size() < 2){
            cards.add(card);
        }
    }
    
    public ArrayList<CardClass> getCards() {
        return new ArrayList<>(cards);
    }
    
    public void clear() {
        cards.clear();
    }

    public void clearHand() {
        cards.clear();
    }
}
