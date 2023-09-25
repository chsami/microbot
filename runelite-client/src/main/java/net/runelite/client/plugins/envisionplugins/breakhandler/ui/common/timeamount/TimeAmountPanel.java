package net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class TimeAmountPanel extends JPanel {
    private final MinimumTimeAmount minimumTimeAmount;
    private final MaximumTimeAmount maximumTimeAmount;

    public TimeAmountPanel(TimerTypes myType) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        minimumTimeAmount = new MinimumTimeAmount(myType);
        add(minimumTimeAmount);
        maximumTimeAmount = new MaximumTimeAmount(myType);
        add(maximumTimeAmount);
    }

    public MinimumTimeAmount getMinimumTimeAmount() {
        return minimumTimeAmount;
    }

    public MaximumTimeAmount getMaximumTimeAmount() {
        return maximumTimeAmount;
    }
}
