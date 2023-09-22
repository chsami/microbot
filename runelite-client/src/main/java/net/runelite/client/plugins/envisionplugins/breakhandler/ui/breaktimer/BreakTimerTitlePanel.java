package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BreakTimerTitlePanel extends JPanel {
    public BreakTimerTitlePanel() {
        // Style & Layout Setup
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Label Setup
        JLabel label = new JLabel("Break Timer");
        add(label);
    }
}
