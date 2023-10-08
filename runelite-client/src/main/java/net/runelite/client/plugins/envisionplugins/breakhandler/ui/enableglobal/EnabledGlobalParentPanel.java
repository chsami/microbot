package net.runelite.client.plugins.envisionplugins.breakhandler.ui.enableglobal;


import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.JTitle;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EnabledGlobalParentPanel extends JPanel {
    JTitle title = new JTitle("Enable Breaks");
    EnableGlobalCheckbox enableGlobalCheckbox = new EnableGlobalCheckbox();

    public EnabledGlobalParentPanel() {
        setStyle();

        add(title);
        add(enableGlobalCheckbox);
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBorder(new EmptyBorder(0, 45, 0, 0));
        Dimension d = new Dimension(300, 40);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        title.setBackground(ColorScheme.DARK_GRAY_COLOR);

    }

    public boolean pluginEnabledBoxChecked() {
        return enableGlobalCheckbox.pluginEnabledBoxChecked();
    }
}
