package net.runelite.client.plugins.microbot.bankjs.development;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.bankjs.BanksBankStander.CurrentStatus;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BanksBankPinScript extends Script {
    @Inject
    private BanksBankPinConfig config;
    @Inject
    private OverlayManager overlayManager;

    public static double version = 1.0;
    private CurrentStatus currentStatus = CurrentStatus.FETCH_SUPPLIES;


    public boolean run(BanksBankPinConfig config) {
        this.config = config; // Initialize the config object before accessing its parameters

        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                //start

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

}