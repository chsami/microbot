package net.runelite.client.plugins.microbot.hunter;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.example.ExampleConfig;
import net.runelite.client.plugins.microbot.hunter.data.HunterType;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.hunter.Rs2Hunter;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleepGaussian;

public class AutoHunterScript extends Script {

    public static List<WorldPoint> hunterArea = new ArrayList<>();

    public boolean run(ExampleConfig config) {
        Microbot.enableAutoRunOn = false;
        Optional<HunterType> hunterType = Rs2Hunter.determineHuntingType();
        hunterArea = Rs2Tile.getWalkableTilesAroundStartingLocation(
                Rs2Player.getWorldLocation(),
                3,
                3,
                x -> Rs2GameObject.findGameObjectByLocation(x) == null);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                //CODE HERE

                System.out.println(hunterType.get().name());

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private void walkToLocation(WorldPoint location) {
        sleepUntil(() -> {
            Rs2Walker.walkFastCanvas(location);
            sleepGaussian(400, 150);
            return Rs2Player.getWorldLocation().equals(location);
        });
    }

    private void layTrap(WorldPoint location, Optional<HunterType> hunterType) {
        hunterType.ifPresent(type -> {
            String requiredItem = type.getRequiredItems().stream().findFirst().orElse(null);
            if (requiredItem != null) {
                Rs2Inventory.interact(requiredItem, "lay");
                sleepUntil(() -> Rs2GameObject.getGameObject(location) != null);
            }
        });
    }
}
