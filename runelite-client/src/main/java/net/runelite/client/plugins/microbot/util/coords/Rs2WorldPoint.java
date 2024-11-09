package net.runelite.client.plugins.microbot.util.coords;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class Rs2WorldPoint {
    @Setter
    private WorldPoint worldPoint;

    // Constructor accepting coordinates
    public Rs2WorldPoint(int x, int y, int plane) {
        this.worldPoint = new WorldPoint(x, y, plane);
    }

    // Constructor accepting a WorldPoint instance
    public Rs2WorldPoint(WorldPoint worldPoint) {
        this.worldPoint = worldPoint;

    }

    // Getter methods
    public int getX() {
        return worldPoint.getX();
    }

    public int getY() {
        return worldPoint.getY();
    }

    public int getPlane() {
        return worldPoint.getPlane();
    }

    public WorldPoint getWorldPoint() {
        return worldPoint;
    }

    public List<WorldPoint> pathTo(WorldPoint other)
    {
        Client client = Microbot.getClient();
        if (getPlane() != other.getPlane())
        {
            return null;
        }

        LocalPoint sourceLp = LocalPoint.fromWorld(client.getTopLevelWorldView(), getX(), getY());
        LocalPoint targetLp = LocalPoint.fromWorld(client.getTopLevelWorldView(), other.getX(), other.getY());
        if (sourceLp == null || targetLp == null)
        {
            return null;
        }

        int thisX = sourceLp.getSceneX();
        int thisY = sourceLp.getSceneY();
        int otherX = targetLp.getSceneX();
        int otherY = targetLp.getSceneY();

        Tile[][][] tiles = client.getTopLevelWorldView().getScene().getTiles();
        Tile sourceTile = tiles[getPlane()][thisX][thisY];

        Tile targetTile = tiles[getPlane()][otherX][otherY];
        List<Tile> checkpointTiles = Rs2Tile.pathTo(sourceTile,targetTile);
        if (checkpointTiles == null)
        {
            return null;
        }
        List<WorldPoint> checkpointWPs = new ArrayList<>();
        for (Tile checkpointTile : checkpointTiles)
        {
            if (checkpointTile == null)
            {
                break;
            }
            checkpointWPs.add(checkpointTile.getWorldLocation());
        }
        return checkpointWPs;
    }

    public int distanceToPath(WorldPoint other)
    {
        if(other == null)
        {
            return Integer.MAX_VALUE;
        }
        List<WorldPoint> checkpointWPs = this.pathTo(other);
        if (checkpointWPs == null)
        {
            // No path found
            return Integer.MAX_VALUE;
        }

        WorldPoint destinationPoint = checkpointWPs.get(checkpointWPs.size() - 1);
        if (other.getX() != destinationPoint.getX() || other.getY() != destinationPoint.getY())
        {
            // Path found but not to the requested tile
            return Integer.MAX_VALUE;
        }
        WorldPoint Point1 = getWorldPoint();
        int distance = 0;
        for (WorldPoint Point2 : checkpointWPs)
        {
            distance += Point1.distanceTo2D(Point2);
            Point1 = Point2;
        }
        return distance;
    }

    /**
     * Finds the nearest walkable tile around a specified game object that the player can interact with.
     *
     * <p>This method calculates the closest walkable tile adjacent to the given game object, considering the player's current position.
     * It ensures that both the player and the object are on the same plane before proceeding. The method retrieves interactable points
     * around the object, filters out non-walkable tiles, and selects the closest one to the player.</p>
     *
     * @param tileObject The {@link GameObject} for which to find the nearest walkable tile.
     * @return An {@link Rs2WorldPoint} representing the nearest walkable tile around the object, or {@code null} if none are found.
     */
    public static Rs2WorldPoint getNearestWalkableTile(GameObject tileObject) {
        // Cache player's location and top-level world view
        Rs2WorldPoint playerLocation = Rs2Player.getRs2WorldPoint();
        WorldView topLevelWorldView = Microbot.getClient().getTopLevelWorldView();

        // Check if player and object are on the same plane
        if (playerLocation.getPlane() != tileObject.getWorldLocation().getPlane()) {
            return null;
        }

        // Get the world area of the game object
        Rs2WorldArea gameObjectArea = new Rs2WorldArea(Objects.requireNonNull(Rs2GameObject.getWorldArea(tileObject)));

        // Get interactable points around the game object
        List<WorldPoint> interactablePoints = getInteractablePoints(gameObjectArea, topLevelWorldView);

        if (interactablePoints.isEmpty()) {
            return null; // No interactable points found
        }

        // Filter points that are walkable
        List<WorldPoint> walkablePoints = interactablePoints.stream()
                .filter(Rs2Tile::isWalkable)
                .collect(Collectors.toList());

        if (walkablePoints.isEmpty()) {
            return null; // No walkable points available
        }

        // Find the nearest walkable interact point to the player
        WorldPoint nearestPoint = walkablePoints.stream()
                .min(Comparator.comparingInt(playerLocation::distanceToPath))
                .orElse(null);

        return new Rs2WorldPoint(nearestPoint);
    }

    /**
     * Retrieves a list of interactable points around a given game object area.
     *
     * <p>This method calculates interactable points around the specified game object area. If no initial interactable points
     * are found, it expands the area and collects new points, excluding those within the original object area. It also filters out
     * points from which the object cannot be reached via melee attacks or points that have walls obstructing interaction.</p>
     *
     * @param gameObjectArea   The {@link Rs2WorldArea} representing the area of the game object.
     * @param topLevelWorldView The top-level {@link WorldView} of the game client.
     * @return A {@link List} of {@link WorldPoint} objects that are interactable around the game object.
     */
    private static List<WorldPoint> getInteractablePoints(Rs2WorldArea gameObjectArea, WorldView topLevelWorldView) {
        // Get initial interactable points
        List<WorldPoint> interactablePoints = new ArrayList<>(gameObjectArea.getInteractable());

        if (interactablePoints.isEmpty()) {
            // If no interactable points, expand the area and get new points
            Rs2WorldArea expandedArea = gameObjectArea.offset(1);
            interactablePoints = expandedArea.toWorldPointList();

            // Remove points inside the game object area
            interactablePoints.removeIf(gameObjectArea::contains);

            // Remove points from which the object cannot be melee'd
            interactablePoints.removeIf(point -> !gameObjectArea.canMelee(topLevelWorldView, new Rs2WorldArea(point.toWorldArea())));
        } else {
            // Filter points from which the object can be melee'd
            interactablePoints = interactablePoints.stream()
                    .filter(point -> gameObjectArea.canMelee(topLevelWorldView, new Rs2WorldArea(point.toWorldArea())))
                    .collect(Collectors.toList());

            if (interactablePoints.isEmpty()) {
                // If no melee points, remove points with walls
                interactablePoints = gameObjectArea.getInteractable();
                interactablePoints.removeIf(Rs2Tile::tileHasWalls);
            }
        }

        return interactablePoints;
    }




    // Override equals, hashCode, and toString if necessary
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Rs2WorldPoint) {
            Rs2WorldPoint other = (Rs2WorldPoint) obj;
            return worldPoint.equals(other.worldPoint);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return worldPoint.hashCode();
    }

    @Override
    public String toString() {
        return worldPoint.toString();
    }
}

