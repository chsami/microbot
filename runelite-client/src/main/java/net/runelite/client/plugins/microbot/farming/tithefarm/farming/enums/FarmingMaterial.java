package net.runelite.client.plugins.microbot.farming.tithefarm.farming.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FarmingMaterial {
    OAK_TREE("Oak", "oak sapling", "tomatoes(5)", 1, 15),
    WILLOW_TREE("Willow", "willow sapling", "apples(5)", 1, 30),
    MAPLE_TREE("Maple", "maple sapling", "oranges(5)", 1, 45),
    YEW_TREE("Yew", "yew sapling", "cactus spine", 10, 60),
    MAGIC_TREE("Magic", "magic sapling", "coconut", 25, 75),
    APPLE_TREE("Apple", "apple sapling", "sweetcorn", 9, 27),
    BANANA_TREE("Banana", "banana sapling", "apples(5)", 4, 33),
    ORANGE_TREE("Orange", "orange sapling", "strawberries(5)", 3, 39),
    CURRY_TREE("Curry", "curry sapling", "bananas(5)", 5, 42),
    PINEAPPLE_TREE("Pineapple", "pineapple sapling", "watermelon", 10, 51),
    PAPAYA_TREE("Papaya", "papaya sapling", "pineapple", 10, 57),
    PALM_TREE("Palm", "palm sapling", "papaya fruit", 15, 68),
    DRAGONFRUIT_TREE("Dragonfruit", "dragonfruit sapling", "coconut", 15, 81);


    private final String name;
    private final String itemName;
    private final String protectionItem;
    private final int protectionItemAmount;
    private final int levelRequired;

    @Override
    public String toString() {
        return name;
    }
}
