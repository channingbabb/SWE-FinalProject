import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class WaitingRoomControl {
    private WaitingRoomPanel view;
    private List<User> players;
    private JFrame mainFrame;
    private PlayerClient client;

    public WaitingRoomControl(WaitingRoomPanel view, JFrame mainFrame, PlayerClient client) {
        this.view = view;
        this.mainFrame = mainFrame;
        this.client = client;
        this.players = new ArrayList<>();
        view.setController(this);
        setupNetworkListeners();
    }

    private void setupNetworkListeners() {
        client.addMessageHandler("PLAYER_UPDATE", message -> {
            final List<User> players = message.getPlayers().size() > 8 
                ? message.getPlayers().subList(0, 8) 
                : message.getPlayers();
            SwingUtilities.invokeLater(() -> updatePlayers(players));
        });

        client.addMessageHandler("GAME_START", message -> {
            SwingUtilities.invokeLater(this::startGame);
        });

        client.addMessageHandler("PLAYER_KICKED", message -> {
            if (message.getKickedPlayer().equals(client.getUsername())) {
                SwingUtilities.invokeLater(this::handleKicked);
            }
        });
    }

    public void handleStartGame() {
        client.sendMessage(new Message("START_GAME"));
    }

    public void handleLeaveGame() {
        client.sendMessage(new Message("LEAVE_GAME"));
        returnToLobby();
    }

    public void handleKickPlayer() {
        String selectedPlayer = view.getSelectedPlayer();
        if (selectedPlayer != null) {
            Message kickMessage = new Message("KICK_PLAYER");
            kickMessage.setKickedPlayer(selectedPlayer);
            client.sendMessage(kickMessage);
        }
    }

    private void startGame() {
        mainFrame.getContentPane().removeAll();
        
        GamePanel gamePanel = new GamePanel(players);
        mainFrame.add(gamePanel);
        
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void handleKicked() {
        JOptionPane.showMessageDialog(mainFrame, 
            "You have been kicked from the game", 
            "Kicked", 
            JOptionPane.WARNING_MESSAGE);
        returnToLobby();
    }

    private void returnToLobby() {
        mainFrame.getContentPane().removeAll();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void updatePlayers(List<User> players) {
        this.players = players;
        view.updatePlayersList(players);
    }
}