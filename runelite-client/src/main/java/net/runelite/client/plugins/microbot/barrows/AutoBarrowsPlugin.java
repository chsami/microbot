package net.runelite.client.plugins.microbot.barrows;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.barrows.enums.PRAYSTYLE;
import net.runelite.client.plugins.microbot.barrows.enums.STATE;
import net.runelite.client.plugins.microbot.barrows.models.TheBarrowsBrothers;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

@PluginDescriptor(
        name = PluginDescriptor.Pumster + "Barrows",
        description = "Microbot barrows plugin",
        tags = {"pvm", "microbot", "barrows"},
        enabledByDefault = false
)
public class AutoBarrowsPlugin extends Plugin {
    private static final int[] BARROWS_BROTHERS_IDS = {
            NpcID.AHRIM_THE_BLIGHTED,
            NpcID.DHAROK_THE_WRETCHED,
            NpcID.GUTHAN_THE_INFESTED,
            NpcID.KARIL_THE_TAINTED,
            NpcID.TORAG_THE_CORRUPTED,
            NpcID.VERAC_THE_DEFILED
    };

    private static final Map<TheBarrowsBrothers, WorldPoint> CRYPT_LOCATIONS = new HashMap<>();

    private static final Map<TheBarrowsBrothers, WorldPoint> CRYPT_BELOW_LOCATIONS = new HashMap<>();

    private static final Map<Integer, PRAYSTYLE> PRAYER_RECOMMENDATIONS = new HashMap<>();
    private static final Map<Integer, String> BROTHER_CRYPT_MAPPING = new HashMap<>();

    private static final ImmutableList<Integer> POSSIBLE_SOLUTIONS = ImmutableList.of(
            ComponentID.BARROWS_PUZZLE_ANSWER1,
            ComponentID.BARROWS_PUZZLE_ANSWER2,
            ComponentID.BARROWS_PUZZLE_ANSWER3
    );


    public boolean isPlayerFightingBrother = false;


