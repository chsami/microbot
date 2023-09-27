package net.runelite.client.plugins.envisionplugins.breakhandler.ui.regeneratetimes;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RegenerateTimesParentPanel extends JPanel {
    public RegenerateTimesParentPanel() {
        setStyle();

        add(new JTitle("Regenerate Times"));
        JButton regenerateRunTimeBtn = new JButton("Run Time");
        add(regenerateRunTimeBtn);
        regenerateRunTimeBtn.addActionListener(e -> BreakHandlerScript.regenerateExpectedRunTime());
        JButton regenerateBreakTimeBtn = new JButton("Break Time");
        add(regenerateBreakTimeBtn);
        regenerateBreakTimeBtn.addActionListener(e -> BreakHandlerScript.calcExpectedBreak());
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(20, 0, 0, 0));
    }
}
