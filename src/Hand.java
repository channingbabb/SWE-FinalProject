import java.util.ArrayList;

public class Hand implements java.io.Serializable {
    private ArrayList<CardClass> cards;
    private ArrayList<CardClass> communityCards;
    
    public Hand() {
        cards = new ArrayList<>();
        communityCards = new ArrayList<>();
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
    
    public void clear() {
        cards.clear();
    }

    public void clearHand() {
        cards.clear();
    }

    public ArrayList<CardClass> getCommunityCards() {
        return new ArrayList<>(communityCards);
    }

    public void addCommunityCard(CardClass card) {
        communityCards.add(card);
    }

    public void clearCommunityCards() {
        communityCards.clear();
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
