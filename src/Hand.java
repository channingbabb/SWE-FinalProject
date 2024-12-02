import java.util.ArrayList;

public class Hand implements java.io.Serializable {
    private ArrayList<CardClass> cards;
    
    public Hand() {
        cards = new ArrayList<>();
    }
    
    public void addCard(CardClass card) {
        System.out.println("Adding card to hand: " + card.toString());
        if (cards.size() < 2){
            cards.add(card);
            System.out.println("Hand now contains " + cards.size() + " cards");
        }
    }
    
    public ArrayList<CardClass> getCards() {
        return new ArrayList<>(cards);
    }
    
    public void setCards(ArrayList<CardClass> cards) {
    	this.cards = cards;
    }
    
    public void clear() {
        cards.clear();
    }

    public void clearHand() {
        cards.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CardClass card : cards) {
            sb.append(card.toString()).append(" ");
        }
        return sb.toString().trim();
    }
}
