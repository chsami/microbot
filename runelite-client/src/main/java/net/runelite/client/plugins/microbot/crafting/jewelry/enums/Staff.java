package net.runelite.client.plugins.microbot.crafting.jewelry.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Staff {
    NONE(0, Collections.emptyList()),
    STAFF_OF_AIR(ItemID.STAFF_OF_AIR, List.of(ItemID.AIR_RUNE)),
    STAFF_OF_WATER(ItemID.STAFF_OF_WATER, List.of(ItemID.WATER_RUNE)),
    STAFF_OF_EARTH(ItemID.STAFF_OF_EARTH, List.of(ItemID.EARTH_RUNE)),
    STAFF_OF_FIRE(ItemID.STAFF_OF_FIRE, List.of(ItemID.FIRE_RUNE)),
    AIR_BATTLESTAFF(ItemID.AIR_BATTLESTAFF, List.of(ItemID.AIR_RUNE)),
    WATER_BATTLESTAFF(ItemID.WATER_BATTLESTAFF, List.of(ItemID.WATER_RUNE)),
    EARTH_BATTLESTAFF(ItemID.EARTH_BATTLESTAFF, List.of(ItemID.EARTH_RUNE)),
    FIRE_BATTLESTAFF(ItemID.FIRE_BATTLESTAFF, List.of(ItemID.FIRE_RUNE)),
    MUD_BATTLESTAFF(ItemID.MUD_BATTLESTAFF, List.of(ItemID.WATER_RUNE, ItemID.EARTH_RUNE)),
    LAVA_BATTLESTAFF(ItemID.LAVA_BATTLESTAFF, List.of(ItemID.FIRE_RUNE, ItemID.EARTH_RUNE)),
    MYSTIC_AIR_STAFF(ItemID.MYSTIC_AIR_STAFF, List.of(ItemID.AIR_RUNE)),
    MYSTIC_WATER_STAFF(ItemID.MYSTIC_WATER_STAFF, List.of(ItemID.WATER_RUNE)),
    MYSTIC_EARTH_STAFF(ItemID.MYSTIC_EARTH_STAFF, List.of(ItemID.EARTH_RUNE)),
    MYSTIC_FIRE_STAFF(ItemID.MYSTIC_FIRE_STAFF, List.of(ItemID.FIRE_RUNE));
    
    private final int itemID;
    private final List<Integer> runeItemIDs;


    public static List<Staff> getFireRuneStaffs() {
        return Stream.of(Staff.values())
                .filter(staff -> staff.getRuneItemIDs().contains(ItemID.FIRE_RUNE))
                .collect(Collectors.toList());
    }

    public static Staff getByItemID(int itemID) {
        return Stream.of(Staff.values())
                .filter(staff -> staff.getItemID() == itemID)
                .findFirst()
                .orElse(NONE);
    }
}
