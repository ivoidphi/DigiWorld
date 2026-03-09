import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*; // PARAS BG MUSIC
import java.io.*;

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
    NPC chiefRei;
    TileManager tileManager = new TileManager(this); // NEW


    Clip backgroundMusic;
    Clip walkSound;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyHandler);
        playBackgroundMusic();

        chiefRei = new NPC(
                this,
                "Chief Rei",
                6, 5,                        // tile position (col, row)
                "res/player/chief-rei.png",     // sprite path
                new String[]{
                        "Welcome, traveler. I am Chief Rei, guardian of this village.",
                        "You seek the Alpha Beast? Then follow the Mystic Forest.",
                        "The path will test you before you reach the Alpha. Be prepared."
                }
        );
    }

    private void playBackgroundMusic() {
        try {
            File musicFile = new File("res/SOUNDS/BG_MUSIC.wav");  // para ilis bg sound
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            
            
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-35.0f); // pang pa hinay sa volume sa bg music
            
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing background music: GE UNSA MANI NIMO BAI, WALA NAMAY MUSIC");
        }
    }
   
    public void playWalkSound() {
        try {
            if (walkSound == null) {
                File soundFile = new File("res/SOUNDS/WALK_GRASS.wav"); // walk sound effect for grass
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                walkSound = AudioSystem.getClip();
                walkSound.open(audioStream);
                
                
                FloatControl gainControl = (FloatControl) walkSound.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(6.0f);  // pang pataas sa volume sa walk sound para madungog siya over bg music
            }
        // para ma loop ang walk sound basta nag move ang character, pero kung dili na siya move, mo stop siya
            if (!walkSound.isRunning()) {
                walkSound.setFramePosition(0);  // e reset ang sound sa starting point para ma loop siya
                walkSound.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing walk sound");
        }
    }
   // pang stop sa walk sound kung wala ga move ang character
    public void stopWalkSound() {
        if (walkSound != null && walkSound.isRunning()) {
            walkSound.stop();
        }
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
        chiefRei.update(keyHandler);
        keyHandler.clearPressed();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2); // draw map FIRST
        player.draw(g2);      // draw player ON TOP
        chiefRei.draw(g2);
        g2.dispose();
    }
}