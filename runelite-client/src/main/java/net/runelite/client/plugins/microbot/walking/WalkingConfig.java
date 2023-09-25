package net.runelite.client.plugins.microbot.walking;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMode;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;

@ConfigGroup("walking")
public interface WalkingConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Locations",
            name = "Locations",
            description = "Choose your location",
            position = 0,
            section = generalSection
    )
    default BankLocation locations()
    {
        return BankLocation.GRAND_EXCHANGE;
    }
}
