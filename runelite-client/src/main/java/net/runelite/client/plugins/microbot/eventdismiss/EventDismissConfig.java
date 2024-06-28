package net.runelite.client.plugins.microbot.eventdismiss;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("EventDismiss")
public interface EventDismissConfig extends Config {

    @ConfigItem(
            name = "Beekeeper Dismiss",
            keyName = "dismissBeekeeper",
            position = 0,
            description = "Dismiss Beekeeper random event"
    )
    default boolean dismissBeekeeper() {
        return true;
    }

    @ConfigItem(
            name = "Capt' Arnav Dismiss",
            keyName = "dismissCaptArnav",
            position = 1,
            description = "Dismiss Capt' Arnav random event"
    )
    default boolean dismissArnav() {
        return true;
    }

    @ConfigItem(
            name = "Certers Dismiss",
            keyName = "dismissCerters",
            position = 2,
            description = "Dismiss Giles, Miles, and Niles Certer random events"
    )
    default boolean dismissCerters() {
        return true;
    }

    @ConfigItem(
            name = "Count Check Dismiss",
            keyName = "dismissCountCheck",
            position = 3,
            description = "Dismiss Count Check random event"
    )
    default boolean dismissCountCheck() {
        return false;
    }

    @ConfigItem(
            name = "Drill Demon Dismiss",
            keyName = "dismissDrillDemon",
            position = 4,
            description = "Dismiss Drill Demon random event"
    )
    default boolean dismissDrillDemon() {
        return true;
    }

    @ConfigItem(
            name = "Drunken Dwarf Dismiss",
            keyName = "dismissDrunkenDwarf",
            position = 5,
            description = "Dismiss Drunken Dwarf random event"
    )
    default boolean dismissDrunkenDwarf() {
        return true;
    }

    @ConfigItem(
            name = "Evil Bob Dismiss",
            keyName = "dismissEvilBob",
            position = 6,
            description = "Dismiss Evil Bob random event"
    )
    default boolean dismissEvilBob() {
        return true;
    }

    @ConfigItem(
            name = "Evil Twin Dismiss",
            keyName = "dismissEvilTwin",
            position = 7,
            description = "Dismiss Evil Twin random event"
    )
    default boolean dismissEvilTwin() {
        return true;
    }

    @ConfigItem(
            name = "Freaky Forester Dismiss",
            keyName = "dismissFreakyForester",
            position = 8,
            description = "Dismiss Freaky Forester random event"
    )
    default boolean dismissFreakyForester() {
        return true;
    }

    @ConfigItem(
            name = "Genie Dismiss",
            keyName = "dismissGenie",
            position = 9,
            description = "Dismiss Genie random event"
    )
    default boolean dismissGenie() {
        return false;
    }

    @ConfigItem(
            name = "Gravedigger Dismiss",
            keyName = "dismissGravedigger",
            position = 10,
            description = "Dismiss Gravedigger random event"
    )
    default boolean dismissGravedigger() {
        return true;
    }

    @ConfigItem(
            name = "Jekyll and Hyde Dismiss",
            keyName = "dismissJekyllAndHyde",
            position = 11,
            description = "Dismiss Jekyll and Hyde random events"
    )
    default boolean dismissJekyllAndHyde() {
        return true;
    }

    @ConfigItem(
            name = "Kiss the Frog Dismiss",
            keyName = "dismissKissTheFrog",
            position = 12,
            description = "Dismiss Kiss the Frog random event"
    )
    default boolean dismissKissTheFrog() {
        return true;
    }

    @ConfigItem(
            name = "Mysterious Old Man Dismiss",
            keyName = "dismissMysteriousOldMan",
            position = 13,
            description = "Dismiss Mysterious Old Man random event"
    )
    default boolean dismissMysteriousOldMan() {
        return true;
    }

    @ConfigItem(
            name = "Pillory Dismiss",
            keyName = "dismissPillory",
            position = 14,
            description = "Dismiss Pillory random event"
    )
    default boolean dismissPillory() {
        return true;
    }

    @ConfigItem(
            name = "Pinball Dismiss",
            keyName = "dismissPinball",
            position = 15,
            description = "Dismiss Pinball random events"
    )
    default boolean dismissPinball() {
        return true;
    }

    @ConfigItem(
            name = "Quiz Master Dismiss",
            keyName = "dismissQuizMaster",
            position = 16,
            description = "Dismiss Quiz Master random event"
    )
    default boolean dismissQuizMaster() {
        return true;
    }

    @ConfigItem(
            name = "Rick Turpentine Dismiss",
            keyName = "dismissRickTurpentine",
            position = 17,
            description = "Dismiss Rick Turpentine random event"
    )
    default boolean dismissRickTurpentine() {
        return true;
    }

    @ConfigItem(
            name = "Sandwich Lady Dismiss",
            keyName = "dismissSandwichLady",
            position = 18,
            description = "Dismiss Sandwich Lady random event"
    )
    default boolean dismissSandwichLady() {
        return true;
    }

    @ConfigItem(
            name = "Strange Plant Dismiss",
            keyName = "dismissStrangePlant",
            position = 19,
            description = "Dismiss Strange Plant random event"
    )
    default boolean dismissStrangePlant() {
        return true;
    }

    @ConfigItem(
            name = "Surprise Exam Dismiss",
            keyName = "dismissSurpriseExam",
            position = 20,
            description = "Dismiss Surprise Exam random event"
    )
    default boolean dismissSurpriseExam() {
        return true;
    }

}
