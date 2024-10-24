 package net.runelite.client.plugins.microbot.PlayerAlarm;
 
 import java.awt.Color;
 import net.runelite.client.config.Alpha;
 import net.runelite.client.config.Config;
 import net.runelite.client.config.ConfigGroup;
 import net.runelite.client.config.ConfigItem;
 import net.runelite.client.config.Range;
 

 @ConfigGroup("PlayerAlarm")
 public interface PlayerAlarmConfig
   extends Config
 {
   @Range(max = 30, min = 0)
   @ConfigItem(keyName = "alarmRadius", name = "Alarm radius", description = "Distance for another player to trigger the alarm. WARNING: Players within range that are not rendered will not trigger the alarm.", position = 0)
   default int alarmRadius() { return 15; }
   @ConfigItem(keyName = "desktopNotification", name = "Desktop notification", description = "Receive a desktop notification when the alarm triggers", position = 1)
   default boolean desktopNotification() { return false; }
   @ConfigItem(keyName = "pvpWorldAlerts", name = "Pvp world alerts", description = "Will alert you anywhere when in pvp worlds", position = 2)
   default boolean pvpWorldAlerts() { return false; }
   @ConfigItem(keyName = "ignoreFriends", name = "Ignore friends", description = "Do not alarm for players on your friends list", position = 3)
   default boolean ignoreFriends() { return true; }
   @ConfigItem(keyName = "ignoreClan", name = "Ignore clan", description = "Do not alarm for players in your clan", position = 4)
   default boolean ignoreClan() { return true; }
   @ConfigItem(keyName = "ignoreFriendsChat", name = "Ignore friends chat", description = "Do not alarm for players in the same friends chat as you", position = 5)
   default boolean ignoreFriendsChat() { return false; }
   @ConfigItem(keyName = "ignoreIgnored", name = "Ignore 'ignore list'", description = "Do not alarm for players on your ignore list", position = 6)
   default boolean ignoreIgnored() { return false; }
   @ConfigItem(keyName = "timeoutToIgnore", name = "Timeout", description = "Ignores players after they've been present for the specified time (in seconds). A value of 0 means players won't be ignored regardless of how long they are present.", position = 7)
   default int timeoutToIgnore() { return 0; }
   @Alpha
   @ConfigItem(keyName = "flashColor", name = "Flash color", description = "Sets the color of the alarm flashes", position = 8)
   default Color flashColor()
   { return new Color(255, 255, 0, 70); }
   @ConfigItem(keyName = "playSound", name = "Play Sound?", description = "Would you like the alarm to be audible as well?", position = 9)
   default boolean playSound() { return true; }
 }