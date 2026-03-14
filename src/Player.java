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

    private final GamePanel gp;

    public int x, y;
    private final int speed = 4;
    private final int frameDelay = 6;

    private List<BufferedImage> framesDown, framesLeft, framesRight, framesUp;
    private List<BufferedImage> currentFrames;

    private int frameIndex = 0;
    private int frameTimer = 0;
    private boolean moving = false;

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

    public void update(KeyHandler key) {
        moving = false;
        List<BufferedImage> newFrames = currentFrames;

        int dx = 0, dy = 0;

        // Primary direction from keys
        if (key.up)    { dy -= speed; newFrames = framesUp; }
        if (key.down)  { dy += speed; newFrames = framesDown; }
        if (key.left)  { dx -= speed; newFrames = framesLeft; }
        if (key.right) { dx += speed; newFrames = framesRight; }

        if (dx != 0 || dy != 0) moving = true;

        if (moving) {
            if (!isCollidingAt(x + dx, y + dy)) {
                // Free — move normally
                x += dx;
                y += dy;
            } else if (dy != 0 && dx == 0) {
                // Only vertical pressed, try sliding horizontally based on position
                // nudge left or right depending on which side has more room
                int slideDir = getSlideDirX(dy);
                if (slideDir != 0 && !isCollidingAt(x + slideDir, y + dy)) {
                    x += slideDir;
                    y += dy;
                    newFrames = (slideDir < 0) ? framesLeft : framesRight;
                } else if (slideDir != 0 && !isCollidingAt(x + slideDir, y)) {
                    x += slideDir;
                    newFrames = (slideDir < 0) ? framesLeft : framesRight;
                } else {
                    moving = false;
                }
            } else if (dx != 0 && dy == 0) {
                // Only horizontal pressed, try sliding vertically
                int slideDir = getSlideDirY(dx);
                if (slideDir != 0 && !isCollidingAt(x + dx, y + slideDir)) {
                    x += dx;
                    y += slideDir;
                    newFrames = (slideDir < 0) ? framesUp : framesDown;
                } else if (slideDir != 0 && !isCollidingAt(x, y + slideDir)) {
                    y += slideDir;
                    newFrames = (slideDir < 0) ? framesUp : framesDown;
                } else {
                    moving = false;
                }
            } else {
                // Both axes pressed (diagonal), try each axis independently
                if (!isCollidingAt(x + dx, y)) x += dx;
                else if (!isCollidingAt(x, y + dy)) y += dy;
                else moving = false;
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
        }

        clampToBounds();
    }

    private int getSlideDirX(int dy) {
        if (!isCollidingAt(x - speed, y + dy)) return -speed;
        if (!isCollidingAt(x + speed, y + dy)) return  speed;
        return 0;
    }

    private int getSlideDirY(int dx) {
        if (!isCollidingAt(x + dx, y - speed)) return -speed;
        if (!isCollidingAt(x + dx, y + speed)) return  speed;
        return 0;
    }


    private boolean isCollidingAt(int nextX, int nextY) {
        int size = gp.tileSize - 1; // hitbox size matches tile
        return gp.tileManager.isSolid(nextX,        nextY       ) // top-left
                || gp.tileManager.isSolid(nextX + size,  nextY       ) // top-right
                || gp.tileManager.isSolid(nextX,         nextY + size) // bottom-left
                || gp.tileManager.isSolid(nextX + size,  nextY + size); // bottom-right
    }


    private void clampToBounds() {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > gp.screenWidth  - gp.tileSize) x = gp.screenWidth  - gp.tileSize;
        if (y > gp.screenHeight - gp.tileSize) y = gp.screenHeight - gp.tileSize;
    }

    public void draw(Graphics2D g2) {
        if (currentFrames != null && !currentFrames.isEmpty())
            g2.drawImage(currentFrames.get(frameIndex), x, y, gp.tileSize, gp.tileSize, null);
    }
}