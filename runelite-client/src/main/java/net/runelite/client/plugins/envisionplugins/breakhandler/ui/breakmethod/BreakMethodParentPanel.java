package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakmethod;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BreakMethodParentPanel extends JPanel {
    private static final BreakMethodPanel breakMethodPanel = new BreakMethodPanel();

    public BreakMethodParentPanel() {
        setStyle();

        add(new JTitle("Break Method", false));
        add(breakMethodPanel);
    }

    public static String getBreakMethod() {
        return breakMethodPanel.getBreakingMethod();
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(20, 0, 0, 0));
        Dimension d = new Dimension(300, 80);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }
}
