package net.runelite.client.plugins.hoseaplugins.AutoRuneDragon.data;

import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Constants {
    //Locations
    public static final List<Integer> HOME_REGIONS = Arrays.asList(7513, 7514, 7769, 7770, 8025, 8026,
            13986, 13987, 13988, 14242, 14243, 14244, 14498, 14499, 14500);
    public static final List<Integer> LITH_REGIONS = Arrays.asList(14242, 6223);
    public static final WorldArea EDGEVILLE_TELE = new WorldArea(3083, 3487, 17, 14, 0);
    public static final WorldArea LITH_TELE = new WorldArea(3540, 10443, 21, 26, 0);
    public static final WorldArea LITH_TELE_DOWNSTAIRS = new WorldArea(3542, 10467, 16, 19, 0);
    public static final WorldArea RUNE_DRAGONS_DOOR = new WorldArea(1562, 5058, 12, 21, 0);
    public static final WorldArea RUNE_DRAGONS_DOOR_ENTER = new WorldArea(1570, 5072, 4, 6, 0);
    public static final WorldPoint RUNE_DRAGONS_DOOR_TILE = new WorldPoint(1572, 5074, 0);
    public static final WorldArea RUNE_DRAGONS = new WorldArea(1574, 5061, 25, 28, 0);
    // Potions
    public static final Set<Integer> EXTENDED_ANTIFIRE_POTS = Set.of(
            ItemID.EXTENDED_ANTIFIRE1,
            ItemID.EXTENDED_ANTIFIRE2,
            ItemID.EXTENDED_ANTIFIRE3,
            ItemID.EXTENDED_ANTIFIRE4
    );
    public static final Set<Integer> SUPER_EXTENDED_ANTIFIRE_POTS = Set.of(
            ItemID.EXTENDED_SUPER_ANTIFIRE1,
            ItemID.EXTENDED_SUPER_ANTIFIRE2,
            ItemID.EXTENDED_SUPER_ANTIFIRE3,
            ItemID.EXTENDED_SUPER_ANTIFIRE4
    );
    public static final Set<Integer> PRAYER_POTS = Set.of(
            ItemID.PRAYER_POTION1,
            ItemID.PRAYER_POTION2,
            ItemID.PRAYER_POTION3,
            ItemID.PRAYER_POTION4
    );
    public static final Set<Integer> SUPER_COMBAT_POTS = Set.of(
            ItemID.SUPER_COMBAT_POTION1,
            ItemID.SUPER_COMBAT_POTION2,
            ItemID.SUPER_COMBAT_POTION3,
            ItemID.SUPER_COMBAT_POTION4
    );
    public static final Set<Integer> DIVINE_SUPER_COMBAT_POTS = Set.of(
            ItemID.DIVINE_SUPER_COMBAT_POTION1,
            ItemID.DIVINE_SUPER_COMBAT_POTION2,
            ItemID.DIVINE_SUPER_COMBAT_POTION3,
            ItemID.DIVINE_SUPER_COMBAT_POTION4
    );
    public static final Set<Integer> DIGSITE_PENDANTS = Set.of(
            ItemID.DIGSITE_PENDANT_1,
            ItemID.DIGSITE_PENDANT_2,
            ItemID.DIGSITE_PENDANT_3,
            ItemID.DIGSITE_PENDANT_4,
            ItemID.DIGSITE_PENDANT_5
    );

}

