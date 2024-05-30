package net.runelite.client.plugins.microbot.barrows;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.barrows.enums.PRAYSTYLE;
import net.runelite.client.plugins.microbot.construction.ConstructionConfig;
import net.runelite.client.plugins.microbot.derangedarchaeologist.DerangedAchaeologistScript;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
        name = PluginDescriptor.Pumster + "Barrows",
        description = "Microbot barrows plugin",
        tags = {"pvm", "microbot", "barrows"},
        enabledByDefault = false
)
public class BarrowsPlugin extends Plugin {
    private static final int[] BARROWS_BROTHERS_IDS = {
            NpcID.AHRIM_THE_BLIGHTED,
            NpcID.DHAROK_THE_WRETCHED,
            NpcID.GUTHAN_THE_INFESTED,
            NpcID.KARIL_THE_TAINTED,
            NpcID.TORAG_THE_CORRUPTED,
            NpcID.VERAC_THE_DEFILED
    };

    private static final Map<String, WorldPoint> CRYPT_LOCATIONS = new HashMap<>();
    private static final Map<Integer, PRAYSTYLE> PRAYER_RECOMMENDATIONS = new HashMap<>();
    private static final Map<Integer, String> BROTHER_CRYPT_MAPPING = new HashMap<>();

    public boolean isPlayerFightingBrother = false;


    static {
        // Define the approximate center points for each crypt
        CRYPT_LOCATIONS.put("Ahrim", new WorldPoint(3565, 3289, 0));
        CRYPT_LOCATIONS.put("Dharok", new WorldPoint(3575, 3298, 0));
        CRYPT_LOCATIONS.put("Guthan", new WorldPoint(3575, 3275, 0));
        CRYPT_LOCATIONS.put("Karil", new WorldPoint(3566, 3276, 0));
        CRYPT_LOCATIONS.put("Torag", new WorldPoint(3554, 3282, 0));
        CRYPT_LOCATIONS.put("Verac", new WorldPoint(3557, 3297, 0));

        // Define prayer recommendations for each Barrows Brother
        PRAYER_RECOMMENDATIONS.put(NpcID.AHRIM_THE_BLIGHTED, PRAYSTYLE.MAGE);
        PRAYER_RECOMMENDATIONS.put(NpcID.DHAROK_THE_WRETCHED, PRAYSTYLE.MELEE);
        PRAYER_RECOMMENDATIONS.put(NpcID.GUTHAN_THE_INFESTED, PRAYSTYLE.MELEE);
        PRAYER_RECOMMENDATIONS.put(NpcID.KARIL_THE_TAINTED, PRAYSTYLE.RANGED);
        PRAYER_RECOMMENDATIONS.put(NpcID.TORAG_THE_CORRUPTED, PRAYSTYLE.MELEE);
        PRAYER_RECOMMENDATIONS.put(NpcID.VERAC_THE_DEFILED, PRAYSTYLE.MELEE);

        // Map each Barrows Brother to their respective crypt
        BROTHER_CRYPT_MAPPING.put(NpcID.AHRIM_THE_BLIGHTED, "Ahrim");
        BROTHER_CRYPT_MAPPING.put(NpcID.DHAROK_THE_WRETCHED, "Dharok");
        BROTHER_CRYPT_MAPPING.put(NpcID.GUTHAN_THE_INFESTED, "Guthan");
        BROTHER_CRYPT_MAPPING.put(NpcID.KARIL_THE_TAINTED, "Karil");
        BROTHER_CRYPT_MAPPING.put(NpcID.TORAG_THE_CORRUPTED, "Torag");
        BROTHER_CRYPT_MAPPING.put(NpcID.VERAC_THE_DEFILED, "Verac");
    }

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BarrowsOverlay barrowsOverlay;

    @Inject
    private BarrowsConfig config;

    @Inject
    public BarrowsScript barrowsScript;


