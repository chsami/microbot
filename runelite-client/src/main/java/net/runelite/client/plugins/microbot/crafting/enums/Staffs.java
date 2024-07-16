package net.runelite.client.plugins.microbot.crafting.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Staffs {
    NONE(" ","",0,""),
    PROGRESSIVE("Progressive Mode","None", 1, ""),
    WATER_BATTLESTAFF("Water Battlestaff", "Water Battlestaff", 54, "Water Orb"),
    EARTH_BATTLESTAFF("Earth Battlestaff", "Earth Battlestaff", 58, "Earth Orb"),
    FIRE_BATTLESTAFF("Fire Battlestaff", "Fire Battlestaff", 62, "Fire Orb"),
    AIR_BATTLESTAFF("Air Battlestaff", "Air Battlestaff", 66, "Air Orb");

    private final String label;
    private final String itemName;
    private final int levelRequired;
    private final String orb;
    @Override
    public String toString()
    {
        return label;
    }
}