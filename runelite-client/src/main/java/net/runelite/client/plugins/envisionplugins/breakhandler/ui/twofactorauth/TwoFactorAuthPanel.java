package net.runelite.client.plugins.envisionplugins.breakhandler.ui.twofactorauth;

import javax.swing.*;
import java.awt.*;

public class TwoFactorAuthPanel extends JPanel {
    private final JPasswordField f2a = new JPasswordField();
    private final JPasswordField pin = new JPasswordField();

    public TwoFactorAuthPanel() {
        setStyle();

        add(f2a);
        add(pin);
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        f2a.setPreferredSize(new Dimension(100, 25));
        pin.setPreferredSize(new Dimension(100, 25));
    }

    public JPasswordField getF2A() {
        return f2a;
    }

    public JPasswordField getPin() {
        return pin;
    }
}
