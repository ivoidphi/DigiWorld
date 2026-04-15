/** Immutable skill definition from the Mecha Beast catalog. */
public final class BattleSkillDefinition {

    public final String name;
    public final ElementType element;
    public final int power;
    public final int energyCost;
    public final int cooldownTurns;
    public final SkillSpecial special;

    public BattleSkillDefinition(String name, ElementType element, int power,
                                 int energyCost, int cooldownTurns, SkillSpecial special) {
        this.name = name;
        this.element = element != null ? element : ElementType.NEUTRAL;
        this.power = power;
        this.energyCost = energyCost;
        this.cooldownTurns = cooldownTurns;
        this.special = special != null ? special : SkillSpecial.NONE;
    }
}
