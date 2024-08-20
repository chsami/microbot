package net.runelite.client.plugins.microbot.util.antiban.ui;

import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class NavigationPanel extends JPanel {

    public NavigationPanel(CardPanel cardPanel) {
        setLayout(new BorderLayout());

        // Create a navigation panel for the buttons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(1, 6)); // 1 rows, 6 column

        // Create buttons with icons
        JButton generalButton = createIconButton("general.png", "General");
        JButton activityButton = createIconButton("activity.png", "Activity");
        JButton profileButton = createIconButton("profile.png", "Profile");
        JButton mouseButton = createIconButton("mouse.png", "Mouse");
        JButton microBreakButton = createIconButton("microbreak.png", "MicroBreak");
        JButton cooldownButton = createIconButton("cooldown.png", "Cooldown");

        // Add buttons to the navigation panel
        navPanel.add(generalButton);
        navPanel.add(activityButton);
        navPanel.add(profileButton);
        navPanel.add(mouseButton);
        navPanel.add(microBreakButton);
        navPanel.add(cooldownButton);

        // Add navigation panel to the NavigationPanel
        add(navPanel, BorderLayout.CENTER);

        // Add action listeners to switch panels on button click
        generalButton.addActionListener(e -> cardPanel.showPanel("General"));
        activityButton.addActionListener(e -> cardPanel.showPanel("Activity"));
        profileButton.addActionListener(e -> cardPanel.showPanel("Profile"));
        mouseButton.addActionListener(e -> cardPanel.showPanel("Mouse"));
        microBreakButton.addActionListener(e -> cardPanel.showPanel("MicroBreak"));
        cooldownButton.addActionListener(e -> cardPanel.showPanel("Cooldown"));
    }

    private JButton createIconButton(String iconPath, String altText) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(Rs2Antiban.class.getResource(iconPath)));
        JButton button = new JButton(icon);
        button.setToolTipText(altText); // Set alt text for accessibility
        return button;
    }
}
