/** Level-based stats from design doc. */
public final class StatCalculator {

    private StatCalculator() {}

    public static int maxHp(int baseHp, int level) {
        return ((2 * baseHp) * level / 20) + level + 10;
    }

    public static int stat(int baseStat, int level) {
        return Math.max(1, ((2 * baseStat) * level / 20) + 5);
    }

    /** Exp to reach next level from current level L: floor(5 * L^2 / 2) — doc BaseXP=5 */
    public static int expToNextLevel(int currentLevel) {
        return (5 * currentLevel * currentLevel) / 2;
    }
}
