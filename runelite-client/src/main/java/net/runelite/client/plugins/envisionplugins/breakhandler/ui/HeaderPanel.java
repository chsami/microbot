package net.runelite.client.plugins.envisionplugins.breakhandler.ui;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPlugin;
import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HeaderPanel extends JPanel {
    private final JLabel title = new JLabel("Micro Break Handler V" + BreakHandlerScript.version);
    private final JLabel icon = new JLabel();

    public HeaderPanel() {
        setStyle();

        icon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(BreakHandlerPlugin.discordLink));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setIconSize(23);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIconSize(22);
            }
        });

        add(title);
        add(icon);
    }

    private void setStyle() {
        setBorder(new EmptyBorder(10, 8, 9, 0));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        Dimension d = new Dimension(300, 60);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
        title.setForeground(Color.WHITE);
        icon.setBorder(new EmptyBorder(0, 5, 0, 0));

        Font baseFont = new JLabel().getFont();
        Font font = baseFont.deriveFont(baseFont.getStyle() | Font.BOLD, 16);
        title.setFont(font);

        setIconSize(22);
    }

    private void setIconSize(int size) {
        ImageIcon imageIcon = new ImageIcon(BreakHandlerPlugin.discordIcon);
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newImage);
        icon.setIcon(imageIcon);
    }
}
