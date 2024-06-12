package net.runelite.client.plugins.hoseaplugins.ethanapi.Packets;

import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketDef;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketReflection;
import lombok.SneakyThrows;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WidgetPackets {
    @SneakyThrows
    public static void queueWidgetActionPacket(int actionFieldNo, int widgetId, int itemId, int childId) {
        switch (actionFieldNo) {
            case 1:
                PacketReflection.sendPacket(PacketDef.getIfButton1(), widgetId, childId, itemId);
                break;
            case 2:
                PacketReflection.sendPacket(PacketDef.getIfButton2(), widgetId, childId, itemId);
                break;
            case 3:
                PacketReflection.sendPacket(PacketDef.getIfButton3(), widgetId, childId, itemId);
                break;
            case 4:
                PacketReflection.sendPacket(PacketDef.getIfButton4(), widgetId, childId, itemId);
                break;
            case 5:
                PacketReflection.sendPacket(PacketDef.getIfButton5(), widgetId, childId, itemId);
                break;
            case 6:
                PacketReflection.sendPacket(PacketDef.getIfButton6(), widgetId, childId, itemId);
                break;
            case 7:
                PacketReflection.sendPacket(PacketDef.getIfButton7(), widgetId, childId, itemId);
                break;
            case 8:
                PacketReflection.sendPacket(PacketDef.getIfButton8(), widgetId, childId, itemId);
                break;
            case 9:
                PacketReflection.sendPacket(PacketDef.getIfButton9(), widgetId, childId, itemId);
                break;
            case 10:
                PacketReflection.sendPacket(PacketDef.getIfButton10(), widgetId, childId, itemId);
                break;
        }
    }

    @SneakyThrows
    public static void queueWidgetAction(Widget widget, String... actionlist) {
        if (widget == null) {
            System.out.println("call to queueWidgetAction with null widget");
            return;
        }
        List<String> actions = Arrays.stream(widget.getActions()).collect(Collectors.toList());
        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i) == null)
                continue;
            actions.set(i, actions.get(i).toLowerCase());
        }
        int num = -1;
        for (String action : actions) {
            for (String action2 : actionlist) {
                if (action != null && Text.removeTags(action).equalsIgnoreCase(action2)) {
                    num = actions.indexOf(action.toLowerCase()) + 1;
                }
            }
        }

        if (num < 1 || num > 10) {
            return;
        }
        queueWidgetActionPacket(num, widget.getId(), widget.getItemId(), widget.getIndex());
    }

    public static void queueWidgetOnWidget(Widget srcWidget, Widget destWidget) {
        queueWidgetOnWidget(srcWidget.getId(), srcWidget.getIndex(), srcWidget.getItemId(), destWidget.getId(), destWidget.getIndex(), destWidget.getItemId());
    }

    public static void queueWidgetOnWidget(int sourceWidgetId, int sourceSlot, int sourceItemId,
                                           int destinationWidgetId, int destinationSlot, int destinationItemId) {
        PacketReflection.sendPacket(PacketDef.getIfButtonT(), sourceWidgetId, sourceSlot, sourceItemId, destinationWidgetId,
                destinationSlot, destinationItemId);
    }

    public static void queueResumePause(int widgetId, int childId) {
        PacketReflection.sendPacket(PacketDef.getResumePausebutton(), widgetId, childId);
    }

}