import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    final int tileSize    = 64;
    final int screenCols  = 16;
    final int screenRows  = 12;
    final int screenWidth  = tileSize * screenCols;
    final int screenHeight = tileSize * screenRows;
    final int FPS          = 60;

    List<NPC> npcs = new ArrayList<>();
    Thread gameThread;

    KeyHandler        keyHandler   = new KeyHandler();
    Player            player       = new Player(this);
    TileManager       tileManager  = new TileManager(this);
    WorldManager      worldManager;
    TransitionManager transition;

    private Clip walkSound;

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(keyHandler);

        worldManager = new WorldManager(this);
        transition   = new TransitionManager(this);
        npcs         = Characters.loadAll(this);

        playBackgroundMusic();
    }

    private void playBackgroundMusic() {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File("res/SOUNDS/BG_MUSIC.wav"));
            Clip backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(stream);
            ((FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-35.0f);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing background music.");
        }
    }

    public void playWalkSound() {
        try {
            if (walkSound == null) {
                AudioInputStream stream = AudioSystem.getAudioInputStream(new File("res/SOUNDS/WALK_GRASS.wav"));
                walkSound = AudioSystem.getClip();
                walkSound.open(stream);
                ((FloatControl) walkSound.getControl(FloatControl.Type.MASTER_GAIN)).setValue(6.0f);
            }
            if (!walkSound.isRunning()) {
                walkSound.setFramePosition(0);
                walkSound.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing walk sound.");
        }
    }

    public void stopWalkSound() {
        if (walkSound != null && walkSound.isRunning()) walkSound.stop();
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

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (!transition.isTransitioning()) {
            player.update(keyHandler);
            for (NPC npc : npcs) npc.update(keyHandler);
            worldManager.checkPortal();
        }
        transition.update();
        keyHandler.clearPressed();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2);
        player.draw(g2);
        for (NPC npc : npcs) npc.draw(g2);
        transition.draw(g2);

        g2.setFont(new Font("DIALOG_INPUT", Font.BOLD, 25));
        g2.setColor(Color.WHITE);
        g2.drawString(worldManager.getCurrentWorldName(), 16, 24);
        g2.drawString("Tile: " + (player.x / tileSize) + ", " + (player.y / tileSize), 16, 44);

        g2.dispose();
    }
}