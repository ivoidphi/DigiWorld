import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class NPC {

    GamePanel gp;

    public int x, y;
    public String name;
    public String[] dialogue;
    public int dialogueIndex = 0;
    public boolean talking = false;

    BufferedImage sprite;

    public NPC(GamePanel gp, String name, int tileX, int tileY, String spritePath, String[] dialogue) {
        this.gp = gp;
        this.name = name;
        this.x = tileX * gp.tileSize;
        this.y = tileY * gp.tileSize;
        this.dialogue = dialogue;
        try {
            sprite = ImageIO.read(new File(spritePath));
        } catch (IOException e) {
            System.out.println("Could not load NPC sprite: " + spritePath);
        }
    }

    // Check if player is close enough to interact
    public boolean isPlayerNearby() {
        int px = gp.player.x;
        int py = gp.player.y;
        int dist = 2 * gp.tileSize; // interact range
        return Math.abs(px - x) < dist && Math.abs(py - y) < dist;
    }

    // Called when player presses E
    public void interact() {
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
        if (key.interactPressed && isPlayerNearby()) {
            interact();
        }
    }

    public void draw(Graphics2D g2) {
        if (sprite != null) {
            g2.drawImage(sprite, x, y, gp.tileSize * 2, gp.tileSize * 2, null);
        }

        if (talking) {
            drawDialogueBox(g2);
        }
    }

    private void drawDialogueBox(Graphics2D g2) {
        // Box dimensions
        int boxX = 40;
        int boxY = gp.screenHeight - 180;
        int boxW = gp.screenWidth - 80;
        int boxH = 140;
        int arc  = 20;

        // Background
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, arc, arc);

        // Border
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, arc, arc);

        // NPC name
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.setColor(new Color(255, 220, 50)); // yellow name
        g2.drawString(name, boxX + 20, boxY + 30);

        // Dialogue text
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        g2.drawString(dialogue[dialogueIndex], boxX + 20, boxY + 65);

        // "Press E" prompt
        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(180, 180, 180));
        String prompt = (dialogueIndex < dialogue.length - 1) ? "Press E to continue..." : "Press E to close";
        g2.drawString(prompt, boxX + 20, boxY + 115);
    }
}