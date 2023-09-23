package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.utility.PanelUtils;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;

public class BreakTimerPanel extends JPanel {
    protected static FlatTextField durationTextField = new FlatTextField();

    public BreakTimerPanel() {

        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        durationTextField = new FlatTextField();
        durationTextField.setText("HH:MM:SS");
        durationTextField.setEditable(false);
        add(durationTextField);

        JButton breakNowButton = new JButton("Break Now");
        add(breakNowButton);
    }

    public static void setDurationTextField(long runtimeDuration) {
        durationTextField.setText(PanelUtils.getFormattedDuration(runtimeDuration));
    }
}
