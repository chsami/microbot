package net.runelite.client.plugins.microbot.fletching;


import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingItem;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMaterial;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMode;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

class ProgressiveFletchingModel {
    @Getter
    @Setter
    private FletchingItem fletchingItem;
    @Getter
    @Setter
    private FletchingMaterial fletchingMaterial;
}

public class FletchingScript extends Script {

    public static double version = 1.0;
    ProgressiveFletchingModel model = new ProgressiveFletchingModel();

    String primaryItemToFletch = "";
    String secondaryItemToFletch = "";

    public void run(FletchingConfig config) {

        if (config.fletchingMode() == FletchingMode.PROGRESSIVE)
            calculateItemToFletch();

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!configChecks(config)) return;
            if (config.Afk() && Random.random(1, 100) == 2)
                sleep(1000, 60000);
            try {
                boolean hasRequirementsToFletch = false;
                boolean hasRequirementsToBank = false;
                primaryItemToFletch = config.fletchingMode().getItemName();

                if (config.fletchingMode() == FletchingMode.PROGRESSIVE) {
                    secondaryItemToFletch = (model.getFletchingMaterial().getName() + " logs").trim();
                    hasRequirementsToFletch = Inventory.hasItem(primaryItemToFletch)
                            && Inventory.hasItemAmount(secondaryItemToFletch, model.getFletchingItem().getAmountRequired());
                    hasRequirementsToBank = !Inventory.hasItem(primaryItemToFletch)
                            || !Inventory.hasItemAmount(secondaryItemToFletch, model.getFletchingItem().getAmountRequired());
                } else {
                    secondaryItemToFletch = config.fletchingMode() == FletchingMode.STRUNG
                            ? config.fletchingMaterial().getName() + " " + config.fletchingItem().getContainsInventoryName() + " (u)"
                            : (config.fletchingMaterial().getName() + " logs").trim();
                    hasRequirementsToFletch = Inventory.hasItem(primaryItemToFletch)
                            && Inventory.hasItemAmount(secondaryItemToFletch, config.fletchingItem().getAmountRequired());
                    hasRequirementsToBank = !Inventory.hasItem(primaryItemToFletch)
                            || !Inventory.hasItemAmount(secondaryItemToFletch, config.fletchingItem().getAmountRequired());
                }



                if (hasRequirementsToFletch) {
                    fletch(config);
                }
                if (hasRequirementsToBank) {
                    bankItems(config);
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    private void bankItems(FletchingConfig config) {
        Rs2Bank.openBank();
        if (config.fletchingMode() == FletchingMode.STRUNG) {
            Rs2Bank.depositXContains(config.fletchingItem().getContainsInventoryName(), config.fletchingMode().getAmount());
        } else if (config.fletchingMode() == FletchingMode.PROGRESSIVE) {
            Rs2Bank.depositAllContains(model.getFletchingItem().getContainsInventoryName());
            calculateItemToFletch();
            secondaryItemToFletch = (model.getFletchingMaterial().getName() + " logs").trim();
        } else {
            Rs2Bank.depositAllContains(config.fletchingItem().getContainsInventoryName());
        }
        sleepUntilOnClientThread(() -> !Inventory.hasItemContains(config.fletchingItem().getContainsInventoryName()));


        Rs2Bank.withdrawItemX(true, primaryItemToFletch, config.fletchingMode().getAmount());
        if (Rs2Bank.isOpen() && !Rs2Bank.hasItem(secondaryItemToFletch)) {
            Rs2Bank.closeBank();
            Microbot.status = "[Shutting down] - Reason: " + secondaryItemToFletch + " not found in the bank.";
            Microbot.getNotifier().notify(Microbot.status);
            // logout();
            shutdown();
            return;
        }
        if (config.fletchingMode() == FletchingMode.STRUNG)
            Rs2Bank.withdrawItemX(true, secondaryItemToFletch, config.fletchingMode().getAmount());
        else
            Rs2Bank.withdrawItemsAll(secondaryItemToFletch);

        final String finalSecondaryItemToFletch = secondaryItemToFletch;

        sleepUntilOnClientThread(() -> Inventory.hasItem(finalSecondaryItemToFletch));
        sleep(600, 3000);
        Rs2Bank.closeBank();
    }

    private void fletch(FletchingConfig config) {
        Inventory.useItemOnItem(primaryItemToFletch, secondaryItemToFletch);
        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) != null);
        if (config.fletchingMode() == FletchingMode.PROGRESSIVE) {
            keyPress(model.getFletchingItem().getOption(model.getFletchingMaterial(), config.fletchingMode()));
        } else {
            keyPress(config.fletchingItem().getOption(config.fletchingMaterial(), config.fletchingMode()));
        }
        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) == null);
        if (config.fletchingMode() == FletchingMode.PROGRESSIVE) {
            sleepUntilOnClientThread(() -> !Inventory.hasItemAmount(secondaryItemToFletch, model.getFletchingItem().getAmountRequired()), 60000);
        } else {
            sleepUntilOnClientThread(() -> !Inventory.hasItemAmount(secondaryItemToFletch, config.fletchingItem().getAmountRequired()), 60000);
        }
    }

    private boolean configChecks(FletchingConfig config) {
        if (config.fletchingMaterial() == FletchingMaterial.REDWOOD && config.fletchingItem() != FletchingItem.SHIELD) {
            Microbot.getNotifier().notify("[Wrong Configuration] You can only make shields with redwood logs.");
            shutdown();
            return false;
        }
        return true;
    }

    public ProgressiveFletchingModel calculateItemToFletch() {
        int fletchingLevel = Microbot.getClient().getRealSkillLevel(Skill.FLETCHING);
        if (fletchingLevel < 5) {
            model.setFletchingItem(FletchingItem.ARROW_SHAFT);
            model.setFletchingMaterial(FletchingMaterial.LOG);
        } else if (fletchingLevel < 10) {
            model.setFletchingItem(FletchingItem.SHORT);
            model.setFletchingMaterial(FletchingMaterial.LOG);
        } else if (fletchingLevel < 20) {
            model.setFletchingItem(FletchingItem.LONG);
            model.setFletchingMaterial(FletchingMaterial.LOG);
        } else if (fletchingLevel < 25) {
            model.setFletchingItem(FletchingItem.SHORT);
            model.setFletchingMaterial(FletchingMaterial.OAK);
        } else if (fletchingLevel < 35) {
            model.setFletchingItem(FletchingItem.LONG);
            model.setFletchingMaterial(FletchingMaterial.OAK);
        } else if (fletchingLevel < 40) {
            model.setFletchingItem(FletchingItem.SHORT);
            model.setFletchingMaterial(FletchingMaterial.WILLOW);
        } else if (fletchingLevel < 50) {
            model.setFletchingItem(FletchingItem.LONG);
            model.setFletchingMaterial(FletchingMaterial.WILLOW);
        } else if (fletchingLevel < 55) {
            model.setFletchingItem(FletchingItem.SHORT);
            model.setFletchingMaterial(FletchingMaterial.MAPLE);
        } else if (fletchingLevel < 65) {
            model.setFletchingItem(FletchingItem.LONG);
            model.setFletchingMaterial(FletchingMaterial.MAPLE);
        } else if (fletchingLevel < 70) {
            model.setFletchingItem(FletchingItem.SHORT);
            model.setFletchingMaterial(FletchingMaterial.YEW);
        } else if (fletchingLevel < 80) {
            model.setFletchingItem(FletchingItem.LONG);
            model.setFletchingMaterial(FletchingMaterial.YEW);
        } else if (fletchingLevel < 85) {
            model.setFletchingItem(FletchingItem.SHORT);
            model.setFletchingMaterial(FletchingMaterial.YEW);
        } else if (fletchingLevel < 99) {
            model.setFletchingItem(FletchingItem.LONG);
            model.setFletchingMaterial(FletchingMaterial.MAGIC);
        }
        return model;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}

