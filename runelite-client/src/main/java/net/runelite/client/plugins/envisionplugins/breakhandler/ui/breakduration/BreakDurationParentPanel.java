package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration.timeamount.TimeAmountPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class BreakDurationParentPanel extends JPanel {
    public BreakDurationParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        add(new BreakDurationTitlePanel());
        add(new TimeAmountPanel());
    }
}
