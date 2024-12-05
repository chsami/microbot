package net.runelite.client.plugins.microbot.discord;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class DiscordPanel extends PluginPanel {
    private final DiscordPlugin plugin;
    private final DiscordConfig config;
    private final JCheckBox enableNotificationsCheckbox = new JCheckBox("Enable Notifications");
    private final JButton testButton = new JButton("Test Webhook");

    @Inject
    private DiscordPanel(DiscordPlugin plugin, DiscordConfig config) {
        this.plugin = plugin;
        this.config = config;

        setBorder(new EmptyBorder(10, 10, 10, 10));
        setLayout(new GridBagLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 10, 0);

        JLabel title = new JLabel("Discord Notifications");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, c);

        c.gridy++;
        JLabel webhookNote = new JLabel("<html>Configure Discord webhook URL in RuneLite profile settings</html>");
        webhookNote.setForeground(Color.LIGHT_GRAY);
        add(webhookNote, c);

        c.gridy++;
        enableNotificationsCheckbox.setSelected(config.enableNotifications());
        enableNotificationsCheckbox.setForeground(Color.WHITE);
        enableNotificationsCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        enableNotificationsCheckbox.addActionListener(e -> 
            plugin.updateConfig("enableNotifications", enableNotificationsCheckbox.isSelected()));
        add(enableNotificationsCheckbox, c);

        c.gridy++;
        testButton.setBackground(ColorScheme.BRAND_ORANGE);
        testButton.setForeground(Color.WHITE);
        testButton.setFocusPainted(false);
        testButton.addActionListener(e -> plugin.testWebhook());
        add(testButton, c);

        c.gridy++;
        c.insets = new Insets(10, 0, 5, 0);
        JPanel eventPanel = new JPanel(new GridBagLayout());
        eventPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        eventPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ColorScheme.BRAND_ORANGE),
            "Event Notifications",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            Color.WHITE
        ));

        GridBagConstraints ec = new GridBagConstraints();
        ec.fill = GridBagConstraints.HORIZONTAL;
        ec.weightx = 1;
        ec.gridx = 0;
        ec.gridy = 0;
        ec.insets = new Insets(0, 5, 5, 5);

        JCheckBox loginLogoutCheckbox = new JCheckBox("Login/Logout");
        loginLogoutCheckbox.setSelected(config.notifyLoginLogout());
        loginLogoutCheckbox.setForeground(Color.WHITE);
        loginLogoutCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        loginLogoutCheckbox.addActionListener(e ->
            plugin.updateConfig("notifyLoginLogout", loginLogoutCheckbox.isSelected()));
        eventPanel.add(loginLogoutCheckbox, ec);

        ec.gridy++;
        JCheckBox deathCheckbox = new JCheckBox("Player Death");
        deathCheckbox.setSelected(config.notifyDeath());
        deathCheckbox.setForeground(Color.WHITE);
        deathCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        deathCheckbox.addActionListener(e ->
            plugin.updateConfig("notifyDeath", deathCheckbox.isSelected()));
        eventPanel.add(deathCheckbox, ec);

        ec.gridy++;
        JCheckBox levelUpCheckbox = new JCheckBox("Level Up");
        levelUpCheckbox.setSelected(config.notifyLevelUp());
        levelUpCheckbox.setForeground(Color.WHITE);
        levelUpCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        levelUpCheckbox.addActionListener(e ->
            plugin.updateConfig("notifyLevelUp", levelUpCheckbox.isSelected()));
        eventPanel.add(levelUpCheckbox, ec);

        add(eventPanel, c);
    }
} 