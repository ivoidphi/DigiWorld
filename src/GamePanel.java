import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    final int tileSize = 32;
    final int screenCols = 16;
    final int screenRows = 12;
    final int screenWidth = tileSize * screenCols;
    final int screenHeight = tileSize * screenRows;

    final int FPS = 60;
    Thread gameThread;

    // NEW
    KeyHandler keyHandler = new KeyHandler();
    Player player = new Player(this);

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyHandler); // NEW
    }

    public void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    public void update() {
        player.update(keyHandler); // NEW
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        player.draw(g2); // NEW
        g2.dispose();
    }
}