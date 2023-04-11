package net.runelite.client.plugins.microbot.util.gameobject;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.menu.Menu;

import java.awt.*;


public class GameObject {

    public static net.runelite.api.GameObject findGameObject(int id) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Scene scene = Microbot.getClient().getScene();
            Tile[][][] tiles = scene.getTiles();

            int z = Microbot.getClient().getPlane();

            for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
                for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                    Tile tile = tiles[z][x][y];

                    if (tile == null) {
                        continue;
                    }

                    Player player = Microbot.getClient().getLocalPlayer();
                    if (player == null) {
                        continue;
                    }
                    net.runelite.api.GameObject[] gameObjects = tile.getGameObjects();
                    if (gameObjects != null) {
                        for (net.runelite.api.GameObject gameObject : gameObjects) {
                            if (gameObject != null && gameObject.getSceneMinLocation().equals(tile.getSceneLocation())) {
                                if (player.getLocalLocation().distanceTo(gameObject.getLocalLocation()) <= 2400) {
                                    if (gameObject.getId() == id) {
                                        return gameObject;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        });
    }

    public static net.runelite.api.GameObject findGameObject(int[] ids) {
        net.runelite.api.GameObject gameObject = null;
        for (int id :
                ids) {
            gameObject = findGameObject(id);
        }
        return gameObject;
    }


    public static boolean interact(int objectId, String action) {
        net.runelite.api.GameObject gameObject = findGameObject(objectId);
        if (gameObject == null) {
            return false;
        }
        if (!Camera.isTileOnScreen(gameObject)) {
            Camera.turnTo(gameObject);
            return false;
        }
        Polygon canvasPoint = gameObject.getCanvasTilePoly();
        Point screenLoc = new Point((int) canvasPoint.getBounds().getCenterX(), (int) canvasPoint.getBounds().getCenterY());

        return Menu.doAction(action, screenLoc);
    }

    public static boolean interact(int[] objectIds, String action) {
        for (int i = 0; i < objectIds.length; i++) {
            if (interact(objectIds[i], action))
                return true;
        }
        return false;
    }

}
