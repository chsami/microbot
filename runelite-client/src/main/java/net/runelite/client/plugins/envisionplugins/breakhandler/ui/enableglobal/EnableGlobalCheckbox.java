package net.runelite.client.plugins.envisionplugins.breakhandler.ui.enableglobal;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class EnableGlobalCheckbox extends JCheckBox {
    int state = ItemEvent.DESELECTED;

    public EnableGlobalCheckbox() {
        setStyle();

        addItemListener((e) -> {
            state = e.getStateChange();
        });
    }

    private void setStyle() {
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    public boolean pluginEnabledBoxChecked() {
        return state == ItemEvent.SELECTED;
    }

}