package net.runelite.client.plugins.microbot.discord;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class DiscordPanel extends PluginPanel {
    private final DiscordPlugin plugin;
    private final DiscordConfig config;
    private final JCheckBox enableNotificationsCheckbox = new JCheckBox("Enable Notifications");
    private final JButton testButton = new JButton("Test Webhook");
    private final JPanel proximitySettingsPanel = new JPanel();

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

        ec.gridy++;
        JCheckBox proximityAlertsCheckbox = new JCheckBox("Proximity Alerts");
        proximityAlertsCheckbox.setSelected(config.enableProximityAlerts());
        proximityAlertsCheckbox.setForeground(Color.WHITE);
        proximityAlertsCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        proximityAlertsCheckbox.addActionListener(e -> {
            boolean enabled = proximityAlertsCheckbox.isSelected();
            plugin.updateConfig("enableProximityAlerts", enabled);
            updateProximitySettingsVisibility(enabled);
        });
        eventPanel.add(proximityAlertsCheckbox, ec);

        proximitySettingsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        proximitySettingsPanel.setLayout(new GridBagLayout());
        GridBagConstraints psc = new GridBagConstraints();
        psc.fill = GridBagConstraints.HORIZONTAL;
        psc.weightx = 1;
        psc.gridx = 0;
        psc.gridy = 0;
        psc.insets = new Insets(0, 20, 5, 5);

        JPanel settingsRow = new JPanel(new GridLayout(2, 1, 0, 5));
        settingsRow.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        JPanel radiusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radiusPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JLabel radiusLabel = new JLabel("Alert Radius (in tiles):");
        radiusLabel.setForeground(Color.WHITE);
        radiusPanel.add(radiusLabel);

        JSpinner radiusSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        radiusSpinner.setValue(config.proximityRadius());
        radiusSpinner.addChangeListener(e ->
            plugin.updateConfig("proximityRadius", radiusSpinner.getValue()));
        
        ((JSpinner.DefaultEditor) radiusSpinner.getEditor()).getTextField().setColumns(3);
        radiusSpinner.setUI(new BasicSpinnerUI() {
            protected Component createNextButton() {
                return null;
            }
            protected Component createPreviousButton() {
                return null;
            }
        });

        JTextField spinnerTextField = ((JSpinner.DefaultEditor) radiusSpinner.getEditor()).getTextField();
        spinnerTextField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        spinnerTextField.setForeground(Color.WHITE);
        spinnerTextField.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        spinnerTextField.setPreferredSize(new Dimension(35, spinnerTextField.getPreferredSize().height));
        
        radiusPanel.add(radiusSpinner);
        settingsRow.add(radiusPanel);

        JPanel trackNewPlayersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        trackNewPlayersPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        JCheckBox onlyTrackNewPlayersCheckbox = new JCheckBox("Only Track New Players");
        onlyTrackNewPlayersCheckbox.setSelected(config.onlyTrackNewPlayers());
        onlyTrackNewPlayersCheckbox.setForeground(Color.WHITE);
        onlyTrackNewPlayersCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        onlyTrackNewPlayersCheckbox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        onlyTrackNewPlayersCheckbox.addActionListener(e ->
            plugin.updateConfig("onlyTrackNewPlayers", onlyTrackNewPlayersCheckbox.isSelected()));
        trackNewPlayersPanel.add(onlyTrackNewPlayersCheckbox);
        settingsRow.add(trackNewPlayersPanel);

        proximitySettingsPanel.add(settingsRow, psc);
        ec.gridy++;
        eventPanel.add(proximitySettingsPanel, ec);
        updateProximitySettingsVisibility(config.enableProximityAlerts());

        ec.gridy++;
        JPanel chatMonitorPanel = new JPanel(new GridBagLayout());
        chatMonitorPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        GridBagConstraints cmc = new GridBagConstraints();
        cmc.fill = GridBagConstraints.HORIZONTAL;
        cmc.weightx = 1;
        cmc.gridx = 0;
        cmc.gridy = 0;
        cmc.insets = new Insets(0, 0, 5, 0);

        JCheckBox chatMonitorCheckbox = new JCheckBox("Player Chat Monitor");
        chatMonitorCheckbox.setForeground(Color.WHITE);
        chatMonitorCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        chatMonitorPanel.add(chatMonitorCheckbox, cmc);

        cmc.gridy++;
        JTextField phraseInput = new JTextField(20);
        phraseInput.setToolTipText("Enter exact phrases to monitor, separated by commas (e.g., Bot, \"You're botting\", REPORTED)");
        phraseInput.setVisible(false);

        chatMonitorCheckbox.addActionListener(e -> {
            phraseInput.setVisible(chatMonitorCheckbox.isSelected());
            plugin.updateConfig("notifyChatMonitor", chatMonitorCheckbox.isSelected());
        });

        phraseInput.getDocument().addDocumentListener(new DocumentListener() {
            private void updatePhrases() {
                if (chatMonitorCheckbox.isSelected()) {
                    plugin.updateConfig("chatMonitorPhrases", phraseInput.getText());
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePhrases();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePhrases();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePhrases();
            }
        });

        chatMonitorPanel.add(phraseInput, cmc);
        eventPanel.add(chatMonitorPanel, ec);

        add(eventPanel, c);

        c.gridy++;
        testButton.setBackground(ColorScheme.BRAND_ORANGE);
        testButton.setForeground(Color.WHITE);
        testButton.setFocusPainted(false);
        testButton.addActionListener(e -> plugin.testWebhook());
        add(testButton, c);
    }

    private void updateProximitySettingsVisibility(boolean visible) {
        proximitySettingsPanel.setVisible(visible);
        revalidate();
        repaint();
    }
} 