package net.runelite.client.plugins.microbot.globval.enums;

import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.globval.GlobalWidgetInfo;

import java.awt.event.KeyEvent;


/**
 * An enumerated type representing the interface tabs and their WidgetInfo.
 */
public enum InterfaceTab {
    COMBAT("Combat Options", KeyEvent.VK_F5,
            GlobalWidgetInfo.FIXED_CLASSIC_COMBAT_OPTIONS,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_COMBAT_OPTIONS,
            GlobalWidgetInfo.RESIZABLE_MODERN_COMBAT_OPTIONS),
    SKILLS("Skills", 0,
            GlobalWidgetInfo.FIXED_CLASSIC_SKILLS,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_SKILLS,
            GlobalWidgetInfo.RESIZABLE_MODERN_SKILLS),
    QUESTS("Quest List", 0,
            GlobalWidgetInfo.FIXED_CLASSIC_QUESTS,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_QUESTS,
            GlobalWidgetInfo.RESIZABLE_MODERN_QUESTS),
    INVENTORY("Inventory", KeyEvent.VK_F1,
            GlobalWidgetInfo.FIXED_CLASSIC_INVENTORY,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_INVENTORY,
            GlobalWidgetInfo.RESIZABLE_MODERN_INVENTORY),
    EQUIPMENT("Worn Equipment", KeyEvent.VK_F2,
            GlobalWidgetInfo.FIXED_CLASSIC_WORN_EQUIPMENT,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_WORN_EQUIPMENT,
            GlobalWidgetInfo.RESIZABLE_MODERN_WORN_EQUIPMENT),
    PRAYER("Prayer", KeyEvent.VK_F3,
            GlobalWidgetInfo.FIXED_CLASSIC_PRAYER,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_PRAYER,
            GlobalWidgetInfo.RESIZABLE_MODERN_PRAYER),
    MAGIC("Magic", KeyEvent.VK_F4,
            GlobalWidgetInfo.FIXED_CLASSIC_MAGIC,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_MAGIC,
            GlobalWidgetInfo.RESIZABLE_MODERN_MAGIC),
    FRIENDS("Friends List", 0,
            GlobalWidgetInfo.FIXED_CLASSIC_FRIEND_LIST,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_FRIEND_LIST,
            GlobalWidgetInfo.RESIZABLE_MODERN_FRIEND_LIST),
    SETTINGS("Settings", 0,
            GlobalWidgetInfo.FIXED_CLASSIC_SETTINGS,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_SETTINGS,
            GlobalWidgetInfo.RESIZABLE_MODERN_SETTINGS),
    MUSIC("Music Player", 0,
            GlobalWidgetInfo.FIXED_CLASSIC_MUSIC_PLAYER,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_MUSIC_PLAYER,
            GlobalWidgetInfo.RESIZABLE_MODERN_MUSIC_PLAYER),
    LOGOUT("Logout", 0,
            GlobalWidgetInfo.FIXED_CLASSIC_LOGOUT,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_LOGOUT,
            GlobalWidgetInfo.RESIZABLE_MODERN_LOGOUT),
    CHAT("Chat Channel", 0,
            GlobalWidgetInfo.FIXED_CLASSIC_CHAT_CHANNEL,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_CHAT_CHANNEL,
            GlobalWidgetInfo.RESIZABLE_MODERN_CHAT_CHANNEL),
    ACC_MAN("Account Management", 0,
            GlobalWidgetInfo.FIXED_CLASSIC_ACC_MANAGEMENT,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_ACC_MANAGEMENT,
            GlobalWidgetInfo.RESIZABLE_MODERN_ACC_MANAGEMENT),
    EMOTES("Emotes", 0,
            GlobalWidgetInfo.FIXED_CLASSIC_EMOTES,
            GlobalWidgetInfo.RESIZABLE_CLASSIC_EMOTES,
            GlobalWidgetInfo.RESIZABLE_MODERN_EMOTES),
    // bogus widget info
    NOTHING_SELECTED("NothingSelected", 0,
            GlobalWidgetInfo.CHATBOX_FULL_INPUT,
            GlobalWidgetInfo.CHATBOX_FULL_INPUT,
            GlobalWidgetInfo.CHATBOX_FULL_INPUT);

    private final String name;
    private final int hotkey;
    private final GlobalWidgetInfo fixedClassicInfo;
    private final GlobalWidgetInfo resizableClassicInfo;
    private final GlobalWidgetInfo resizableModernInfo;

    InterfaceTab(String name, int hotkey,
                 GlobalWidgetInfo fixedClassicInfo,
                 GlobalWidgetInfo resizableClassicInfo,
                 GlobalWidgetInfo resizableModernInfo) {
        this.name = name;
        this.hotkey = hotkey;
        this.fixedClassicInfo = fixedClassicInfo;
        this.resizableClassicInfo = resizableClassicInfo;
        this.resizableModernInfo = resizableModernInfo;
    }

    public String getName() {
        return name;
    }

    public int getHotkey() {
        return hotkey;
    }

    public Widget getFixedClassicWidget() {
        return Microbot.getClient().getWidget(fixedClassicInfo.getGroupId(), fixedClassicInfo.getChildId());
    }

    public Widget getResizableClassicWidget() {
        return Microbot.getClient().getWidget(resizableClassicInfo.getGroupId(), resizableClassicInfo.getChildId());
    }

    public Widget getResizableModernWidget() {
        return Microbot.getClient().getWidget(resizableModernInfo.getGroupId(), resizableModernInfo.getChildId());
    }
}
