package net.runelite.client.plugins.microbot.grapefarmer;

import net.runelite.client.config.*;

@ConfigGroup("Info")
@ConfigInformation("<br/>Zamorak's grapes farmer<br/><br/><br/> <br/>Start at the vineyard next to the bank<br/><br/><p>Have the following in your bank:</p>\n" +
        "<ol>\n" +
        "    <li>Grape seeds</li>\n" +
        "    <li>Bologa's Blessings</li>\n" +
        "    <li>Seed dibber</li>\n" +
        "    <li>Spade</li>\n" +
        "    <li>Gardening trowel</li>\n" +
        "    <li>Saltpetre</li>\n" +
        "</ol>")


public interface GrapeFarmerConfig extends Config {
    @ConfigItem(
            keyName = "enableGearing",
            name = "Enable gearing?",
            description = "Enable gearing?",
            position = 1,
            section = settingsSection
    )
    default boolean GEARING() {
        return true;
    }

    @ConfigItem(
            keyName = "farmingOutfit",
            name = "Equip Farmer's outfit?",
            description = "Equip Farmer's outfit?",
            position = 1,
            section = settingsSection
    )
    default boolean FARMING_OUTFIT() {
        return true;
    }

    @ConfigSection(
            name = "Settings",
            description = "Settings",
            position = 1
    )
    String settingsSection = "Settings";
}
