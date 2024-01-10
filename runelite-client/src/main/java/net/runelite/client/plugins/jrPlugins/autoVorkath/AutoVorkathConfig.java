/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package net.runelite.client.plugins.jrPlugins.autoVorkath;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.jrPlugins.autoVorkath.enums.*;

import java.awt.*;

@ConfigGroup("AutoVorkath")
public interface AutoVorkathConfig extends Config {

    @ConfigItem(
            keyName = "crossbow",
            name = "Crossbow",
            description = "Choose your crossbow",
            position = 0
    )
    default CROSSBOW CROSSBOW() {
        return CROSSBOW.DRAGON_HUNTER_CROSSBOW;
    }

    @ConfigItem(
            keyName = "slayersStaff",
            name = "Slayers Staff",
            description = "Choose your slayers staff",
            position = 1
    )
    default STAFF SLAYERSTAFF() {
        return STAFF.SLAYER_STAFF;
    }

    @ConfigItem(
            keyName = "teleport",
            name = "Teleport",
            description = "Choose your teleport",
            position = 2
    )
    default TELEPORT TELEPORT() {
        return TELEPORT.CONSTRUCT_CAPE_T;
    }

    @ConfigItem(
            keyName = "portal",
            name = "Portal",
            description = "What Portal to use to teleport to Lunar Isle.",
            position = 3
    )
    default PORTAL PORTAL() {
        return PORTAL.PORTAL_NEXUS;
    }

    @ConfigItem(
            keyName = "rigour",
            name = "Rigour",
            description = "Activate Rigour?",
            position = 4
    )
    default boolean ACTIVATERIGOUR() {
        return true;
    }

    @ConfigItem(
            keyName = "rangePotion",
            name = "Ranging Potion",
            description = "What Ranging potion to use?",
            position = 5
    )
    default RANGE_POTION RANGEPOTION() {
        return RANGE_POTION.DIVINE_RANGING_POTION;
    }

    @ConfigItem(
            keyName = "prayerPotion",
            name = "Prayer Potion",
            description = "What Prayer potion to use?",
            position = 6
    )
    default PRAYER_POTION PRAYERPOTION() {
        return PRAYER_POTION.PRAYER;
    }

    @ConfigItem(
            keyName = "antiFirePotion",
            name = "Antifire Potion",
            description = "What Antifire potion to use?",
            position = 7
    )
    default ANTIFIRE ANTIFIRE() {
        return ANTIFIRE.EXTENDED_SUPER_ANTIFIRE;
    }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Food Amount",
            description = "MAX FOOD : MIN FOOD",
            position = 8
    )
    default Dimension FOODAMOUNT() {
        return new Dimension(15, 15);
    }

    @ConfigItem(
            keyName = "poolDrinkat",
            name = "Ornate Pool Drink",
            description = "HEALTH : PRAYER",
            position = 9
    )
    default Dimension POOLDRINK() {
        return new Dimension(90, 90);
    }

    @ConfigItem(
            keyName = "eatat",
            name = "Eat at",
            description = "Eat at what health?",
            position = 10
    )
    default int EATAT() {
        return 75;
    }

    @ConfigItem(
            keyName = "food",
            name = "Food",
            description = "What food to use? (NOT MANTA RAY!)",
            position = 12
    )
    default String FOOD() {
        return "Shark";
    }

}
