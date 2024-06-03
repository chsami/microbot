package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.NPC;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.enums.AttackStyle;
import net.runelite.client.plugins.microbot.playerassist.model.Monster;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager.attackStyleMap;

public class FlickerScript extends Script {

    List<Monster> monsters = new ArrayList<>();
    public static List<Monster> currentMonstersAttackingUs = new ArrayList<>();

    AttackStyle prayFlickAttackStyle = null;

    public boolean run(PlayerAssistConfig config) {
        monsters.add(new Monster(3274, 426));
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                List<NPC> npcs = Rs2Npc.getNpcsForPlayer();


                //keep track of which monsters still have aggro on the player
                for (Monster monster : currentMonstersAttackingUs) {
                    if (!npcs.stream().anyMatch(x -> x.getIndex() == monster.npc.getIndex()))
                        monster.delete = true;
                }

                currentMonstersAttackingUs = currentMonstersAttackingUs.stream().filter(x -> !x.delete).collect(Collectors.toList());

                for (NPC npc : npcs) {
                    Monster currentMonster = currentMonstersAttackingUs.stream().filter(x -> x.npc.getIndex() == npc.getIndex()).findFirst().orElse(null);
                    String attackAnimation = Rs2NpcManager.attackAnimationMap.get(npc.getId());
                    if (attackAnimation == null || attackAnimation.isEmpty()) continue;

                    if (currentMonster != null) {
                        if (!npc.isDead() && npc.getAnimation() == Integer.parseInt(attackAnimation) && currentMonster.lastAttack <= 0)
                            currentMonster.lastAttack = currentMonster.rs2NpcStats.getAttackSpeed();
                        if (currentMonster.lastAttack <= -currentMonster.rs2NpcStats.getAttackSpeed() / 2)
                            currentMonstersAttackingUs.remove(currentMonster);
                    } else {
                        if (!npc.isDead() && npc.getAnimation() == Integer.parseInt(attackAnimation)) {
                            Monster monsterToAdd = new Monster(npc, Objects.requireNonNull(Rs2NpcManager.getStats(npc.getId())));
                            currentMonstersAttackingUs.add(monsterToAdd);
                        }
                    }
                }
                if (prayFlickAttackStyle != null) {
                    switch (prayFlickAttackStyle) {
                        case MAGE:
                            prayFlickAttackStyle = null;
                            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
                            sleep(400);
                            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, false);
                            break;
                        case MELEE:
                            prayFlickAttackStyle = null;
                            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
                            sleep(400);
                            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, false);
                            break;
                        case RANGED:
                            prayFlickAttackStyle = null;
                            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
                            sleep(400);
                            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, false);
                            break;
                    }
                }


            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutdown() {
        super.shutdown();
    }


    public void onGameTick() {
        if (!currentMonstersAttackingUs.isEmpty()) {
            for (Monster currentMonster : currentMonstersAttackingUs) {
                currentMonster.lastAttack--;
                String attackStyle = attackStyleMap.get(currentMonster.npc.getId());
                if (currentMonster.lastAttack == 1) {
                    prayFlickAttackStyle = attackStyle.equalsIgnoreCase("magic") ? AttackStyle.MAGE :
                            attackStyle.equalsIgnoreCase("ranged") ? AttackStyle.RANGED : AttackStyle.MELEE;
                }
            }
        }
    }

    public void onNpcDespawned(NpcDespawned npcDespawned) {
        Monster monster = currentMonstersAttackingUs.stream()
                .filter(x -> x.npc.getIndex() == npcDespawned.getNpc().getIndex())
                .findFirst().orElse(null);

        if (monster != null) {
            currentMonstersAttackingUs.remove(monster);
        }
    }

}
