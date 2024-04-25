package net.runelite.client.plugins.microbot.bankjs.development;

import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.bankjs.BanksBankStander.CurrentStatus;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
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

    public boolean run(BanksBankPinConfig config) {
        this.config = config; // Initialize the config object before accessing its parameters

        String pin = config.bankPin();

        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {

                handleBankPin(config.bankPin());

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    public static boolean handleBankPin(String pin) {
        Widget bankPinWidget = Rs2Widget.getWidget(ComponentID.BANK_PIN_CONTAINER);

        boolean isBankPinVisible = Microbot.getClientThread().runOnClientThread(() -> bankPinWidget != null && !bankPinWidget.isHidden());

        if (isBankPinVisible) {
            VirtualKeyboard.typeString(pin);
            return true;
        }
        return false;
    }
}