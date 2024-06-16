package net.runelite.client.plugins.microbot.fishing.minnows;

import net.runelite.api.GraphicID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

public class MinnowsScript extends Script {

    public static final String version = "1.0.1";
    public static final WorldArea MINNOWS_PLATFORM = new WorldArea(new WorldPoint(2607, 3440, 0), 2622 - 2607, 3446 - 3440);
    private static final int FLYING_FISH_GRAPHIC_ID = GraphicID.FLYING_FISH;

    private static final int FISHING_SPOT_1_ID = NpcID.FISHING_SPOT_7732;
    private static final int FISHING_SPOT_2_ID = NpcID.FISHING_SPOT_7733;

    private static int TARGET_SPOT_ID = FISHING_SPOT_1_ID;
    private NPC fishingspot;
    private int timeout;

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (!MINNOWS_PLATFORM.contains(Rs2Player.getWorldLocation())) {
                    Microbot.showMessage("Not at minnows platform, please go to minnows platform to start the script.");
                    Microbot.status = "NOT AT MINNOWS PLATFORM";
                    sleep(15000);
                    return;
                }
                if (Rs2Player.isMoving()) {
                    Microbot.status = "MOVING";
                    return;
                }
                if (Rs2Player.isInteracting()) {
                    if (Microbot.getClient().getLocalPlayer().getInteracting().hasSpotAnim(FLYING_FISH_GRAPHIC_ID)) {
                        if (TARGET_SPOT_ID == FISHING_SPOT_1_ID) {
                            TARGET_SPOT_ID = FISHING_SPOT_2_ID;
                        } else if (TARGET_SPOT_ID == FISHING_SPOT_2_ID) {
                            TARGET_SPOT_ID = FISHING_SPOT_1_ID;
                        }
                        Microbot.status = "DODGING FLYING FISH";
                        fishingspot = Rs2Npc.getNpc(TARGET_SPOT_ID);
                        Rs2Npc.interact(fishingspot, "Small Net");
                        return;
                    }
                    Microbot.status = "FISHING";
                    return;
                }
                if (timeout != 0) return;
                Microbot.status = "INTERACTING";
                fishingspot = Rs2Npc.getNpc(TARGET_SPOT_ID);
                Rs2Npc.interact(fishingspot, "Small Net");

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void onGameTick() {
        if (timeout > 0) {
            timeout--;
        }
        if (Rs2Player.isInteracting()) {
            //set timeout to random number between 2 and 3
            timeout = (int) (Math.random() * 2) + 2;
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
