import ocsf.server.ConnectionToClient;

public class ConnectedClient {
    private long clientId;
    private String username;
    private int balance;
    private boolean authenticated;
    private ConnectionToClient client;

    public ConnectedClient(long clientId, String username, int balance, boolean authenticated) {
        this.clientId = clientId;
        this.username = username;
        this.balance = balance;
        this.authenticated = authenticated;
    }

    public ConnectedClient(long clientId, String username, int balance, boolean authenticated, ConnectionToClient client) {
        this.clientId = clientId;
        this.username = username;
        this.balance = balance;
        this.authenticated = authenticated;
        this.client = client;
    }

    public ConnectionToClient getClient() {
        return client;
    }

    public long getClientId() { return clientId; }
    public String getUsername() { return username; }
    public int getBalance() { return balance; }
    public boolean isAuthenticated() { return authenticated; }

    public void setUsername(String username) { this.username = username; }
    public void setBalance(int balance) { this.balance = balance; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
}