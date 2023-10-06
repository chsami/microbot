package net.runelite.client.plugins.envisionplugins.breakhandler.ui.worldhopping.enable;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WorldHoppingEnableFeature extends JCheckBox {

    public WorldHoppingEnableFeature() {
        setStyle();
    }

    private void setStyle() {
        setBorder(new EmptyBorder(0, 2, 0, 15));
    }

}
