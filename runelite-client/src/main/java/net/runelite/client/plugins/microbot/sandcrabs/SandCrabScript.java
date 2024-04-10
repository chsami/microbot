package net.runelite.client.plugins.microbot.sandcrabs;

import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.envisionplugins.breakhandler.util.Login;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.sandcrabs.enums.State;
import net.runelite.client.plugins.microbot.sandcrabs.models.ScanLocation;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SandCrabScript extends Script {

    public static double version = 1.0;

    public int afkTimer = 0;
    public int hijackTimer = 0;

    public State state = State.FIGHT;

    public ScanLocation currentScanLocation;

    public int timesHopped = 0;

    List<ScanLocation> sandCrabLocations = Arrays.asList(new ScanLocation(new WorldPoint(1843, 3462, 0)),
            new ScanLocation(new WorldPoint(1833, 3458, 0)),
            new ScanLocation(new WorldPoint(1790, 3468, 0)),
            new ScanLocation(new WorldPoint(1776, 3468, 0), true),
            new ScanLocation(new WorldPoint(1773, 3461, 0), true),
            new ScanLocation(new WorldPoint(1765, 3468, 0), true),
            new ScanLocation(new WorldPoint(1749, 3469, 0)),
            new ScanLocation(new WorldPoint(1738, 3468, 0)));

    public boolean run(SandCrabConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {
                long startTime = System.currentTimeMillis();

               // Rs2Combat.enableAutoRetialiate();

                if (otherPlayerDetected() && !Rs2Combat.inCombat())
                    hijackTimer++;
                else
                    hijackTimer = 0;

                if (hijackTimer > 10) {
                    state = State.HOP_WORLD;
                }

                if (sandCrabLocations.stream().noneMatch(x -> x.getWorldPoint().equals(Microbot.getClient().getLocalPlayer().getWorldLocation())) && state != State.RESET_AGGRO && state != State.WALK_BACK) {
                    state = State.SCAN_LOCATIONS;
                }

                if (config.useFood()) {
                    Rs2Player.eatAt(50);

                    if (Rs2Inventory.getInventoryFood().isEmpty()) {
                        Microbot.getWalker().hybridWalkTo(new WorldPoint(1720, 3465, 0));
                        if (Rs2Bank.useBank()) {
                            Rs2Bank.withdrawAll(config.food().getName());
                        }
                        return;
                    }
                }



                switch (state) {
                    case FIGHT:
                        if (!Microbot.getClient().getLocalPlayer().isInteracting() && Rs2Combat.inCombat()) {
                            Rs2Tab.switchToCombatOptionsTab();
                            Rs2Combat.enableAutoRetialiate();
                        }
                        if (!isNpcAggressive() || afkTimer > 10) {
                            state = State.AFK;
                        }
                        break;
                    case AFK:
                        afkTimer++;
                        if (Rs2Combat.inCombat()) {
                            resetAfkTimer();
                        }
                        if (afkTimer > 10) {
                            state = State.RESET_AGGRO;
                        }
                        break;
                    case RESET_AGGRO:
                        resetAggro();
                        break;
                    case WALK_BACK:
                        walkBack();
                        break;
                    case HOP_WORLD:
                        int world = Login.getRandomWorld(true, null);
                        Microbot.hopToWorld(world);
                        boolean result = sleepUntil(() -> Rs2Widget.findWidget("Switch World") != null);
                        if (result) {
                            VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                            sleepUntil(() -> Microbot.getClient().getGameState() == GameState.HOPPING);
                            sleepUntil(() -> Microbot.getClient().getGameState() == GameState.LOGGED_IN);
                            if (timesHopped > 10) {
                                timesHopped = 0;
                                state = State.SCAN_LOCATIONS;
                            } else {
                                timesHopped++;
                                hijackTimer = 0;
                                state = State.FIGHT;
                                resetScanLocations();
                            }

                        }
                        break;
                    case SCAN_LOCATIONS:
                        scanSandCrabLocations(config);
                        break;
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println(totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * Checks if there are sandy rocks spawned next to the player
     * This is used to know if the aggro timer has ran out
     *
     * @return true if npc is aggressive
     */
    private boolean isNpcAggressive() {
        List<NPC> npcs = Rs2Npc.getNpcs("Sandy rocks");
        if (npcs.isEmpty()) {
            return false;
        }
        for (NPC sandyRock : npcs) {
            //ignore sandcrabs far away from the player
            if (!sandyRock.getWorldArea().isInMeleeDistance(Microbot.getClient().getLocalPlayer().getWorldArea()))
                continue;

            return false; //found a sandy rock crab near the player
        }
        return true; //did not find any sandy rocks near the player
    }

    /**
     * Reset aggro will walk 20 tiles north
     */
    private void resetAggro() {
        boolean walkedFarEnough = Microbot.getWalker().hybridWalkTo(new WorldPoint(initialPlayerLocation.getX(), initialPlayerLocation.getY() + 40, initialPlayerLocation.getPlane()));
        if (!walkedFarEnough) return;

        state = State.WALK_BACK;
    }

    /**
     * Walks back to the initial player location when the script started
     */
    private void walkBack() {
        boolean backToInitialLocation = Microbot.getWalker().hybridWalkTo(initialPlayerLocation, false, true);
        if (!backToInitialLocation) return;

        resetAfkTimer();
    }

    /**
     * Reset afk timer and sets state back to fight
     */
    private void resetAfkTimer() {
        afkTimer = 0;
        state = State.FIGHT;
    }

    private boolean otherPlayerDetected() {
        return otherPlayerDetected(Microbot.getClient().getLocalPlayer().getWorldLocation());
    }

    private boolean otherPlayerDetected(WorldPoint worldPoint) {
        for (Player player : Rs2Player.getPlayers()) {
            if (player.getWorldLocation().distanceTo(worldPoint) > 2)
                continue;
            if (player == Microbot.getClient().getLocalPlayer()) continue;
            if (!player.isInteracting()) continue;
            return true;
        }
        return false;
    }

    private void scanSandCrabLocations(SandCrabConfig config) {
        if (config.threeNpcs()) {
            sandCrabLocations = sandCrabLocations.stream().filter(x -> x.hasThreeNpcs).collect(Collectors.toList());
        }
        currentScanLocation = sandCrabLocations.stream()
                .filter(x -> !x.isScanned())
                .min(Comparator.comparingInt(x -> x.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation())))
                .orElse(null);

        if (currentScanLocation == null) {
            state = State.HOP_WORLD;
            return;
        }
        boolean reachedLocation = Microbot.getWalker().hybridWalkTo(currentScanLocation.getWorldPoint(), false, true);
        if (reachedLocation) {
            if (otherPlayerDetected(currentScanLocation.getWorldPoint())) {
                currentScanLocation.scanned = true;
            } else {
                initialPlayerLocation = currentScanLocation.getWorldPoint();
                state = State.FIGHT;
            }
        } else {
            if (currentScanLocation.triedWalking > 20) { //something went wrong, just skip this location
                currentScanLocation.scanned = true;
            } else {
                currentScanLocation.triedWalking++;
            }
        }
    }

    private void resetScanLocations() {
        for(ScanLocation scanLocation: sandCrabLocations) {
            scanLocation.reset();
        }
    }
}
