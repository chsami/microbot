package net.runelite.client.plugins.microbot.globval;

import java.util.HashMap;
import java.util.Map;

public enum VarcIntValues {
    // Tooltip State
    TOOLTIP_SHOWN(0),
    TOOLTIP_HIDDEN(1),
    // Chatbox Input Type
    RUNELITE_CHATBOX_PANEL(-3),
    RUNELITE(-2),
    NONE(0),
    PRIVATE_MESSAGE(6),
    SEARCH(11),
    // Chatbox Scrollbar Position
    CHATBOX_SCROLLBAR_POSITION_MIN(0),
    CHATBOX_SCROLLBAR_POSITION_MAX(1302),
    // Report Window State
    REPORT_WINDOW_OPENED(1),
    REPORT_WINDOW_CLOSED(0),
    // Selected Chat Tab
    SELECTED_CHAT_TAB_ALL(0),
    SELECTED_CHAT_TAB_GAME(1),
    SELECTED_CHAT_TAB_PUBLIC(2),
    SELECTED_CHAT_TAB_PRIVATE(3),
    SELECTED_CHAT_TAB_CHANNEL(4),
    SELECTED_CHAT_TAB_CLAN(5),
    SELECTED_CHAT_TAB_TRADE(6),
    SELECTED_CHAT_TAB_FRAME_CLOSED(1337),
    // Highlighted Chat Tab
    HIGHLIGHTED_CHAT_TAB_NONE(-1),
    HIGHLIGHTED_CHAT_TAB_ALL(0),
    HIGHLIGHTED_CHAT_TAB_GAME(1),
    HIGHLIGHTED_CHAT_TAB_PUBLIC(2),
    HIGHLIGHTED_CHAT_TAB_PRIVATE(3),
    HIGHLIGHTED_CHAT_TAB_CHANNEL(4),
    HIGHLIGHTED_CHAT_TAB_CLAN(5),
    HIGHLIGHTED_CHAT_TAB_TRADE(6),
    // Bank Scrollbar Position
    BANK_SCROLLBAR_POSITION_MIN(0),
    BANK_SCROLLBAR_POSITION_400_ITEMS_MAX(1511),
    // Report Ignore Player State
    REPORT_IGNORE_PLAYER_CHECKED(1),
    REPORT_IGNORE_PLAYER_UNCHECKED(0),
    // Bank Worn Items
    BANK_WORN_ITEMS_SHOWN(1),
    BANK_WORN_ITEMS_HIDDEN(0),
    // Interface Tabs
    TAB_COMBAT_OPTIONS(0),
    TAB_SKILLS(1),
    TAB_QUEST_LIST(2),
    TAB_INVENTORY(3),
    TAB_WORN_EQUIPMENT(4),
    TAB_PRAYER(5),
    TAB_SPELLBOOK(6),
    TAB_CHAT_CHANNEL(7),
    TAB_ACC_MANAGEMENT(8),
    TAB_FRIEND_LIST(9),
    TAB_LOGOUT(10),
    TAB_SETTINGS(11),
    TAB_EMOTES(12),
    TAB_MUSIC(13),
    TAB_NOT_SELECTED(-1)
    ;

    // cache values on load
    private static final Map<Object, Object> hashMap = new HashMap<>();
    static {
        for (VarcIntValues varcEnum : VarcIntValues.values()) {
            hashMap.put(varcEnum.value, varcEnum);
        }
    }

    private final int value;

    VarcIntValues(int value) {
        this.value = value;
    }

    public static VarcIntValues valueOf(int varcEnum) {
        return (VarcIntValues) hashMap.get(varcEnum);
    }

    public int getValue() {
        return value;
    }
}
