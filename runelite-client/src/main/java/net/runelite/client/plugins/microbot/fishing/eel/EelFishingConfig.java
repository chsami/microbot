package net.runelite.client.plugins.microbot.fishing.eel;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.fishing.eel.enums.EelFishingSpot;

@ConfigGroup("eelFishing")
public interface EelFishingConfig extends Config {

    @ConfigSection(
            name = "General",
            description = "General settings for eel fishing",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "fishingSpot",
            name = "Fishing Spot",
            description = "Choose the fishing spot: Infernal or Sacred eels",
            position = 0,
            section = generalSection
    )
    default EelFishingSpot fishingSpot() {
        return EelFishingSpot.INFERNAL_EEL;
    }

    // check box if the user wants to use fast combination
    @ConfigItem(
            keyName = "useFastCombination",
            name = "Use fast combination",
            description = "Use fast combination",
            position = 1,
            section = generalSection
    )
    default boolean useFastCombination() {
        return false;
    }
}