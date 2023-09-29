package net.runelite.client.plugins.envisionplugins.breakhandler.ui.worldhopping.enable;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class WorldHoppingEnabledParentPanel extends JPanel {

    public WorldHoppingEnabledParentPanel() {
        setStyle();

        add(new JLabel("Enable"));
        add(new WorldHoppingEnableFeature());

        add(new JLabel("Members"));
        add(new WorldHoppingEnableMembers());
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
    }

}
