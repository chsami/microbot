package net.runelite.client.plugins.ogPlugins.ogConstruction.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;

@Getter
@RequiredArgsConstructor
public enum Furniture {
    OAK_LARDER("Oak Larder",ObjectID.LARDER_SPACE,ObjectID.LARDER_13566, 2, ItemID.OAK_PLANK,"Oak plank", 8),
    OAK_DUNGEON_DOOR("Oak Dungeon Door",ObjectID.DOOR_SPACE_15328,ObjectID.DOOR_13344,1,ItemID.OAK_PLANK,"Oak plank",10),
    MOUNTED_MYTHICAL_CAPE("Mythical Cape",ObjectID.GUILD_TROPHY_SPACE,ObjectID.MOUNTED_MYTHICAL_CAPE,4,ItemID.TEAK_PLANK,"Teak plank",3),
    MAHOHANY_TABLE("Mahogany Table",ObjectID.TABLE_SPACE,ObjectID.MAHOGANY_TABLE,6,ItemID.MAHOGANY_PLANK,"Mahogany plank",6),
    GNOME_BENCH_R("Gnome Bench",ObjectID.SEATING_SPACE_29137,ObjectID.GNOME_BENCH_29272,2,ItemID.MAHOGANY_PLANK,"Mahogany plank",6),
    GNOME_BENCH_L("Gnome Bench",ObjectID.SEATING_SPACE_29138,ObjectID.GNOME_BENCH_29272,2,ItemID.MAHOGANY_PLANK,"Mahogany plank",6);


    @Getter
    private final String name;
    @Getter
    private final int unBuiltID;
    @Getter
    private final int builtID;
    @Getter
    private final int buildOption;
    @Getter
    private final int plankNeeded;
    @Getter
    private final String notedPlankNameNeeded;
    @Getter
    private final int plankAmountNeeded;
}
