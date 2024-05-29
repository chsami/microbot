package net.runelite.client.plugins.microbot.playerassist;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.playerassist.cannon.CannonScript;
import net.runelite.client.plugins.microbot.playerassist.combat.*;
import net.runelite.client.plugins.microbot.playerassist.loot.LootScript;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "AIO Fighter",
        description = "Microbot Fighter plugin",
        tags = {"fight", "microbot", "misc", "combat", "playerassistant"},
        enabledByDefault = false
)
@Slf4j
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
    private final BuryBoneScript buryBoneScript = new BuryBoneScript();
    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        if (overlayManager != null) {
            overlayManager.add(playerAssistOverlay);
        }
        lootScript.run(config);
        cannonScript.run(config);
        flickerScript.run(config);
        attackNpc.run(config);
        combatPotion.run(config);
        foodScript.run(config);
        prayerPotionScript.run(config);
//        safeSpotScript.run(config); // TODO: safespot
        useSpecialAttackScript.run(config);
        antiPoisonScript.run(config);
        buryBoneScript.run(config);
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
        buryBoneScript.shutdown();
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
       //execute flicker script
        flickerScript.onGameTick();
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        flickerScript.onNpcDespawned(npcDespawned);
    }
    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event){
        if (event.getActor().getInteracting() != Microbot.getClient().getLocalPlayer()) return;
        final Hitsplat hitsplat = event.getHitsplat();
        if ((hitsplat.getHitsplatType() == HitsplatID.BLOCK_ME || hitsplat.getHitsplatType() == HitsplatID.DAMAGE_ME) && event.getActor() instanceof NPC) {
            Rs2Prayer.disableAllPrayers();
            if(config.toggleQuickPrayFlick())
                Rs2Prayer.toggleQuickPrayer(false);
            flickerScript.resetLastAttack();

        }
    }
}
