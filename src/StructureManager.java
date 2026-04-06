
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StructureManager {

    GamePanel gp;
    List<Structure> structures = new ArrayList<>();

    // House 1 constants
    private static final int HOUSE1_W        = 320; // rendered width
    private static final int HOUSE1_H        = 192; // rendered base height
    private static final int HOUSE1_WALL_OFF = 42;  // baked roof portion (top 64px)
    private static final int HOUSE1_ROOF_H   = 100;  // roof tip image height
    private static final int HOUSE1_LEFT_PAD  = 10;
    private static final int HOUSE1_RIGHT_PAD = 10;

    private static final int LAB_W        = 320; // rendered width
    private static final int LAB_H        = 200; // rendered base height
    private static final int LAB_WALL_OFF = 56;  // baked roof portion (top 64px)
    private static final int LAB_ROOF_H   = 56;  // roof tip image height
    private static final int LAB_LEFT_PAD  = 5;
    private static final int LAB_RIGHT_PAD = 5;

    public StructureManager(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Place house 1 at the given tile column and row (top-left of base image).
     */
    public void placeHouse1(int tileCol, int tileRow) {
        int px = tileCol * gp.tileSize;
        int py = tileRow * gp.tileSize;
        structures.add(new Structure(
                px, py,
                HOUSE1_W, HOUSE1_H, HOUSE1_WALL_OFF,
                HOUSE1_LEFT_PAD, HOUSE1_RIGHT_PAD,
                "res/Structures/house_finalBot.png",
                "res/Structures/house_finalTop.png",
                HOUSE1_ROOF_H
        ));
    }

    public void placeLab(int tileCol, int tileRow) {
        int px = tileCol * gp.tileSize;
        int py = tileRow * gp.tileSize;
        structures.add(new Structure(
                px, py,
                LAB_W, LAB_H, LAB_WALL_OFF,
                LAB_LEFT_PAD, LAB_RIGHT_PAD,
                "res/Structures/lab_finalBot.png",
                "res/Structures/lab_finalTop.png",
                LAB_ROOF_H
        ));
    }

    /**
     * Generic placer for any future structure.
     */
    public void placeCustom(int tileCol, int tileRow, int w, int h, int wallOffY,
                            int leftPad, int rightPad,
                            String basePath, String roofPath, int roofH) {
        int px = tileCol * gp.tileSize;
        int py = tileRow * gp.tileSize;
        structures.add(new Structure(px, py, w, h, wallOffY, leftPad, rightPad, basePath, roofPath, roofH));
    }

    public void clear() {
        structures.clear();
    }

    /**
     * Draw bases with Y-sorting against the player:
     * - If player is in FRONT of (below) the wall start → base draws BEFORE player
     * - If player is BEHIND (above) the wall start   → base draws AFTER player
     *
     * Call drawBeforePlayer() before player.draw() and
     *      drawAfterPlayer()  after  player.draw().
     */
    public void drawBeforePlayer(Graphics2D g2) {
        int playerFeetY = gp.player.y + gp.tileSize; // bottom of player sprite
        for (Structure s : structures) {
            if (playerFeetY >= s.wallStartY()) // player in front → house behind
                s.drawBase(g2);
        }
    }

    public void drawAfterPlayer(Graphics2D g2) {
        int playerFeetY = gp.player.y + gp.tileSize;
        for (Structure s : structures) {
            if (playerFeetY < s.wallStartY()) // player behind → house in front
                s.drawBase(g2);
        }
        // Roof tip ALWAYS draws over player — separate pass so it's never skipped
        for (Structure s : structures)
            s.drawRoof(g2);
    }

    /**
     * Collision check using lookahead rect.
     */
    public boolean collidesWithAny(Rectangle playerRect) {
        for (Structure s : structures) {
            if (s.getCollisionRect().intersects(playerRect)) return true;
        }
        return false;
    }
}