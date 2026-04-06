import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Choose 3 of 9 Mecha Beasts (ENTER toggles, R confirms). */
public class BeastSelectionScreen {

    private final GameState gameState;
    private final MechaBeastTemplate[] options;
    private int cursor;
    private boolean finished;

    public BeastSelectionScreen(GameState gameState) {
        this.gameState = gameState;
        this.options = MechaBeastCatalog.allPlayable();
        this.cursor = 0;
    }

    public void reset() {
        gameState.clearParty();
        cursor = 0;
        finished = false;
    }

    public boolean isFinished() {
        return finished;
    }

    public void update(KeyHandler key) {
        if (finished) return;
        if (key.navUpPressed) cursor = (cursor + options.length - 1) % options.length;
        if (key.navDownPressed) cursor = (cursor + 1) % options.length;
        if (key.navLeftPressed) cursor = (cursor + options.length - 1) % options.length;
        if (key.navRightPressed) cursor = (cursor + 1) % options.length;

        if (key.enterPressed) {
            MechaBeastId id = options[cursor].id;
            if (gameState.getPartyIds().contains(id)) {
                gameState.removePartyMember(id);
            } else {
                gameState.addPartyMember(id);
            }
        }

        if (key.rPressed) {
            if (gameState.isPartyFull()) {
                finished = true;
                gameState.setBeastPickComplete(true);
            }
        }
    }

    public void draw(Graphics2D g2, int screenW, int screenH) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(new Color(12, 14, 22));
        g2.fillRect(0, 0, screenW, screenH);
        g2.setColor(new Color(240, 242, 255));
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        UiText.drawWrapped(g2, "Choose 3 Mecha Beasts — ENTER toggles selection — R confirms when you have 3.",
                24, 32, screenW - 48, 26, 3);

        int colW = 320;
        int x0 = 24;
        int y0 = 100;
        for (int i = 0; i < options.length; i++) {
            int col = i % 2;
            int row = i / 2;
            int x = x0 + col * colW;
            int y = y0 + row * 36;
            MechaBeastTemplate t = options[i];
            boolean sel = gameState.getPartyIds().contains(t.id);
            boolean hi = i == cursor;
            if (hi) g2.setColor(new Color(255, 220, 100));
            else if (sel) g2.setColor(new Color(120, 200, 140));
            else g2.setColor(new Color(200, 200, 210));

            String mark = sel ? "[*] " : "[ ] ";
            g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
            g2.drawString(mark + t.name + " (" + t.primaryType.displayName() + ")", x, y);
        }

        g2.setColor(new Color(160, 160, 180));
        g2.drawString("Selected: " + gameState.getPartyIds().size() + " / 3", 24, screenH - 48);
        if (gameState.isPartyFull()) {
            g2.setColor(new Color(255, 200, 120));
            g2.drawString("Party full — press R to begin!", 24, screenH - 24);
        }
    }
}
