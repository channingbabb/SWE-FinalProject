import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel {
    private static final int TABLE_WIDTH = 1900;
    private static final int TABLE_HEIGHT = 900;
    private static final Color TABLE_COLOR = new Color(34, 139, 34);
    private static final Color FELT_COLOR = new Color(53, 101, 77);
    private static final Point[] SEAT_POSITIONS = {
        new Point(1415, 145),
        new Point(1669, 470),
        new Point(1571, 744),
        new Point(1169, 869),
        new Point(817, 861),
        new Point(432, 734),
        new Point(345, 475),
        new Point(573, 158)
    };
    private static final Point DEALER_POSITION = new Point(999, 120);
    private static final int REFERENCE_WIDTH = 1900;
    private static final int REFERENCE_HEIGHT = 900;
    private static final Point COMMUNITY_CARDS_POSITION = new Point(950, 450);
    private List<User> players;
    private BufferedImage tableImage;
    private BufferedImage cardBackImage;
    private JButton callButton;
    private JButton foldButton;
    private JButton raiseButton;
    private JButton checkButton;
    private ActionListener actionListener;
    private JLabel balanceLabel;
    private JLabel potLabel;
    private int currentPot;
    private PlayerClient client;
    private JLabel turnLabel;
    private List<CardClass> communityCards = new ArrayList<>();

    public GamePanel(List<User> players, PlayerClient client) {
        this.players = players;
        this.client = client;
        
        this.currentPot = players.size() * 50;
        //this.currentPot = 0;
        
        setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        setMinimumSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        setSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());
        
        createTableImage();
        setupButtons();
        setupLabels();
        
        potLabel.setText("Current Pot: $" + currentPot);
        
        revalidate();
        repaint();
    }

    private void createTableImage() {
        try {
            tableImage = ImageIO.read(new File("assets/poker-table.png"));
            cardBackImage = ImageIO.read(new File("assets/card-back.png"));
        } catch (IOException e) {
            System.err.println("Error loading poker table image: " + e.getMessage());
            tableImage = new BufferedImage(TABLE_WIDTH, TABLE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = tableImage.createGraphics();
            g2d.setColor(TABLE_COLOR);
            g2d.fillOval(50, 50, TABLE_WIDTH - 100, TABLE_HEIGHT - 100);
            g2d.setColor(FELT_COLOR);
            g2d.fillOval(70, 70, TABLE_WIDTH - 140, TABLE_HEIGHT - 140);
            g2d.dispose();
        }
    }

    private void setupButtons() {
        callButton = new JButton("Call");
        foldButton = new JButton("Fold");
        raiseButton = new JButton("Raise");
        checkButton = new JButton("Check");
        
        setButtonsEnabled(false);
    }

    private void setupLabels() {
        JPanel topInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        potLabel = new JLabel("Pot: $0");
        potLabel.setFont(new Font("Arial", Font.BOLD, 24));
        potLabel.setForeground(Color.WHITE);
        topInfoPanel.setOpaque(false);
        topInfoPanel.add(potLabel);
        add(topInfoPanel, BorderLayout.NORTH);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setOpaque(false);
        
        balanceLabel = new JLabel("Balance: $0");
        turnLabel = new JLabel("Waiting for turn...", SwingConstants.CENTER);
        
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(Color.WHITE);
        turnLabel.setForeground(Color.WHITE);
        
        infoPanel.add(balanceLabel);
        infoPanel.add(Box.createHorizontalStrut(30));
        infoPanel.add(turnLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(callButton);
        buttonPanel.add(foldButton);
        buttonPanel.add(raiseButton);
        buttonPanel.add(checkButton);
        
        southPanel.add(infoPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(southPanel, BorderLayout.SOUTH);
    }

    public void addActionListeners(ActionListener listener) {
        this.actionListener = listener;
        callButton.addActionListener(listener);
        foldButton.addActionListener(listener);
        raiseButton.addActionListener(listener);
        checkButton.addActionListener(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(tableImage, 0, 0, getWidth(), getHeight(), this);

        if (players != null && !players.isEmpty()) {
            System.out.println("Drawing " + players.size() + " players");
            drawPlayers(g2d);
            drawDealerButton(g2d);
            drawCommunityCards(g2d);
        } else {
            System.out.println("No players to draw");
        }
    }

    private void drawPlayers(Graphics2D g2d) {
        if (players == null || players.isEmpty()) {
            System.out.println("No players to draw");
            return;
        }
        
        System.out.println("Drawing " + players.size() + " players");
        double scaleX = (double) getWidth() / REFERENCE_WIDTH;
        double scaleY = (double) getHeight() / REFERENCE_HEIGHT;
        
        for (int i = 0; i < Math.min(players.size(), SEAT_POSITIONS.length); i++) {
            User player = players.get(i);
            Point seatPos = SEAT_POSITIONS[i];
            
            int x = (int) (seatPos.x * scaleX);
            int y = (int) (seatPos.y * scaleY);
            
            System.out.println("Drawing player " + player.getUsername() + " at position " + i);
            drawUser(g2d, player, x, y);
        }
    }

    private void drawUser(Graphics2D g2d, User user, int x, int y) {
        FontMetrics metrics;
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - 25, y - 25, 50, 50);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - 25, y - 25, 50, 50);
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        metrics = g2d.getFontMetrics();
        String initial = user.getUsername().substring(0, 1).toUpperCase();
        int initialWidth = metrics.stringWidth(initial);
        g2d.drawString(initial, x - initialWidth/2, y + metrics.getHeight()/3);
        
        drawUserCards(g2d, x, y, user.getHand().getCards(), user.getUsername());
    }

    private void drawUserCards(Graphics2D g2d, int x, int y, List<CardClass> cards, String username) {
        int cardWidth = 60;
        int cardHeight = 80;
        
        if (cardBackImage == null) {
            try {
                cardBackImage = ImageIO.read(new File("assets/card-back.png"));
            } catch (IOException e) {
                System.err.println("Error loading card back image: " + e.getMessage());
            }
        }
        
        if (username.equals(client.getCurrentUser().getUsername())) {
            System.out.println("Drawing cards for current player: " + username);
            for (int i = 0; i < cards.size(); i++) {
                CardClass card = cards.get(i);
                String rankText = convertRankToText(card.getRank());
                String imagePath = "assets/cards/" + rankText + "_of_" + card.getSuit().toLowerCase() + ".png";
                
                System.out.println("Attempting to load card image: " + imagePath);
                try {
                    BufferedImage cardImage = ImageIO.read(new File(imagePath));
                    int cardX = x + (i * (cardWidth + 10)) - 40;
                    int cardY = y - cardHeight - 20;
                    g2d.drawImage(cardImage, cardX, cardY, cardWidth, cardHeight, null);
                    System.out.println("Successfully drew card: " + imagePath);
                } catch (IOException e) {
                    System.err.println("Error loading card image: " + imagePath);
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(x + (i * (cardWidth + 10)) - 40, y - cardHeight - 20, cardWidth, cardHeight);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(rankText + " of " + card.getSuit(), x + (i * (cardWidth + 10)) - 30, y - cardHeight + 20);
                }
            }
        } else {
            for (int i = 0; i < 2; i++) {
                int cardX = x + (i * (cardWidth + 10)) - 40;
                int cardY = y - cardHeight - 20;
                if (cardBackImage != null) {
                    g2d.drawImage(cardBackImage, cardX, cardY, cardWidth, cardHeight, null);
                } else {
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.fillRect(cardX, cardY, cardWidth, cardHeight);
                    g2d.setColor(Color.GRAY);
                    g2d.drawRect(cardX, cardY, cardWidth, cardHeight);
                }
            }
        }
    }

    private String convertRankToText(int rank) {
        switch (rank) {
            case 11: return "jack";
            case 12: return "queen";
            case 13: return "king";
            case 14: case 1: return "ace";
            default: return String.valueOf(rank);
        }
    }

    private void drawDealerButton(Graphics2D g2d) {
        double scaleX = (double) getWidth() / REFERENCE_WIDTH;
        double scaleY = (double) getHeight() / REFERENCE_HEIGHT;
        
        int x = (int) (DEALER_POSITION.x * scaleX);
        int y = (int) (DEALER_POSITION.y * scaleY);
        
        int buttonSize = 20;
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - buttonSize/2, y - buttonSize/2, buttonSize, buttonSize);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - buttonSize/2, y - buttonSize/2, buttonSize, buttonSize);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("D", x - 4, y + 4);
    }

    public void updatePot(int pot) {
        this.currentPot = pot;
        SwingUtilities.invokeLater(() -> {
            potLabel.setText("Current Pot: $" + pot);
            potLabel.repaint();
            System.out.println("Updated pot display to: $" + pot);  
        });
    }

    public void updatePlayerBalance(int balance) {
        balanceLabel.setText("Balance: $" + balance);
        repaint();
    }

    public void updatePlayers(List<User> players) {
        System.out.println("Updating players in GamePanel: " + players.size() + " players");
        this.players = new ArrayList<>(players); 
        for (User player : players) {
            System.out.println("Player: " + player.getUsername() + ", Active: " + player.isActive());
        }
        SwingUtilities.invokeLater(this::repaint);
    }

    public void setButtonsEnabled(boolean enabled) {
        callButton.setEnabled(enabled);
        foldButton.setEnabled(enabled);
        raiseButton.setEnabled(enabled);
        checkButton.setEnabled(enabled);
    }

    public void refresh() {
        repaint();
    }

    public void updateTurnIndicator(String currentPlayer) {
        SwingUtilities.invokeLater(() -> {
            if (currentPlayer.equals(client.getCurrentUser().getUsername())) {
                turnLabel.setText("★ YOUR TURN TO ACT ★");
                turnLabel.setForeground(new Color(50, 205, 50));
                setButtonsEnabled(true);
            } else {
                turnLabel.setText("Waiting for " + currentPlayer + " to act...");
                turnLabel.setForeground(Color.WHITE);
                setButtonsEnabled(false);
            }
            turnLabel.repaint();
        });
    }

    public void updateCommunityCards(List<CardClass> cards) {
        if (cards != null) {
            this.communityCards = new ArrayList<>(cards);
            System.out.println("Updated community cards: " + cards.size() + " cards");
            for (CardClass card : cards) {
                System.out.println("Community card: " + card.getSuit() + " of rank " + card.getRank());
            }
            SwingUtilities.invokeLater(() -> {
                repaint();
                System.out.println("Repainting game panel with " + cards.size() + " community cards");
            });
        } else {
            this.communityCards.clear();
            System.out.println("Cleared community cards");
            SwingUtilities.invokeLater(this::repaint);
        }
        
        if(cards == null) {
        	System.out.println("Community cards is null when trying to updateCommunityCards");
        }
    }

    private void drawCommunityCards(Graphics2D g2d) {
        if (communityCards == null || communityCards.isEmpty()) {
            return;
        }

        double scaleX = (double) getWidth() / REFERENCE_WIDTH;
        double scaleY = (double) getHeight() / REFERENCE_HEIGHT;
        
        int x = (int) (COMMUNITY_CARDS_POSITION.x * scaleX);
        int y = (int) (COMMUNITY_CARDS_POSITION.y * scaleY);
        
        int cardWidth = 60;
        int cardHeight = 80;
        int spacing = 15;
        
        int totalWidth = (cardWidth + spacing) * communityCards.size() - spacing;
        int startX = x - (totalWidth / 2);
        
        for (int i = 0; i < communityCards.size(); i++) {
            CardClass card = communityCards.get(i);
            String rankText = convertRankToText(card.getRank());
            String imagePath = "assets/cards/" + rankText + "_of_" + card.getSuit().toLowerCase() + ".png";
            
            try {
                BufferedImage cardImage = ImageIO.read(new File(imagePath));
                int cardX = startX + (i * (cardWidth + spacing));
                g2d.drawImage(cardImage, cardX, y, cardWidth, cardHeight, null);
                System.out.println("Successfully drew community card: " + card.toString());
            } catch (IOException e) {
                System.err.println("Failed to load card image: " + imagePath);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(startX + (i * (cardWidth + spacing)), y, cardWidth, cardHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawString(rankText + " of " + card.getSuit(), startX + (i * (cardWidth + spacing)), y + 40);
            }
        }
    }
}
