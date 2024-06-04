package net.runelite.client.plugins.microbot.scurrius;

import net.runelite.api.ObjectID;
import net.runelite.api.Projectile;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.scurrius.enums.State;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class ScurriusScript extends Script {
    public static double version = 1.0;

    final WorldPoint bossLocation = new WorldPoint(3279, 9869, 0);

    private static final int FALLING_ROCKS = 2644;

    final List<Integer> scurriusNpcIds = List.of(7221, 7222);

    public static State state = State.BANKING;

    net.runelite.api.NPC scurrius = null;

    public boolean run(ScurriusConfig config) {
        Microbot.enableAutoRunOn = true;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                for (int scurriusNpcId: scurriusNpcIds) {
                    scurrius = Rs2Npc.getNpc(scurriusNpcId);
                    if (scurrius != null) break;
                }

                boolean hasFood = !Rs2Inventory.getInventoryFood().isEmpty();
                boolean hasPrayerPotions = Rs2Inventory.hasItem("prayer potion");
                boolean isScurriusPresent = scurrius != null;
                boolean hasLineOfSightWithScurrius = Rs2Npc.hasLineOfSight(scurrius);

                if (!isScurriusPresent && !hasFood && !hasPrayerPotions) {
                    state = State.BANKING;
                }

                if (isScurriusPresent && hasFood && hasLineOfSightWithScurrius) {
                    state = State.FIGHTING;
                }

                if (isScurriusPresent && !hasFood && Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) < 25) {
                    state = State.TELEPORT_AWAY;
                }

                if ((!isScurriusPresent || !hasLineOfSightWithScurrius) && hasFood && hasPrayerPotions) {
                    state = State.WALK_TO_BOSS;
                }

                switch(state) {
                    case BANKING:
                        boolean isCloseToBank = Rs2Bank.walkToBank();
                        if (isCloseToBank) {
                            Rs2Bank.useBank();
                        }
                        if (Rs2Bank.isOpen()) {
                            Rs2Bank.withdrawX(true, "shark", 20);
                            Rs2Bank.withdrawX(true,"prayer potion(3)", 2);
                            Rs2Bank.withdrawX(true, "varrock teleport", 1);
                        }
                        break;
                    case FIGHTING:
                        List<WorldPoint> dangerousWorldPoints = Rs2Tile.getDangerousGraphicsObjectTiles().stream().map(x -> x.getKey()).collect(Collectors.toList());

                        for (WorldPoint worldPoint: dangerousWorldPoints) {
                            if (Rs2Player.getWorldLocation().equals(worldPoint)) {
                                final WorldPoint safeTile = Rs2Tile.getSafeTile();
                                Rs2Walker.walkFastCanvas(safeTile);
                            }
                        }

                        Rs2Player.eatAt(50);
                        Rs2Player.drinkPrayerPotionAt(20);

                        boolean didWeAttackAGiantRat = scurrius != null && config.prioritizeRats() && Rs2Npc.attack("giant rat");

                        if (didWeAttackAGiantRat)
                            return;


                        if (!Microbot.getClient().getLocalPlayer().isInteracting()) {
                            Rs2Npc.attack(scurrius);
                        }
                        break;
                    case TELEPORT_AWAY:
                        if (Rs2Inventory.getInventoryFood().isEmpty()) {
                            if (scurrius != null) {
                                Rs2Inventory.interact("varrock teleport", "break");
                            }
                        }
                        break;
                    case WALK_TO_BOSS:
                        Rs2Walker.walkTo(bossLocation);
                        Rs2GameObject.interact(ObjectID.BROKEN_BARS, "Climb through (private)");
                        break;
                }


                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 400, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }


    /**
     * Pray against mage & range attacks
     *
     * @param projectile
     */
    public void prayAgainstProjectiles(Projectile projectile) {
        if (projectile.getId() == 2642) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
        } else if (projectile.getId() == 2640) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
        }
        Microbot.getClientThread().runOnSeperateThread(() -> {
            sleep(1500);
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
            return true;
        });
    }
}
