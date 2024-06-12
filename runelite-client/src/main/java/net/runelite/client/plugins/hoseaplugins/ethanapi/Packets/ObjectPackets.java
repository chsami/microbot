package net.runelite.client.plugins.hoseaplugins.ethanapi.Packets;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.TileObjectQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketDef;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketReflection;
import lombok.SneakyThrows;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectPackets {
    @SneakyThrows
    public static void queueObjectAction(int actionFieldNo, int objectId, int worldPointX, int worldPointY,
                                         boolean ctrlDown) {
        int ctrl = ctrlDown ? 1 : 0;
        switch (actionFieldNo) {
            case 1:
                PacketReflection.sendPacket(PacketDef.getOpLoc1(), objectId, worldPointX, worldPointY, ctrl);
                break;
            case 2:
                PacketReflection.sendPacket(PacketDef.getOpLoc2(), objectId, worldPointX, worldPointY, ctrl);
                break;
            case 3:
                PacketReflection.sendPacket(PacketDef.getOpLoc3(), objectId, worldPointX, worldPointY, ctrl);
                break;
            case 4:
                PacketReflection.sendPacket(PacketDef.getOpLoc4(), objectId, worldPointX, worldPointY, ctrl);
                break;
            case 5:
                PacketReflection.sendPacket(PacketDef.getOpLoc5(), objectId, worldPointX, worldPointY, ctrl);
                break;
        }
    }

    @SneakyThrows
    public static void queueObjectAction(TileObject object, boolean ctrlDown, String... actionlist) {
        if (object == null) {
            return;
        }
        ObjectComposition comp = TileObjectQuery.getObjectComposition(object);
        if (comp == null) {
            return;
        }
        if (comp.getActions() == null) {
            return;
        }
        List<String> actions = Arrays.stream(comp.getActions()).collect(Collectors.toList());
        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i) == null)
                continue;
            actions.set(i, actions.get(i).toLowerCase());
        }
        Point p;
        if (object instanceof GameObject) {
            GameObject gameObject = (GameObject) object;
            p = gameObject.getSceneMinLocation();
        } else {
            p = new Point(object.getLocalLocation().getSceneX(), object.getLocalLocation().getSceneY());
        }
        LocalPoint lp = new LocalPoint(p.getX(), p.getY());
        WorldPoint wp = WorldPoint.fromScene(PacketReflection.getClient(), lp.getX(), lp.getY(), object.getPlane());
        int num = -1;
        for (String action : actions) {
            for (String action2 : actionlist) {
                if (action != null && action.equalsIgnoreCase(action2.toLowerCase())) {
                    num = actions.indexOf(action) + 1;
                }
            }
        }

        if (num < 1 || num > 10) {
            return;
        }
        queueObjectAction(num, object.getId(), wp.getX(), wp.getY(), ctrlDown);
    }

    public static void queueWidgetOnTileObject(int objectId, int worldPointX, int worldPointY, int sourceSlot,
                                               int sourceItemId, int sourceWidgetId, boolean ctrlDown) {
        int ctrl = ctrlDown ? 1 : 0;
        PacketReflection.sendPacket(PacketDef.getOpLocT(), objectId, worldPointX, worldPointY, sourceSlot, sourceItemId,
                sourceWidgetId, ctrl);
    }

    public static void queueWidgetOnTileObject(Widget widget, TileObject object) {
        Point p;
        if (object instanceof GameObject) {
            GameObject gameObject = (GameObject) object;
            p = gameObject.getSceneMinLocation();
        } else {
            p = new Point(object.getLocalLocation().getSceneX(), object.getLocalLocation().getSceneY());
        }
        LocalPoint lp = new LocalPoint(p.getX(), p.getY());
        WorldPoint wp = WorldPoint.fromScene(PacketReflection.getClient(), lp.getX(), lp.getY(), object.getPlane());
        queueWidgetOnTileObject(object.getId(), wp.getX(), wp.getY(), widget.getIndex(),
                widget.getItemId(),
                widget.getId(),
                false);
    }
}
