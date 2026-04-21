import java.awt.*;

public class TransitionManager {

    private enum State { NONE, FADING_OUT, FADING_IN }

    private final GamePanel gp;
    private State state = State.NONE;
    private float alpha = 0f;
    private int fromWorld  = 0;
    private int targetWorld = 0;
    private int spawnCol = -1; // -1 = use world default or center
    private int spawnRow = -1;

    public TransitionManager(GamePanel gp) {
        this.gp = gp;
    }

    /** Portal transition — uses destination world's spawn or screen center. */
    public void triggerTransition(int fromWorldIndex, int toWorldIndex) {
        triggerTransition(fromWorldIndex, toWorldIndex, -1, -1);
    }

    /** Door transition — uses explicit spawn tile position. */
    public void triggerTransition(int fromWorldIndex, int toWorldIndex, int spawnCol, int spawnRow) {
        if (state != State.NONE) return;
        fromWorld   = fromWorldIndex;
        targetWorld = toWorldIndex;
        this.spawnCol = spawnCol;
        this.spawnRow = spawnRow;
        state = State.FADING_OUT;
        alpha = 0f;
    }

    public boolean isTransitioning() { return state != State.NONE; }

    public void update() {
        switch (state) {
            case FADING_OUT -> {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    gp.worldManager.loadWorld(targetWorld);
                    gp.getGameState().onPortalTravel(fromWorld, targetWorld);
                    placePlayer();
                    state = State.FADING_IN;
                }
            }
            case FADING_IN -> {
                alpha -= 0.05f;
                if (alpha <= 0f) { alpha = 0f; state = State.NONE; }
            }
            default -> {}
        }
    }

    private void placePlayer() {
        if (spawnCol >= 0 && spawnRow >= 0) {
            // Explicit spawn from DoorEntry
            gp.player.x = spawnCol * gp.tileSize;
            gp.player.y = spawnRow * gp.tileSize;
        } else {
            // Default: screen center
            gp.player.x = gp.screenWidth  / 2 - gp.tileSize / 2;
            gp.player.y = gp.screenHeight / 2 - gp.tileSize / 2;
        }
    }

    public void draw(Graphics2D g2) {
        if (state == State.NONE) return;
        g2.setColor(new Color(0f, 0f, 0f, Math.min(alpha, 1f)));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }
}