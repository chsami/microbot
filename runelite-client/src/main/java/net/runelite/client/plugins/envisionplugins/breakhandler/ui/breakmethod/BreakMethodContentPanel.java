package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakmethod;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BreakMethodContentPanel extends JPanel {
    public BreakMethodContentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 10));

        JLabel breakMethodLabel = new JLabel();
        breakMethodLabel.setText("Break Method");
        breakMethodLabel.setForeground(Color.WHITE);
        breakMethodLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        add(breakMethodLabel, BorderLayout.NORTH);

        String[] breakMethods = {"AFK", "LOGOUT"};
        JComboBox<String> breakingMethodComboBox = new JComboBox<>(breakMethods);
        add(breakingMethodComboBox, BorderLayout.SOUTH);
    }
}
