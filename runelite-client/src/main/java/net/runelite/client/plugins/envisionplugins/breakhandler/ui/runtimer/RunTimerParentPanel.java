package net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimer;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer.BreakTimerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer.BreakTimerTitlePanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class RunTimerParentPanel extends JPanel {
    public RunTimerParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BreakHandlerPanel.BORDER);

        add(new RunTimerTitlePanel());
        add(new RunTimerPanel());

    }
}
