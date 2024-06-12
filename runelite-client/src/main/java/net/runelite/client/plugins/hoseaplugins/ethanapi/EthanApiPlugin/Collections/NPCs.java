package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.NPCQuery;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class NPCs {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    private static final List<NPC> npcList = new ArrayList<>();

    public static NPCQuery search() {
        return new NPCQuery(npcList);
    }

    @Subscribe(priority = 10000)
    public void onGameTick(GameTick e) {
        npcList.clear();
        for (NPC npc : client.getNpcs()) {
            if (npc == null)
                continue;
            if (npc.getId() == -1)
                continue;
            npcList.add(npc);
        }
    }
}
