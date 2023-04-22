package net.runelite.client.plugins.microbot.scripts.bosses;

import net.runelite.api.GameState;
import net.runelite.api.Perspective;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Script;
import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.scripts.bosses.enums.ZulrahPhase;
import net.runelite.client.plugins.microbot.scripts.bosses.enums.ZulrahType;
import net.runelite.client.plugins.microbot.scripts.bosses.patterns.*;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class ZulrahScript extends Script {

    public static NPC zulrah;
    public static ZulrahInstance instance;
    private static final ZulrahPattern[] patterns = new ZulrahPattern[]
            {
                    new ZulrahPatternA(),
                    new ZulrahPatternB(),
                    new ZulrahPatternC(),
                    new ZulrahPatternD()
            };

    @Override
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            //automation step
            try {
                if (instance == null) return;
                if (instance.getPhase().getType() == ZulrahType.RANGE) {
                    Rs2Prayer.turnOnRangePrayer();
                } else if (instance.getPhase().getType() == ZulrahType.MAGIC) {
                    Rs2Prayer.turnOnMagePrayer();
                } else { // melee

                }
                if (instance.getPhase() != null
                        && !Microbot.getClient().getSelectedSceneTile().getLocalLocation().equals(Microbot.getClient().getLocalPlayer().getLocalLocation())) {
                    Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), instance.getPhase().getStandTile(instance.getStartLocation()));
                    Microbot.getMouse().click(poly.getBounds());
                    sleep(1000);
                    sleepUntil(() -> !Microbot.isWalking());
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }

        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    public static void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (npc != null && npc.getName() != null &&
                npc.getName().toLowerCase().contains("zulrah")) {
            zulrah = npc;
        }
    }

    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (npc != null && npc.getName() != null &&
                npc.getName().toLowerCase().contains("zulrah")) {
            zulrah = null;
        }
    }

    public void onGameTick(GameTick event) {
        if (Microbot.getClient().getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (zulrah == null) {
            if (instance != null) {
                System.out.println("Zulrah encounter has ended.");
                instance = null;
            }
            return;
        }

        if (instance == null) {
            instance = new ZulrahInstance(zulrah);
            System.out.println("Zulrah encounter has started.");
        }

        ZulrahPhase currentPhase = ZulrahPhase.valueOf(zulrah, instance.getStartLocation());

        if (instance.getPhase() == null) {
            instance.setPhase(currentPhase);
        } else if (!instance.getPhase().equals(currentPhase)) {
            ZulrahPhase previousPhase = instance.getPhase();
            instance.setPhase(currentPhase);
            instance.nextStage();

            System.out.println("Zulrah phase has moved from " + previousPhase + " to " + currentPhase + " stage:" + instance.getStage());
        }

        ZulrahPattern pattern = instance.getPattern();

        if (pattern == null) {
            int potential = 0;
            ZulrahPattern potentialPattern = null;

            for (ZulrahPattern p : patterns) {
                if (p.stageMatches(instance.getStage(), instance.getPhase())) {
                    potential++;
                    potentialPattern = p;
                }
            }

            if (potential == 1) {
                System.out.println("Zulrah pattern identified: " + potentialPattern);

                instance.setPattern(potentialPattern);
            }
        } else if (pattern.canReset(instance.getStage()) && (instance.getPhase() == null || instance.getPhase().equals(pattern.get(0)))) {
            System.out.println("Zulrah pattern has reset.");

            instance.reset();
        }
    }

}
