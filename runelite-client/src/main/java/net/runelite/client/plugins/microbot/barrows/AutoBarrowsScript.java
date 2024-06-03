package net.runelite.client.plugins.microbot.barrows;

import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.barrows.enums.STATE;
import net.runelite.client.plugins.microbot.barrows.models.BarrowsBrother;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AutoBarrowsScript extends Script {

    private static final int CRYPT_REGION_ID = 14231;

    public static final WorldArea BARROWS_AREA = new WorldArea(
            3545, // x-coordinate of the top-left corner
            3267, // y-coordinate of the top-left corner
            3585 - 3545, // width (x2 - x1)
            3320 - 3267, // height (y1 - y2)
            0 // plane
    );

    public STATE state = STATE.IDLE;

    public static String version = "0,0,1";

    AutoBarrowsConfig config;

    AutoBarrowsPlugin plugin;
    public ArrayList<BarrowsBrother> barrowsBrothers = new ArrayList<BarrowsBrother>();

    public boolean run(AutoBarrowsConfig config, AutoBarrowsPlugin plugin) {
        Microbot.enableAutoRunOn = false;
        this.config = config;
        this.plugin = plugin;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn() || Microbot.pauseAllScripts) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                switch (state) {
                    case IDLE: break;
                    case BANKING: break;
                    case DIGGING: {
                        if(isInCrypt()) {
                            state = STATE.SEARCHING_GRAVE;
                            break;
                        }

                        if(Rs2Inventory.contains("Spade")) {
                            Rs2Inventory.interact("Spade", "Dig");
                            sleepUntil(() -> plugin.isInCrypt());
                            state = STATE.SEARCHING_GRAVE;

                        }
                        break;
                    }
                    case FIGHTHING: {
                        if(!Rs2Combat.inCombat()) {
                            Rs2Npc.attack(plugin.brotherToFight.getId());
                        }

                        break;
                    }
                    case WALKING: {

                        boolean isNearHill = Rs2Walker.walkTo(plugin.getNextCryptLocation(), 1);
                        System.out.println(isNearHill);
                        if(plugin.isInCrypt()) {
                            state = STATE.SEARCHING_GRAVE;
                        }
                        break;
                    }
                    case LEAVING_CRYPT: {
                        if(!Microbot.getClient().getLocalPlayer().isInteracting() && plugin.isInCrypt()) {
                            boolean isNearStairs = Rs2Walker.walkMiniMap(Rs2GameObject.findObjectById(plugin.brotherToFight.getStaircaseId()).getWorldLocation());

                            if(isNearStairs) {
                                Rs2GameObject.interact(plugin.brotherToFight.getStaircaseId(), "Climb-up");
                                sleep(0, 400);
                            }
                        } else if (!plugin.isInCrypt()) {
                            state = STATE.WALKING;
                        }
                        break;
                    }

                    case SEARCHING_GRAVE: {
                        if(!Microbot.getClient().getLocalPlayer().isInteracting()) {
                            Rs2GameObject.interact("Sarcophagus", "Search");
                        }

                        if(Rs2Dialogue.isInDialogue()) {
                            System.out.println("WE FOUND THE TUNNEL");
                            Rs2Dialogue.clickContinue();

                            if(Rs2Dialogue.hasSelectAnOption()) {
                                Rs2Dialogue.selectOption();
                            }
                        }
                        break;
                    }
                }


            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private void calculateBrotherToKill () {
        if (barrowsBrothers.stream().count() > 0) {
            for (BarrowsBrother brother:barrowsBrothers) {
                if(brother.isDead) {
                    barrowsBrothers.remove(barrowsBrothers.indexOf(brother));
                } else {
                    state = brother.stateToExecute;
                }
            }
        } else {
            state = STATE.BANKING;
        }

    }

    private void handleBanking () {

    }

    private void handleTeleport () {
        if(Rs2Inventory.contains("Barrows Teleport")) {
            Rs2Inventory.interact("Barrows Teleport");
        } else {
            System.out.println("No teleport in inv");
            state = STATE.ERROR;
            Microbot.status = "ERROR NO TELEPORTS";
            shutdown();
        }
    }

    private void handleBrotherFight (BarrowsBrother brother) {
        if (Rs2Npc.getNpc(brother.id) != null) {
            NPC currentBrother = Rs2Npc.getNpc(brother.id);
            if(!Rs2Combat.inCombat()) {
                Rs2Npc.attack(currentBrother);
                sleepUntil(() -> currentBrother.isDead());
            }
            else {
                if(currentBrother.isDead()) {

                }
            }
        }

    }
    public boolean isInCrypt()
    {
        return getRegionID() == CRYPT_REGION_ID;
    }

    public int getRegionID()
    {
        final Player localPlayer = Microbot.getClient().getLocalPlayer();

        if ( localPlayer == null )
        {
            return 0;
        }

        return localPlayer.getWorldLocation().getRegionID();
    }

    public WorldPoint getWorldLocation()
    {
        final Player localPlayer = Microbot.getClient().getLocalPlayer();

        if ( localPlayer == null )
        {
            return new WorldPoint(0, 0, 0);
        }

        return localPlayer.getWorldLocation();
    }

}
