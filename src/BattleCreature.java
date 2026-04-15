import java.util.Arrays;

/**
 * Runtime Mecha Beast in battle: level, HP, energy, skill cooldowns, and combat status.
 */
public class BattleCreature {

    private final MechaBeastTemplate template;
    private String nickname;
    private int level;
    private int currentExp;

    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int speed;

    private int maxEnergy;
    private int currentEnergy;
    private int energyRegenPerTurn;

    private final int[] cooldownRemaining = new int[3];

    public final CombatStatus status = new CombatStatus();

    public BattleCreature(MechaBeastTemplate template, int level, String nickname) {
        this.template = template;
        this.nickname = nickname != null ? nickname : template.name;
        this.level = Math.max(1, level);
        recalcStats();
        this.currentHp = maxHp;
        this.currentEnergy = maxEnergy;
    }

    public void recalcStats() {
        maxHp = StatCalculator.maxHp(template.baseHp, level);
        attack = StatCalculator.stat(template.baseAttack, level);
        defense = StatCalculator.stat(template.baseDefense, level);
        speed = StatCalculator.stat(template.baseSpeed, level);
        maxEnergy = template.baseEnergy;
        energyRegenPerTurn = template.energyRegenPerTurn;
        if (currentHp > maxHp) currentHp = maxHp;
        if (currentEnergy > maxEnergy) currentEnergy = maxEnergy;
    }

    public MechaBeastTemplate getTemplate() {
        return template;
    }

    public MechaBeastId getId() {
        return template.id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public int expNeededForNextLevel() {
        return StatCalculator.expToNextLevel(level);
    }

    public void addExperience(int amount) {
        if (amount <= 0) return;
        currentExp += amount;
        while (currentExp >= expNeededForNextLevel()) {
            currentExp -= expNeededForNextLevel();
            level++;
            int oldMax = maxHp;
            recalcStats();
            int gained = maxHp - oldMax;
            currentHp = Math.min(maxHp, currentHp + gained);
        }
    }

    public ElementType getPrimaryType() {
        return template.primaryType;
    }

    public ElementType getSecondaryType() {
        return null;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getAttack() {
        return (int) Math.round(attack * status.attackMultiplier);
    }

    public int getDefense() {
        return (int) Math.round(defense * status.defenseMultiplier);
    }

    public int getRawAttack() {
        return attack;
    }

    public int getRawDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public int getEnergyRegenPerTurn() {
        return energyRegenPerTurn;
    }

    public BattleSkillDefinition getSkill(int index) {
        return template.skills[index];
    }

    public int getCooldown(int index) {
        return cooldownRemaining[index];
    }

    public boolean canUseSkill(int index) {
        BattleSkillDefinition s = template.skills[index];
        return cooldownRemaining[index] == 0 && currentEnergy >= s.energyCost;
    }

    public void paySkillCost(int index) {
        BattleSkillDefinition s = template.skills[index];
        currentEnergy -= s.energyCost;
        if (currentEnergy < 0) currentEnergy = 0;
        if (s.cooldownTurns > 0) {
            cooldownRemaining[index] = s.cooldownTurns;
        }
    }

    public void tickCooldowns() {
        for (int i = 0; i < 3; i++) {
            if (cooldownRemaining[i] > 0) cooldownRemaining[i]--;
        }
    }

    public void regenerateEnergy() {
        currentEnergy = Math.min(maxEnergy, currentEnergy + energyRegenPerTurn);
    }

    public void healPercentOfMax(double pct) {
        int add = (int) (maxHp * pct);
        currentHp = Math.min(maxHp, currentHp + add);
    }

    public boolean isFainted() {
        return currentHp <= 0;
    }

    public void applyDamage(int amount) {
        if (amount <= 0) return;
        currentHp = Math.max(0, currentHp - amount);
    }

    public void healFully() {
        currentHp = maxHp;
        currentEnergy = maxEnergy;
        Arrays.fill(cooldownRemaining, 0);
        status.burnTurns = 0;
        status.paralyzeTurns = 0;
        status.fearTurns = 0;
        status.attackMultiplier = 1.0;
        status.defenseMultiplier = 1.0;
        status.attackDebuffTurns = 0;
        status.defenseDebuffTurns = 0;
    }

    public boolean hasStab(ElementType moveType) {
        if (moveType == null || moveType == ElementType.NEUTRAL) return false;
        return template.primaryType == moveType;
    }

    public String getHenshinAnnouncement() {
        return template.henshinAnnouncement;
    }

    public static BattleCreature fromId(MechaBeastId id, int level) {
        return new BattleCreature(MechaBeastCatalog.get(id), level, null);
    }
}
