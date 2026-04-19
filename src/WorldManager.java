public class WorldManager {

    private final GamePanel gp;
    private final World[] worlds;
    public int currentWorldIndex = 0;

    private static final int PORTAL_TILE = 9;

    public WorldManager(GamePanel gp) {
        this.gp = gp;
        worlds = new World[]{

                new World("Alpha Village", World.BETA_CITY, new int[][]{
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

                new World("Beta City", World.MYSTIC_FOREST, new int[][]{
                        {13,11,14,14,13,11,12,16,17,13,11,11,12,11,14,12},
                        {13,9, 14,12,12,12,14,16,17,11,11,12,13,14,12,13},
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

                new World("Mystic Forest", World.HOUSE, new int[][]{
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

                new World("House", World.ALPHA_VILLAGE, new int[][]{
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,4,0,0,0,0,0,0,0,3,0,0,0},
                        {0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                }),
        };
        loadWorld(World.ALPHA_VILLAGE);
    }

    public void loadWorld(int index) {
        currentWorldIndex = index;
        gp.tileManager.map = worlds[index].map;

        // Reload NPCs for this world (Branch 1)
        gp.npcs = Characters.loadAll(gp, index);

        // Clear and place structures for this world (Branch 1)
        gp.structureManager.clear();
        if (index == World.ALPHA_VILLAGE) {
            gp.structureManager.placeHouse1(10, 2);
        }
        if (index == World.BETA_CITY) {
            gp.structureManager.placeHouse1(9, 1);
            gp.structureManager.placeLab(9, 8);
        }
    }

    public World getCurrentWorld() { return worlds[currentWorldIndex]; }
    public String getCurrentWorldName() { return getCurrentWorld().getName(); }

    public void checkPortal() {
        int col = gp.player.x / gp.tileSize;
        int row = gp.player.y / gp.tileSize;
        int[][] map = getCurrentWorld().map;
        if (row < 0 || row >= map.length)    return;
        if (col < 0 || col >= map[0].length) return;
        if (map[row][col] == PORTAL_TILE) {
            int from = currentWorldIndex;
            int to   = getCurrentWorld().getPortalDestination();
            if (!gp.getGameState().canUsePortal(from, to)) return;
            gp.transition.triggerTransition(from, to);
        }
    }
}