package net.runelite.client.plugins.microbot.qualityoflife.enums;

import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

public enum FletchingArrow {
    BRONZE("Bronze arrowtips", "Headless arrow", "Bronze arrow", ItemID.BRONZE_ARROWTIPS, 1),
    IRON("Iron arrowtips", "Headless arrow", "Iron arrow", ItemID.IRON_ARROWTIPS, 15),
    STEEL("Steel arrowtips", "Headless arrow", "Steel arrow", ItemID.STEEL_ARROWTIPS, 30),
    MITHRIL("Mithril arrowtips", "Headless arrow", "Mithril arrow", ItemID.MITHRIL_ARROWTIPS, 45),
    BROAD("Broad arrowheads", "Headless arrow", "Broad arrow", ItemID.BROAD_ARROWHEADS, 52),
    ADAMANT("Adamant arrowtips", "Headless arrow", "Adamant arrow", ItemID.ADAMANT_ARROWTIPS, 60),
    RUNE("Rune arrowtips", "Headless arrow", "Rune arrow", ItemID.RUNE_ARROWTIPS, 75),
    AMETHYST("Amethyst arrowtips", "Headless arrow", "Amethyst arrow", ItemID.AMETHYST_ARROWTIPS, 82),
    DRAGON("Dragon arrowtips", "Headless arrow", "Dragon arrow", ItemID.DRAGON_ARROWTIPS, 90);

    @Getter
    private final String arrowTip;
    @Getter
    private final String headlessArrow;
    @Getter
    private final String arrow;
    @Getter
    private final int arrowTipId;
    private final int levelRequirement;

    FletchingArrow(String arrowTip, String headlessArrow, String arrow, int arrowTipId, int levelRequirement) {
        this.arrowTip = arrowTip;
        this.headlessArrow = headlessArrow;
        this.arrow = arrow;
        this.arrowTipId = arrowTipId;
        this.levelRequirement = levelRequirement;
    }

    public boolean meetsLevelRequirement() {
        return levelRequirement <= Rs2Player.getRealSkillLevel(Skill.FLETCHING);
    }

    public static FletchingArrow getArrowByArrowTipId(int arrowTipId) {
        for (FletchingArrow arrow : FletchingArrow.values()) {
            if (arrow.getArrowTipId() == arrowTipId) {
                return arrow;
            }
        }
        return null;
    }


}
