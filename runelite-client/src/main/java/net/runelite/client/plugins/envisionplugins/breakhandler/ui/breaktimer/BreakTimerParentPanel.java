package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration.BreakDurationTitlePanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration.timeamount.TimeAmountPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class BreakTimerParentPanel extends JPanel {
    public BreakTimerParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        add(new BreakTimerTitlePanel());
        add(new BreakTimerPanel());
    }
}
