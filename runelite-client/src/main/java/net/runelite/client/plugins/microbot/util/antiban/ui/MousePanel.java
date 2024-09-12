package net.runelite.client.plugins.microbot.util.antiban.ui;

import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

import static net.runelite.client.plugins.microbot.util.antiban.ui.UiHelper.setupSlider;

public class MousePanel extends JPanel {
    private final JCheckBox useNaturalMouse = new JCheckBox("Use Natural Mouse");
    private final JCheckBox simulateMistakes = new JCheckBox("Simulate Mistakes");
    private final JCheckBox moveMouseOffScreen = new JCheckBox("Move Mouse Off Screen");
    private final JCheckBox moveMouseRandomly = new JCheckBox("Move Mouse Randomly");
    private final JSlider moveMouseRandomlyChance = new JSlider(0, 100, (int) (Rs2AntibanSettings.moveMouseRandomlyChance * 100));
    private final JLabel moveMouseRandomlyChanceLabel = new JLabel("Random Mouse Movement (%): " + (int) (Rs2AntibanSettings.moveMouseRandomlyChance * 100));

    public MousePanel() {
        // Set the layout manager for the panel to GridBagLayout
        setLayout(new GridBagLayout());
        setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
        setupSlider(moveMouseRandomlyChance, 20, 100, 10);

        // Create a GridBagConstraints object to define the layout settings for each component
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left
        gbc.gridx = 0; // All components will be in column 0
        gbc.gridy = GridBagConstraints.RELATIVE; // Components will be placed in consecutive rows

        // Add the "Use Natural Mouse" checkbox
        add(useNaturalMouse, gbc);

        // Add a gap between "Use Natural Mouse" and the rest of the settings
        gbc.insets = new Insets(20, 5, 5, 5); // Increase the top padding to create a larger gap
        add(Box.createVerticalStrut(15), gbc); // Add a vertical gap of 15 pixels

        // Add the "Simulate Mistakes" checkbox
        gbc.insets = new Insets(5, 5, 5, 5); // Reset padding for normal spacing
        add(simulateMistakes, gbc);

        // Add the "Move Mouse Off Screen" checkbox
        add(moveMouseOffScreen, gbc);

        // Add the "Move Mouse Randomly" checkbox
        add(moveMouseRandomly, gbc);

        // Add the "Random Mouse Movement" label
        add(moveMouseRandomlyChanceLabel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        // Add the "Random Mouse Movement" slider
        add(moveMouseRandomlyChance, gbc);

        setupActionListeners();
    }

    private void setupActionListeners() {
        useNaturalMouse.addActionListener(e -> Rs2AntibanSettings.naturalMouse = useNaturalMouse.isSelected());
        simulateMistakes.addActionListener(e -> Rs2AntibanSettings.simulateMistakes = simulateMistakes.isSelected());
        moveMouseOffScreen.addActionListener(e -> Rs2AntibanSettings.moveMouseOffScreen = moveMouseOffScreen.isSelected());
        moveMouseRandomly.addActionListener(e -> Rs2AntibanSettings.moveMouseRandomly = moveMouseRandomly.isSelected());
        moveMouseRandomlyChance.addChangeListener(e -> {
            Rs2AntibanSettings.moveMouseRandomlyChance = moveMouseRandomlyChance.getValue() / 100.0;
            moveMouseRandomlyChanceLabel.setText("Random Mouse Movement (%): " + moveMouseRandomlyChance.getValue());
        });
    }

    public void updateValues() {

        useNaturalMouse.setSelected(Rs2AntibanSettings.naturalMouse);
        simulateMistakes.setSelected(Rs2AntibanSettings.simulateMistakes);
        moveMouseOffScreen.setSelected(Rs2AntibanSettings.moveMouseOffScreen);
        moveMouseRandomly.setSelected(Rs2AntibanSettings.moveMouseRandomly);
        moveMouseRandomlyChance.setValue((int) (Rs2AntibanSettings.moveMouseRandomlyChance * 100));
        moveMouseRandomlyChanceLabel.setText("Random Mouse Movement (%): " + moveMouseRandomlyChance.getValue());
    }
}
