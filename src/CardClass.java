public class CardClass {
    private String suit;
    private int value;
    
    public CardClass(String suit, int value) {
        this.suit = suit;
        this.value = value;
    }
    
    public String getSuit() {
        return suit;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        String valueString;
        switch (value) {
            case 11: valueString = "Jack"; break;
            case 12: valueString = "Queen"; break;
            case 13: valueString = "King"; break;
            case 14: valueString = "Ace"; break;
            default: valueString = String.valueOf(value);
        }
        return valueString + " of " + suit;
    }
}
