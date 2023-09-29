package net.runelite.client.plugins.envisionplugins.breakhandler.ui.worldhopping;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class WorldHoppingRegionPanel extends JPanel {
    private static JComboBox<String> comboBox;

    public WorldHoppingRegionPanel() {
        setStyle();

        String[] regionTypes = {"United States", "United Kingdom", "Australia", "Germany"};
        comboBox = new JComboBox<>(regionTypes);

        comboBox.addActionListener(e -> {
            System.out.println("Region type changed");
        });

        add(comboBox);
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
    }

}
