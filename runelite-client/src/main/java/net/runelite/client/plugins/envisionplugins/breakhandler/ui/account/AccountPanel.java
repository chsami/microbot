package net.runelite.client.plugins.envisionplugins.breakhandler.ui.account;

import javax.swing.*;
import java.awt.*;

public class AccountPanel extends JPanel {
    public AccountPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JTextField username = new JTextField();
        username.setPreferredSize(new Dimension(100, 25));
        JPasswordField password = new JPasswordField();
        password.setPreferredSize(new Dimension(100, 25));


        add(username);
        add(password);
    }
}
