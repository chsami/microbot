package net.runelite.client.plugins.microbot.thieving.stalls.constants;

import lombok.Getter;
import net.runelite.api.ItemID;

public enum StallLoot {
    BAKER(new Integer[]{ItemID.CAKE, ItemID.BREAD, ItemID.CHOCOLATE_SLICE}),
    SILK(new Integer[]{ItemID.SILK}),
    FRUIT(new Integer[]{ItemID.COOKING_APPLE, ItemID.BANANA, ItemID.STRAWBERRY,
                        ItemID.JANGERBERRIES, ItemID.LEMON, ItemID.REDBERRIES,
                        ItemID.PINEAPPLE, ItemID.LIME, ItemID.STRANGE_FRUIT,
                        ItemID.GOLOVANOVA_FRUIT_TOP, ItemID.PAPAYA_FRUIT})
    ;

    @Getter
    private Integer[] itemIds;

    StallLoot(Integer[] itemIds)
    {
        this.itemIds = itemIds;
    }
}
