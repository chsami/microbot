package net.runelite.client.plugins.microbot.cluesolverv2;

import com.google.inject.Provides;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.cluescrolls.ClueScrollPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
        name = "Clue Solver V2",
        description = "Automates clue-solving tasks alongside ClueScrollPlugin",
        tags = {"clue", "solver", "automation"}
)
@PluginDependency(ClueScrollPlugin.class)
public class ClueSolverPlugin extends Plugin {
    @Inject
    private ClueSolverConfig config;

    @Inject
    private ClueScrollPlugin clueScrollPlugin;

    @Inject
    private ClueSolverScriptV2 clueSolverScriptV2;

    @Inject
    private ClueSolverOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Override
    protected void startUp() {

        overlayManager.add(overlay);

        if (config.toggleAll()) {
            clueSolverScriptV2.start(config);
        }
        log.info("Clue Solver V2 started.");
    }

    @Override
    protected void shutDown() {

        clueSolverScriptV2.stop();
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
            if (config.toggleAll()) {
                clueSolverScriptV2.start(config);
            } else {
                clueSolverScriptV2.stop();
            }
        }
    }
}
