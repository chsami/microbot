package net.runelite.client.plugins.envisionplugins.breakhandler.ui.error;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ErrorParentPanel extends JPanel {
    JLabel label = new JLabel("", SwingConstants.CENTER);

    public ErrorParentPanel() {
        setStyle();

        add(label, BorderLayout.CENTER);

    }

    private void setStyle() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(0, 10, 0, 10));
        Dimension d = new Dimension(300, 150);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        label.setForeground(Color.RED);
    }

    public void setText(String failureMessage) {
        SwingUtilities.invokeLater(() -> {
            label.setText("<html>" + failureMessage + "</html>");
            repaint();
        });
    }
}
