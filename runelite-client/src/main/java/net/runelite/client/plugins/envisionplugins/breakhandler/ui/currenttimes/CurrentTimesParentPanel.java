package net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CurrentTimesParentPanel extends JPanel {
    public CurrentTimesParentPanel() {
        setStyle();

        add(new JTitle("Current Times"));
        add(new CurrentTimesRunPanel());
        add(new CurrentTimesBreakPanel());
        add(new JButton("Break Now"));
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(20, 0, 0, 0));
        Dimension d = new Dimension(300, 150);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }
}
