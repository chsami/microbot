package net.runelite.client.plugins.envisionplugins.breakhandler.ui.account;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountParentPanel extends JPanel {
    AccountPanel accountPanel = new AccountPanel();

    public AccountParentPanel() {
        setStyle();

        add(new JTitle("Username / Password"));
        add(accountPanel);
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(20, 0, 0, 0));
        Dimension d = new Dimension(300, 80);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    public JPasswordField getPassword() {
        return accountPanel.getPassword();
    }

    public JTextField getUsername() {
        return accountPanel.getUsername();
    }
}
