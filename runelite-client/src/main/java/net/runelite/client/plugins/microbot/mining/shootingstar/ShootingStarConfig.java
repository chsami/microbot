package net.runelite.client.plugins.microbot.mining.shootingstar;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(ShootingStarConfig.configGroup)
public interface ShootingStarConfig extends Config {
    
    String configGroup = "shooting-star";
    String displayAsMinutes = "displayAsMinutes";
    String hideF2PWorlds = "hideF2PWorlds";
    String hideMembersWorlds = "hideMembersWorlds";
    String hideWildernessLocations = "hideWildernessLocations";
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
            description = "Name of mInventory Setup for mining",
            position = 0,
            section = generalSection
    )
    default String inventorySetupName() {
        return "Shooting Star";
    }

    @ConfigItem(
            keyName = useNearestHighTierStar,
            name = "Use Nearest High Tier Star",
            description = "Toggles automatic mode - when enabled script will automatically check all stars within an acceptable tier range & find the closest star that has the highest tier",
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
            keyName = hideMembersWorlds,
            name = "Hide Members Worlds",
            description = "Hide Members worlds inside of the panel",
            position = 0,
            section = panelSection
    )
    default boolean isHideMembersWorlds() {
        return false;
    }

    @ConfigItem(
            keyName = hideF2PWorlds,
            name = "Hide F2P Worlds",
            description = "Hide F2P worlds inside of the panel",
            position = 1,
            section = panelSection
    )
    default boolean isHideF2PWorlds() {
        return false;
    }

    @ConfigItem(
            keyName = hideWildernessLocations,
            name = "Hide Wilderness Locations",
            description = "Hide Wilderness locations inside of the panel",
            position = 2,
            section = panelSection
    )
    default boolean isHideWildernessLocations() {
        return true;
    }

    @ConfigItem(
            keyName = displayAsMinutes,
            name = "Display as Minutes",
            description = "Shows time left as minutes",
            position = 3,
            section = panelSection
    )
    default boolean isDisplayAsMinutes() {
        return false;
    }
}
