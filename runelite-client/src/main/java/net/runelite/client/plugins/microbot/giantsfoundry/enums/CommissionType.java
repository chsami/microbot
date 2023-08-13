package net.runelite.client.plugins.microbot.giantsfoundry.enums;

public enum CommissionType {
    NONE,
    NARROW, // 1
    LIGHT, // 2
    FLAT, // 3
    BROAD, // 4
    HEAVY, // 5
    SPIKED, // 6
    ;

    public static final CommissionType[] values = CommissionType.values();

    public static CommissionType forVarbit(int varbitValue) {
        if (varbitValue < 0 || varbitValue >= values.length) {
            return NONE;
        }
        return CommissionType.values[varbitValue];
    }
}
