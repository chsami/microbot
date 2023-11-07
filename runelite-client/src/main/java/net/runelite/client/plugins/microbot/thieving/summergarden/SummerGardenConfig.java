package net.runelite.client.plugins.microbot.thieving.summergarden;

import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup(SummerGardenPlugin.CONFIG_GROUP)
public interface SummerGardenConfig extends Config {

    @ConfigItem(
            keyName = "highlightGood",
            name = "On-Parity Color",
            description = "Color to highlight elementals whose parity is correct.",
            position = 1
    )
    default Color highlightGood()
    {
        return Color.green.darker();
    }

    @ConfigItem(
            keyName = "highlightBad",
            name = "Off-Parity Color",
            description = "Color to highlight elementals whose parity is incorrect.",
            position = 2
    )
    default Color highlightBad()
    {
        return Color.orange;
    }

    @ConfigItem(
            keyName = "highlightLaunch",
            name = "Launch Color",
            description = "Color to highlight elementals when it is time to click the Sq'irk tree.",
            position = 3
    )
    default Color highlightLaunch()
    {
        return Color.decode("#00ADFF");
    }

    @ConfigItem(
            keyName = "highlightLaunchTile",
            name = "Highlight Launch Tile",
            description = "Whether or not to highlight the tile at which the first elemental will be when you click the Sq'irk tree.",
            position = 4
    )
    default boolean highlightLaunchTile()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showNumbers",
            name = "Show Numbers",
            description = "Whether to show numbers on Elementals, showing how many resets will be needed.",
            position = 5
    )
    default ShowNumbers showNumbers()
    {
        return ShowNumbers.YES;
    }

    @ConfigItem(
            keyName = "staminaWarning",
            name = "Low Stamina Threshold",
            description = "What stamina level to warn to use stamina potion (0 to disable).",
            position = 6
    )
    default int staminaThreshold()
    {
        return 25;
    }

    @ConfigItem(
            keyName = "cycleNotification",
            name = "Cycle Notification",
            description = "Whether to send a notification when the launch cycle is starting.",
            position = 7
    )
    default boolean cycleNotification()
    {
        return false;
    }

    @ConfigItem(
            keyName = "notifyTicksBeforeStart",
            name = "Notify ticks before start",
            description = "Requires \"Cycle Notification\" to be enabled. This number controls how many ticks prior to the time you will need to click the tree that the notification is sent.",
            position = 8
    )
    default int notifyTicksBeforeStart()
    {
        return 7;
    }

    @ConfigItem(
            keyName = SummerGardenPlugin.CONFIG_KEY_COUNTDOWN_TIMER_INFOBOX,
            name = "Show countdown timer",
            description = "Shows a countdown timer infobox for when it is time to start running the maze. Click on the tree when it says \"0\"",
            position = 9
    )
    @Units(Units.TICKS)
    default boolean showCountdownTimer()
    {
        return true;
    }

    @ConfigItem(
            keyName = SummerGardenPlugin.CONFIG_KEY_GATE_START,
            name = "Start on tile after gate",
            description = "Whether to start from the tile you stand on after entering the gate, rather than the normal start tile which is 3 tiles further. This saves the click of having to run to the start tile after clicking the gate.",
            position = 10
    )
    default boolean useGateStartPoint()
    {
        return true;
    }

    @ConfigItem(
            keyName = "countdownOnTreeSize",
            name = "Tree Countdown",
            description = "Font size of a countdown on the tree. Set to 0 to disable.",
            position = 11
    )
    default int countdownOnTreeSize()
    {
        return 0;
    }

    // Race-style countdown  -Green Donut
    @ConfigSection(
            name = "Race-Style Countdown",
            description = "Options for the race-style countdown",
            position = 12,
            closedByDefault = true
    )
    String raceStyleSection = "raceStyle";

    @ConfigItem(
            keyName = SummerGardenPlugin.CONFIG_KEY_RACE_STYLE_COUNTDOWN,
            name = "Enable race-style countdown",
            description = "Plays race-style countdown sounds on the last few ticks before and when the player needs to click the tree.",
            position = 1,
            section = raceStyleSection
    )
    default boolean raceStyleCountdown()
    {
        return false;
    }

    @Range(
            max = SoundEffectVolume.HIGH
    )
    @ConfigItem(
            keyName = SummerGardenPlugin.CONFIG_KEY_RACE_STYLE_VOLUME,
            name = "Race-style volume",
            description = "Configures the volume of the race-style countdown sounds.",
            position = 2,
            section = raceStyleSection
    )
    default int raceStyleVolume()
    {
        return SoundEffectVolume.MEDIUM_HIGH;
    }

    @ConfigSection(
            name = "Bot Settings",
            description = "Options for the bot",
            position = 13,
            closedByDefault = false
    )
    String botSection = "botSection";

    @ConfigItem(
            keyName = "botGuide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1,
            section = botSection
    )
    default String GUIDE() {
        return "To start this script, right click the tree at summers garden and click 'Start' or toggle this plugin on. " +
                "For full automation, enable the 'Auto Hand in and Reset' and 'Auto Maze Completion' options. " +
                "If the 'Auto Hand in and Reset' option is enabled your inventory should only contain a pestle and mortar (at the moment, it does not support stamina potions). " +
                "Otherwise, if you only have the 'Auto Maze Completion' option enabled, " +
                "you need to get enough beer glasses and manually hand in or bank your sq'irk before returning to the garden. " +
                "Stamina potions are recommended if you're only using the 'Auto Maze Completion' option (you'll need to drink them manually). " +
                "Note that if collision check is not disabled, the script might fail the first few attempts until it is tick perfect";
    }

    @ConfigItem(
            keyName = "autoMazeCompletion",
            name = "Auto Maze Completion",
            description = "Clicks the tree, makes the sq'irk juice and reenters the summer maze.",
            position = 2,
            section = botSection
    )
    default boolean autoMazeCompletion() { return false; }

    @ConfigItem(
            keyName = "handInAndReset",
            name = "Auto Hand in and Reset",
            description = "Hands in all of your juice to Osman, gets more beer glasses from the shelves and teleports to the garden.",
            position = 3,
            section = botSection
    )
    default boolean autoHandInAndReset() { return false; }

    @ConfigItem(
            keyName = "waitForOneClick",
            name = "Disable Collision Check",
            description = "Immediately clicks the tree without checking elemental NPC position. This is useful when you're in a world with many people and you aren't caught.",
            position = 4,
            section = botSection
    )
    default boolean waitForOneClick() { return false; }

    @ConfigItem(
            keyName = "sendInvFullNotification",
            name = "Full Inventory Notification",
            description = "Sends a notification in the chat box when there's no more empty beer glasses left.",
            position = 5,
            section = botSection
    )
    default boolean sendInvFullNotification() { return false; }
}
