package net.runelite.client.plugins.hoseaplugins.AutoTitheFarm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static net.runelite.client.plugins.hoseaplugins.AutoTitheFarm.AutoTitheFarmPlugin.*;

@AllArgsConstructor
public enum Plants {
    GOLOVANOVA("Golovanova", 34, 53, 27384, 27387, 27390, 27393),

    BOLOGANO("Bologano", 54, 73, 27395, 27398, 27401, 27404),

    LOGAVANO("Logavano", 74, 99, 27406, 27409, 27412, 27415);

    @Getter(AccessLevel.PACKAGE)
    private final String plantName;

    private final int minLevelRequirement;

    private final int maxLevelRequirement;

    // unwatered IDs
    @Getter(AccessLevel.PACKAGE)
    private final int firstStageId;

    @Getter(AccessLevel.PACKAGE)
    private final int secondStageId;

    @Getter(AccessLevel.PACKAGE)
    private final int thirdStageId;

    @Getter(AccessLevel.PACKAGE)
    private final int fourthStageId;

    private boolean farmingLevelIsInRange() {
        return getFarmingLevel() >= minLevelRequirement && getFarmingLevel() <= maxLevelRequirement;
    }

    private Plants getPlant() {
        return farmingLevelIsInRange() ? this : null;
    }

    public static Plants getNeededPlant() {
        Plants neededPlant = null;
        for (Plants plant : Plants.values()) {
            if (plant.getPlant() == null) {
                continue;
            }
            neededPlant = plant.getPlant();
        }
        return neededPlant;
    }
}
