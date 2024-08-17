package net.runelite.client.plugins.microbot.util.antiban;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * The AntibanPluginPanel is a user interface panel for configuring anti-ban settings.
 *
 * <p>
 * This panel allows users to adjust settings related to the anti-ban system, such as enabling micro-breaks,
 * adjusting action cooldown probabilities, and configuring behavioral simulations like fatigue or attention span.
 * The panel is divided into different categories, each focusing on specific aspects of anti-ban behavior,
 * including activity settings, mouse behavior, and cooldown management.
 * </p>
 *
 * <p>
 * Users can interact with various checkboxes and sliders to tailor the bot's anti-ban features to their preferences,
 * making it behave more like a human player during automated tasks.
 * </p>
 *
 * <h3>Main Features:</h3>
 * <ul>
 *   <li>Enable or disable anti-ban features like action cooldowns and micro-breaks.</li>
 *   <li>Customize the bot's behavior with random intervals, dynamic activity, and simulated fatigue.</li>
 *   <li>Adjust the duration and probability of micro-breaks and action cooldowns.</li>
 *   <li>Fine-tune mouse behavior, including natural movements and random actions.</li>
 *   <li>View real-time information about the current play style, activity, and bot status.</li>
 * </ul>
 *
 * <p>
 * This panel is automatically integrated into the bot's user interface and does not require manual initialization by the user.
 * </p>
 */

public class AntibanPluginPanel extends PluginPanel {
    private final JCheckBox isActionCooldownActive = new JCheckBox("Action Cooldown Active");
    private final JCheckBox isMicroBreakActive = new JCheckBox("Micro Break Active");
    private final JCheckBox isEnabled = new JCheckBox("Enabled");
    private final JCheckBox universalAntiban = new JCheckBox("Universal Antiban");
    private final JCheckBox usePlayStyle = new JCheckBox("Use Play Style");
    private final JCheckBox useRandomIntervals = new JCheckBox("Use Random Intervals");
    private final JCheckBox simulateFatigue = new JCheckBox("Simulate Fatigue");
    private final JCheckBox simulateAttentionSpan = new JCheckBox("Simulate Attention Span");
    private final JCheckBox useBehavioralVariability = new JCheckBox("Use Behavioral Variability");
    private final JCheckBox useNonLinearIntervals = new JCheckBox("Use Non-Linear Intervals");
    private final JCheckBox enableProfileSwitching = new JCheckBox("Enable Profile Switching");
    private final JCheckBox adjustForTimeOfDay = new JCheckBox("Adjust For Time Of Day");
    private final JCheckBox simulateMistakes = new JCheckBox("Simulate Mistakes");
    private final JCheckBox useNaturalMouse = new JCheckBox("Use Natural Mouse");
    private final JCheckBox moveMouseOffScreen = new JCheckBox("Move Mouse Off Screen");
    private final JCheckBox moveMouseRandomly = new JCheckBox("Move Mouse Randomly");
    private final JCheckBox useContextualVariability = new JCheckBox("Use Contextual Variability");
    private final JCheckBox dynamicActivityIntensity = new JCheckBox("Dynamic Activity Intensity");
    private final JCheckBox dynamicActivity = new JCheckBox("Dynamic Activity");
    private final JCheckBox devDebug = new JCheckBox("Dev Debug");
    private final JCheckBox takeMicroBreaks = new JCheckBox("Take Micro Breaks");
    private final JCheckBox simulatePlaySchedule = new JCheckBox("Simulate Play Schedule");

    private final JSlider microBreakDurationLow = new JSlider(1, 10, Rs2AntibanSettings.microBreakDurationLow);
    private final JSlider microBreakDurationHigh = new JSlider(1, 30, Rs2AntibanSettings.microBreakDurationHigh);
    private final JSlider actionCooldownChance = new JSlider(0, 100, (int) (Rs2AntibanSettings.actionCooldownChance * 100));
    private final JSlider microBreakChance = new JSlider(0, 100, (int) (Rs2AntibanSettings.microBreakChance * 100));
    private final JSlider timeout = new JSlider(0, 60, Rs2Antiban.getTIMEOUT());
    private final JSlider moveMouseRandomlyChance = new JSlider(0, 100, (int) (Rs2AntibanSettings.moveMouseRandomlyChance * 100));

