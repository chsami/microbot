package net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimer;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;

public class RunTimerPanel extends JPanel {
    protected final FlatTextField durationTextField;

    public RunTimerPanel() {

        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        durationTextField = new FlatTextField();
        durationTextField.setText("HH:MM:SS");
        durationTextField.setEditable(false);
        add(durationTextField);
    }
}
