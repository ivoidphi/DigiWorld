

public class WorldManager {

    GamePanel gp;
    public int currentWorldIndex = 0;
    World[] worlds;

    public WorldManager(GamePanel gp) {
        this.gp = gp;
        worlds = new World[]{

                new World("Alpha Village", 1, new int[][]{
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                        {1,9,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                        {1,1,1,1,2,2,2,2,1,1,1,1,1,1,1,1},
                        {1,1,1,1,3,3,3,3,1,1,1,1,1,1,1,1},
                        {1,1,1,7,4,0,0,4,7,1,1,1,1,1,1,1},
                        {1,1,1,6,0,0,0,0,6,1,1,1,1,1,1,1},
                        {1,1,1,5,0,0,0,0,5,1,1,1,1,1,1,1},
                        {1,1,1,7,4,0,0,4,7,1,1,1,1,1,1,1},
                        {1,1,1,1,3,3,3,3,1,1,1,1,1,1,1,1},
                        {1,1,1,1,2,2,2,2,1,1,1,1,1,1,1,1},
                        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                        {1,1,1,1,1,8,1,1,1,1,1,1,1,1,1,1},
                }),

                new World("Beta City", 2, new int[][]{
                        {13,11,14,14,13,11,12,16,17,13,11,11,12,11,14,12},
                        {13,9,14,12,12,12,14,16,17,11,11,12,13,14,12,13},
                        {14,12,13,11,12,13,12,16,17,12,12,12,12,14,14,11},
                        {11,12,12,14,11,12,14,16,17,12,12,23,14,12,12,12},
                        {14,11,12,14,12,13,13,16,17,12,11,23,13,14,14,13},
                        {15,15,15,15,15,15,15,21,20,15,15,15,15,15,15,15},
                        {18,18,18,18,18,18,18,19,22,18,18,18,18,18,18,18},
                        {13,11,13,14,11,14,14,16,17,14,12,13,12,14,13,13},
                        {11,12,14,14,14,11,11,16,17,13,12,13,11,12,12,14},
                        {14,14,11,11,13,13,14,16,17,12,12,11,11,14,14,12},
                        {14,14,11,12,12,12,14,16,17,14,11,23,11,11,11,14},
                        {14,13,13,14,12,14,13,16,17,11,11,23,12,13,11,12},
                }),

                new World("Mystic Forest", 0, new int[][]{ // loops back to world 0
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
                }),
        };
    }

    public void loadWorld(int index) {
        gp.tileManager.map = worlds[index].map;
        currentWorldIndex = index;
        gp.tileManager.map = worlds[index].map;

        gp.structureManager.clear(); // clear previous world's structures

        // Place houses per world
        if (index == 0) { // Alpha Village
            gp.structureManager.placeHouse1(10, 2);

            // add more: gp.structureManager.placeHouse1(9, 5);
        }
        if (index == 1) { // Beta City
            gp.structureManager.placeHouse1(9, 1);
            gp.structureManager.placeLab(9,8 );
        }
    }

    public World getCurrentWorld() {
        return worlds[currentWorldIndex];
    }

    public void checkPortal() {
        int playerCol = gp.player.x / gp.tileSize;
        int playerRow = gp.player.y / gp.tileSize;
        if (playerCol == 1 && playerRow == 0) {
            gp.transition.triggerTransition(getCurrentWorld().portalDestination);
        }
    }

    public String getCurrentWorldName() {
        return getCurrentWorld().name;
    }

}