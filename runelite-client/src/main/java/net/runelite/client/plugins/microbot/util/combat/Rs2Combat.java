package net.runelite.client.plugins.microbot.util.combat;

import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

public class Rs2Combat {

    private static boolean toggleCombatStyle(WidgetInfo attackStyleWidgetInfo) {
        Tab.switchToCombatOptionsTab();
        Global.sleep(150, 300);
        return Rs2Widget.clickWidget(attackStyleWidgetInfo);
    }

    public static boolean toggleAccurateCombatStyle() {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_ONE);
    }

    public static boolean toggleAggressiveCombatStyle() {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_TWO);
    }

    public static boolean toggleControlledCombatStyle() {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_THREE);
    }

    public static boolean toggleDefensiveCombatStyle() {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_FOUR);
    }

    public static boolean isAccurateCombatStyleSelected() {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 0;
    }

    public static boolean isAggressiveCombatStyleSelected() {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 1;
    }

    public static boolean isControlledCombatStyleSelected() {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 2;
    }

    public static boolean isDefensiveCombatStyleSelected() {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 3;
    }

}
