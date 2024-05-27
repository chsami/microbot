/*
 * Copyright (c) 2023, Mocrosoft <https://github.com/chsami>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.microbot.farming;

import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.farming.enums.FarmingState;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class FarmingScript extends Script {

    public static double version = 1.0;

    public List<ItemRequirement> itemsRequired = new ArrayList();

    public static FarmingState state = FarmingState.RESET;

    WorldPoint TREE_RUN_GNOME_STRONDHOLD = new WorldPoint(2437, 3418, 0);
    WorldPoint SPIRIT_TREE_GRAND_EXCHANGE = new WorldPoint(3185, 3507, 0);
    WorldPoint SPIRIT_TREE_GNOME_STRONGHOLD = new WorldPoint(2461, 3443, 0);

    WorldPoint TREE_RUN_VARROCK = new WorldPoint(3226, 3458, 0);

    WorldPoint TREE_RUN_LUMBRIDGE = new WorldPoint(3193, 3228, 0);
    WorldPoint TREE_RUN_FALADOR = new WorldPoint(3003, 3376, 0);

    WorldArea grandExchange = new WorldArea(3142, 3470, 50, 50, 0);
    BooleanSupplier isInGrandExchange = () -> grandExchange.contains(Microbot.getClient().getLocalPlayer().getWorldLocation());

    int sleepBetweenTeleports = 4000;

    public boolean run(FarmingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (state == FarmingState.RESET) {
                    if (!isInGrandExchange.getAsBoolean()) {
                        Microbot.getNotifier().notify("Start the script in the grand exchange.");
                        shutdown();
                    }
                    /*itemsRequired = new ArrayList<>(Arrays.asList(new ItemRequirement("falador teleport", ItemID.FALADOR_TELEPORT, 1),
                            new ItemRequirement("lumbridge teleport", ItemID.LUMBRIDGE_TELEPORT, 1),
                            new ItemRequirement("spade", ItemID.SPADE, 1),
                            new ItemRequirement("rake", ItemID.RAKE, 1),
                            new ItemRequirement("stamina potion(4)", ItemID.STAMINA_POTION4, 1),
                            new ItemRequirement("varrock teleport", ItemID.VARROCK_TELEPORT, 2),
                            new ItemRequirement("oak sapling", ItemID.OAK_SAPLING, 4),
                            new ItemRequirement("tomatoes(5)", ItemID.TOMATOES5, 4),
                            new ItemRequirement("coins", ItemID.COINS_995, 10000)));
                    if (!hasItems(itemsRequired)) {
                        openBank();
                        depositAll();
                        state = FarmingState.BANKING;
                    } else {
                        state = FarmingState.FARMING_GNOME_STRONHOLD;
                    }*/
                } else if (state == FarmingState.BANKING) {

                  /*  boolean hasItems = Rs2Bank.withdrawItemsRequired(itemsRequired);

                    if (!hasItems)
                        return;*/

                    state = FarmingState.FARMING_GNOME_STRONHOLD;

                } else if (state == FarmingState.FARMING_GNOME_STRONHOLD) {
                    if (isInGrandExchange.getAsBoolean()) {
                        boolean isNearSpiritTreeGrandExchange = Rs2Walker.walkTo(SPIRIT_TREE_GRAND_EXCHANGE);

                        if (!isNearSpiritTreeGrandExchange)
                            return;

                        //extract this to a general spirit tree teleport option

                        Rs2GameObject.interact(1295, "travel");

                        sleepUntil(() -> Rs2Widget.hasWidget("spirit tree locations"));

                        Rs2Keyboard.typeString("2");

                        sleepUntil(() -> !isInGrandExchange.getAsBoolean());

                        //extract this to a general spirit tree teleport option


                    } else {
                        boolean isNearTreePatchGnomeStronghold = Rs2Walker.walkTo(TREE_RUN_GNOME_STRONDHOLD);
                        if (!isNearTreePatchGnomeStronghold)
                            return;

                        if (plantTree(config, 19147, NpcID.PRISSY_SCILLA, 3, FarmingState.FARMING_VARROCK)) {
                        }
                    }
                } else if (state == FarmingState.FARMING_VARROCK) {
                    if (Rs2Inventory.hasItemAmount("varrock teleport", 2, true)) {
                        Rs2Inventory.interact(ItemID.VARROCK_TELEPORT, "break");
                        sleep(sleepBetweenTeleports);
                        return;
                    }
                    boolean isNearVarrockPatch = Rs2Walker.walkTo(TREE_RUN_VARROCK);

                    if (!isNearVarrockPatch)
                        return;

                    if (plantTree(config, 8390, NpcID.TREZNOR_11957, 2, FarmingState.FARMING_FALADOR)) {
                    }
                } else if (state == FarmingState.FARMING_FALADOR) {
                    if (Rs2Inventory.hasItem(ItemID.FALADOR_TELEPORT)) {
                        Rs2Inventory.interact(ItemID.FALADOR_TELEPORT, "break");
                        sleep(sleepBetweenTeleports);
                        return;
                    }
                    boolean isNearFaladorPatch = Rs2Walker.walkTo(TREE_RUN_FALADOR);

                    if (!isNearFaladorPatch)
                        return;

                    if (plantTree(config, 8389, NpcID.HESKEL, 1, FarmingState.FARMING_LUMBRIDGE)) {
                    }

                } else if (state == FarmingState.FARMING_LUMBRIDGE) {
                    if (Rs2Inventory.hasItem(ItemID.LUMBRIDGE_TELEPORT)) {
                        Rs2Inventory.interact(ItemID.LUMBRIDGE_TELEPORT, "break");
                        sleep(sleepBetweenTeleports);
                        return;
                    }

                    boolean isNearLumbridgePatch = Rs2Walker.walkTo(TREE_RUN_LUMBRIDGE);

                    if (!isNearLumbridgePatch)
                        return;

                    if (plantTree(config, 8391, NpcID.FAYETH, 0, FarmingState.FINISHED)) {
                    }
                } else if (state == FarmingState.FINISHED) {
                    if (Rs2Inventory.hasItem(ItemID.VARROCK_TELEPORT)) {
                        Rs2Inventory.interact(ItemID.VARROCK_TELEPORT, "break");
                        sleep(sleepBetweenTeleports);
                        return;
                    }

                    boolean isNearGeBank = Rs2Walker.walkTo(BankLocation.GRAND_EXCHANGE.getWorldPoint()); //walk to ge bank

                    if (isNearGeBank) {
                        shutdown();
                    }

                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean plantTree(FarmingConfig config, int objectId, int npcId, int totalItemsForNextStep, FarmingState nextState) {
        try {
            final ObjectComposition tree = Rs2GameObject.findObjectComposition(objectId);
            if (tree != null && tree.getImpostor().getName().equalsIgnoreCase(config.farmingMaterial().getName())) {
                if (Rs2GameObject.hasAction(tree, "check-health")) {
                    Rs2GameObject.interact(objectId, "check-health");
                    int currentFarmingExp = Microbot.getClient().getSkillExperience(Skill.FARMING);
                    sleepUntilOnClientThread(() -> currentFarmingExp != Microbot.getClient().getSkillExperience(Skill.FARMING));
                } else if (Rs2GameObject.hasAction(tree, "chop down")) {
                    Rs2Npc.interact(npcId, "pay");
                    sleepUntil(() -> Rs2Widget.hasWidget("pay 200 coins"));
                    Rs2Keyboard.typeString("1");
                    // sleepUntil(() -> !Rs2GameObject.hasAction(tree, "chop down")); -> this crashes the client for some reason
                } else {
                    //pay protect tree

                    if (tree != null
                            && !Rs2GameObject.hasAction(tree, "chop down")
                            && !Rs2GameObject.hasAction(tree, "check-health")
                            && tree.getImpostor().getName().equalsIgnoreCase(config.farmingMaterial().getName())) {
                        Rs2Npc.interact(npcId, "pay");
                        sleepUntil(() -> Rs2Inventory.hasItemAmount(config.farmingMaterial().getProtectionItem(), totalItemsForNextStep, true));
                    }
                }
            } else {
                //rake patch & plant tree
                if (!RakeAndPlantTree(objectId, config.farmingMaterial().getItemName(), tree)) return true;
            }

            if (Rs2Inventory.hasItemAmount(config.farmingMaterial().getItemName(), totalItemsForNextStep, false, true) &&
                    Rs2Inventory.hasItemAmount(config.farmingMaterial().getProtectionItem(), totalItemsForNextStep, false, true)) {
                //new state
                state = nextState;
            }
            return false;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    private boolean RakeAndPlantTree(int patchId, String treeToPlant, ObjectComposition tree) {

        if (Microbot.isAnimating()) return false;

        GameObject farmingPatch = Rs2GameObject.findObjectByImposter(patchId, "rake");
        if (farmingPatch != null) {
            Rs2GameObject.interact(farmingPatch, "rake");
            sleep(2000);
            sleepUntil(() -> !Microbot.isAnimating());
        } else {
            Rs2Inventory.use(treeToPlant);
            boolean success = Rs2GameObject.interact(patchId);
            if (success) {
                sleepUntil(() -> tree != null);
            }
        }
        Rs2Inventory.dropAll("weeds");
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        state = FarmingState.RESET;
    }
}
