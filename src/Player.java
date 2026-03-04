import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;


public class Player {

    GamePanel gp;

    // Position on screen
    public int x, y;
    public int speed = 4;

    // Animation
    BufferedImage[] idleFrames;
    BufferedImage currentFrame;
    int animFrame = 0;
    int animCounter = 0;
    int animDelay = 15; // frames before switching (lower = faster)

    String direction = "down";
    boolean moving = false;

    public Player(GamePanel gp) {
        this.gp = gp;

        // Start in center of screen
        x = gp.screenWidth / 2 - gp.tileSize / 2;
        y = gp.screenHeight / 2 - gp.tileSize / 2;

        loadSprites();
    }

    private void loadSprites() {
        try {
            idleFrames = loadAnimatedGif("res/player/kyoflare-idle.gif");
            currentFrame = idleFrames[0];
        } catch (Exception e) {
            System.out.println("Could not find GIF!");
            e.printStackTrace();
        }
    }

    // Loads all frames from an animated GIF using direct file path
    private BufferedImage[] loadAnimatedGif(String path) throws IOException {
        ArrayList<BufferedImage> frames = new ArrayList<>();

        File gifFile = new File(path);
        System.out.println("Looking for GIF at: " + gifFile.getAbsolutePath());

        ImageInputStream stream = ImageIO.createImageInputStream(gifFile);

        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
        ImageReader reader = readers.next();
        reader.setInput(stream);

        int numFrames = reader.getNumImages(true);
        for (int i = 0; i < numFrames; i++) {
            frames.add(reader.read(i));
        }

        return frames.toArray(new BufferedImage[0]);
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

        // Always animate idle (even when not moving)
        animCounter++;
        if (animCounter >= animDelay) {
            animFrame = (animFrame + 1) % idleFrames.length;
            animCounter = 0;
        }

        currentFrame = idleFrames[animFrame];
    }

    public void draw(Graphics2D g2) {
        // Draw scaled up 2x so it's visible (32x32 -> 64x64)
        g2.drawImage(currentFrame, x, y, gp.tileSize * 2, gp.tileSize * 2, null);
    }
}