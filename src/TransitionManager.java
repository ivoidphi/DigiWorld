import java.awt.*;

public class TransitionManager {

    private enum State { NONE, FADING_OUT, FADING_IN }

    private final GamePanel gp;
    private State state = State.NONE;
    private float alpha = 0f;
    private int fromWorld = 0;
    private int targetWorld = 0;

    public TransitionManager(GamePanel gp) {
        this.gp = gp;
    }

    public void triggerTransition(int fromWorldIndex, int toWorldIndex) {
        if (state != State.NONE) return;
        fromWorld = fromWorldIndex;
        targetWorld = toWorldIndex;
        state = State.FADING_OUT;
        alpha = 0f;
    }

    public boolean isTransitioning() {
        return state != State.NONE;
    }

    public void update() {
        switch (state) {
            case FADING_OUT -> {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    gp.worldManager.loadWorld(targetWorld);
                    gp.getGameState().onPortalTravel(fromWorld, targetWorld);
                    gp.player.x = gp.screenWidth  / 2 - gp.tileSize / 2;
                    gp.player.y = gp.screenHeight / 2 - gp.tileSize / 2;
                    state = State.FADING_IN;
                }
            }
            case FADING_IN -> {
                alpha -= 0.05f;
                if (alpha <= 0f) {
                    alpha = 0f;
                    state = State.NONE;
                }
            }
            default -> {}
        }
    }

    public void draw(Graphics2D g2) {
        if (state == State.NONE) return;
        g2.setColor(new Color(0f, 0f, 0f, Math.min(alpha, 1f)));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
    }
}