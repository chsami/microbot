package net.runelite.client.plugins.microbot.thieving;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.thieving.enums.ThievingNpc;
import net.runelite.client.plugins.microbot.util.misc.Food;

@ConfigGroup("Thieving")
public interface ThievingConfig extends Config {

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1
    )
    default String GUIDE() {
        return "Start near any of the npc\n" +
                "Script default food is monkfish\n" +
                "Script will walk to bank if out of food\n"+
                "Script supports dodgy necklace\n"+
                "Use Open CoinPouch Helper";
    }
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Npc",
            name = "Npc",
            description = "Choose the npc to start thieving from",
            position = 0,
            section = generalSection
    )
    default ThievingNpc THIEVING_NPC()
    {
        return ThievingNpc.NONE;
    }

    String foodAndHitpoints = "FOOD & Hitpoints";

    @ConfigItem(
            keyName = "Hitpoints",
            name = "Hitpoints treshhold",
            description = "Use food at certain hitpoint treshhold",
            position = 1,
            section = foodAndHitpoints
    )
    default int hitpoints()
    {
        return 20;
    }

    @ConfigItem(
            keyName = "Food",
            name = "Food",
            description = "type of food",
            position = 2,
            section = foodAndHitpoints
    )
    default Food food()
    {
        return Food.MONKFISH;
    }


    String coinPouchSection = "COIN POUCH";

    @ConfigItem(
            keyName = "Coin Pouch TreshHold",
            name = "How many coinpouches in your inventory before opening?",
            description = "How many coinpouches do you need in your inventory before opening them?",
            position = 1,
            section = coinPouchSection
    )
    default int coinPouchTreshHold()
    {
        return 28;
    }

}
