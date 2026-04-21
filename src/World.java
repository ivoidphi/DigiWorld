public class World {

    // World index constants
    public static final int ALPHA_VILLAGE = 0;
    public static final int BETA_CITY     = 1;
    public static final int MYSTIC_FOREST = 2;
    public static final int HOUSE_1       = 3; // renamed from HOUSE

    // Add new house worlds here as you build them:
    // public static final int HOUSE_2 = 4;

    private final String name;
    private final int portalDestination;
    public int[][] map;

    // Where the player spawns when entering this world via a door or portal
    public final int spawnCol;
    public final int spawnRow;

    /** Constructor with custom spawn position. */
    public World(String name, int portalDestination, int spawnCol, int spawnRow, int[][] map) {
        this.name               = name;
        this.portalDestination  = portalDestination;
        this.spawnCol           = spawnCol;
        this.spawnRow           = spawnRow;
        this.map                = map;
    }

    /** Convenience constructor — spawns at center of screen. */
    public World(String name, int portalDestination, int[][] map) {
        this(name, portalDestination, -1, -1, map);
    }

    public String getName()            { return name; }
    public int getPortalDestination()  { return portalDestination; }

    /** Returns true if this world has a custom spawn defined. */
    public boolean hasCustomSpawn()    { return spawnCol >= 0 && spawnRow >= 0; }
}