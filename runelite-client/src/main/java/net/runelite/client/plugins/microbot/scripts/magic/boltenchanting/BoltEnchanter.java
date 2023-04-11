package net.runelite.client.plugins.microbot.scripts.magic.boltenchanting;

import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Scripts;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class BoltEnchanter extends Scripts {

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                super.run();
                if (Microbot.getMouse().mousePositions.isEmpty()) {
                    final Point point = new Point((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
                    Microbot.getMouse().click(point);
                } else {
                    Microbot.getMouse().click(Microbot.getMouse().mousePositions.stream().findFirst().get());
                }
                VirtualKeyboard.keyHold(KeyEvent.VK_SPACE);
                sleep(400, 10000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 5000, 100, TimeUnit.MILLISECONDS);
        return true;
    }
}