    private final JLabel microBreakDurationLowLabel = new JLabel("Micro Break Duration Low (min): " + Rs2AntibanSettings.microBreakDurationLow);
    private final JLabel microBreakDurationHighLabel = new JLabel("Micro Break Duration High (min): " + Rs2AntibanSettings.microBreakDurationHigh);
    private final JLabel actionCooldownChanceLabel = new JLabel("Action Cooldown Chance (%): " + (int) (Rs2AntibanSettings.actionCooldownChance * 100));
    private final JLabel microBreakChanceLabel = new JLabel("Micro Break Chance (%): " + (int) (Rs2AntibanSettings.microBreakChance * 100));
    private final JLabel timeoutLabel = new JLabel("Timeout (min): " + Rs2Antiban.getTIMEOUT());
    private final JLabel moveMouseRandomlyChanceLabel = new JLabel("Random Mouse Movement (%): " + (int) (Rs2AntibanSettings.moveMouseRandomlyChance * 100));

    // Additional Info Panel
    private final JLabel playStyleLabel = new JLabel("Play Style: " + (Rs2Antiban.getPlayStyle() != null ? Rs2Antiban.getPlayStyle().getName() : "null"));
    private final JLabel playStyleChangeLabel = new JLabel("Play Style Change: " + (Rs2Antiban.getPlayStyle() != null ? Rs2Antiban.getPlayStyle().getTimeLeftUntilNextSwitch() : "null"));
    private final JLabel profileLabel = new JLabel("Category: " + (Rs2Antiban.getCategory() != null ? Rs2Antiban.getCategory().getName() : "null"));
    private final JLabel activityLabel = new JLabel("Activity: " + (Rs2Antiban.getActivity() != null ? Rs2Antiban.getActivity().getMethod() : "null"));
    private final JLabel activityIntensityLabel = new JLabel("Activity Intensity: " + (Rs2Antiban.getActivityIntensity() != null ? Rs2Antiban.getActivityIntensity().getName() : "null"));
    private final JLabel busyLabel = new JLabel("Busy: " + (Rs2Antiban.getCategory() != null ? Rs2Antiban.getCategory().isBusy() : "null"));

