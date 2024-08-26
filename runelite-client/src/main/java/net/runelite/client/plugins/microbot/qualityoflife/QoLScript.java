package net.runelite.client.plugins.microbot.qualityoflife;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
public class QoLScript extends Script {

    public boolean bankOpen = false;

    public boolean run(QoLConfig config) {
        Microbot.enableAutoRunOn = false;
        try {
            Rs2NpcManager.loadJson();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) {
                    return;
                }

                if (QoLPlugin.executeActions) {
                    sleepUntil(Rs2Bank::isOpen, 10000);
                    if (!Rs2Bank.isOpen()) {
                        Microbot.log("Bank did not open");
                        QoLPlugin.executeActions = false;
                        return;
                    }
                    for (NewMenuEntry menuEntry : QoLPlugin.menuEntries) {
                        Microbot.log("Executing action: " + menuEntry.getOption() + " " + menuEntry.getTarget());
                        if (menuEntry.getOption().contains("Withdraw")) {
                            int itemTab = Rs2Bank.getItemTabForBankItem(menuEntry.getParam0());
                            if (!Rs2Bank.isTabOpen(itemTab)) {
                                Microbot.log("Switching to tab: " + itemTab);
                                Rs2Bank.openTab(itemTab);
                                sleepUntil(() -> Rs2Bank.isTabOpen(itemTab), 5000);
                                Rs2Bank.scrollBankToSlot(menuEntry.getParam0());
                                Rs2Random.wait(200, 500);
                                menuEntry.setWidget(Rs2Bank.getItemWidget(menuEntry.getParam0()));
                            }
                            Rs2Bank.scrollBankToSlot(menuEntry.getParam0());
                            Rs2Random.wait(200, 500);
                        }
                        Microbot.doInvoke(menuEntry, Objects.requireNonNull(menuEntry.getWidget()).getBounds());
                        Rs2Random.wait(200, 500);
                    }
                    QoLPlugin.executeActions = false;
                }


            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }


    @Override
    public void shutdown() {

        super.shutdown();
    }
}
