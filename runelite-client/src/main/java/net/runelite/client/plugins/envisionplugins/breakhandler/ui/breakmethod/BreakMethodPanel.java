package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakmethod;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class BreakMethodPanel extends JPanel {
    public BreakMethodPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        String[] breakMethods = {"AFK", "LOGOUT"};
        JComboBox<String> breakingMethodComboBox = new JComboBox<>(breakMethods);
        add(breakingMethodComboBox, BorderLayout.SOUTH);
    }
}
