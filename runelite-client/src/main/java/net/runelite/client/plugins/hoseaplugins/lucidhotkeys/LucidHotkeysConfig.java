package net.runelite.client.plugins.hoseaplugins.lucidhotkeys;

import net.runelite.client.config.*;

@ConfigGroup("lucid-hotkeys")
public interface LucidHotkeysConfig extends Config
{
    @ConfigSection(name = "General", description = "General settings", position = 0)
    String generalSection = "General";

    @ConfigSection(name = "Preset Loading/Saving", description = "Save/Load a custom preset", position = 1)
    String presetSection = "Preset Loading/Saving";

    @ConfigSection(name = "Custom Variables", description = "Set custom variables here", position = 2)
    String customVariablesSection = "Custom Variables";

    @ConfigSection(name = "Custom Hotkey 1", description = "Hotkey # 1", position = 3)
    String section1 = "Custom Hotkey 1";

    @ConfigSection(name = "Custom Hotkey 2", description = "Hotkey # 2", position = 4)
    String section2 = "Custom Hotkey 2";

    @ConfigSection(name = "Custom Hotkey 3", description = "Hotkey # 3", position = 5)
    String section3 = "Custom Hotkey 3";

    @ConfigSection(name = "Custom Hotkey 4", description = "Hotkey # 4", position = 6)
    String section4 = "Custom Hotkey 4";

    @ConfigSection(name = "Custom Hotkey 5", description = "Hotkey # 5", position = 7)
    String section5 = "Custom Hotkey 5";

    @ConfigSection(name = "Custom Hotkey 6", description = "Hotkey # 6", position = 8)
    String section6 = "Custom Hotkey 6";

    @ConfigSection(name = "Custom Hotkey 7", description = "Hotkey # 7", position = 9)
    String section7 = "Custom Hotkey 7";

    @ConfigSection(name = "Custom Hotkey 8", description = "Hotkey # 8", position = 10)
    String section8 = "Custom Hotkey 8";

    @ConfigSection(name = "Custom Hotkey 9", description = "Hotkey # 9", position = 11)
    String section9 = "Custom Hotkey 9";

    @ConfigSection(name = "Custom Hotkey 10", description = "Hotkey # 10", position = 12)
    String section10 = "Custom Hotkey 10";

    @ConfigSection(name = "Custom Hotkey 11", description = "Hotkey # 11", position = 13)
    String section11 = "Custom Hotkey 11";

    @ConfigSection(name = "Custom Hotkey 12", description = "Hotkey # 12", position = 14)
    String section12 = "Custom Hotkey 12";

    @ConfigSection(name = "Custom Hotkey 13", description = "Hotkey # 13", position = 15)
    String section13 = "Custom Hotkey 13";

    @ConfigSection(name = "Custom Hotkey 14", description = "Hotkey # 14", position = 16)
    String section14 = "Custom Hotkey 14";

    @ConfigSection(name = "Custom Hotkey 15", description = "Hotkey # 15", position = 17)
    String section15 = "Custom Hotkey 15";

    // General Settings

    @ConfigItem(name = "Use As Bot", description = "Ignores hotkeys and evaluates all preset slot preconditions each game tick and will execute the actions that its able to", position = 0, keyName = "useAsBot", section = generalSection)
    default boolean useAsBot()
    {
        return false;
    }

    @ConfigItem(name = "Debug Output", description = "Will output text in chat and in runelite log telling you if the activated hotkey precondition is valid and what it activates", position = 1, keyName = "debugOutput", section = generalSection)
    default boolean debugOutput()
    {
        return false;
    }

    // Preset Loading/Saving

    @ConfigItem(name = "Preset Name", description = "Name of the preset (replaces all non-alphanumerical characters with a space)", position = 0, keyName = "presetName", section = presetSection)
    default String presetName()
    {
        return "";
    }