    static {
        // Define the approximate center points for each crypt
        CRYPT_LOCATIONS.put(TheBarrowsBrothers.AHRIM, new WorldPoint(3565, 3289, 0));
        CRYPT_LOCATIONS.put(TheBarrowsBrothers.DHAROK, new WorldPoint(3575, 3298, 0));
        CRYPT_LOCATIONS.put(TheBarrowsBrothers.GUTHAN, new WorldPoint(3575, 3283, 0));
        CRYPT_LOCATIONS.put(TheBarrowsBrothers.KARIL, new WorldPoint(3566, 3276, 0));
        CRYPT_LOCATIONS.put(TheBarrowsBrothers.TORAG, new WorldPoint(3554, 3282, 0));
        CRYPT_LOCATIONS.put(TheBarrowsBrothers.VERAC, new WorldPoint(3557, 3297, 0));

        CRYPT_BELOW_LOCATIONS.put(TheBarrowsBrothers.AHRIM, new WorldPoint(3565, 3289, 3));
        CRYPT_BELOW_LOCATIONS.put(TheBarrowsBrothers.DHAROK, new WorldPoint(3575, 3298, 3));
        CRYPT_BELOW_LOCATIONS.put(TheBarrowsBrothers.GUTHAN, new WorldPoint(3575, 3283, 3));
        CRYPT_BELOW_LOCATIONS.put(TheBarrowsBrothers.KARIL, new WorldPoint(3566, 3276, 3));
        CRYPT_BELOW_LOCATIONS.put(TheBarrowsBrothers.TORAG, new WorldPoint(3554, 3282, 3));
        CRYPT_BELOW_LOCATIONS.put(TheBarrowsBrothers.VERAC, new WorldPoint(3557, 3297, 3));

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
    private AutoBarrowsOverlay barrowsOverlay;

    @Inject
    private AutoBarrowsConfig config;

    @Inject
    public AutoBarrowsScript barrowsScript;


    @Provides
    AutoBarrowsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoBarrowsConfig.class);
    }

    private String currentCrypt = "Unknown";
    public PRAYSTYLE recommendedPrayer = PRAYSTYLE.OFF;
    private String nextCrypt = "None";

    public TheBarrowsBrothers brotherToFight;


    @Override
    protected void startUp() throws Exception {
        overlayManager.add(barrowsOverlay);

        // Initialize the status of all Barrows Brothers to alive
        barrowsScript.run(config, this);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(barrowsOverlay);
        currentCrypt = "Unknown";
        recommendedPrayer = PRAYSTYLE.OFF;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        int npcId = event.getNpc().getId();
        // Id	15007745 FOUDND A TUNNEL
        if (isInCrypt() && Arrays.stream(BARROWS_BROTHERS_IDS).anyMatch(x -> x == npcId) && !event.getNpc().isInteracting()  && brotherToFight == TheBarrowsBrothers.fromId(npcId)) {
            recommendedPrayer = PRAYER_RECOMMENDATIONS.getOrDefault(npcId, PRAYSTYLE.OFF);
                Rs2Npc.attack(npcId);
                isPlayerFightingBrother = true;
                barrowsScript.state = STATE.FIGHTHING;

        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {

        if (event.getGroupId() == 15007745) {
            Microbot.getClient().getWidget(15007745);
        }
     if (event.getGroupId() == InterfaceID.BARROWS_PUZZLE)
        {
            final int answer = Microbot.getClient().getWidget(ComponentID.BARROWS_PUZZLE_SEQUENCE_1).getModelId() - 3;
            System.out.println("Answer: " + answer);
            for (int puzzleComponent : POSSIBLE_SOLUTIONS)
            {
                final Widget widgetToCheck = Microbot.getClient().getWidget(puzzleComponent);

                if (widgetToCheck != null && widgetToCheck.getModelId() == answer)
                {
                    System.out.println("Clicking answer");
                    System.out.print(widgetToCheck);
                    Rs2Widget.clickWidget(widgetToCheck.getId());
                    break;
                }
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        int npcId = event.getNpc().getId();
        if (isInCrypt() && Arrays.stream(BARROWS_BROTHERS_IDS).anyMatch(x -> x == npcId) && !Rs2Combat.inCombat() && event.getNpc().isDead()) {
            isPlayerFightingBrother = false;
            recommendedPrayer = PRAYSTYLE.OFF;
            // Move out of crypt
            barrowsScript.state = STATE.LEAVING_CRYPT;


        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        currentCrypt = determineCrypt(playerLocation);
        nextCrypt = determineNextCrypt();
        handlePraying();
    }

    private String determineCrypt(WorldPoint playerLocation) {
        for (Map.Entry<TheBarrowsBrothers, WorldPoint> entry : CRYPT_LOCATIONS.entrySet()) {
            if (playerLocation.distanceTo(entry.getValue()) < 3) // Adjust the distance threshold as needed
            {
                brotherToFight = entry.getKey();
                if(!Microbot.getClient().getLocalPlayer().isInteracting() && Microbot.getClient().getVarbitValue(entry.getKey().getKilledVarbit()) == 0) {
                    barrowsScript.state = STATE.DIGGING;
//                    if(Rs2Inventory.contains("Spade")) {
//                        Rs2Inventory.interact("Spade", "Dig");
//                        barrowsScript.state = STATE.SEARCHING_GRAVE;
//                        sleepUntil(() -> !Microbot.getClient().getLocalPlayer().isInteracting());
//                    }
                } else {
                    System.out.println("Current varbit of this brother" + Microbot.getClient().getVarbitValue(entry.getKey().getKilledVarbit()));
                }
                return entry.getKey().getName();
            }
        }
        return "Unknown";
    }

    private String determineNextCrypt() {
        for (TheBarrowsBrothers brother : TheBarrowsBrothers.values())
        {
            final boolean brotherSlain = Microbot.getClient().getVarbitValue(brother.getKilledVarbit()) == 0;
            if (brotherSlain) // If the brother is alive
            {
                return BROTHER_CRYPT_MAPPING.get(brother.getId());
            }
        }
        return "None";
    }


    public String getCurrentCrypt() {
        return currentCrypt;
    }

    public String getRecommendedPrayer() {
        return recommendedPrayer.toString();
    }

    public boolean isInCrypt()
    {
        Player localPlayer = client.getLocalPlayer();
        return localPlayer != null && localPlayer.getWorldLocation().getRegionID() == 14231;
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

    private void handlePraying () {
        if(Rs2Prayer.isOutOfPrayer()) {
            return;
        }

        boolean isNearNpc = false;

        if (recommendedPrayer == PRAYSTYLE.OFF) {
            if(Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MAGIC)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, false );
            }
            if(Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, false);
            }
            if(Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MELEE)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, false);
            }
            return;
        }

        for (TheBarrowsBrothers brother:TheBarrowsBrothers.values()) {
            if(Rs2Npc.getNpc(brother.getId()) != null) {
                isNearNpc = true;
                break;
            }

        }

        // Should the auto prayer fuck up
        if(!isNearNpc) return;

        switch (recommendedPrayer) {
            case MAGE: {
                if(!Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MAGIC)) {
                    Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
                }
                break;
            }
            case RANGED: {
                if(!Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE)) {
                    Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
                }
                break;
            }
            case MELEE: {
                if(!Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MELEE)) {
                    Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
                }
                break;
            }
        }
    }


}
