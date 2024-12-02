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

    public GamePanel(List<User> players) {
        this.players = players;
        System.out.println("Creating GamePanel with " + (players != null ? players.size() : 0) + " players");
        if (players != null) {
            for (User player : players) {
                System.out.println("Player in GamePanel: " + player.getUsername() + " Balance: " + player.getBalance());
            }
        }
        
        setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        setMinimumSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        setSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        setBackground(Color.DARK_GRAY);
        
        createTableImage();
        setupLabels();
        setupButtons();
        
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

    private void setupLabels() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balanceLabel = new JLabel("Balance: $0");
        potLabel = new JLabel("Pot: $0");
        balanceLabel.setForeground(Color.WHITE);
        potLabel.setForeground(Color.WHITE);
        infoPanel.setOpaque(false);
        infoPanel.add(balanceLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(potLabel);
        add(infoPanel, BorderLayout.NORTH);
    }

    private void setupButtons() {
        callButton = new JButton("Call");
        foldButton = new JButton("Fold");
        raiseButton = new JButton("Raise");
        checkButton = new JButton("Check");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(callButton);
        buttonPanel.add(foldButton);
        buttonPanel.add(raiseButton);
        buttonPanel.add(checkButton);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);
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
        } else {
            System.out.println("No players to draw");
        }
    }

    private void drawPlayers(Graphics2D g2d) {
        if (players == null) return;
        
        double scaleX = (double) getWidth() / REFERENCE_WIDTH;
        double scaleY = (double) getHeight() / REFERENCE_HEIGHT;
        
        for (int i = 0; i < Math.min(players.size(), SEAT_POSITIONS.length); i++) {
            User user = players.get(i);
            Point seatPos = SEAT_POSITIONS[i];
            
            int x = (int) (seatPos.x * scaleX);
            int y = (int) (seatPos.y * scaleY);
            
            drawUser(g2d, user, x, y);
        }
    }

    private void drawUser(Graphics2D g2d, User user, int x, int y) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics metrics = g2d.getFontMetrics();
        String name = user.getUsername();
        String balance = "$" + user.getBalance();
        int nameWidth = metrics.stringWidth(name);
        int balanceWidth = metrics.stringWidth(balance);
        int textWidth = Math.max(nameWidth, balanceWidth) + 10;
        int textHeight = 40;
        
        g2d.setColor(new Color(0, 0, 0, 180)); 
        g2d.fillRect(x - textWidth/2, y - 90, textWidth, textHeight);
        
        g2d.setColor(Color.WHITE);
        g2d.drawString(name, x - nameWidth/2, y - 70);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        metrics = g2d.getFontMetrics();
        balanceWidth = metrics.stringWidth(balance);
        g2d.drawString(balance, x - balanceWidth/2, y - 55);
        
        g2d.setColor(Color.GRAY);
        g2d.fillOval(x - 32, y - 32, 64, 64);
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - 30, y - 30, 60, 60);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        metrics = g2d.getFontMetrics();
        String initial = user.getUsername().substring(0, 1).toUpperCase();
        int initialWidth = metrics.stringWidth(initial);
        g2d.drawString(initial, x - initialWidth/2, y + metrics.getHeight()/3);
        
        drawUserCards(g2d, x, y, user.getHand().getCards());
    }

    private void drawUserCards(Graphics2D g2d, int x, int y, List<CardClass> cards) {
    	 int cardWidth = 30;
         int cardHeight = 40;
         
         if (cardBackImage != null) {
             g2d.drawImage(cardBackImage, x - 40, y - 15, cardWidth, cardHeight, null);
             g2d.drawImage(cardBackImage, x + 10, y - 15, cardWidth, cardHeight, null);
         } else {
             g2d.setColor(new Color(220, 220, 220));
             g2d.fillRect(x - 40, y - 15, cardWidth, cardHeight);
             g2d.fillRect(x + 10, y - 15, cardWidth, cardHeight);
             g2d.setColor(Color.GRAY);
             g2d.drawRect(x - 40, y - 15, cardWidth, cardHeight);
             g2d.drawRect(x + 10, y - 15, cardWidth, cardHeight);
        }
         
         drawPlayerHand(g2d, cards, getWidth() / 2 - (cards.size() * (cardWidth + 10)) / 2, getHeight() - 100, cardWidth, cardHeight);
    }
    
    private void drawPlayerHand(Graphics2D g2d, List<CardClass> cards, int x, int y, int cardWidth, int cardHeight) {
    	int handWidth = 60;
    	int handHeight = 90;
    	
    	for (int i = 0; i < cards.size(); i++) {
    		CardClass card = cards.get(i);
    		BufferedImage handImage = null;
    	
	    	try {
	    		handImage = ImageIO.read(new File(card.getImage()));
	    	} catch(IOException e) {
	    		System.err.println("Failed to load hand image: " + card.getImage() + ". Using placeholder.");
	    	}
	    	if (handImage == null) {
	    		// Draw placeholder card if image not found
	            g2d.setColor(new Color(220, 220, 220));
	            g2d.fillRect(x + (i * (handWidth + 10)), y, handWidth, handHeight);
	            g2d.setColor(Color.GRAY);
	            g2d.drawRect(x + (i * (handWidth + 10)), y, handWidth, handHeight);
	            g2d.setColor(Color.BLACK);
	            g2d.drawString("?", x + (i * (handWidth + 10)) + handWidth / 2 - 5, y + handHeight / 2 + 5);
	        } else {
	            // Draw the card image
	            g2d.drawImage(handImage, x + (i * (handWidth + 10)), y, handWidth, handHeight, null);
	        }
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
        potLabel.setText("Pot: $" + pot);
        repaint();
    }

    public void updatePlayerBalance(int balance) {
        balanceLabel.setText("Balance: $" + balance);
        repaint();
    }

    public void updatePlayers(List<User> players) {
        this.players = players;
        repaint();
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
}
