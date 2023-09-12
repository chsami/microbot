package net.runelite.client.plugins.microbot.util.walker.pathfinder;

import net.runelite.api.ObjectID;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.WorldType;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.devtools.MovementFlag;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.walker.Transport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollisionMap {

    public CollisionMap() {
        doorIds.add(ObjectID.DOOR_11773);
        doorIds.add(ObjectID.LARGE_DOOR_12349);
        doorIds.add(ObjectID.LARGE_DOOR_12350);
        doorIds.add(ObjectID.DOOR_1535);
        doorIds.add(ObjectID.DOOR_1543);
        doorIds.add(ObjectID.GATE_1558);
        doorIds.add(ObjectID.GATE_1560);
        doorIds.add(ObjectID.DOOR_11775);
        doorIds.add(44598);//alkharid gate
        doorIds.add(44599);//alkharid gate
        doorIds.add(ObjectID.DOOR_1804);//hill giants edgeville dungeon
        doorIds.add(ObjectID.LARGE_DOOR_17091);//taverly
        doorIds.add(ObjectID.LARGE_DOOR_17093); //taverly
        doorIds.add(ObjectID.GATE_9720); //rat gate tutorialIsland
        doorIds.add(ObjectID.GATE_9719); //rat gate tutorialIsland
        doorIds.add(ObjectID.DOOR_9721); //tutorialIsland door to financial advisor
        doorIds.add(ObjectID.DOOR_9722); //tutorialIsland door to prayer altar
        doorIds.add(ObjectID.DOOR_9723); //tutorialIsland door to mage
        doorIds.add(ObjectID.DOOR_24318); // door to warriors guild
        doorIds.add(ObjectID.DOOR_24309); // door to animated armour inside warrior guild
        doorIds.add(ObjectID.DOOR_24306); // door to animated armour inside warrior guild
        doorIds.add(ObjectID.GATE_26131); // Murder Mystery start quest gate
        doorIds.add(ObjectID.GATE_26131); // Murder Mystery start quest gate
    }

    public static WorldArea[] blockingAreas = new WorldArea[] {
            new WorldArea(new WorldPoint(3085, 3333, 0), 50, 50), //draynor manor
            new WorldArea(new WorldPoint(2535, 3109, 0), 10, 30)}; //under maze

    private static List<Integer> doorIds = new ArrayList<>();
    public static List<CheckedNode> nodesChecked = new ArrayList<>();
    public static List<CheckedNode> wallNodes = new ArrayList<>();


    public List<Node> getNeighbors(Node node, PathfinderConfig config, boolean useTransport, WorldPoint target, boolean canReachActivated) {
        try {
            List<Node> neighbors = new ArrayList<>();
            CheckedNode checkedNode = new CheckedNode();
            checkedNode.node = node;

            //list of hard to navigate areas like "draynor". If target point is not inside draynor, then we block any points that are calculated in draynor. This is to avoid getting stuck
            if (Arrays.stream(blockingAreas).anyMatch(x -> x.contains(node.position)))
                return new ArrayList<>();

            if (useTransport) {
                for (Transport transport : config.getTransports().getOrDefault(node.position, new ArrayList<>())) {
                    if (transport.isMember && !Microbot.getClient().getWorldType().contains(WorldType.MEMBERS))
                        continue;
                    if (config.useTransport(transport)) {
                        neighbors.add(new TransportNode(transport.getDestination(), node, transport.getWait()));
                    }
                }
            }


            boolean[] traversable;
            int[][] flags = Microbot.getClient().getCollisionMaps()[Microbot.getClient().getPlane()].getFlags();
            LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), node.position);
            if (localPoint.getSceneX() < 0 || localPoint.getSceneY() < 0) return neighbors;
            if (localPoint.getSceneX() >= 104 || localPoint.getSceneY() >= 104) return neighbors;
            int data = flags[localPoint.getSceneX()][localPoint.getSceneY()];
            MovementFlag[] movementFlags = MovementFlag.getSetFlags(data).toArray(MovementFlag[]::new);

            if (MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_FULL) && node instanceof TransportNode == false) {
                traversable = new boolean[]{
                        false, false, false, false, false, false, false, false
                };
                checkedNode.status = 0;
            }
            else {
                traversable = new boolean[]{
                        !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_WEST),
                        !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_EAST),
                        !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_SOUTH),
                        !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_NORTH),
                        false,
                        false,
                        false,
                        false,
                };
                checkedNode.status = 1;
                /*
                code for navigating diagonally
                     !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_SOUTH_WEST) && !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_WEST)  && !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_SOUTH),
                        !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_SOUTH_EAST) && !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_EAST) && !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_SOUTH),
                        !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_NORTH_WEST) && !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_WEST) &&  !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_NORTH),
                        !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_NORTH_EAST) && !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_EAST) && !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_NORTH),
                 */
            }

            Scene scene = Microbot.getClient().getScene();
            Tile[][][] tiles = scene.getTiles();
            CheckedNode wallNode = new CheckedNode();

            for (int i = 0; i < traversable.length; i++) {
                OrdinalDirection d = OrdinalDirection.values()[i];
                if (d == OrdinalDirection.NORTH_WEST || d == OrdinalDirection.NORTH_EAST || d == OrdinalDirection.SOUTH_EAST || d == OrdinalDirection.SOUTH_WEST)
                    continue;

                if (traversable[i]) {
//                    localPoint = LocalPoint.fromWorld(Microbot.getClient(), node.position.dx(d.x).dy(d.y));
//                    if (localPoint.getSceneX() < 0 || localPoint.getSceneY() < 0) return neighbors;
//                    if (localPoint.getSceneX() >= 104 || localPoint.getSceneY() >= 104) return neighbors;
//                    data = flags[localPoint.getSceneX()][localPoint.getSceneY()];
//                    movementFlags = MovementFlag.getSetFlags(data).toArray(MovementFlag[]::new);
//                    if (!MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_FULL) && !MovementFlag.hasFlag(movementFlags, MovementFlag.BLOCK_MOVEMENT_OBJECT))
//                    {
                        neighbors.add(new Node(node.position.dx(d.x).dy(d.y), node));
                        checkedNode.status = 2;
//                    }
                } else if (!canReachActivated) {
                    LocalPoint localNodePointNorth = LocalPoint.fromWorld(Microbot.getClient(), new WorldPoint(node.position.getX(), node.position.getY() + 1, node.position.getPlane()));
                    LocalPoint localNodePointEast = LocalPoint.fromWorld(Microbot.getClient(), new WorldPoint(node.position.getX() + 1, node.position.getY(), node.position.getPlane()));
                    LocalPoint localNodePointSouth = LocalPoint.fromWorld(Microbot.getClient(), new WorldPoint(node.position.getX(), node.position.getY() - 1, node.position.getPlane()));
                    LocalPoint localNodePointWest = LocalPoint.fromWorld(Microbot.getClient(), new WorldPoint(node.position.getX() - 1, node.position.getY(), node.position.getPlane()));

                    Tile tileNorth = tiles[node.position.getPlane()][localNodePointNorth.getSceneX()][localNodePointNorth.getSceneY()];
                    Tile tileEast = tiles[node.position.getPlane()][localNodePointEast.getSceneX()][localNodePointEast.getSceneY()];
                    Tile tileSouth = tiles[node.position.getPlane()][localNodePointSouth.getSceneX()][localNodePointSouth.getSceneY()];
                    Tile tileWest = tiles[node.position.getPlane()][localNodePointWest.getSceneX()][localNodePointWest.getSceneY()];

                    LocalPoint currentNode = LocalPoint.fromWorld(Microbot.getClient(), new WorldPoint(node.position.getX(), node.position.getY(), node.position.getPlane()));
                    Tile currentTile = tiles[node.position.getPlane()][currentNode.getSceneX()][currentNode.getSceneY()];

                    if (currentTile.getWallObject() != null) {
                        if (doorIds.stream().anyMatch(doorId -> doorId == currentTile.getWallObject().getId()))
                        {
                            neighbors.add(new Node(node.position.dx(d.x).dy(d.y), node, true));
                            wallNode.node = new Node(currentTile.getWorldLocation(), node);
                            wallNode.shape = currentTile.getWallObject().getClickbox();
                            wallNode.status = 3;
                            wallNodes.add(wallNode);
                        }
                    }

                   if (tileNorth.getWallObject() != null && d == OrdinalDirection.NORTH) {
                        if (doorIds.stream().anyMatch(doorId -> doorId == tileNorth.getWallObject().getId()))
                        {
                            neighbors.add(new Node(tileNorth.getWorldLocation(), node, true));
                            wallNode.node = new Node(tileNorth.getWorldLocation(), node);
                            wallNode.shape = tileNorth.getWallObject().getClickbox();
                            wallNode.status = 3;
                            wallNodes.add(wallNode);
                        }
                    } else if (tileEast.getWallObject() != null &&  d == OrdinalDirection.EAST) {
                        if (doorIds.stream().anyMatch(doorId -> doorId == tileEast.getWallObject().getId()))
                        {
                            neighbors.add(new Node(tileEast.getWorldLocation(), node, true));
                            wallNode.node = new Node(tileEast.getWorldLocation(), node);
                            wallNode.shape = tileEast.getWallObject().getClickbox();
                            wallNode.status = 3;
                            wallNodes.add(wallNode);
                        }
                    } else if (tileSouth.getWallObject() != null &&  d == OrdinalDirection.SOUTH) {
                        if (doorIds.stream().anyMatch(doorId -> doorId == tileSouth.getWallObject().getId()))
                        {
                            neighbors.add(new Node(tileSouth.getWorldLocation(), node, true));
                            wallNode.node = new Node(tileSouth.getWorldLocation(), node);
                            wallNode.shape = tileSouth.getWallObject().getClickbox();
                            wallNode.status = 3;
                            wallNodes.add(wallNode);
                        }
                    } else if (tileWest.getWallObject() != null && d == OrdinalDirection.WEST) {
                        if (doorIds.stream().anyMatch(doorId -> doorId == tileWest.getWallObject().getId()))
                        {
                            neighbors.add(new Node(tileWest.getWorldLocation(), node, true));
                            wallNode.node = new Node(tileWest.getWorldLocation(), node);
                            wallNode.shape = tileWest.getWallObject().getClickbox();
                            wallNode.status = 3;
                            wallNodes.add(wallNode);
                        }
                    }
                }
            }
            nodesChecked.add(checkedNode);
            return neighbors;
        } catch(Exception ex) {
           // System.out.println(ex.getMessage());
        }
        return new ArrayList<>();
    }
}
