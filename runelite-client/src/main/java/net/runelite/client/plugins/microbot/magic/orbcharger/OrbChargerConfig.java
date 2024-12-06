package net.runelite.client.plugins.microbot.magic.orbcharger;

import net.runelite.client.config.*;

@ConfigGroup(OrbChargerConfig.configGroup)
@ConfigInformation(
        "• This plugin will craft air orbs at the air obelisk. <br />" +
        "• Ensure you have any type of air staff, amulet of glory, unpowered orbs & cosmic runes. <br />" +
        "• You can also wear weight reducing clothing & select if you want to energy restore/stamina potions. <br />" +
        "• Start the plugin it will configure the inventory, if needed, then start running to the air obelisk. <br />"
)
public interface OrbChargerConfig extends Config {
    
    String configGroup = "orb-charger";
    String useEnergyPotions = "useEnergyPotions";
    String useStaminaPotions = "useStaminaPotions";

    @ConfigSection(
            name = "General Settings",
            description = "Configure general plugin configuration & preferences",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = useEnergyPotions,
            name = "Use Energy Potions",
            description = "Should withdraw & use energy potions",
            position = 0,
            section = generalSection
    )
    default boolean useEnergyPotions() {
        return false;
    }

    @ConfigItem(
            keyName = useStaminaPotions,
            name = "Use Stamina Potions",
            description = "Should withdraw & use stamina potions",
            position = 0,
            section = generalSection
    )
    default boolean useStaminaPotions() {
        return false;
    }
}
