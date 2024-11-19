import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import javax.swing.*;

public class GameServer extends AbstractServer {
    // Add counters at the class level
    private int unauthenticatedUsers = 0;
    private int authenticatedUsers = 0;
    private JTextArea unauthenticatedCountArea;
    private JTextArea authenticatedCountArea;

    public GameServer(int port, JTextArea unauthenticatedCountArea, JTextArea authenticatedCountArea) {
        super(port);
        this.unauthenticatedCountArea = unauthenticatedCountArea;
        this.authenticatedCountArea = authenticatedCountArea;
        updateCounterDisplays();
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        unauthenticatedUsers++;
        updateCounterDisplays();
    }

    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        // Check if client was authenticated
        Boolean isAuthenticated = (Boolean) client.getInfo("authenticated");
        if (isAuthenticated != null && isAuthenticated) {
            authenticatedUsers--;
        } else {
            unauthenticatedUsers--;
        }
        updateCounterDisplays();
    }

    // Add this method to handle successful login
    public void handleSuccessfulLogin(ConnectionToClient client) {
        unauthenticatedUsers--;
        authenticatedUsers++;
        client.setInfo("authenticated", true);
        updateCounterDisplays();
    }

    private void updateCounterDisplays() {
        SwingUtilities.invokeLater(() -> {
            unauthenticatedCountArea.setText("Unauthenticated Users: " + unauthenticatedUsers);
            authenticatedCountArea.setText("Authenticated Users: " + authenticatedUsers);
        });
    }

    @Override
    protected void handleMessageFromClient(Object o, ConnectionToClient connectionToClient) {

    }
}