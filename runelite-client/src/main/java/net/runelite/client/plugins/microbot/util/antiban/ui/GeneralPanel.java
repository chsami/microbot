package net.runelite.client.plugins.microbot.util.antiban.ui;

import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class GeneralPanel extends JPanel {

    private final JCheckBox isEnabled = new JCheckBox("Enabled");
    private final JCheckBox universalAntiban = new JCheckBox("Universal Antiban");
    private final JCheckBox useContextualVariability = new JCheckBox("Use Contextual Variability");
    private final JCheckBox devDebug = new JCheckBox("Dev Debug");

    public GeneralPanel() {
        // Set the layout manager for the panel to GridBagLayout
        setLayout(new GridBagLayout());
        setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
        // Create a GridBagConstraints object to define the layout settings for each component
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left
        gbc.gridx = 0; // All components will be in column 0
        gbc.gridy = GridBagConstraints.RELATIVE; // Components will be placed in consecutive rows

        // Add the "Enabled" checkbox
        add(isEnabled, gbc);

        // Add the "Universal Antiban" checkbox
        add(universalAntiban, gbc);

        // Add the "Use Contextual Variability" checkbox
        add(useContextualVariability, gbc);

        // Add the "Dev Debug" checkbox
        add(devDebug, gbc);

        setupActionListeners();
    }

    private void setupActionListeners() {
        isEnabled.addActionListener(e -> Rs2AntibanSettings.antibanEnabled = isEnabled.isSelected());
        universalAntiban.addActionListener(e -> Rs2AntibanSettings.universalAntiban = universalAntiban.isSelected());
        useContextualVariability.addActionListener(e -> Rs2AntibanSettings.contextualVariability = useContextualVariability.isSelected());
        devDebug.addActionListener(e -> Rs2AntibanSettings.devDebug = devDebug.isSelected());

    }

    public void updateValues() {
        isEnabled.setSelected(Rs2AntibanSettings.antibanEnabled);
        universalAntiban.setSelected(Rs2AntibanSettings.universalAntiban);
        useContextualVariability.setSelected(Rs2AntibanSettings.contextualVariability);
        devDebug.setSelected(Rs2AntibanSettings.devDebug);
    }

}
