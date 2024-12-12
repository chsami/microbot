package net.runelite.client.plugins.microbot.util.hunter;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.hunter.data.HunterType;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Optional;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class Rs2Hunter {

    /**
     * Determines what a player can hunt based on inventory items
     * @return
     */
    public static Optional<HunterType> determineHuntingType() {
        for (HunterType hunterType : HunterType.values()) {
            if (Rs2Inventory.hasItem(hunterType.getRequiredItems())) {
                return Optional.of(hunterType);
            }
        }
        return Optional.empty();
    }

    public void handleTrapPlacement(WorldPoint freeTrapLocation, Optional<HunterType> hunterType) {
        // Determine the target location
        WorldPoint targetLocation = (freeTrapLocation == null) ? Rs2Tile.getCenter(hunterArea) : freeTrapLocation;

        // Ensure the target location has a game object
        if (Rs2GameObject.findGameObjectByLocation(targetLocation) != null) {
            // Walk to the target location
            Rs2Walker.walkToLocationUntilArrived(targetLocation);

            // Lay the trap if we're at the target location
            if (Rs2Player.getWorldLocation().equals(targetLocation)) {
                layTrap(targetLocation, hunterType);
            }
        }
    }

    public static void layTrap(WorldPoint location, Optional<HunterType> hunterType) {
        hunterType.ifPresent(type -> {
            String requiredItem = type.getRequiredItems().stream().findFirst().orElse(null);
            if (requiredItem != null) {
                Rs2Inventory.interact(requiredItem, "lay");
                sleepUntil(() -> Rs2GameObject.getGameObject(location) != null);
            }
        });
}
