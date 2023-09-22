package net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimer;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class RunTimerPanel extends JPanel {
    public RunTimerPanel() {

        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JTextField timer = new JTextField("XX:XX");
        timer.setEditable(false);
        add(timer);
    }
}
