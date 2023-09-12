package net.runelite.client.plugins.microbot.paydirt;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.walker.Walker;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class PayDirtScript extends Script {
    public static double version = 1.0;

    public boolean run() {
        Microbot.enableAutoRunOn = true;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (Microbot.isAnimating() || Microbot.getClient().getLocalPlayer().isInteracting()) return;

                if (Inventory.isFull()) {
                    if (Inventory.hasItem(ItemID.PAYDIRT)) {
                        WorldPoint point = new WorldPoint(3748, 5673, Microbot.getClient().getPlane());
                        Microbot.getWalker().walkFastCanvas(point);
                        sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(point) <= 3, 60000);

                        Rs2GameObject.interact(26674);
                        sleepUntil(() -> Microbot.getVarbitValue(Varbits.SACK_NUMBER) != 0, 60000);

                        Rs2GameObject.interact(26688);
                        sleepUntil(() -> Microbot.getVarbitValue(Varbits.SACK_NUMBER) == 0, 60000);

                        Rs2GameObject.interact(26707);
                        sleepUntil(Rs2Bank::isOpen);

                        Rs2Bank.depositAll();
                        sleepUntil(Inventory::isEmpty);

                        Rs2Bank.depositAll();

                        Rs2Bank.closeBank();
                    }
                    return;
                }

                WallObject closest = Rs2GameObject.getWallObjects()
                        .stream()
                        .filter(x -> x.getId() == 26661 ||x.getId() == 26662 || x.getId() == 26663 || x.getId() == 26664)
                        .sorted(Comparator.comparingInt(x -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(x.getWorldLocation())))
                        .filter(x -> Microbot.getWalker().canInteract(x.getWorldLocation()))
                        .findFirst()
                        .orElse(null);

                if (closest == null) return;

                Rs2GameObject.interact(closest);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}