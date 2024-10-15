package net.runelite.client.plugins.microbot.tutorialisland;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "TutorialIsland",
        description = "Microbot tutorialIsland plugin",
        tags = {"TutorialIsland", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class TutorialislandPlugin extends Plugin {
    
    @Getter
    private boolean toggleMusic;
    @Getter
    private boolean toggleRoofs;
    @Getter
    private boolean toggleLevelUp;
    @Getter
    private boolean toggleShiftDrop;
    @Getter
    private boolean toggleDevOverlay;
    
    
    @Inject
    private TutorialIslandConfig config;
    @Provides
    TutorialIslandConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TutorialIslandConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TutorialIslandOverlay tutorialIslandOverlay;

    @Inject
    TutorialIslandScript tutorialIslandScript;


    @Override
    protected void startUp() throws AWTException {
        toggleMusic = config.toggleMusic();
        toggleRoofs = config.toggleRoofs();
        toggleLevelUp = config.toggleDisableLevelUp();
        toggleShiftDrop = config.toggleShiftDrop();
        toggleDevOverlay = config.toggleDevOverlay();
        
        if (overlayManager != null) {
            overlayManager.add(tutorialIslandOverlay);
        }
        
        tutorialIslandScript.run(config);
    }

    protected void shutDown() {
        tutorialIslandScript.shutdown();
        overlayManager.remove(tutorialIslandOverlay);
    }

    @Subscribe
    public void onConfigChanged(final ConfigChanged event) {
        if (!event.getGroup().equals(TutorialIslandConfig.configGroup)) return;
        
        if (event.getKey().equals(TutorialIslandConfig.toggleMusic)) {
            toggleMusic = config.toggleMusic();
        }
        
        if (event.getKey().equals(TutorialIslandConfig.toggleRoofs)) {
            toggleRoofs = config.toggleRoofs();
        }

        if (event.getKey().equals(TutorialIslandConfig.toggleLevelUp)) {
            toggleLevelUp = config.toggleDisableLevelUp();
        }

        if (event.getKey().equals(TutorialIslandConfig.toggleShiftDrop)) {
            toggleShiftDrop = config.toggleShiftDrop();
        }

        if (event.getKey().equals(TutorialIslandConfig.toggleDevOverlay)) {
            toggleDevOverlay = config.toggleDevOverlay();
        }
    }
}
