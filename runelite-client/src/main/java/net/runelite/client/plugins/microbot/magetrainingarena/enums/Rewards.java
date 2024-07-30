package net.runelite.client.plugins.microbot.magetrainingarena.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.mta.telekinetic.TelekineticRoom;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum Rewards {
    INFINITY_HAT(ItemID.INFINITY_HAT, Map.of(
            Points.TELEKINETIC, 350,
            Points.GRAVEYARD, 350,
            Points.ENCHANTMENT, 3000,
            Points.ALCHEMIST, 400), null),

    INFINITY_TOP(ItemID.INFINITY_TOP, Map.of(
            Points.TELEKINETIC, 400,
            Points.GRAVEYARD, 400,
            Points.ENCHANTMENT, 4000,
            Points.ALCHEMIST, 450), null),

    INFINITY_BOTTOMS(ItemID.INFINITY_BOTTOMS, Map.of(
            Points.TELEKINETIC, 450,
            Points.GRAVEYARD, 450,
            Points.ENCHANTMENT, 5000,
            Points.ALCHEMIST, 500), null),

    INFINITY_GLOVES(ItemID.INFINITY_GLOVES, Map.of(
            Points.TELEKINETIC, 175,
            Points.GRAVEYARD, 175,
            Points.ENCHANTMENT, 1500,
            Points.ALCHEMIST, 225), null),

    INFINITY_BOOTS(ItemID.INFINITY_BOOTS, Map.of(
            Points.TELEKINETIC, 120,
            Points.GRAVEYARD, 120,
            Points.ENCHANTMENT, 1200,
            Points.ALCHEMIST, 120), null),

    BEGINNER_WAND(ItemID.BEGINNER_WAND, Map.of(
            Points.TELEKINETIC, 30,
            Points.GRAVEYARD, 30,
            Points.ENCHANTMENT, 300,
            Points.ALCHEMIST, 30), null),

    APPRENTICE_WAND(ItemID.APPRENTICE_WAND, Map.of(
            Points.TELEKINETIC, 60,
            Points.GRAVEYARD, 60,
            Points.ENCHANTMENT, 600,
            Points.ALCHEMIST, 60), BEGINNER_WAND),

    TEACHER_WAND(ItemID.TEACHER_WAND, Map.of(
            Points.TELEKINETIC, 150,
            Points.GRAVEYARD, 150,
            Points.ENCHANTMENT, 1500,
            Points.ALCHEMIST, 200), APPRENTICE_WAND),

    MASTER_WAND(ItemID.MASTER_WAND, Map.of(
            Points.TELEKINETIC, 240,
            Points.GRAVEYARD, 240,
            Points.ENCHANTMENT, 2400,
            Points.ALCHEMIST, 240), TEACHER_WAND),

    MAGES_BOOK(ItemID.MAGES_BOOK, Map.of(
            Points.TELEKINETIC, 500,
            Points.GRAVEYARD, 500,
            Points.ENCHANTMENT, 6000,
            Points.ALCHEMIST, 550), null),

    BONES_TO_PEACHES(ItemID.BONES_TO_PEACHES, Map.of(
            Points.TELEKINETIC, 200,
            Points.GRAVEYARD, 200,
            Points.ENCHANTMENT, 2000,
            Points.ALCHEMIST, 300), null);

    private final int itemId;
    private final Map<Points, Integer> points;
    private final Rewards previousReward;

    @Override
    public String toString() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
    }
}
