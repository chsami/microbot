package net.runelite.client.plugins.microbot.magic.orbcharger;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.magic.orbcharger.enums.Teleport;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;

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
    String food = "food";
    String eatAtPercent = "eatAtPercent";
    String teleport = "teleport";

    @ConfigSection(
            name = "General Settings",
            description = "Configure general plugin configuration & preferences",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = food,
            name = "Food",
            description = "Select food that should be used when low HP (eats when at bank)",
            position = 0,
            section = generalSection
    )
    default Rs2Food food() {
        return Rs2Food.JUG_OF_WINE;
    }

    @ConfigItem(
            keyName = eatAtPercent,
            name = "Eat At",
            description = "Percent health player should eat at the bank",
            position = 1,
            section = generalSection
    )
    default int eatAtPercent() {
        return 65;
    }

    @ConfigItem(
            keyName = teleport,
            name = "Teleport to use",
            description = "Choose which teleport to use (when using ring of dueling, will drink from refreshment pool)",
            position = 2,
            section = generalSection
    )
    default Teleport teleport() {
        return Teleport.AMULET_OF_GLORY;
    }

    @ConfigItem(
            keyName = useEnergyPotions,
            name = "Use Energy Potions",
            description = "Should withdraw & use energy potions",
            position = 3,
            section = generalSection
    )
    default boolean useEnergyPotions() {
        return false;
    }

    @ConfigItem(
            keyName = useStaminaPotions,
            name = "Use Stamina Potions",
            description = "Should withdraw & use stamina potions",
            position = 4,
            section = generalSection
    )
    default boolean useStaminaPotions() {
        return false;
    }
}
