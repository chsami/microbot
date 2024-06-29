package net.runelite.client.plugins.microbot.pottery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ObjectID;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

@Getter
@RequiredArgsConstructor
public enum PotteryLocations {
    /*
        TODO: Gather DARKMEYER Object IDs & WorldPoints
     */
    BARBARIAN_VILLAGE (
            ObjectID.POTTERS_WHEEL_14887, new WorldPoint(3086, 3410, 0),
            ObjectID.POTTERY_OVEN_11601, new WorldPoint(3085, 3408, 0),
            ObjectID.SINK_12279, new WorldPoint(3078, 3490, 0),
            ObjectID.WELL_884, new WorldPoint(3083, 3504, 0)
    ),
    EAST_ARDOUGNE (
            ObjectID.POTTERS_WHEEL_14887, new WorldPoint(2593, 3322, 0),
            ObjectID.POTTERY_OVEN_11601, new WorldPoint(2590, 3326, 0),
            ObjectID.SINK_874, new WorldPoint(2572, 3332, 0),
            ObjectID.WELL_884, new WorldPoint(2641, 3369, 0)
    ),
    RELLEKKA (
            ObjectID.POTTERS_WHEEL, new WorldPoint(2619, 3659, 0),
            ObjectID.POTTERY_OVEN, new WorldPoint(2617, 3662, 0),
            ObjectID.SINCLAIR_FAMILY_FOUNTAIN, new WorldPoint(2746, 3564, 0),
            ObjectID.WELL_8927, new WorldPoint(2667, 3661, 0)
    ),
    CRAFTING_GUILD (
            ObjectID.POTTERS_WHEEL_14887, new WorldPoint(2936, 3289, 0),
            ObjectID.POTTERY_OVEN_14888, new WorldPoint(2930, 3291, 0),
            ObjectID.SINK_9684, new WorldPoint(4928, 3136, 0),
            ObjectID.WELL_24150, new WorldPoint(2986, 3316, 0)
    );

    private final int wheelObjectID;
    private final WorldPoint wheelWorldPoint;
    private final int ovenObjectID;
    private final WorldPoint ovenWorldPoint;
    private final int waterPointObjectID;
    private final WorldPoint waterPoint;
    private final int wellWaterPointObjectID;
    private final WorldPoint wellWaterPoint;


    public boolean hasRequirements() {
        switch (this) {
            case EAST_ARDOUGNE:
                return Rs2Player.isMember();
            case RELLEKKA:
                return Rs2Player.isMember() && Rs2Player.getQuestState(Quest.THE_FREMENNIK_TRIALS) == QuestState.FINISHED;
            case CRAFTING_GUILD:
                return Rs2Player.getSkillRequirement(Skill.CRAFTING, 40, false) &&
                        (Rs2Equipment.isWearing("brown apron") || Rs2Equipment.isWearing("golden apron"));
            default:
                return true;
        }
    }
}
