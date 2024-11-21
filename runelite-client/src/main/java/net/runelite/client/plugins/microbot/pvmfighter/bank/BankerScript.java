package net.runelite.client.plugins.microbot.pvmfighter.bank;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.Rs2InventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterPlugin;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterState;
import net.runelite.client.plugins.microbot.pvmfighter.constants.Constants;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

enum ItemToKeep {
    TELEPORT(Constants.TELEPORT_IDS, PvmFighterConfig::ignoreTeleport, PvmFighterConfig::staminaValue),
    STAMINA(Constants.STAMINA_POTION_IDS, PvmFighterConfig::useStamina, PvmFighterConfig::staminaValue),
    PRAYER(Constants.PRAYER_RESTORE_POTION_IDS, PvmFighterConfig::usePrayer, PvmFighterConfig::prayerValue),
    FOOD(Constants.FOOD_ITEM_IDS, PvmFighterConfig::useFood, PvmFighterConfig::foodValue),
    ANTIPOISON(Constants.ANTI_POISON_POTION_IDS, PvmFighterConfig::useAntipoison, PvmFighterConfig::antipoisonValue),
    ANTIFIRE(Constants.ANTI_FIRE_POTION_IDS, PvmFighterConfig::useAntifire, PvmFighterConfig::antifireValue),
    COMBAT(Constants.STRENGTH_POTION_IDS, PvmFighterConfig::useCombat, PvmFighterConfig::combatValue),
    RESTORE(Constants.RESTORE_POTION_IDS, PvmFighterConfig::useRestore, PvmFighterConfig::restoreValue);

    private final List<Integer> ids;
    private final Function<PvmFighterConfig, Boolean> useConfig;
    private final Function<PvmFighterConfig, Integer> valueConfig;

    ItemToKeep(Set<Integer> ids, Function<PvmFighterConfig, Boolean> useConfig, Function<PvmFighterConfig, Integer> valueConfig) {
        this.ids = ids.stream().collect(Collectors.toList());
        this.useConfig = useConfig;
        this.valueConfig = valueConfig;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public boolean isEnabled(PvmFighterConfig config) {
        return useConfig.apply(config);
    }

    public int getValue(PvmFighterConfig config) {
        return valueConfig.apply(config);
    }
}

@Slf4j
public class BankerScript extends Script {
    boolean initialized = false;

    public void run(PvmFighterConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (PvmFighterPlugin.fulfillConditionsToRun()) return;
                if (!config.bank() || PvmFighterPlugin.playerState != PvmFighterState.BANKING) return;

                if (needsBanking(config)) {
                    walkToNearestBank();
                    handleBanking(config);
                } else {
                    walkToCombatZone(config);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    private void walkToNearestBank() {
        WorldPoint nearestBank = Rs2Bank.getNearestBank().getWorldPoint();
        Rs2Walker.walkTo(nearestBank, 8);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> nearestBank.equals(Rs2Player.getWorldLocation()));
        log.info("Walk to nearest bank method finished");
        Microbot.pauseAllScripts = false;
    }

    private void walkToCombatZone(PvmFighterConfig config) {
        WorldPoint combatZone = config.centerLocation();
        Rs2Walker.walkTo(combatZone, 8);
        Microbot.pauseAllScripts = true;
        sleepUntil(() -> combatZone.equals(Rs2Player.getWorldLocation()));
        Microbot.pauseAllScripts = false;
        PvmFighterPlugin.playerState = PvmFighterState.COMBAT;
    }

    public boolean goToBank() {
        return Rs2Walker.walkTo(Rs2Bank.getNearestBank().getWorldPoint(), 8);
    }

    public boolean needsBanking(PvmFighterConfig config) {
        return isUpkeepItemDepleted(config) || Rs2Inventory.getEmptySlots() <= config.minFreeSlots();
    }

    public boolean withdrawUpkeepItems(PvmFighterConfig config) {
        if (config.useInventorySetup()) {
            Rs2InventorySetup inventorySetup = new Rs2InventorySetup(config.inventorySetup(), mainScheduledFuture);
            if (!inventorySetup.doesEquipmentMatch()) {
                inventorySetup.loadEquipment();
            }
            inventorySetup.loadInventory();
            return true;
        }

        for (ItemToKeep item : ItemToKeep.values()) {
            if (item.isEnabled(config)) {
                int count = item.getIds().stream().mapToInt(Rs2Inventory::count).sum();
                log.info("Item: {} Count: {}", item.name(), count);
                if (count < item.getValue(config)) {
                    log.info("Withdrawing {} {}(s)", item.getValue(config) - count, item.name());
                    if (item.name().equals("FOOD")) {
                        for (Rs2Food food : Arrays.stream(Rs2Food.values()).sorted(Comparator.comparingInt(Rs2Food::getHeal).reversed()).collect(Collectors.toList())) {
                            log.info("Checking bank for food: {}", food.getName());
                            if (Rs2Bank.hasBankItem(food.getId(), item.getValue(config) - count)) {
                                Rs2Bank.withdrawX(true, food.getId(), item.getValue(config) - count);
                                break;
                            }
                        }
                    } else {
                        ArrayList<Integer> ids = new ArrayList<>(item.getIds());
                        Collections.reverse(ids);
                        for (int id : ids) {
                            log.info("Checking bank for item: {}", id);
                            if (Rs2Bank.hasBankItem(id, item.getValue(config) - count)) {
                                Rs2Bank.withdrawX(true, id, item.getValue(config) - count);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return !isUpkeepItemDepleted(config);
    }

    public boolean depositAllExcept(PvmFighterConfig config) {
        List<Integer> ids = Arrays.stream(ItemToKeep.values())
                .filter(item -> item.isEnabled(config))
                .flatMap(item -> item.getIds().stream())
                .collect(Collectors.toList());
        Rs2Bank.depositAllExcept(ids.toArray(new Integer[0]));
        return Rs2Bank.isOpen();
    }

    public boolean isUpkeepItemDepleted(PvmFighterConfig config) {
        return Arrays.stream(ItemToKeep.values())
                .filter(item -> item != ItemToKeep.TELEPORT && item.isEnabled(config))
                .anyMatch(item -> item.getIds().stream().mapToInt(Rs2Inventory::count).sum() == 0);
    }

    public void handleBanking(PvmFighterConfig config) {
        if (Rs2Bank.openBank()) {
            depositAllExcept(config);
            withdrawUpkeepItems(config);
        }
    }

    public void shutdown() {
        super.shutdown();
        // reset the initialized flag
        initialized = false;

    }
}
