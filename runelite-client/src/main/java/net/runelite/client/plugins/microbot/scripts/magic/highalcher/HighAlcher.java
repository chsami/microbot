package net.runelite.client.plugins.microbot.scripts.magic.highalcher;

import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Scripts;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.tabs.Tab;

import java.util.concurrent.TimeUnit;

public class HighAlcher extends Scripts {

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                super.run();
                Widget highAlch = Microbot.getClient().getWidget(14286888);
                if (highAlch == null) return;
                Point point = new Point((int) highAlch.getBounds().getCenterX(), (int) highAlch.getBounds().getCenterY());
                highAlch(point);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 5000, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    private void highAlch(Point point) {
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.MAGIC), 5000);
        sleep(300, 600);
        Microbot.getMouse().click(point);
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.INVENTORY), 5000);
        sleep(300, 600);
        Microbot.getMouse().click(point);
    }
}
