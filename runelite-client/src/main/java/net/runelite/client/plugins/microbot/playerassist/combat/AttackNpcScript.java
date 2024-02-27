package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AttackNpcScript extends Script {

    String[] configAttackableNpcs;

    public static Actor currentNpc = null;

    public static List<NPC> attackableNpcs = new ArrayList();

    boolean clicked = false;

    public void run(PlayerAssistConfig config) {
        String npcToAttack = Arrays.stream(Arrays.stream(config.attackableNpcs().split(",")).map(String::trim).toArray(String[]::new)).findFirst().get();
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!config.toggleCombat()) return;
                 attackableNpcs =  Microbot.getClient().getNpcs().stream()
                        .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                        .filter(x -> !x.isDead()
                                && x.getWorldLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) < config.attackRadius()
                                && (!x.isInteracting() || x.getInteracting() == Microbot.getClient().getLocalPlayer())
                                && (x.getInteracting() == null  || x.getInteracting() == Microbot.getClient().getLocalPlayer())
                                && x.getAnimation() == -1 && npcToAttack.equalsIgnoreCase(x.getName())).collect(Collectors.toList());
                Player player = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer());
                if (player.isInteracting() || player.getAnimation() != -1) {
                    return;
                }
                for (NPC npc : attackableNpcs) {
                    if (npc == null
                            || npc.getAnimation() != -1
                            || npc.isDead()
                            || (npc.getInteracting() != null && npc.getInteracting() != Microbot.getClient().getLocalPlayer())
                            || (npc.isInteracting()  && npc.getInteracting() != Microbot.getClient().getLocalPlayer())
                            || !npc.getName().equalsIgnoreCase(npcToAttack))
                        break;
                    if (npc.getWorldLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > config.attackRadius())
                        break;
                    if (!Rs2Camera.isTileOnScreen(npc.getLocalLocation()))
                        Rs2Camera.turnTo(npc);

                    if (!Microbot.getWalker().canReach(npc.getWorldLocation()))
                        continue;
                    Rs2Npc.interact(npc, "attack");
                    sleepUntil(() -> Microbot.getClient().getLocalPlayer().isInteracting() && Microbot.getClient().getLocalPlayer().getInteracting() instanceof NPC);
                    break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    public static void skipNpc() {
        currentNpc = null;
    }

    public void shutdown() {
        super.shutdown();
        configAttackableNpcs = null;
        clicked = false;
    }
}