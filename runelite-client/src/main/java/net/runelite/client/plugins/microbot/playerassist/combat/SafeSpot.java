package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistPlugin;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SafeSpot extends Script {

    public WorldPoint currentSafeSpot = null;
    private boolean messageShown = false;

public boolean run(PlayerAssistConfig config) {
    AtomicReference<List<String>> npcsToAttack = new AtomicReference<>(Arrays.stream(Arrays.stream(config.attackableNpcs().split(",")).map(String::trim).toArray(String[]::new)).collect(Collectors.toList()));
    mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
        try {
            if (!Microbot.isLoggedIn() || !super.run() || !config.toggleSafeSpot() || Microbot.isMoving()) return;

            currentSafeSpot = config.safeSpot();
            if(isDefaultSafeSpot(currentSafeSpot)){

                if(!messageShown){
                    Microbot.showMessage("Please set a center location");
                    messageShown = true;
                }
                return;
            }
            if (isDefaultSafeSpot(currentSafeSpot) || isPlayerAtSafeSpot(currentSafeSpot)) {
                //ckeck if there is an NPC targeting us
                List<NPC> npcList = Rs2Npc.getNpcsAttackingPlayer(Microbot.getClient().getLocalPlayer());

                //if there is an NPC interacting with us, and we are not Interacting with it, attack it again
                if (!npcList.isEmpty() && !Rs2Player.isInMulti()) {
                    npcList.forEach(npc -> {
                        if (Microbot.getClient().getLocalPlayer().getInteracting() == null) {
                            if (npcsToAttack.get().contains(npc.getName())) {
                                Rs2Npc.attack(npc);
                                PlayerAssistPlugin.setCooldown(config.playStyle().getRandomTickInterval());
                            }
                        }
                    });
                    return;
                }

                return;
            }

            messageShown = false;

            if(Rs2Walker.walkMiniMap(currentSafeSpot)) {
                Microbot.pauseAllScripts = true;
                sleepUntil(() -> isPlayerAtSafeSpot(currentSafeSpot));
                Microbot.pauseAllScripts = false;
                //ckeck if there is an NPC targeting us
                List<NPC> npcList = Rs2Npc.getNpcsAttackingPlayer(Microbot.getClient().getLocalPlayer());

                //if there is an NPC interacting with us, and we are not Interacting with it, attack it again
                if (!npcList.isEmpty() && !Rs2Player.isInMulti()) {
                    npcList.forEach(npc -> {
                        if (Microbot.getClient().getLocalPlayer().getInteracting() == null) {
                            if (npcsToAttack.get().contains(npc.getName())) {
                                Rs2Npc.attack(npc);
                                PlayerAssistPlugin.setCooldown(config.playStyle().getRandomTickInterval());
                            }

                        }
                    });
                }


            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }, 0, 600, TimeUnit.MILLISECONDS);
    return true;
}

private boolean isDefaultSafeSpot(WorldPoint safeSpot) {
    return safeSpot.getX() == 0 && safeSpot.getY() == 0;
}

private boolean isPlayerAtSafeSpot(WorldPoint safeSpot) {
    return safeSpot.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) <= 0;
}

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
