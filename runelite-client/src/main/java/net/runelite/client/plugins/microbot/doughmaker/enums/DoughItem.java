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

    BREAD_DOUGH("Bread dough", ItemID.BREAD_DOUGH, KeyEvent.VK_1),
    PASTRY_DOUGH("Pastry dough", ItemID.PASTRY_DOUGH, KeyEvent.VK_2),
    PIZZA_BASE("Pizza base", ItemID.PIZZA_BASE,  KeyEvent.VK_3);

    private final String itemName;
    private final int itemId;
    private final int keyEvent;
}
