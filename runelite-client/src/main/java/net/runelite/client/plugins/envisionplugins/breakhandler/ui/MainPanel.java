package net.runelite.client.plugins.envisionplugins.breakhandler.ui;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    public MainPanel() {
        // Style & Layout Setup
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setLayout(new BorderLayout());

        // Title Panel Setup
        TitlePanel titlePanel = new TitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        setVisible(true);
    }
}
