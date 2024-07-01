package net.runelite.client.plugins.microbot.pottery;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.pottery.enums.HumidifyAction;
import net.runelite.client.plugins.microbot.pottery.enums.HumidifyItems;
import net.runelite.client.plugins.microbot.pottery.enums.PotteryItems;
import net.runelite.client.plugins.microbot.pottery.enums.State;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.settings.Rs2Settings;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

public class PotteryScript extends Script {
    public static double version = 1.0;
    public State state;
    public long lastAnimationTime = 0;
    boolean init = true;

    public boolean run(PotteryConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;

                if (init) {
                    Rs2Camera.setAngle(0);
                    Rs2Camera.setPitch(0.81f);
                    if (!Rs2Settings.isHideRoofsEnabled()) {
                        Rs2Settings.hideRoofs();
                        sleepUntilTrue(Rs2Settings::isHideRoofsEnabled, 500, 15000);
                    }
                    if (Rs2Settings.isLevelUpNotificationsEnabled()) {
                        Rs2Settings.disableLevelUpNotifications();
                        sleepUntilTrue(() -> !Rs2Settings.isLevelUpNotificationsEnabled(), 500, 15000);
                    }

                    if (!config.location().hasRequirements()) {
                        Microbot.showMessage("You do not meet the requirements for this location");
                        shutdown();
                        return;
                    }
                    
                    getPotteryState(config);
                }

                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || Microbot.pauseAllScripts) return;

                long startTime = System.currentTimeMillis();

