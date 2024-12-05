package net.runelite.client.plugins.microbot.util.groundobject;

import net.runelite.api.Client;
import net.runelite.api.GroundObject;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.ObjectComposition;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.misc.Rs2UiHelper;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Rs2GroundObject {

    public static List<GroundObject> getAll() {
        return Arrays.stream(Microbot.getClient().getScene().getTiles())
                .flatMap(Arrays::stream)
                .flatMap(Arrays::stream)
                .filter(tile -> tile != null && tile.getGroundObject() != null)
                .map(tile -> tile.getGroundObject())
                .collect(Collectors.toList());
    }

    public static List<GroundObject> get(Predicate<GroundObject> filter) {
        return getAll().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static GroundObject getNearest(Predicate<GroundObject> filter) {
        WorldPoint playerLocation = Microbot.getClient().getLocalPlayer().getWorldLocation();
        return getAll().stream()
                .filter(filter)
                .min((o1, o2) -> {
                    double dist1 = o1.getWorldLocation().distanceTo(playerLocation);
                    double dist2 = o2.getWorldLocation().distanceTo(playerLocation);
                    return Double.compare(dist1, dist2);
                })
                .orElse(null);
    }

    public static GroundObject getNearest(int id) {
        return getNearest(obj -> obj.getId() == id);
    }

    public static boolean interact(int id, String... actions) {
        GroundObject object = getNearest(id);
        return interact(object, actions);
    }

    public static boolean interact(GroundObject groundObject, String... actions) {
        if (groundObject == null) return false;
        if (actions == null || actions.length == 0 || actions[0].isEmpty()) return false;

        Client client = Microbot.getClient();
        if (client.getLocalPlayer().getWorldLocation().distanceTo(groundObject.getWorldLocation()) > 20) {
            return false;
        }

        Point point = groundObject.getCanvasLocation();
        if (point == null) return false;

        Shape shape = groundObject.getClickbox();
        if (shape == null) return false;

        Rectangle bounds = shape.getBounds();
        if (bounds == null) return false;

        String targetAction = actions[0];
        ObjectComposition objComp = client.getObjectDefinition(groundObject.getId());
        if (objComp == null) return false;

        int optionIndex = -1;
        String[] objectActions = objComp.getActions();
        for (int i = 0; i < objectActions.length; i++) {
            if (objectActions[i] != null && objectActions[i].equals(targetAction)) {
                optionIndex = i;
                break;
            }
        }

        if (optionIndex == -1) return false;

        MenuAction menuAction;
        switch (optionIndex) {
            case 0:
                menuAction = MenuAction.GAME_OBJECT_FIRST_OPTION;
                break;
            case 1:
                menuAction = MenuAction.GAME_OBJECT_SECOND_OPTION;
                break;
            case 2:
                menuAction = MenuAction.GAME_OBJECT_THIRD_OPTION;
                break;
            case 3:
                menuAction = MenuAction.GAME_OBJECT_FOURTH_OPTION;
                break;
            case 4:
                menuAction = MenuAction.GAME_OBJECT_FIFTH_OPTION;
                break;
            default:
                return false;
        }

        Microbot.doInvoke(new NewMenuEntry(
                groundObject.getLocalLocation().getSceneX(),
                groundObject.getLocalLocation().getSceneY(),
                menuAction.getId(),
                groundObject.getId(),
                -1,
                targetAction,
                "",
                groundObject
        ), Rs2UiHelper.getObjectClickbox(groundObject));
        
        return true;
    }

    public static boolean exists(int id) {
        return getNearest(id) != null;
    }

    public static boolean exists(Predicate<GroundObject> filter) {
        return getNearest(filter) != null;
    }

    public static boolean isReachable(GroundObject groundObject) {
        if (groundObject == null) return false;
        return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(groundObject.getWorldLocation()) <= 20;
    }

    public static boolean isReachable(int id) {
        GroundObject object = getNearest(id);
        return isReachable(object);
    }
} 