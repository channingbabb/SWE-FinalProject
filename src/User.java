package SWEFinalProject;

public class User implements java.io.Serializable {
    private String username;
    private Hand hand;
    private int balance;
    private boolean isActive;
    private int currentBet;

    public User(String username, int startingBalance) {
        this.username = username;
        this.hand = new Hand();
        System.out.println("Created new hand for user: " + username);
        this.balance = startingBalance;
        this.isActive = true;
        this.currentBet = 0;
    }

    public String getUsername() {
        return username;
    }

    public Hand getHand() {
        return hand;
    }

    public int getBalance() {
        return balance;
    }

    public void updateBalance(int amount) {
        this.balance += amount;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getCurrentBet() {
        return currentBet;
    }
    
    public void setCurrentBet(int currentBet) {
    	this.currentBet = currentBet;
    }

    public void placeBet(int amount) {
        if (!isActive) {
            throw new IllegalStateException("Player is inactive and cannot bet.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Not enough balance to place the bet! Required: " + 
                amount + ", Available: " + balance);
        }
        System.out.println("Player " + username + " placing bet: " + amount + 
                          " (Current balance: " + balance + ", Current bet: " + currentBet + ")");
        balance -= amount;
        currentBet += amount;
        System.out.println("New balance: " + balance + ", New current bet: " + currentBet);
    }

    public void fold() {
        isActive = false;
    }

    public void resetCurrentBet() {
        currentBet = 0;
    }

    public void clearHand() {
        hand.clear();
    }

    @Override
    public String toString() {
        return username + " - Balance: $" + balance + ", Current Bet: $" + currentBet +
                ", Hand: " + hand.getCards() + ", Active: " + isActive;
    }
}