                switch (state) {
                    case HUMIDIFY:
                        if (getClayItemCount() >= 1 && getHumidifyItemCount(config) >= getClayItemCount()) {
                            Rs2Inventory.combine("clay", config.humidifyItem().getFilledItemName());
                            sleep(Random.random(600, 800));
                            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                            sleepUntilTrue(() -> getClayItemCount() == 0, 500, 150000);
                            return;
                        }

                        state = State.BANK;
                        break;
                    case SPINNING:
                        if (!config.potteryItem().hasRequirements()) {
                            Microbot.showMessage("You do not meet the requirements for this item");
                            shutdown();
                            return;
                        }

                        if (!isNearPotteryWheel(config, 4)) {
                            Rs2Walker.walkTo(config.location().getWheelWorldPoint(), 4);
                            return;
                        }

                        Rs2Walker.walkFastCanvas(config.location().getWheelWorldPoint());
                        sleepUntil(() -> isNearPotteryWheel(config, 0));

                        Rs2Inventory.useItemOnObject(ItemID.SOFT_CLAY, config.location().getWheelObjectID());
                        sleepUntil(() -> !Rs2Player.isMoving() && Rs2Widget.findWidget("how many do you wish to make?", null, false) != null);

                        Rs2Widget.clickWidget(config.potteryItem().getUnfiredWheelWidgetID());

                        sleepUntilTrue(() -> getSoftClayItemCount() == 0 && hasPlayerStoppedAnimating(), 500, 150000);
                        state = State.COOKING;
                        break;
                    case COOKING:
                        if (!config.potteryItem().hasRequirements()) {
                            Microbot.showMessage("You do not meet the requirements for this item");
                            shutdown();
                            return;
                        }

                        if (!isNearPotteryOven(config, 4)) {
                            Rs2Walker.walkTo(config.location().getOvenWorldPoint(), 4);
                            return;
                        }

                        Rs2Walker.walkFastCanvas(config.location().getOvenWorldPoint());
                        sleepUntil(() -> isNearPotteryOven(config, 0));

                        Rs2Inventory.useItemOnObject(config.potteryItem().getUnfiredItemID(), config.location().getOvenObjectID());
                        sleepUntil(() -> !Rs2Player.isMoving() && Rs2Widget.findWidget("What would you like to fire in the oven?", null, false) != null);

                        Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                        sleepUntilTrue(() -> getUnfiredPotteryItemCount(config) == 0 && hasPlayerStoppedAnimating(), 500, 150000);
                        state = State.BANK;
                        break;
                    case REFILLING:
                        if (config.humidifyItem().equals(HumidifyItems.BUCKET)) {
                            Rs2Walker.walkTo(config.location().getWellWaterPoint(), 4);
                            if (!isNearWellWaterPoint(config, 4)) return;

                            Rs2Inventory.useItemOnObject(config.humidifyItem().getItemID(), config.location().getWellWaterPointObjectID());
                        } else {
                            Rs2Walker.walkTo(config.location().getWaterPoint(), 4);
                            if (!isNearWaterPoint(config, 4)) return;

                            Rs2Inventory.useItemOnObject(config.humidifyItem().getItemID(), config.location().getWaterPointObjectID());
                        }

                        sleepUntilTrue(() -> getEmptyHumidifyItemCount(config) == 0 & hasPlayerStoppedAnimating(), 500, 150000);
                        state = State.BANK;
                        break;
                    case BANK:
                        boolean isBankOpen = Rs2Bank.walkToBankAndUseBank();
                        if (!isBankOpen || !Rs2Bank.isOpen()) return;

                        Rs2Bank.depositAll();

                        if (getClayItemCount() == 0 && getSoftClayItemCount() == 0) {
                            Rs2Bank.closeBank();
                            Microbot.showMessage("No clay and soft clay found in bank");
                            shutdown();
                            break;
                        }

                        if (getHumidifyAction(config).equals(HumidifyAction.ITEM)) {
                            if (getEmptyHumidifyItemCount(config) > 0 && config.forceRefill()) {
                                Rs2Bank.withdrawAll(config.humidifyItem().getItemName(), true);
                                Rs2Bank.closeBank();
                                state = State.REFILLING;
                                break;
                            }

                            if (getClayItemCount() >= 1) {
                                if (getHumidifyItemCount(config) < getClayItemCount()) {
                                    if (getEmptyHumidifyItemCount(config) == 0) {
                                        Rs2Bank.closeBank();
                                        Microbot.showMessage("Not enough " + config.humidifyItem().getItemName() + " found in bank");
                                        shutdown();
                                        return;
                                    }
                                    Rs2Bank.withdrawAll(config.humidifyItem().getItemName(), true);
                                    Rs2Bank.closeBank();
                                    state = State.REFILLING;
                                    break;
                                }

                                Rs2Bank.withdrawX(config.humidifyItem().getFilledItemName(), 14);
                                Rs2Bank.withdrawX("clay", 14);
                                Rs2Bank.closeBank();
                                sleep(Random.random(600, 800));
                                state = State.HUMIDIFY;
                                break;
                            }
                        }

                        if (config.potteryItem().equals(PotteryItems.CUP)) {
                            Rs2Bank.withdrawX("soft clay", 7);
                        } else {
                            Rs2Bank.withdrawAll("soft clay");
                        }
                        Rs2Bank.closeBank();
                        sleep(Random.random(600, 800));
                        state = State.SPINNING;
                        break;
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

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

    private void getPotteryState(PotteryConfig config) {
        if (getEmptyHumidifyItemCount(config) > 0 && (getEmptyHumidifyItemCount(config) + getHumidifyItemCount(config) + Rs2Inventory.getEmptySlots() == 28)) {
            state = State.REFILLING;
            init = false;
            return;
        }

        if (getSoftClayItemCount() > 0 && getUnfiredPotteryItemCount(config) > 0) {
            state = State.SPINNING;
            init = false;
            return;
        }

        if (getUnfiredPotteryItemCount(config) > 0 && getUnfiredPotteryItemCount(config) > 0) {
            state = State.COOKING;
            init = false;
            return;
        }

        if (Rs2Bank.isNearBank(8)) {
            if (getClayItemCount() > 0 && getHumidifyItemCount(config) > 0) {
                state = State.HUMIDIFY;
                init = false;
                return;
            }
            state = State.BANK;
            init = false;
            return;
        }

        if (isNearWaterPoint(config, 8) || isNearWellWaterPoint(config, 8)) {
            if (getEmptyHumidifyItemCount(config) == 0) {
                state = State.REFILLING;
                init = false;
                return;
            }
            state = State.BANK;
            init = false;
            return;
        }

        if (isNearPotteryWheel(config, 8)) {
            if (getSoftClayItemCount() > 0) {
                state = State.SPINNING;
                init = false;
                return;
            }

            if (getUnfiredPotteryItemCount(config) > 0 && getSoftClayItemCount() < 0) {
                state = State.COOKING;
                init = false;
                return;
            }

            state = State.BANK;
            init = false;
            return;
        }

        if (isNearPotteryOven(config, 8)) {
            if (getUnfiredPotteryItemCount(config) == 0) {
                state = State.COOKING;
                init = false;
                return;
            }
            state = State.BANK;
            init = false;
            return;
        }

        state = State.BANK;
        init = false;
    }

    private boolean hasPlayerStoppedAnimating() {
        if (lastAnimationTime == 0 || (System.currentTimeMillis() - lastAnimationTime) < 5000)
            return false;
        lastAnimationTime = 0;
        return true;
    }

    private boolean isNearWaterPoint(PotteryConfig config, int distance) {
        return Rs2Player.getWorldLocation().distanceTo(config.location().getWaterPoint()) <= distance && !Rs2Player.isMoving();
    }

    private boolean isNearWellWaterPoint(PotteryConfig config, int distance) {
        return Rs2Player.getWorldLocation().distanceTo(config.location().getWellWaterPoint()) <= distance && !Rs2Player.isMoving();
    }

    private boolean isNearPotteryWheel(PotteryConfig config, int distance) {
        return Rs2Player.getWorldLocation().distanceTo(config.location().getWheelWorldPoint()) <= distance && !Rs2Player.isMoving();
    }

    private boolean isNearPotteryOven(PotteryConfig config, int distance) {
        return Rs2Player.getWorldLocation().distanceTo(config.location().getOvenWorldPoint()) <= distance && !Rs2Player.isMoving();
    }

    private int getEmptyHumidifyItemCount(PotteryConfig config) {
        if (Rs2Bank.isOpen()) {
            return Rs2Bank.count(config.humidifyItem().getItemName());
        }
        return Rs2Inventory.count(config.humidifyItem().getItemID());
    }

    private int getHumidifyItemCount(PotteryConfig config) {
        if (Rs2Bank.isOpen()) {
            return Rs2Bank.count(config.humidifyItem().getFilledItemName());
        }
        return Rs2Inventory.count(config.humidifyItem().getFilledItemID());
    }

    private int getUnfiredPotteryItemCount(PotteryConfig config) {
        if (Rs2Bank.isOpen()) {
            return Rs2Bank.count(config.potteryItem().getUnfiredItemName());
        }
        return Rs2Inventory.count(config.potteryItem().getUnfiredItemID());
    }

    private int getPotteryItemCount(PotteryConfig config) {
        if (Rs2Bank.isOpen()) {
            return Rs2Bank.count(config.potteryItem().getFiredItemName());
        }
        return Rs2Inventory.count(config.potteryItem().getFiredItemID());
    }

    private int getClayItemCount() {
        if (Rs2Bank.isOpen()) {
            return Rs2Bank.count("clay", true);
        }
        return Rs2Inventory.count(ItemID.CLAY);
    }

    private int getSoftClayItemCount() {
        if (Rs2Bank.isOpen()) {
            return Rs2Bank.count("soft clay", true);
        }
        return Rs2Inventory.count(ItemID.SOFT_CLAY);
    }

    private HumidifyAction getHumidifyAction(PotteryConfig config) {
        return config.humidifyAction();
    }
}
