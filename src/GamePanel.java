import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;

public class GamePanel extends JPanel {
    private static final int TABLE_WIDTH = 800;
    private static final int TABLE_HEIGHT = 600;
    private static final Color TABLE_COLOR = new Color(34, 139, 34);
    private static final Color FELT_COLOR = new Color(53, 101, 77);
    private List<User> players;
    private BufferedImage tableImage;
    private JButton callButton;
    private JButton foldButton;
    private JButton raiseButton;
    private JButton checkButton;
    private ActionListener actionListener;

    public GamePanel(List<User> players) {
        this.players = players;
        setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        setBackground(Color.DARK_GRAY);
        createTableImage();
        setupButtons();
    }

    private void createTableImage() {
        tableImage = new BufferedImage(TABLE_WIDTH, TABLE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tableImage.createGraphics();

        g2d.setColor(TABLE_COLOR);
        g2d.fillOval(50, 50, TABLE_WIDTH - 100, TABLE_HEIGHT - 100);

        g2d.setColor(FELT_COLOR);
        g2d.fillOval(70, 70, TABLE_WIDTH - 140, TABLE_HEIGHT - 140);

        g2d.dispose();
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

        g2d.drawImage(tableImage, 0, 0, this);

        if (players != null && !players.isEmpty()) {
            drawPlayers(g2d);
        }
    }

    private void drawPlayers(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 100;

        for (int i = 0; i < players.size(); i++) {
            User user = players.get(i);
            double angle = (2 * Math.PI * i / players.size()) - Math.PI / 2;

            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));

            drawUser(g2d, user, x, y);
        }
    }

    private void drawUser(Graphics2D g2d, User user, int x, int y) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.GRAY);
        g2d.fillOval(x - 32, y - 32, 64, 64);
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x - 30, y - 30, 60, 60);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics metrics = g2d.getFontMetrics();
        String initial = user.getUsername().substring(0, 1).toUpperCase();
        int initialWidth = metrics.stringWidth(initial);
        g2d.drawString(initial, x - initialWidth/2, y + metrics.getHeight()/3);

        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        metrics = g2d.getFontMetrics();
        String name = user.getUsername();
        int nameWidth = metrics.stringWidth(name);
        g2d.drawString(name, x - nameWidth/2, y + 45);
        
        drawUserCards(g2d, x, y);
    }

    private void drawUserCards(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x - 40, y - 15, 30, 40);
        g2d.fillRect(x + 10, y - 15, 30, 40);

        g2d.setColor(Color.RED);
        g2d.drawRect(x - 40, y - 15, 30, 40);
        g2d.drawRect(x + 10, y - 15, 30, 40);
    }
}
