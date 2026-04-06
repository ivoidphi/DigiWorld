
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {

    GamePanel gp;

    public int x, y;
    public int speed = 4;

    // Collision box is smaller than the tile so it feels fair
    // Adjust these offsets to match your sprite's feet area
    private static final int CB_X_OFF = 8;   // left offset into tile
    private static final int CB_Y_OFF = 24;  // top offset (pushes box to lower half)
    private static final int CB_W     = 48;  // collision box width
    private static final int CB_H     = 42;  // collision box height

    List<BufferedImage> framesDown, framesLeft, framesRight, framesUp;
    List<BufferedImage> currentFrames;

    int frameIndex = 0;
    int frameTimer = 0;
    int frameDelay = 6;

    String direction = "down";
    boolean moving = false;

    public Player(GamePanel gp) {
        this.gp = gp;
        x = gp.screenWidth  / 2 - gp.tileSize / 2;
        y = gp.screenHeight / 2 - gp.tileSize / 2;
        loadSprites();
    }

    private void loadSprites() {
        framesDown  = loadGIF("res/player/player-walking-down.gif");
        framesLeft  = loadGIF("res/player/player-walking-left.gif");
        framesRight = loadGIF("res/player/player-walking-right.gif");
        framesUp    = loadGIF("res/player/player-walking-up.gif");
        currentFrames = framesDown;
    }

    private List<BufferedImage> loadGIF(String path) {
        List<BufferedImage> frames = new ArrayList<>();
        try {
            ImageInputStream stream = ImageIO.createImageInputStream(new File(path));
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) return frames;
            ImageReader reader = readers.next();
            reader.setInput(stream);
            int count = reader.getNumImages(true);
            for (int i = 0; i < count; i++) frames.add(reader.read(i));
            reader.dispose();
        } catch (IOException e) {
            System.out.println("Could not load GIF: " + path);
        }
        return frames;
    }

    /** Returns the player's collision rectangle at their current position. */
    public Rectangle getCollisionRect() {
        return new Rectangle(x + CB_X_OFF, y + CB_Y_OFF, CB_W, CB_H);
    }

    /** Returns a collision rectangle offset by (dx, dy) — used for lookahead. */
    private Rectangle getCollisionRect(int dx, int dy) {
        return new Rectangle(x + CB_X_OFF + dx, y + CB_Y_OFF + dy, CB_W, CB_H);
    }

    public void update(KeyHandler key) {
        moving = false;
        List<BufferedImage> newFrames = currentFrames;

        int dx = 0, dy = 0;

        if (key.up)         { direction = "up";    dy = -speed; newFrames = framesUp; }
        else if (key.down)  { direction = "down";  dy =  speed; newFrames = framesDown; }
        else if (key.left)  { direction = "left";  dx = -speed; newFrames = framesLeft; }
        else if (key.right) { direction = "right"; dx =  speed; newFrames = framesRight; }

        if (dx != 0 || dy != 0) {
            moving = true;
            // Only move if the destination doesn't collide with any structure
            if (!gp.structureManager.collidesWithAny(getCollisionRect(dx, dy))) {
                x += dx;
                y += dy;
            }
        }

        if (newFrames != currentFrames) {
            currentFrames = newFrames;
            frameIndex = 0;
            frameTimer = 0;
        }

        if (moving) {
            frameTimer++;
            if (frameTimer >= frameDelay) {
                frameTimer = 0;
                frameIndex = (frameIndex + 1) % currentFrames.size();
            }
            gp.playWalkSound();
        } else {
            frameIndex = 0;
            frameTimer = 0;
            gp.stopWalkSound();
            currentFrames = framesDown;
        }
    }

    public void draw(Graphics2D g2) {
        if (currentFrames != null && !currentFrames.isEmpty())
            g2.drawImage(currentFrames.get(frameIndex), x, y, gp.tileSize, gp.tileSize, null);

        // Uncomment to debug collision box:
        g2.setColor(Color.RED);
         g2.draw(getCollisionRect());
    }
}