package net.runelite.client.plugins.microbot.thieving;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.thieving.enums.ThievingNpc;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.timers.TimersPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment.getEquippedItem;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class ThievingScript extends Script {

    public static double version = 1.2;

    public boolean run(ThievingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                List<Rs2Item> foods = Microbot.getClientThread().runOnClientThread(Rs2Inventory::getInventoryFood);
                if (foods.isEmpty()) {
                    Microbot.status = "Getting food from bank...";
                    if (Rs2Bank.walkToBank()) {
                        Rs2Bank.useBank();
                        Rs2Bank.withdrawX(true, config.food().getName(), config.foodAmount(), true);
                        final Rs2Item amulet = getEquippedItem(EquipmentInventorySlot.AMULET);
                        if (!Rs2Equipment.isWearing("dodgy necklace")) {
                            Rs2Bank.withdrawItem(true, "dodgy necklace");
                        }
                        Rs2Bank.closeBank();
                        sleep(1000, 2000);
                        Rs2Inventory.wield("dodgy necklace");
                    }
                    return;
                }
                if (Rs2Inventory.isFull()) {
                    Rs2Inventory.dropAllExcept(config.keepItemsAboveValue(), true);
                }
                openCoinPouches(config);
                if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > config.hitpoints()) {
                    if (config.THIEVING_NPC() != ThievingNpc.NONE) {
                        if (random(1, 10) == 2)
                            sleepUntil(() -> TimersPlugin.t == null || !TimersPlugin.t.render());
                        if (config.THIEVING_NPC() == ThievingNpc.ELVES) {
                            handleElves();
                        } else {
                            if (Rs2Npc.pickpocket(config.THIEVING_NPC().getName())) {
                                Microbot.status = "Pickpocketting " + config.THIEVING_NPC().getName();
                                sleep(300, 600);
                            }
                        }

                    }
                } else {
                    for (Rs2Item food : foods) {
                        Rs2Inventory.interact(food, "eat");
                        if (random(1, 10) == 2) { //double eat
                            Rs2Inventory.interact(food, "eat");
                        }
                        break;
                    }
                }
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
}
