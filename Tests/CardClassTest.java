// junit unit test
import static org.junit.Assert.*;
import org.junit.Test;

public class CardClassTest
{
    @Test
    public void testConstructorAndGetters() {
        // Test with valid inputs
        CardClass card = new CardClass("Hearts", 11);
        assertEquals("Hearts", card.getSuit());
        assertEquals(11, card.getRank());
    }

    @Test
    public void testDefaultConstructor() {
        // Test the default constructor
        CardClass card = new CardClass();
        assertNull(card.getSuit());
        assertEquals(0, card.getRank());
    }

    @Test
    public void testGetImagePath() {
        // Test image path generation
        CardClass card = new CardClass("Diamonds", 14);
        String expectedPath = "_ace_of_diamonds.png"; // image_path is empty in this test
        assertEquals(expectedPath, card.getImage());
    }

    @Test
    public void testRankToString() {
        // Test the rankToString functionality indirectly via getImage
        CardClass jackCard = new CardClass("Spades", 11);
        assertTrue(jackCard.getImage().contains("jack_of_spades"));

        CardClass queenCard = new CardClass("Clubs", 12);
        assertTrue(queenCard.getImage().contains("queen_of_clubs"));

        CardClass aceCard = new CardClass("Hearts", 14);
        assertTrue(aceCard.getImage().contains("ace_of_hearts"));

        CardClass numberCard = new CardClass("Hearts", 7);
        assertTrue(numberCard.getImage().contains("7_of_hearts"));
    }
}
