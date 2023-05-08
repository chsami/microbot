package net.runelite.client.plugins.microbot.playerassist;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.playerassist.cannon.CannonScript;
import net.runelite.client.plugins.microbot.playerassist.combat.AttackNpcScript;
import net.runelite.client.plugins.microbot.playerassist.combat.CombatPotionScript;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.playerassist.combat.PrayerPotionScript;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
@PluginDescriptor(
        name = "Micro PlayerAssistant",
        description = "Microbot playerassistant plugin",
        tags = {"assist", "microbot", "misc", "combat"},
        enabledByDefault = false
)
public class PlayerAssistPlugin extends Plugin {
    @Inject
    private PlayerAssistConfig config;

    @Provides
    PlayerAssistConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PlayerAssistConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private Notifier notifier;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PlayerAssistOverlay playerAssistOverlay;

    private CannonScript cannonScript = new CannonScript();
    private AttackNpcScript attackNpc = new AttackNpcScript();
    private CombatPotionScript combatPotion = new CombatPotionScript();
    private FoodScript foodScript = new FoodScript();
    private PrayerPotionScript prayerPotionScript = new PrayerPotionScript();


    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(playerAssistOverlay);
        }
        cannonScript.run(config);
        attackNpc.run(config);
        combatPotion.run(config);
        foodScript.run(config);
        prayerPotionScript.run(config);

    }

    protected void shutDown() {
        cannonScript.shutdown();
        attackNpc.shutdown();
        combatPotion.shutdown();
        foodScript.shutdown();
        prayerPotionScript.shutdown();
        overlayManager.remove(playerAssistOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getMessage().contains("reach that")) {
            AttackNpcScript.skipNpc();
        }
    }
}
