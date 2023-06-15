package net.runelite.client.plugins.microbot.example;

import net.runelite.api.ObjectID;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.World;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.example.enums.FiremakingStatus;
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

    FiremakingStatus firemakingStatus = FiremakingStatus.FETCH_LOGS;

    WorldPoint[] startingPositions = new WorldPoint[] {new WorldPoint(3205, 3429, 0), new WorldPoint(3205, 3428, 0), new WorldPoint(3205, 3430, 0)};

    public boolean run(FiremakingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

                // fetch logs
                // Walk to firemaking spot
                // Check if tile is available for firemaking
                // Use Tinderbox on log (repeat until inventory is empty)
                // Walk to bank

                boolean hasTinderbox = Inventory.hasItem("tinderbox");
                String logs = "Oak logs";
                boolean hasLogs = Inventory.hasItem(logs);

                if (firemakingStatus == FiremakingStatus.FETCH_LOGS) {
                    if (hasLogs)
                    {
                        firemakingStatus = FiremakingStatus.FIND_EMPTY_SPOT;
                        return;
                    }

                    if (!Rs2Bank.isBankOpen()) {
                        Rs2Bank.useBank();
                        if (!hasTinderbox) {
                            Rs2Bank.withdrawItem("tinderbox");
                        }
                        Rs2Bank.withdrawItemAll(true, logs);
                    }
                }

                if (firemakingStatus == FiremakingStatus.FIND_EMPTY_SPOT) {
                    if (!Inventory.hasItem(logs) || !Inventory.hasItem("tinderbox")) {
                        firemakingStatus = FiremakingStatus.FETCH_LOGS;
                        return;
                    }

                    Microbot.getWalker().walkFastMinimap(startingPositions[Random.random(0, startingPositions.length - 1)]);

                    boolean isCloseToFiremakingSpot = Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(startingPositions[0]) < 6;

                    if (isCloseToFiremakingSpot == false)
                        return;

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

                if (firemakingStatus == FiremakingStatus.FIREMAKING) {
                    if (Rs2GameObject.findObject(ObjectID.FIRE_26185, Microbot.getClient().getLocalPlayer().getWorldLocation()) != null)
                    {
                        firemakingStatus = FiremakingStatus.FIND_EMPTY_SPOT;
                        return;
                    }
                    final WorldPoint start = Microbot.getClient().getLocalPlayer().getWorldLocation();
                    Inventory.useItem("tinderbox");
                    Inventory.useItem(logs);
                    sleep(1800);
                    sleepUntilOnClientThread(() -> !Microbot.isAnimating() ||  !start.equals( Microbot.getClient().getLocalPlayer().getWorldLocation()) , 30000);
                    boolean didNotMove = start.equals( Microbot.getClient().getLocalPlayer().getWorldLocation());
                    if (didNotMove) {
                        firemakingStatus = FiremakingStatus.FIND_EMPTY_SPOT;
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
}
