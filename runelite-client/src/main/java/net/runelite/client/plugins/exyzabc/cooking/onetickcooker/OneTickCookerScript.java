package net.runelite.client.plugins.exyzabc.cooking.onetickcooker;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.exyzabc.cooking.onetickcooker.enums.OneTickCookerLocation;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.natepainthelper.Info.*;

@Slf4j
public class OneTickCookerScript extends Script {

    public static OneTickCookerStatus status = OneTickCookerStatus.Idle;
    @Inject
    OneTickCookerConfig config;

    @Subscribe
    public void onGameTick() {
       switch (status) {
           case Cook:
               cook();
               break;
           case Bank:
               bank();
               break;
           default:
               Microbot.showMessage("OneTickCooker stopped unexpectedly!");
               break;
       }
    }

    public boolean run() {

            mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
                if(!super.run()) {
                    return;
                }
                if(!Microbot.isLoggedIn()){
                    return;
                }
                if(!checkRequirements()) {
                    return;
                }
                if(expstarted == 0) {
                    expstarted = Microbot.getClient().getSkillExperience(Skill.COOKING);
                    startinglevel = Microbot.getClient().getRealSkillLevel(Skill.COOKING);
                    timeBegan = System.currentTimeMillis();
                }

                checkStatus();
                onGameTick();

            }, 0, 1, TimeUnit.MILLISECONDS);

            return true;
    }

    private void checkStatus() {
        if(!Inventory.hasItem(ItemID.RAW_KARAMBWAN)) {
            status = OneTickCookerStatus.Bank;
        }
        else {
            status = OneTickCookerStatus.Cook;
        }
    }

    private boolean checkRequirements() {
        if(!hasRequiredCookingLevel()) {
            Microbot.showMessage("The plugin has been disabled due to a not high enough Cooking level! You need at least level 30. Please make sure you have the required level and restart the script afterwards.");
            shutdown();
            return false;
        }
        return true;
    }

    private boolean hasRequiredCookingLevel() {
        return Microbot.getClient().getRealSkillLevel(Skill.COOKING) >= 30;
    }

    private void cook() {
        if(!Inventory.hasItem(ItemID.RAW_KARAMBWAN)) {
            status = OneTickCookerStatus.Bank;
        }
        sleep(50, 125);
        useItemLastOnObject(ItemID.RAW_KARAMBWAN, getCookingPlaceId());
    }

    private void bank() {
            sleep(600, 1000);
            Rs2GameObject.interact(Rs2GameObject.findObjectById(getBankChestId()));

            if(Rs2Bank.isOpen()) {
                if(!Rs2Bank.hasItem(ItemID.RAW_KARAMBWAN)) {
                    Microbot.showMessage("No Raw Karambwans have been found in your bank. The script has shutdown!");
                    shutdown();
                }
            }
            sleep(180, 300);
            Rs2Bank.depositAll();
            sleep(800, 1200);
            Rs2Bank.withdrawAllFast(ItemID.RAW_KARAMBWAN);

            sleep(600, 1000);

            Rs2Bank.closeBank();

            if(Inventory.hasItem(ItemID.RAW_KARAMBWAN)) {
                status = OneTickCookerStatus.Cook;
            }
    }

    public void useItemLastOnObject(int item, int objectID) {
        if(!Inventory.hasItem(ItemID.RAW_KARAMBWAN)) {
            return;
        }
        if (Rs2Bank.isOpen()) return;

        Widget item1 = Inventory.findItemLast(item);
        TileObject object = Rs2GameObject.findObjectById(objectID);
        if (item1 == null || object == null) return;

        Microbot.getMouse().click(item1.getBounds().getCenterX(), item1.getBounds().getCenterY());
        Microbot.getMouse().click(object.getCanvasLocation().getX(), object.getCanvasLocation().getY());
    }

    private int getCookingPlaceId() {
        if(config.LOCATION().name().equals(OneTickCookerLocation.HOSIDIUS_KITCHEN.name())) {
            return ObjectID.CLAY_OVEN_21302;
        }
        else {
            return ObjectID.RANGE_31631;
        }
    }

    private int getBankChestId() {
        if(config.LOCATION().name().equals(OneTickCookerLocation.HOSIDIUS_KITCHEN.name())) {
            return ObjectID.BANK_CHEST_21301;
        }
        else {
            return ObjectID.BANK_CHEST_30087;
        }
    }
}