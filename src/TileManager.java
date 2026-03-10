import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TileManager {

    GamePanel gp;

    // Tile images
    BufferedImage[] tileImages;

    // The map — each number = a tile type
    // 0 = grass, 1 = water
    int[][] map = {
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    };

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tileImages = new BufferedImage[10];
        loadTiles();
    }

    private void loadTiles() {
        try {
            tileImages[0] = ImageIO.read(new File("res/tiles/dirt.png"));
            tileImages[1] = ImageIO.read(new File("res/tiles/tempblack.png"));
            tileImages[2] = ImageIO.read(new File("res/tiles/tempgray.png"));
            tileImages[3] = ImageIO.read(new File("res/tiles/tempgreen.png"));
            tileImages[4] = ImageIO.read(new File("res/tiles/tempblue.png"));
            tileImages[5] = ImageIO.read(new File("res/tiles/templime.png"));
            tileImages[6] = ImageIO.read(new File("res/tiles/tempmagenta.png"));
            tileImages[7] = ImageIO.read(new File("res/tiles/temporange.png"));
            tileImages[8] = ImageIO.read(new File("res/tiles/tempwhite.png"));
            tileImages[9] = ImageIO.read(new File("res/tiles/portal.png"));
        } catch (IOException e) {
            System.out.println("Could not load tiles!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {

                int tileType = map[row][col];
                int x = col * gp.tileSize;
                int y = row * gp.tileSize;

                g2.drawImage(tileImages[tileType], x, y, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}