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

    public int x, y;
    public int speed = 4;

    // Animation
    BufferedImage[] idleFrames;
    BufferedImage[] walkLeftFrames;
    BufferedImage[] walkRightFrames;
    BufferedImage currentFrame;
    int animFrame = 0;
    int animCounter = 0;
    int animDelay = 5;

    String direction = "down";
    String lastDirection = "down"; // tracks direction changes
    boolean moving = false;
    int walkSoundCounter = 0;

    public Player(GamePanel gp) {
        this.gp = gp;
        x = gp.screenWidth / 2 - gp.tileSize / 2;
        y = gp.screenHeight / 2 - gp.tileSize / 2;
        loadSprites();
    }

    private void loadSprites() {
        try {
            idleFrames      = loadAnimatedGif("res/player/kyoflare-idle.gif");
            walkLeftFrames  = loadAnimatedGif("res/player/kyoflare-walkleft.gif");
            walkRightFrames = loadAnimatedGif("res/player/kyoflare-walkright.gif");
            currentFrame = idleFrames[0];
        } catch (Exception e) {
            System.out.println("Could not find GIF!");
            e.printStackTrace();
        }
    }

    private BufferedImage[] loadAnimatedGif(String path) throws IOException {
        ArrayList<BufferedImage> frames = new ArrayList<>();
        File gifFile = new File(path);
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

    // Returns the correct frame array for the current direction
    private BufferedImage[] currentFrames() {
        return switch (direction) {
            case "left"  -> walkLeftFrames;
            case "right" -> walkRightFrames;
            default      -> idleFrames;
        };
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

        // Reset animation when direction changes
        if (!direction.equals(lastDirection)) {
            animFrame = 0;
            animCounter = 0;
            lastDirection = direction;
        }

        if (moving) {
            // Animate walk
            animCounter++;
            if (animCounter >= animDelay) {
                animFrame = (animFrame + 1) % currentFrames().length;
                animCounter = 0;
            }

            // Play walk sound every 10 frames
            walkSoundCounter++;
            if (walkSoundCounter >= 10) {
                gp.playWalkSound();
                walkSoundCounter = 0;
            }

            // Pick walk animation
            switch (direction) {
                case "left"  -> currentFrame = walkLeftFrames[animFrame];
                case "right" -> currentFrame = walkRightFrames[animFrame];
                default      -> currentFrame = idleFrames[animFrame];
            }
        } else {
            walkSoundCounter = 0;
            // Stop walk sound when character stops moving
            gp.stopWalkSound();
            
            // Animate idle
            animCounter++;
            if (animCounter >= animDelay) {
                animFrame = (animFrame + 1) % idleFrames.length;
                animCounter = 0;
            }
            currentFrame = idleFrames[animFrame]; // always idle when stopped
        }
    }
    public void draw(Graphics2D g2) {
        // Draw scaled up 2x (32x32 -> 64x64)
        g2.drawImage(currentFrame, x, y, gp.tileSize * 2, gp.tileSize * 2, null);
    }
}