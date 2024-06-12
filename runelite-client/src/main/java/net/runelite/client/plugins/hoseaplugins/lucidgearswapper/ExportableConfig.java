package net.runelite.client.plugins.hoseaplugins.lucidgearswapper;

import lombok.Getter;
import net.runelite.client.config.Keybind;

public class ExportableConfig
{
    @Getter
    public boolean[] swapEnabled;

    @Getter
    public String[] swapString;

    @Getter
    public Keybind[] swapHotkey;

    @Getter
    public boolean[] equipFirstItem;

    @Getter
    public boolean[] toggleSpecOnActivation;

    @Getter
    public int[] specThreshold;

    public ExportableConfig()
    {
        swapEnabled = new boolean[6];
        swapString = new String[6];
        swapHotkey = new Keybind[6];
        equipFirstItem = new boolean[6];
        toggleSpecOnActivation = new boolean[6];
        specThreshold = new int[6];
    }

    public void setSwap(int index, final boolean swapEnabled, final String swapString,
                        final Keybind swapHotkey, final boolean equipFirstItem, final boolean toggleSpecOnActivation, final int specThreshold)
    {
        this.swapEnabled[index] = swapEnabled;
        this.swapString[index] = swapString;
        this.swapHotkey[index] = swapHotkey;
        this.equipFirstItem[index] = equipFirstItem;
        this.toggleSpecOnActivation[index] = toggleSpecOnActivation;
        this.specThreshold[index] = specThreshold;
    }

}