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

    public void update(KeyHandler key) {
        moving = false;
        List<BufferedImage> newFrames = currentFrames;

        if (key.up) {
            direction = "up";    y -= speed; moving = true; newFrames = framesUp;
        } else if (key.down) {
            direction = "down";  y += speed; moving = true; newFrames = framesDown;
        } else if (key.left) {
            direction = "left";  x -= speed; moving = true; newFrames = framesLeft;
        } else if (key.right) {
            direction = "right"; x += speed; moving = true; newFrames = framesRight;
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
        if (currentFrames != null && !currentFrames.isEmpty()) {
            g2.drawImage(currentFrames.get(frameIndex), x, y, gp.tileSize, gp.tileSize, null);
        }
    }
}