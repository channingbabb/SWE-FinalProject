public class User {
    private String username;
    private int balance;
    
    public User(String username, int balance) {
        this.username = username;
        this.balance = balance;
    }
    
    public String getUsername() {
        return username;
    }
    
    public int getBalance() {
        return balance;
    }
    
    public void setBalance(int balance) {
        this.balance = balance;
    }
    
    public void updateBalance(int amount) {
        this.balance += amount;
    }
} 