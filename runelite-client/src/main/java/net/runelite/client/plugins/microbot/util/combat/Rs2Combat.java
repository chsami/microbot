package net.runelite.client.plugins.microbot.util.combat;

import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

public class Rs2Combat {
    /**
     * Sets the attack style
     *
     * @param style WidgetInfo. ex. COMBAT_STYLE_ONE
     * @return boolean, whether the action succeeded
     */
    public static boolean setAttackStyle(WidgetInfo style) {
        Widget widget = Microbot.getClient().getWidget(style);
        if (widget == null) return false;
        if (isSelected(widget.getId() + 1)) {
            return true;
        }

        Microbot.getMouse().click(widget.getBounds());
        return true;
    }

    /**
     * Sets the auto retaliate state
     *
     * @param state boolean, true for enabled, false for disabled
     * @return boolean, whether the action succeeded
     */
    public static boolean setAutoRetaliate(boolean state) {
        Widget widget = Microbot.getClient().getWidget(WidgetInfo.COMBAT_AUTO_RETALIATE);
        if (widget == null) return false;
        if (state == isSelected(widget.getId() + 2)) return true;

        Microbot.getMouse().click(widget.getBounds());
        Global.sleep(600, 1000);
        return true;
    }

    /**
     * Sets the special attack state if currentSpecEnergy >= specialAttackEnergyRequired
     *
     * @param state                       boolean, true for enabled, false for disabled
     * @param specialAttackEnergyRequired int, 1000 = 100%
     * @return boolean, whether the action succeeded
     */
    public static boolean setSpecState(boolean state, int specialAttackEnergyRequired) {
        Widget widget = Microbot.getClient().getWidget(WidgetInfo.MINIMAP_SPEC_ORB);
        int currentSpecEnergy = Microbot.getClient().getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
        if (widget == null) return false;
        if (currentSpecEnergy < specialAttackEnergyRequired) return false;
        if (state == getSpecState()) return true;

        Microbot.getMouse().click(widget.getBounds());
        return true;
    }

    /**
     * Sets the special attack state
     *
     * @param state boolean, true for enabled, false for disabled
     * @return boolean, whether the action succeeded
     */
    public static boolean setSpecState(boolean state) {
        return setSpecState(state, -1);
    }

    /**
     * Checks the state of the spec widget
     *
     * @return boolean, whether the spec is enabled
     */
    public static boolean getSpecState() {
        Widget widget = Microbot.getClient().getWidget(WidgetInfo.MINIMAP_SPEC_ORB.getId() + 4);
        if (widget == null) throw new RuntimeException("Somehow the spec orb is null!");

        return widget.getSpriteId() == 1608;
    }

    /**
     * Checks if the widget is selected (based on the red background)
     *
     * @param widgetId int, the widget id
     * @return boolean, whether the widget is selected
     */
    private static boolean isSelected(int widgetId) {
        return Rs2Widget.getChildWidgetSpriteID(widgetId, 0) == 1150;
    }

    /**
     * Toggles the combat style
     *
     * @param attackStyleWidgetInfo WidgetInfo, the attack style widget
     * @return boolean, whether the action succeeded
     */
    private static boolean toggleCombatStyle(WidgetInfo attackStyleWidgetInfo) {
        Tab.switchToCombatOptionsTab();
        Global.sleep(150, 300);
        return Rs2Widget.clickWidget(attackStyleWidgetInfo);
    }

    /**
     * Toggles the accurate combat style
     *
     * @return boolean, whether the action succeeded
     */
    public static boolean toggleAccurateCombatStyle() {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_ONE);
    }

    /**
     * Toggles the aggressive combat style
     *
     * @return boolean, whether the action succeeded
     */
    public static boolean toggleAggressiveCombatStyle() {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_TWO);
    }

    /**
     * Toggles the controlled combat style
     *
     * @return boolean, whether the action succeeded
     */
    public static boolean toggleControlledCombatStyle() {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_THREE);
    }

    /**
     * Toggles the defensive combat style
     *
     * @return boolean, whether the action succeeded
     */
    public static boolean toggleDefensiveCombatStyle() {
        return toggleCombatStyle(WidgetInfo.COMBAT_STYLE_FOUR);
    }

    /**
     * Checks if the accurate combat style is selected
     *
     * @return boolean, whether the accurate combat style is selected
     */
    public static boolean isAccurateCombatStyleSelected() {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 0;
    }

    /**
     * Checks if the aggressive combat style is selected
     *
     * @return boolean, whether the aggressive combat style is selected
     */
    public static boolean isAggressiveCombatStyleSelected() {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 1;
    }

    /**
     * Checks if the controlled combat style is selected
     *
     * @return boolean, whether the controlled combat style is selected
     */
    public static boolean isControlledCombatStyleSelected() {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 2;
    }

    /**
     * Checks if the defensive combat style is selected
     *
     * @return boolean, whether the defensive combat style is selected
     */
    public static boolean isDefensiveCombatStyleSelected() {
        return Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE) == 3;
    }

}
