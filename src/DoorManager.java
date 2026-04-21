import java.util.ArrayList;
import java.util.List;

/**
 * Manages all door entries across all worlds.
 * Call check() every frame to detect player stepping on a door tile.
 *
 * To add a new house door:
 *   doors.add(new DoorEntry(World.ALPHA_VILLAGE, doorCol, doorRow, World.HOUSE_2, spawnCol, spawnRow));
 */
public class DoorManager {

    private final GamePanel gp;
    private final List<DoorEntry> doors = new ArrayList<>();

    public DoorManager(GamePanel gp) {
        this.gp = gp;
        registerDoors();
    }

    private void registerDoors() {
        // House 1 — exterior door on Alpha Village, interior exit near bottom of House_1
        // Adjust doorCol/doorRow to match where your door tile is on the Alpha Village map
        doors.add(new DoorEntry(
                World.BETA_CITY, 11, 3,   // source: Alpha Village, door at tile (12, 6)
                World.HOUSE_1,       8,  9     // dest: House_1, spawn at tile (8, 9)
        ));

        // House_1 exit — the portal tile (9) in House_1 already handles going back to Alpha Village.
        // If you want a separate door exit instead, add it here:
        // doors.add(new DoorEntry(World.HOUSE_1, exitCol, exitRow, World.ALPHA_VILLAGE, 12, 7));

        // Add more houses here as you build them:
        // doors.add(new DoorEntry(World.BETA_CITY, 5, 3, World.HOUSE_2, 8, 9));
    }

    /**
     * Called every frame during world update.
     * Checks if the player's tile position matches any registered door.
     */
    /**
     * Called from Player.update() with the lookahead rect BEFORE collision is checked.
     * Returns true if a door was triggered (caller should skip movement).
     */
    public boolean checkAt(java.awt.Rectangle nextRect) {
        int currentWorld = gp.worldManager.currentWorldIndex;

        //System.out.println("Current world: " + currentWorld);
       // System.out.println("Player next rect: " + nextRect);

        //Debug to check if Player intersects door tile
        for (DoorEntry door : doors) {
            //System.out.println("Checking door: world=" + door.sourceWorld + " col=" + door.doorCol + " row=" + door.doorRow);
            if (door.sourceWorld != currentWorld) continue;

            java.awt.Rectangle doorRect = new java.awt.Rectangle(
                    door.doorCol * gp.tileSize,
                    door.doorRow * gp.tileSize,
                    gp.tileSize,
                    gp.tileSize
            );

           // System.out.println("Door rect: " + doorRect + " intersects: " + nextRect.intersects(doorRect));

            if (nextRect.intersects(doorRect)) {
                gp.transition.triggerTransition(currentWorld, door.destWorld, door.spawnCol, door.spawnRow);
                return true;
            }
        }
        return false;
    }

    /** Frame-based check using top of player hitbox (for non-blocked doors). */
    public void check() {
        int currentWorld = gp.worldManager.currentWorldIndex;
        java.awt.Rectangle playerRect = gp.player.getCollisionRect();

        for (DoorEntry door : doors) {
            if (door.sourceWorld != currentWorld) continue;

            // Build the door tile's rectangle
            java.awt.Rectangle doorRect = new java.awt.Rectangle(
                    door.doorCol * gp.tileSize,
                    door.doorRow * gp.tileSize,
                    gp.tileSize,
                    gp.tileSize
            );

            if (playerRect.intersects(doorRect)) {
                gp.transition.triggerTransition(currentWorld, door.destWorld, door.spawnCol, door.spawnRow);
                return;
            }
        }
    }

    public List<DoorEntry> getDoors() {
        return doors;
    }
}