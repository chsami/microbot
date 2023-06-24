package net.runelite.client.plugins.microbot.firemaking;

import net.runelite.api.ObjectID;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.firemaking.enums.FiremakingStatus;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.mta.telekinetic.TelekineticRoom;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class FiremakingScript extends Script {

    public static double version = 1.0;

    public static String logs = "Willow logs";

    public FiremakingStatus firemakingStatus = net.runelite.client.plugins.microbot.firemaking.enums.FiremakingStatus.FETCH_LOGS;

    WorldPoint[] startingPositions = new WorldPoint[] {new WorldPoint(3205, 3429, 0),
            new WorldPoint(3205, 3428, 0), new WorldPoint(3205, 3430, 0)};

    public boolean run(FiremakingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

                boolean hasTinderbox = Inventory.hasItem("tinderbox");

                fetchLogsAndTinderbox(hasTinderbox, logs);

                findEmptyFiremakingSpot();

                burnLogs();

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean fetchLogsAndTinderbox(boolean hasTinderbox, String logs) {
        if (firemakingStatus == FiremakingStatus.FETCH_LOGS) {
            if (Inventory.hasItem(logs)) {
                firemakingStatus = FiremakingStatus.FIND_EMPTY_SPOT;
                return false;
            }
            if (!Rs2Bank.isBankOpen()) {
                Rs2Bank.useBank();
                if (!hasTinderbox) {
                    Rs2Bank.withdrawItem("tinderbox");
                }
                Rs2Bank.withdrawItemAll(true, logs);
            }
        }
        return true;
    }

    private boolean findEmptyFiremakingSpot() {
        if (firemakingStatus == FiremakingStatus.FIND_EMPTY_SPOT) {

            Microbot.getWalker().walkFastMinimap(startingPositions[Random.random(0, startingPositions.length - 1)]);

            boolean isCloseToFiremakingSpot = Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(startingPositions[0]) < 6;

            if (isCloseToFiremakingSpot == false)
                return false;

            WorldPoint currentFiremakingSpot = null;
            for (WorldPoint firemakingSpot: startingPositions) {
                if (Rs2GameObject.findObject(ObjectID.FIRE_26185, firemakingSpot) == null) {
                    currentFiremakingSpot = firemakingSpot;
                    break;
                }
            }

            if (currentFiremakingSpot != null) {
                Microbot.getWalker().walkFastCanvas(currentFiremakingSpot);
            }

            if (Microbot.getClient().getLocalPlayer().getWorldLocation().equals(currentFiremakingSpot)) {
                firemakingStatus = FiremakingStatus.FIREMAKING;
            }
        }
        return true;
    }

    private boolean burnLogs() {
        if (firemakingStatus == FiremakingStatus.FIREMAKING) {
            if (!Inventory.hasItem(logs)) {
                firemakingStatus = FiremakingStatus.FETCH_LOGS;
                return false;
            }

            if (Rs2GameObject.findObject(ObjectID.FIRE_26185, Microbot.getClient().getLocalPlayer().getWorldLocation()) != null) {
                firemakingStatus = FiremakingStatus.FIND_EMPTY_SPOT;
                return false;
            }


            while (firemakingStatus == FiremakingStatus.FIREMAKING) {
                if (!Inventory.hasItem(logs))
                    break;

                if (Rs2GameObject.findObject(ObjectID.FIRE_26185, Microbot.getClient().getLocalPlayer().getWorldLocation()) != null) {
                    firemakingStatus = FiremakingStatus.FIND_EMPTY_SPOT;
                    return false;
                }

                Inventory.useItemUnsafe("tinderbox");
                Inventory.useItemUnsafe(logs);
                sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getPoseAnimation() == 1205, 30000);
            }
        }
        return true;
    }
}
