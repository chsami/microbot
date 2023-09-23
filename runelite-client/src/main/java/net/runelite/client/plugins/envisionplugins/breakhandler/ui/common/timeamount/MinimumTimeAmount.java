package net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.utility.PanelUtils;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class MinimumTimeAmount extends JPanel {
    protected final FlatTextField durationTextField;

    public MinimumTimeAmount() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setMinimumSize(new Dimension(100, 5));

        final JLabel label1 = new JLabel();
        label1.setText("Min");
        add(label1);

        durationTextField = new FlatTextField();
        durationTextField.setText("HH:MM:SS");
        durationTextField.setEditable(true);
        durationTextField.setPreferredSize(new Dimension(70, 25));
        durationTextField.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        durationTextField.addActionListener(e -> getParent().requestFocusInWindow());

        durationTextField.getTextField().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                durationTextField.getTextField().selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                long duration = 0;

                try {
                    duration = PanelUtils.stringToSeconds(durationTextField.getText());
                } catch (Exception ignored){
                }

                updateDisplayInput(duration);
            }
        });

        add(durationTextField);
    }

    void updateDisplayInput(long seconds)
    {
        if (!durationTextField.getTextField().hasFocus())
        {
            durationTextField.setText(PanelUtils.getFormattedDuration(seconds));
        }
    }
}
