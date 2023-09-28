package net.runelite.client.plugins.envisionplugins.breakhandler.ui.regeneratetimes;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes.CurrentTimesRunPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RegenerateTimesParentPanel extends JPanel {

    private final JButton regenerateRunTimeBtn = new JButton("Run Time");

    public RegenerateTimesParentPanel() {
        setStyle();

        add(new JTitle("Regenerate Times"));

        add(regenerateRunTimeBtn);
        regenerateRunTimeBtn.addActionListener(e -> BreakHandlerScript.regenerateExpectedRunTime(true));
        JButton regenerateBreakTimeBtn = new JButton("Break Time");
        add(regenerateBreakTimeBtn);
        regenerateBreakTimeBtn.addActionListener(e -> BreakHandlerScript.regenerateExpectedBreakTime());
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(20, 0, 0, 0));
    }

    public void setRuntimeToDisabled() {
        regenerateRunTimeBtn.setEnabled(false);
    }

    public void setRuntimeToEnabled() {
        regenerateRunTimeBtn.setEnabled(true);
    }
}
