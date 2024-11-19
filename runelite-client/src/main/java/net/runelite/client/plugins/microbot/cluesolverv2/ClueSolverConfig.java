package net.runelite.client.plugins.microbot.cluesolverv2;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInformation;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("cluesolverv2")
@ConfigInformation("- Enable the official RuneLite plugin 'Clue Scroll'<br />" +
        "  <br />" +
        "1. Configure the delay between task completions<br />" +
        "- This is a WIP and  WILL not fully automate the clue scrolls without issues.<br />" +
        "  <br />" +
        "2. If you have any issues or suggestions, please inform me on discord. <br />" +
        "- Currently you need to open the bank and input bankpin<br />" +
        "- You need to have the clue in your inventory and click on it with both plugins active.<br />" +
        "- It will not buy items for you, however it will attempt to bank and check for the items and proceed to withdraw.<br />" +
        "  <br />" +
        "- I would also suggest that you keep an eye on it at all times. <br />" +
        "  <br />" +
        "- 'More features to come'")
public interface ClueSolverConfig extends Config {

    // General Toggle
    @ConfigItem(
            keyName = "toggleAll",
            name = "Enable Clue Solver",
            description = "Enable or disable all clue-solving tasks"
    )
    default boolean toggleAll() {
        return false;
    }

    // Task Interval Setting
    @ConfigItem(
            keyName = "taskInterval",
            name = "Task Interval",
            description = "Interval between clue-solving actions (in milliseconds)"
    )
    default int taskInterval() {
        return 1000;
    }

//    // Additional Settings
//    @ConfigItem(
//            keyName = "enableHints",
//            name = "Enable Clue Hints",
//            description = "Placeholder"
//    )
//    default boolean enableHints() {
//        return false;
//    }

//    @ConfigItem(
//            keyName = "enableHintArrows",
//            name = "Enable Hint Arrows",
//            description = "Display hint arrows pointing to target locations or NPCs"
//    )
//    default boolean enableHintArrows() {
//        return true;
//    }

    // Cooldown Control Between Tasks
    @ConfigItem(
            keyName = "cooldownBetweenTasks",
            name = "Cooldown Between Tasks",
            description = "Cooldown time (in milliseconds) between clue-solving tasks"
    )
    default int cooldownBetweenTasks() {
        return 1500;
    }
}
