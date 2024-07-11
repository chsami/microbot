package net.runelite.client.ui.components;

import javax.annotation.Nullable;
import javax.swing.Icon;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ComboBoxIconEntry
{
    private Icon icon;
    private String text;
    @Nullable
    private Object data;
}