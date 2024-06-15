package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.enums.AttackStyle;
import net.runelite.client.plugins.microbot.playerassist.enums.AttackStyleMapper;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class AttackNpcScript extends Script {

    String[] configAttackableNpcs;

    public static Actor currentNpc = null;

    public static List<NPC> attackableNpcs = new ArrayList();

    boolean clicked = false;

    boolean messageShown = false;

    public void run(PlayerAssistConfig config) {
        Rs2NpcManager.loadJson();
        AtomicReference<List<String>> npcsToAttack = new AtomicReference<>(Arrays.stream(Arrays.stream(config.attackableNpcs().split(",")).map(String::trim).toArray(String[]::new)).collect(Collectors.toList()));
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (!config.toggleCombat()) return;
                npcsToAttack.set(Arrays.stream(Arrays.stream(config.attackableNpcs().split(",")).map(String::trim).toArray(String[]::new)).collect(Collectors.toList()));
                double treshHold = (double) (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100) / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
                if (Rs2Inventory.getInventoryFood().isEmpty() && treshHold < 10) return;
                if (config.centerLocation().getX() == 0 && config.centerLocation().getY() == 0 && config.toggleCenterTile()) {
                    if(!messageShown){
                        Microbot.showMessage("Please set a center location");
                        messageShown = true;
                    }

                return;
                }
                messageShown = false;

                attackableNpcs = Microbot.getClient().getNpcs().stream()
                        .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                        .filter(x -> !x.isDead()
                                && x.getWorldLocation().distanceTo(config.centerLocation()) < config.attackRadius()
                                && (x.getInteracting() == null || x.getInteracting() == Microbot.getClient().getLocalPlayer())
                                && x.getAnimation() == -1
                                && npcsToAttack.get().stream().anyMatch(n -> n.equalsIgnoreCase(x.getName())))
                        .collect(Collectors.toList());
                if (Rs2Combat.inCombat()) {
                    return;
                }
                for (NPC npc : attackableNpcs) {
                    if (npc == null
                            || npc.getAnimation() != -1
                            || npc.isDead()
                            || (npc.getInteracting() != null && npc.getInteracting() != Microbot.getClient().getLocalPlayer())
                            || npcsToAttack.get().stream().noneMatch(n -> npc.getName().equalsIgnoreCase(n)))
                        break;
                    if (npc.getWorldLocation().distanceTo(config.centerLocation()) > config.attackRadius())
                        break;
                    if (!Rs2Camera.isTileOnScreen(npc.getLocalLocation()))
                        Rs2Camera.turnTo(npc);

                    if (!Rs2Npc.hasLineOfSight(npc))
                        continue;


                    if(config.togglePrayer() && !config.toggleQuickPrayFlick()){
                        AttackStyle attackStyle = AttackStyleMapper.mapToAttackStyle(Rs2NpcManager.getAttackStyle(npc.getId()));
                        if (attackStyle != null) {
                            switch (attackStyle) {
                                case MAGE:

                                    Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
                                    break;
                                case MELEE:
                                    Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
                                    break;
                                case RANGED:
                                    Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
                                    break;
                            }

                        }
                    }
                    if(config.togglePrayer() && config.toggleQuickPrayFlick()){
                        Rs2Prayer.toggleQuickPrayer(true);
                    }
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