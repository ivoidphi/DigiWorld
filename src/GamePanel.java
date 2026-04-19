import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements Runnable {

    final int tileSize = 64;
    final int screenCols = 16;
    final int screenRows = 12;
    final int screenWidth  = tileSize * screenCols;
    final int screenHeight = tileSize * screenRows;
    final int FPS = 60;

    List<NPC> npcs = new ArrayList<>();
    Thread gameThread;

    KeyHandler keyHandler       = new KeyHandler();
    Player player               = new Player(this);
    TileManager tileManager     = new TileManager(this);
    StructureManager structureManager = new StructureManager(this); // Branch 1
    WorldManager worldManager;
    TransitionManager transition;
    BattleSequence battle;                                           // Branch 2

    final GameState gameState   = new GameState();                   // Branch 2
    IntroSequence introSequence;                                     // Branch 2
    BeastSelectionScreen beastSelection;                             // Branch 2
    AppMode appMode             = AppMode.INTRO;                     // Branch 2

    private Clip walkSound;

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(keyHandler);

        introSequence  = new IntroSequence(gameState);
        beastSelection = new BeastSelectionScreen(gameState);

        worldManager = new WorldManager(this);
        transition   = new TransitionManager(this);
        battle       = new BattleSequence(this, gameState);

        npcs = Characters.loadAll(this, World.ALPHA_VILLAGE);

        playBackgroundMusic();
    }

    private void playBackgroundMusic() {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File("res/SOUNDS/BG_MUSIC.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-35.0f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
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

    public GameState getGameState() { return gameState; }

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
            if (delta >= 1) { update(); repaint(); delta--; }
        }
    }

    private void update() {
        if (appMode == AppMode.INTRO) {
            introSequence.update(keyHandler);
            if (introSequence.isFinished()) { beastSelection.reset(); appMode = AppMode.BEAST_SELECT; }
            keyHandler.clearPressed();
            return;
        }
        if (appMode == AppMode.BEAST_SELECT) {
            beastSelection.update(keyHandler);
            if (beastSelection.isFinished()) appMode = AppMode.WORLD;
            keyHandler.clearPressed();
            return;
        }

        if (battle.isActive()) {
            battle.update(keyHandler);
            transition.update();
            keyHandler.clearPressed();
            return;
        }

        if (!transition.isTransitioning()) {
            player.update(keyHandler);
            for (NPC npc : npcs) npc.update(keyHandler);
            worldManager.checkPortal();
            if (keyHandler.battlePressed) {
                boolean tut = !gameState.isTutorialBattleComplete();
                battle.startWildBattle(BattleCreature.fromId(MechaBeastId.VINERATOPS, 4), tut ? 30 : 50, tut);
            }
        }
        transition.update();
        keyHandler.clearPressed();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (appMode == AppMode.INTRO) {
            introSequence.draw(g2, screenWidth, screenHeight);
            g2.dispose();
            return;
        }
        if (appMode == AppMode.BEAST_SELECT) {
            beastSelection.draw(g2, screenWidth, screenHeight);
            g2.dispose();
            return;
        }

        // World rendering with structure support (Branch 1)
        tileManager.draw(g2);
        structureManager.drawBeforePlayer(g2);
        player.draw(g2);
        for (NPC npc : npcs) npc.draw(g2);
        structureManager.drawAfterPlayer(g2);
        transition.draw(g2);

        // NPC dialogue always on top (Branch 1)
        for (NPC npc : npcs) npc.drawUI(g2);

        // HUD
        if (!battle.isActive()) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            g2.setColor(new Color(248, 248, 255));
            g2.drawString(worldManager.getCurrentWorldName(), 14, 24);
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            g2.setColor(new Color(220, 225, 240));
            g2.drawString(gameState.getPlayerName() + " — " + gameState.getPartyIds().size() + " beasts", 14, 44);
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            g2.setColor(new Color(190, 200, 225));
            UiText.drawWrapped(g2, gameState.getStageLabel(), 14, 58, screenWidth - 28, 18, 2);
        }

        // Battle overlay always on top (Branch 2)
        if (battle.isActive()) battle.draw(g2);

        g2.dispose();
    }
}