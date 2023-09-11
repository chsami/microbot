package net.runelite.client.plugins.microbot.crafting.scripts;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.crafting.CraftingConfig;
import net.runelite.client.plugins.microbot.crafting.enums.Glass;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

class ProgressiveGlassblowingModel {
    @Getter
    @Setter
    private Glass itemToCraft;
}

public class GlassblowingScript extends Script {

    public static double version = 1.0;
    ProgressiveGlassblowingModel model = new ProgressiveGlassblowingModel();

    String moltenGlass = "molten glass";
    String glassblowingPipe = "glassblowing pipe";
    Glass itemToCraft;

    public void run(CraftingConfig config) {

        if (config.glassType() == Glass.PROGRESSIVE)
            calculateItemToCraft();

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (config.Afk() && Random.random(1, 100) == 2)
                sleep(1000, 60000);
            try {

                if (config.glassType() == Glass.PROGRESSIVE) {
                    itemToCraft = model.getItemToCraft();
                } else {
                    itemToCraft = config.glassType();
                }

                if (Inventory.hasItem(moltenGlass)
                        && Inventory.hasItem(glassblowingPipe)) {
                    craft(config);
                }
                if (!Inventory.hasItem(moltenGlass)
                        || !Inventory.hasItem(glassblowingPipe)) {
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
        sleepUntilOnClientThread(() -> !Inventory.hasItem(itemToCraft.getItemName()));

        Rs2Bank.withdrawItem(true, glassblowingPipe);
        sleepUntilOnClientThread(() -> Inventory.hasItem(glassblowingPipe));

        verifyItemInBank(moltenGlass);

        Rs2Bank.withdrawItemAll(true, moltenGlass);
        sleepUntilOnClientThread(() -> Inventory.hasItem(moltenGlass));

        sleep(600, 3000);
        Rs2Bank.closeBank();
    }

    private void verifyItemInBank(String item) {
        if (Rs2Bank.isOpen() && !Rs2Bank.hasItem(item)) {
            Rs2Bank.closeBank();
            Microbot.status = "[Shutting down] - Reason: " + item + " not found in the bank.";
            Microbot.getNotifier().notify(Microbot.status);
            shutdown();
            return;
        }
    }

    private void craft(CraftingConfig config) {
        Inventory.useItemOnItem(glassblowingPipe, moltenGlass);

        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) != null);

        keyPress(itemToCraft.getMenuEntry());

        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) == null);

        sleepUntilOnClientThread(() -> !Inventory.hasItem(moltenGlass), 60000);
    }

    public ProgressiveGlassblowingModel calculateItemToCraft() {
        int craftinglvl = Microbot.getClient().getRealSkillLevel(Skill.CRAFTING);
        if (craftinglvl < 4) {
            model.setItemToCraft(Glass.BEER_GLASS);
        } else if (craftinglvl < 12) {
            model.setItemToCraft(Glass.CANDLE_LANTERN);
        } else if (craftinglvl < 33) {
            model.setItemToCraft(Glass.OIL_LAMP);
        } else if (craftinglvl < 42) {
            model.setItemToCraft(Glass.VIAL);
        } else if (craftinglvl < 46) {
            model.setItemToCraft(Glass.FISHBOWL);
        } else if (craftinglvl < 49) {
            model.setItemToCraft(Glass.UNPOWERED_ORB);
        } else if (craftinglvl < 87) {
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
