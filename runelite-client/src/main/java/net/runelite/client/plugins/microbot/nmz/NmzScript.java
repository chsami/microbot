package net.runelite.client.plugins.microbot.nmz;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.combat.PrayerPotionScript;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

import static net.runelite.api.Varbits.NMZ_ABSORPTION;

public class NmzScript extends Script {

    public static double version = 2.0;

    public static NmzConfig config;

    public static boolean useOverload = false;

    public static PrayerPotionScript prayerPotionScript;

    public boolean canStartNmz() {
        return Rs2Inventory.count("overload (4)") == config.overloadPotionAmount() && Rs2Inventory.count("absorption (4)") == config.absorptionPotionAmount() ||
                (Rs2Inventory.hasItem("prayer potion") && config.togglePrayerPotions());
    }


    public boolean run(NmzConfig config) {
        NmzScript.config = config;
        prayerPotionScript = new PrayerPotionScript();
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                Rs2Combat.enableAutoRetialiate();
                if (Random.random(1, 50) == 1 && config.randomMouseMovements()) {
                    Microbot.getMouse().click(Random.random(0, Microbot.getClient().getCanvasWidth()), Random.random(0, Microbot.getClient().getCanvasHeight()), true);
                }
                boolean isOutsideNmz = Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(new WorldPoint(2602, 3116, 0)) < 20;
                useOverload = Microbot.getClient().getBoostedSkillLevel(Skill.RANGED) == Microbot.getClient().getRealSkillLevel(Skill.RANGED);
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
        if (config.togglePrayerPotions())
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, false);
        if (!hasStartedDream) {
            startNmzDream();
        } else {
            final String overload = "Overload (4)";
            final String absorption = "Absorption (4)";
            storePotions(ObjectID.OVERLOAD_POTION, "overload", config.overloadPotionAmount());
            storePotions(ObjectID.ABSORPTION_POTION, "absorption", config.absorptionPotionAmount());
            handleStore();
            fetchPotions(ObjectID.OVERLOAD_POTION, overload, config.overloadPotionAmount());
            fetchPotions(ObjectID.ABSORPTION_POTION, absorption, config.absorptionPotionAmount());
            if (canStartNmz()) {
                consumeEmptyVial();
            } else {
                Microbot.showMessage("Bot can't start because your overloads or absorption potions do not match the configured number in your plugin settings.");
                sleep(2000);
            }
        }
    }

    public void handleInsideNmz() {
        prayerPotionScript.run();
        if (config.togglePrayerPotions())
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
        useZapperIfConfigured();
        useOverloadPotion();
        manageLocatorOrb();
        toggleSpecialAttack();
        useAbsorptionPotion();
    }

    public void startNmzDream() {
        Rs2Npc.interact(NpcID.DOMINIC_ONION, "Dream");
        sleepUntil(() -> Rs2Widget.hasWidget("Which dream would you like to experience?"));
        Rs2Widget.clickWidget("Previous:");
        sleepUntil(() -> Rs2Widget.hasWidget("Click here to continue"));
        Rs2Widget.clickWidget("Click here to continue");
        sleepUntil(() -> Rs2Widget.hasWidget("Agree to pay"));
        Rs2Keyboard.typeString("1");
        Rs2Keyboard.enter();
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
            Rs2Walker.walkFastLocal(rs2GameObject.getLocalLocation());
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(rs2GameObject.getWorldLocation()) < 5);
            Rs2GameObject.interact(objectId);
        }
    }

    public void manageLocatorOrb() {
        if (Rs2Inventory.hasItem(ItemID.LOCATOR_ORB)) {
            handleLocatorOrbUsage();
            randomlyToggleRapidHeal();
        } else if (Rs2Inventory.hasItem(ItemID.DWARVEN_ROCK_CAKE_7510)) {
            handleDwarvenRockCake();
            randomlyToggleRapidHeal();
        }
    }

    private void handleDwarvenRockCake() {
        if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > Random.random(1, 5) && !useOverload
                && Microbot.getClient().getBoostedSkillLevel(Skill.RANGED) != Microbot.getClient().getRealSkillLevel(Skill.RANGED)) {
            Rs2Inventory.interact(ItemID.DWARVEN_ROCK_CAKE_7510, "guzzle");
        }
    }

    public void handleLocatorOrbUsage() {
        if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > Random.random(1, 5) && !useOverload
                && Microbot.getClient().getBoostedSkillLevel(Skill.RANGED) != Microbot.getClient().getRealSkillLevel(Skill.RANGED)) {
            Rs2Inventory.interact(ItemID.LOCATOR_ORB, "feel");
        }
    }

    public void randomlyToggleRapidHeal() {
        if (Random.random(1, 50) == 2) {
            Rs2Prayer.toggle(Rs2PrayerEnum.RAPID_HEAL, true);
            sleep(300, 600);
            Rs2Prayer.toggle(Rs2PrayerEnum.RAPID_HEAL, false);
        }
    }

    public void toggleSpecialAttack() {
        if (Microbot.getClient().getLocalPlayer().isInteracting() && config.useSpecialAttack()) {
            Rs2Combat.setSpecState(true, 1000);
        }
    }

    public void useOverloadPotion() {
        if (useOverload && Rs2Inventory.hasItem("overload") && Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > 50) {
            Rs2Inventory.interact(x -> x.name.toLowerCase().contains("overload"), "drink");
            sleep(10000);
        }
    }

    public void useAbsorptionPotion() {
        if (Microbot.getVarbitValue(NMZ_ABSORPTION) < Random.random(300, 600) && Rs2Inventory.hasItem("absorption")) {
            for (int i = 0; i < Random.random(1, 5); i++) {
                Rs2Inventory.interact(x -> x.name.toLowerCase().contains("absorption"), "drink");
                sleep(600, 1000);
            }
        }
    }

    private void storePotions(int objectId, String itemName, int requiredAmount) {
        if (Rs2Inventory.count(itemName) == requiredAmount) return;
        if (Rs2Inventory.get(itemName) == null) return;

        Rs2GameObject.interact(objectId, "Store");
        String storeWidgetText = "Store all your ";
        sleepUntil(() -> Rs2Widget.hasWidget(storeWidgetText));
        if (Rs2Widget.hasWidget(storeWidgetText)) {
            Rs2Keyboard.typeString("1");
            Rs2Keyboard.enter();
            sleepUntil(() -> !Rs2Inventory.hasItem(objectId));
        }
    }

    private void fetchPotions(int objectId, String itemName, int requiredAmount) {
        if (Rs2Inventory.count(itemName) == requiredAmount) return;

        Rs2GameObject.interact(objectId, "Take");
        String widgetText = "How many doses of ";
        sleepUntil(() -> Rs2Widget.hasWidget(widgetText));
        if (Rs2Widget.hasWidget(widgetText)) {
            Rs2Keyboard.typeString(Integer.toString(requiredAmount * 4));
            Rs2Keyboard.enter();
            sleepUntil(() -> Rs2Inventory.count(itemName) == requiredAmount);
        }
    }

    public void consumeEmptyVial() {
        final int EMPTY_VIAL = 26291;
        if (Microbot.getClientThread().runOnClientThread(() -> Rs2Widget.getWidget(129, 6) == null || Rs2Widget.getWidget(129, 6).isHidden())) {
            Rs2GameObject.interact(EMPTY_VIAL, "drink");
        }
        Widget widget = Rs2Widget.getWidget(129, 6);
        if (!Microbot.getClientThread().runOnClientThread(widget::isHidden)) {
            Rs2Widget.clickWidget(widget.getId());
            sleep(300);
            Rs2Widget.clickWidget(widget.getId());
        }
    }

    public void handleStore() {
        if (canStartNmz()) return;
        int varbitOverload = 3953;
        int varbitAbsorption = 3954;
        int overloadAmt = Microbot.getVarbitValue(varbitOverload);
        int absorptionAmt = Microbot.getVarbitValue(varbitAbsorption);
        int nmzPoints = Microbot.getVarbitPlayerValue(VarPlayer.NMZ_REWARD_POINTS);

        if (absorptionAmt > config.absorptionPotionAmount() * 4 && overloadAmt > config.overloadPotionAmount() * 4)
            return;

        if (!Rs2Inventory.isFull()) {
            if ((absorptionAmt < (config.absorptionPotionAmount() * 4) || overloadAmt < config.overloadPotionAmount() * 4) && nmzPoints < 100000) {
                Microbot.showMessage("BOT SHUTDOWN: Not enough points to buy potions");
                shutdown();
                return;
            }
        }

        Rs2GameObject.interact(26273);

        sleepUntil(() -> Rs2Widget.getWidget(13500418) != null, 10000);

        Widget benefitsBtn = Rs2Widget.getWidget(13500418);
        if (benefitsBtn == null) return;
        boolean notSelected = benefitsBtn.getSpriteId() != 813;
        if (notSelected) {
            Rs2Widget.clickWidgetFast(benefitsBtn, 4, 4);
        }
        int count = 0;
        while (count < Random.random(3, 5)) {
            Widget nmzRewardShop = Rs2Widget.getWidget(206, 6);
            if (nmzRewardShop == null) break;
            Widget overload = nmzRewardShop.getChild(6);
            Rs2Widget.clickWidgetFast(overload, 6, 4);
            Widget absorption = nmzRewardShop.getChild(9);
            Rs2Widget.clickWidgetFast(absorption, 9, 4);
            sleep(600, 1200);
            count++;
        }
    }

}
