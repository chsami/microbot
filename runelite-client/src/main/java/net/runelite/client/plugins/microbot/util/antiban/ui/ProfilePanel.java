package net.runelite.client.plugins.microbot.util.antiban.ui;

import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private final JCheckBox enableProfileSwitching = new JCheckBox("Enable Profile Switching");
    private final JCheckBox adjustForTimeOfDay = new JCheckBox("Adjust For Time Of Day");
    private final JCheckBox simulatePlaySchedule = new JCheckBox("Simulate Play Schedule");

    public ProfilePanel() {
        // Set the layout manager for the panel to GridBagLayout
        setLayout(new GridBagLayout());
        setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
        // Create a GridBagConstraints object to define the layout settings for each component
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left
        gbc.gridx = 0; // All components will be in column 0
        gbc.gridy = GridBagConstraints.RELATIVE; // Components will be placed in consecutive rows

        // Add the "Enable Profile Switching" checkbox
        add(enableProfileSwitching, gbc);

        // Add the "Adjust For Time Of Day" checkbox
        add(adjustForTimeOfDay, gbc);

        // Add the "Simulate Play Schedule" checkbox
        add(simulatePlaySchedule, gbc);

        setupActionListeners();
    }

    private void setupActionListeners() {
        enableProfileSwitching.addActionListener(e -> Rs2AntibanSettings.profileSwitching = enableProfileSwitching.isSelected());
        adjustForTimeOfDay.addActionListener(e -> Rs2AntibanSettings.timeOfDayAdjust = adjustForTimeOfDay.isSelected());
        simulatePlaySchedule.addActionListener(e -> Rs2AntibanSettings.playSchedule = simulatePlaySchedule.isSelected());

    }

    public void updateValues() {
        enableProfileSwitching.setSelected(Rs2AntibanSettings.profileSwitching);
        adjustForTimeOfDay.setSelected(Rs2AntibanSettings.timeOfDayAdjust);
        simulatePlaySchedule.setSelected(Rs2AntibanSettings.playSchedule);
    }
}
