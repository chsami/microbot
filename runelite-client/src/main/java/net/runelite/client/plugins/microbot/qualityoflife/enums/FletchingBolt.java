package net.runelite.client.plugins.microbot.qualityoflife.enums;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

public enum FletchingBolt {
    BRONZE("Bronze bolts (unf)", "Feather", "Bronze bolts", ItemID.BRONZE_BOLTS_UNF, 9),
    BLURITE("Blurite bolts (unf)", "Feather", "Blurite bolts", ItemID.BLURITE_BOLTS_UNF, 24),
    IRON("Iron bolts (unf)", "Feather", "Iron bolts", ItemID.IRON_BOLTS_UNF, 39),
    SILVER("Silver bolts (unf)", "Feather", "Silver bolts", ItemID.SILVER_BOLTS_UNF, 43),
    STEEL("Steel bolts (unf)", "Feather", "Steel bolts", ItemID.STEEL_BOLTS_UNF, 46),
    MITHRIL("Mithril bolts (unf)", "Feather", "Mithril bolts", ItemID.MITHRIL_BOLTS_UNF, 54),
    BROAD("Broad bolts (unf)", "Feather", "Broad bolts", ItemID.UNFINISHED_BROAD_BOLTS, 55),
    ADAMANT("Adamant bolts (unf)", "Feather", "Adamant bolts", ItemID.ADAMANT_BOLTSUNF, 61),
    RUNITE("Runite bolts (unf)", "Feather", "Runite bolts", ItemID.RUNITE_BOLTS_UNF, 69),
    DRAGON("Dragon bolts (unf)", "Feather", "Dragon bolts", ItemID.DRAGON_BOLTS_UNF, 84);

    @Getter
    private final String boltTip;
    @Getter
    private final String feather;
    @Getter
    private final String bolt;
    @Getter
    private final int boltTipId;
    private final int levelRequirement;

    FletchingBolt(String boltTip, String feather, String bolt, int boltTipId, int levelRequirement) {
        this.boltTip = boltTip;
        this.feather = feather;
        this.bolt = bolt;
        this.boltTipId = boltTipId;
        this.levelRequirement = levelRequirement;
    }

    public boolean meetsLevelRequirement() {
        return levelRequirement <= Rs2Player.getRealSkillLevel(Skill.FLETCHING);
    }

    public static FletchingBolt getBoltByBoltTipId(int boltTipId) {
        for (FletchingBolt bolt : FletchingBolt.values()) {
            if (bolt.getBoltTipId() == boltTipId) {
                return bolt;
            }
        }
        return null;
    }


}
