package net.runelite.client.plugins.microbot.shadeskiller;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.shadeskiller.enums.Shades;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;

@ConfigGroup("ShadesKiller")
public interface ShadesKillerConfig extends Config {


    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "General";
    @ConfigSection(
            name = "Food",
            description = "Food",
            position = 1
    )
    String foodSection = "FOOD";

    @ConfigSection(
            name = "Teleport",
            description = "Teleport",
            position = 2
    )
    String teleportSection = "Teleport";
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "Start at the bank with your equipment on";
    }

    @ConfigItem(
            keyName = "ShadesToKill",
            name = "Shade Type",
            description = "Type of shade to kill",
            position = 1,
            section = generalSection
    )
    default Shades SHADES() {
        return Shades.FIYR;
    }

    @ConfigItem(
            keyName = "UseCoffin",
            name = "Use Coffin?",
            description = "The gold coffin is an item that can store up to 28 shade remains of any different type.",
            position = 2,
            section = generalSection
    )
    default boolean useCoffin() {
        return true;
    }

    @ConfigItem(
            keyName = "Price of items to loot",
            name = "Price of items to loot",
            description = "Price of items to loot comma seperated",
            position = 3,
            section = generalSection
    )
    default int priceOfItemsToLoot()
    {
        return 5000;
    }

    @ConfigItem(
            keyName = "SpecialAttack",
            name = "Special Attack %",
            description = "At what percentage should the bot use special attack?",
            position = 4,
            section = generalSection
    )
    default int specialAttack()
    {
        return 55;
    }

    @ConfigItem(
            keyName = "Food",
            name = "Food",
            description = "Select the type of food to use",
            position = 1,
            section = foodSection
    )
    default Rs2Food food() {
        return Rs2Food.MONKFISH;
    }

    @ConfigItem(
            keyName = "Amount",
            name = "Amount",
            description = "Amount of food to use",
            position = 2,
            section = foodSection
    )
    default int foodAmount() {
        return 6;
    }

    @ConfigItem(
            keyName = "Eat at %",
            name = "Eat at %",
            description = "Eat at specific percentage health.",
            position = 3,
            section = foodSection
    )
    default int eatAt() {
        return 70;
    }

    @ConfigItem(
            keyName = "Hitpoints Tresshold",
            name = "HP % to run away",
            description = "Runs to the bank if a specific health treshhold is reached and the player does not have any food in their inventory.",
            position = 4,
            section = foodSection
    )
    default int hpTreshhold() {
        return 30;
    }

    @ConfigItem(
            keyName = "TeleportToShades",
            name = "Teleport To shades",
            description = "Item used to teleport to shades.",
            position = 0,
            section = teleportSection
    )
    default String teleportItemToShades() {
        return "barrows teleport";
    }

    @ConfigItem(
            keyName = "TeleportToAction",
            name = "Teleport to shades action",
            description = "Action used on the teleport item",
            position = 1,
            section = teleportSection
    )
    default String teleportActionToShades() {
        return "break";
    }

    @ConfigItem(
            keyName = "TeleportBackToBank",
            name = "Teleport item back to bank",
            description = "Item used to teleport back to the bank.",
            position = 2,
            section = teleportSection
    )
    default String teleportItemToBank() {
        return "varrock teleport";
    }

    @ConfigItem(
            keyName = "TeleportToBank",
            name = "Teleport To bank action",
            description = "Action used on the teleport item",
            position = 3,
            section = teleportSection
    )
    default String teleportActionToBank() {
        return "break";
    }
}
