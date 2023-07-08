package net.runelite.client.plugins.microbot.example;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemID;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.mta.telekinetic.TelekineticRoom;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class ExampleScript extends Script {

    public static double version = 1.0;

    public boolean run(ExampleConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;

            try {
                Microbot.getWalker().walkTo(new WorldPoint(3116, 9851, 0), true);
              //  WorldArea area = Microbot.getClient().getLocalPlayer().getWorldArea();

                //System.out.println(area.canTravelInDirection(Microbot.getClient(), 0, 2));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

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
                  /*  if (!result) {
                        Microbot.getWalker()
                                .walkFastRegion(
                                        Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionX() - 10,
                                        Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionY());
                        sleep(3000);
                        Microbot.getWalker()
                                .walkFastRegion(
                                        Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionX(),
                                        Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionY() -10);
                        sleep(3000);
                    }*/
            return;
        }

        if (Microbot.isWalking()) return;

        WorldPoint w = TelekineticRoom.optimal();

        if (!Camera.isTileOnScreen(LocalPoint.fromWorld(Microbot.getClient(), w))) {
            Microbot.getWalker().walkFastRegion(w.getX(), w.getY());
        }

        if (!Microbot.getClient().getLocalPlayer().getWorldLocation().equals(w)) {
            Microbot.getWalker().walkFastRegionCanvas(w.getRegionX(), w.getRegionY());
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


    /**
     *
     * try {
     *                 net.runelite.api.NPC npc = Npc.getNpc("Jaltok-jad");
     *                 Field field = npc.getClass().getSuperclass().getDeclaredField("cf");
     *                 field.setAccessible(true);
     *                 int value = (int) field.get(npc);
     *                 int realAnimation = value * -2121799935;
     *                 if (realAnimation == 7592) { //magic
     *                     Prayer.turnOnMagePrayer();
     *                 } else if (realAnimation == 7593) { //range
     *                     Prayer.turnOnRangePrayer();
     *                 }
     *             } catch (IllegalAccessException e) {
     *             } catch (NoSuchFieldException e) {
     *             }
     *
     */
}
