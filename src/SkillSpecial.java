/** Extra behavior on skills (burn, multi-hit, stat drops, etc.). */
public enum SkillSpecial {
    NONE,

    BURN_10_T3,
    BURN_30_T3,

    /** +45% crit chance for this move (Aqua Slice) */
    CRIT_BONUS_45,

    /** 50% chance to strike again for half damage (Shuriken Wave) */
    DOUBLE_STRIKE_HALF,

    HEAL_SELF_10,
    HEAL_SELF_15,

    ATK_DOWN_20_T1,
    DEF_DOWN_20_T1,
    ATK_DOWN_50_T1,

    PARALYZE_30_T2,
    PARALYZE_40_T2,

    FEAR_50_T1,

    AFTERSHOCK_30,
    AFTERSHOCK_100,

    /** Gekuma: three extra hits at 20% power */
    MULTI_EXTRA_3_20,

    /** Woltrix: up to 5 hits, first full, follow-ups 30% */
    THUNDER_BARRAGE,

    /** Kingmantis: two hits, second 50% */
    TWIN_STRIKE_50,

    /** +55% crit on Sovereign Blade */
    CRIT_BONUS_55,

    /** Crescent Claw: +20% crit */
    CRIT_BONUS_20
}
