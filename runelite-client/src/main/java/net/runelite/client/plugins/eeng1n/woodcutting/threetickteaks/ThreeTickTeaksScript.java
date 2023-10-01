package net.runelite.client.plugins.eeng1n.woodcutting.threetickteaks;


import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.natepainthelper.Info.*;

public class ThreeTickTeaksScript extends Script {

    ThreeTickTeaksStatus status = ThreeTickTeaksStatus.Idle;

    public boolean run() {
            status = ThreeTickTeaksStatus.Idle;

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
                    expstarted = Microbot.getClient().getSkillExperience(Skill.WOODCUTTING);
                    startinglevel = Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING);
                    timeBegan = System.currentTimeMillis();
                }

                    try {
                        switch(status) {
                            case UseGuam:
                                useGuam();
                                break;
                            case UseTarAndDrop:
                                useTarAndDropLog();
                                break;
                            case ClickTeakTree:
                            case Idle:
                                clickTeakTree();
                                break;
                            default:
                                Microbot.showMessage("ThreeTickTeaks stopped unexpectedly!");
                                break;
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }

            }, 0, 1, TimeUnit.MILLISECONDS);
            return true;
    }

    private boolean checkRequirements() {
        if(!hasRequiredWoodcuttingLevel()) {
            Microbot.showMessage("The plugin has been disabled due to a not high enough Woodcutting level! You need at least level 35. Please make sure you have the required level and restart the script afterwards.");
            return false;
        }
        if(!hasRequiredItems()) {
            Microbot.showMessage("The plugin has been disabled due to missing items! Please make sure you have the required items and restart the script afterwards.");
            return false;
        }
        return true;
    }

    private boolean hasRequiredWoodcuttingLevel() {
        return Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING) >= 35;
    }

    private boolean hasRequiredItems() {
        return Inventory.hasItem("Guam leaf") && Inventory.hasItem("Pestle and mortar") && Inventory.hasItem("Swamp tar");
    }

    private void useGuam() {
        sleep(13, 167);
        Widget guamLeafWidget = Inventory.findItem("Guam leaf");
        Microbot.getMouse().click(guamLeafWidget.getBounds());

        status = ThreeTickTeaksStatus.UseTarAndDrop;
    }

    private void useTarAndDropLog() {
        sleep(18, 132);

        Inventory.useItemFast(ItemID.SWAMP_TAR, "Use");

        Inventory.useItemFast(ItemID.TEAK_LOGS, "Drop");

        status = ThreeTickTeaksStatus.ClickTeakTree;
    }

    private void clickTeakTree() {
        sleep(23, 195);

        GameObject teakTree = getTeakTree();

        Rs2GameObject.interact(teakTree, "Chop down");

        status = ThreeTickTeaksStatus.UseGuam;
    }

    private GameObject getTeakTree() {
        return Rs2GameObject.findObject("Teak tree");
    }
}