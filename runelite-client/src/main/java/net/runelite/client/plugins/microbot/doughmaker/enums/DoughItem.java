package net.runelite.client.plugins.microbot.doughmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.awt.event.KeyEvent;

@Getter
@RequiredArgsConstructor
public enum DoughItem {

    BREAD_DOUGH("Bread dough", ItemID.BREAD_DOUGH, 1, KeyEvent.VK_1),
    PASTRY_DOUGH("Pastry dough", ItemID.PASTRY_DOUGH, 1, KeyEvent.VK_2),
    PIZZA_BASE("Pizza base", ItemID.PIZZA_BASE, 1, KeyEvent.VK_SPACE);

    private final String itemName;
    private final int itemId;
    private final int levelRequired;
    private final int keyEvent;

    private boolean hasLevelRequired() {
        return Rs2Player.getSkillRequirement(Skill.COOKING, this.getLevelRequired());
    }

//    public boolean hasRequirements() {
//        switch (this) {
//            case RAW_COD:
//            case RAW_KARAMBWAN:
//            case RAW_BASS:
//            case RAW_MONKFISH:
//            case RAW_SHARK:
//            case RAW_SEA_TURTLE:
//            case RAW_DARK_CRAB:
//            case RAW_MANTA_RAY:
//                return hasLevelRequired() && Rs2Player.isMember();
//            default:
//                return hasLevelRequired();
//        }
//    }
}
