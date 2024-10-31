package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums.*;

@ConfigGroup("actionHotkey")
@ConfigInformation("Storm's Simple action <br />" +
        "to hotkey script, more <br />" +
        "actions to be added <br />" +
        "later. useful for things <br />" +
        "like training prayer at <br />" +
        "the chaos altar, or <br />" +
        "anything else repetitive <br /> " +
        "or hard to click. must <br /> " +
        "have significant <br /> " +
        "differences in <br /> " +
        "sleep values, or will autocalculate")
public interface actionHotkeyConfig extends Config {
    @ConfigSection(
            name = "Instructions",
            description = "Instructions",
            position = 3
    )
    String instructions = "Instructions";
    @ConfigItem(
            keyName = "plugin instructions",
            name = "plugin instructions",
            description = "",
            position = 0,
            section = instructions
    )
    default String disclaimer() {
        return "Set the mode of operation and \n" +
                "Category under Options, then \n" +
                "go to the same Category to \n" +
                "set your parameters, and action.";
    }
    @ConfigSection(
            name = "Options",
            description = "options",
            position = 4,
            closedByDefault = true
    )
    String options = "Options";
    @ConfigSection(
            name = "Conditions",
            description = "Conditions",
            position = 7,
            closedByDefault = true
    )
    String conditionsSection = "Conditions";
    @ConfigSection(
            name = "First Category",
            description = "first category",
            position = 8,
            closedByDefault = true
    )
    String firstCategories = "First Category";
    @ConfigSection(
            name = "Second Category",
            description = "second category",
            position = 9,
            closedByDefault = true
    )
    String secondCategories = "Second Category";
    @ConfigSection(
            name = "debug",
            description = "debug",
            position = 10,
            closedByDefault = true
    )
    String debug = "Debug";
    @ConfigItem(keyName = "toggle mode", name = "toggle mode", description = "Do you want a toggle instead of keydown?", position = 0, section = options)
    default boolean toggle() { return false; }
    @ConfigItem(keyName = "alternate", name = "Alternate?", description = "When holding down the key do you want to alternate the actions?", position = 1, section = options)
    default boolean alternate() { return false; }
    @ConfigItem(keyName = "First Key", name = "First Key", description = "what should first key be?", position = 2, section = options)
    default Keys key1() { return Keys.VK_ESCAPE; }
    @ConfigItem(keyName = "firstCategory", name = "1 Category ", description = "pick category for first action", position = 3, section = options)
    default Categories firstCategoryName() { return Categories.RS2BANK; }
    @ConfigItem(keyName = "Second Key", name = "Second Key", description = "what should Second Key be?", position = 4, section = options)
    default Keys key2() { return Keys.VK_CONTROL; }
    @ConfigItem(keyName = "secondCategory", name = "2 Category ", description = "pick category for second action", position = 5, section = options)
    default Categories secondCategoryName() { return Categories.RS2BANK; }

    @ConfigItem(keyName = "sleepMin", name = "sleepMin", description = "Minimum sleep time", position = 6, section = options)
    @Range( min = 60, max = 30000 )
    default int sleepMin() { return 60; }
    @ConfigItem(keyName = "sleepMax", name = "sleepMax", description = "Maximum sleep time", position = 7, section = options)
    @Range( min = 90, max = 30000 )
    default int sleepMax() { return 160; }


    @ConfigItem(keyName = "firstCondition", name = "condition for first", description = "What should the condition be for hotkey 1?", position = 0, section = conditionsSection)
    default Conditionals conditionsForOne() { return Conditionals.NONE; }
    @ConfigItem(keyName = "firstConditionIDEntry", name = "first parameter for condition one?", description = "what ID to send to first condition?", position = 1, section = conditionsSection)
    default String firstConditionIDEntry() { return ""; }
    @ConfigItem(keyName = "firstConditionMenu", name = "second parameter for condition one?", description = "what condition one menu?", position = 2, section = conditionsSection)
    default String firstConditionMenu() { return ""; }
    @ConfigItem(keyName = "secondCondition", name = "condition for second", description = "What should the condition be for hotkey 2?", position = 3, section = conditionsSection)
    default Conditionals conditionsForTwo() { return Conditionals.NONE; }
    @ConfigItem(keyName = "secondConditionIDEntry", name = "first parameter for condition two?", description = "what ID to send to second condition?", position = 4, section = conditionsSection)
    default String secondConditionIDEntry() { return ""; }
    @ConfigItem(keyName = "secondConditionMenu", name = "second parameter for condition two?", description = "what condition two menu?", position = 5, section = conditionsSection)
    default String secondConditionMenu() { return ""; }

