package net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;

import javax.swing.*;
import java.awt.*;

public class MinimumTimeAmount extends JPanel {

    public MinimumTimeAmount() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setMinimumSize(new Dimension(100, 5));
        final JLabel label1 = new JLabel();
        label1.setText("Min");
        add(label1);
        JTextField textField1 = new JTextField();
        textField1.setPreferredSize(new Dimension(70, 25));
        add(textField1);
    }
}
