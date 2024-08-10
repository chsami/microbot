package net.runelite.client.plugins.microbot.util.antiban.enums;

import lombok.Getter;
import net.runelite.api.Skill;

public enum ActivityIntensity {
    VERY_LOW("Very Low", 0.6, 1.8, PlayStyle.PASSIVE),
    LOW("Low", 0.4, 1.5, PlayStyle.CAUTIOUS),
    MODERATE("Moderate", 0.3, 1.2, PlayStyle.AGGRESSIVE),
    HIGH("High", 0.2, 1.1, PlayStyle.EXTREME_AGGRESSIVE),
    EXTREME("Extreme", 0.1, 1.0, PlayStyle.EXTREME_AGGRESSIVE);

    @Getter
    private final String name;
    @Getter
    private final double frequency;
    @Getter
    private final double amplitude;
    @Getter
    private final PlayStyle playStyle;


    ActivityIntensity(String name, double frequency, double amplitude, PlayStyle playStyle) {
        this.name = name;
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.playStyle = playStyle;
    }

    public static ActivityIntensity random() {
        return values()[(int) (Math.random() * values().length - 1)];
    }

    public static ActivityIntensity fromSkill(Skill skill) {
        switch (skill) {
            case ATTACK:
            case DEFENCE:
            case STRENGTH:
            case RANGED:
            case PRAYER:
            case MAGIC:
            case CONSTRUCTION:
                return HIGH;
            case COOKING:
            case WOODCUTTING:
            case FLETCHING:
            case FISHING:
            case FIREMAKING:
            case CRAFTING:
            case SMITHING:
            case MINING:
            case HERBLORE:
                return LOW;
            case AGILITY:
            case THIEVING:
            case SLAYER:
            case RUNECRAFT:
            case HUNTER:
            case FARMING:
                return MODERATE;
            default:
                return null;
        }
    }
}
