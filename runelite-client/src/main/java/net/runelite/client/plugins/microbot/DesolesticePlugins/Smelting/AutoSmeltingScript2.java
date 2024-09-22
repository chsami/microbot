package net.runelite.client.plugins.microbot.DesolesticePlugins.Smelting;

import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.DesolesticePlugins.Smelting.enums.Ores;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.misc.TickTrackerPlugin;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class AutoSmeltingScript2 extends Script {

    public static String version = "1.0.0";

    public boolean run(AutoSmeltingConfig2 config) {

        initialPlayerLocation = null;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                if (config.SELECTED_BAR_TYPE().getRequiredSmithingLevel() > Rs2Player.getBoostedSkillLevel(Skill.SMITHING)) {
                    Microbot.showMessage("Your smithing level isn't high enough for " + config.SELECTED_BAR_TYPE().toString());
                    super.shutdown();
                    return;
                }

                if (initialPlayerLocation == null) {
                    initialPlayerLocation = Rs2Player.getWorldLocation();
                }

                if (Rs2Player.isMoving()
                    || Rs2Player.isAnimating()
                    || Microbot.pauseAllScripts
                    || (TickTrackerPlugin.getTicksSinceLastExperienceGain(Skill.SMITHING) < 5)
                ) return;

                // walk to bank until it's open then deposit everything and withdraw materials
                if (!inventoryHasMaterialsForOneBar(config)) {
                    if (!Rs2Bank.isOpen()) {
                        Rs2Bank.walkToBankAndUseBank();
                        return;
                    }
                    if(checkIfInventoryContainsUnwantedItems(config)){
                        Rs2Bank.depositAll();
                        return;
                    }

                    withdrawRightAmountOfMaterials(config);
                    return;
                }

                // walk to the initial position (near furnace)
                if (initialPlayerLocation.distanceTo(Rs2Player.getWorldLocation()) > 4) {
                    Rs2Walker.walkTo(initialPlayerLocation, 4);
                    return;
                }

                GameObject furnace = Rs2GameObject.findObject("furnace", true, 10, false, initialPlayerLocation);

                // interact with the furnace until the smelting dialogue opens in chat, click the selected bar icon
                // then wait for animation to finish
                if (furnace != null) {
                    Rs2GameObject.interact(furnace, "smelt");
                    sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694733) != null);
                    if (Rs2Widget.getWidget(17694733) != null) {
                        Rs2Widget.clickWidget(17694734 + config.SELECTED_BAR_TYPE().ordinal());
                        Rs2Player.waitForAnimation();
                    }
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean inventoryHasMaterialsForOneBar(AutoSmeltingConfig2 config) {
        for (Map.Entry<Ores, Integer> requiredMaterials : config.SELECTED_BAR_TYPE().getRequiredMaterials().entrySet()) {
            Integer amount = requiredMaterials.getValue();
            String name = requiredMaterials.getKey().toString();
            if (!Rs2Inventory.hasItemAmount(name, amount)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIfInventoryContainsUnwantedItems(AutoSmeltingConfig2 config) {

        var unwantedItems = config
            .SELECTED_BAR_TYPE()
            .getRequiredMaterials()
            .keySet()
            .stream()
            .map(Ores::getName)
            .collect(Collectors.toList());

        for(var item: Rs2Inventory.items()){
            if(!unwantedItems.contains(item.name.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    private void withdrawRightAmountOfMaterials(AutoSmeltingConfig2 config) {
        for (Map.Entry<Ores, Integer> requiredMaterials : config.SELECTED_BAR_TYPE().getRequiredMaterials().entrySet()) {

            Integer amountForOne = requiredMaterials.getValue();
            String name = requiredMaterials.getKey().toString();
            int totalAmount = config.SELECTED_BAR_TYPE().maxBarsForFullInventory() * amountForOne;

            if(Rs2Inventory.contains(requiredMaterials.getKey().toString())){
                var numberOfItems = Rs2Inventory.count(requiredMaterials.getKey().toString());

                if(numberOfItems > totalAmount){
                    //Too many items.  Deposit all and let the loop try to withdraw the right amount again.
                    Rs2Bank.depositAll(name);
                }else{
                    if(numberOfItems < totalAmount && Rs2Bank.hasBankItem(name, totalAmount - numberOfItems)){
                        Rs2Bank.withdrawX(name, totalAmount - numberOfItems);
                    }
                    continue;
                }
            }

            if (!Rs2Bank.hasBankItem(name, totalAmount) && Rs2Inventory.count(name) == 0) {
                Microbot.showMessage(MessageFormat.format("Required Materials not in bank. You need {1} {0}.", name, totalAmount));
                super.shutdown();
            }
            Rs2Bank.withdrawX(name, totalAmount);
        }
    }
}
