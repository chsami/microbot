package net.runelite.client.plugins.microbot.crafting.scripts;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.crafting.CraftingConfig;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;


public class GemsScript extends Script {

    public static double version = 1.0;

    public boolean run(CraftingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (!Microbot.hasLevel(config.gemType().getLevelRequired(), Skill.CRAFTING)) {
                    Microbot.showMessage("Crafting level to low to craft " + config.gemType().getName());
                    shutdown();
                    return;
                }
                final String uncutGemName = "uncut " + config.gemType().getName();
                if (!Inventory.hasItem("uncut " + config.gemType().getName()) || !Inventory.hasItem("chisel")) {
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
                    Inventory.useItem("chisel");
                    Inventory.useItem(uncutGemName);
                    sleep(600);
                    VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                    sleep(4000);
                    sleepUntil(() -> !Microbot.isGainingExp || !Inventory.hasItem(uncutGemName), 30000);
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
