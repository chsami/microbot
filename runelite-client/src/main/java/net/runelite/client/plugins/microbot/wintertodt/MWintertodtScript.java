package net.runelite.client.plugins.microbot.wintertodt;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.microbot.wintertodt.enums.State;

import java.util.concurrent.TimeUnit;

import static net.runelite.api.ObjectID.BRAZIER_29312;
import static net.runelite.api.ObjectID.BURNING_BRAZIER_29314;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;
import static net.runelite.client.plugins.microbot.util.player.Rs2Player.eatAt;


/**
 * 27/04/2024 - Reworking the MWintertodtScript.java
 */

public class MWintertodtScript extends Script {
    public static double version = 1.4;

    public static State state = State.BANKING;
    public static boolean resetActions = false;

    final WorldPoint BOSS_ROOM = new WorldPoint(1630, 3982, 0);

    static MWintertodtConfig config;

    String axe = "";
    final String SUPPLY_CRATE = "supply crate";
    int wintertodtHp = -1;

    private static boolean lockState = false;

    public boolean run(MWintertodtConfig config) {
        this.config = config;
        state = State.BANKING;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {

                long startTime = System.currentTimeMillis();

                if (config.axeInInventory()) {
                    if (!Rs2Inventory.hasItem("axe")) {
                        Microbot.showMessage("It seems that you selected axeInInventory option but no axe was found in your inventory.");
                        sleep(5000);
                        return;
                    }
                    axe = Rs2Inventory.get("axe").name;
                }


                boolean wintertodtRespawning = Rs2Widget.hasWidget("returns in");
                boolean isWintertodtAlive = Rs2Widget.hasWidget("Wintertodt's Energy");
                GameObject brazier = Rs2GameObject.findObject(BRAZIER_29312, config.brazierLocation().getOBJECT_BRAZIER_LOCATION());
                GameObject fireBrazier = Rs2GameObject.findObject(ObjectID.BURNING_BRAZIER_29314, config.brazierLocation().getOBJECT_BRAZIER_LOCATION());
                boolean playerIsLowHealth = (double) (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100) / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS) <= config.hpTreshhold();
                boolean needBanking = !Rs2Inventory.hasItemAmount(config.food().getName(), config.foodAmount(), false, false)
                        && playerIsLowHealth;
                Widget wintertodtHealthbar = Rs2Widget.getWidget(25952276);

                if (wintertodtHealthbar != null && isWintertodtAlive) {
                    String widgetText = wintertodtHealthbar.getText();
                    wintertodtHp = Integer.parseInt(widgetText.split("\\D+")[1]);
                } else {
                    wintertodtHp = -1;
                }

                if (Rs2Widget.hasWidget("Leave and lose all progress")) {
                    Rs2Keyboard.typeString("1");
                    sleep(1600, 2000);
                    return;
                }

                dropUnnecessaryItems();
                shouldLightBrazier(isWintertodtAlive, needBanking, fireBrazier, brazier);
                shouldBank(needBanking);
                shouldEat();
                dodgeOrbDamage();

                if (!needBanking) {
                    if (!isWintertodtAlive) {
                        if (state != State.ENTER_ROOM && state != State.WAITING && state != State.BANKING) {
                            setLockState(State.GLOBAL, false);
                            changeState(State.BANKING);
                        }
                    } else {
                        handleMainLoop();
                    }
                } else {
                    setLockState(State.BANKING, false);
                }

                //todo: hasFixAction is not working?
//                if (isNearbyBrazier && isWintertodtAlive && hasFixAction != false && !needBanking) {
//                    state = State.FIX_BRAZIER;
//                }


                switch (state) {
                    case BANKING:
                        if (!handleBankLogic(config)) return;
                        if (Rs2Player.isFullHealth() && Rs2Inventory.hasItemAmount(config.food().getName(), config.foodAmount(), false, true)) {
                            changeState(State.ENTER_ROOM);
                        }
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
                        shouldLightBrazier(isWintertodtAlive, needBanking, fireBrazier, brazier);
                        break;
                    case LIGHT_BRAZIER:
                        if (brazier != null && !Rs2Player.isAnimating()) {
                            Rs2GameObject.interact(brazier, "light");
                            sleep(1000);
                            return;
                        }
                        break;
                    case CHOP_ROOTS:
                        Rs2Combat.setSpecState(true, 1000);
                        if (!Rs2Player.isAnimating() || resetActions) {
                            Rs2GameObject.interact(ObjectID.BRUMA_ROOTS, "Chop");
                            sleepUntil(Rs2Player::isAnimating, 2000);
                            resetActions = false;
                        }
                        break;
                    case FLETCH_LOGS:
                        if (!Microbot.isGainingExp || resetActions)
                        {
                            walkToBrazier();
                            Rs2Inventory.combine(ItemID.KNIFE, ItemID.BRUMA_ROOT);
                            Rs2Player.waitForAnimation();
                            resetActions = false;
                        }
                        break;
                    case BURN_LOGS:
                        if (!Microbot.isGainingExp || resetActions) {
                            TileObject burningBrazier = Rs2GameObject.findObjectById(BURNING_BRAZIER_29314);
                            if (burningBrazier.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) < 10) {
                                Rs2GameObject.interact(BURNING_BRAZIER_29314, "feed");
                                Rs2Player.waitForAnimation();
                                sleep(2000);
                            }
                            resetActions = false;
                        }
                        break;
//                    case FIX_BRAZIER: // TODO: fix this state
//                        if (hasFixAction == false) {
//                            state = state.BURN_LOGS;
//                            return;
//                        }
//                        Rs2GameObject.interact(brazier);
//                        Rs2Player.waitForAnimation();
//                        break;
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

    private void handleMainLoop() {
        if (isWintertodtAlmostDead()) {
            setLockState(State.BURN_LOGS, false);
            if (shouldBurnLogs()) return;
        } else {
            if (shouldChopRoots()) return;
            if (shouldFletchRoots()) return;
            if (shouldBurnLogs()) return;
        }
    }

    private static void changeState(State scriptState) {
        changeState(scriptState, false);
    }

    private static void changeState(State scriptState, boolean lock) {
        if (state == scriptState || lockState) return;
        System.out.println("Changing current script state from: " + state + " to " +  scriptState);
        state = scriptState;
        resetActions = true;
        setLockState(scriptState, lock);
        lockState = lock;
    }

    private static void setLockState(State state, boolean lock) {
        if (lockState == lock) return;
        lockState = lock;
        System.out.println("State " + state.toString() + " has set lockState to " + lockState);
    }

    private static boolean shouldFletchRoots() {
        if (!config.fletchRoots()) return false;
        if (!Rs2Inventory.hasItem(ItemID.BRUMA_ROOT)) {
            setLockState(State.FLETCH_LOGS, false);
            return false;
        }
        changeState(State.FLETCH_LOGS, true);
        return true;
    }

    private boolean shouldBurnLogs() {
        if (!hasItemsToBurn()) {
            setLockState(State.BURN_LOGS, false);
            return false;
        }
        changeState(State.BURN_LOGS, true);
        return true;
    }

    private boolean shouldEat() {
        if (eatAt(config.eatAt())) {
            resetActions = true;
            return true;
        }
        return false;
    }

    private boolean shouldBank(boolean needBanking) {
        if (!needBanking) return  false;
        changeState(State.BANKING);
        return true;
    }

    private boolean shouldChopRoots() {
        if (Rs2Inventory.isFull()) {
            if (state == State.CHOP_ROOTS) {
                setLockState(State.CHOP_ROOTS, false);
            }
            return false;
        }
        if (hasItemsToBurn()) return false;
        changeState(State.CHOP_ROOTS, true);
        return true;
    }

    private boolean shouldLightBrazier(boolean isWintertodtAlive, boolean needBanking, GameObject fireBrazier, GameObject brazier) {
        if (!isWintertodtAlive) return false;
        if (needBanking) return false;
        if (state == State.CHOP_ROOTS) return false;// we are most likely to far from the brazier to light it in time

        if (brazier == null || fireBrazier != null) {
            setLockState(State.LIGHT_BRAZIER, false);
            return false;
        }

        changeState(State.LIGHT_BRAZIER, true);
        return true;
    }

    private boolean isWintertodtAlmostDead() {
        return wintertodtHp > 0 && wintertodtHp < 15;
    }

    private boolean hasItemsToBurn() {
        return Rs2Inventory.hasItem(ItemID.BRUMA_KINDLING) || Rs2Inventory.hasItem(ItemID.BRUMA_ROOT);
    }

    private void dropUnnecessaryItems() {
        if (!config.fletchRoots() && Rs2Inventory.hasItem(ItemID.KNIFE)) {
            Rs2Inventory.drop(ItemID.KNIFE);
        }
//        if (!config.fixBrazier() && Rs2Inventory.hasItem(ItemID.HAMMER)) {
//            Rs2Inventory.drop(ItemID.HAMMER);
//        }
        if (Rs2Equipment.hasEquipped(ItemID.BRUMA_TORCH) && Rs2Inventory.hasItem(ItemID.TINDERBOX)) {
            Rs2Inventory.drop(ItemID.TINDERBOX);
        }
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

    private void dodgeOrbDamage() {
        for (GraphicsObject graphicsObject : Microbot.getClient().getGraphicsObjects())
        {
            if (!resetActions && graphicsObject.getId() == 502
                    && WorldPoint.fromLocalInstance(Microbot.getClient(),
                    graphicsObject.getLocation()).distanceTo(Rs2Player.getWorldLocation()) == 1) {
                System.out.println(WorldPoint.fromLocalInstance(Microbot.getClient(),
                        graphicsObject.getLocation()).distanceTo(Rs2Player.getWorldLocation()));
                //walk south
                Rs2Walker.walkFastCanvas(new WorldPoint(Rs2Player.getWorldLocation().getX(), Rs2Player.getWorldLocation().getY() - 1, Rs2Player.getWorldLocation().getPlane()));
                Rs2Player.waitForWalking(4000);
                resetActions = true;
            }
        }
    }

    private boolean handleBankLogic(MWintertodtConfig config) {
        if (!Rs2Player.isFullHealth() && Rs2Inventory.hasItem(config.food().getName(), false)) {
            eatAt(99);
            return true;
        }
        if (Rs2Inventory.hasItemAmount(config.food().getName(), config.foodAmount(), true)) {
            state = State.ENTER_ROOM;
            return true;
        }
        WorldPoint bankLocation = new WorldPoint(1640, 3944, 0);
        if (Rs2Player.getWorldLocation().distanceTo(bankLocation) > 6) {
            Rs2Walker.walkTo(bankLocation);
            Rs2Player.waitForWalking();
            if (config.openCrates()) {
                Rs2Inventory.interact(SUPPLY_CRATE, "open");
            }
        }
        Rs2Bank.useBank();
        if (!Rs2Bank.isOpen()) return true;
        Rs2Bank.depositAll();
        int foodCount = (int) Rs2Inventory.getInventoryFood().stream().count();
//        if (config.fixBrazier()) {
//            Rs2Bank.withdrawX(true, "hammer", 1);
//        }
        if (!Rs2Equipment.hasEquipped(ItemID.BRUMA_TORCH)) {
            Rs2Bank.withdrawX(true, "tinderbox", 1, true);
        }
        if (config.fletchRoots()) {
            Rs2Bank.withdrawX(true, "knife", 1, true);
        }
        if (config.axeInInventory()) {
            Rs2Bank.withdrawX(true, axe, 1);
        }
        if (!Rs2Bank.hasBankItem(config.food().getName(), config.foodAmount(), true)) {
            Microbot.showMessage("Insufficient food supply");
            Microbot.pauseAllScripts = true;
            return true;
        }
        Rs2Bank.withdrawX(config.food().getId(), config.foodAmount() - foodCount);
        return sleepUntilTrue(() -> Rs2Inventory.hasItemAmount(config.food().getName(), config.foodAmount(), false, true), 100, 5000);
    }
}
