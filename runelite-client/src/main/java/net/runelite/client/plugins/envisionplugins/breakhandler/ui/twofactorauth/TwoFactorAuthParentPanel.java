package net.runelite.client.plugins.envisionplugins.breakhandler.ui.twofactorauth;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TwoFactorAuthParentPanel extends JPanel {

    TwoFactorAuthPanel twoFactorAuthPanel = new TwoFactorAuthPanel();

    public TwoFactorAuthParentPanel() {
        setStyle();

        add(new JTitle("F2A / PIN"));
        add(twoFactorAuthPanel);
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(20, 0, 0, 0));
        Dimension d = new Dimension(300, 80);
        setSize(d);
    }

    public JPasswordField getF2A() {
        return twoFactorAuthPanel.getF2A();
    }

    public JPasswordField getPin() {
        return twoFactorAuthPanel.getPin();
    }
}
