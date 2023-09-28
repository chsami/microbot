package net.runelite.client.plugins.envisionplugins.breakhandler.ui.account;

import javax.swing.*;
import java.awt.*;

public class AccountPanel extends JPanel {
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();

    public AccountPanel() {
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
