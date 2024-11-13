package net.runelite.client.plugins.microbot.nateplugins.skilling.arrowmaker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.nateplugins.skilling.arrowmaker.enums.*;


@ConfigGroup("ArrowMaking")
public interface ArrowConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "ActivateArrowMaking",
            name = "Activate Arrow Making",
            description = "Activate Arrow Making",
            position = 0,
            section = generalSection
    )
    default boolean ARROWBool()
    {
        return false;
    }
    @ConfigItem(
            keyName = "ArrowToMake",
            name = "Arrow to Make",
            description = "Choose the Arrow to Make",
            position = 1,
            section = generalSection
    )
    default Arrows ARROW()
    {
        return Arrows.HEADLESS_ARROW;
    }
    @ConfigItem(
            keyName = "ActivateBolts",
            name = "Activate Bolt Making",
            description = "Activate Bolt Making",
            position = 2,
            section = generalSection
    )
    default boolean BOLTBool()
    {
        return false;
    }
    @ConfigItem(
            keyName = "BoltstoMake",
            name = "Bolts to Make",
            description = "Choose the Bolt to Make",
            position = 3,
            section = generalSection
    )
    default Bolts BOLT()
    {
        return Bolts.BRONZE_BOLT;
    }
    @ConfigItem(
            keyName = "ActivateDarts",
            name = "Activate Dart Making",
            description = "Activate Dart Making",
            position = 4,
            section = generalSection
    )
    default boolean DARTBool()
    {
        return false;
    }
    @ConfigItem(
            keyName = "DartsToMake",
            name = "Darts to Make",
            description = "Choose the Darts to Make",
            position = 5,
            section = generalSection
    )
    default Darts DART()
    {
        return Darts.BRONZE_DART;
    }
    @ConfigItem(
            keyName = "ActivateTipping",
            name = "Activate Tipping",
            description = "Activate Tipping",
            position = 6,
            section = generalSection
    )
    default boolean TIPPINGBool()
    {
        return false;
    }
    @ConfigItem(
            keyName = "TipsToAdd",
            name = "Tips to Add",
            description = "Choose the Tips to Add",
            position = 7,
            section = generalSection
    )
    default Tipping TIP()
    {
        return Tipping.OPAL_TIPPED_BRONZE;
    }
    @ConfigItem(
            keyName = "ActivateDragonTipping",
            name = "Activate Dragon Tipping",
            description = "Activate Dragon Tipping",
            position = 8,
            section = generalSection
    )
    default boolean DRAGONTIPPINGBool()
    {
        return false;
    }
    @ConfigItem(
            keyName = "DragonTipsToAdd",
            name = "Dragon Tips to Add",
            description = "Choose the Tips to Add",
            position = 9,
            section = generalSection
    )
    default DragonTipping DragonTIP()
    {
        return DragonTipping.OPAL_TIPPED_BRONZE;
    }
}
