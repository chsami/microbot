package net.runelite.client.plugins.microbot.util.misc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rs2Food {
    Dark_Crab(1961,27,"Dark Crab"),
    ROCKTAIL(15272,23,"Rocktail"),
    MANTA(391,22,"Manta Ray"),
    SHARK(385,20,"Shark"),
    KARAMBWAN(	3144,18,"Cooked karambwan"),
    LOBSTER(379,12,"Lobster"),
    TROUT(333,7,"Trout"),
    SALMON(329,9,"Salmon"),
    SWORDFISH(373,14,"Swordfish"),
    TUNA(361,10,"Tuna"),
    MONKFISH(7946,16,"Monkfish"),
    SEA_TURTLE(397,21,"Sea Turtle"),
    CAKE(1891,4,"Cake"),
    BASS(365,13,"Bass"),
    COD(339,7,"Cod"),
    POTATO(1942,1,"Potato"),
    BAKED_POTATO(6701,4,"Baked Potato"),
    POTATO_WITH_CHEESE(6705,16,"Potato with Cheese"),
    EGG_POTATO(7056,16,"Egg Potato"),
    CHILLI_POTATO(7054,14,"Chilli Potato"),
    MUSHROOM_POTATO(7058,20,"Mushroom Potato"),
    TUNA_POTATO(7060,22,"Tuna Potato"),
    SHRIMPS(315,3,"Shrimps"),
    HERRING(347,5,"Herring"),
    SARDINE(325,4,"Sardine"),
    CHOCOLATE_CAKE(1897,5,"Chocolate Cake"),
    ANCHOVIES(319,1,"Anchovies"),
    PLAIN_PIZZA(2289,7,"Plain Pizza"),
    MEAT_PIZZA(2293,8,"Meat Pizza"),
    ANCHOVY_PIZZA(2297,9,"Anchovy Pizza"),
    PINEAPPLE_PIZZA(2301,11,"Pineapple Pizza"),
    BREAD(2309,5,"Bread"),
    APPLE_PIE(2323,7,"Apple Pie"),
    REDBERRY_PIE(2325,5,"Redberry Pie"),
    MEAT_PIE(2327,6,"Meat Pie"),
    PIKE(351,8,"Pike"),
    POTATO_WITH_BUTTER(6703,14,"Potato with Butter"),
    BANANA(1963,2,"Banana"),
    PEACH(6883,8,"Peach"),
    ORANGE(2108,2,"Orange"),
    PINEAPPLE_RINGS(2118,2,"Pineapple Rings"),
    PINEAPPLE_CHUNKS(2116,2,"Pineapple Chunks"),
    PURPLE_SWEETS(10476,3,"Purple Sweets");

    private int id; private int heal; private String name;

    Rs2Food(int id, int heal, String name) {
        this.id = id;
        this.heal = heal;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " (+" + getHeal() + ")";
    }

    public int getId() {
        return id;
    }

    public int getHeal() {
        return heal;
    }

    public String getName() {
        return name;
    }

}
