package net.runelite.client.plugins.envisionplugins.breakhandler.ui.worldhopping.enable;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class WorldHoppingEnabledParentPanel extends JPanel {
    WorldHoppingEnableFeature worldHoppingEnableFeature = new WorldHoppingEnableFeature();
    WorldHoppingEnableMembers worldHoppingEnableMembers = new WorldHoppingEnableMembers();

    public WorldHoppingEnabledParentPanel() {
        setStyle();

        add(new JLabel("Enable"));
        worldHoppingEnableFeature.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                BreakHandlerScript.setEnableWorldHoppingPostBreak(e.getStateChange() == 1);
            }
        });

        add(worldHoppingEnableFeature);

        add(new JLabel("Members"));
        worldHoppingEnableMembers.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                BreakHandlerScript.setUseMemberWorldsToHop(e.getStateChange() == 1);
            }
        });
        add(worldHoppingEnableMembers);
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
    }

    public WorldHoppingEnableFeature getWorldHoppingEnableFeature() {
        return worldHoppingEnableFeature;
    }

    public WorldHoppingEnableMembers getWorldHoppingEnableMembers() {
        return worldHoppingEnableMembers;
    }
}
