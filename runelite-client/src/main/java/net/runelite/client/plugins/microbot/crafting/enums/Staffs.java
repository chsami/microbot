package net.runelite.client.plugins.microbot.crafting.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Staffs {
    PROGRESSIVE("Progressive Mode","none", 1, ""),
    WATER_BATTLESTAFF("Water battlestaff", "water battlestaff", 54, "Water orb"),
    EARTH_BATTLESTAFF("Earth battlestaff", "earth battlestaff", 58, "Earth orb"),
    FIRE_BATTLESTAFF("Fire battlestaff", "fire battlestaff", 62, "Fire orb"),
    AIR_BATTLESTAFF("Air battlestaff", "air battlestaff", 66, "Air orb");

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