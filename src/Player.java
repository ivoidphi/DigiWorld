import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player {

    GamePanel gp;

    public int x, y;
    public int speed = 4;

    // Single PNG per state (swap these for sprite strips later)
    BufferedImage idleFrame;
    BufferedImage walkLeftFrame;
    BufferedImage walkRightFrame;
    BufferedImage currentFrame;
    BufferedImage behindFrame;

    String direction = "down";
    boolean moving = false;

    public Player(GamePanel gp) {
        this.gp = gp;
        x = gp.screenWidth / 2 - gp.tileSize / 2;
        y = gp.screenHeight / 2 - gp.tileSize / 2;
        loadSprites();
    }

    private void loadSprites() {
        idleFrame      = loadPNG("res/player/kyoflare-front.png");
        walkLeftFrame  = loadPNG("res/player/kyoflare-left.png");
        walkRightFrame = loadPNG("res/player/kyoflare-right.png");
        behindFrame = loadPNG("res/player/kyoflare-behind.png");

        // Fallback: if walk PNGs are missing, reuse idle
        if (walkLeftFrame  == null) walkLeftFrame  = idleFrame;
        if (walkRightFrame == null) walkRightFrame = idleFrame;

        currentFrame = idleFrame;
    }

    private BufferedImage loadPNG(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            if (img == null) {
                System.out.println("Warning: ImageIO returned null for " + path);
            }
            return img;
        } catch (IOException e) {
            System.out.println("Could not load PNG: " + path);
            return null;
        }
    }

    public void update(KeyHandler key) {
        moving = false;

        if (key.up) {
            direction = "up";
            y -= speed;
            moving = true;
        } else if (key.down) {
            direction = "down";
            y += speed;
            moving = true;
        } else if (key.left) {
            direction = "left";
            x -= speed;
            moving = true;
        } else if (key.right) {
            direction = "right";
            x += speed;
            moving = true;
        }

        // Pick frame based on direction
        if (moving) {
            currentFrame = switch (direction) {
                case "left"  -> walkLeftFrame;
                case "right" -> walkRightFrame;
                case "up" -> behindFrame;
                default      -> idleFrame;
            };

            gp.playWalkSound();
        } else {
            currentFrame = idleFrame;
            gp.stopWalkSound();
        }
    }

    public void draw(Graphics2D g2) {
        if (currentFrame != null) {
            g2.drawImage(currentFrame, x, y, gp.tileSize * 2, gp.tileSize * 2, null);
        }
    }
}