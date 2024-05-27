package net.runelite.client.plugins.microbot.crafting.scripts;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.crafting.CraftingConfig;
import net.runelite.client.plugins.microbot.crafting.enums.Glass;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

class ProgressiveGlassblowingModel {
    @Getter
    @Setter
    private Glass itemToCraft;
}

public class GlassblowingScript extends Script {

    public static double version = 2.0;
    ProgressiveGlassblowingModel model = new ProgressiveGlassblowingModel();

    String moltenGlass = "molten glass";
    String glassblowingPipe = "glassblowing pipe";
    Glass itemToCraft;

    public void run(CraftingConfig config) {

        if (config.glassType() == Glass.PROGRESSIVE)
            calculateItemToCraft();

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (config.Afk() && Random.random(1, 100) == 2)
                    sleep(1000, 60000);
                if (config.glassType() == Glass.PROGRESSIVE) {
                    itemToCraft = model.getItemToCraft();
                } else {
                    itemToCraft = config.glassType();
                }

                if (Rs2Inventory.hasItem(moltenGlass)
                        && Rs2Inventory.hasItem(glassblowingPipe)) {
                    craft(config);
                }
                if (!Rs2Inventory.hasItem(moltenGlass)
                        || !Rs2Inventory.hasItem(glassblowingPipe)) {
                    bank(config);
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    private void bank(CraftingConfig config) {
        Rs2Bank.openBank();
        sleepUntilOnClientThread(() -> Rs2Bank.isOpen());

        Rs2Bank.depositAll(itemToCraft.getItemName());
        sleepUntilOnClientThread(() -> !Rs2Inventory.hasItem(itemToCraft.getItemName()));

        Rs2Bank.withdrawItem(true, glassblowingPipe);
        sleepUntilOnClientThread(() -> Rs2Inventory.hasItem(glassblowingPipe));

        verifyItemInBank(moltenGlass);

        Rs2Bank.withdrawItemAll(true, moltenGlass);
        sleepUntilOnClientThread(() -> Rs2Inventory.hasItem(moltenGlass));

        sleep(600, 3000);
        Rs2Bank.closeBank();
    }

    private void verifyItemInBank(String item) {
        if (Rs2Bank.isOpen() && !Rs2Bank.hasItem(item)) {
            Rs2Bank.closeBank();
            Microbot.status = "[Shutting down] - Reason: " + item + " not found in the bank.";
            Microbot.getNotifier().notify(Microbot.status);
            shutdown();
        }
    }

    private void craft(CraftingConfig config) {
        Rs2Inventory.combine(glassblowingPipe, moltenGlass);

        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) != null);

        keyPress(itemToCraft.getMenuEntry());

        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) == null);

        sleepUntilOnClientThread(() -> !Rs2Inventory.hasItem(moltenGlass), 60000);
    }

    public ProgressiveGlassblowingModel calculateItemToCraft() {
        int craftinglvl = Microbot.getClient().getRealSkillLevel(Skill.CRAFTING);
        if (craftinglvl < Glass.CANDLE_LANTERN.getLevelRequired()) {
            model.setItemToCraft(Glass.BEER_GLASS);
        } else if (craftinglvl < Glass.OIL_LAMP.getLevelRequired()) {
            model.setItemToCraft(Glass.CANDLE_LANTERN);
        } else if (craftinglvl < Glass.VIAL.getLevelRequired()) {
            model.setItemToCraft(Glass.OIL_LAMP);
        } else if (craftinglvl < Glass.FISHBOWL.getLevelRequired()) {
            model.setItemToCraft(Glass.VIAL);
        } else if (craftinglvl < Glass.UNPOWERED_ORB.getLevelRequired()) {
            model.setItemToCraft(Glass.FISHBOWL);
        } else if (craftinglvl < Glass.LANTERN_LENS.getLevelRequired()) {
            model.setItemToCraft(Glass.UNPOWERED_ORB);
        } else if (craftinglvl < Glass.LIGHT_ORB.getLevelRequired()) {
            model.setItemToCraft(Glass.LANTERN_LENS);
        } else if (craftinglvl < 99) {
            model.setItemToCraft(Glass.LIGHT_ORB);
        }
        return model;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