    @ConfigItem(keyName = "firstActionIDEntry", name = "first parameter for first action?", description = "what ID to send to action?", position = 0, section = firstCategories)
    default String firstActionIDEntry() { return ""; }
    @ConfigItem(keyName = "firstActionMenu", name = "second parameter for first action?", description = "what action menu", position = 1, section = firstCategories)
    default String firstActionMenu() { return ""; }
    @ConfigItem(keyName = "firstRs2Bank", name = "Rs2Bank Action", description = "pick action for Rs2Bank", position = 3, section = firstCategories)
    default sRs2Bank firstRs2Bank() { return sRs2Bank.OPEN_BANK; }
    @ConfigItem(keyName = "firstRs2GameObject", name = "Rs2GameObject Action", description = "pick action for Rs2GameObject", position = 3, section = firstCategories)
    default sRs2GameObject firstRs2GameObject() { return sRs2GameObject.OBJ_INTERACT; }
    @ConfigItem(keyName = "firstRs2Inventory", name = "Rs2Inventory Action", description = "pick action for Rs2Inventory", position = 3, section = firstCategories)
    default sRs2Inventory firstRs2Inventory() { return sRs2Inventory.INV_INTERACT; }
    @ConfigItem(keyName = "firstRs2Npc", name = "Rs2Npc Action", description = "pick action for Rs2Npc", position = 3, section = firstCategories)
    default sRs2Npc firstRs2Npc() { return sRs2Npc.NPC_INTERACT; }
    @ConfigItem(keyName = "firstRs2Player", name = "Rs2Player Action", description = "pick action for Rs2Player", position = 3, section = firstCategories)
    default sRs2Player firstRs2Player() { return sRs2Player.USE_FOOD; }
    @ConfigItem(keyName = "firstRs2Walker", name = "Rs2Walker Action", description = "pick action for Rs2Walker", position = 3, section = firstCategories)
    default sRs2Walker firstRs2Walker() { return sRs2Walker.WALK_FAST_CANVAS; }
    @ConfigItem(keyName = "firstRs2Widget", name = "Rs2Widget Action", description = "pick action for Rs2Widget", position = 3, section = firstCategories)
    default sRs2Widget firstRs2Widget() { return sRs2Widget.GET_WIDGET; }
    @ConfigItem(keyName = "firstRs2Magic", name = "Rs2Magic Action", description = "pick action for Rs2Magic", position = 3, section = firstCategories)
    default sRs2Magic firstRs2Magic() { return sRs2Magic.ALCH; }
    @ConfigItem(keyName = "firstOther", name = "Other Action", description = "pick action for Other", position = 3, section = firstCategories)
    default Other firstOther() { return Other.PRINTLN; }


    @ConfigItem(keyName = "secondActionIDEntry", name = "first parameter for second action?", description = "what ID to send to action?", position = 0, section = secondCategories)
    default String secondActionIDEntry() { return ""; }
    @ConfigItem(keyName = "secondActionMenu", name = "second parameter for second action?", description = "what action menu", position = 1, section = secondCategories)
    default String secondActionMenu() { return ""; }
    @ConfigItem(keyName = "secondRs2Bank", name = "Rs2Bank Action", description = "pick action for Rs2Bank", position = 3, section = secondCategories)
    default sRs2Bank secondRs2Bank() { return sRs2Bank.OPEN_BANK; }
    @ConfigItem(keyName = "secondRs2GameObject", name = "Rs2GameObject Action", description = "pick action for Rs2GameObject", position = 3, section = secondCategories)
    default sRs2GameObject secondRs2GameObject() { return sRs2GameObject.OBJ_INTERACT; }
    @ConfigItem(keyName = "secondRs2Inventory", name = "Rs2Inventory Action", description = "pick action for Rs2Inventory", position = 3, section = secondCategories)
    default sRs2Inventory secondRs2Inventory() { return sRs2Inventory.INV_INTERACT; }
    @ConfigItem(keyName = "secondRs2Npc", name = "Rs2Npc Action", description = "pick action for Rs2Npc", position = 3, section = secondCategories)
    default sRs2Npc secondRs2Npc() { return sRs2Npc.NPC_INTERACT; }
    @ConfigItem(keyName = "secondRs2Player", name = "Rs2Player Action", description = "pick action for Rs2Player", position = 3, section = secondCategories)
    default sRs2Player secondRs2Player() { return sRs2Player.USE_FOOD; }
    @ConfigItem(keyName = "secondRs2Walker", name = "Rs2Walker Action", description = "pick action for Rs2Walker", position = 3, section = secondCategories)
    default sRs2Walker secondRs2Walker() { return sRs2Walker.WALK_FAST_CANVAS; }
    @ConfigItem(keyName = "secondRs2Widget", name = "Rs2Widget Action", description = "pick action for Rs2Widget", position = 3, section = secondCategories)
    default sRs2Widget secondRs2Widget() { return sRs2Widget.GET_WIDGET; }
    @ConfigItem(keyName = "secondRs2Magic", name = "Rs2Magic Action", description = "pick action for Rs2Magic", position = 3, section = firstCategories)
    default sRs2Magic secondRs2Magic() { return sRs2Magic.ALCH; }
    @ConfigItem(keyName = "secondOther", name = "Other Action", description = "pick action for Other", position = 3, section = secondCategories)
    default Other secondOther() { return Other.PRINTLN; }

    @ConfigItem(keyName = "doAction", name = "Do Action?", description = "do you want this action done?", position = 0, section = debug)
    default boolean doAction() { return true; }
}
