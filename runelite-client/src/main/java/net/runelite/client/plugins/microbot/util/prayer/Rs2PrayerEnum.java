package net.runelite.client.plugins.microbot.util.prayer;

import net.runelite.api.Varbits;
import net.runelite.api.annotations.Varbit;

public enum Rs2PrayerEnum {
    THICK_SKIN("Thick Skin", 35454985, 1, Varbits.PRAYER_THICK_SKIN, 0),
    BURST_STRENGTH("Burst of Strength", 35454986, 4, Varbits.PRAYER_BURST_OF_STRENGTH, 1),
    CLARITY_THOUGHT("Clarity of Thought", 35454987, 7, Varbits.PRAYER_CLARITY_OF_THOUGHT, 2),
    SHARP_EYE("Sharp Eye", 35455003, 8, Varbits.PRAYER_SHARP_EYE, 3),
    MYSTIC_WILL("Mystic Will", 35455006, 9, Varbits.PRAYER_MYSTIC_WILL, 19),
    ROCK_SKIN("Rock Skin", 35454988, 10, Varbits.PRAYER_ROCK_SKIN, 3),
    SUPERHUMAN_STRENGTH("Superhuman Strength", 35454989, 13, Varbits.PRAYER_SUPERHUMAN_STRENGTH, 4),
    IMPROVED_REFLEXES("Improved Reflexes", 35454990, 16, Varbits.PRAYER_IMPROVED_REFLEXES, 5),
    RAPID_RESTORE("Rapid Restore", 35454991, 19, Varbits.PRAYER_RAPID_RESTORE, 6),
    RAPID_HEAL("Rapid heal", 35454992, 22, Varbits.PRAYER_RAPID_HEAL, 7),
    PROTECT_ITEM("Protect Item", 35454993, 25, Varbits.PRAYER_PROTECT_ITEM, 8),
    HAWK_EYE("Hawk eye", 35455004, 26, Varbits.PRAYER_HAWK_EYE, 20),
    MYSTIC_LORE("Mystic Lore", 35455007, 27, Varbits.PRAYER_MYSTIC_LORE, 21),
    STEEL_SKIN("Steel Skin", 35454994, 28, Varbits.PRAYER_STEEL_SKIN, 9),
    ULTIMATE_STRENGTH("Ultimate Strength", 35454995, 31, Varbits.PRAYER_ULTIMATE_STRENGTH, 10),
    INCREDIBLE_REFLEXES("Incredible Reflexes", 35454996, 34, Varbits.PRAYER_INCREDIBLE_REFLEXES, 11),
    PROTECT_MAGIC("Protect From Magic", 35454997, 37, Varbits.PRAYER_PROTECT_FROM_MAGIC, 12),
    PROTECT_RANGE("Protect From Missiles", 35454998, 40, Varbits.PRAYER_PROTECT_FROM_MISSILES, 13),
    PROTECT_MELEE("Protect From Melee", 35454999, 43, Varbits.PRAYER_PROTECT_FROM_MELEE, 14),
    EAGLE_EYE("Eagle Eye", 35455005, 44, Varbits.PRAYER_EAGLE_EYE, 22),
    MYSTIC_MIGHT("Mystic Might", 35455008, 45, Varbits.PRAYER_MYSTIC_MIGHT, 23),
    RETRIBUTION("Retribution", 35455000, 46, Varbits.PRAYER_RETRIBUTION, 15),
    REDEMPTION("Redemption", 35455001, 49, Varbits.PRAYER_REDEMPTION, 16),
    SMITE("Smite", 35455002, 52, Varbits.PRAYER_SMITE, 17),
    PRESERVE("Preserve", 35455013, 55, Varbits.PRAYER_PRESERVE, 28),
    CHIVALRY("Chivalry", 35455010,60, Varbits.PRAYER_CHIVALRY, 25),
    PIETY("Piety", 35455011, 70, Varbits.PRAYER_PIETY, 26),
    RIGOUR("Rigour", 35455009, 74, Varbits.PRAYER_RIGOUR, 24),
    AUGURY("Augury", 35455012, 77, Varbits.PRAYER_AUGURY, 27);

    private final String name;
    private final int index;
    private final int level;

    private final int pVar;

    private final int quickPrayerIndex;

    Rs2PrayerEnum(String name, int index, int level, int pVar, int quickPrayerIndex){
        this.name = name;
        this.index = index;
        this.level = level;
        this.pVar = pVar;
        this.quickPrayerIndex = quickPrayerIndex;
    }


    public String getName(){
        return name;
    }
    public int getIndex(){
        return index;
    }
    public int getLevel(){
        return level;
    }

    public int getQuickPrayerIndex(){ return quickPrayerIndex; }
    public @Varbit int getVarbit(){
        return pVar;
    }

}
