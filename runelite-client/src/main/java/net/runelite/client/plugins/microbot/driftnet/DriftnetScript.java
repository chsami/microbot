package net.runelite.client.plugins.microbot.driftnet;

import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.mta.telekinetic.TelekineticRoom;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DriftnetScript extends Script {

    public static double version = 1.0;

    List<Integer> alreadyInteractedIndex = new ArrayList<Integer>();

    public boolean run(DriftnetConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

                net.runelite.api.NPC fish = Rs2Npc.getNpc(NpcID.FISH_SHOAL, alreadyInteractedIndex);

                if (fish != null) {
                    Rs2Npc.interact(fish, "chase");
                    alreadyInteractedIndex.add(fish.getIndex());
                    sleep(1000, 4000);
                }

                if (alreadyInteractedIndex.size() > 6) {
                    alreadyInteractedIndex = new ArrayList<>();
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);

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
}
