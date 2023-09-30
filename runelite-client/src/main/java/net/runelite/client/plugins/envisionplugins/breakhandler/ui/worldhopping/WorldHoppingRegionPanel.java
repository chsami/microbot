package net.runelite.client.plugins.envisionplugins.breakhandler.ui.worldhopping;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.ui.ColorScheme;
import net.runelite.http.api.worlds.WorldRegion;

import javax.swing.*;
import java.awt.*;

public class WorldHoppingRegionPanel extends JPanel {
    private static JComboBox<String> comboBox;

    public WorldHoppingRegionPanel() {
        setStyle();

        String[] regionTypes = {"United States", "United Kingdom", "Australia", "Germany"};
        comboBox = new JComboBox<>(regionTypes);

        comboBox.addActionListener(e -> {
            if (comboBox.getSelectedItem().toString().equals("United States")) {
                BreakHandlerScript.setWorldRegionToHopTo(WorldRegion.UNITED_STATES_OF_AMERICA);
            } else if (comboBox.getSelectedItem().toString().equals("United Kingdom")) {
                BreakHandlerScript.setWorldRegionToHopTo(WorldRegion.UNITED_KINGDOM);
            } else if (comboBox.getSelectedItem().toString().equals("Australia")) {
                BreakHandlerScript.setWorldRegionToHopTo(WorldRegion.AUSTRALIA);
            } else if (comboBox.getSelectedItem().toString().equals("Germany")) {
                BreakHandlerScript.setWorldRegionToHopTo(WorldRegion.GERMANY);
            }
        });

        add(comboBox);
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
    }

}
