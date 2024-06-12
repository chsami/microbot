package net.runelite.client.plugins.hoseaplugins.AutoCombat.helper;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.NPCPackets;
import com.google.inject.Inject;
import net.runelite.client.plugins.hoseaplugins.AutoCombat.SlayerNpc;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SlayerHelper {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    public boolean isSlayerNPC(List<String> names) {
        for (SlayerNpc snpc : SlayerNpc.values()) {
            if (names.contains(snpc.getNpcName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isSlayerNPC(String name) {
        for (SlayerNpc snpc : SlayerNpc.values()) {
            if (snpc.getNpcName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public SlayerNpc getSlayerInfo(List<String> names) {
        if (!isSlayerNPC(names))
            return null;
        return Arrays.stream(SlayerNpc.values()).filter(snpc ->
                names.contains(snpc.getNpcName())).findFirst().orElse(null);

    }

    public SlayerNpc getSlayerInfo(String name) {
        if (!isSlayerNPC(name))
            return null;
        return Arrays.stream(SlayerNpc.values()).filter(snpc ->
                snpc.getNpcName().equals(name)).findFirst().orElse(null);

    }

    public void useSlayerItem(String itemName) {
        Inventory.search().nameContains(itemName).filter(i -> !i.getName().contains("ay 0")).first().ifPresent(item -> {
            log.info("Using item: " + item.getName());
            MousePackets.queueClickPacket();
            MousePackets.queueClickPacket();
            NPCPackets.queueWidgetOnNPC(NPCs.search().interactingWithLocal().first().get(), item);

        });
    }

}