package net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.enums.TimerTypes;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.utility.PanelUtils;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class MinimumTimeAmount extends JPanel {
    protected final FlatTextField durationTextField = new FlatTextField();

    protected TimerTypes timerType;

    public MinimumTimeAmount(TimerTypes myType) {
        setStyle();
        timerType = myType;

        final JLabel label1 = new JLabel("Min");
        add(label1);

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

                    if (myType == TimerTypes.BREAK) {
                        BreakHandlerScript.setMinBreakDuration(duration);
                    } else if (myType == TimerTypes.RUNTIME) {
                        BreakHandlerScript.setMinRunTimeDuration(duration);
                    }
                } catch (Exception ignored) {
                }

                updateDisplayInput(duration);
            }
        });
        add(durationTextField);
    }

    private void setStyle() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setMinimumSize(new Dimension(100, 5));
        durationTextField.setText("HH:MM:SS");
        durationTextField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        durationTextField.setEditable(true);
        durationTextField.setPreferredSize(new Dimension(70, 25));
        durationTextField.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
    }

    void updateDisplayInput(long seconds) {
        if (!durationTextField.getTextField().hasFocus()) {
            durationTextField.setText(PanelUtils.getFormattedDuration(seconds));
        }
    }

    public void setDurationFromConfig(long seconds) {
        if (timerType == TimerTypes.BREAK) {
            BreakHandlerScript.setMinBreakDuration(seconds);
        } else if (timerType == TimerTypes.RUNTIME) {
            BreakHandlerScript.setMinRunTimeDuration(seconds);
        }

        updateDisplayInput(seconds);
    }
}
