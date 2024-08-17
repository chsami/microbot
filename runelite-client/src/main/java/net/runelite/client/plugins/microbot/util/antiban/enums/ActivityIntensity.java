package net.runelite.client.plugins.microbot.util.antiban.enums;

import lombok.Getter;
import net.runelite.api.Skill;

/**
 * The ActivityIntensity enum represents different levels of intensity for player activities,
 * each associated with a specific play style and behavior pattern.
 *
 * <p>
 * Activity intensities are used to simulate varying levels of focus and action frequency during bot operation.
 * Each intensity level controls the frequency and amplitude of actions, which corresponds to how aggressive or passive
 * the bot's behavior is during different tasks. The intensity also determines the corresponding play style, which
 * adjusts the bot's overall behavior to better mimic human variability.
 * </p>
 *
 * <h3>Main Features:</h3>
 * <ul>
 *   <li>Intensity Levels: Ranges from "Very Low" to "Extreme," with varying levels of action frequency and amplitude.</li>
 *   <li>Play Style Association: Each intensity is linked to a <code>PlayStyle</code>, which defines the overall behavior
 *   of the bot during specific activities.</li>
 *   <li>Skill Mapping: Different skills are mapped to specific intensity levels, with combat-related skills generally
 *   assigned higher intensities, while skilling activities are mapped to lower intensities.</li>
 *   <li>Randomization: Provides a method to generate a random activity intensity, adding unpredictability to bot behavior.</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <p>
 * The <code>ActivityIntensity</code> enum is used to dynamically adjust the bot's behavior based on the intensity of the
 * player's activity. Higher intensity levels correspond to more aggressive actions, while lower intensity levels simulate
 * slower, more deliberate behavior. This allows the bot to adapt to different types of activities, from combat to skilling.
 * </p>
 *
 * <h3>Example:</h3>
 * <pre>
 * ActivityIntensity currentIntensity = ActivityIntensity.HIGH;
 * PlayStyle associatedStyle = currentIntensity.getPlayStyle();
 * double actionFrequency = currentIntensity.getFrequency();
 * </pre>
 *
 * <h3>Skill-Based Intensity:</h3>
 * <p>
 * The <code>fromSkill(Skill skill)</code> method maps in-game skills to specific activity intensity levels. For example,
 * combat-related skills such as Attack, Defence, and Ranged are mapped to higher intensities, while skills like Cooking,
 * Fishing, and Crafting are mapped to lower intensities. This allows the bot to adjust its behavior based on the current
 * skill being trained.
 * </p>
 *
 * <h3>Random Intensity:</h3>
 * <p>
 * The <code>random()</code> method provides a way to select a random activity intensity, introducing unpredictability
 * and making the bot's actions appear more human-like by varying the intensity over time.
 * </p>
 */

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
