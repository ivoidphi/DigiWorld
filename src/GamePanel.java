import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    final int tileSize = 64;
    final int screenCols = 16;
    final int screenRows = 12;
    final int screenWidth = tileSize * screenCols;   // 512px
    final int screenHeight = tileSize * screenRows;  // 384px

    final int FPS = 60;
    Thread gameThread;

    KeyHandler keyHandler = new KeyHandler();
    Player player = new Player(this);
    TileManager tileManager = new TileManager(this); // NEW

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyHandler);
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
        player.update(keyHandler);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2); // draw map FIRST
        player.draw(g2);      // draw player ON TOP

        g2.dispose();
    }
}