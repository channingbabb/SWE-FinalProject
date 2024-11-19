public class User implements java.io.Serializable {
    private String username;          // Player's name
    private Hand hand;                // Player's hand (two cards)
    private int balance;              // Player's total money (balance)
    private boolean isActive;         // Whether the player is still in the round
    private int currentBet;           // Current bet amount for the ongoing round

    // Constructor: Initializes a player with a name and starting balance
    public User(String username, int startingBalance) {
        this.username = username;
        this.hand = new Hand();
        this.balance = startingBalance;
        this.isActive = true;
        this.currentBet = 0;
    }

    // Get the player's username
    public String getUsername() {
        return username;
    }

    // Get the player's hand
    public Hand getHand() {
        return hand;
    }

    // Get the player's balance
    public int getBalance() {
        return balance;
    }

    // Update the player's balance (add or subtract money)
    public void updateBalance(int amount) {
        this.balance += amount;
    }

    // Set the player's balance
    public void setBalance(int balance) {
        this.balance = balance;
    }

    // Check if the player is active
    public boolean isActive() {
        return isActive;
    }

    // Set the player's active status
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    // Get the player's current bet
    public int getCurrentBet() {
        return currentBet;
    }

    // Place a bet
    public void placeBet(int amount) {
        if (!isActive) {
            throw new IllegalStateException("Player is inactive and cannot bet.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Not enough balance to place the bet!");
        }
        balance -= amount;
        currentBet += amount;
    }

    // Fold the player (remove them from the round)
    public void fold() {
        isActive = false;
    }

    // Reset the current bet for the next round
    public void resetCurrentBet() {
        currentBet = 0;
    }

    // Clear the player's hand (for a new round)
    public void clearHand() {
        hand.clear();
    }

    // String representation of the player
    @Override
    public String toString() {
        return username + " - Balance: $" + balance + ", Current Bet: $" + currentBet +
                ", Hand: " + hand.getCards() + ", Active: " + isActive;
    }
}