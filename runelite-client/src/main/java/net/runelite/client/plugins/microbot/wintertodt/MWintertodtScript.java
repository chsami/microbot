package net.runelite.client.plugins.microbot.wintertodt;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.microbot.wintertodt.enums.State;

import java.util.concurrent.TimeUnit;

import static net.runelite.api.ObjectID.BRAZIER_29312;
import static net.runelite.api.ObjectID.BURNING_BRAZIER_29314;
import static net.runelite.client.plugins.microbot.util.player.Rs2Player.eatAt;


public class MWintertodtScript extends Script {
    public static double version = 1.0;

    public static State state = State.BANKING;
    public static boolean resetActions = false;

    final WorldPoint BOSS_ROOM = new WorldPoint(1630, 3982, 0);

    MWintertodtConfig config;

    String axe = "";

    public boolean run(MWintertodtConfig config) {
        this.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {

                long startTime = System.currentTimeMillis();

                if (config.axeInInventory() && axe.equals("")) {
                    axe = Rs2Inventory.get("axe").name;
                }

                boolean wintertodtRespawning = Rs2Widget.hasWidget("returns in");
                boolean isWintertodtAlive = Rs2Widget.hasWidget("Wintertodt's Energy");
                GameObject brazier = Rs2GameObject.findObject(BRAZIER_29312, config.brazierLocation().getOBJECT_BRAZIER_LOCATION());
                boolean hasFixAction = brazier != null && Rs2GameObject.hasAction(Rs2GameObject.convertGameObjectToObjectComposition(brazier), "fix");
                GameObject fireBrazier = Rs2GameObject.findObject(ObjectID.BURNING_BRAZIER_29314, config.brazierLocation().getOBJECT_BRAZIER_LOCATION());
                boolean needBanking = !Rs2Inventory.hasItemAmount(config.food().getName(), config.foodAmount())
                        && Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) < config.hpTreshhold();
                boolean isNearbyBrazier = Rs2Player.getWorldLocation().distanceTo(config.brazierLocation().getOBJECT_BRAZIER_LOCATION()) < 4;
                Widget wintertodtHealthbar = Rs2Widget.getWidget(25952276);
                int wintertodtHp = -1;

                if (wintertodtHealthbar != null && isWintertodtAlive) {
                    String widgetText = wintertodtHealthbar.getText();
                    wintertodtHp = Integer.parseInt(widgetText.split("\\D+")[1]);
                } else {
                    wintertodtHp = -1;
                }

                if (Rs2Widget.hasWidget("Leave and lose all progress")) {
                    VirtualKeyboard.typeString("1");
                    sleep(1600, 2000);
                    return;
                }
                System.out.println("AFTER LEAVE AND PROGRESS");


                if (!config.fletchRoots() && Rs2Inventory.hasItem(ItemID.KNIFE)) {
                    Rs2Inventory.drop(ItemID.KNIFE);
                }


                //todo: hasFixAction is not working?
                if (isNearbyBrazier && isWintertodtAlive && hasFixAction != false && !needBanking) {
                    state = State.FIX_BRAZIER;
                }

                if (isNearbyBrazier && isWintertodtAlive && fireBrazier == null && !needBanking) {
                    state = State.LIGHT_BRAZIER;
                }

                if (needBanking) {
                    state = State.BANKING;
                }
                boolean ate = eatAt(70);
                if (ate) {
                    resetActions = true;
                }

                for (GraphicsObject graphicsObject : Microbot.getClient().getGraphicsObjects())
                {
                    if (!resetActions && graphicsObject.getId() == 502
                            && WorldPoint.fromLocalInstance(Microbot.getClient(),
                            graphicsObject.getLocation()).distanceTo(Rs2Player.getWorldLocation()) < 4) {
                        //walk south
                        Rs2Walker.walkFastCanvas(new WorldPoint(Rs2Player.getWorldLocation().getX(), Rs2Player.getWorldLocation().getY() - 1, Rs2Player.getWorldLocation().getPlane()));
                        Rs2Player.waitForWalking(2000);
                        resetActions = true;
                    }
                }

                switch (state) {
                    case BANKING:
                        if (!Rs2Player.isFullHealth() && Rs2Inventory.hasItem(config.food().getName())) {
                            eatAt(99);
                            return;
                        }
                        if (Rs2Inventory.hasItemAmount(config.food().getName(), config.foodAmount())) {
                            state = State.ENTER_ROOM;
                            return;
                        }
                        WorldPoint bankLocation = new WorldPoint(1640, 3944, 0);
                        if (Rs2Player.getWorldLocation().distanceTo(bankLocation) > 6) {
                            Rs2Walker.walkTo(bankLocation);
                            Rs2Player.waitForWalking();
                            if (config.openCrates()) {
                                Rs2Inventory.interact("supply crate", "open");
                            }
                        }
                        Rs2Bank.useBank();
                        if (!Rs2Bank.isOpen()) return;
                        if (!config.openCrates()) {
                            Rs2Bank.depositOne("supply crate");
                        } else {
                            Rs2Bank.depositAll();
                        }
                        int foodCount = (int) Rs2Inventory.getInventoryFood().stream().count();
                        if (config.fixBrazier()) {
                            Rs2Bank.withdrawX(true, "hammer", 1);
                        }
                        Rs2Bank.withdrawX(true, "tinderbox", 1);
                        if (config.fletchRoots()) {
                            Rs2Bank.withdrawX(true, "knife", 1);
                        }
                        if (config.axeInInventory()) {
                            Rs2Bank.withdrawX(true, axe, 1);
                        }
                        if (!Rs2Bank.hasBankItem(config.food().getName(), config.foodAmount())) {
                            Microbot.showMessage("Insufficient food supply");
                            Microbot.pauseAllScripts = true;
                            return;
                        }
                        Rs2Bank.withdrawX(config.food().getName(), config.foodAmount() - foodCount);
                        sleepUntil(() -> Rs2Inventory.hasItemAmount("monkfish", config.foodAmount()));
                        break;
                    case ENTER_ROOM:
                        if (!wintertodtRespawning && !isWintertodtAlive) {
                            Rs2Walker.walkTo(BOSS_ROOM);
                        } else {
                            state = State.WAITING;
                        }
                        break;
                    case WAITING:
                        walkToBrazier();
                        if (isWintertodtAlive) {
                            state = State.LIGHT_BRAZIER;
                        }
                        break;
                    case LIGHT_BRAZIER:
                        if (!isWintertodtAlive) {
                            state = State.BANKING;
                            return;
                        }
                        if (brazier != null && !Rs2Player.isAnimating()) {
                            Rs2GameObject.interact(brazier, "light");
                            sleep(1000);
                        } else {
                            state = State.CHOP_ROOTS;
                        }
                        break;
                    case CHOP_ROOTS:
                        if (!isWintertodtAlive) {
                            state = State.BANKING;
                            System.out.println("BANKING!");
                            return;
                        }
                        if (Rs2Inventory.hasItem(ItemID.BRUMA_KINDLING)
                                || (wintertodtHp > 0
                                && wintertodtHp < 20
                                && Rs2Inventory.hasItemAmount(ItemID.BRUMA_ROOT, 5))) {
                            state = State.BURN_LOGS;
                            resetActions = true;
                            return;
                        }
                        Rs2Combat.setSpecState(true, 1000);
                        if (!Rs2Inventory.isFull()) {
                            if (!Rs2Player.isAnimating() || resetActions) {
                                Rs2GameObject.interact(29311, "Chop");
                                sleepUntil(() -> Rs2Player.isAnimating(), 2000);
                                resetActions = false;
                            }
                        } else {
                            state = State.FLETCH_LOGS;
                            resetActions = true;
                        }
                        break;
                    case FLETCH_LOGS:
                        if (!isWintertodtAlive) {
                            state = State.BANKING;
                            return;
                        }
                        if (!config.fletchRoots()) {
                            state = State.BURN_LOGS;
                            return;
                        }
                        if (!Microbot.isGainingExp || resetActions) {
                            walkToBrazier();
                            Rs2Inventory.combine(ItemID.KNIFE, ItemID.BRUMA_ROOT);
                            Rs2Player.waitForAnimation();
                            resetActions = false;
                        }
                        if (!Rs2Inventory.hasItem(ItemID.BRUMA_ROOT)
                                && !Rs2Inventory.hasItem(ItemID.BRUMA_KINDLING)) {
                            state = State.CHOP_ROOTS;
                            resetActions = true;
                            return;
                        } else if (!Rs2Inventory.hasItem(ItemID.BRUMA_ROOT)
                                || wintertodtHp > 0 && wintertodtHp < 20) {
                            state = State.BURN_LOGS;
                            resetActions = true;
                        }
                        break;
                    case BURN_LOGS:
                        if (!isWintertodtAlive) {
                            state = State.BANKING;
                            return;
                        }
                        if (wintertodtHp >= 20
                                && (config.fletchRoots() && !Rs2Inventory.hasItem(ItemID.BRUMA_KINDLING))
                                || !config.fletchRoots() && !Rs2Inventory.hasItem(ItemID.BRUMA_ROOT)) {
                            state = State.CHOP_ROOTS;
                            resetActions = true;
                            return;
                        }
                        if (wintertodtHp < 20  && !Rs2Inventory.hasItem(ItemID.BRUMA_ROOT) && !Rs2Inventory.hasItem(ItemID.BRUMA_KINDLING) )
                        {
                            return;
                        }
                        if (!Microbot.isGainingExp || resetActions) {
                            TileObject burningBrazier = Rs2GameObject.findObjectById(BURNING_BRAZIER_29314);
                            if (burningBrazier.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) < 10) {
                                Rs2GameObject.interact(BURNING_BRAZIER_29314, "feed");
                                Rs2Player.waitForAnimation();
                            }
                            resetActions = false;
                        }
                        break;
                    case FIX_BRAZIER:
                        if (hasFixAction == false) {
                            state = state.BURN_LOGS;
                            return;
                        }
                        Rs2GameObject.interact(brazier);
                        Rs2Player.waitForAnimation();
                        break;
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                 System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    public static void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
        Actor actor = hitsplatApplied.getActor();

        if (actor != Microbot.getClient().getLocalPlayer()) {
            return;
        }

        resetActions = true;

    }

    private void walkToBrazier() {
        if (Rs2Player.getWorldLocation().distanceTo(config.brazierLocation().getBRAZIER_LOCATION()) > 6) {
            Rs2Walker.walkTo(config.brazierLocation().getBRAZIER_LOCATION(), 2);
        } else if (!Rs2Player.getWorldLocation().equals(config.brazierLocation().getBRAZIER_LOCATION())) {
            Rs2Walker.walkFastCanvas(config.brazierLocation().getBRAZIER_LOCATION());
            if (Rs2Player.getWorldLocation().distanceTo(config.brazierLocation().getBRAZIER_LOCATION()) > 4) {
                Rs2Player.waitForWalking();
            } else {
                sleep(3000);
            }
        }
    }
}
