package net.runelite.client.plugins.microbot.mining.shootingstar;

import net.runelite.client.config.*;

@ConfigGroup(ShootingStarConfig.configGroup)
@ConfigInformation(
        "• This plugin will assist in finding & traveling to shooting stars. <br />" +
        "• Configure inventory setup name or the plugin will look for best owned pickaxe <br />" +
        "• Start the plugin it will configure the inventory, if needed, then travel based on configuration settings <br />"
)
public interface ShootingStarConfig extends Config {
    
    String configGroup = "shooting-star";
    String displayAsMinutes = "displayAsMinutes";
    String hideF2PWorlds = "hideF2PWorlds";
    String hideMembersWorlds = "hideMembersWorlds";
    String hideWildernessLocations = "hideWildernessLocations";
    String useNearestHighTierStar = "useNearestHighTierStar";
    String useBreakAtBank = "useBreakAtBank";
    String hideOverlay = "hideOverlay";
    String hideDevOverlay = "hideDevOverlay";

    @ConfigSection(
            name = "General Settings",
            description = "Configure general plugin configuration & preferences",
            position = 0
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
            position = 1,
            section = generalSection
    )
    default boolean useNearestHighTierStar() {
        return false;
    }

    @ConfigItem(
            keyName = useBreakAtBank,
            name = "Use Break at Bank",
            description = "Toggles breaks at the bank - when enabled script will force the player to a bank before triggering the Break Handler",
            position = 2,
            section = generalSection
    )
    default boolean useBreakAtBank() {
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

    @ConfigSection(
            name = "Overlay Settings",
            description = "Configure overlay settings",
            position = 2
    )
    String overlaySection = "overlay";

    @ConfigItem(
            keyName = hideOverlay,
            name = "Hide Overlay",
            description = "Hide overlay",
            position = 0,
            section = overlaySection
    )
    default boolean isHideOverlay() {
        return false;
    }

    @ConfigItem(
            keyName = hideDevOverlay,
            name = "Hide Dev Overlay",
            description = "Hide developer overlay",
            position = 1,
            section = overlaySection
    )
    default boolean isHideDevOverlay() {
        return true;
    }
}
