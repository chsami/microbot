package net.runelite.client.plugins.microbot.util.tabs;

import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.globval.VarcIntValues;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;

import java.awt.event.KeyEvent;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class Rs2Tab {
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
        if (getCurrentTab() == InterfaceTab.INVENTORY) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4678)));
        return getCurrentTab() == InterfaceTab.INVENTORY;
    }

    public static boolean switchToCombatOptionsTab() {
        if (getCurrentTab() == InterfaceTab.COMBAT) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4675)));
        return getCurrentTab() == InterfaceTab.COMBAT;
    }

    public static boolean switchToSkillsTab() {
        if (getCurrentTab() == InterfaceTab.SKILLS) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4676)));
        return getCurrentTab() == InterfaceTab.SKILLS;
    }

    public static boolean switchToQuestTab() {
        if (getCurrentTab() == InterfaceTab.QUESTS) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4677)));
        return getCurrentTab() == InterfaceTab.QUESTS;
    }


    public static boolean switchToEquipmentTab() {
        if (getCurrentTab() == InterfaceTab.EQUIPMENT) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4679)));
        return getCurrentTab() == InterfaceTab.EQUIPMENT;
    }

    public static boolean switchToPrayerTab() {
        if (getCurrentTab() == InterfaceTab.PRAYER) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4680)));
        return getCurrentTab() == InterfaceTab.PRAYER;
    }

    public static boolean switchToMagicTab() {
        if (getCurrentTab() == InterfaceTab.MAGIC) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4682)));
        return getCurrentTab() == InterfaceTab.MAGIC;
    }

    public static boolean switchToGroupingTab() {
        if (getCurrentTab() == InterfaceTab.CHAT) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4683)));
        return getCurrentTab() == InterfaceTab.CHAT;
    }

    public static boolean switchToFriendsTab() {
        if (getCurrentTab() == InterfaceTab.FRIENDS) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4684)));
        return getCurrentTab() == InterfaceTab.FRIENDS;
    }

    public static boolean switchToAccountManagementTab() {
        if (getCurrentTab() == InterfaceTab.ACC_MAN) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(6517)));
        return getCurrentTab() == InterfaceTab.ACC_MAN;
    }


    public static boolean switchToSettingsTab() {
        if (getCurrentTab() == InterfaceTab.SETTINGS) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4686)));
        return getCurrentTab() == InterfaceTab.SETTINGS;
    }

    public static boolean switchToEmotesTab() {
        if (getCurrentTab() == InterfaceTab.EMOTES) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4687)));
        return getCurrentTab() == InterfaceTab.EMOTES;
    }

    public static boolean switchToMusicTab() {
        if (getCurrentTab() == InterfaceTab.MUSIC) {
            return true;
        }
        VirtualKeyboard.keyPress(getKeyBind(Microbot.getVarbitValue(4688)));
        return getCurrentTab() == InterfaceTab.MUSIC;
    }

    public static boolean switchToLogout() {
        if (getCurrentTab() == InterfaceTab.LOGOUT) return true;

        WidgetInfo logoutWidget;
        if (InterfaceTab.LOGOUT.getFixedClassicWidget() != null) {
            logoutWidget = WidgetInfo.FIXED_VIEWPORT_LOGOUT_TAB;
        } else if (InterfaceTab.LOGOUT.getResizableClassicWidget() != null) {
            logoutWidget = WidgetInfo.RESIZABLE_VIEWPORT_LOGOUT_TAB;
        } else {
            logoutWidget = null;
        }

        Widget tab = Microbot.getClient().getWidget(logoutWidget);
        if (tab == null) return false;
        Microbot.getMouse().click(tab.getBounds());
        sleep(200, 600);
        return getCurrentTab() == InterfaceTab.LOGOUT;
    }

    private static int getKeyBind(int value) {
        if (value == 1) return KeyEvent.VK_F1;
        if (value == 2) return KeyEvent.VK_F2;
        if (value == 3) return KeyEvent.VK_F3;
        if (value == 4) return KeyEvent.VK_F4;
        if (value == 5) return KeyEvent.VK_F5;
        if (value == 6) return KeyEvent.VK_F6;
        if (value == 7) return KeyEvent.VK_F7;
        if (value == 8) return KeyEvent.VK_F8;
        if (value == 9) return KeyEvent.VK_F9;
        if (value == 10) return KeyEvent.VK_F10;
        if (value == 11) return KeyEvent.VK_F11;
        if (value == 12) return KeyEvent.VK_F12;
        if (value == 13) return KeyEvent.VK_ESCAPE;

        return -1;
    }


}
