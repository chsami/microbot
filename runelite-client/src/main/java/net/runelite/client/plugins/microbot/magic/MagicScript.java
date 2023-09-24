package net.runelite.client.plugins.microbot.magic;

import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.tabs.Tab;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class MagicScript extends Script {


    public boolean run(MagicConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (config.boltEnchanting()) {
                    boltEnchanting();
                } else if (config.highAlchemy()) {
                    highAlch();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void boltEnchanting() {
        if (Microbot.getMouse().mousePositions.isEmpty()) {
            final Point point = new Point((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
            Microbot.getMouse().click(point);
        } else {
            Microbot.getMouse().click(Microbot.getMouse().mousePositions.stream().findFirst().get());
        }
        VirtualKeyboard.keyHold(KeyEvent.VK_SPACE);
        sleep(400, 10000);
    }

    private void highAlch() {
        Widget highAlch = Microbot.getClient().getWidget(14286888);
        if (highAlch == null) return;
        Point point = new Point((int) highAlch.getBounds().getCenterX(), (int) highAlch.getBounds().getCenterY());
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.MAGIC), 5000);
        sleep(300, 600);
        Microbot.getMouse().click(point);
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.INVENTORY), 5000);
        sleep(300, 600);
        Microbot.getMouse().click(point);
    }
}



//Might need this one day :D

/*
    private void telekneticGrab() {
        if (Rs2Widget.hasWidget("select an option")) {
            VirtualKeyboard.typeString("1");
            return;
        }

        if (Rs2Widget.hasWidget("click here to continue")) {
            VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
            return;
        }

        if (Rs2Npc.getNpc(6777) == null) {
            Microbot.getMouse().click(50, 50);
            boolean result = Rs2Npc.interact(6779, "Talk-to");
//                    if (!result) {
//                        Microbot.getWalker()
//                                .walkFastRegion(
//                                        Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionX() - 10,
//                                        Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionY());
//                        sleep(3000);
//                        Microbot.getWalker()
//                                .walkFastRegion(
//                                        Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionX(),
//                                        Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionY() -10);
//                        sleep(3000);
//                    }
            return;
                    }

                    if (Microbot.isMoving()) return;

                    WorldPoint w = TelekineticRoom.optimal();

                    if (!Rs2Camera.isTileOnScreen(LocalPoint.fromWorld(Microbot.getClient(), w))) {
                    Microbot.getWalker().walkFastRegion(w.getX(), w.getY());
                    }

                    if (!Microbot.getClient().getLocalPlayer().getWorldLocation().equals(w)) {
                    Microbot.getWalker().walkRegionCanvas(w.getRegionX(), w.getRegionY());
                    } else {
                    Tab.switchToMagicTab();
                    sleep(300, 600);
                    Rs2Widget.clickWidget("grab");
                    sleep(300, 600);
                    Rs2Npc.interact(6777, "cast");
                    sleepUntil(() -> Rs2Npc.getNpc(6777) == null);
                    sleepUntil(() -> Rs2Npc.getNpc(6777) != null);
                    }
                    }

 */
