package net.runelite.client.plugins.microbot.crafting.jewelry;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.crafting.jewelry.enums.*;

@ConfigGroup(JewelryConfig.configGroup)
@ConfigInformation(
        "• This plugin is an all-in-one jewelry crafting plugin. <br />" +
        "• Select the jewelry you would like to craft in the config <br />" +
        "• Prepare bank with all required items & runes <br />"
)
public interface JewelryConfig extends Config {
    
    String configGroup = "micro-jewelry";
    String jewelry = "jewelry";
    String craftingLocation = "craftingLocation";
    String completionAction = "completionAction";
    String staff = "staff";
    String useRunePouch = "useRunePouch";

    @ConfigSection(
            name = "General",
            description = "Configure general settings for the plugin",
            position = 0
    )
    String generalSection = "general";

    @ConfigSection(
            name = "Magic",
            description = "Configure settings for magic",
            position = 1
    )
    String magicSection = "magic";

    @ConfigItem(
            keyName = jewelry,
            name = "Item",
            description = "Chose the jewelry item you would like to craft",
            position = 0,
            section = generalSection
    )
    default Jewelry jewelry() { return Jewelry.GOLD_RING; }

    @ConfigItem(
            keyName = craftingLocation,
            name = "Location",
            description = "Choose location to craft jewelry",
            position = 1,
            section = generalSection
    )
    default CraftingLocation craftingLocation() { return CraftingLocation.EDGEVILLE; }

    @ConfigItem(
            keyName = completionAction,
            name = "Completion Action",
            description = "Choose action to perform once all items have been prepared",
            position = 2,
            section = generalSection
    )
    default CompletionAction completionAction() { return CompletionAction.NONE; }

    @ConfigItem(
            keyName = staff,
            name = "Staff",
            description = "Choose the staff that will be used in-order to cast",
            position = 0,
            section = magicSection
    )
    default Staff staff() { return Staff.NONE; }

    @ConfigItem(
            keyName = useRunePouch,
            name = "Use RunePouch",
            description = "Should withdraw & check runes in the rune pouch (must be loaded)",
            position = 0,
            section = magicSection
    )
    default boolean useRunePouch() { return true; }
}
