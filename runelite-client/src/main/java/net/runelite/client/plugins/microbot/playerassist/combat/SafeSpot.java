package net.runelite.client.plugins.microbot.playerassist.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistPlugin;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistState;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class SafeSpot extends Script {

    public int minimumHealth = 10;
    public WorldPoint currentSafeSpot = null;
    private boolean messageShown = false;

    public boolean run(PlayerAssistConfig config) {
        AtomicReference<List<String>> npcsToAttack = new AtomicReference<>(Arrays.stream(Arrays.stream(config.attackableNpcs().split(",")).map(String::trim).toArray(String[]::new)).collect(Collectors.toList()));

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run() || PlayerAssistPlugin.fulfillConditionsToRun()) return;
                if (!config.toggleSafeSpot()) return;

                minimumHealth = config.minimumHealthSafeSpot();
                currentSafeSpot = config.safeSpot();
                if (isDefaultSafeSpot(currentSafeSpot)) {
                    if (!messageShown) {
                        Microbot.showMessage("Please set a safe spot location");
                        messageShown = true;
                    }
                    return;
                }

                // Player health is less than minimum configured
                if (shouldRetreat() && !isPlayerAtSafeSpot(currentSafeSpot)) {
                    PlayerAssistPlugin.playerState = PlayerAssistState.RETREAT;
                    walkToSafeSpot();
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private void walkToSafeSpot() {
        Rs2Walker.walkFastCanvas(currentSafeSpot);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> isPlayerAtSafeSpot(currentSafeSpot));
        Microbot.pauseAllScripts = false;
    }

    private void attackNpcFromSafeSpot(PlayerAssistConfig config, List<String> npcsToAttack) {
        // check if there is an NPC targeting us
        List<NPC> npcList = Rs2Npc.getNpcsAttackingPlayer(Microbot.getClient().getLocalPlayer());

        // if there is an NPC interacting with us, and we are not Interacting with it, attack it again
        if (!npcList.isEmpty() && !Rs2Player.isInMulti()) {
            npcList.forEach(npc -> {
                if (Microbot.getClient().getLocalPlayer().getInteracting() == null) {
                    if (npcsToAttack.get(0).contains(Objects.requireNonNull(npc.getName()))) {
                        Rs2Npc.attack(npc);
                        PlayerAssistPlugin.setCooldown(config.playStyle().getRandomTickInterval());
                    }
                }
            });
        }
    }

    private boolean isDefaultSafeSpot(WorldPoint safeSpot) {
        return safeSpot.getX() == 0 && safeSpot.getY() == 0;
    }

    private boolean isPlayerAtSafeSpot(WorldPoint safeSpot) {
        return safeSpot.equals(Microbot.getClient().getLocalPlayer().getWorldLocation());
    }

    private boolean shouldRetreat() {
        int currentHp = Rs2Player.checkCurrentHealth();
        return minimumHealth >= currentHp;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
