package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.NpcID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.enums.AttackStyle;
import net.runelite.client.plugins.microbot.playerassist.model.Monster;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.runelite.api.NPC;

public class FlickerScript extends Script {

    List<Monster> monsters;
    public static List<Monster> currentMonstersAttackingUs = new ArrayList<>();

    AttackStyle prayFlickAttackStyle = null;

    public void init() {
        monsters = new ArrayList<>();
        monsters.add(new Monster(NpcID.GUARD_11947, 6, 426, AttackStyle.RANGED));
        monsters.add(new Monster(NpcID.GUARD_3271, 6, 395, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.FIRE_GIANT_2081, 5, 4667, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.FIRE_GIANT_2082, 5, 4667, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.FIRE_GIANT_2083, 5, 4666, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.FIRE_GIANT_2084, 5, 4667, AttackStyle.MELEE));

    }

    public boolean run(PlayerAssistConfig config) {
        init();
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!config.prayFlick()) return;
            try {

                List<NPC> npcs = Rs2Npc.getNpcsForPlayer();


                //keep track of which monsters still have aggro on the player
                for (Monster monster: currentMonstersAttackingUs) {
                    if (!npcs.stream().anyMatch(x -> x.getIndex() == monster.npc.getIndex()))
                        monster.delete = true;
                }

                currentMonstersAttackingUs = currentMonstersAttackingUs.stream().filter(x -> !x.delete).collect(Collectors.toList());

                for (NPC npc: npcs) {
                    boolean npcIsNotInTheList = currentMonstersAttackingUs.stream().anyMatch(x -> x.npc.getIndex() == npc.getIndex());
                    if (npc != null && !npcIsNotInTheList) {
                        Monster currentMonster = monsters.stream().filter(x -> x.id == npc.getId()).findFirst().orElse(null);
                        if (currentMonster == null) continue;
                        if (!npc.isDead() && npc.getAnimation() == currentMonster.attackAnimation) {
                            Monster monsterToAdd = new Monster(currentMonster.id, currentMonster.attackSpeed,
                                    currentMonster.attackAnimation, currentMonster.attackStyle);
                            monsterToAdd.npc = npc;
                            currentMonstersAttackingUs.add(monsterToAdd);
                        }
                    }
                }


                if (prayFlickAttackStyle != null) {
                    switch(prayFlickAttackStyle) {
                        case MAGE:
                            prayFlickAttackStyle = null;
                            Rs2Prayer.turnOnMagePrayer();
                            sleep(200);
                            Rs2Prayer.turnOffMagePrayer();
                            break;
                        case MELEE:
                            prayFlickAttackStyle = null;
                            Rs2Prayer.turnOnMeleePrayer();
                            sleep(200);
                            Rs2Prayer.turnOffMeleePrayer();
                            break;
                        case RANGED:
                            prayFlickAttackStyle = null;
                            Rs2Prayer.turnOnRangedPrayer();
                            sleep(200);
                            Rs2Prayer.turnOffRangedPrayer();
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


    public void onGameTick(GameTick gameTick) {
        if (!currentMonstersAttackingUs.isEmpty()) {
            for (Monster currentMonster: currentMonstersAttackingUs) {
                if (currentMonster.adjustableAttackSpeed > 0) {
                    currentMonster.adjustableAttackSpeed--;
                }

                if (currentMonster.adjustableAttackSpeed <= 0) {
                    currentMonster.adjustableAttackSpeed = currentMonster.attackSpeed;
                }
                if (currentMonster.adjustableAttackSpeed == 1) {
                    prayFlickAttackStyle = currentMonster.attackStyle;
                }
            }
        }
    }

    public void onNpcDespawned(NpcDespawned npcDespawned) {
        Monster monster = currentMonstersAttackingUs.stream()
                .filter(x -> x.npc.getIndex() == npcDespawned.getNpc().getIndex())
                .findFirst().orElse(null);

        if (monster != null) {
            prayFlickAttackStyle = monster.attackStyle;
            currentMonstersAttackingUs.remove(monster);
        }
    }

}
