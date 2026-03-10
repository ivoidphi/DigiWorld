public class World {
    public String name;
    public int[][] map;
    public int portalDestination;

    public World(String name, int portalDestination, int[][] map) {
        this.name = name;
        this.portalDestination = portalDestination;
        this.map = map;
    }
}