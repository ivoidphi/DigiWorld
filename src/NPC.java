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

    private int dialogueIndex = 0;
    private boolean talking = false;

    public NPC(GamePanel gp, String name, int tileX, int tileY, String spritePath, int worldIndex, String[] dialogue) {
        this.gp = gp;
        this.name = name;
        this.x = tileX * gp.tileSize;
        this.y = tileY * gp.tileSize;
        this.worldIndex = worldIndex;
        this.dialogue = dialogue;
        this.sprite = loadSprite(spritePath);
    }

    private BufferedImage loadSprite(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Could not load NPC sprite: " + path);
            return null;
        }
    }

    private boolean isInCurrentWorld() {
        return gp.worldManager.getCurrentWorld() != null
                && gp.worldManager.currentWorldIndex == worldIndex;
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
        if (!isInCurrentWorld()) {
            talking = false;
            return;
        }
        if (key.interactPressed && isPlayerNearby()) interact();
    }

    public void draw(Graphics2D g2) {
        if (!isInCurrentWorld()) return;
        if (sprite != null)
            g2.drawImage(sprite, x, y, gp.tileSize * 2, gp.tileSize * 2, null);
        if (talking)
            drawDialogueBox(g2);
    }

    private void drawDialogueBox(Graphics2D g2) {
        int boxX = 20;
        int boxY = gp.screenHeight - 160;
        int boxW = gp.screenWidth - 40;
        int boxH = 140;
        int arc  = 8;
        int border = 4;

        // Outer dark border
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(boxX, boxY, boxW, boxH, arc, arc);

        // Inner white box
        g2.setColor(new Color(248, 248, 248));
        g2.fillRoundRect(boxX + border, boxY + border, boxW - border * 2, boxH - border * 2, arc, arc);

        // Inner inner dark border line
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(boxX + border + 4, boxY + border + 4, boxW - (border + 4) * 2, boxH - (border + 4) * 2, arc, arc);

        // Name tag box (top left, outside/overlapping the main box)
        int nameBoxX = boxX + 16;
        int nameBoxY = boxY - 36;
        int nameBoxW = 160;
        int nameBoxH = 40;

        g2.setColor(Color.BLACK);
        g2.fillRoundRect(nameBoxX, nameBoxY, nameBoxW, nameBoxH, arc, arc);

        g2.setColor(new Color(248, 248, 248));
        g2.fillRoundRect(nameBoxX + border, nameBoxY + border, nameBoxW - border * 2, nameBoxH - border * 2, arc, arc);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(nameBoxX + border + 2, nameBoxY + border + 2, nameBoxW - (border + 2) * 2, nameBoxH - (border + 2) * 2, arc, arc);

        // Name text
        g2.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 16));
        g2.setColor(new Color(30, 30, 30));
        g2.drawString(name, nameBoxX + 12, nameBoxY + 26);

        // Dialogue text
        g2.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 22));
        g2.setColor(Color.BLACK);
        g2.drawString(dialogue[dialogueIndex], boxX + 24, boxY + 52);

        // Blinking arrow prompt (bottom right)
        long now = System.currentTimeMillis();
        boolean blink = (now / 500) % 2 == 0;
        if (blink) {
            int arrowX = boxX + boxW - 30;
            int arrowY = boxY + boxH - 20;
            int[] ax = { arrowX, arrowX + 12, arrowX + 6 };
            int[] ay = { arrowY, arrowY, arrowY + 8 };
            g2.setColor(Color.BLACK);
            g2.fillPolygon(ax, ay, 3);
        }
    }
}