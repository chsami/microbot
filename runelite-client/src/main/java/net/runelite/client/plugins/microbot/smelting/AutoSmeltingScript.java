package net.runelite.client.plugins.microbot.smelting;

import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.smelting.enums.Ores;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class AutoSmeltingScript extends Script {

    public static String version = "1.0.0";

    public boolean run(AutoSmeltingConfig config) {

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

                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || Microbot.pauseAllScripts) return;

                // walk to bank until it's open then deposit everything and withdraw materials
                if (!inventoryHasMaterialsForOneBar(config)) {
                    if (!Rs2Bank.isOpen()) {
                        Rs2Bank.walkToBankAndUseBank();
                        return;
                    }
                    Rs2Bank.depositAll();
                    withdrawRightAmountOfMaterials(config);
                    return;
                }

                // walk to the initial position (near furnace)
                if (initialPlayerLocation.distanceTo(Rs2Player.getWorldLocation()) > 4) {
                    Rs2Walker.walkTo(initialPlayerLocation, 4);
                    return;
                }

                // interact with the furnace until the smelting dialogue opens in chat, click the selected bar icon
                // then wait for animation to finish
                GameObject furnace = Rs2GameObject.findObject("furnace", true, 10, true, initialPlayerLocation);
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
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean inventoryHasMaterialsForOneBar(AutoSmeltingConfig config) {
        for (Map.Entry<Ores, Integer> requiredMaterials : config.SELECTED_BAR_TYPE().getRequiredMaterials().entrySet()) {
            Integer amount = requiredMaterials.getValue();
            String name = requiredMaterials.getKey().toString();
            if (!Rs2Inventory.hasItemAmount(name, amount)) {
                return false;
            }
        }
        return true;
    }
    private void withdrawRightAmountOfMaterials(AutoSmeltingConfig config) {
        for (Map.Entry<Ores, Integer> requiredMaterials : config.SELECTED_BAR_TYPE().getRequiredMaterials().entrySet()) {
            Integer amountForOne = requiredMaterials.getValue();
            String name = requiredMaterials.getKey().toString();
            int totalAmount = config.SELECTED_BAR_TYPE().maxBarsForFullInventory() * amountForOne;
            if (!Rs2Bank.hasBankItem(name, totalAmount)) {
                Microbot.showMessage(MessageFormat.format("Required Materials not in bank. You need {1} {0}.", name, totalAmount));
                super.shutdown();
            }
            Rs2Bank.withdrawX(name, totalAmount);
        }
    }
}
