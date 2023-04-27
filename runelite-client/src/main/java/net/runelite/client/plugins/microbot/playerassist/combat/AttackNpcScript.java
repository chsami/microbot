package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AttackNpcScript extends Script {

    String[] attackableNpcs;

    public static Actor currentNpc = null;

    public void run(PlayerAssistConfig config) {
        attackableNpcs = Arrays.stream(config.attackableNpcs().split(",")).map(x -> x.trim()).toArray(String[]::new);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            NPC[] npcs = Rs2Npc.getNpcs();
            Player player = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer());
            if (player.isInteracting() || player.getAnimation() != -1) {
                return;
            }
            for (String npcToAttack : attackableNpcs) {
                for (NPC npc : npcs) {
                    if ((npc.getInteracting() == player && npc.getCombatLevel() > 0) || npc.getName().toLowerCase().equals(npcToAttack.toLowerCase())) {
                        if (player.isInteracting() == true && npc.getInteracting() == player)
                            break;
                        if (npc.isInteracting() && npc.getInteracting() != player)
                            continue;
                        if (!Camera.isTileOnScreen(npc.getLocalLocation()))
                            Camera.turnTo(npc);
                        if (currentNpc == npc) continue;
                        Rs2Menu.doAction("Attack", npc.getCanvasTilePoly(), new String[]{npc.getName()});
                        Microbot.pauseAllScripts = true;
                        sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().isInteracting());
                        sleep(1200, 2000);
                        currentNpc = npc;
                        Microbot.pauseAllScripts = false;
                        break;
                    }
                }
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    public static void skipNpc() {
        currentNpc = null;
    }

    public void shutdown() {
        super.shutdown();
        attackableNpcs = null;
    }
}