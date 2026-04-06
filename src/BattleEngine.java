import java.util.Random;

/**
 * Damage (design doc), crit, STAB, type chart, run odds, catch rate.
 */
public final class BattleEngine {

    public static final double BASE_CRIT_CHANCE = 0.25;
    public static final double CRIT_MULTIPLIER = 1.5;

    private final Random random;

    public BattleEngine() {
        this(new Random());
    }

    public BattleEngine(Random random) {
        this.random = random;
    }

    /**
     * Doc-style: (((2L/5)+2) * Power * (Atk/Def) / 50 + 2) * modifiers.
     */
    public int computeDamage(BattleCreature attacker, BattleCreature defender,
                             BattleSkillDefinition skill, double powerMultiplier,
                             double critChance) {
        int atk = attacker.getAttack();
        int def = Math.max(1, defender.getDefense());
        int level = attacker.getLevel();
        int power = (int) (skill.power * powerMultiplier);

        double base = (2.0 * level / 5.0 + 2) * power * atk / (double) def / 50.0 + 2;

        double stab = attacker.hasStab(skill.element) ? 1.5 : 1.0;
        double type = typeMultiplier(skill.element, defender);

        boolean crit = random.nextDouble() < critChance;
        double critM = crit ? CRIT_MULTIPLIER : 1.0;
        double rand = 0.85 + random.nextDouble() * 0.15;

        return Math.max(1, (int) Math.floor(base * stab * type * critM * rand));
    }

    public double typeMultiplier(ElementType moveElement, BattleCreature defender) {
        if (moveElement == null || moveElement == ElementType.NEUTRAL) return 1.0;
        double e = moveElement.multiplierAgainst(defender.getPrimaryType());
        if (defender.getSecondaryType() != null) {
            e *= moveElement.multiplierAgainst(defender.getSecondaryType());
        }
        return e;
    }

    public double critChanceForSkill(BattleSkillDefinition skill) {
        return switch (skill.special) {
            case CRIT_BONUS_45 -> 0.45;
            case CRIT_BONUS_55 -> Math.min(0.95, BASE_CRIT_CHANCE + 0.55);
            case CRIT_BONUS_20 -> Math.min(0.95, BASE_CRIT_CHANCE + 0.20);
            default -> BASE_CRIT_CHANCE;
        };
    }

    public int aftershockDamage(BattleCreature target, boolean guaranteed) {
        if (!guaranteed && random.nextDouble() > 0.30) return 0;
        return Math.max(1, target.getMaxHp() / 10);
    }

    public int aftershockDamageGuaranteed(BattleCreature target) {
        return Math.max(1, target.getMaxHp() / 10);
    }

    public boolean rollParalyze(int chancePercent) {
        return random.nextDouble() * 100 < chancePercent;
    }

    public boolean rollBurn(int chancePercent) {
        return random.nextDouble() * 100 < chancePercent;
    }

    public boolean rollFear(int chancePercent) {
        return random.nextDouble() * 100 < chancePercent;
    }

    /** Wild run: lower level/speed reduces odds. Trainer battles cannot run. */
    public boolean attemptWildRun(BattleCreature player, BattleCreature wild) {
        if (player.getSpeed() >= wild.getSpeed()) return random.nextDouble() < 0.9;
        int a = player.getSpeed();
        int b = wild.getSpeed();
        int odds = (int) Math.floor(256.0 * a / b) + 30;
        return random.nextInt(256) < Math.min(odds, 255);
    }

    /**
     * Beast Card catch — simplified from doc: lower HP = easier; scales 0.1–0.9.
     */
    public boolean attemptCatch(BattleCreature target) {
        double hpRatio = target.getCurrentHp() / (double) Math.max(1, target.getMaxHp());
        double chance = (1.0 - hpRatio) * 0.65 + 0.12;
        return random.nextDouble() < chance;
    }

    public boolean goesFirst(BattleCreature a, BattleCreature b) {
        if (a.getSpeed() != b.getSpeed()) return a.getSpeed() > b.getSpeed();
        return random.nextBoolean();
    }

    public Random getRandom() {
        return random;
    }
}
