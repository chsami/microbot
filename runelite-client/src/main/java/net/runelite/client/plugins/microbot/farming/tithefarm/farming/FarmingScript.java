package net.runelite.client.plugins.microbot.farming.tithefarm.farming;

import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.farming.tithefarm.farming.enums.FarmingState;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static net.runelite.client.plugins.microbot.util.bank.Rs2Bank.*;

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


    public boolean run(FarmingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (state == FarmingState.RESET) {
                    itemsRequired = new ArrayList<>(Arrays.asList(new ItemRequirement("falador teleport", ItemID.FALADOR_TELEPORT, 1),
                            new ItemRequirement("lumbridge teleport", ItemID.LUMBRIDGE_TELEPORT, 1),
                            new ItemRequirement("varrock teleport", ItemID.VARROCK_TELEPORT, 1),
                            new ItemRequirement("spade", ItemID.SPADE, 1),
                            new ItemRequirement("rake", ItemID.RAKE, 1),
                            new ItemRequirement("stamina potion(4)", ItemID.STAMINA_POTION4, 1),
                            new ItemRequirement("oak sapling", ItemID.OAK_SAPLING, 4),
                            new ItemRequirement("coins", ItemID.COINS_995, 10000)));
                    if (!hasItems(itemsRequired)) {
                        openBank();
                        depositAll();
                        state = FarmingState.BANKING;
                    } else {
                        state = FarmingState.FARMING_GNOME_STRONHOLD;
                    }
                } else if (state == FarmingState.BANKING) {

                    boolean hasItems = Rs2Bank.withdrawItemsRequired(itemsRequired);

                    if (!hasItems)
                        return;

                    state = FarmingState.FARMING_GNOME_STRONHOLD;

                } else if (state == FarmingState.FARMING_GNOME_STRONHOLD) {

                    WorldArea grandExchange = new WorldArea(3142, 3470, 50, 50, 0);
                    BooleanSupplier isInGrandExchange = () -> grandExchange.contains(Microbot.getClient().getLocalPlayer().getWorldLocation());

                    if (isInGrandExchange.getAsBoolean()) {
                        boolean isNearSpiritTreeGrandExchange = Microbot.getWalker().walkTo(SPIRIT_TREE_GRAND_EXCHANGE, false, false);

                        if (!isNearSpiritTreeGrandExchange)
                            return;

                        Rs2GameObject.interact(1295, "travel");

                        sleepUntil(() -> Rs2Widget.hasWidget("spirit tree locations"));

                        VirtualKeyboard.typeString("2");

                        sleepUntil(() -> !isInGrandExchange.getAsBoolean());

                    } else {
                        boolean isNearTreePatchGnomeStronghold = Microbot.getWalker().walkTo(TREE_RUN_GNOME_STRONDHOLD, true, false);
                        if (!isNearTreePatchGnomeStronghold)
                            return;


                        if (Inventory.hasItemAmount(config.farmingMaterial().getItemName(), 3) && Inventory.hasItemAmountStackable(config.farmingMaterial().getProtectionItem(), 3)) {
                            //new state
                        }

                        //rake patch & plant tree
                        if (RakeAndPlantTree(19147, config.farmingMaterial().getItemName())) return;

                        //pay protect tree
                        if (Inventory.hasItemAmount(config.farmingMaterial().getItemName(), 3) && Inventory.hasItemAmountStackable(config.farmingMaterial().getProtectionItem(), 4)) {
                            Rs2Npc.interact(NpcID.PRISSY_SCILLA, "pay");
                            sleepUntil(() -> !Inventory.hasItemAmountStackable(config.farmingMaterial().getProtectionItem(), 4));
                            return;
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean RakeAndPlantTree(int patchId, String treeToPlant) {
        GameObject farmingPatch = Rs2GameObject.findObjectByOption("rake");
        if (Inventory.hasItemAmount(treeToPlant, 4)) {
            if (farmingPatch != null) {
                Rs2GameObject.interact(farmingPatch, "rake");
                sleep(2000);
                sleepUntil(() -> !Microbot.isAnimating());
            } else {
                Inventory.useItem(treeToPlant);
                boolean success = Rs2GameObject.interact(patchId);
                if (success) {
                    sleepUntil(() -> Inventory.hasItemAmount(treeToPlant, 3));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        state = FarmingState.RESET;
    }
}
