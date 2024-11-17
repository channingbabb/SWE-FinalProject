
public class CardClass {
    private String suit;
    private int rank;
    private static final String image_path = ""; // replace with imae path later

    public CardClass(String suit, int rank) {
        this.suit = suit;
        this.rank = rank;
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
