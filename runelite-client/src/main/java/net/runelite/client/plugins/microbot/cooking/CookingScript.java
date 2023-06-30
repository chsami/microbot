package net.runelite.client.plugins.microbot.cooking;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.cooking.enums.CookingEnum;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.mta.telekinetic.TelekineticRoom;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.random;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class CookingScript extends Script {

    public static double version = 1.0;

    public boolean run(int gameObjectId) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

                String itemToCook = getItemToCook();

                if (!Inventory.isFull() || !Inventory.hasItem(itemToCook)) {
                    if (!Rs2Bank.isOpen()) {
                        boolean bankIsOnScreen = Rs2Bank.useBank();
                        if (!bankIsOnScreen) {
                            Rs2Bank.walkToBank();
                        }
                        Rs2Bank.depositAll();
                        Rs2Bank.withdrawItemAll(true, itemToCook);
                    }
                }

                if (Inventory.hasItem(itemToCook)) {
                    Microbot.getWalker().walkFastMinimap(new WorldPoint(3273 + random(-2, 2), 3180+ random(-2, 2), 0));

                    TileObject cookingRange = Rs2GameObject.findObjectById(gameObjectId);
                    if (cookingRange != null) {
                        if (!Camera.isTileOnScreen(cookingRange.getLocalLocation())) {
                            Camera.turnTo(cookingRange.getLocalLocation());
                            return;
                        }
                        if (Rs2Widget.getWidget(17694734) == null)
                        {
                            Rs2GameObject.interact(cookingRange);
                            sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694734) != null);
                            sleep(1200, 1600);
                        }
                        VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                        sleep(5000);
                        while (true) {
                            long rawFoodCount = Inventory.getAmountForItem(itemToCook);
                            sleep(3000);
                            if (rawFoodCount == Inventory.getAmountForItem(itemToCook))
                                break;
                        }
                    }
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }


    private String getItemToCook() {
        for (CookingEnum cookingEnum: CookingEnum.values()) {
            if (Microbot.getClient().getRealSkillLevel(Skill.COOKING) >= cookingEnum.getLevelRequired()) {
                return cookingEnum.getRawFoodName();
            }
        }
        return "";
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
