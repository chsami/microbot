package net.runelite.client.plugins.microbot.playerassist;

import com.google.inject.Provides;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.playerassist.cannon.CannonScript;
import net.runelite.client.plugins.microbot.playerassist.combat.*;
import net.runelite.client.plugins.microbot.playerassist.loot.LootScript;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "PlayerAssistant",
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
    private OverlayManager overlayManager;
    @Inject
    private PlayerAssistOverlay playerAssistOverlay;

    private final CannonScript cannonScript = new CannonScript();
    private final AttackNpcScript attackNpc = new AttackNpcScript();
    private final CombatPotionScript combatPotion = new CombatPotionScript();
    private final FoodScript foodScript = new FoodScript();
    private final PrayerPotionScript prayerPotionScript = new PrayerPotionScript();
    private final LootScript lootScript = new LootScript();
    private final SafeSpot safeSpotScript = new SafeSpot();
    private final FlickerScript flickerScript = new FlickerScript();
    private final UseSpecialAttackScript useSpecialAttackScript = new UseSpecialAttackScript();
    private final AntiPoisonScript antiPoisonScript = new AntiPoisonScript();


    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        if (overlayManager != null) {
            overlayManager.add(playerAssistOverlay);
        }
        lootScript.run(config);
        cannonScript.run(config);
        attackNpc.run(config);
        combatPotion.run(config);
        foodScript.run(config);
        prayerPotionScript.run(config);
        safeSpotScript.run(config);
        flickerScript.run(config);
        useSpecialAttackScript.run(config);
        antiPoisonScript.run(config);
    }

    protected void shutDown() {
        lootScript.shutdown();
        cannonScript.shutdown();
        attackNpc.shutdown();
        combatPotion.shutdown();
        foodScript.shutdown();
        prayerPotionScript.shutdown();
        safeSpotScript.shutdown();
        flickerScript.shutdown();
        useSpecialAttackScript.shutdown();
        antiPoisonScript.shutdown();
        overlayManager.remove(playerAssistOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getMessage().contains("reach that")) {
            AttackNpcScript.skipNpc();
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (config.prayFlick())
            executor.submit(flickerScript::onGameTick);
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        if (config.prayFlick())
            executor.submit(() -> flickerScript.onNpcDespawned(npcDespawned));
    }
}
