package net.runelite.client.plugins.microbot.cooking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.cooking.AutoCookingConfig;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

@Getter
@RequiredArgsConstructor
public enum CookingLocation {

    CATHERBY(new WorldPoint(2817, 3443, 0), CookingAreaType.RANGE, ObjectID.RANGE_26181),
    SEERS_VILLAGE(new WorldPoint(2715, 3477, 0), CookingAreaType.RANGE, ObjectID.RANGE_26181),
    EAST_ARDOUGNE_FARM(new WorldPoint(2642, 3356, 0), CookingAreaType.RANGE, ObjectID.RANGE_26181),
    EAST_ARDOUGNE_SOUTH(new WorldPoint(2647, 3298, 0), CookingAreaType.RANGE, ObjectID.RANGE_26181),
    YANILLE(new WorldPoint(2566, 3103, 0), CookingAreaType.RANGE, ObjectID.RANGE_26181),
    COOKS_KITCHEN(new WorldPoint(3211, 3216, 0), CookingAreaType.RANGE, ObjectID.COOKING_RANGE),
    ROUGES_DEN(new WorldPoint(3043, 4972, 1), CookingAreaType.FIRE, ObjectID.FIRE_43475),
    LUMBRIDGE(new WorldPoint(3231, 3196, 0), CookingAreaType.RANGE, ObjectID.RANGE_26181),
    FALADOR(new WorldPoint(3039, 3345, 0), CookingAreaType.RANGE, 40296),
    PORT_SARIM(new WorldPoint(3018, 3238, 0), CookingAreaType.RANGE, ObjectID.RANGE_26181),
    RIMMINGTON(new WorldPoint(2969, 3210, 0), CookingAreaType.RANGE, ObjectID.RANGE_9682),
    BARBARBIAN_VILLAGE(new WorldPoint(3106, 3433, 0), CookingAreaType.FIRE, ObjectID.FIRE_43475),
    EDGEVILLE(new WorldPoint(3078, 3496, 0), CookingAreaType.RANGE, ObjectID.STOVE_12269),
    COOKS_GUILD(new WorldPoint(3146, 3452, 0), CookingAreaType.RANGE, ObjectID.RANGE_7183),
    VARROCK_WEST(new WorldPoint(3160, 3428, 0), CookingAreaType.RANGE, ObjectID.RANGE_7183),
    VARROCK_EAST(new WorldPoint(3247, 3397, 0), CookingAreaType.RANGE, ObjectID.RANGE_7183),
    AL_KHARID(new WorldPoint(3272, 3180, 0), CookingAreaType.RANGE, ObjectID.RANGE_26181),
    TAI_BWO_WANNAI(new WorldPoint(2788, 3048, 0), CookingAreaType.FIRE, ObjectID.FIRE_26185),
    PORT_PISCARILIUS(new WorldPoint(1806, 3735, 0), CookingAreaType.RANGE, ObjectID.RANGE_27724),
    HOSIDIUS(new WorldPoint(1738, 3612, 0), CookingAreaType.RANGE, ObjectID.RANGE_27517),
    HOSIDIUS_KITCHEN(new WorldPoint(1680, 3621, 0), CookingAreaType.RANGE, ObjectID.CLAY_OVEN_21302),
    NARDAH(new WorldPoint(3434, 2887, 0), CookingAreaType.RANGE, ObjectID.CLAY_OVEN),
    LANDS_END(new WorldPoint(1515, 3442, 0), CookingAreaType.RANGE, ObjectID.RANGE_7183);

    private final WorldPoint cookingObjectWorldPoint;
    private final CookingAreaType cookingAreaType;
    private final int cookingObjectID;

    public static CookingLocation findNearestCookingLocation(AutoCookingConfig config, WorldPoint playerWorldPoint) {
        CookingLocation nearestLocation = null;
        double nearestDistance = Double.MAX_VALUE;

        for (CookingLocation location : values()) {
            double distance = calculateDistance(playerWorldPoint, location.getCookingObjectWorldPoint());
            if (!location.hasRequirements()) continue;
            if ((config.cookingItem().getCookingAreaType() != CookingAreaType.BOTH) &&
                    (location.getCookingAreaType() != config.cookingItem().getCookingAreaType())) continue;
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestLocation = location;
            }
        }

        return nearestLocation;
    }

    private static double calculateDistance(WorldPoint point1, WorldPoint point2) {
        int y = point1.getY();
        boolean isInCave = y > 9000;
        if (isInCave) {
            y -= 6300;
        }
        int dx = point1.getX() - point2.getX();
        int dy = y - point2.getY();
        int dz = point1.getPlane() - point2.getPlane();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public boolean hasRequirements() {
        boolean hasLineOfSight = Microbot.getClient().getLocalPlayer().getWorldArea().hasLineOfSightTo(Microbot.getClient().getTopLevelWorldView(), this.cookingObjectWorldPoint);
        switch (this) {
            case COOKS_GUILD:
                boolean hasFaladorHardDiary = Microbot.getVarbitValue(Varbits.DIARY_FALADOR_HARD) == 1;
                boolean hasMaxedCrafting = Rs2Player.getSkillRequirement(Skill.CRAFTING, 99, false);
                boolean isWearingCraftingGuild = (Rs2Equipment.isWearing("brown apron") || Rs2Equipment.isWearing("golden apron"));

                if (hasLineOfSight && Rs2Player.isMember() && (hasMaxedCrafting || hasFaladorHardDiary)) return true;
                return Rs2Player.isMember() && isWearingCraftingGuild &&
                        (hasMaxedCrafting || hasFaladorHardDiary);
            case HOSIDIUS_KITCHEN:
                boolean hasKourendEasyDiary = Microbot.getVarbitValue(Varbits.DIARY_KOUREND_EASY) == 1;

                if (hasLineOfSight && Rs2Player.isMember() && hasKourendEasyDiary) return true;
                return Rs2Player.isMember() && hasKourendEasyDiary;
            default:
                return true;
        }
    }
}
