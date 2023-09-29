package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakmethod;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class BreakMethodPanel extends JPanel {
    private static JComboBox<String> breakingMethodComboBox;

    public BreakMethodPanel() {
        setStyle();

        String[] breakMethods = {"AFK", "LOGOUT"};
        breakingMethodComboBox = new JComboBox<>(breakMethods);

        breakingMethodComboBox.addActionListener(e -> BreakHandlerScript.setBreakMethod(breakingMethodComboBox.getSelectedItem().toString()));

        add(breakingMethodComboBox);
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
    }

    public String getBreakingMethod() {
        return breakingMethodComboBox.getSelectedItem().toString();
    }
}
