package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.overlay;

import net.runelite.api.Prayer;
import net.runelite.api.Varbits;

public enum PrayerExtended
{
    /**
     * Thick Skin (Level 1, Defence).
     */
    THICK_SKIN(Varbits.PRAYER_THICK_SKIN, 5.0, 1, WidgetInfoPlus.PRAYER_THICK_SKIN),
    /**
     * Burst of Strength (Level 4, Strength).
     */
    BURST_OF_STRENGTH(Varbits.PRAYER_BURST_OF_STRENGTH, 5.0, 4, WidgetInfoPlus.PRAYER_BURST_OF_STRENGTH),
    /**
     * Clarity of Thought (Level 7, Attack).
     */
    CLARITY_OF_THOUGHT(Varbits.PRAYER_CLARITY_OF_THOUGHT, 5.0, 7, WidgetInfoPlus.PRAYER_CLARITY_OF_THOUGHT),
    /**
     * Sharp Eye (Level 8, Ranging).
     */
    SHARP_EYE(Varbits.PRAYER_SHARP_EYE, 5.0, 8, WidgetInfoPlus.PRAYER_SHARP_EYE),
    /**
     * Mystic Will (Level 9, Magic).
     */
    MYSTIC_WILL(Varbits.PRAYER_MYSTIC_WILL, 5.0, 9, WidgetInfoPlus.PRAYER_MYSTIC_WILL),
    /**
     * Rock Skin (Level 10, Defence).
     */
    ROCK_SKIN(Varbits.PRAYER_ROCK_SKIN, 10.0, 10, WidgetInfoPlus.PRAYER_ROCK_SKIN),
    /**
     * Superhuman Strength (Level 13, Strength).
     */
    SUPERHUMAN_STRENGTH(Varbits.PRAYER_SUPERHUMAN_STRENGTH, 10.0, 13, WidgetInfoPlus.PRAYER_SUPERHUMAN_STRENGTH),
    /**
     * Improved Reflexes (Level 16, Attack).
     */
    IMPROVED_REFLEXES(Varbits.PRAYER_IMPROVED_REFLEXES, 10.0, 16, WidgetInfoPlus.PRAYER_IMPROVED_REFLEXES),
    /**
     * Rapid Restore (Level 19, Stats).
     */
    RAPID_RESTORE(Varbits.PRAYER_RAPID_RESTORE, 60.0 / 36.0, 19, WidgetInfoPlus.PRAYER_RAPID_RESTORE),
    /**
     * Rapid Heal (Level 22, Hitpoints).
     */
    RAPID_HEAL(Varbits.PRAYER_RAPID_HEAL, 60.0 / 18, 22, WidgetInfoPlus.PRAYER_RAPID_HEAL),
    /**
     * Protect Item (Level 25).
     */
    PROTECT_ITEM(Varbits.PRAYER_PROTECT_ITEM, 60.0 / 18, 25, WidgetInfoPlus.PRAYER_PROTECT_ITEM),
    /**
     * Hawk Eye (Level 26, Ranging).
     */
    HAWK_EYE(Varbits.PRAYER_HAWK_EYE, 10.0, 26, WidgetInfoPlus.PRAYER_HAWK_EYE),
    /**
     * Mystic Lore (Level 27, Magic).
     */
    MYSTIC_LORE(Varbits.PRAYER_MYSTIC_LORE, 10.0, 27, WidgetInfoPlus.PRAYER_MYSTIC_LORE),
    /**
     * Steel Skin (Level 28, Defence).
     */
    STEEL_SKIN(Varbits.PRAYER_STEEL_SKIN, 20.0, 28, WidgetInfoPlus.PRAYER_STEEL_SKIN),
    /**
     * Ultimate Strength (Level 31, Strength).
     */
    ULTIMATE_STRENGTH(Varbits.PRAYER_ULTIMATE_STRENGTH, 20.0, 31, WidgetInfoPlus.PRAYER_ULTIMATE_STRENGTH),
    /**
     * Incredible Reflexes (Level 34, Attack).
     */
    INCREDIBLE_REFLEXES(Varbits.PRAYER_INCREDIBLE_REFLEXES, 20.0, 34, WidgetInfoPlus.PRAYER_INCREDIBLE_REFLEXES),
    /**
     * Protect from Magic (Level 37).
     */
    PROTECT_FROM_MAGIC(Varbits.PRAYER_PROTECT_FROM_MAGIC, 20.0, 37, WidgetInfoPlus.PRAYER_PROTECT_FROM_MAGIC),
    /**
     * Protect from Missiles (Level 40).
     */
    PROTECT_FROM_MISSILES(Varbits.PRAYER_PROTECT_FROM_MISSILES, 20.0, 40, WidgetInfoPlus.PRAYER_PROTECT_FROM_MISSILES),
    /**
     * Protect from Melee (Level 43).
     */
    PROTECT_FROM_MELEE(Varbits.PRAYER_PROTECT_FROM_MELEE, 20.0, 43, WidgetInfoPlus.PRAYER_PROTECT_FROM_MELEE),
    /**
     * Eagle Eye (Level 44, Ranging).
     */
    EAGLE_EYE(Varbits.PRAYER_EAGLE_EYE, 20.0, 44, WidgetInfoPlus.PRAYER_EAGLE_EYE),
    /**
     * Mystic Might (Level 45, Magic).
     */
    MYSTIC_MIGHT(Varbits.PRAYER_MYSTIC_MIGHT, 20.0, 45, WidgetInfoPlus.PRAYER_MYSTIC_MIGHT),
    /**
     * Retribution (Level 46).
     */
    RETRIBUTION(Varbits.PRAYER_RETRIBUTION, 5.0, 46, WidgetInfoPlus.PRAYER_RETRIBUTION),
    /**
     * Redemption (Level 49).
     */
    REDEMPTION(Varbits.PRAYER_REDEMPTION, 10.0, 49, WidgetInfoPlus.PRAYER_REDEMPTION),
    /**
     * Smite (Level 52).
     */
    SMITE(Varbits.PRAYER_SMITE, 30.0, 52, WidgetInfoPlus.PRAYER_SMITE),
    /**
     * Chivalry (Level 60, Defence/Strength/Attack).
     */
    CHIVALRY(Varbits.PRAYER_CHIVALRY, 40.0, 60, WidgetInfoPlus.PRAYER_CHIVALRY),
    /**
     * Piety (Level 70, Defence/Strength/Attack).
     */
    PIETY(Varbits.PRAYER_PIETY, 40.0, 70, WidgetInfoPlus.PRAYER_PIETY),
    /**
     * Preserve (Level 55).
     */
    PRESERVE(Varbits.PRAYER_PRESERVE, 60.0 / 18, 55, WidgetInfoPlus.PRAYER_PRESERVE),
    /**
     * Rigour (Level 74, Ranging/Damage/Defence).
     */
    RIGOUR(Varbits.PRAYER_RIGOUR, 40.0, 74, WidgetInfoPlus.PRAYER_RIGOUR),
    /**
     * Augury (Level 77, Magic/Magic Def./Defence).
     */
    AUGURY(Varbits.PRAYER_AUGURY, 40.0, 77, WidgetInfoPlus.PRAYER_AUGURY),