    public AntibanPluginPanel() {
        setLayout(new BorderLayout());

        // Create CardLayout and main content panel
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        // Create navigation panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));

        // Add buttons with icons to navigation panel
        addNavButton(navPanel, "General Settings", "general.png", cardPanel, "GeneralSettings");
        addNavButton(navPanel, "Activity Settings", "activity.png", cardPanel, "ActivitySettings");
        addNavButton(navPanel, "Profile Settings", "profile.png", cardPanel, "ProfileSettings");
        addNavButton(navPanel, "Mouse Settings", "mouse.png", cardPanel, "MouseSettings");
        addNavButton(navPanel, "Micro Break Settings", "microbreak.png", cardPanel, "MicroBreakSettings");
        addNavButton(navPanel, "Cooldown Settings", "cooldown.png", cardPanel, "CooldownSettings");

        // Create and add panels for each card
        cardPanel.add(createGeneralSettingsPanel(), "GeneralSettings");
        cardPanel.add(createActivitySettingsPanel(), "ActivitySettings");
        cardPanel.add(createProfileSettingsPanel(), "ProfileSettings");
        cardPanel.add(createMouseSettingsPanel(), "MouseSettings");
        cardPanel.add(createMicroBreakSettingsPanel(), "MicroBreakSettings");
        cardPanel.add(createCooldownSettingsPanel(), "CooldownSettings");

        // Add navigation and card panels to the main panel
        add(navPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        // Add the Info Panel at the bottom
        add(createInfoPanel(), BorderLayout.SOUTH);

        setupSliders();
        setupListeners();
        loadSettings();
    }

    private void addNavButton(JPanel navPanel, String tooltip, String iconPath, JPanel cardPanel, String cardName) {
        JButton button = new JButton(createIcon(iconPath));
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(e -> {
            CardLayout cl = (CardLayout) (cardPanel.getLayout());
            cl.show(cardPanel, cardName);
        });
        navPanel.add(button);
    }

    private JPanel createGeneralSettingsPanel() {
        JPanel panel = createPanel("General Settings");
        addCheckboxesToPanel(panel, isEnabled, universalAntiban, useContextualVariability, devDebug);
        return panel;
    }

    private JPanel createActivitySettingsPanel() {
        JPanel panel = createPanel("Activity Settings");
        addCheckboxesToPanel(panel, usePlayStyle, useRandomIntervals, simulateFatigue, simulateAttentionSpan, useBehavioralVariability, useNonLinearIntervals, dynamicActivityIntensity, dynamicActivity);
        return panel;
    }

    private JPanel createProfileSettingsPanel() {
        JPanel panel = createPanel("Profile Settings");
        addCheckboxesToPanel(panel, enableProfileSwitching, adjustForTimeOfDay, simulatePlaySchedule);
        return panel;
    }

    private JPanel createMouseSettingsPanel() {
        JPanel panel = createPanel("Mouse Settings");
        addCheckboxesToPanel(panel, useNaturalMouse, simulateMistakes, moveMouseOffScreen, moveMouseRandomly);
        addSlidersToPanel(panel, moveMouseRandomlyChanceLabel, moveMouseRandomlyChance);
        return panel;
    }

    private JPanel createMicroBreakSettingsPanel() {
        JPanel panel = createPanel("Micro Break Settings");
        addCheckboxesToPanel(panel, isMicroBreakActive, takeMicroBreaks);
        addSlidersToPanel(panel, microBreakDurationLowLabel, microBreakDurationLow, microBreakDurationHighLabel, microBreakDurationHigh, microBreakChanceLabel, microBreakChance);
        return panel;
    }

    private JPanel createCooldownSettingsPanel() {
        JPanel panel = createPanel("Cooldown and Timeout Settings");
        addCheckboxesToPanel(panel, isActionCooldownActive);
        addSlidersToPanel(panel, actionCooldownChanceLabel, actionCooldownChance, timeoutLabel, timeout);
        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Additional Info"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(playStyleLabel, gbc);
        panel.add(playStyleChangeLabel, gbc);
        panel.add(profileLabel, gbc);
        panel.add(activityLabel, gbc);
        panel.add(activityIntensityLabel, gbc);
        panel.add(busyLabel, gbc);

        return panel;
    }

    private JPanel createPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void addCheckboxesToPanel(JPanel panel, JCheckBox... checkBoxes) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        for (JCheckBox checkBox : checkBoxes) {
            panel.add(checkBox, gbc);
        }
    }

