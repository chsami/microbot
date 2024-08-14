package net.runelite.client.plugins.microbot.CrashedStar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

@Getter
@RequiredArgsConstructor
public enum StarInfo {

    TIER1(ObjectID.CRASHED_STAR_41229, 10),
    TIER2(ObjectID.CRASHED_STAR_41228, 20),
    TIER3(ObjectID.CRASHED_STAR_41227, 30),
    TIER4(ObjectID.CRASHED_STAR_41226, 40),
    TIER5(ObjectID.CRASHED_STAR_41225, 50),
    TIER6(ObjectID.CRASHED_STAR_41224, 60),
    TIER7(ObjectID.CRASHED_STAR_41223, 70),
    TIER8(ObjectID.CRASHED_STAR_41021, 80),
    TIER9(ObjectID.CRASHED_STAR, 90);

    private final int objectID;
    private final int miningLevel;

    public boolean hasRequiredLevel() { return Rs2Player.getSkillRequirement(Skill.MINING, this.miningLevel); }

    public int getObjectID() {
        return objectID;
    }

    public int getMiningLevel() { return miningLevel; }


    }
