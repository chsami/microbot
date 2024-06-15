package net.runelite.client.plugins.microbot.playerassist.bank;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.playerassist.constants.Constants;
import net.runelite.client.plugins.microbot.util.MicrobotInventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

enum ItemToKeep {
    TELEPORT(Constants.TELEPORT_IDS, PlayerAssistConfig::ignoreTeleport, PlayerAssistConfig::staminaValue),
    STAMINA(Constants.STAMINA_POTION_IDS, PlayerAssistConfig::useStamina, PlayerAssistConfig::staminaValue),
    PRAYER(Constants.PRAYER_RESTORE_POTION_IDS, PlayerAssistConfig::usePrayer, PlayerAssistConfig::prayerValue),
    FOOD(Constants.FOOD_ITEM_IDS, PlayerAssistConfig::useFood, PlayerAssistConfig::foodValue),
    ANTIPOISON(Constants.ANTI_POISON_POTION_IDS, PlayerAssistConfig::useAntipoison, PlayerAssistConfig::antipoisonValue),
    ANTIFIRE(Constants.ANTI_FIRE_POTION_IDS, PlayerAssistConfig::useAntifire, PlayerAssistConfig::antifireValue),
    COMBAT(Constants.STRENGTH_POTION_IDS, PlayerAssistConfig::useCombat, PlayerAssistConfig::combatValue),
    RESTORE(Constants.RESTORE_POTION_IDS, PlayerAssistConfig::useRestore, PlayerAssistConfig::restoreValue);

    private final List<Integer> ids;
    private final Function<PlayerAssistConfig, Boolean> useConfig;
    private final Function<PlayerAssistConfig, Integer> valueConfig;

    ItemToKeep(Set<Integer> ids, Function<PlayerAssistConfig, Boolean> useConfig, Function<PlayerAssistConfig, Integer> valueConfig) {
        this.ids = ids.stream().collect(Collectors.toList());
        this.useConfig = useConfig;
        this.valueConfig = valueConfig;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public boolean isEnabled(PlayerAssistConfig config) {
        return useConfig.apply(config);
    }

    public int getValue(PlayerAssistConfig config) {
        return valueConfig.apply(config);
    }
}

@Slf4j
public class BankerScript extends Script {
    PlayerAssistConfig config;


    boolean initialized = false;

    public boolean run(PlayerAssistConfig config) {
        this.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!config.bank()) return;
                if (Rs2Player.isMoving() || Rs2Player.isAnimating()) return;
                if (isUpkeepItemDepleted(config) || Rs2Inventory.count() >= 28 - config.minFreeSlots())
                    if (handleBanking())
                        Microbot.pauseAllScripts = false;
                // Add other conditional checks here as needed
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }


    //check inventory for items and how many of each item
    public boolean withdrawUpkeepItems(PlayerAssistConfig config) {
        if (config.useInventorySetup() && !MicrobotInventorySetup.doesEquipmentMatch(config.inventorySetup())) {
            MicrobotInventorySetup.loadEquipment(config.inventorySetup(), mainScheduledFuture);
        }
        // use inventory setup if enabled
        if (config.useInventorySetup()) {
            MicrobotInventorySetup.loadInventory(config.inventorySetup(), mainScheduledFuture);
            return true;
        }

        // check for items in inventory
        for (ItemToKeep item : ItemToKeep.values()) {
            if (item.isEnabled(config)) {
                int count = item.getIds().stream().mapToInt(Rs2Inventory::count).sum();
                log.info("Item: {} Count: {}", item.name(), count);
                if (count < item.getValue(config)) {
                    // withdraw item
                    // log what we are withdrawing and how many
                    log.info("Withdrawing {} {}(s)", item.getValue(config) - count, item.name());
                    List<Integer> sortedIds = item.getIds().stream()
                            .sorted(Comparator.reverseOrder())
                            .collect(Collectors.toList());
                    for (int id : sortedIds) {
                        log.info("Checking bank for item: {}", id);
                        if (Rs2Bank.hasBankItem(id, item.getValue(config) - count)) {
                            Rs2Bank.withdrawX(true, id, item.getValue(config) - count);
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }

    //Deposit all items except for the ones we want to keep
    public boolean depositAllExcept(PlayerAssistConfig config) {
        List<Integer> ids = new ArrayList<>();
        for (ItemToKeep item : ItemToKeep.values()) {
            if (item.isEnabled(config)) {
                //Convert the list of ids to a ArrayList
                for (int id : item.getIds()) ids.add(id);

            }
        }
        return Rs2Bank.depositAllExcept(ids.toArray(new Integer[0]));

    }

    //If any of the items we want to keep are completely depleted
    public boolean isUpkeepItemDepleted(PlayerAssistConfig config) {
        for (ItemToKeep item : ItemToKeep.values()) {
            if (item == ItemToKeep.TELEPORT) continue;
            if (item.isEnabled(config)) {
                int count = item.getIds().stream().mapToInt(Rs2Inventory::count).sum();
                if (count == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check if any upkeep items are missing based on InventorySetup

    public boolean goToBank() {
        WorldPoint bankPoint = Rs2Bank.getNearestBank().getWorldPoint();
        return Rs2Walker.walkTo(bankPoint, 6);
    }

    // execute banking actions
    public boolean handleBanking() {
        Microbot.pauseAllScripts = true;
        if (goToBank()) {
            if (Rs2Bank.openBank()) {
                if (depositAllExcept(config)) {

                }

                if (withdrawUpkeepItems(config)) {
                    Rs2Walker.walkTo(config.centerLocation());
                }
            }
        }
        return true;
    }

    public void shutdown() {
        super.shutdown();
        // reset the initialized flag
        initialized = false;

    }
}
