import ocsf.client.AbstractClient;

public class PlayerClient extends AbstractClient {
    private LoginControl loginControl;
    private CreateAccountControl createAccountControl;
    private GameControl gameControl;
    private LobbyControl lobbyControl;
    
    public PlayerClient(String host, int port) {
        super(host, port);
    }
    
    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof LoginData) {
            loginControl.handleLoginResult((LoginData)msg);
        } 
        else if (msg instanceof CreateAccountData) {
            createAccountControl.handleCreateAccountResult((CreateAccountData)msg);
        }
        else if (msg instanceof LeaderboardData) {
            lobbyControl.updateLeaderboard((LeaderboardData)msg);
        }
    }
    
    public void setLoginControl(LoginControl loginControl) {
        this.loginControl = loginControl;
    }
    
    public void setCreateAccountControl(CreateAccountControl createAccountControl) {
        this.createAccountControl = createAccountControl;
    }
    
    public void setGameControl(GameControl gameControl) {
        this.gameControl = gameControl;
    }
    
    public void setLobbyControl(LobbyControl lobbyControl) {
        this.lobbyControl = lobbyControl;
    }
}
