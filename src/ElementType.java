/** Elemental typings from DigiWorld design doc (Strengths / Weaknesses chart). */
public enum ElementType {
    NEUTRAL,
    FIRE,
    WATER,
    GRASS,
    ELECTRIC,
    EARTH,
    WIND,
    FIGHTING,
    DARK,
    STEEL;

    private static final double[][] CHART;

    static {
        int n = values().length;
        CHART = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                CHART[i][j] = (i == 0 || j == 0) ? 1.0 : 1.0;
            }
        }
        set(FIRE, GRASS, 2.0);
        set(FIRE, WIND, 2.0);
        set(FIRE, STEEL, 2.0);
        set(FIRE, WATER, 0.5);
        set(FIRE, EARTH, 0.5);

        set(WATER, FIRE, 2.0);
        set(WATER, EARTH, 2.0);
        set(WATER, GRASS, 0.5);
        set(WATER, ELECTRIC, 0.5);

        set(GRASS, WATER, 2.0);
        set(GRASS, EARTH, 2.0);
        set(GRASS, FIRE, 0.5);
        set(GRASS, WIND, 0.5);

        set(ELECTRIC, WATER, 2.0);
        set(ELECTRIC, WIND, 2.0);
        set(ELECTRIC, EARTH, 0.5);
        set(ELECTRIC, STEEL, 0.5);

        set(EARTH, ELECTRIC, 2.0);
        set(EARTH, STEEL, 2.0);
        set(EARTH, FIRE, 2.0);
        set(EARTH, WATER, 0.5);
        set(EARTH, GRASS, 0.5);
        set(EARTH, DARK, 0.5);

        set(WIND, GRASS, 2.0);
        set(WIND, FIGHTING, 2.0);
        set(WIND, FIRE, 0.5);
        set(WIND, ELECTRIC, 0.5);

        set(FIGHTING, DARK, 2.0);
        set(FIGHTING, STEEL, 2.0);
        set(FIGHTING, WIND, 0.5);

        set(DARK, EARTH, 2.0);
        set(DARK, WIND, 2.0);
        set(DARK, FIGHTING, 0.5);
        set(DARK, STEEL, 0.5);

        set(STEEL, DARK, 2.0);
        set(STEEL, ELECTRIC, 2.0);
        set(STEEL, FIRE, 0.5);
        set(STEEL, EARTH, 0.5);
    }

    private static void set(ElementType atk, ElementType def, double mult) {
        CHART[atk.ordinal()][def.ordinal()] = mult;
    }

    /** Multiplier when a move of this element hits a defender (single type). */
    public double multiplierAgainst(ElementType defending) {
        if (this == NEUTRAL || defending == null || defending == NEUTRAL) return 1.0;
        return CHART[ordinal()][defending.ordinal()];
    }

    public String displayName() {
        return switch (this) {
            case NEUTRAL -> "Neutral";
            case FIRE -> "Fire";
            case WATER -> "Water";
            case GRASS -> "Grass";
            case ELECTRIC -> "Electric";
            case EARTH -> "Earth";
            case WIND -> "Wind";
            case FIGHTING -> "Fighting";
            case DARK -> "Dark";
            case STEEL -> "Steel";
        };
    }
}
