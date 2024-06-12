package net.runelite.client.plugins.hoseaplugins.ethanapi.Packets;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.NPCQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketDef;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketReflection;
import lombok.SneakyThrows;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.widgets.Widget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NPCPackets {

    @SneakyThrows
    public static void queueNPCAction(int actionFieldNo, int npcIndex, boolean ctrlDown) {
        int ctrl = ctrlDown ? 1 : 0;
        switch (actionFieldNo) {
            case 1:
                PacketReflection.sendPacket(PacketDef.getOpNpc1(), npcIndex, ctrl);
                break;
            case 2:
                PacketReflection.sendPacket(PacketDef.getOpNpc2(), npcIndex, ctrl);
                break;
            case 3:
                PacketReflection.sendPacket(PacketDef.getOpNpc3(), npcIndex, ctrl);
                break;
            case 4:
                PacketReflection.sendPacket(PacketDef.getOpNpc4(), npcIndex, ctrl);
                break;
            case 5:
                PacketReflection.sendPacket(PacketDef.getOpNpc5(), npcIndex, ctrl);
                break;
        }
    }

    @SneakyThrows
    public static void queueNPCAction(NPC npc, String... actionList) {
        if (npc == null) {
            return;
        }
        NPCComposition comp = NPCQuery.getNPCComposition(npc);
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
        int num = -1;
        for (String action : actions) {
            for (String action2 : actionList) {
                if (action != null && action.equalsIgnoreCase(action2)) {
                    num = actions.indexOf(action.toLowerCase()) + 1;
                }
            }
        }

        if (num < 1 || num > 10) {
            return;
        }
        queueNPCAction(num, npc.getIndex(), false);
    }

    public static void queueWidgetOnNPC(int npcIndex, int sourceItemId, int sourceSlot, int sourceWidgetId,
                                        boolean ctrlDown) {
        int ctrl = ctrlDown ? 1 : 0;
        PacketReflection.sendPacket(PacketDef.getOpNpcT(), npcIndex, sourceItemId, sourceSlot, sourceWidgetId, ctrl);
    }

    public static void queueWidgetOnNPC(NPC npc, Widget widget) {
        queueWidgetOnNPC(npc.getIndex(), widget.getItemId(), widget.getIndex(), widget.getId(), false);
    }
}
