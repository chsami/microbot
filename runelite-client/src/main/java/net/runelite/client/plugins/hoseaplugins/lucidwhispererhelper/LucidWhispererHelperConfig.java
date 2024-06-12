package net.runelite.client.plugins.hoseaplugins.lucidwhispererhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("lucid-whisperer-helper")
public interface LucidWhispererHelperConfig extends Config
{
    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0
    )
    String generalSection = "General";
    @ConfigItem(
            name = "Auto-pray",
            description = "Swaps prayers against projectiles",
            position = 1,
            keyName = "autoPray",
            section = generalSection
    )
    default boolean autoPray()
    {
        return false;
    }

    @ConfigItem(
            name = "Auto-activate Blackstone fragment",
            description = "Auto-activates the Blackstone fragment when there's a special",
            position = 2,
            keyName = "autoFragment",
            section = generalSection
    )
    default boolean autoFragment()
    {
        return false;
    }

    @ConfigItem(
            name = "Auto-dodge tentacle attacks",
            description = "Auto-dodges the tentacle attacks. You might still need to occasionally avoid the edge.",
            position = 3,
            keyName = "autoDodge",
            section = generalSection
    )
    default boolean autoDodge()
    {
        return false;
    }

    @ConfigItem(
            name = "Auto-attack after dodging tentacle attacks",
            description = "Auto-attacks after dodging the tentacle attacks",
            position = 4,
            keyName = "autoAttack",
            section = generalSection
    )
    default boolean autoAttack()
    {
        return false;
    }

    @ConfigItem(
            name = "Leeches Overlay",
            description = "Overlays the tiles for the leeches",
            position = 5,
            keyName = "leechOverlay",
            section = generalSection
    )
    default boolean leechOverlay()
    {
        return false;
    }

    @ConfigItem(
            name = "Bind Ticks Overlay",
            description = "Overlays the ticks until you get unbound from Whisperer's bind attack",
            position = 6,
            keyName = "bindOverlay",
            section = generalSection
    )
    default boolean bindOverlay()
    {
        return false;
    }

    @ConfigItem(
            name = "Vita! Overlay",
            description = "Overlays the tile where the Vita! npcs are.",
            position = 7,
            keyName = "vitaOverlay",
            section = generalSection
    )
    default boolean vitaOverlay()
    {
        return false;
    }

    @ConfigItem(
            name = "Pillar Overlay",
            description = "Overlays the tiles with the order to stand.",
            position = 8,
            keyName = "pillarOverlay",
            section = generalSection
    )
    default boolean pillarOverlay()
    {
        return false;
    }

    @ConfigItem(
            name = "Auto-equip Venator",
            description = "Auto-equip venator bow.",
            position = 9,
            keyName = "autoVenator",
            section = generalSection
    )
    default boolean autoVenator()
    {
        return false;
    }

    @ConfigItem(
            name = "Show Unsafe Tiles",
            description = "Shows the tiles that will be hit by tentacles. Don't stand on them",
            position = 10,
            keyName = "showUnsafeTiles",
            section = generalSection
    )
    default boolean showUnsafeTiles()
    {
        return false;
    }

    @ConfigItem(
            name = "Weapon Max Tile Range",
            description = "The plugin uses this distance to make sure you stay in attack range while dodging",
            position = 11,
            keyName = "maxWeaponRange",
            section = generalSection
    )
    default int maxWeaponRange()
    {
        return 8;
    }
}
