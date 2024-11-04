package net.runelite.client.plugins.microbot.quest;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInformation;

@ConfigGroup("quest")
@ConfigInformation("<b>This is a helper plugin.</b> <br /> " +
        "This plugin assists you in completing quests by using the quest helper from runelite <br />" +
        "<b>How to use it:</b> <br />" +
        "1. Start a quest as you normally would in RuneLite with MQuester enabled. <br/>" +
        "2. MQuester will guide you through the quest by walking to locations, picking up items, and interacting with NPCs. <br />" +
        "<b>Important: </b> <br/>" +
        "MQuester will <b>NOT</b> fetch items from your bank or buy them from the Grand Exchange. Make sure you have all required quest items ready before you start.")
public interface MQuestConfig extends Config {
}
