package net.runelite.client.plugins.microbot.crafting.scripts;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.crafting.CraftingConfig;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;


public class GemsScript extends Script {

    public static double version = 1.0;

    public boolean run(CraftingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (!Microbot.hasLevel(config.gemType().getLevelRequired(), Skill.CRAFTING)) {
                    Microbot.showMessage("Crafting level to low to craft " + config.gemType().getName());
                    shutdown();
                    return;
                }
                final String uncutGemName = "uncut " + config.gemType().getName();
                if (!Rs2Inventory.hasItem("uncut " + config.gemType().getName()) || !Rs2Inventory.hasItem("chisel")) {
                    Rs2Bank.openBank();
                    if (Rs2Bank.isOpen()) {
                        Rs2Bank.depositAll("crushed gem");
                        Rs2Bank.depositAll(config.gemType().getName());
                        if(Rs2Bank.hasItem(uncutGemName)) {
                            Rs2Bank.withdrawItem(true, "chisel");
                            Rs2Bank.withdrawItemAll(true, uncutGemName);
                        } else{
                            Microbot.showMessage("Run out of Materials");
                            shutdown();
                        }
                        Rs2Bank.closeBank();
                        sleepUntil(() -> !Rs2Bank.isOpen());
                    }
                } else {
                    Rs2Inventory.use("chisel");
                    Rs2Inventory.use(uncutGemName);
                    sleep(600);
                    Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                    sleep(4000);
                    sleepUntil(() -> !Microbot.isGainingExp || !Rs2Inventory.hasItem(uncutGemName), 30000);
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
}
