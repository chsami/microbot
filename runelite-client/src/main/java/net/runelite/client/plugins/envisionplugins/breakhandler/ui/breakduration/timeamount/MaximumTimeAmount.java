package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration.timeamount;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;

import javax.swing.*;
import java.awt.*;

public class MaximumTimeAmount extends JPanel {
    public MaximumTimeAmount() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        final JLabel label1 = new JLabel();
        label1.setText("Max");
        add(label1);
        JTextField textField1 = new JTextField();
        textField1.setPreferredSize(new Dimension(70, 25));
        add(textField1);
    }
}
