package net.runelite.client.plugins.microbot.driftnet;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(DriftNetPlugin.CONFIG_GROUP)
@ConfigInformation("Start this script at the driftet fishing area. <br /> Make sure to have driftnet in your inventory or driftnet stored with anetta.")
public interface DriftNetConfig extends Config {

        @ConfigItem(
                keyName = "guide",
                name = "How to use",
                description = "How to use this plugin",
                position = 1
        )
        default String GUIDE() {
            return "Start at the driftnet area\n" +
                    "MUST HAVE DRIFTNET IN INVENTORY OR STORED WITH THE NPC";
        }

        @ConfigItem(
                position = 1,
                keyName = "showNetStatus",
                name = "Show net status",
                description = "Show net status and fish count"
        )
        default boolean showNetStatus() {
            return true;
        }

        @ConfigItem(
                position = 2,
                keyName = "countColor",
                name = "Fish count color",
                description = "Color of the fish count text"
        )
        default Color countColor() {
            return Color.WHITE;
        }

        @ConfigItem(
                position = 3,
                keyName = "highlightUntaggedFish",
                name = "Highlight untagged fish",
                description = "Highlight the untagged fish"
        )
        default boolean highlightUntaggedFish() {
            return true;
        }

        @ConfigItem(
                position = 4,
                keyName = "timeoutDelay",
                name = "Tagged timeout",
                description = "Time required for a tag to expire"
        )
        @Range(
                min = 1,
                max = 100
        )
        @Units(Units.TICKS)
        default int timeoutDelay() {
            return 60;
        }

        @Alpha
        @ConfigItem(
                keyName = "untaggedFishColor",
                name = "Untagged fish color",
                description = "Color of untagged fish",
                position = 5
        )
        default Color untaggedFishColor() {
            return Color.CYAN;
        }

        @ConfigItem(
                keyName = "tagAnnette",
                name = "Tag Annette",
                description = "Tag Annette when no nets in inventory",
                position = 6
        )
        default boolean tagAnnetteWhenNoNets() {
            return true;
        }

        @Alpha
        @ConfigItem(
                keyName = "annetteTagColor",
                name = "Annette tag color",
                description = "Color of Annette tag",
                position = 7
        )
        default Color annetteTagColor() {
            return Color.RED;
        }
}
