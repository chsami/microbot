package net.runelite.client.plugins.envisionplugins.breakhandler.ui.regeneratetimes;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegenerateTimesTitlePanel extends JPanel {
    public RegenerateTimesTitlePanel() {
        // Style & Layout Setup
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 10, 1, 10));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Label Setup
        JLabel label = new JLabel("Regenerate Times");
        Font font = label.getFont();

        label.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
        add(label);
    }
}
