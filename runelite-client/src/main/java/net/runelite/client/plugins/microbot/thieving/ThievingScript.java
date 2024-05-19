package net.runelite.client.plugins.microbot.thieving;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.thieving.enums.ThievingNpc;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.timers.TimersPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class ThievingScript extends Script {

    public static double version = 1.5;
    ThievingConfig config;

    public boolean run(ThievingConfig config) {
        this.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                List<Rs2Item> foods = Microbot.getClientThread().runOnClientThread(Rs2Inventory::getInventoryFood);

                if (foods.isEmpty()) {
                    bank();
                    return;
                }
                if (Rs2Inventory.isFull()) {
                    dropItems();
                }
                openCoinPouches(config);
                wearDodgyNecklace();
                pickpocket();
                Rs2Player.eatAt(config.hitpoints());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private void handleElves() {
        List<String> names = Arrays.asList(
                "Anaire", "Aranwe", "Aredhel", "Caranthir", "Celebrian", "Celegorm",
                "Cirdan", "Curufin", "Earwen", "Edrahil", "Elenwe", "Elladan", "Enel",
                "Erestor", "Enerdhil", "Enelye", "Feanor", "Findis", "Finduilas",
                "Fingolfin", "Fingon", "Galathil", "Gelmir", "Glorfindel", "Guilin",
                "Hendor", "Idril", "Imin", "Iminye", "Indis", "Ingwe", "Ingwion",
                "Lenwe", "Lindir", "Maeglin", "Mahtan", "Miriel", "Mithrellas",
                "Nellas", "Nerdanel", "Nimloth", "Oropher", "Orophin", "Saeros",
                "Salgant", "Tatie", "Thingol", "Turgon", "Vaire"
        );
        net.runelite.api.NPC npc = Rs2Npc.getNpcs()
                .filter(x -> names.stream()
                        .anyMatch(n -> n.equalsIgnoreCase(x.getName())))
                .findFirst()
                .orElse(null);
        if (npc != null) {
            if (Rs2Npc.pickpocket(npc)) {
                Microbot.status = "Pickpocketting " + npc.getName();
                sleep(300, 600);
            }
        }
    }

    private void openCoinPouches(ThievingConfig config) {
        if (Rs2Inventory.hasItemAmount("coin pouch", config.coinPouchTreshHold(), true)) {
            Rs2Inventory.interact("coin pouch", "open-all");
        }
    }

    private void wearDodgyNecklace() {
        if (!Rs2Equipment.isWearing("dodgy necklace")) {
            Rs2Inventory.wield("dodgy necklace");
        }
    }

    private void pickpocket() {
        if (config.THIEVING_NPC() != ThievingNpc.NONE) {
            sleepUntil(() -> TimersPlugin.t == null || !TimersPlugin.t.render());
            if (config.THIEVING_NPC() == ThievingNpc.ELVES) {
                handleElves();
            } else {
                if (Rs2Npc.pickpocket(config.THIEVING_NPC().getName())) {
                    sleep(50, 250);
                }
            }
        }
    }

    private void bank() {
        Microbot.status = "Getting food from bank...";
        if (Rs2Bank.walkToBank()) {
            dropItems();
            boolean isBankOpen = Rs2Bank.useBank();
            if (!isBankOpen) return;
            Rs2Bank.depositAll();
            Rs2Bank.withdrawX(true, config.food().getName(), config.foodAmount(), true);
            Rs2Bank.withdrawX(true, "dodgy necklace", config.dodgyNecklaceAmount());
            Rs2Bank.closeBank();
        }
    }

    private void dropItems() {
        List<String> doNotDropItemList = Arrays.stream(config.DoNotDropItemList().split(",")).collect(Collectors.toList());
        doNotDropItemList.add(config.food().getName());
        doNotDropItemList.add("dodgy necklace");
        Rs2Inventory.dropAllExcept(config.keepItemsAboveValue(), doNotDropItemList);
    }
}
