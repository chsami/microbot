package net.runelite.client.plugins.envisionplugins.breakhandler.ui.twofactorauth;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TwoFactorAuthTitlePanel extends JPanel {
    public TwoFactorAuthTitlePanel() {
        // Style & Layout Setup
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 10, 0, 10));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Label Setup
        JLabel label = new JLabel("F2A / PIN");
        add(label);
    }
}
