
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Structure {

    public int x, y;           // pixel position (top-left of the base image)
    public int width, height;  // rendered size of the base image
    public int roofHeight;     // rendered height of the roof tip image
    public int wallOffsetY;    // how many px from top of base image the WALL starts
    public int leftPad, rightPad; // horizontal insets for collision rect

    BufferedImage baseImage;
    BufferedImage roofImage;

    /**
     * @param pixelX      top-left X in world pixels
     * @param pixelY      top-left Y in world pixels (top of base image)
     * @param width       rendered width in pixels
     * @param height      rendered height of the base image in pixels
     * @param wallOffsetY pixels from top of base image where the wall section begins
     *                    (the roof-baked-in portion height)
     * @param basePath    path to base image
     * @param roofPath    path to roof tip image (null if none)
     * @param roofHeight  rendered height of the roof tip image
     */
    public Structure(int pixelX, int pixelY, int width, int height, int wallOffsetY,
                     int leftPad, int rightPad,
                     String basePath, String roofPath, int roofHeight) {
        this.x = pixelX;
        this.y = pixelY;
        this.width = width;
        this.height = height;
        this.wallOffsetY = wallOffsetY;
        this.leftPad = leftPad;
        this.rightPad = rightPad;
        this.roofHeight = roofHeight;

        try {
            baseImage = ImageIO.read(new File(basePath));
        } catch (IOException e) {
            System.out.println("Could not load structure base: " + basePath);
        }
        if (roofPath != null) {
            try {
                roofImage = ImageIO.read(new File(roofPath));
            } catch (IOException e) {
                System.out.println("Could not load structure roof: " + roofPath);
            }
        }
    }

    public void drawBase(Graphics2D g2) {
        if (baseImage != null)
            g2.drawImage(baseImage, x, y, width, height, null);
    }

    public void drawRoof(Graphics2D g2) {
        if (roofImage != null)
            // roof tip sits directly above the base image
            g2.drawImage(roofImage, x, y - roofHeight, width, roofHeight, null);
    }

    /**
     * The Y position where the wall section begins in world pixels.
     * Used for depth sorting: if the player's Y is above this, the roof overlays them.
     */
    public int wallStartY() {
        return y + wallOffsetY;
    }

    /**
     * Solid collision rect = only the wall/door section (below the baked roof pixels).
     */
    public Rectangle getCollisionRect() {
        return new Rectangle(x + this.leftPad, y + wallOffsetY, width - leftPad - this.rightPad, height - wallOffsetY);
    }
}