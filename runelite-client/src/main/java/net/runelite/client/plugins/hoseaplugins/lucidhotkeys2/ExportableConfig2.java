package net.runelite.client.plugins.hoseaplugins.lucidhotkeys2;

import lombok.Getter;
import net.runelite.client.config.Keybind;

public class ExportableConfig2
{

    @Getter
    String userVars;

    @Getter
    public Keybind[] hotkey;

    @Getter
    public String[] hotkeyExpressions;

    @Getter
    public boolean[] useAsBot;

    public ExportableConfig2(String uservars)
    {
        userVars = uservars;
        hotkey = new Keybind[15];
        useAsBot = new boolean[15];
        hotkeyExpressions = new String[15];
    }

    public void setHotkeyConfig(int index, Keybind hotkey, String hotkeyExpression, boolean useAsBot)
    {
        this.hotkey[index] = hotkey;
        this.hotkeyExpressions[index] = hotkeyExpression;
        this.useAsBot[index] = useAsBot;
    }
}
