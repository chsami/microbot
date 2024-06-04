package net.runelite.client.plugins.forn.birdhouseruns;

import lombok.Getter;
import net.runelite.api.ItemID;

public class FornBirdhouseRunsInfo {
    public static states botStatus;
    public static int selectedSeed;
    public static int seedAmount;
    public static int selectedLogs;
    public static int birdhouseType;

    public enum states {
        GEARING,
        TELEPORTING,
        VERDANT_TELEPORT,
        MUSHROOM_TELEPORT,
        DISMANTLE_HOUSE_1,
        BUILD_HOUSE_1,
        SEED_HOUSE_1,
        DISMANTLE_HOUSE_2,
        BUILD_HOUSE_2,
        SEED_HOUSE_2,
        DISMANTLE_HOUSE_3,
        BUILD_HOUSE_3,
        SEED_HOUSE_3,
        DISMANTLE_HOUSE_4,
        BUILD_HOUSE_4,
        SEED_HOUSE_4,
        FINISHING,
        FINISHED
    }

    @Getter
    public enum seedTypes {
        BARLEY_SEED(ItemID.BARLEY_SEED, 10),
        HAMMERSTONE_SEED(ItemID.HAMMERSTONE_SEED, 10),
        ASGARNIAN_SEED(ItemID.ASGARNIAN_SEED, 10),
        JUTE_SEED(ItemID.JUTE_SEED, 10),
        YANILLIAN_SEED(ItemID.YANILLIAN_SEED, 10),
        KRANDORIAN_SEED(ItemID.KRANDORIAN_SEED, 10),
        WILDBLOOD_SEED(ItemID.WILDBLOOD_SEED, 5),
        GUAM_SEED(ItemID.GUAM_SEED, 10),
        MARRENTILL_SEED(ItemID.MARRENTILL_SEED, 10),
        TARROMIN_SEED(ItemID.TARROMIN_SEED, 10),
        HARRALANDER_SEED(ItemID.HARRALANDER_SEED, 10),
        RANARR_SEED(ItemID.RANARR_SEED, 5),
        TOADFLAX_SEED(ItemID.TOADFLAX_SEED, 5),
        IRIT_SEED(ItemID.IRIT_SEED, 5),
        AVANTOE_SEED(ItemID.AVANTOE_SEED, 5),
        KWUARM_SEED(ItemID.KWUARM_SEED, 5),
        SNAPDRAGON_SEED(ItemID.SNAPDRAGON_SEED, 5),
        CADANTINE_SEED(ItemID.CADANTINE_SEED, 5),
        LANTADYME_SEED(ItemID.LANTADYME_SEED, 5),
        DWARF_WEED_SEED(ItemID.DWARF_WEED_SEED, 5),
        TORSTOL_SEED(ItemID.TORSTOL_SEED, 5),
        MARIGOLD_SEED(ItemID.MARIGOLD_SEED, 10),
        ROSEMARY_SEED(ItemID.ROSEMARY_SEED, 10),
        NASTURTIUM_SEED(ItemID.NASTURTIUM_SEED, 10),
        WOAD_SEED(ItemID.WOAD_SEED, 10),
        LIMPWURT_SEED(ItemID.LIMPWURT_SEED, 10),
        WHITE_LILY_SEED(ItemID.WHITE_LILY_SEED, 10),
        POTATO_SEED(ItemID.POTATO_SEED, 10),
        ONION_SEED(ItemID.ONION_SEED, 10),
        CABBAGE_SEED(ItemID.CABBAGE_SEED, 10),
        TOMATO_SEED(ItemID.TOMATO_SEED, 10),
        SWEETCORN_SEED(ItemID.SWEETCORN_SEED, 10),
        STRAWBERRY_SEED(ItemID.STRAWBERRY_SEED, 10),
        WATERMELON_SEED(ItemID.WATERMELON_SEED, 10),
        SNAPE_GRASS_SEED(ItemID.SNAPE_GRASS_SEED, 10),
        REDBERRY_SEED(ItemID.REDBERRY_SEED, 10),
        POISON_IVY_SEED(ItemID.POISON_IVY_SEED, 10),
        CADAVABERRY_SEED(ItemID.CADAVABERRY_SEED, 10),
        DWELLBERRY_SEED(ItemID.DWELLBERRY_SEED, 10),
        JANGERBERRY_SEED(ItemID.JANGERBERRY_SEED, 10),
        WHITEBERRY_SEED(ItemID.WHITEBERRY_SEED, 10);

        private final int itemId;
        private final int amountPerHouse;

        seedTypes(final int _itemId,
                  final int _amountPerHouse) {
            itemId = _itemId;
            amountPerHouse = _amountPerHouse;
        }

    }

    @Getter
    public enum logTypes {
        LOGS(ItemID.LOGS, ItemID.BIRD_HOUSE),
        OAK_LOGS(ItemID.OAK_LOGS, ItemID.OAK_BIRD_HOUSE),
        WILLOW_LOGS(ItemID.WILLOW_LOGS, ItemID.WILLOW_BIRD_HOUSE),
        TEAK_LOGS(ItemID.TEAK_LOGS, ItemID.TEAK_BIRD_HOUSE),
        MAPLE_LOGS(ItemID.MAPLE_LOGS, ItemID.MAPLE_BIRD_HOUSE),
        MAHOGANY_LOGS(ItemID.MAHOGANY_LOGS, ItemID.MAHOGANY_BIRD_HOUSE),
        YEW_LOGS(ItemID.YEW_LOGS, ItemID.YEW_BIRD_HOUSE),
        MAGIC_LOGS(ItemID.MAGIC_LOGS, ItemID.MAGIC_BIRD_HOUSE),
        REDWOOD_LOGS(ItemID.REDWOOD_LOGS, ItemID.REDWOOD_BIRD_HOUSE);

        private final int itemId;
        private final int birdhouseType;

        logTypes(final int _itemId,
                 final int _birdhouseType) {
            itemId = _itemId;
            birdhouseType = _birdhouseType;
        }

    }
}
