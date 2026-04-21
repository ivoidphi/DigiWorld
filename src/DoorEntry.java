public class DoorEntry {
    public final int sourceWorld;   // world index this door is in
    public final int doorCol;       // tile column of the door
    public final int doorRow;       // tile row of the door
    public final int destWorld;     // world index to travel to
    public final int spawnCol;      // player spawn tile col in dest world
    public final int spawnRow;      // player spawn tile row in dest world

    public DoorEntry(int sourceWorld, int doorCol, int doorRow,
                     int destWorld, int spawnCol, int spawnRow) {
        this.sourceWorld = sourceWorld;
        this.doorCol     = doorCol;
        this.doorRow     = doorRow;
        this.destWorld   = destWorld;
        this.spawnCol    = spawnCol;
        this.spawnRow    = spawnRow;
    }
}
