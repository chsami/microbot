package net.runelite.client.plugins.envisionplugins.breakhandler.ui.worldhopping;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.worldhopping.enable.WorldHoppingEnabledParentPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class WorldHoppingParentPanel extends JPanel {
    WorldHoppingRegionPanel worldHoppingRegionPanel = new WorldHoppingRegionPanel();

    public WorldHoppingParentPanel() {
        setStyle();

        add(new JTitle("Post Break World Hopping"));
        add(new WorldHoppingEnabledParentPanel());
        add(worldHoppingRegionPanel);
    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
    }

    public WorldHoppingRegionPanel getWorldHoppingRegionPanel() {
        return worldHoppingRegionPanel;
    }
}
