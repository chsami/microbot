package net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.utility.PanelUtils;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;
import java.awt.*;

public class CurrentTimesRunPanel extends JPanel {
    protected static FlatTextField durationTextField = new FlatTextField();
    private final JLabel label = new JLabel("Run Timer");

    public CurrentTimesRunPanel() {
        setStyle();

        add(label);
        add(durationTextField);
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        label.setPreferredSize(new Dimension(72, 20));
        durationTextField = new FlatTextField();
        durationTextField.setText("HH:MM:SS");
        durationTextField.setEditable(false);
        durationTextField.setBackground(ColorScheme.DARK_GRAY_COLOR);
        durationTextField.setPreferredSize(new Dimension(72, 20));
    }

    public static void setDurationTextField(long runtimeDuration) {
        durationTextField.setText(PanelUtils.getFormattedDuration(runtimeDuration));
    }
}
