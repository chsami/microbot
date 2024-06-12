package net.runelite.client.plugins.hoseaplugins.lucidautodialog;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("lucid-auto-dialog")
public interface LucidAutoDialogConfig extends Config
{
    // General section
    @ConfigSection(
            name = "General Settings",
            description = "Change the main settings",
            position = 0,
            closedByDefault = true
    )
    String generalSection = "General Settings";

    @ConfigItem(name = "Auto-quest helper dialog",
            description = "If there is a dialog quest helper highlights, it will select that option automatically",
            position = 0,
            keyName = "autoQuestDialog",
            section = generalSection
    )
    default boolean autoQuestDialog()
    {
        return false;
    }

    @ConfigItem(name = "Auto-continue",
            description = "If there is a 'Press space to continue' option, it will be selected automatically.",
            position = 1,
            keyName = "autoContinue",
            section = generalSection
    )
    default boolean autoContinue()
    {
        return false;
    }

    @ConfigItem(name = "Auto-select Highlighted Options",
            description = "If there is an option with a different color, select it.",
            position = 2,
            keyName = "autoSelectHighlight",
            section = generalSection
    )
    default boolean autoSelectHighlight()
    {
        return false;
    }

    // Random settings
    @ConfigSection(
            name = "Random Event Settings",
            description = "Change the main settings",
            position = 1,
            closedByDefault = true
    )
    String randomSection = "Random Event Settings";

    @ConfigItem(name = "Only When Not Animating",
            description = "Only tries to dismiss randoms if you aren't doing an animation",
            position = 0,
            keyName = "dismissNotAnimating",
            section = randomSection
    )
    default boolean dismissNotAnimating()
    {
        return false;
    }

    @ConfigItem(name = "Beekeeper Dismiss",
            description = "Dismisses Bee keeper random",
            position = 1,
            keyName = "dismissBeekeeper",
            section = randomSection
    )
    default boolean dismissBeekeeper()
    {
        return true;
    }

    @ConfigItem(name = "Capt' Arnav Dismiss",
            description = "Dismisses Capt' Arnav random",
            position = 2,
            keyName = "dismissArnav",
            section = randomSection
    )
    default boolean dismissArnav()
    {
        return true;
    }

    @ConfigItem(name = "Certers Dismiss",
            description = "Dismisses Giles/Miles/Niles Certer random",
            position = 3,
            keyName = "dismissGiles",
            section = randomSection
    )
    default boolean dismissGiles()
    {
        return true;
    }

    @ConfigItem(name = "Count Check Dismiss",
            description = "Dismisses Count Check random",
            position = 4,
            keyName = "dismissCountCheck",
            section = randomSection
    )
    default boolean dismissCountCheck()
    {
        return false;
    }

    @ConfigItem(name = "Take Count Check Lamp",
            description = "Talks to the Genie to grab that sweet lamp. I Love Lamp.",
            position = 5,
            keyName = "takeCountCheckLamp",
            section = randomSection
    )
    default boolean takeCountCheckLamp()
    {
        return false;
    }

    @ConfigItem(name = "Drill Demon Dismiss",
            description = "Dismisses Drill Demon random",
            position = 6,
            keyName = "dismissDrillDemon",
            section = randomSection
    )
    default boolean dismissDrillDemon()
    {
        return true;
    }

    @ConfigItem(name = "Drunken Dwarf Dismiss",
            description = "Dismisses Drunken Dwarf random",
            position = 7,
            keyName = "dismissDrunkenDwarf",
            section = randomSection
    )
    default boolean dismissDrunkenDwarf()
    {
        return true;
    }

    @ConfigItem(name = "Evil Bob Dismiss",
            description = "Dismisses Evil Bob random",
            position = 8,
            keyName = "dismissEvilBob",
            section = randomSection
    )
    default boolean dismissEvilBob()
    {
        return true;
    }

