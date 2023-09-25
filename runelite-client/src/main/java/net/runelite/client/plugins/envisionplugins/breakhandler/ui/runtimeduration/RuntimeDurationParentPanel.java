package net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimeduration;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.TimeAmountPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.TimerTypes;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class RuntimeDurationParentPanel extends JPanel {
    private final TimeAmountPanel timeAmountPanel;

    public RuntimeDurationParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BreakHandlerPanel.BORDER);

        add(new RuntimeDurationTitlePanel());
        timeAmountPanel = new TimeAmountPanel(TimerTypes.RUNTIME);
        add(timeAmountPanel);
    }

    public TimeAmountPanel getTimeAmountPanel() {
        return timeAmountPanel;
    }
}
