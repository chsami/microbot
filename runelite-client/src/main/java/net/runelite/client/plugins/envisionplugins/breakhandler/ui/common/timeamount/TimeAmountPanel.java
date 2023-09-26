package net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.enums.TimerTypes;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class TimeAmountPanel extends JPanel {
    private final MinimumTimeAmount minimumTimeAmount;
    private final MaximumTimeAmount maximumTimeAmount;

    public TimeAmountPanel(TimerTypes myType) {
        setStyle();

        minimumTimeAmount = new MinimumTimeAmount(myType);
        add(minimumTimeAmount);
        maximumTimeAmount = new MaximumTimeAmount(myType);
        add(maximumTimeAmount);
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
    }

    public MinimumTimeAmount getMinimumTimeAmount() {
        return minimumTimeAmount;
    }

    public MaximumTimeAmount getMaximumTimeAmount() {
        return maximumTimeAmount;
    }
}