    @ConfigItem(name = "Load Preset Hotkey", description =  "Loads the preset with the saved preset in your runelite/lucid-hotkeys/ folder", position = 1, keyName = "loadPresetHotkey", section = presetSection)
    default Keybind loadPresetHotkey()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Save Preset Hotkey", description =  "Saves the preset as a JSON file to your runelite/lucid-hotkeys/ folder", position = 2, keyName = "savePresetHotkey", section = presetSection)
    default Keybind savePresetHotkey()
    {
        return Keybind.NOT_SET;
    }

    // Custom Variables

    @ConfigItem(name = "Custom Variables", description = "format: variableName=value,variableName=value  e.g. time=1", position = 0, keyName = "customVariables", section = customVariablesSection)
    default String customVariables()
    {
        return "i=1 /";
    }

    // Custom hotkey 1

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey1", section = section1)
    default Keybind hotkey1()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions1", section = section1)
    default String actions1()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions1", section = section1)
    default String preconditions1()
    {
        return "";
    }

    // Custom hotkey 2

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey2", section = section2)
    default Keybind hotkey2()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions2", section = section2)
    default String actions2()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions2", section = section2)
    default String preconditions2()
    {
        return "";
    }

    // Custom hotkey 3

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey3", section = section3)
    default Keybind hotkey3()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions3", section = section3)
    default String actions3()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions3", section = section3)
    default String preconditions3()
    {
        return "";
    }

    // Custom hotkey 4

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey4", section = section4)
    default Keybind hotkey4()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions4", section = section4)
    default String actions4()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions4", section = section4)
    default String preconditions4()
    {
        return "";
    }

    // Custom hotkey 5

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey5", section = section5)
    default Keybind hotkey5()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions5", section = section5)
    default String actions5()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions5", section = section5)
    default String preconditions5()
    {
        return "";
    }

    // Custom hotkey 6

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey6", section = section6)
    default Keybind hotkey6()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions6", section = section6)
    default String actions6()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions6", section = section6)
    default String preconditions6()
    {
        return "";
    }

    // Custom hotkey 7

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey7", section = section7)
    default Keybind hotkey7()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions7", section = section7)
    default String actions7()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions7", section = section7)
    default String preconditions7()
    {
        return "";
    }

    // Custom hotkey 8

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey8", section = section8)
    default Keybind hotkey8()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions8", section = section8)
    default String actions8()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions8", section = section8)
    default String preconditions8()
    {
        return "";
    }

    // Custom hotkey 9

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey9", section = section9)
    default Keybind hotkey9()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions9", section = section9)
    default String actions9()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions9", section = section9)
    default String preconditions9()
    {
        return "";
    }

    // Custom hotkey 10

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey10", section = section10)
    default Keybind hotkey10()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions10", section = section10)
    default String actions10()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions10", section = section10)
    default String preconditions10()
    {
        return "";
    }

    // Custom hotkey 11

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey11", section = section11)
    default Keybind hotkey11()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions11", section = section11)
    default String actions11()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions11", section = section11)
    default String preconditions11()
    {
        return "";
    }

    // Custom hotkey 12

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey12", section = section12)
    default Keybind hotkey12()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions12", section = section12)
    default String actions12()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions12", section = section12)
    default String preconditions12()
    {
        return "";
    }

    // Custom hotkey 13

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey13", section = section13)
    default Keybind hotkey13()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions13", section = section13)
    default String actions13()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions13", section = section13)
    default String preconditions13()
    {
        return "";
    }

    // Custom hotkey 14

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey14", section = section14)
    default Keybind hotkey14()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions14", section = section14)
    default String actions14()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions14", section = section14)
    default String preconditions14()
    {
        return "";
    }

    // Custom hotkey 15

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey15", section = section15)
    default Keybind hotkey15()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Actions", description = "What actions the hotkey does. For info on how to setup, visit the Discord.", position = 2, keyName = "actions15", section = section15)
    default String actions15()
    {
        return "";
    }

    @ConfigItem(name = "Hotkey Pre-conditions", description = "What conditions are required for activation. For info on how to setup, visit the Discord.", position = 1, keyName = "preconditions15", section = section15)
    default String preconditions15()
    {
        return "";
    }
}
