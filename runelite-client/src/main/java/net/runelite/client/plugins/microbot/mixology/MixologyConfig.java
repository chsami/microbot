package net.runelite.client.plugins.microbot.mixology;

import net.runelite.client.config.*;
import org.jetbrains.annotations.Range;

@ConfigGroup("mixology")
@ConfigInformation("<p>Start the script at the bankchest of the mixology minigame room with an empty inventory.</p> <br />" +
        "<br />Requirements: " +
        "<ol> " +
        "<li>60 Herblore</li>" +
        "<li>Herbs for making mox/aga/lye paste</li>" +
        "</ol>")
public interface MixologyConfig extends Config {
    @ConfigSection(
            name = "Refiner",
            description = "Refiner configuration",
            position = 0
    )
    String refiner = "Refiner";

    @ConfigSection(
            name = "Minigame",
            description = "General minigame configuration",
            position = 1
    )
    String minigame = "Minigame";

    @ConfigItem(
            keyName = "RefinerHerbMox",
            name = "Refining Mox Herb",
            description = "Refine herbs into mox paste",
            position = 0,
            section = refiner
    )
    default MoxHerbs moxHerb() {
        return MoxHerbs.GUAM;
    }
    @Range(from = 100, to = 3000)
    @ConfigItem(
            keyName = "RefinerHerbMoxAmt",
            name = "Refining Mox Herb Amount",
            description = "Amount of herbs to refine into mox paste",
            position = 1,
            section = refiner
    )
    default int amtMoxHerb() {
        return 1000;
    }

    @ConfigItem(
            keyName = "RefinerHerbLye",
            name = "Refining Lye Herb",
            description = "Refine herbs into lye paste",
            position = 2,
            section = refiner
    )
    default LyeHerbs lyeHerb() {
        return LyeHerbs.Ranarr;
    }
    @Range(from = 100, to = 3000)
    @ConfigItem(
            keyName = "RefinerHerbLyeAmt",
            name = "Refining lye Herb Amount",
            description = "Amount of herbs to refine into lye paste",
            position = 3,
            section = refiner
    )
    default int amtLyeHerb() {
        return 1000;
    }

    @ConfigItem(
            keyName = "RefinerHerbAga",
            name = "Refining Aga Herb",
            description = "Refine herbs into aga paste",
            position = 4,
            section = refiner
    )
    default AgaHerbs agaHerb() {
        return AgaHerbs.Irit;
    }
    @Range(from = 100, to = 3000)
    @ConfigItem(
            keyName = "RefinerHerbAgaAmt",
            name = "Refining Aga Herb Amount",
            description = "Amount of herbs to refine into aga paste",
            position = 5,
            section = refiner
    )
    default int amtAgaHerb() {
        return 1000;
    }

    @ConfigItem(
            keyName = "useQuickActionRefiner",
            name = "Use Quick Action on Refiner",
            description = "Will click while paste to allow for faster completion of the task",
            position = 5,
            section = refiner
    )
    default boolean useQuickActionRefiner() {
        return true;
    }

    // -- MINIGAME SECTION -- //

    @ConfigItem(
            keyName = "useQuickActionAlembic",
            name = "Use Quick Action on Alembic",
            description = "Will click once there is a quick action available on the alembic",
            position = 0,
            section = minigame
    )
    default boolean useQuickActionOnAlembic() {
        return true;
    }
    @ConfigItem(
            keyName = "useQuickActionAgitator",
            name = "Use Quick Action on Agitator",
            description = "Will click once there is a quick action available on the agitator",
            position = 1,
            section = minigame
    )
    default boolean useQuickActionOnAgitator() {
        return true;
    }

    @ConfigItem(
            keyName = "useQuickActionRetort",
            name = "Use Quick Action on Retort",
            description = "Will click once there is a quick action available on the retort",
            position = 2,
            section = minigame
    )
    default boolean useQuickActionOnRetort() {
        return true;
    }

    @ConfigItem(
            keyName = "pickDigWeed",
            name = "Pick DigWeed",
            description = "Will pick digweed if available to increase points",
            position = 3,
            section = minigame
    )
    default boolean pickDigWeed() {
        return true;
    }

    @ConfigItem(
            keyName = "useQuickActionLever",
            name = "Use Quick Action on Lever",
            description = "Will click fast when interacting with the lever for mixing potions",
            position = 4,
            section = minigame
    )
    default boolean useQuickActionLever() {
        return true;
    }
}
