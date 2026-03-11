public class World {

    public static final int ALPHA_VILLAGE = 0;
    public static final int BETA_CITY     = 1;
    public static final int MYSTIC_FOREST = 2;
    public static final int HOUSE = 3;

    private final String name;
    private final int portalDestination;
    public int[][] map;

    public World(String name, int portalDestination, int[][] map) {
        this.name = name;
        this.portalDestination = portalDestination;
        this.map = map;
    }

    public String getName() { return name; }
    public int getPortalDestination() { return portalDestination; }
}