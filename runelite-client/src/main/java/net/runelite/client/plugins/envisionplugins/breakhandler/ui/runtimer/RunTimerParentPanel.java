package net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimer;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer.BreakTimerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer.BreakTimerTitlePanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class RunTimerParentPanel extends JPanel {
    public RunTimerParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        add(new RunTimerTitlePanel());
        add(new RunTimerPanel());

    }
}
