package net.runelite.client.plugins.microbot.cluesolverv2;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.cluescrolls.ClueScrollPlugin;
import net.runelite.client.ui.overlay.OverlayManager;



@Slf4j
@PluginDescriptor(

        name = PluginDescriptor.Budbomber + "Clue Solver V2",
        description = "Automates clue-solving tasks alongside ClueScrollPlugin",
        tags = {"clue", "solver", "automation"},
        enabledByDefault = false

)
@PluginDependency(ClueScrollPlugin.class)
public class ClueSolverPlugin extends Plugin {
    @Inject
    private ClueSolverConfig config;

    @Inject
    private ClueSolverScriptV2 clueSolverScriptV2;

    @Inject
    private ClueSolverOverlay overlay;

    @Inject
    private OverlayManager overlayManager;


    @Override
    public void startUp() {
        overlayManager.add(overlay);
        if (config.toggleAll()) {
            clueSolverScriptV2.run();
        }

        log.info("Clue Solver V2 started.");
    }

    @Override
    public void shutDown() {
        clueSolverScriptV2.shutdown();
        overlayManager.remove(overlay);
        log.info("Clue Solver V2 stopped.");
    }

    @Provides
    ClueSolverConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ClueSolverConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if ("cluesolverv2".equals(event.getGroup())) {
            clueSolverScriptV2.updateConfig(config);

            // Toggle script based on config setting
            if (config.toggleAll()) {
                // If the script is not already running, start it
                if (!clueSolverScriptV2.isRunning()) {
                    clueSolverScriptV2.run(config);
                }
            } else {
                // Shutdown if the toggle is turned off
                clueSolverScriptV2.shutdown();
            }

            // If task interval or cooldown between tasks changed, restart script with new settings
            if ("taskInterval".equals(event.getKey()) || "cooldownBetweenTasks".equals(event.getKey())) {
                if (clueSolverScriptV2.isRunning()) {
                    clueSolverScriptV2.shutdown();
                    clueSolverScriptV2.run(config);
                }
            }

            log.info("ClueSolverConfig changed: {}", event.getKey());
        }
    }

}
