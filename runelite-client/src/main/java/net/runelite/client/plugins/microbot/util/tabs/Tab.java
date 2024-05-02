package net.runelite.client.plugins.microbot.util.tabs;

import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;

@Deprecated(since = "Use Rs2Tab instead", forRemoval = true)
public class Tab {
    public static InterfaceTab getCurrentTab() {
        return Rs2Tab.getCurrentTab();
    }

    public static boolean switchToInventoryTab() {
        return Rs2Tab.switchToInventoryTab();
    }

    public static boolean switchToCombatOptionsTab() {
        return Rs2Tab.switchToCombatOptionsTab();
    }

    public static boolean switchToSkillsTab() {
        return Rs2Tab.switchToSkillsTab();
    }

    public static boolean switchToQuestTab() {
        return Rs2Tab.switchToQuestTab();
    }


    public static boolean switchToEquipmentTab() {
        return Rs2Tab.switchToEquipmentTab();
    }

    public static boolean switchToPrayerTab() {
        return Rs2Tab.switchToPrayerTab();
    }

    public static boolean switchToMagicTab() {
        return Rs2Tab.switchToMagicTab();
    }

    public static boolean switchToGroupingTab() {
        return Rs2Tab.switchToGroupingTab();
    }

    public static boolean switchToFriendsTab() {
        return Rs2Tab.switchToFriendsTab();
    }

    public static boolean switchToAccountManagementTab() {
        return Rs2Tab.switchToAccountManagementTab();
    }


    public static boolean switchToSettingsTab() {
        return Rs2Tab.switchToSettingsTab();
    }

    public static boolean switchToEmotesTab() {
        return Rs2Tab.switchToEmotesTab();
    }

    public static boolean switchToMusicTab() {
        return Rs2Tab.switchToMusicTab();
    }

    public static boolean switchToLogout() {
        return Rs2Tab.switchToLogout();
    }


}
