import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import java.util.ArrayList;

public class ServerClass extends AbstractServer {
    private DatabaseClass database;
    private ArrayList<Game> activeGames;
    private ChatServer chatServer;
    
    public ServerClass(int port) {
        super(port);
        database = new DatabaseClass();
        activeGames = new ArrayList<>();
        chatServer = new ChatServer();
    }
    
    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof LoginData) {
            handleLogin((LoginData)msg, client);
        }
        else if (msg instanceof CreateAccountData) {
            handleCreateAccount((CreateAccountData)msg, client);
        }
    }
    
    private void handleLogin(LoginData data, ConnectionToClient client) {
        boolean success = database.verifyLogin(data);
        try {
            client.sendToClient(new LoginData(success));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void handleCreateAccount(CreateAccountData data, ConnectionToClient client) {
        boolean success = database.createAccount(data);
        try {
            client.sendToClient(new CreateAccountData(success));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
