package net.runelite.client.plugins.envisionplugins.breakhandler.ui;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TitlePanel extends JPanel {
    public TitlePanel() {
        // Style & Layout Setup
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());

        // Label Setup
        JLabel title = new JLabel();
        title.setText("Micro Break Handler V" + BreakHandlerScript.version);
        title.setForeground(Color.WHITE);

        add(title, BorderLayout.WEST);
    }
}