    ;


    private final int varbit;
    private final double drainRate;
    private final int level;
    private final WidgetInfoPlus widgetInfoPlus;

    PrayerExtended(int varbit, double drainRate, int level, WidgetInfoPlus widgetInfoPlus)
    {
        this.varbit = varbit;
        this.drainRate = drainRate;
        this.level = level;
        this.widgetInfoPlus = widgetInfoPlus;
    }

    /**
     * Gets the varbit that stores whether the prayer is active or not.
     *
     * @return the prayer active varbit
     */
    public int getVarbit()
    {
        return varbit;
    }

    /**
     * Gets the prayer drain rate (measured in pray points/minute)
     *
     * @return the prayer drain rate
     */
    public double getDrainRate()
    {
        return drainRate;
    }
    public int getLevel()
    {
        return level;
    }

    public WidgetInfoPlus getWidgetInfoPlus()
    {
        return widgetInfoPlus;
    }

    public static Prayer getPrayer(PrayerExtended prayer)
    {
        return Prayer.valueOf(prayer.name());
    }

    public static int getPrayerWidgetId(Prayer prayer)
    {
        return PrayerExtended.valueOf(prayer.name()).getWidgetInfoPlus().getId();
    }
    public static int getPrayerChildId(Prayer prayer)
    {
        return PrayerExtended.valueOf(prayer.name()).getWidgetInfoPlus().getChildId();
    }
    public static int getPrayerGroupId(Prayer prayer)
    {
        return PrayerExtended.valueOf(prayer.name()).getWidgetInfoPlus().getGroupId();
    }
}