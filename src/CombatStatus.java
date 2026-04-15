/** Per-creature status during battle (burn, paralysis, fear, temporary stat mods). */
public final class CombatStatus {

    public int burnTurns;
    public int paralyzeTurns;
    public int fearTurns;

    public double attackMultiplier = 1.0;
    public int attackDebuffTurns;

    public double defenseMultiplier = 1.0;
    public int defenseDebuffTurns;

    public void clearTurnDebuffs() {
        if (attackDebuffTurns > 0) {
            attackDebuffTurns--;
            if (attackDebuffTurns <= 0) attackMultiplier = 1.0;
        }
        if (defenseDebuffTurns > 0) {
            defenseDebuffTurns--;
            if (defenseDebuffTurns <= 0) defenseMultiplier = 1.0;
        }
    }

    public void applyAttackDebuff(double mult, int turns) {
        attackMultiplier = mult;
        attackDebuffTurns = Math.max(attackDebuffTurns, turns);
    }

    public void applyDefenseDebuff(double mult, int turns) {
        defenseMultiplier = mult;
        defenseDebuffTurns = Math.max(defenseDebuffTurns, turns);
    }

    /** Returns burn DOT damage for this tick (caller applies), then decrements burn. */
    public int tickBurn(int maxHp) {
        if (burnTurns <= 0) return 0;
        burnTurns--;
        return Math.max(1, maxHp / 16);
    }

    public void tickParalyzeAndFear() {
        if (paralyzeTurns > 0) paralyzeTurns--;
        if (fearTurns > 0) fearTurns--;
    }
}
