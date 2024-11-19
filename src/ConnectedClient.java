public class ConnectedClient {
    private long clientId;
    private String username;
    private int balance;
    private boolean authenticated;

    public ConnectedClient(long clientId, String username, int balance, boolean authenticated) {
        this.clientId = clientId;
        this.username = username;
        this.balance = balance;
        this.authenticated = authenticated;
    }

    // Getters
    public long getClientId() { return clientId; }
    public String getUsername() { return username; }
    public int getBalance() { return balance; }
    public boolean isAuthenticated() { return authenticated; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setBalance(int balance) { this.balance = balance; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
}