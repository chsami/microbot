package net.runelite.client.plugins.envisionplugins.breakhandler.ui.regeneratetimes;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RegenerateTimesParentPanel extends JPanel {
    public RegenerateTimesParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(20, 0, 0, 0));

        add(new JTitle("Regenerate Times"));
        add(new JButton("Run Time"));
        add(new JButton("Break Time"));
    }
}
