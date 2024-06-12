package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query;

public enum QuickPrayer {
    THICK_SKIN(0),
    BURST_OF_STRENGTH(1),
    CLARITY_OF_THOUGHT(2),
    SHARP_EYE(18),
    MYSTIC_WILL(19),
    ROCK_SKIN(3),
    SUPERHUMAN_STRENGTH(4),
    IMPROVED_REFLEXES(5),
    RAPID_RESTORE(6),
    RAPID_HEAL(7),
    PROTECT_ITEM(8),
    HAWK_EYE(20),
    MYSTIC_LORE(21),
    STEEL_SKIN(9),
    ULTIMATE_STRENGTH(10),
    INCREDIBLE_REFLEXES(11),
    PROTECT_FROM_MAGIC(12),
    PROTECT_FROM_MISSILES(13),
    PROTECT_FROM_MELEE(14),
    EAGLE_EYE(22),
    MYSTIC_MIGHT(23),
    RETRIBUTION(15),
    REDEMPTION(16),
    SMITE(17),
    PRESERVE(28),
    CHIVALRY(25),
    PIETY(26),
    RIGOUR(24),
    AUGURY(27);
    private final int index;
    private int varbit;

    QuickPrayer(int index) {
        this.varbit = varbit;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