    private void addSlidersToPanel(JPanel panel, JComponent... labelsAndSliders) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        for (JComponent component : labelsAndSliders) {
            panel.add(component, gbc);
        }
    }

    private void setupSliders() {
        setupSlider(microBreakDurationLow, 1, 5, 1);
        setupSlider(microBreakDurationHigh, 5, 15, 1);
        setupSlider(actionCooldownChance, 20, 100, 10);
        setupSlider(microBreakChance, 20, 100, 10);
        setupSlider(timeout, 10, 60, 5);
        setupSlider(moveMouseRandomlyChance, 20, 100, 10);
    }

    private void setupSlider(JSlider slider, int majorTickSpacing, int max, int minorTickSpacing) {
        slider.setMajorTickSpacing(majorTickSpacing);
        slider.setMinorTickSpacing(minorTickSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMinimum(0);
        slider.setMaximum(max);
    }

    private void setupListeners() {
        isActionCooldownActive.addActionListener(e -> Rs2AntibanSettings.actionCooldownActive = isActionCooldownActive.isSelected());
        isMicroBreakActive.addActionListener(e -> Rs2AntibanSettings.microBreakActive = isMicroBreakActive.isSelected());
        isEnabled.addActionListener(e -> Rs2AntibanSettings.antibanEnabled = isEnabled.isSelected());
        universalAntiban.addActionListener(e -> Rs2AntibanSettings.universalAntiban = universalAntiban.isSelected());
        usePlayStyle.addActionListener(e -> Rs2AntibanSettings.usePlayStyle = usePlayStyle.isSelected());
        useRandomIntervals.addActionListener(e -> Rs2AntibanSettings.randomIntervals = useRandomIntervals.isSelected());
        simulateFatigue.addActionListener(e -> Rs2AntibanSettings.simulateFatigue = simulateFatigue.isSelected());
        simulateAttentionSpan.addActionListener(e -> Rs2AntibanSettings.simulateAttentionSpan = simulateAttentionSpan.isSelected());
        useBehavioralVariability.addActionListener(e -> Rs2AntibanSettings.behavioralVariability = useBehavioralVariability.isSelected());
        useNonLinearIntervals.addActionListener(e -> Rs2AntibanSettings.nonLinearIntervals = useNonLinearIntervals.isSelected());
        enableProfileSwitching.addActionListener(e -> Rs2AntibanSettings.profileSwitching = enableProfileSwitching.isSelected());
        adjustForTimeOfDay.addActionListener(e -> Rs2AntibanSettings.timeOfDayAdjust = adjustForTimeOfDay.isSelected());
        simulateMistakes.addActionListener(e -> Rs2AntibanSettings.simulateMistakes = simulateMistakes.isSelected());
        useNaturalMouse.addActionListener(e -> Rs2AntibanSettings.naturalMouse = useNaturalMouse.isSelected());
        moveMouseOffScreen.addActionListener(e -> Rs2AntibanSettings.moveMouseOffScreen = moveMouseOffScreen.isSelected());
        moveMouseRandomly.addActionListener(e -> Rs2AntibanSettings.moveMouseRandomly = moveMouseRandomly.isSelected());
        useContextualVariability.addActionListener(e -> Rs2AntibanSettings.contextualVariability = useContextualVariability.isSelected());
        dynamicActivityIntensity.addActionListener(e -> Rs2AntibanSettings.dynamicIntensity = dynamicActivityIntensity.isSelected());
        dynamicActivity.addActionListener(e -> Rs2AntibanSettings.dynamicActivity = dynamicActivity.isSelected());
        devDebug.addActionListener(e -> Rs2AntibanSettings.devDebug = devDebug.isSelected());
        takeMicroBreaks.addActionListener(e -> Rs2AntibanSettings.takeMicroBreaks = takeMicroBreaks.isSelected());
        simulatePlaySchedule.addActionListener(e -> Rs2AntibanSettings.playSchedule = simulatePlaySchedule.isSelected());

        microBreakDurationLow.addChangeListener(e -> {
            Rs2AntibanSettings.microBreakDurationLow = microBreakDurationLow.getValue();
            microBreakDurationLowLabel.setText("Micro Break Duration Low (min): " + microBreakDurationLow.getValue());
        });
        microBreakDurationHigh.addChangeListener(e -> {
            Rs2AntibanSettings.microBreakDurationHigh = microBreakDurationHigh.getValue();
            microBreakDurationHighLabel.setText("Micro Break Duration High (min): " + microBreakDurationHigh.getValue());
        });
        actionCooldownChance.addChangeListener(e -> {
            Rs2AntibanSettings.actionCooldownChance = actionCooldownChance.getValue() / 100.0;
            actionCooldownChanceLabel.setText("Action Cooldown Chance (%): " + actionCooldownChance.getValue());
        });
        microBreakChance.addChangeListener(e -> {
            Rs2AntibanSettings.microBreakChance = microBreakChance.getValue() / 100.0;
            microBreakChanceLabel.setText("Micro Break Chance (%): " + microBreakChance.getValue());
        });
        timeout.addChangeListener(e -> {
            Rs2Antiban.setTIMEOUT(timeout.getValue());
            timeoutLabel.setText("Timeout (min): " + timeout.getValue());
        });
        moveMouseRandomlyChance.addChangeListener(e -> {
            Rs2AntibanSettings.moveMouseRandomlyChance = moveMouseRandomlyChance.getValue() / 100.0;
            moveMouseRandomlyChanceLabel.setText("Random Mouse Movement (%): " + moveMouseRandomlyChance.getValue());
        });
    }

    public void loadSettings() {
        isActionCooldownActive.setSelected(Rs2AntibanSettings.actionCooldownActive);
        isMicroBreakActive.setSelected(Rs2AntibanSettings.microBreakActive);
        isEnabled.setSelected(Rs2AntibanSettings.antibanEnabled);
        usePlayStyle.setSelected(Rs2AntibanSettings.usePlayStyle);
        useRandomIntervals.setSelected(Rs2AntibanSettings.randomIntervals);
        simulateFatigue.setSelected(Rs2AntibanSettings.simulateFatigue);
        simulateAttentionSpan.setSelected(Rs2AntibanSettings.simulateAttentionSpan);
        useBehavioralVariability.setSelected(Rs2AntibanSettings.behavioralVariability);
        useNonLinearIntervals.setSelected(Rs2AntibanSettings.nonLinearIntervals);
        enableProfileSwitching.setSelected(Rs2AntibanSettings.profileSwitching);
        adjustForTimeOfDay.setSelected(Rs2AntibanSettings.timeOfDayAdjust);
        simulateMistakes.setSelected(Rs2AntibanSettings.simulateMistakes);
        useNaturalMouse.setSelected(Rs2AntibanSettings.naturalMouse);
        moveMouseOffScreen.setSelected(Rs2AntibanSettings.moveMouseOffScreen);
        useContextualVariability.setSelected(Rs2AntibanSettings.contextualVariability);
        dynamicActivityIntensity.setSelected(Rs2AntibanSettings.dynamicIntensity);
        dynamicActivity.setSelected(Rs2AntibanSettings.dynamicActivity);
        devDebug.setSelected(Rs2AntibanSettings.devDebug);
        takeMicroBreaks.setSelected(Rs2AntibanSettings.takeMicroBreaks);
        simulatePlaySchedule.setSelected(Rs2AntibanSettings.playSchedule);

        microBreakDurationLow.setValue(Rs2AntibanSettings.microBreakDurationLow);
        microBreakDurationHigh.setValue(Rs2AntibanSettings.microBreakDurationHigh);
        actionCooldownChance.setValue((int) (Rs2AntibanSettings.actionCooldownChance * 100));
        microBreakChance.setValue((int) (Rs2AntibanSettings.microBreakChance * 100));
        timeout.setValue(Rs2Antiban.getTIMEOUT());

        microBreakDurationLowLabel.setText("Micro Break Duration Low (min): " + Rs2AntibanSettings.microBreakDurationLow);
        microBreakDurationHighLabel.setText("Micro Break Duration High (min): " + Rs2AntibanSettings.microBreakDurationHigh);
        actionCooldownChanceLabel.setText("Action Cooldown Chance (%): " + (int) (Rs2AntibanSettings.actionCooldownChance * 100));
        microBreakChanceLabel.setText("Micro Break Chance (%): " + (int) (Rs2AntibanSettings.microBreakChance * 100));
        timeoutLabel.setText("Timeout (min): " + Rs2Antiban.getTIMEOUT());

        if (!Microbot.isLoggedIn())
            return;

        playStyleLabel.setText("Play Style: " + (Rs2Antiban.getPlayStyle() != null ? Rs2Antiban.getPlayStyle().getName() : "null"));
        playStyleChangeLabel.setText("Play Style Change: " + (Rs2Antiban.getPlayStyle() != null ? Rs2Antiban.getPlayStyle().getTimeLeftUntilNextSwitch() : "null"));
        profileLabel.setText("Category: " + (Rs2Antiban.getCategory() != null ? Rs2Antiban.getCategory().getName() : "null"));
        activityLabel.setText("Activity: " + (Rs2Antiban.getActivity() != null ? Rs2Antiban.getActivity().getMethod() : "null"));
        activityIntensityLabel.setText("Activity Intensity: " + (Rs2Antiban.getActivityIntensity() != null ? Rs2Antiban.getActivityIntensity().getName() : "null"));
        busyLabel.setText("Busy: " + (Rs2Antiban.getCategory() != null ? Rs2Antiban.getCategory().isBusy() : "null"));
    }

    private Icon createIcon(String path) {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(path)));
    }
}
