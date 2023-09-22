package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BreakDurationTitlePanel extends JPanel {
    public BreakDurationTitlePanel() {
        // Style & Layout Setup
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 10, 0, 10));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Label Setup
        JLabel label = new JLabel("Break Duration");
        add(label);
    }
}
