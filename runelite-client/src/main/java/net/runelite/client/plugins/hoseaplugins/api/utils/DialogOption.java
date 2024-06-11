package net.runelite.client.plugins.hoseaplugins.api.utils;

import lombok.Getter;

public class DialogOption
{

    @Getter
    private int index;

    @Getter
    private String optionText;

    @Getter
    private int textColor;

    public DialogOption(int index, String optionText, int textColor)
    {
        this.index = index;
        this.optionText = optionText;
        this.textColor = textColor;
    }
}
