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
    private final int frameDelay = 20;

    // Collision box constants (used for NPC + structure collision)
    private static final int CB_X_OFF = 8;
    private static final int CB_Y_OFF = 24;
    private static final int CB_W     = 48;
    private static final int CB_H     = 42;

    private List<BufferedImage> framesDown, framesLeft, framesRight, framesUp, framesIdle;
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
        framesDown  = loadGIF("res/player/v3CharacterDown.gif");
        framesLeft  = loadGIF("res/player/v3CharacterLeft.gif");
        framesRight = loadGIF("res/player/v3CharacterRight.gif");
        framesUp    = loadGIF("res/player/v3CharacterUp.gif");
        framesIdle  = loadGIF("res/player/v3CharacterIdle.gif");
        currentFrames = framesIdle;
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
        } catch (IOException e) { System.out.println("Could not load GIF: " + path); }
        return frames;
    }

    public Rectangle getCollisionRect() {
        return new Rectangle(x + CB_X_OFF, y + CB_Y_OFF, CB_W, CB_H);
    }

    private Rectangle getCollisionRect(int dx, int dy) {
        return new Rectangle(x + CB_X_OFF + dx, y + CB_Y_OFF + dy, CB_W, CB_H);
    }

    public void update(KeyHandler key) {
        moving = false;
        List<BufferedImage> newFrames = currentFrames;

        int dx = 0, dy = 0;
        if (key.up)    { dy -= speed; newFrames = framesUp; }
        if (key.down)  { dy += speed; newFrames = framesDown; }
        if (key.left)  { dx -= speed; newFrames = framesLeft; }
        if (key.right) { dx += speed; newFrames = framesRight; }

        if (dx != 0 || dy != 0) {
            moving = true;
            Rectangle next = getCollisionRect(dx, dy);

            // Check tile collision + structure collision + NPC collision
            if (!isCollidingAt(x + dx, y + dy)
                    && !gp.structureManager.collidesWithAny(next)
                    && !collidesWithNPCs(next)) {
                x += dx;
                y += dy;
            } else {
                // Slide assist: try each axis independently
                if (dy != 0 && dx == 0) {
                    int slide = getSlideDirX(dy);
                    Rectangle slideNext = getCollisionRect(slide, dy);
                    if (slide != 0 && !isCollidingAt(x + slide, y + dy)
                            && !gp.structureManager.collidesWithAny(slideNext)
                            && !collidesWithNPCs(slideNext)) {
                        x += slide; y += dy;
                        newFrames = slide < 0 ? framesLeft : framesRight;
                    } else { moving = false; }
                } else if (dx != 0 && dy == 0) {
                    int slide = getSlideDirY(dx);
                    Rectangle slideNext = getCollisionRect(dx, slide);
                    if (slide != 0 && !isCollidingAt(x + dx, y + slide)
                            && !gp.structureManager.collidesWithAny(slideNext)
                            && !collidesWithNPCs(slideNext)) {
                        x += dx; y += slide;
                        newFrames = slide < 0 ? framesUp : framesDown;
                    } else { moving = false; }
                } else {
                    // Diagonal: try each axis alone
                    Rectangle xNext = getCollisionRect(dx, 0);
                    Rectangle yNext = getCollisionRect(0, dy);
                    if (!isCollidingAt(x + dx, y) && !gp.structureManager.collidesWithAny(xNext) && !collidesWithNPCs(xNext))
                        x += dx;
                    else if (!isCollidingAt(x, y + dy) && !gp.structureManager.collidesWithAny(yNext) && !collidesWithNPCs(yNext))
                        y += dy;
                    else moving = false;
                }
            }
        }

        if (!moving) newFrames = framesIdle;

        if (newFrames != currentFrames) {
            currentFrames = newFrames;
            frameIndex = 0;
            frameTimer = 0;
        }

        frameTimer++;
        if (frameTimer >= frameDelay && !currentFrames.isEmpty()) {
            frameTimer = 0;
            frameIndex = (frameIndex + 1) % currentFrames.size();
        }

        if (moving) gp.playWalkSound(); else gp.stopWalkSound();
        clampToBounds();
    }

    /** Tile-based collision (from Branch 2's isSolid). */
    private boolean isCollidingAt(int nx, int ny) {
        int size = gp.tileSize - 1;
        return gp.tileManager.isSolid(nx, ny)
                || gp.tileManager.isSolid(nx + size, ny)
                || gp.tileManager.isSolid(nx, ny + size)
                || gp.tileManager.isSolid(nx + size, ny + size);
    }

    /** NPC collision check (from Branch 1). */
    private boolean collidesWithNPCs(Rectangle rect) {
        for (NPC npc : gp.npcs)
            if (npc.getCollisionRect().intersects(rect)) return true;
        return false;
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

    private void clampToBounds() {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > gp.screenWidth  - gp.tileSize) x = gp.screenWidth  - gp.tileSize;
        if (y > gp.screenHeight - gp.tileSize) y = gp.screenHeight - gp.tileSize;
    }

    public void draw(Graphics2D g2) {
        if (currentFrames != null && !currentFrames.isEmpty())
            g2.drawImage(currentFrames.get(frameIndex), x, y, gp.tileSize, gp.tileSize, null);

        // Uncomment to debug collision box:
        // g2.setColor(Color.RED);
        // g2.draw(getCollisionRect());
    }
}