    @Provides
    BarrowsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BarrowsConfig.class);
    }

    private final Map<Integer, Boolean> barrowsBrothersStatus = new HashMap<>();

    private String currentCrypt = "Unknown";
    private PRAYSTYLE recommendedPrayer = PRAYSTYLE.OFF;
    private String nextCrypt = "None";


    @Override
    protected void startUp() throws Exception {
        overlayManager.add(barrowsOverlay);

        // Initialize the status of all Barrows Brothers to alive
        for (int id : BARROWS_BROTHERS_IDS) {
            barrowsBrothersStatus.put(id, true);
        }

        //barrowsScript.run(config, this);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(barrowsOverlay);
        barrowsBrothersStatus.clear();
        currentCrypt = "Unknown";
        recommendedPrayer = PRAYSTYLE.OFF;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        int npcId = event.getNpc().getId();
        if (barrowsBrothersStatus.containsKey(npcId)) {
            barrowsBrothersStatus.put(npcId, true); // The brother is alive
            recommendedPrayer = PRAYER_RECOMMENDATIONS.getOrDefault(npcId, PRAYSTYLE.OFF);
            Rs2Npc.interact(npcId, "Attack");
            Rs2Prayer.toggle(prayerStyleToEnum());
            isPlayerFightingBrother = true;


        }
    }

    @Subscribe
    public void onNpcChanged(NpcChanged event) {
        if (isBarrowsBrother(event.getNpc().getId())) {
            isPlayerFightingBrother = true;
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        int npcId = event.getNpc().getId();
        if (barrowsBrothersStatus.containsKey(npcId)) {
            barrowsBrothersStatus.put(npcId, false); // The brother is dead
            isPlayerFightingBrother = false;
            Rs2Prayer.toggle(prayerStyleToEnum());
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        currentCrypt = determineCrypt(playerLocation);
        nextCrypt = determineNextCrypt();
    }

    private String determineCrypt(WorldPoint playerLocation) {
        for (Map.Entry<String, WorldPoint> entry : CRYPT_LOCATIONS.entrySet()) {
            if (playerLocation.distanceTo(entry.getValue()) < 5) // Adjust the distance threshold as needed
            {
                return entry.getKey();
            }
        }
        return "Unknown";
    }

    private String determineNextCrypt() {
        for (Map.Entry<Integer, Boolean> entry : barrowsBrothersStatus.entrySet()) {
            if (entry.getValue()) // If the brother is alive
            {
                return BROTHER_CRYPT_MAPPING.get(entry.getKey());
            }
        }
        return "None";
    }

    public Map<Integer, Boolean> getBarrowsBrothersStatus() {
        return barrowsBrothersStatus;
    }

    public String getCurrentCrypt() {
        return currentCrypt;
    }

    public String getRecommendedPrayer() {
        return recommendedPrayer.toString();
    }


    // Add a method to check if the NPC ID belongs to a Barrows Brother
    private boolean isBarrowsBrother(int npcId) {
        for (int id : BARROWS_BROTHERS_IDS) {
            if (npcId == id) {
                return true;
            }
        }
        return false;
    }

    public String getNextCrypt() {
        return nextCrypt;
    }

    // Add a method to retrieve the flag indicating if the player is fighting a brother
    public boolean isPlayerFightingBrother() {
        return isPlayerFightingBrother;
    }

    public WorldPoint getNextCryptLocation() {
        String nextCrypt = determineNextCrypt();
        return CRYPT_LOCATIONS.getOrDefault(nextCrypt, null);
    }

    public Rs2PrayerEnum prayerStyleToEnum() {
        switch (recommendedPrayer) {
            case MELEE:
                return Rs2PrayerEnum.PROTECT_MELEE;
            case RANGED:
                return Rs2PrayerEnum.PROTECT_RANGE;
            case MAGE:
                return Rs2PrayerEnum.PROTECT_MAGIC;
        }
        return Rs2PrayerEnum.PROTECT_MELEE;
    }


}
