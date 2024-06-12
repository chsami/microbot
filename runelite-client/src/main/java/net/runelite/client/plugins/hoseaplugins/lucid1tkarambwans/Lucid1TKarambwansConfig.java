package net.runelite.client.plugins.hoseaplugins.lucid1tkarambwans;

import lombok.Getter;
import net.runelite.client.config.*;

@ConfigGroup("lucid-1tkarambwans")
public interface Lucid1TKarambwansConfig extends Config
{
    // Auto section
    @ConfigSection(
            name = "Auto Settings",
            description = "Change the main auto settings",
            position = 0,
            closedByDefault = true
    )
    String autoSection = "Auto Settings";

    @ConfigItem(name = "Show Overlay",
            description = "Shows an overlay with some info about the plugin",
            position = 0,
            keyName = "showOverlay",
            section = autoSection
    )
    default boolean showOverlay()
    {
        return false;
    }

    @ConfigItem(name = "Auto Toggle",
            description = "Hotkey to toggle auto",
            position = 1,
            keyName = "autoToggle",
            section = autoSection
    )
    default Keybind autoToggle()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Cooking Location",
            description = "Select a pre-defined location or use custom values",
            position = 2,
            keyName = "cookingLocation",
            section = autoSection
    )
    default CookingLocation cookingLocation()
    {
        return CookingLocation.CUSTOM;
    }

    // Custom section
    @ConfigSection(
            name = "Custom Location Settings",
            description = "Change the main auto settings",
            position = 1,
            closedByDefault = true
    )
    String customSection = "Custom Location Settings";
    @ConfigItem(name = "Cooking Range ID",
            description = "ID of cooking range to 1 tick on (Myths guild range is default)",
            position = 0,
            keyName = "rangeId",
            section = customSection
    )
    default int rangeId()
    {
        return 31631;
    }

    @ConfigItem(
            name = "Bank Type",
            description = "Is the bank an npc or object?",
            position = 1,
            keyName = "bankType",
            section = customSection

    )
    default BankingType bankType()
    {
        return BankingType.OBJECT;
    }

    @ConfigItem(name = "Bank Name",
            description = "Name of the Bank object/npc you are using",
            position = 2,
            keyName = "bankName",
            section = customSection
    )
    default String bankName()
    {
        return "Bank chest";
    }

    @ConfigItem(name = "Bank Action",
            description = "Name of the action that opens the bank",
            position = 3,
            keyName = "bankAction",
            section = customSection
    )
    default String bankAction()
    {
        return "Use";
    }

    // Anti-ban section
    @ConfigSection(
            name = "Anti-ban Settings",
            description = "Change the anti-ban settings",
            position = 2,
            closedByDefault = true
    )
    String antibanSection = "Anti-ban Settings";

    @ConfigItem(name = "Take Breaks",
            description = "Will take breaks based on the below settings.",
            position = 0,
            keyName = "takeBreaks",
            section = antibanSection
    )
    default boolean takeBreaks()
    {
        return false;
    }

    @ConfigItem(name = "Break Every X Ticks",
            description = "How often to take breaks",
            position = 1,
            keyName = "breakAfter",
            section = antibanSection
    )
    default int breakAfter()
    {
        return 6000;
    }

    @ConfigItem(name = "Break For X Ticks",
            description = "How long to take breaks",
            position = 2,
            keyName = "breakFor",
            section = antibanSection
    )
    default int breakFor()
    {
        return 200;
    }

    @ConfigItem(name = "Randomly Miss Ticks",
            description = "Will randomly miss a tick so that you're not 100% tick perfect.",
            position = 3,
            keyName = "randomlyMiss",
            section = antibanSection
    )
    default boolean randomlyMiss()
    {
        return false;
    }

    @ConfigItem(name = "Missed Ticks Per Hour",
            description = "How many ticks per hour should be missed randomly. There are ~5k cooking ticks per hour.",
            position = 4,
            keyName = "missedPerHour",
            section = antibanSection
    )
    default int missedPerHour()
    {
        return 50;
    }

    enum CookingLocation
    {
        CATHERBY(26181, "Bank booth", "Bank", BankingType.OBJECT),
        MYTHS_GUILD(31631, "Bank chest", "Use", BankingType.OBJECT),
        HOSIDIUS_KITCHEN(21302, "Bank chest", "Use", BankingType.OBJECT),
        ROGUES_DEN(43475, "Emerald Benedict", "Bank", BankingType.NPC),
        CUSTOM(-1, "", "", null);

        @Getter
        final int rangeId;
        @Getter
        final String bankName;
        @Getter
        final String bankAction;
        @Getter
        final BankingType bankingType;
        CookingLocation(int rangeId, String bankName, String bankAction, BankingType bankingType)
        {
            this.rangeId = rangeId;
            this.bankName = bankName;
            this.bankAction = bankAction;
            this.bankingType = bankingType;
        }
    }

    enum BankingType
    {
        OBJECT, NPC
    }
}