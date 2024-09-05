package net.runelite.client.plugins.microbot.mining.shootingstar;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(ShootingStarConfig.configGroup)
public interface ShootingStarConfig extends Config {
    
    String configGroup = "shooting-star";
    String displayAsMinutes = "displayAsMinutes";
    String displayMembersWorlds = "displayMembersWorlds";
    String displayWildernessLocations = "displayWildernessLocations";
    String useNearestHighTierStar = "useNearestHighTierStar";

    @ConfigSection(
            name = "",
            description = "",
            position = 0
    )
    String guideSection = "guide";

    @ConfigItem(
            keyName = "guide",
            name = "Guide",
            description = "",
            position = 0,
            section = guideSection
    )
    default String guide() {
        return "This plugin will assist in finding & traveling to shooting stars.\n" +
                "Start this plugin in any state & it will setup inventory, then travel based on configuration settings";
    }

    @ConfigSection(
            name = "General Settings",
            description = "Configure general plugin configuration & preferences",
            position = 1
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "inventorySetupName",
            name = "MInventorySetup Name",
            description = "Name of MInventory Setup for mining ",
            position = 0,
            section = generalSection
    )
    default String inventorySetupName() {
        return "Shooting Star";
    }

    @ConfigItem(
            keyName = useNearestHighTierStar,
            name = "Use Nearest High Tier Star",
            description = "The star with the highest tier the is closet to you will be selected",
            position = 2,
            section = generalSection
    )
    default boolean useNearestHighTierStar() {
        return false;
    }

    @ConfigSection(
            name = "Panel Settings",
            description = "Configure view settings within the panel",
            position = 1
    )
    String panelSection = "panel";

    @ConfigItem(
            keyName = displayMembersWorlds,
            name = "Display Members Worlds",
            description = "Shows Members worlds inside of the panel",
            position = 0,
            section = panelSection
    )
    default boolean isDisplayMembersWorlds() {
        return true;
    }

    @ConfigItem(
            keyName = displayWildernessLocations,
            name = "Display Wilderness Locations",
            description = "Shows Members worlds inside of the panel",
            position = 1,
            section = panelSection
    )
    default boolean isDisplayWildernessLocations() {
        return false;
    }

    @ConfigItem(
            keyName = displayAsMinutes,
            name = "Display as Minutes",
            description = "Shows time left as minutes",
            position = 2,
            section = panelSection
    )
    default boolean isDisplayAsMinutes() {
        return false;
    }


}
