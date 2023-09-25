package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.TimeAmountPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.TimerTypes;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class BreakDurationParentPanel extends JPanel {
    private final TimeAmountPanel timeAmountPanel;

    public BreakDurationParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BreakHandlerPanel.BORDER);

        add(new BreakDurationTitlePanel());
        timeAmountPanel = new TimeAmountPanel(TimerTypes.BREAK);
        add(timeAmountPanel);
    }

    public TimeAmountPanel getTimeAmountPanel() {
        return timeAmountPanel;
    }
}
