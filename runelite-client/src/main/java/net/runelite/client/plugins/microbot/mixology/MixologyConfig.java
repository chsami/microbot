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
            description = "General configuration",
            position = 0
    )
    String refiner = "Refiner";

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

}
