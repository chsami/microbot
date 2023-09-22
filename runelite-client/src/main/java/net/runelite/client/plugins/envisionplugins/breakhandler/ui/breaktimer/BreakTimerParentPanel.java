package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class BreakTimerParentPanel extends JPanel {
    public BreakTimerParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BreakHandlerPanel.BORDER);

        add(new BreakTimerTitlePanel());
        add(new BreakTimerPanel());
    }
}
