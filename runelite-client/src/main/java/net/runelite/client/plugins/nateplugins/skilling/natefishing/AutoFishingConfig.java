package net.runelite.client.plugins.nateplugins.skilling.natefishing;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.nateplugins.skilling.natefishing.enums.Fishs;

@ConfigGroup("Fishing")
public interface AutoFishingConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Fish",
            name = "Fish",
            description = "Choose the fish",
            position = 0,
            section = generalSection
    )
    default Fishs Fish()
    {
        return Fishs.SHRIMP;
    }

    @ConfigItem(
            keyName = "UseBank",
            name = "UseBank",
            description = "Use bank and walk back to original location",
            position = 3,
            section = generalSection
    )
    default boolean useBank()
    {
        return false;
    }

    @ConfigItem(
            keyName = "ItemsToBank",
            name = "Items to bank (Comma seperated)",
            description = "Items to bank",
            position = 4
    )
    default String itemsToBank() {
        return "swordfish,lobster,tuna,trout,salmon,shrimp,anchovies,shark,crab,monkfish,angler,eel,clue,casket";
    }

}
