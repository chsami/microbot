package net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimeduration;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.TimeAmountPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class RuntimeDurationParentPanel extends JPanel {
    public RuntimeDurationParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        add(new RuntimeDurationTitlePanel());
        add(new TimeAmountPanel());
    }
}
