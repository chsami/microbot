package net.runelite.client.plugins.microbot.util.player;

import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;


public class Rs2Player {
    static int VENOM_VALUE_CUTOFF = -38;
    private static int antiFireTime = -1;
    private static int superAntiFireTime = -1;
    private static int divineRangedTime = -1;
    private static int divineBastionTime = -1;
    public static int antiVenomTime = -1;

    public static int antiPoisonTime = -1;


    public static boolean hasAntiFireActive() {
        return antiFireTime > 0 || hasSuperAntiFireActive();
    }

    public static boolean hasSuperAntiFireActive() {
        return superAntiFireTime > 0;
    }

    public static boolean hasDivineRangedActive() {
        return divineRangedTime > 0 || hasDivineBastionActive();
    }

    public static boolean hasRangingPotionActive() {
        return Microbot.getClient().getBoostedSkillLevel(Skill.RANGED) - 5 > Microbot.getClient().getRealSkillLevel(Skill.RANGED);
    }

    public static boolean hasDivineBastionActive() {
        return divineBastionTime > 0;
    }

    public static boolean hasAntiVenomActive() {
        if (Rs2Equipment.hasEquipped("serpentine helm")) {
            return true;
        } else return antiVenomTime < VENOM_VALUE_CUTOFF;
    }
    public static boolean hasAntiPoisonActive() {
        return antiPoisonTime > 0;
    }
    public static void handlePotionTimers(VarbitChanged event) {
        if (event.getVarbitId() == Varbits.ANTIFIRE) {
            antiFireTime = event.getValue();
        }
        if (event.getVarbitId() == Varbits.SUPER_ANTIFIRE) {
            superAntiFireTime = event.getValue();
        }
        if (event.getVarbitId() == Varbits.DIVINE_RANGING) {
            divineRangedTime = event.getValue();
        }
        if (event.getVarbitId() == Varbits.DIVINE_BASTION) {
            divineBastionTime = event.getValue();
        }
        if (event.getVarpId() == VarPlayer.POISON) {
            if (event.getValue() >= VENOM_VALUE_CUTOFF) {
                antiVenomTime = 0;
            } else {
                antiVenomTime = event.getValue();
            }
            final int poisonVarp = event.getValue();

            if (poisonVarp == 0)
            {
                antiPoisonTime = -1;
            } else {
                antiPoisonTime = poisonVarp;
            }
        }
    }

    public static void waitForWalking() {
        sleepUntil(Rs2Player::isWalking);
        sleepUntil(() -> !Rs2Player.isWalking());
    }

    public static void waitForAnimation() {
        sleepUntil(Rs2Player::isAnimating);
        sleepUntil(() -> !Rs2Player.isAnimating());
    }

    public static void waitForAnimation(int time) {
        sleepUntil(Rs2Player::isAnimating, time);
        sleepUntil(() -> !Rs2Player.isAnimating(), time);
    }

    public static boolean isAnimating() {
        return Microbot.isAnimating();
    }

    public static boolean isWalking() {
        return Microbot.isMoving();
    }

    public static boolean isMoving() {
        return Microbot.isMoving();
    }

    public static boolean isInteracting() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().isInteracting());
    }

    public static boolean isMember() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarpValue(VarPlayer.MEMBERSHIP_DAYS) > 0);
    }

    @Deprecated(since = "Use the Rs2Combat.specState method", forRemoval = true)
    public static void toggleSpecialAttack(int energyRequired) {
        int currentSpecEnergy = Microbot.getClient().getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
        if (currentSpecEnergy >= energyRequired && (Microbot.getClient().getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED) == 0)) {
            Rs2Widget.clickWidget("special attack");
        }
    }

    public static boolean toggleRunEnergy(boolean toggle) {

        if (Microbot.getVarbitPlayerValue(173) == 0 && !toggle) return true;
        if (Microbot.getVarbitPlayerValue(173) == 1 && toggle) return true;
        Widget widget = Rs2Widget.getWidget(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getId());
        if (widget == null) return false;
        if (Microbot.getClient().getEnergy() > 1000 && toggle) {
            Microbot.getMouse().click(widget.getCanvasLocation());
            return true;
        } else if (!toggle) {
            Microbot.getMouse().click(widget.getCanvasLocation());
            return true;
        }
        return false;
    }

    public static WorldPoint getWorldLocation() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().getWorldLocation());
    }
}
