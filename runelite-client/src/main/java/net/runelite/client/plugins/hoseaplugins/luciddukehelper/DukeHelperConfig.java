package net.runelite.client.plugins.hoseaplugins.luciddukehelper;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("lucid-duke-helper")
public interface DukeHelperConfig extends Config
{
    @ConfigSection(name = "General", description = "General settings", position = 0)
    String generalSection = "General";

    @ConfigItem(name = "Enable Overlays", description = "Enables showing all the overlays", position = 0, keyName = "enableOverlays", section = generalSection)
    default boolean enableOverlays()
    {
        return false;
    }

    @ConfigItem(name = "Enable Text", description = "Shows the tick info on overlays", position = 1, keyName = "enableText", section = generalSection)
    default boolean enableText()
    {
        return true;
    }

    @ConfigItem(name = "One-Click Mushroom Grinding", description = "Allows you to just click on a mushroom in inventory to auto use it on pestle to grind it and pick another in 1 tick", position = 2, keyName = "oneClickMushroom", section = generalSection)
    default boolean oneClickMushroom()
    {
        return false;
    }

    @ConfigItem(name = "One-Click Dust->Duke", description = "Allows you to just click on dust in inventory to auto use it on Duke.", position = 3, keyName = "oneClickDust", section = generalSection)
    default boolean oneClickDust()
    {
        return false;
    }

    @ConfigSection(name = "Dodging", description = "Dodging settings", position = 1)
    String dodgingSection = "Dodging";

    @ConfigItem(name = "4-Tick Cycle above 25% HP", description = "If the weapon you're using when duke is above 25% HP is 4-ticks. Turn off if it's 5-tick", position = 0, keyName = "fourTickCycle", section = dodgingSection)
    default boolean fourTickCycle()
    {
        return true;
    }

    @ConfigItem(name = "4-Tick Cycle below 25% HP", description = "If the weapon you're using when duke is below 25% HP is 4-ticks. Turn off if it's 5-tick", position = 1, keyName = "fourTickCycleEnrage", section = dodgingSection)
    default boolean fourTickCycleEnrage()
    {
        return true;
    }

    @ConfigItem(name = "Enrage Weapon Swap", description = "Swaps to this weapon when duke gets below 25% HP", position = 2, keyName = "enrageWeapon", section = dodgingSection)
    default String enrageWeapon()
    {
        return "Arclight";
    }

    @ConfigItem(name = "Auto-Dodge Spikes", description = "Auto-step back when the spikes come up", position = 3, keyName = "autoDodgeSpikes", section = dodgingSection)
    default boolean autoDodgeSpikes()
    {
        return false;
    }

    @ConfigItem(name = "Auto-Attack Spikes", description = "Auto-attack after dodging spikes", position = 4, keyName = "autoAttackSpikes", section = dodgingSection)
    default boolean autoAttackSpikes()
    {
        return false;
    }

    @ConfigItem(name = "Auto-Dodge Gas", description = "Auto-run to the other side when duke does gas attack", position = 5, keyName = "autoDodgeGas", section = dodgingSection)
    default boolean autoDodgeGas()
    {
        return false;
    }

    @ConfigItem(name = "Auto-Attack Gas", description = "Auto-attack during the run to the other side when duke does gas attack", position = 6, keyName = "autoAttackGas", section = dodgingSection)
    default boolean autoAttackGas()
    {
        return false;
    }

    @ConfigItem(name = "Auto-Dodge Eye", description = "Auto-dodge the eye attack", position = 7, keyName = "autoDodgeEye", section = dodgingSection)
    default boolean autoDodgeEye()
    {
        return false;
    }

    @ConfigItem(name = "Auto-Attack Eye", description = "Auto-attack when it becomes safe again after the eye attack", position = 8, keyName = "autoAttackEye", section = dodgingSection)
    default boolean autoAttackEye()
    {
        return false;
    }

    @ConfigSection(name = "Color Settings", description = "Color settings", position = 2)
    String colorSection = "Color Settings";

    @ConfigItem(name = "Safe Tile Color", description = "What color should the safe tiles be?", position = 0, keyName = "safeTileColor", section = colorSection)
    @Alpha
    default Color safeTileColor()
    {
        return new Color(5, 255, 5, 120);
    }

    @ConfigItem(name = "Safe Text Color", description = "What should be the safe text color?", position = 1, keyName = "safeTextColor", section = colorSection)
    @Alpha
    default Color safeTextColor()
    {
        return Color.WHITE;
    }

    @ConfigItem(name = "Unsafe Tile Color", description = "What color should the unsafe tiles be?", position = 0, keyName = "unsafeTileColor", section = colorSection)
    @Alpha
    default Color unsafeTileColor()
    {
        return new Color(5, 5, 5, 120);
    }

    @ConfigItem(name = "Unsafe Text Color", description = "What color should the text on unsafe tiles be?", position = 1, keyName = "unsafeTextColor", section = colorSection)
    @Alpha
    default Color unsafeTextColor()
    {
        return Color.WHITE;
    }

}
