package net.runelite.client.plugins.microbot.util.tabs;

import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.globval.VarcIntValues;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;

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
        if (Rs2Bank.isOpen()) return true;
        VirtualKeyboard.keyPress(KeyEvent.VK_ESCAPE);
        return getCurrentTab() == InterfaceTab.INVENTORY;
    }

    public static boolean switchToCombatOptionsTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F1);
        return getCurrentTab() == InterfaceTab.COMBAT;
    }

    public static boolean switchToSkillsTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F2);
        return getCurrentTab() == InterfaceTab.SKILLS;
    }

    public static boolean switchToQuestTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F3);
        return getCurrentTab() == InterfaceTab.QUESTS;
    }


    public static boolean switchToEquipmentTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F4);
        return getCurrentTab() == InterfaceTab.EQUIPMENT;
    }

    public static boolean switchToPrayerTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F5);
        return getCurrentTab() == InterfaceTab.PRAYER;
    }

    public static boolean switchToMagicTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F6);
        return getCurrentTab() == InterfaceTab.MAGIC;
    }

    public static boolean switchToGroupingTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F7);
        return getCurrentTab() == InterfaceTab.CHAT;
    }

    public static boolean switchToFriendsTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F8);
        return getCurrentTab() == InterfaceTab.FRIENDS;
    }

    public static boolean switchToAccountManagementTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F9);
        return getCurrentTab() == InterfaceTab.ACC_MAN;
    }


    public static boolean switchToSettingsTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F10);
        return getCurrentTab() == InterfaceTab.SETTINGS;
    }

    public static boolean switchToEmotesTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F11);
        return getCurrentTab() == InterfaceTab.EMOTES;
    }

    public static boolean switchToMusicTab() {
        VirtualKeyboard.keyPress(KeyEvent.VK_F12);
        return getCurrentTab() == InterfaceTab.MUSIC;
    }

    public static boolean switchToLogout() {
        if (getCurrentTab() == InterfaceTab.LOGOUT) return true;
        Widget tab = Microbot.getClient().getWidget(	10551341);
        if (tab == null) return false;
        Microbot.getMouse().click(tab.getBounds());
        sleep(600, 1000);
        return getCurrentTab() == InterfaceTab.LOGOUT;
    }




}
