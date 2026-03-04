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
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0},
            {0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0},
            {0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    };

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tileImages = new BufferedImage[2]; // 2 tile types for now
        loadTiles();
    }

    private void loadTiles() {
        try {
            tileImages[0] = ImageIO.read(new File("res/tiles/grass.png")); // 0 = grass
            tileImages[1] = ImageIO.read(new File("res/tiles/water.png")); // 1 = water
        } catch (IOException e) {
            System.out.println("Could not load tiles!");
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        for (int row = 0; row < gp.screenRows; row++) {
            for (int col = 0; col < gp.screenCols; col++) {

                int tileType = map[row][col]; // get tile number
                int x = col * gp.tileSize;   // pixel position x
                int y = row * gp.tileSize;   // pixel position y

                g2.drawImage(tileImages[tileType], x, y, gp.tileSize, gp.tileSize, null);
            }
        }
    }
}