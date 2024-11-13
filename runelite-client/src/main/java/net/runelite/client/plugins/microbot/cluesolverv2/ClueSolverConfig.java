package net.runelite.client.plugins.microbot.cluesolverv2;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("cluesolverv2")
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
