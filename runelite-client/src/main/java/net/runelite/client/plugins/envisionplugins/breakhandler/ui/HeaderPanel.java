package net.runelite.client.plugins.envisionplugins.breakhandler.ui;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HeaderPanel extends JPanel {
    public HeaderPanel() {

        // Style & Layout Setup
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        Dimension d = new Dimension(300, 50);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);

        // Label Setup
        JLabel title = new JLabel("Micro Break Handler V" + BreakHandlerScript.version);
        title.setForeground(Color.WHITE);
        add(title);
    }
}
