import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class NPC {

    private final GamePanel gp;
    private final String name;
    private final String[] dialogue;
    private final BufferedImage sprite;
    private final int x, y;
    private final int worldIndex;

    // Collision box offsets relative to (x, y)
    private final int cbXOff, cbYOff, cbW, cbH;

    private int dialogueIndex = 0;
    private boolean talking = false;

    /** Default collision box constructor — used by Characters.java. */
    public NPC(GamePanel gp, String name, int tileX, int tileY, String spritePath,
               int worldIndex, String[] dialogue) {
        this(gp, name, tileX, tileY, spritePath, worldIndex, dialogue, 16, 80, 96, 48);
    }

    /** Full constructor with custom collision box. */
    public NPC(GamePanel gp, String name, int tileX, int tileY, String spritePath,
               int worldIndex, String[] dialogue, int cbXOff, int cbYOff, int cbW, int cbH) {
        this.gp = gp;
        this.name = name;
        this.x = tileX * gp.tileSize;
        this.y = tileY * gp.tileSize;
        this.worldIndex = worldIndex;
        this.dialogue = dialogue;
        this.cbXOff = cbXOff;
        this.cbYOff = cbYOff;
        this.cbW = cbW;
        this.cbH = cbH;
        this.sprite = loadSprite(spritePath);
    }

    private BufferedImage loadSprite(String path) {
        try { return ImageIO.read(new File(path)); }
        catch (IOException e) { System.out.println("Could not load NPC sprite: " + path); return null; }
    }

    public Rectangle getCollisionRect() {
        return new Rectangle(x + cbXOff, y + cbYOff, cbW, cbH);
    }

    private boolean isInCurrentWorld() {
        return gp.worldManager.currentWorldIndex == worldIndex;
    }

    private boolean isPlayerNearby() {
        int dist = 2 * gp.tileSize;
        return Math.abs(gp.player.x - x) < dist && Math.abs(gp.player.y - y) < dist;
    }

    private void interact() {
        if (!talking) {
            talking = true;
            dialogueIndex = 0;
        } else {
            dialogueIndex++;
            if (dialogueIndex >= dialogue.length) {
                talking = false;
                dialogueIndex = 0;
            }
        }
    }

    public void update(KeyHandler key) {
        if (!isInCurrentWorld()) { talking = false; return; }
        if (key.interactPressed && isPlayerNearby()) interact();
    }

    public void draw(Graphics2D g2) {
        if (!isInCurrentWorld()) return;
        if (sprite != null)
            g2.drawImage(sprite, x, y, gp.tileSize * 2, gp.tileSize * 2, null);

        // Uncomment to debug collision box:
        // g2.setColor(Color.CYAN);
        // g2.draw(getCollisionRect());
    }

    /** Call after ALL other draw calls so dialogue is always on top. */
    public void drawUI(Graphics2D g2) {
        if (!isInCurrentWorld()) return;
        if (talking) drawDialogueBox(g2);
    }

    private void drawDialogueBox(Graphics2D g2) {
        int boxX = 20, boxY = gp.screenHeight - 160;
        int boxW = gp.screenWidth - 40, boxH = 140, arc = 8, border = 4;

        g2.setColor(Color.BLACK);
        g2.fillRoundRect(boxX, boxY, boxW, boxH, arc, arc);
        g2.setColor(new Color(248, 248, 248));
        g2.fillRoundRect(boxX + border, boxY + border, boxW - border * 2, boxH - border * 2, arc, arc);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(boxX + border + 4, boxY + border + 4, boxW - (border + 4) * 2, boxH - (border + 4) * 2, arc, arc);

        // Name tag
        int nameBoxX = boxX + 16, nameBoxY = boxY - 36;
        int nameBoxW = 160, nameBoxH = 40;
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(nameBoxX, nameBoxY, nameBoxW, nameBoxH, arc, arc);
        g2.setColor(new Color(248, 248, 248));
        g2.fillRoundRect(nameBoxX + border, nameBoxY + border, nameBoxW - border * 2, nameBoxH - border * 2, arc, arc);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(nameBoxX + border + 2, nameBoxY + border + 2, nameBoxW - (border + 2) * 2, nameBoxH - (border + 2) * 2, arc, arc);
        g2.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 16));
        g2.setColor(Color.BLACK);
        g2.drawString(name, nameBoxX + 12, nameBoxY + 26);

        // Dialogue text
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int textPadX = 28, textPadRight = 28;
        int textMaxW = boxW - textPadX - textPadRight;
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        g2.setColor(new Color(25, 28, 38));
        UiText.drawWrapped(g2, dialogue[dialogueIndex], boxX + textPadX, boxY + 48, textMaxW, 24, 4);

        // Blinking prompt + arrow
        boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
        if (blink) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            FontMetrics fm = g2.getFontMetrics();
            String hint = "Press E to continue";
            int hintW = fm.stringWidth(hint);
            int hintX = Math.max(boxX + textPadX, boxX + boxW - hintW - textPadX);
            g2.drawString(hint, hintX, boxY + boxH - 18);
            int ax = boxX + boxW - 26, ay = boxY + boxH - 22;
            g2.setColor(Color.BLACK);
            g2.fillPolygon(new int[]{ax, ax + 12, ax + 6}, new int[]{ay, ay, ay + 8}, 3);
        }
    }
}