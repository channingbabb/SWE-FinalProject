import ocsf.server.ConnectionToClient;

public class ConnectedClient {
    private long clientId;
    private String username;
    private int balance;
    private boolean authenticated;
    private String activity;
    private ConnectionToClient client;

    public ConnectedClient(long clientId, String username, int balance, boolean authenticated) {
        this.clientId = clientId;
        this.username = username;
        this.balance = balance;
        this.authenticated = authenticated;
        this.activity = "Not logged in";
    }

    public ConnectedClient(long clientId, String username, int balance, boolean authenticated, ConnectionToClient client) {
        this.clientId = clientId;
        this.username = username;
        this.balance = balance;
        this.authenticated = authenticated;
        this.client = client;
        this.activity = "Not logged in";
    }

    public ConnectionToClient getClient() {
        return client;
    }

    public long getClientId() { return clientId; }
    public String getUsername() { return username; }
    public int getBalance() { return balance; }
    public boolean isAuthenticated() { return authenticated; }
    public String getActivity() { return activity; }

    public void setUsername(String username) { this.username = username; }
    public void setBalance(int balance) { this.balance = balance; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
    public void setActivity(String activity) { this.activity = activity; }
}