package net.runelite.client.plugins.microbot.util.tabs;

import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.globval.VarcIntValues;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class Tab {
    public static InterfaceTab getCurrentTab() {
        int varcIntValue = Microbot.getClient().getVarcIntValue(VarClientInt.INVENTORY_TAB);
        switch (VarcIntValues.valueOf(varcIntValue)) {
            case TAB_COMBAT_OPTIONS:
                return InterfaceTab.COMBAT;
            case TAB_SKILLS:
                return InterfaceTab.SKILLS;
            case TAB_QUEST_LIST:
                return InterfaceTab.QUESTS;
            case TAB_INVENTORY:
                return InterfaceTab.INVENTORY;
            case TAB_WORN_EQUIPMENT:
                return InterfaceTab.EQUIPMENT;
            case TAB_PRAYER:
                return InterfaceTab.PRAYER;
            case TAB_SPELLBOOK:
                return InterfaceTab.MAGIC;
            case TAB_FRIEND_LIST:
                return InterfaceTab.FRIENDS;
            case TAB_LOGOUT:
                return InterfaceTab.LOGOUT;
            case TAB_SETTINGS:
                return InterfaceTab.SETTINGS;
            case TAB_MUSIC:
                return InterfaceTab.MUSIC;
            case TAB_CHAT_CHANNEL:
                return InterfaceTab.CHAT;
            case TAB_ACC_MANAGEMENT:
                return InterfaceTab.ACC_MAN;
            case TAB_EMOTES:
                return InterfaceTab.EMOTES;
            case TAB_NOT_SELECTED:
                return InterfaceTab.NOTHING_SELECTED;
            default:
                throw new IllegalStateException("Unexpected value: " + VarcIntValues.valueOf(varcIntValue));
        }
    }

    public static boolean switchToInventoryTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F2);
        return getCurrentTab() == InterfaceTab.INVENTORY;
    }

    public static boolean switchToPrayerTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F3);
        return getCurrentTab() == InterfaceTab.PRAYER;
    }

    public static boolean switchToSettings() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F10);
        return getCurrentTab() == InterfaceTab.SETTINGS;
    }
    public static boolean switchToLogout() {
        if (getCurrentTab() == InterfaceTab.LOGOUT) return true;
        Widget tab = Microbot.getClient().getWidget(	10551341);
        if (tab == null) return false;
        Microbot.getMouse().click(tab.getBounds());
        sleep(600, 1000);
        return getCurrentTab() == InterfaceTab.LOGOUT;
    }

    public static boolean switchToMagicTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F4);
        return getCurrentTab() == InterfaceTab.MAGIC;
    }
}
