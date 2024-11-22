public class CardClass implements java.io.Serializable {
    private String suit;
    private int rank;
    private static final String image_path = "";

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


    // need to replace with actual file path later
    public String getImage(){
        String rankString = rankToString(rank).toLowerCase().replace(" ", "_");
        String subString = suit.toLowerCase();
        return image_path + rankString + "_of_" + subString + ".png";
    }

    // this fixed names not displaying correctly
    public String toString() {
        return rankToString(rank) + " of " + suit;
    }

    private String rankToString(int rank){
        switch (rank){
            case 11: return "Jack";
            case 12: return "Queen";
            case 13: return "King";
            case 14: return "Ace";
            default: return String.valueOf(rank);
        }
    }
}
