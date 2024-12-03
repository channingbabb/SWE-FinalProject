public class CardClass implements java.io.Serializable {
    private String suit;
    private int rank;
    private static final String IMAGE_PATH = "assets/cards/";

    // initialize card
    public CardClass(String suit, int rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public CardClass() {
    }

    public String getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    public String getImage() {
        String rankString = rankToString(rank).toLowerCase();
        String suitString = suit.toLowerCase();
        return IMAGE_PATH + rankString + "_of_" + suitString + ".png";
    }

    // convert ranks to string so we can use to get images
    private String rankToString(int rank) {
        switch (rank) {
            case 11: return "jack";
            case 12: return "queen";
            case 13: return "king";
            case 14: return "ace";
            default: return String.valueOf(rank);
        }
    }

    @Override
    public String toString() {
        return rankToString(rank) + " of " + suit;
    }
}