    @ConfigItem(name = "Evil Twin Dismiss",
            description = "Dismisses Evil Twin random",
            position = 9,
            keyName = "dismissEvilTwin",
            section = randomSection
    )
    default boolean dismissEvilTwin()
    {
        return true;
    }

    @ConfigItem(name = "Freaky Forester Dismiss",
            description = "Dismisses Freaky Forester random",
            position = 10,
            keyName = "dismissFreakyForester",
            section = randomSection
    )
    default boolean dismissFreakyForester()
    {
        return true;
    }

    @ConfigItem(name = "Genie Dismiss",
            description = "Dismisses Genie random... Why would you do that, you hate free XP or something?",
            position = 11,
            keyName = "dismissGenie",
            section = randomSection
    )
    default boolean dismissGenie()
    {
        return false;
    }

    @ConfigItem(name = "Take Genie Lamp",
            description = "Talks to the Genie to grab that sweet lamp. I Love Lamp.",
            position = 12,
            keyName = "takeGenieLamp",
            section = randomSection
    )
    default boolean takeGenieLamp()
    {
        return true;
    }

    @ConfigItem(name = "Gravedigger Dismiss",
            description = "Dismisses Gravedigger random",
            position = 13,
            keyName = "dismissGravedigger",
            section = randomSection
    )
    default boolean dismissGravedigger()
    {
        return true;
    }

    @ConfigItem(name = "Jekyll and Hyde Dismiss",
            description = "Dismisses Jekyll and Hyde random",
            position = 14,
            keyName = "dismissJekyllAndHyde",
            section = randomSection
    )
    default boolean dismissJekyllAndHyde()
    {
        return true;
    }

    @ConfigItem(name = "Kiss the Frog Dismiss",
            description = "Dismisses Kiss the Frog random",
            position = 15,
            keyName = "dismissKissTheFrog",
            section = randomSection
    )
    default boolean dismissKissTheFrog()
    {
        return true;
    }

    @ConfigItem(name = "Mysterious Old Man Dismiss",
            description = "Dismisses Mysterious Old Man random",
            position = 16,
            keyName = "dismissMysteriousOldMan",
            section = randomSection
    )
    default boolean dismissMysteriousOldMan()
    {
        return true;
    }

    @ConfigItem(name = "Pillory Dismiss",
            description = "Dismisses Pillory random",
            position = 17,
            keyName = "dismissPillory",
            section = randomSection
    )
    default boolean dismissPillory()
    {
        return true;
    }

    @ConfigItem(name = "Pinball Dismiss",
            description = "Dismisses Pinball random",
            position = 18,
            keyName = "dismissPinball",
            section = randomSection
    )
    default boolean dismissPinball()
    {
        return true;
    }

    @ConfigItem(name = "Quiz Master Dismiss",
            description = "Dismisses Quiz Master random",
            position = 19,
            keyName = "dismissQuizMaster",
            section = randomSection
    )
    default boolean dismissQuizMaster()
    {
        return true;
    }

    @ConfigItem(name = "Rick Turpentine Dismiss",
            description = "Dismisses Rick Turpentine random",
            position = 20,
            keyName = "dismissRickTurpentine",
            section = randomSection
    )
    default boolean dismissRickTurpentine()
    {
        return true;
    }

    @ConfigItem(name = "Sandwich Lady Dismiss",
            description = "Dismisses Sandwich Lady random",
            position = 21,
            keyName = "dismissSandwichLady",
            section = randomSection
    )
    default boolean dismissSandwichLady()
    {
        return true;
    }

    @ConfigItem(name = "Strange Plant Dismiss",
            description = "Dismisses Strange Plant random",
            position = 22,
            keyName = "dismissStrangePlant",
            section = randomSection
    )
    default boolean dismissStrangePlant()
    {
        return true;
    }

    @ConfigItem(name = "Surprise Exam Dismiss",
            description = "Dismisses Surprise Exam random",
            position = 23,
            keyName = "dismissSurpriseExam",
            section = randomSection
    )
    default boolean dismissSurpriseExam()
    {
        return true;
    }
}
