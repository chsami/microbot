package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.MaximumTimeAmount;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.MinimumTimeAmount;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.TimeAmountPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.enums.TimerTypes;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class BreakDurationParentPanel extends JPanel {
    private final TimeAmountPanel timeAmountPanel = new TimeAmountPanel(TimerTypes.BREAK);

    public BreakDurationParentPanel() {
        setStyle();

        add(new JTitle("Break Duration"));
        add(timeAmountPanel);
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        Dimension d = new Dimension(300, 100);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    public TimeAmountPanel getTimeAmountPanel() {
        return timeAmountPanel;
    }

    public MinimumTimeAmount getMinimumTimeAmount() {
        return timeAmountPanel.getMinimumTimeAmount();
    }

    public MaximumTimeAmount getMaximumTimeAmount() {
        return timeAmountPanel.getMaximumTimeAmount();
    }
}
