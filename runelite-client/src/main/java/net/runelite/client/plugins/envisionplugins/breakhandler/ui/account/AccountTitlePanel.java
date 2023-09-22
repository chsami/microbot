package net.runelite.client.plugins.envisionplugins.breakhandler.ui.account;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountTitlePanel extends JPanel {
    public AccountTitlePanel() {
        // Style & Layout Setup
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 10, 0, 10));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Label Setup
        JLabel label = new JLabel("Username / Password");
        add(label);
    }
}
