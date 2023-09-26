package net.runelite.client.plugins.envisionplugins.breakhandler.ui.common;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JTitle extends JPanel {
    private static final Font baseFont = new JLabel().getFont();
    private static final Font font = baseFont.deriveFont(baseFont.getStyle() | Font.BOLD);

    public JTitle(String text) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 10, 1, 10));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JLabel label = new JLabel(text);
        label.setFont(font);
        add(label);
    }

    public JTitle(String text, boolean noBottomMargin) {
        this(text);
        if (noBottomMargin) setBorder(new EmptyBorder(0, 10, 0, 10));
    }

}
