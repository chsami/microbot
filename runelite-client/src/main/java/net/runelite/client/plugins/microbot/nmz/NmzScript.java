package net.runelite.client.plugins.microbot.nmz;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.prayer.Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

import static net.runelite.api.Varbits.NMZ_ABSORPTION;

public class NmzScript extends Script {

    public static double version = 1.0;

    public static NmzConfig config;

    public static boolean useOverload = false;


    public boolean run(NmzConfig config) {
        NmzScript.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                boolean isOutsideNmz = Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(new WorldPoint(2602, 3116, 0)) < 20;
                useOverload = Microbot.getClient().getBoostedSkillLevel(Skill.ATTACK) == Microbot.getClient().getRealSkillLevel(Skill.ATTACK);
                if (isOutsideNmz) {
                    handleOutsideNmz();
                } else {
                    handleInsideNmz();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    public void handleOutsideNmz() {
        boolean hasStartedDream = Microbot.getVarbitValue(3946) > 0;
        if (!hasStartedDream) {
            startNmzDream();
        } else {
            manageInventoryOutsideNmz();
        }
    }

    public void handleInsideNmz() {
        useZapperIfConfigured();
        manageLocatorOrb();
        toggleSpecialAttack();
        useOverloadPotion();
        useAbsorptionPotion();
    }

    public void startNmzDream() {
        Rs2Npc.interact(NpcID.DOMINIC_ONION, "Dream");
        sleepUntil(() -> Rs2Widget.hasWidget("Which dream would you like to experience?"));
        Rs2Widget.clickWidget("Previous:");
        sleepUntil(() -> Rs2Widget.hasWidget("Click here to continue"));
        Rs2Widget.clickWidget("Click here to continue");
        sleepUntil(() -> Rs2Widget.hasWidget("Agree to pay"));
        VirtualKeyboard.typeString("1");
        VirtualKeyboard.enter();
    }

    public void useZapperIfConfigured() {
        if (config.useZapper()) {
            interactWithObject(ObjectID.ZAPPER_26256);
            interactWithObject(ObjectID.RECURRENT_DAMAGE);
        }
    }

    public void interactWithObject(int objectId) {
        TileObject rs2GameObject = Rs2GameObject.findObjectById(objectId);
        if (rs2GameObject != null) {
            Microbot.getWalker().walkFastLocal(rs2GameObject.getLocalLocation());
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(rs2GameObject.getWorldLocation()) < 5);
            Rs2GameObject.interact(objectId);
        }
    }

    public void manageLocatorOrb() {
        if (Inventory.hasItem(ItemID.LOCATOR_ORB)) {
            handleLocatorOrbUsage();
            randomlyToggleRapidHeal();
        }
    }

    public void handleLocatorOrbUsage() {
        if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > Random.random(1, 5) && !useOverload) {
            Inventory.useItemFast(ItemID.LOCATOR_ORB, "feel");
        }
    }

    public void randomlyToggleRapidHeal() {
        if (Random.random(1, 50) == 2) {
            Rs2Prayer.fastPray(Prayer.RAPID_HEAL, true);
            sleep(300, 600);
            Rs2Prayer.fastPray(Prayer.RAPID_HEAL, false);
        }
    }

    public void toggleSpecialAttack() {
        if (Microbot.getClient().getLocalPlayer().isInteracting() && config.useSpecialAttack()) {
            // Microbot.toggleSpecialAttack(25);
        }
    }

    public void useOverloadPotion() {
        if (useOverload && Inventory.hasItemContains("overload")) {
            Inventory.interact(new String[]{"overload (4)", "overload (3)", "overload (2)", "overload (1)"});
            sleep(10000);
        }
    }

    public void useAbsorptionPotion() {
        if (Microbot.getVarbitValue(NMZ_ABSORPTION) < Random.random(300, 600) && Inventory.hasItemContains("absorption")) {
            for (int i = 0; i < Random.random(1, 5); i++) {
                Inventory.interact(new String[]{"absorption (4)", "absorption (3)", "absorption (2)", "absorption (1)"});
                sleep(600, 1000);
            }
        }
    }

    public void manageInventoryOutsideNmz() {
        managePotionInventory(ObjectID.OVERLOAD_POTION, "overload", config.overloadPotionAmount(), "32");
        managePotionInventory(ObjectID.ABSORPTION_POTION, "absorption", config.absorptionPotionAmount(), "80");
        consumeEmptyVial();
    }

    public void managePotionInventory(int objectId, String itemName, int requiredAmount, String keyboardInput) {
        if (!Inventory.hasItemAmountExact(itemName, requiredAmount)) {
            Rs2GameObject.interact(objectId, "Store");
            String storeWidgetText = "Store all your " + itemName + " potion?";
            sleepUntil(() -> Rs2Widget.hasWidget(storeWidgetText));
            if (Rs2Widget.hasWidget(storeWidgetText)) {
                VirtualKeyboard.typeString("1");
                VirtualKeyboard.enter();
                sleepUntil(() -> !Inventory.hasItem(objectId));
            }
            Rs2GameObject.interact(objectId, "Take");
            String widgetText = "How many doses of " + itemName;
            if (Rs2Widget.hasWidget(widgetText)) {
                VirtualKeyboard.typeString(keyboardInput);
                VirtualKeyboard.enter();
                sleepUntil(() -> Inventory.hasItemAmountExact(itemName + " (4)", requiredAmount));
            }
        }
    }

    public void consumeEmptyVial() {
        if (Inventory.hasItemAmount("overload (4)", config.overloadPotionAmount()) && Inventory.hasItemAmount("absorption (4)", config.absorptionPotionAmount())) {
            final int EMPTY_VIAL = 26291;
            Rs2GameObject.interact(EMPTY_VIAL, "drink");
            sleepUntil(() -> Rs2Widget.hasWidget("Nightmare zone"));
            Rs2Widget.clickWidget(8454150);
            sleep(5000);
        }
    }

}
