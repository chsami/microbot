package net.runelite.client.plugins.hoseaplugins.ethanapi.Packets;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.ETileItem;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketDef;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketReflection;
import lombok.SneakyThrows;
import net.runelite.api.widgets.Widget;

public class TileItemPackets {
    @SneakyThrows
    public static void queueTileItemAction(int actionFieldNo, int objectId, int worldPointX, int worldPointY,
                                           boolean ctrlDown) {
        int ctrl = ctrlDown ? 1 : 0;
        switch (actionFieldNo) {
            case 1:
                PacketReflection.sendPacket(PacketDef.getOpObj1(), objectId, worldPointX, worldPointY, ctrl);
                break;
            case 2:
                PacketReflection.sendPacket(PacketDef.getOpObj2(), objectId, worldPointX, worldPointY, ctrl);
                break;
            case 3:
                PacketReflection.sendPacket(PacketDef.getOpObj3(), objectId, worldPointX, worldPointY, ctrl);
                break;
            case 4:
                PacketReflection.sendPacket(PacketDef.getOpObj4(), objectId, worldPointX, worldPointY, ctrl);
                break;
            case 5:
                PacketReflection.sendPacket(PacketDef.getOpObj5(), objectId, worldPointX, worldPointY, ctrl);
                break;
        }
    }

    public static void queueWidgetOnTileItem(int objectId, int worldPointX, int worldPointY, int sourceSlot,
                                             int sourceItemId, int sourceWidgetId, boolean ctrlDown) {
        int ctrl = ctrlDown ? 1 : 0;
        PacketReflection.sendPacket(PacketDef.getOpObjT(), objectId, worldPointX, worldPointY, sourceSlot, sourceItemId,
                sourceWidgetId, ctrl);
    }

    public static void queueTileItemAction(ETileItem item, boolean ctrlDown) {
        queueTileItemAction(3, item.getTileItem().getId(), item.getLocation().getX(), item.getLocation().getY(),
                ctrlDown);
    }

    public static void queueWidgetOnTileItem(ETileItem item, Widget w,
                                             boolean ctrlDown) {
        queueWidgetOnTileItem(item.getTileItem().getId(), item.getLocation().getX(), item.getLocation().getY(),
                w.getIndex(), w.getItemId(), w.getId(), ctrlDown);
    }
}
