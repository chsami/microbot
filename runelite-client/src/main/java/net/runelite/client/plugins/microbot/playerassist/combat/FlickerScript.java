package net.runelite.client.plugins.microbot.playerassist.combat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.enums.AttackStyle;
import net.runelite.client.plugins.microbot.playerassist.models.Monster;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class FlickerScript extends Script {

    private static ObjectMapper objectMapper = getDefaultObjectMapper();
    String fileName = "npc_defs.json";

    private static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper defaultObjectMapper = new ObjectMapper();

        return defaultObjectMapper;
    }

    List<Monster> monsters;
    public static List<Monster> currentMonsters = new ArrayList<>();

    AttackStyle enablePrayer = null;

    public void init() throws IOException {
        monsters = new ArrayList<>();
        monsters.add(new Monster(NpcID.GUARD_11916, 4, 386, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.GUARD_11917, 4, 386, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.GUARD_11915, 4, 386, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.GUARD_11911, 4, 386, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.GUARD_11943, 4, 386, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.GUARD_11946, 6, 395, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.GUARD_3271, 6, 395, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.GUARD_3269, 4, 386, AttackStyle.MELEE));
        monsters.add(new Monster(NpcID.GUARD_3274, 6, 426, AttackStyle.RANGED));
        monsters.add(new Monster(NpcID.GUARD_11947, 6, 426, AttackStyle.RANGED));
    }

    @SneakyThrows
    public boolean run(PlayerAssistConfig config) {
        init();
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!config.prayFlick()) return;
            try {

                List<NPC> npcs = Rs2Npc.getNpcsForActor(Microbot.getClient().getLocalPlayer());

                //keep track of which monsters still have aggro
                for (Monster monster : currentMonsters) {
                    if (!npcs.stream().anyMatch(x -> x.getIndex() == monster.npc.getIndex()))
                        monster.delete = true;
                }

                currentMonsters = currentMonsters.stream().filter(x -> !x.delete).collect(Collectors.toList());

                for (NPC npc : npcs) {
                    if (npc != null && !currentMonsters.stream().anyMatch(x -> x.npc.getIndex() == npc.getIndex())) {
                        Monster currentMonster = monsters.stream().filter(x -> x.id == npc.getId()).findFirst().orElse(null);
                        if (currentMonster == null) continue;
                        if (!npc.isDead() && npc.getAnimation() == currentMonster.attackAnimation) {
                            currentMonster.npc = npc;
                            currentMonster.adjustableAttackSpeed = currentMonster.attackSpeed;
                            currentMonsters.add(currentMonster);
                        }
                    }
                }
                if (enablePrayer != null) {
                    switch (enablePrayer) {
                        case MAGE:
                            enablePrayer = null;
                            Rs2Prayer.turnOnMagePrayer();
                            sleep(200);
                            Rs2Prayer.turnOffMagePrayer();
                            break;
                        case RANGED:
                            enablePrayer = null;
                            Rs2Prayer.turnOnRangePrayer();
                            sleep(200);
                            Rs2Prayer.turnOffRangedPrayer();
                            break;
                        case MELEE:
                            enablePrayer = null;
                            Rs2Prayer.turnOnMeleePrayer();
                            sleep(200);
                            Rs2Prayer.turnOffMeleePrayer();
                            break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutdown() {
        super.shutdown();
    }

    public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
    }

    public void onGameTick(GameTick gameTick) {
        if (!currentMonsters.isEmpty()) {
            for (Monster currentMonster : currentMonsters) {
                if (currentMonster.adjustableAttackSpeed > 0) {
                    currentMonster.adjustableAttackSpeed--;
                }

                if (currentMonster.adjustableAttackSpeed <= 0) {
                    currentMonster.adjustableAttackSpeed = currentMonster.attackSpeed;
                }
                if (currentMonster.adjustableAttackSpeed == 1) {
                    enablePrayer = currentMonster.attackStyle;
                }
            }
        }
    }

    public void onNpcDespawned(NpcDespawned npcDespawned) {
        Monster monster = currentMonsters.stream()
                .filter(x -> x.npc.getIndex() == npcDespawned.getNpc().getIndex()).findFirst()
                .orElse(null);

        if (monster != null) {
            currentMonsters.remove(monster);
        }
    }
}
