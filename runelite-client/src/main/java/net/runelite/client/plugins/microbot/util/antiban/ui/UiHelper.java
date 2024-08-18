package net.runelite.client.plugins.microbot.util.antiban.ui;

import javax.swing.*;

public class UiHelper {
    public static void setupSlider(JSlider slider, int majorTickSpacing, int max, int minorTickSpacing) {
        slider.setMajorTickSpacing(majorTickSpacing);
        slider.setMinorTickSpacing(minorTickSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMinimum(0);
        slider.setMaximum(max);
    }
}
