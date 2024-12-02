import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    public void testCreateAccountData() {
        CreateAccountData accountData = new CreateAccountData("user123", "securePass");
        assertEquals("user123", accountData.getUsername(), "Username should match the provided value.");
        assertEquals("securePass", accountData.getPassword(), "Password should match the provided value.");

        accountData = new CreateAccountData(true);
        assertTrue(accountData.isSuccess(), "Success should be true when initialized with true.");
    }

    @Test
    public void testLoginData() {
        LoginData loginData = new LoginData("user123", "securePass");
        assertEquals("user123", loginData.getUsername(), "Username should match the provided value.");
        assertEquals("securePass", loginData.getPassword(), "Password should match the provided value.");

        loginData = new LoginData(true);
        assertTrue(loginData.isSuccess(), "Success should be true when initialized with true.");
    }

    @Test
    public void testUser() {
        User user = new User("player1", 1000);
        assertEquals("player1", user.getUsername(), "Username should match the provided value.");
        assertEquals(1000, user.getBalance(), "Initial balance should match the provided value.");

        user.updateBalance(500);
        assertEquals(1500, user.getBalance(), "Balance should be updated correctly.");

        user.placeBet(200);
        assertEquals(1300, user.getBalance(), "Balance should decrease after placing a bet.");
        assertEquals(200, user.getCurrentBet(), "Current bet should reflect the bet placed.");

        user.fold();
        assertFalse(user.isActive(), "User should be inactive after folding.");

        user.clearHand();
        assertTrue(user.getHand().getCards().isEmpty(), "Hand should be cleared after calling clearHand().");
    }

}
