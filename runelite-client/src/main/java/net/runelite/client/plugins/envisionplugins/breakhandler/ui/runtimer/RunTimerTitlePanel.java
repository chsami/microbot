package net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimer;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RunTimerTitlePanel extends JPanel {
    public RunTimerTitlePanel() {
        // Style & Layout Setup
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Label Setup
        JLabel label = new JLabel("Run Timer");
        add(label);
    }
}
