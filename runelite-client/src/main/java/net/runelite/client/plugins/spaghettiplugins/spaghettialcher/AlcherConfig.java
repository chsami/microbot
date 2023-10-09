package net.runelite.client.plugins.spaghettiplugins.spaghettialcher;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("Magic")
public interface AlcherConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Alchable",
            name = "Alchable",
            description = "Item to turn into coins.",
            position = 0,
            section = generalSection
    )
    default Items Item()
    {
        return Items.RUNE_JAVELIN_HEADS;
    }

    @ConfigItem(
            keyName = "Item names",
            name = "Item names",
            description = "Name of item/list of items.",
            position = 1,
            section = generalSection
    )
    default String ItemList()
    {
        return "";
    }
    @ConfigItem(
            keyName = "Random pause delay",
            name = "Random pause delay",
            description = "For how long the bot should randomly pause for.",
            position = 2,
            section = generalSection
    )
    default boolean refill()
    {
        return false;
    }

    @ConfigSection(
            name = "Bot Management",
            description = "Bot Management",
            position = 1,
            closedByDefault = false
    )
    String botManagement = "Bot management";
    @ConfigItem(
            keyName = "Override state",
            name = "Override state",
            description = "If this is checked the state will be overridden, use in case of stuck bot.",
            position = 0,
            section = botManagement
    )
    default boolean overrideState()
    {
        return false;
    }
    @ConfigItem(
            keyName = "State",
            name = "State",
            description = "If this is checked the state will be overridden, use in case of stuck bot.",
            position = 1,
            section = botManagement
    )
    default AlcherState stateToOverrideWith()
    {
        return AlcherState.INVENTORY_DONE;
    }
    @ConfigSection(
            name = "Information",
            description = "Information",
            position = 2,
            closedByDefault = false
    )
    String botInfo = "Info";
    @ConfigItem(
            keyName = "Usage directions",
            name = "Usage directions",
            description = "How to use the script.",
            position = 0,
            section = botInfo
    )
    default String stringInfo()
    {
        return "Automatic goes through the list of items hard coded in the script\nList uses user defined items, this is recommended for daily use.\nOther options are for specific items.";
    }




}
