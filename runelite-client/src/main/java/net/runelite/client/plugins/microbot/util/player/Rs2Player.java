package net.runelite.client.plugins.microbot.util.player;

import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.events.VarbitChanged;


public class Rs2Player {

    public static int antiFireTime = -1;
    public static int superAntiFireTime = -1;
    public static int divineRangedTime = -1;
    public static int divineBastionTime = -1;
    public static int antiVenomTime = -1;

    public static boolean hasAntiFireActive() {
        return antiFireTime > 0 || hasSuperAntiFireActive();
    }

    public static boolean hasSuperAntiFireActive() {
        return superAntiFireTime > 0;
    }

    public static boolean hasDivineRangedActive() {
        return divineRangedTime > 0 || hasDivineBastionActive();
    }

    public static boolean hasDivineBastionActive() {
        return divineBastionTime > 0;
    }

    public static boolean hasAntiVenomActive() {
        return antiVenomTime > 0;
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
            int VENOM_VALUE_CUTOFF = -38;
            if (event.getValue() >= VENOM_VALUE_CUTOFF) {
                antiVenomTime = 0;
                return;
            }
            antiVenomTime = event.getValue();
        }
    }
}
