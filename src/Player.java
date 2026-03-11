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

        int nextX = x;
        int nextY = y;

        if (key.up)         { nextY -= speed; moving = true; newFrames = framesUp; }
        else if (key.down)  { nextY += speed; moving = true; newFrames = framesDown; }
        else if (key.left)  { nextX -= speed; moving = true; newFrames = framesLeft; }
        else if (key.right) { nextX += speed; moving = true; newFrames = framesRight; }

        // Check all 4 corners of the player hitbox before moving
        if (moving && !isCollidingAt(nextX, nextY)) {
            x = nextX;
            y = nextY;
        } else {
            moving = false;
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
            currentFrames = framesDown;
            gp.stopWalkSound();
        }

        clampToBounds();
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