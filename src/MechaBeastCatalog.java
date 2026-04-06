/** All Mecha Beasts from DigiWorld Version 2.1. */
public final class MechaBeastCatalog {

    private MechaBeastCatalog() {}

    private static final MechaBeastTemplate[] ALL = {
            new MechaBeastTemplate(
                    MechaBeastId.KYOFLARE, "Kyoflare",
                    "The supernova tyrant, KYOFLARE!",
                    ElementType.FIRE,
                    95, 120, 70, 95, 110, 14,
                    new BattleSkillDefinition("Kyoryu Tail", ElementType.NEUTRAL, 40, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Blaze Fangs", ElementType.FIRE, 85, 35, 1, SkillSpecial.BURN_10_T3),
                    new BattleSkillDefinition("Solar Burst", ElementType.FIRE, 130, 65, 3, SkillSpecial.BURN_30_T3)
            ),
            new MechaBeastTemplate(
                    MechaBeastId.NOKAMI, "Nokami",
                    "The master of water element, NOKAMI!",
                    ElementType.WATER,
                    100, 95, 70, 115, 100, 15,
                    new BattleSkillDefinition("Swift Strike", ElementType.NEUTRAL, 40, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Aqua Slice", ElementType.WATER, 70, 30, 1, SkillSpecial.CRIT_BONUS_45),
                    new BattleSkillDefinition("Shuriken Wave", ElementType.WATER, 110, 55, 2, SkillSpecial.DOUBLE_STRIKE_HALF)
            ),
            new MechaBeastTemplate(
                    MechaBeastId.VINERATOPS, "Vineratops",
                    "The forest guardian, VINERATOPS!",
                    ElementType.GRASS,
                    140, 85, 125, 50, 120, 16,
                    new BattleSkillDefinition("Horn Charge", ElementType.NEUTRAL, 35, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Horn Vines", ElementType.GRASS, 75, 30, 1, SkillSpecial.HEAL_SELF_10),
                    new BattleSkillDefinition("Horn Bloom", ElementType.GRASS, 115, 60, 3, SkillSpecial.HEAL_SELF_15)
            ),
            new MechaBeastTemplate(
                    MechaBeastId.VOLTCHU, "Voltchu",
                    "The greatest lightning swordsmouse, VOLTCHU!",
                    ElementType.ELECTRIC,
                    100, 100, 65, 120, 100, 15,
                    new BattleSkillDefinition("Quick Draw", ElementType.NEUTRAL, 40, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Electric Slash", ElementType.ELECTRIC, 80, 30, 1, SkillSpecial.ATK_DOWN_20_T1),
                    new BattleSkillDefinition("Lightning Wrath", ElementType.ELECTRIC, 120, 60, 3, SkillSpecial.PARALYZE_30_T2)
            ),
            new MechaBeastTemplate(
                    MechaBeastId.ZYUUGOR, "Zyuugor",
                    "The earthshaking gorilla, Zyuugor!",
                    ElementType.EARTH,
                    150, 65, 130, 45, 130, 18,
                    new BattleSkillDefinition("Kong Fist", ElementType.NEUTRAL, 40, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Mineral Bomb", ElementType.EARTH, 80, 35, 1, SkillSpecial.AFTERSHOCK_30),
                    new BattleSkillDefinition("Colossus Breaker", ElementType.EARTH, 125, 70, 3, SkillSpecial.AFTERSHOCK_100)
            ),
            new MechaBeastTemplate(
                    MechaBeastId.PIRROT, "Pirrot",
                    "The Bird of the seven seas, Pirrot!",
                    ElementType.WIND,
                    105, 90, 65, 115, 100, 15,
                    new BattleSkillDefinition("Feather Bullet", ElementType.NEUTRAL, 40, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Wing Blade", ElementType.WIND, 80, 30, 1, SkillSpecial.DEF_DOWN_20_T1),
                    new BattleSkillDefinition("Tempest Slash", ElementType.WIND, 115, 55, 2, SkillSpecial.ATK_DOWN_50_T1)
            ),
            new MechaBeastTemplate(
                    MechaBeastId.GEKUMA, "Gekuma",
                    "The master of Bear God Fist, Gekuma!",
                    ElementType.FIGHTING,
                    110, 105, 85, 80, 110, 15,
                    new BattleSkillDefinition("Bear Claw", ElementType.NEUTRAL, 40, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Power Jab", ElementType.FIGHTING, 85, 35, 1, SkillSpecial.DEF_DOWN_20_T1),
                    new BattleSkillDefinition("Primal Strike", ElementType.FIGHTING, 120, 60, 3, SkillSpecial.MULTI_EXTRA_3_20)
            ),
            new MechaBeastTemplate(
                    MechaBeastId.SHADEFOX, "Shadefox",
                    "The shadowy phantom thief, SHADEFOX!",
                    ElementType.DARK,
                    95, 115, 60, 110, 100, 14,
                    new BattleSkillDefinition("Sneak Bite", ElementType.NEUTRAL, 40, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Crescent Claw", ElementType.DARK, 90, 35, 1, SkillSpecial.CRIT_BONUS_20),
                    new BattleSkillDefinition("Phantom Slash", ElementType.DARK, 125, 65, 2, SkillSpecial.FEAR_50_T1)
            ),
            new MechaBeastTemplate(
                    MechaBeastId.KINGMANTIS, "Kingmantis",
                    "The king of blades, KINGMANTIS!",
                    ElementType.STEEL,
                    140, 150, 65, 90, 140, 18,
                    new BattleSkillDefinition("Iron Jab", ElementType.NEUTRAL, 50, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Twin Dagger", ElementType.STEEL, 95, 35, 1, SkillSpecial.TWIN_STRIKE_50),
                    new BattleSkillDefinition("Sovereign Blade", ElementType.STEEL, 145, 70, 2, SkillSpecial.CRIT_BONUS_55)
            ),
            new MechaBeastTemplate(
                    MechaBeastId.WOLTRIX, "Woltrix",
                    "The incomplete lightning savage, WOLTRIX!",
                    ElementType.ELECTRIC,
                    150, 160, 130, 140, 180, 25,
                    new BattleSkillDefinition("Iron Fang", ElementType.NEUTRAL, 55, 0, 0, SkillSpecial.NONE),
                    new BattleSkillDefinition("Volt Saber", ElementType.ELECTRIC, 105, 45, 1, SkillSpecial.PARALYZE_40_T2),
                    new BattleSkillDefinition("Thunder Barrage", ElementType.ELECTRIC, 165, 90, 3, SkillSpecial.THUNDER_BARRAGE)
            ),
    };

    public static MechaBeastTemplate get(MechaBeastId id) {
        for (MechaBeastTemplate t : ALL) {
            if (t.id == id) return t;
        }
        throw new IllegalArgumentException("Unknown beast: " + id);
    }

    public static MechaBeastTemplate[] allPlayable() {
        MechaBeastTemplate[] out = new MechaBeastTemplate[ALL.length - 1];
        int j = 0;
        for (MechaBeastTemplate t : ALL) {
            if (t.id != MechaBeastId.WOLTRIX) out[j++] = t;
        }
        return out;
    }

    public static MechaBeastTemplate[] all() {
        return ALL.clone();
    }
}
