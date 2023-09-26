package net.runelite.client.plugins.envisionplugins.breakhandler.ui.twofactorauth;

import javax.swing.*;
import java.awt.*;

public class TwoFactorAuthPanel extends JPanel {
    private final JPasswordField username = new JPasswordField();
    private final JPasswordField password = new JPasswordField();

    public TwoFactorAuthPanel() {
        setStyle();

        add(username);
        add(password);
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        username.setPreferredSize(new Dimension(100, 25));
        password.setPreferredSize(new Dimension(100, 25));
    }
}
