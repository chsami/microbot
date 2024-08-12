package net.runelite.client.plugins.microbot.CrashedStar;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
        name =  PluginDescriptor.zuk + "Crashed Star Miner",
        description = "Crashed Star Miner",
        tags = {"mining", "Crashed Star"},
        enabledByDefault = false
)
@Slf4j
public class CrashedStarPlugin extends Plugin {
    public static int stardustMined;
    @Inject
    private CrashedStarConfig config;

    @Getter
    private double xpPerHour = -1;

    @Getter
    private double dustPerHour = -1;

    @Provides
    CrashedStarConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CrashedStarConfig.class);
    }

    public int getStardustMined() {
        return stardustMined;
    }

    private String currentTier = null;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private CrashedStarOverlay crashedStarOverlay;

    @Inject
    CrashedStarScript crashedStarScript;

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(crashedStarOverlay);
        }
        crashedStarScript.run(config);
    }

    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject == null)
            return;
        // Map of object IDs to tier names
        Map<Integer, String> tierMap = new HashMap<>();
        tierMap.put(null, "");
        tierMap.put(ObjectID.CRASHED_STAR_41229, "Tier 1");
        tierMap.put(ObjectID.CRASHED_STAR_41228, "Tier 2");
        tierMap.put(ObjectID.CRASHED_STAR_41227, "Tier 3");
        tierMap.put(ObjectID.CRASHED_STAR_41226, "Tier 4");
        tierMap.put(ObjectID.CRASHED_STAR_41225, "Tier 5");
        tierMap.put(ObjectID.CRASHED_STAR_41224, "Tier 6");
        tierMap.put(ObjectID.CRASHED_STAR_41223, "Tier 7");
        tierMap.put(ObjectID.CRASHED_STAR_41021, "Tier 8");
        tierMap.put(ObjectID.CRASHED_STAR, "Tier 9");

        // Check if the spawned object is a crashed star
        if (tierMap.containsKey(gameObject.getId())) {
            // If Rs2GameObject.exists(29733) is true, log the corresponding tier name
            if (Rs2GameObject.exists(29733)) {
                currentTier = tierMap.get(gameObject.getId());
                Microbot.log("New Tier Spawned: " + currentTier);
            }
            if (!Rs2GameObject.exists(29733) && currentTier == null) {
                Microbot.log("Star Despawned");
            }
        } else return;
    }

    public String getCurrentTier() {
        return currentTier;
    }

    protected void shutDown() {
        crashedStarScript.shutdown();
        overlayManager.remove(crashedStarOverlay);
    }

}
