import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TileManager {

    private final GamePanel gp;
    private final BufferedImage[] tileImages;
    public boolean[] solidTiles = new boolean[10];

    public int[][] map = new int[0][0];

    private static final String[] TILE_PATHS = {
            "res/tiles/dirt.png", //1
            "res/tiles/tempblack.png", //2
            "res/tiles/tempgray.png", //3
            "res/tiles/tempgreen.png", //4
            "res/tiles/tempblue.png", //5
            "res/tiles/templime.png", //6
            "res/tiles/tempmagenta.png", //7
            "res/tiles/temporange.png", //8
            "res/tiles/tempwhite.png", //9
            "res/tiles/portal.png", //10
    };


    public TileManager(GamePanel gp) {
        this.gp = gp;
        tileImages = new BufferedImage[TILE_PATHS.length];
        loadTiles();
    }

    private void loadTiles() {
        for (int i = 0; i < TILE_PATHS.length; i++) {
            try {
                tileImages[i] = ImageIO.read(new File(TILE_PATHS[i]));
            } catch (IOException e) {
                System.out.println("Could not load tile: " + TILE_PATHS[i]);
            }
        }
        solidTiles[0] = false;
        solidTiles[1] = false;
        solidTiles[2] = false;
        solidTiles[3] = false;
        solidTiles[4] = true;
        solidTiles[5] = false;
        solidTiles[6] = false;
        solidTiles[7] = false;
        solidTiles[8] = false;
        solidTiles[9] = false;
    }

    public boolean isSolid(int pixelX, int pixelY) {
        int col = pixelX / gp.tileSize;
        int row = pixelY / gp.tileSize;
        if (row < 0 || row >= map.length)    return true;
        if (col < 0 || col >= map[0].length) return true;
        return solidTiles[map[row][col]];
    }

    public void draw(Graphics2D g2) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                int x = col * gp.tileSize;
                int y = row * gp.tileSize;
                g2.drawImage(tileImages[map[row][col]], x, y, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}