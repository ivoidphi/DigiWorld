import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Lab opening dialogue (DigiWorld doc). Advance with ENTER. */
public class IntroSequence {

    private final GameState gameState;
    private final List<String> pages = new ArrayList<>();
    private int pageIndex;
    private boolean finished;

    public IntroSequence(GameState gameState) {
        this.gameState = gameState;
        pages.add(
                "DigiWorld — You are a gaming prodigy and 10-time world champion. "
                        + "You have been chosen to test the government's revolutionary game.");
        pages.add(
                "Professor Alfred: Welcome to the lab. I'm Professor Alfred (Ai-P). "
                        + "You will enter a world of Mecha Beasts — fuse, bond, and battle.");
        pages.add(
                "General Edrian: We want a game where you become the character — "
                        + "physically active, yet digital. A new legacy for gamers.");
        pages.add(
                "You: That doesn't fully answer why the government funded this… Oh well. Let's start.");
        pages.add(
                "Professor Alfred: Take your G-Watch, Mech-driver, and Beast-Cards. "
                        + "Next: choose 3 of 10 Mecha Beasts before transport to DigiWorld.");
    }

    public void reset() {
        pageIndex = 0;
        finished = false;
    }

    public boolean isFinished() {
        return finished;
    }

    public void update(KeyHandler key) {
        if (finished) return;
        if (key.enterPressed) {
            pageIndex++;
            if (pageIndex >= pages.size()) {
                finished = true;
                gameState.setStage(StoryStage.ALPHA_VILLAGE);
            }
        }
    }

    public void draw(Graphics2D g2, int screenW, int screenH) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(new Color(15, 18, 28));
        g2.fillRect(0, 0, screenW, screenH);
        String text = finished ? "" : pages.get(pageIndex);
        g2.setColor(new Color(235, 237, 248));
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        int lineH = 26;
        int maxLines = Math.min(26, Math.max(5, (screenH - 100) / lineH));
        UiText.drawWrapped(g2, text, 40, 56, screenW - 80, lineH, maxLines);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        g2.setColor(new Color(160, 170, 200));
        g2.drawString("ENTER — continue", 40, screenH - 36);
    }
}
