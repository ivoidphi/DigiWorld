/** Base stats and skills for one Mecha Beast species (design doc). */
public final class MechaBeastTemplate {

    public final MechaBeastId id;
    public final String name;
    public final String henshinAnnouncement;
    public final ElementType primaryType;

    public final int baseHp;
    public final int baseAttack;
    public final int baseDefense;
    public final int baseSpeed;
    public final int baseEnergy;
    public final int energyRegenPerTurn;

    public final BattleSkillDefinition[] skills;

    public MechaBeastTemplate(MechaBeastId id, String name, String henshinAnnouncement, ElementType primaryType,
                              int baseHp, int baseAttack, int baseDefense, int baseSpeed,
                              int baseEnergy, int energyRegenPerTurn,
                              BattleSkillDefinition s1, BattleSkillDefinition s2, BattleSkillDefinition s3) {
        this.id = id;
        this.name = name;
        this.henshinAnnouncement = henshinAnnouncement;
        this.primaryType = primaryType;
        this.baseHp = baseHp;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseSpeed = baseSpeed;
        this.baseEnergy = baseEnergy;
        this.energyRegenPerTurn = energyRegenPerTurn;
        this.skills = new BattleSkillDefinition[]{s1, s2, s3};
    }
}
