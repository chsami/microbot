package net.runelite.client.plugins.envisionplugins.breakhandler.ui.twofactorauth;

import javax.swing.*;
import java.awt.*;

public class TwoFactorAuthPanel extends JPanel {
    public TwoFactorAuthPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JPasswordField username = new JPasswordField();
        username.setPreferredSize(new Dimension(100, 25));
        JPasswordField password = new JPasswordField();
        password.setPreferredSize(new Dimension(100, 25));


        add(username);
        add(password);
    }
}
