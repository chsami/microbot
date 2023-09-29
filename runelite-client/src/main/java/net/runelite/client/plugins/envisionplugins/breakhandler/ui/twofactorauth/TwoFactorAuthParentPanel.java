package net.runelite.client.plugins.envisionplugins.breakhandler.ui.twofactorauth;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPlugin;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TwoFactorAuthParentPanel extends JPanel {

    TwoFactorAuthPanel twoFactorAuthPanel = new TwoFactorAuthPanel();
    private final JLabel icon = new JLabel();
    JTitle title = new JTitle("F2A / PIN");

    public TwoFactorAuthParentPanel() {
        setStyle();

        add(title);
        add(icon);
        add(twoFactorAuthPanel);
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(20, 0, 0, 0));
        Dimension d = new Dimension(300, 80);
        setSize(d);
        setIconSize();
        title.setBorder(new EmptyBorder(0, 0, 1, 0));
        icon.setBorder(new EmptyBorder(0, 0, 4, 0));
    }

    private void setIconSize() {
        ImageIcon imageIcon = new ImageIcon(BreakHandlerPlugin.soonIcon);
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(34, 24, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newImage);
        icon.setIcon(imageIcon);
    }

    public JPasswordField getF2A() {
        return twoFactorAuthPanel.getF2A();
    }

    public JPasswordField getPin() {
        return twoFactorAuthPanel.getPin();
    }
}
