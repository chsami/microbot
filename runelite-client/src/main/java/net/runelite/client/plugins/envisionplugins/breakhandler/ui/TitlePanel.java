package net.runelite.client.plugins.envisionplugins.breakhandler.ui;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TitlePanel extends JPanel {
    public TitlePanel() {

        // Style & Layout Setup
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Label Setup
        JLabel title = new JLabel("Micro Break Handler V" + BreakHandlerScript.version);
        title.setForeground(Color.WHITE);
        add(title);
    }
}
