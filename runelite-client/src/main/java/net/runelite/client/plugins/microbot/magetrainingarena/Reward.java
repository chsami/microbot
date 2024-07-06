package net.runelite.client.plugins.microbot.magetrainingarena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
@AllArgsConstructor
public enum Reward {
    INFINITY_HAT(ItemID.INFINITY_HAT, 350, 350, 3000, 400, null),
    INFINITY_TOP(ItemID.INFINITY_TOP, 400, 400, 4000, 450, null),
    INFINITY_BOTTOMS(ItemID.INFINITY_BOTTOMS, 450, 450, 5000, 500, null),
    INFINITY_GLOVES(ItemID.INFINITY_GLOVES, 175, 175, 1500, 225, null),
    INFINITY_BOOTS(ItemID.INFINITY_BOOTS, 120, 120, 1200, 120, null),
    BEGINNER_WAND(ItemID.BEGINNER_WAND, 30, 30, 300, 30, null),
    APPRENTICE_WAND(ItemID.APPRENTICE_WAND, 60, 60, 600, 60, BEGINNER_WAND),
    TEACHER_WAND(ItemID.TEACHER_WAND, 150, 150, 1500, 200, APPRENTICE_WAND),
    MASTER_WAND(ItemID.MASTER_WAND, 240, 240, 2400, 240, TEACHER_WAND),
    MAGES_BOOK(ItemID.MAGES_BOOK, 500, 500, 6000, 550, null),
    BONES_TO_PEACHES(ItemID.BONES_TO_PEACHES, 200, 200, 2000, 300, null);

    private final int itemId;
    private final int telekineticPoints;
    private final int graveyardPoints;
    private final int enchantmentPoints;
    private final int alchemistPoints;
    private final Reward previousReward;
}
