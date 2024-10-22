package net.runelite.client.plugins.microbot.scurrius;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;

@ConfigGroup("Scurrius")
public interface ScurriusConfig extends Config {

    @ConfigItem(
            keyName = "bossRoomEntryType",
            name = "Public or Private",
            description = "Select whether to enter through Private or Normal entrance",
            position = 0
    )
    default BossRoomEntryType bossRoomEntryType() {
        return BossRoomEntryType.PRIVATE;
    }

    @ConfigItem(
            keyName = "foodSelection",
            name = "Select Food",
            description = "Select the type of food you want to use",
            position = 1
    )
    default Rs2Food foodSelection() {
        return Rs2Food.SHARK;  // Default to Shark as an example
    }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Food Amount",
            description = "Number of food items to withdraw",
            position = 2
    )
    @Range(
            min = 1,
            max = 28
    )
    default int foodAmount() {
        return 20;
    }
    @ConfigItem(
            keyName = "minEatPercent",
            name = "Min Eat Percent",
            description = "The minimum health percent at which to eat",
            position = 3
    )
    @Range(
            min = 1,
            max = 99
    )
    default int minEatPercent() {
        return 30;
    }

    @ConfigItem(
            keyName = "maxEatPercent",
            name = "Max Eat Percent",
            description = "The maximum health percent at which to eat",
            position = 4
    )
    @Range(
            min = 2,
            max = 100
    )
    default int maxEatPercent() {
        return 50;
    }

    @ConfigItem(
            keyName = "potionSelection",
            name = "Select Prayer Potion",
            description = "Select whether to use Prayer Potion or Super Restore",
            position = 5
    )
    default PotionSelection potionSelection() {
        return PotionSelection.PRAYERPOTION;
    }

    @ConfigItem(
            keyName = "prayerPotionAmount",
            name = "Prayer Potion Amount",
            description = "Number of prayer potions to withdraw",
            position = 6
    )
    @Range(
            min = 1,
            max = 28
    )
    default int prayerPotionAmount() {
        return 2;
    }

    // Min Prayer percent to drink potion
    @ConfigItem(
            keyName = "minPrayerPercent",
            name = "Min Prayer Percent",
            description = "The minimum prayer percent at which to drink a prayer potion",
            position = 7
    )
    @Range(
            min = 1,
            max = 99
    )
    default int minPrayerPercent() {
        return 10;
    }

    // Max Prayer percent to drink potion
    @ConfigItem(
            keyName = "maxPrayerPercent",
            name = "Max Prayer Percent",
            description = "The maximum prayer percent at which to drink a prayer potion",
            position = 8
    )
    @Range(
            min = 2,
            max = 100
    )
    default int maxPrayerPercent() {
        return 30;
    }

    @ConfigItem(
            keyName = "Prioritize Giant rats",
            name = "Prioritize",
            description = "Prioritize Giant rats",
            position = 9
    )
    default boolean prioritizeRats() {
        return false;
    }



    @Getter
    enum PotionSelection {
        PRAYERPOTION(ItemID.PRAYER_POTION4),
        SUPERRESTORE(ItemID.SUPER_RESTORE4);

        private final int itemId;

        PotionSelection(int itemId) {
            this.itemId = itemId;
        }

    }

    @Getter
    enum BossRoomEntryType {
        PRIVATE("Climb-through (private)"),
        NORMAL("Climb-through (normal)");

        private final String interactionText;

        BossRoomEntryType(String interactionText) {
            this.interactionText = interactionText;
        }

    }

}
