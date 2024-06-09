package net.runelite.client.plugins.microbot.blackjack;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.blackjack.enums.Thugs;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;

@ConfigGroup("BlackJack")
public interface BlackJackConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "General";
    @ConfigSection(
            name = "System",
            description = "System",
            position = 1
    )
    String systemSection = "System";
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "                   !W A R N I N G!\n" +
                "It is HIGHLY recommended to babysit\n" +
                "this script! Ban rates at blackjack\n" +
                "are higher! it's an activity with\n" +
                "a high volume of clicks, and Players\n" +
                "regularly walk by, either trying to talk\n" +
                "or opening the curtain to bot check.\n" +
                "best to start at thugs, with\n" +
                "noted wines coins, teleport to bank,\n" +
                "unnoted wines, wearing a blackjack.";
    }
    @ConfigItem(
            keyName = "soundHitSplats",
            name = "Make noise for hitsplats?",
            description = "toggles noise for hitsplats",
            position = 1,
            section = generalSection
    )
    default boolean soundHitSplats() {
        return true;
    }
    @ConfigItem(
            keyName = "ThugsToPickpocket",
            name = "Thug Type",
            description = "Type of thug to pickpocket",
            position = 2,
            section = generalSection
    )
    default Thugs THUGS() {
        return Thugs.MENAPHITE;
    }
    @ConfigItem(
            keyName = "Eat at",
            name = "Eat at",
            description = "Eat at specific health.",
            position = 3,
            section = generalSection
    )
    default int healAt() {
        return 70;
    }
    @ConfigItem(
            keyName = "wearTeleportItem",
            name = "Equip the teleport Item?",
            description = "Does the player wear the teleport item?",
            position = 4,
            section = generalSection
    )
    default boolean wearTeleportItem() {
        return false;
    }
    @ConfigItem(
            keyName = "TeleportBackToBank",
            name = "Teleport item back to bank",
            description = "Item used to teleport back to the bank.",
            position = 5,
            section = generalSection
    )
    default String teleportItemToBank() {
        return "varrock teleport";
    }
    @ConfigItem(
            keyName = "TeleportToBank",
            name = "Teleport To bank action",
            description = "Action used on the teleport item",
            position = 6,
            section = generalSection
    )
    default String teleportActionToBank() {
        return "break";
    }
    @ConfigItem(
            keyName = "PickpocketMinTime",
            name = "PickpocketMinTime",
            description = "Minimum time allowed before pickpocket can't pick twice.",
            position = 0,
            section = systemSection
    )
    default int minTime() {
        return 200;
    }
    @ConfigItem(
            keyName = "PickpocketMaxTime",
            name = "PickpocketMaxTime",
            description = "Maximum time allowed before pickpocket can't pick twice.",
            position = 1,
            section = systemSection
    )
    default int maxTime() {
        return 365;
    }
    @ConfigItem(
            keyName = "ReactHitMaxTime",
            name = "ReactHitMaxTime",
            description = "Maximum time allowed before reacting to hits fails.",
            position = 2,
            section = systemSection
    )
    default int maxReactTime() {
        return 110;
    }
}
