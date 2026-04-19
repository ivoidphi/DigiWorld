import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TileManager {

    private final GamePanel gp;
    private final BufferedImage[] tileImages;
    public boolean[] solidTiles;
    public int[][] map = new int[0][0];

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tileImages = new BufferedImage[24];
        solidTiles = new boolean[24];
        loadTiles();
    }

    private void loadTiles() {
        load(0,  "res/tiles/dirt.png");
        load(1,  "res/tiles/tempblack.png");
        load(2,  "res/tiles/tempgray.png");
        load(3,  "res/tiles/tempgreen.png");
        load(4,  "res/tiles/tempblue.png");
        load(5,  "res/tiles/templime.png");
        load(6,  "res/tiles/tempmagenta.png");
        load(7,  "res/tiles/temporange.png");
        load(8,  "res/tiles/tempwhite.png");
        load(9,  "res/tiles/portal.png");
        load(11, "res/tiles/gblade1.png");
        load(12, "res/tiles/Grass1.png");
        load(13, "res/tiles/Grass2.png");
        load(14, "res/tiles/Grass3.png");
        load(15, "res/tiles/DPathTop.png");
        load(16, "res/tiles/DPathLeft.png");
        load(17, "res/tiles/DPathRight.png");
        load(18, "res/tiles/DPathBot.png");
        load(19, "res/tiles/procktleft.png");
        load(20, "res/tiles/procktright.png");
        load(21, "res/tiles/prockbleft.png");
        load(22, "res/tiles/prockbright.png");
        load(23, "res/tiles/Doorstep.png");

        // Set solid tiles — add more IDs here as needed
        // solidTiles[1] = true; // example: tempblack is solid
    }

    private void load(int index, String path) {
        try { tileImages[index] = ImageIO.read(new File(path)); }
        catch (IOException e) { System.out.println("Could not load tile: " + path); }
    }

    /** Used by Player for tile-based collision. */
    public boolean isSolid(int pixelX, int pixelY) {
        int col = pixelX / gp.tileSize;
        int row = pixelY / gp.tileSize;
        if (row < 0 || row >= map.length)    return true;
        if (col < 0 || col >= map[0].length) return true;
        int tileId = map[row][col];
        if (tileId < 0 || tileId >= solidTiles.length) return false;
        return solidTiles[tileId];
    }

    public void draw(Graphics2D g2) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                int id = map[row][col];
                if (id < 0 || id >= tileImages.length || tileImages[id] == null) continue;
                g2.drawImage(tileImages[id], col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}