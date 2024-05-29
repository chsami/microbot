package net.runelite.client.plugins.microbot.bossassist.api;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

public class NPCUtils {
    public static boolean isNearNPC(int id) {

        NPC currentNPC = Rs2Npc.getNpc(id);

        if (currentNPC == null || currentNPC.isDead()) return false;

        int distance = Microbot.getClient().getLocalPlayer().getLocalLocation().distanceTo(currentNPC.getLocalLocation());
        System.out.println(distance);

        return false;
    }
}
