package net.runelite.client.plugins.hoseaplugins.lucidhotkeys2;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("lucid-hotkeys2")
public interface LucidHotkeys2Config extends Config
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

    @ConfigItem(name = "Debug Output", description = "Will output text in chat and in runelite log telling you if the activated hotkey precondition is valid and what it activates", position = 0, keyName = "debugOutput", section = generalSection)
    default boolean debugOutput()
    {
        return false;
    }

    @ConfigItem(name = "Disable Use As Bot On Import", description = "Will set the Use As Bot option to false on all hotkeys to prevent them from auto-running when imported", position = 1, keyName = "disableRunAsBot", section = generalSection)
    default boolean disableRunAsBot()
    {
        return false;
    }

    @ConfigItem(name = "Debug Msg Color", description = "Color to use for debug messages.", position = 2, keyName = "debugColor", section = generalSection)
    default Color debugColor()
    {
        return Color.RED;
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

    @ConfigItem(name = "Show Overlay Panel", description = "Renders an overlay panel showing the current custom vars and their values", position = 0, keyName = "customVarPanel", section = customVariablesSection)
    default boolean customVarPanel()
    {
        return false;
    }

    @ConfigItem(name = "Custom Variables", description = "format: variableName = value e.g. time = 1 ;", position = 1, keyName = "customVariables", section = customVariablesSection)
    default String customVariables()
    {
        return "variable = 1;";
    }

    @ConfigItem(name = "Re-initialize when changed", description = "Will re-initialize the values when the config is changed, otherwise user vars only initialize on plugin start or with var reload action", position = 2, keyName = "initWhenChanged", section = customVariablesSection)
    default boolean initWhenChanged()
    {
        return false;
    }

    // Custom hotkey 1

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey1", section = section1)
    default Keybind hotkey1()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression1", section = section1)
    default String hotkeyExpression1()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot1", section = section1)
    default boolean useAsBot1()
    {
        return false;
    }

    // Custom hotkey 2

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey2", section = section2)
    default Keybind hotkey2()
    {
        return Keybind.NOT_SET;
    }
    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression2", section = section2)
    default String hotkeyExpression2()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot2", section = section2)
    default boolean useAsBot2()
    {
        return false;
    }

    // Custom hotkey 3

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey3", section = section3)
    default Keybind hotkey3()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression3", section = section3)
    default String hotkeyExpression3()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot3", section = section3)
    default boolean useAsBot3()
    {
        return false;
    }

    // Custom hotkey 4

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey4", section = section4)
    default Keybind hotkey4()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression4", section = section4)
    default String hotkeyExpression4()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot4", section = section4)
    default boolean useAsBot4()
    {
        return false;
    }

    // Custom hotkey 5

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey5", section = section5)
    default Keybind hotkey5()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression5", section = section5)
    default String hotkeyExpression5()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot5", section = section5)
    default boolean useAsBot5()
    {
        return false;
    }

    // Custom hotkey 6

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey6", section = section6)
    default Keybind hotkey6()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression6", section = section6)
    default String hotkeyExpression6()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot6", section = section6)
    default boolean useAsBot6()
    {
        return false;
    }

    // Custom hotkey 7

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey7", section = section7)
    default Keybind hotkey7()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression7", section = section7)
    default String hotkeyExpression7()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot7", section = section7)
    default boolean useAsBot7()
    {
        return false;
    }

    // Custom hotkey 8

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey8", section = section8)
    default Keybind hotkey8()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression8", section = section8)
    default String hotkeyExpression8()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot8", section = section8)
    default boolean useAsBot8()
    {
        return false;
    }

    // Custom hotkey 9

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey9", section = section9)
    default Keybind hotkey9()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression9", section = section9)
    default String hotkeyExpression9()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot9", section = section9)
    default boolean useAsBot9()
    {
        return false;
    }

    // Custom hotkey 10

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey10", section = section10)
    default Keybind hotkey10()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression10", section = section10)
    default String hotkeyExpression10()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot10", section = section10)
    default boolean useAsBot10()
    {
        return false;
    }

    // Custom hotkey 11

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey11", section = section11)
    default Keybind hotkey11()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression11", section = section11)
    default String hotkeyExpression11()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot11", section = section11)
    default boolean useAsBot11()
    {
        return false;
    }

    // Custom hotkey 12

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey12", section = section12)
    default Keybind hotkey12()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression12", section = section12)
    default String hotkeyExpression12()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot12", section = section12)
    default boolean useAsBot12()
    {
        return false;
    }

    // Custom hotkey 13

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey13", section = section13)
    default Keybind hotkey13()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression13", section = section13)
    default String hotkeyExpression13()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot13", section = section13)
    default boolean useAsBot13()
    {
        return false;
    }

    // Custom hotkey 14

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey14", section = section14)
    default Keybind hotkey14()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression14", section = section14)
    default String hotkeyExpression14()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot14", section = section14)
    default boolean useAsBot14()
    {
        return false;
    }

    // Custom hotkey 15

    @ConfigItem(name = "Hotkey", description = "Hotkey to activate actions", position = 0, keyName = "hotkey15", section = section15)
    default Keybind hotkey15()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Hotkey Expression:", description = "Write your hotkey activation conditions and actions here. Refer to the guide for more answers.", position = 1, keyName = "hotkeyExpression15", section = section15)
    default String hotkeyExpression15()
    {
        return "";
    }

    @ConfigItem(name = "Use As Bot", description = "Will evaluate this hotkey each game tick instead of waiting on a hotkey to evaluate it", position = 2, keyName = "useAsBot15", section = section15)
    default boolean useAsBot15()
    {
        return false;
    }
}
