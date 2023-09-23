package net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class TimeAmountPanel extends JPanel {
    public TimeAmountPanel(TimerTypes myType) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        add(new MinimumTimeAmount(myType));
        add(new MaximumTimeAmount(myType));
    }
}
