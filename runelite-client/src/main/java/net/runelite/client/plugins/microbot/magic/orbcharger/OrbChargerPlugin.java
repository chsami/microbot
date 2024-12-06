package net.runelite.client.plugins.microbot.magic.orbcharger;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.magic.orbcharger.enums.OrbChargerState;
import net.runelite.client.plugins.microbot.magic.orbcharger.scripts.AirOrbScript;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.GMason + "Orb Charger",
        description = "Microbot Orb Charger Plugin",
        tags = {"magic", "microbot", "money", "money making"},
        enabledByDefault = false
)
@Slf4j
public class OrbChargerPlugin extends Plugin {
    
    @Inject
    private OrbChargerConfig config;
    @Provides
    OrbChargerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(OrbChargerConfig.class);
    }
    
    public static String version = "1.0.0";
    @Getter
    private boolean useEnergyPotions;
    @Getter
    private boolean useStaminaPotions;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private OrbChargerOverlay orbOverlay;
    
    @Inject
    private AirOrbScript airOrbScript;
    
    @Override
    protected void startUp() throws AWTException {
        useEnergyPotions = config.useEnergyPotions();
        useStaminaPotions = config.useStaminaPotions();
        if (overlayManager != null) {
            overlayManager.add(orbOverlay);
        }
        airOrbScript.run();
        airOrbScript.handleWalk();
    }
    
    @Override
    protected void shutDown() {
        overlayManager.remove(orbOverlay);
        airOrbScript.shutdown();
    }
    
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals(OrbChargerConfig.configGroup)) return;
        
        if (event.getKey().equals(OrbChargerConfig.useEnergyPotions)){
            useEnergyPotions = config.useEnergyPotions();
        }
        
        if (event.getKey().equals(OrbChargerConfig.useStaminaPotions)){
            useStaminaPotions = config.useStaminaPotions();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE && event.getMessage().equalsIgnoreCase("oh dear, you are dead!")) {
            airOrbScript.hasDied = true; 
            airOrbScript.state = OrbChargerState.WALKING;
        }
    }
}
