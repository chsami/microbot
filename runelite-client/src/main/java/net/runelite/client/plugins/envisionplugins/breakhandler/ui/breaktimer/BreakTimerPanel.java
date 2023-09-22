package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class BreakTimerPanel extends JPanel {
    public BreakTimerPanel() {

        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JTextField timer = new JTextField("XX:XX");
        timer.setEditable(false);
        add(timer);

        JButton breakNowButton = new JButton("Break Now");
        add(breakNowButton);
    }
}
