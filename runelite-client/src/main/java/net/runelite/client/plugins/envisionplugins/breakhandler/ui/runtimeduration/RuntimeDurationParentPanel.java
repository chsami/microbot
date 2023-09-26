package net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimeduration;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.MaximumTimeAmount;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.MinimumTimeAmount;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.TimeAmountPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.enums.TimerTypes;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RuntimeDurationParentPanel extends JPanel {
    private final TimeAmountPanel timeAmountPanel = new TimeAmountPanel(TimerTypes.RUNTIME);

    public RuntimeDurationParentPanel() {
        setStyle();

        add(new JTitle("Run Time Duration"));
        add(timeAmountPanel);
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
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
