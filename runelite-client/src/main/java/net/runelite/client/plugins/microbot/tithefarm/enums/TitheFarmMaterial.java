package net.runelite.client.plugins.microbot.tithefarm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

@Getter
@RequiredArgsConstructor
public enum TitheFarmMaterial {
    GOLOVANOVA_SEED("Golovanova seed", 34, '1', ItemID.GOLOVANOVA_FRUIT),
    BOLOGANO_SEED("Bologano seed", 54, '2', ItemID.BOLOGANO_FRUIT),
    LOGAVANO_SEED("Logavano seed", 74, '3', ItemID.LOGAVANO_FRUIT);

    final String name;
    final int levelRequired;
    final char option;
    final int fruitId;

    public static TitheFarmMaterial getSeedForLevel() {
        if (Microbot.getClient().getRealSkillLevel(Skill.FARMING) >= LOGAVANO_SEED.levelRequired)
            return LOGAVANO_SEED;
        if (Microbot.getClient().getRealSkillLevel(Skill.FARMING) >= BOLOGANO_SEED.levelRequired)
            return BOLOGANO_SEED;
        if (Microbot.getClient().getRealSkillLevel(Skill.FARMING) >= GOLOVANOVA_SEED.levelRequired)
            return GOLOVANOVA_SEED;

        return LOGAVANO_SEED;
    }

    public static boolean hasWateringCanToBeFilled() {
        return Rs2Inventory.hasItem(ItemID.WATERING_CAN7) || Rs2Inventory.hasItem(ItemID.WATERING_CAN6)
                || Rs2Inventory.hasItem(ItemID.WATERING_CAN5) || Rs2Inventory.hasItem(ItemID.WATERING_CAN4)
                || Rs2Inventory.hasItem(ItemID.WATERING_CAN3) || Rs2Inventory.hasItem(ItemID.WATERING_CAN2)
                || Rs2Inventory.hasItem(ItemID.WATERING_CAN1) || Rs2Inventory.hasItem(ItemID.WATERING_CAN);
    }

    public static boolean hasGricollersCan() {
        return Rs2Inventory.hasItem(ItemID.GRICOLLERS_CAN);
    }

    public static int getWateringCanToBeFilled() {
        if (Rs2Inventory.hasItem(ItemID.WATERING_CAN7)) {
            return ItemID.WATERING_CAN7;
        }
        if (Rs2Inventory.hasItem(ItemID.WATERING_CAN6)) {
            return ItemID.WATERING_CAN6;
        }
        if (Rs2Inventory.hasItem(ItemID.WATERING_CAN5)) {
            return ItemID.WATERING_CAN5;
        }
        if (Rs2Inventory.hasItem(ItemID.WATERING_CAN4)) {
            return ItemID.WATERING_CAN4;
        }
        if (Rs2Inventory.hasItem(ItemID.WATERING_CAN3)) {
            return ItemID.WATERING_CAN3;
        }
        if (Rs2Inventory.hasItem(ItemID.WATERING_CAN2)) {
            return ItemID.WATERING_CAN2;
        }
        if (Rs2Inventory.hasItem(ItemID.WATERING_CAN1)) {
            return ItemID.WATERING_CAN1;
        }
        if (Rs2Inventory.hasItem(ItemID.WATERING_CAN)) {
            return ItemID.WATERING_CAN;
        }
        return -1;
    }

    @Override
    public String toString() {
        return name;
    }
}
