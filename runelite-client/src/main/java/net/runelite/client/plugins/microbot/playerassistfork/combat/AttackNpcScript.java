package net.runelite.client.plugins.microbot.playerassistfork.combat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassistfork.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassistfork.PlayerAssistPlugin;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class AttackNpcScript extends Script {

    public static Actor currentNpc = null;
    public static List<NPC> attackableNpcs = new ArrayList<>();
    private boolean messageShown = false;

    public static void skipNpc() {
        currentNpc = null;
    }

    public void run(PlayerAssistConfig config) {
        try {
            Rs2NpcManager.loadJson();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run() || PlayerAssistPlugin.fulfillConditionsToRun())
                    return;

                if (!config.toggleCombat()) return;

                List<String> npcsToAttack = Arrays.stream(config.attackableNpcs().split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());

                double healthPercentage = (double) Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100
                        / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
                if (Rs2Inventory.getInventoryFood().isEmpty() && healthPercentage < 10)
                    return;

                if (config.toggleCenterTile() && config.centerLocation().getX() == 0
                        && config.centerLocation().getY() == 0) {
                    if (!messageShown) {
                        Microbot.showMessage("Please set a center location");
                        messageShown = true;
                    }
                    return;
                }
                messageShown = false;

                attackableNpcs = Microbot.getClient().getNpcs().stream()
                        .filter(npc -> !npc.isDead()
                                && npc.getWorldLocation().distanceTo(config.centerLocation()) <= config.attackRadius()
                                && (npc.getInteracting() == null
                                || npc.getInteracting() == Microbot.getClient().getLocalPlayer())
                                && npcsToAttack.contains(npc.getName())
                                && Rs2Npc.hasLineOfSight(npc))
                        .sorted(Comparator
                                .comparing((NPC npc) -> npc.getInteracting() == Microbot.getClient().getLocalPlayer() ? 0 : 1)
                                .thenComparingInt(npc -> npc.getLocalLocation()
                                        .distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                        .collect(Collectors.toList());

                if (PlayerAssistPlugin.getCooldown() > 0 || Rs2Combat.inCombat())
                    return;

                if (!attackableNpcs.isEmpty()) {
                    NPC npc = attackableNpcs.get(0);

                    if (!Rs2Camera.isTileOnScreen(npc.getLocalLocation()))
                        Rs2Camera.turnTo(npc);

                    Microbot.pauseAllScripts = true;
                    Rs2Npc.interact(npc, "attack");
                    Microbot.status = "Attacking " + npc.getName();
                    // Wait until player initialized combat
                    sleepUntil(Rs2Combat::inCombat);
                    // Wait until player finished combat
                    sleepUntil(() -> !Rs2Combat.inCombat());
                    log.info("combat finished");
                    Microbot.pauseAllScripts = false;
                    PlayerAssistPlugin.setCooldown(config.playStyle().getRandomTickInterval());

//                    sleepUntil(Rs2Player::isInteracting, 1000);
//                    sleepUntil(() -> Microbot.getClient().getLocalPlayer().isInteracting()
//                            && Microbot.getClient().getLocalPlayer().getInteracting() instanceof NPC);

//                    if (config.togglePrayer()) {
//                        if (!config.toggleQuickPray()) {
//                            AttackStyle attackStyle = AttackStyleMapper
//                                    .mapToAttackStyle(Rs2NpcManager.getAttackStyle(npc.getId()));
//                            if (attackStyle != null) {
//                                switch (attackStyle) {
//                                    case MAGE:
//                                        Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
//                                        break;
//                                    case MELEE:
//                                        Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
//                                        break;
//                                    case RANGED:
//                                        Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
//                                        break;
//                                }
//                            }
//                        } else {
//                            Rs2Prayer.toggleQuickPrayer(true);
//                        }
//                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        super.shutdown();
    }
}
