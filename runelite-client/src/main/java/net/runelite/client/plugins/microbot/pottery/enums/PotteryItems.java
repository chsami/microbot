package net.runelite.client.plugins.microbot.pottery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

@Getter
@RequiredArgsConstructor
public enum PotteryItems {

    BOWL (
            "unfired bowl", ItemID.UNFIRED_BOWL,
            "bowl", ItemID.BOWL,
            17694736
    ),
    CUP (
            "unfired cup", ItemID.UNFIRED_CUP,
            "empty cup",  ItemID.EMPTY_CUP,
            17694738
    ),
    PIE_DISH (
            "unfired pie dish", ItemID.UNFIRED_PIE_DISH,
            "pie dish", ItemID.PIE_DISH,
            17694735
    ),
    PLANT_POT (
            "unfired plant pot", ItemID.UNFIRED_PLANT_POT,
            "plant pot", ItemID.PLANT_POT,
            17694737
    ),
    POT (
            "unfired pot", ItemID.UNFIRED_POT,
            "pot", ItemID.POT,
            17694734
    );

    private final String unfiredItemName;
    private final int unfiredItemID;
    private final String firedItemName;
    private final int firedItemID;
    private final int unfiredWheelWidgetID;

    public boolean hasRequirements() {
        switch (this) {
            case CUP:
                return Rs2Player.getSkillRequirement(Skill.CRAFTING, 3, false) && Rs2Player.isMember();
            case PLANT_POT:
                return Rs2Player.isMember();
            default:
                return true;
        }
    }
}
