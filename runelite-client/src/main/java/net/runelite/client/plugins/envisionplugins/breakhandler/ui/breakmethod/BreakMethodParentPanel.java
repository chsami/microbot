package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakmethod;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class BreakMethodParentPanel extends JPanel {
    private static final BreakMethodPanel breakMethodPanel = new BreakMethodPanel();

    public BreakMethodParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BreakHandlerPanel.BORDER);

        add(new BreakMethodTitlePanel());
        add(breakMethodPanel);
    }

    public static String getBreakMethod() {
        return breakMethodPanel.getBreakingMethod();
    }
}
