package net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.utility.PanelUtils;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;

public class CurrentTimesRunPanel extends JPanel {
    protected static FlatTextField durationTextField = new FlatTextField();

    public CurrentTimesRunPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel label = new JLabel("Run Timer");
        add(label);

        durationTextField = new FlatTextField();
        durationTextField.setText("HH:MM:SS");
        durationTextField.setEditable(false);
        add(durationTextField);
    }

    public static void setDurationTextField(long runtimeDuration) {
        durationTextField.setText(PanelUtils.getFormattedDuration(runtimeDuration));
    }
}
