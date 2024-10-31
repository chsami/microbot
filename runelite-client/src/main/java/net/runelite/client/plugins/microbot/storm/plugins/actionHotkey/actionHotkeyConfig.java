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
                "set your parameters, and action. \n" +
                "the same applies for conditions.";
    }
    @ConfigSection(
            name = "Options",
            description = "options",
            position = 4,
            closedByDefault = true
    )
    String options = "Options";
    @ConfigSection(
            name = "First Action Category",
            description = "first action category",
            position = 8,
            closedByDefault = true
    )
    String firstActionCategories = "First Action Category";
    @ConfigSection(
            name = "Second Action Category",
            description = "second action category",
            position = 9,
            closedByDefault = true
    )
    String secondActionCategories = "Second Action Category";

    @ConfigSection(
            name = "First Condition Category",
            description = "first condition category",
            position = 10,
            closedByDefault = true
    )
    String firstConditionCategories = "First Condition Category";
    @ConfigSection(
            name = "Second Condition Category",
            description = "second condition category",
            position = 11,
            closedByDefault = true
    )
    String secondConditionCategories = "Second Condition Category";
    @ConfigSection(
            name = "debug",
            description = "debug",
            position = 12,
            closedByDefault = true
    )
    String debug = "Debug";
    @ConfigItem(keyName = "toggle mode", name = "toggle mode", description = "Do you want a toggle instead of keydown?", position = 0, section = options)
    default boolean toggle() { return false; }
    @ConfigItem(keyName = "alternate", name = "Alternate?", description = "When holding down the key do you want to alternate the actions?", position = 1, section = options)
    default boolean alternate() { return false; }
    @ConfigItem(keyName = "First Key", name = "First Key", description = "what should first key be?", position = 2, section = options)
    default Keys key1() { return Keys.VK_ESCAPE; }
    @ConfigItem(keyName = "firstActionCategory", name = "1A Category ", description = "pick category for first action", position = 3, section = options)
    default actionsCategories firstActionCategoryName() { return actionsCategories.RS2BANK; }
    @ConfigItem(keyName = "firstConditionCategory", name = "1C Category ", description = "pick category for first condition", position = 4, section = options)
    default conditionsCategories firstConditionCategoryName() { return conditionsCategories.NONE; }
    @ConfigItem(keyName = "Second Key", name = "Second Key", description = "what should Second Key be?", position = 5, section = options)
    default Keys key2() { return Keys.VK_CONTROL; }
    @ConfigItem(keyName = "secondActionCategory", name = "2A Category ", description = "pick category for second action", position = 6, section = options)
    default actionsCategories secondActionCategoryName() { return actionsCategories.RS2BANK; }
    @ConfigItem(keyName = "secondConditionCategory", name = "2C Category ", description = "pick category for second condition", position = 7, section = options)
    default conditionsCategories secondConditionCategoryName() { return conditionsCategories.NONE; }
    @ConfigItem(keyName = "sleepMin", name = "sleepMin", description = "Minimum sleep time", position = 8, section = options)
    @Range( min = 60, max = 30000 )
    default int sleepMin() { return 60; }
    @ConfigItem(keyName = "sleepMax", name = "sleepMax", description = "Maximum sleep time", position = 9, section = options)
    @Range( min = 90, max = 30000 )
    default int sleepMax() { return 160; }



    @ConfigItem(keyName = "firstActionIDEntry", name = "first parameter for first action?", description = "first parameter for first action?", position = 0, section = firstActionCategories)
    default String firstParameterOne() { return ""; }
    @ConfigItem(keyName = "firstActionParameter", name = "second parameter for first action?", description = "second parameter for first action?", position = 1, section = firstActionCategories)
    default String firstParameterTwo() { return ""; }
    @ConfigItem(keyName = "firstARs2Bank", name = "Rs2Bank Action", description = "pick action for Rs2Bank", position = 3, section = firstActionCategories)
    default aRs2Bank firstARs2Bank() { return aRs2Bank.OPEN_BANK; }
    @ConfigItem(keyName = "firstARs2GameObject", name = "Rs2GameObject Action", description = "pick action for Rs2GameObject", position = 3, section = firstActionCategories)
    default aRs2GameObject firstARs2GameObject() { return aRs2GameObject.OBJ_INTERACT; }
    @ConfigItem(keyName = "firstARs2Inventory", name = "Rs2Inventory Action", description = "pick action for Rs2Inventory", position = 3, section = firstActionCategories)
    default aRs2Inventory firstARs2Inventory() { return aRs2Inventory.INV_INTERACT; }
    @ConfigItem(keyName = "firstARs2Npc", name = "Rs2Npc Action", description = "pick action for Rs2Npc", position = 3, section = firstActionCategories)
    default aRs2Npc firstARs2Npc() { return aRs2Npc.NPC_INTERACT; }
    @ConfigItem(keyName = "firstARs2Player", name = "Rs2Player Action", description = "pick action for Rs2Player", position = 3, section = firstActionCategories)
    default aRs2Player firstARs2Player() { return aRs2Player.USE_FOOD; }
    @ConfigItem(keyName = "firstARs2Walker", name = "Rs2Walker Action", description = "pick action for Rs2Walker", position = 3, section = firstActionCategories)
    default aRs2Walker firstARs2Walker() { return aRs2Walker.WALK_FAST_CANVAS; }
    @ConfigItem(keyName = "firstARs2Widget", name = "Rs2Widget Action", description = "pick action for Rs2Widget", position = 3, section = firstActionCategories)
    default aRs2Widget firstARs2Widget() { return aRs2Widget.GET_WIDGET; }
    @ConfigItem(keyName = "firstARs2Magic", name = "Rs2Magic Action", description = "pick action for Rs2Magic", position = 3, section = firstActionCategories)
    default aRs2Magic firstARs2Magic() { return aRs2Magic.ALCH; }
    @ConfigItem(keyName = "firstAOther", name = "Other Action", description = "pick action for aOther", position = 3, section = firstActionCategories)
    default aOther firstAOther() { return aOther.PRINTLN; }


    @ConfigItem(keyName = "secondParameterOne", name = "first parameter for second action?", description = "first parameter for second action?", position = 0, section = secondActionCategories)
    default String secondParameterOne() { return ""; }
    @ConfigItem(keyName = "secondParameterTwo", name = "second parameter for second action?", description = "second parameter for second action?", position = 1, section = secondActionCategories)
    default String secondParameterTwo() { return ""; }
    @ConfigItem(keyName = "secondARs2Bank", name = "Rs2Bank Action", description = "pick action for Rs2Bank", position = 3, section = secondActionCategories)
    default aRs2Bank secondARs2Bank() { return aRs2Bank.OPEN_BANK; }
    @ConfigItem(keyName = "secondARs2GameObject", name = "Rs2GameObject Action", description = "pick action for Rs2GameObject", position = 3, section = secondActionCategories)
    default aRs2GameObject secondARs2GameObject() { return aRs2GameObject.OBJ_INTERACT; }
    @ConfigItem(keyName = "secondARs2Inventory", name = "Rs2Inventory Action", description = "pick action for Rs2Inventory", position = 3, section = secondActionCategories)
    default aRs2Inventory secondARs2Inventory() { return aRs2Inventory.INV_INTERACT; }
    @ConfigItem(keyName = "secondARs2Npc", name = "Rs2Npc Action", description = "pick action for Rs2Npc", position = 3, section = secondActionCategories)
    default aRs2Npc secondARs2Npc() { return aRs2Npc.NPC_INTERACT; }
    @ConfigItem(keyName = "secondARs2Player", name = "Rs2Player Action", description = "pick action for Rs2Player", position = 3, section = secondActionCategories)
    default aRs2Player secondARs2Player() { return aRs2Player.USE_FOOD; }
    @ConfigItem(keyName = "secondARs2Walker", name = "Rs2Walker Action", description = "pick action for Rs2Walker", position = 3, section = secondActionCategories)
    default aRs2Walker secondARs2Walker() { return aRs2Walker.WALK_FAST_CANVAS; }
    @ConfigItem(keyName = "secondARs2Widget", name = "Rs2Widget Action", description = "pick action for Rs2Widget", position = 3, section = secondActionCategories)
    default aRs2Widget secondARs2Widget() { return aRs2Widget.GET_WIDGET; }
    @ConfigItem(keyName = "secondARs2Magic", name = "Rs2Magic Action", description = "pick action for Rs2Magic", position = 3, section = secondActionCategories)
    default aRs2Magic secondARs2Magic() { return aRs2Magic.ALCH; }
    @ConfigItem(keyName = "secondAOther", name = "Other Action", description = "pick action for aOther", position = 3, section = secondActionCategories)
    default aOther secondAOther() { return aOther.PRINTLN; }

    @ConfigItem(keyName = "firstConditionParameterOne", name = "first parameter for condition one?", description = "what parameter one for first condition?", position = 0, section = firstConditionCategories)
    default String firstConditionParameterOne() { return ""; }
    @ConfigItem(keyName = "firstConditionParameterTwo", name = "second parameter for condition one?", description = "what parameter two for first condition?", position = 1, section = firstConditionCategories)
    default String firstConditionParameterTwo() { return ""; }
    @ConfigItem(keyName = "firstCRs2Inventory", name = "condition for firstCRs2Inventory", description = "What should the condition be for hotkey 1?", position = 3, section = firstConditionCategories)
    default cRs2Inventory firstCRs2Inventory() { return cRs2Inventory.HAS_ITEM; }
    @ConfigItem(keyName = "firstCOther", name = "condition for firstCOther", description = "What should the condition be for hotkey 1?", position = 3, section = firstConditionCategories)
    default cOther firstCOther() { return cOther.NONE; }

    @ConfigItem(keyName = "secondParameterOne", name = "first parameter for condition two?", description = "first parameter for second condition?", position = 0, section = secondConditionCategories)
    default String secondConditionParameterOne() { return ""; }
    @ConfigItem(keyName = "secondParameterTwo", name = "second parameter for condition two?", description = "second parameter for second condition?", position = 1, section = secondConditionCategories)
    default String secondConditionParameterTwo() { return ""; }
    @ConfigItem(keyName = "secondCondition", name = "condition for second", description = "What should the condition be for hotkey 1?", position = 3, section = secondConditionCategories)
    default cRs2Inventory secondCRs2Inventory() { return cRs2Inventory.HAS_ITEM; }
    @ConfigItem(keyName = "secondCondition", name = "condition for second", description = "What should the condition be for hotkey 2?", position = 3, section = secondConditionCategories)
    default cOther secondCOther() { return cOther.NONE; }


    @ConfigItem(keyName = "doAction", name = "Do Action?", description = "do you want this action done?", position = 0, section = debug)
    default boolean doAction() { return true; }
}
