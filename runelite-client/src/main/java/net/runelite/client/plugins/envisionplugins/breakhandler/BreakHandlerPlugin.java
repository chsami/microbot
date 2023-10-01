package net.runelite.client.plugins.envisionplugins.breakhandler;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.envisionplugins.breakhandler.enums.BreakHandlerStates;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.enums.TimeDurationType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;

@PluginDescriptor(
        name = PluginDescriptor.Envision + "Break Handler",
        description = "Break Handler for Microbot",
        tags = {"microbot", "utility"},
        enabledByDefault = true
)
@Slf4j
public class BreakHandlerPlugin extends Plugin {

    @Inject
    BreakHandlerConfig config;

    @Inject
    BreakHandlerScript breakHandlerScript;

    @Inject
    private ClientToolbar clientToolbar;

    @Provides
    BreakHandlerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BreakHandlerConfig.class);
    }

    private BreakHandlerPanel breakHandlerPanel;
    private NavigationButton navButton;
    public static final BufferedImage discordIcon = ImageUtil.loadImageResource(BreakHandlerPlugin.class, "discord_circle.png");
    public static final BufferedImage soonIcon = ImageUtil.loadImageResource(BreakHandlerPlugin.class, "soon.png");
    public static final String discordLink = "https://discord.gg/v7G7ZbxnEf";


    @Override
    protected void startUp() throws Exception {
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "breakhandler_watch.png");

        BreakHandlerScript.setBreakHandlerState(BreakHandlerStates.STARTUP);

        breakHandlerPanel = injector.getInstance(BreakHandlerPanel.class);

        initBreakSettings();

        navButton = NavigationButton.builder()
                .tooltip("Microbot Break Handler")
                .icon(icon)
                .panel(breakHandlerPanel)
                .priority(1)
                .build();

        clientToolbar.addNavigation(navButton);

        breakHandlerScript.run(config, breakHandlerPanel);
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
    }

    /**
     * Setup both the Script's initial min-max break and runtime settings, expected break and run time durations,
     * as well as setting up the display panel with the freshly generated values.
     * <p>
     * Feeds data from Configuration file to Script and Panels
     * <p>
     */
    private void initBreakSettings() throws Exception {
        // Setup Run Time panel defaults
        breakHandlerPanel
                .getMinimumTimeAmount(TimeDurationType.RUNTIME_DURATION)
                .setDurationFromConfig(config.MINIMUM_RUN_TIME_DURATION());
        breakHandlerPanel
                .getMaximumTimeAmount(TimeDurationType.RUNTIME_DURATION)
                .setDurationFromConfig(config.MAXIMUM_RUN_TIME_DURATION());

        // Setup Break panel defaults
        breakHandlerPanel
                .getMinimumTimeAmount(TimeDurationType.BREAK_DURATION)
                .setDurationFromConfig(config.MINIMUM_BREAK_DURATION());
        breakHandlerPanel
                .getMaximumTimeAmount(TimeDurationType.BREAK_DURATION)
                .setDurationFromConfig(config.MAXIMUM_BREAK_DURATION());

        BreakHandlerScript.setBreakMethod(
                breakHandlerPanel.getBreakMethod());
    }
}
