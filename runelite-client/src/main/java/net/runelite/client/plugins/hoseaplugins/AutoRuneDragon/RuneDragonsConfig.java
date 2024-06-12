package net.runelite.client.plugins.hoseaplugins.AutoRuneDragon;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("RuneDragons")
public interface RuneDragonsConfig extends Config
{
	@ConfigSection(name = "Tick Delay Configuration", description = "Configure how the bot handles tick delays", position = 4, closedByDefault = true)
	String tickConfig = "tickConfig";
	@ConfigSection(name = "Logic Configuration", description = "", position = 1)
	String logicConfig = "logicConfig";
	@ConfigSection(name = "Potions & Food Configuration", description = "", position = 2)
	String potionConfig = "potionConfig";
	@ConfigSection(name = "Damage Configuration", description = "", position = 3)
	String damageConfig = "damageConfig";

	@Range(min = 0, max = 10)
	@ConfigItem(keyName = "tickDelayMin", name = "Game Tick Min", description = "", position = 8, section = "tickConfig")
	default int tickDelayMin()
	{
		return 1;
	}

	@Range(min = 0, max = 10)
	@ConfigItem(keyName = "tickDelayMax", name = "Game Tick Max", description = "", position = 9, section = "tickConfig")
	default int tickDelayMax()
	{
		return 3;
	}

	@Range(min = 0, max = 10)
	@ConfigItem(keyName = "tickDelayTarget", name = "Game Tick Target", description = "", position = 10, section = "tickConfig")
	default int tickDelayTarget()
	{
		return 2;
	}

	@Range(min = 0, max = 10)
	@ConfigItem(keyName = "tickDelayDeviation", name = "Game Tick Deviation", description = "", position = 11, section = "tickConfig")
	default int tickDelayDeviation()
	{
		return 1;
	}

	@ConfigItem(keyName = "tickDelayWeightedDistribution", name = "Game Tick Weighted Distribution", description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution", position = 12, section = "tickConfig")
	default boolean tickDelayWeightedDistribution()
	{
		return false;
	}

	@ConfigItem(keyName = "usePOHpool", name = "Drink POH Pool", description = "Enable to drink from POH pool to restore HP / Prayer.", position = 60, section = "logicConfig")
	default boolean usePOHpool()
	{
		return true;
	}

	@ConfigItem(keyName = "useDivinePouch", name = "Use Divine Rune Pouch", description = "Enable to use divine rune pouch, disable to use regular.", position = 61, section = "logicConfig")
	default boolean useDivinePouch()
	{
		return false;
	}

	@ConfigItem(keyName = "lootValue", name = "Minimum loot value", description = "Provide of minimum GP value to loot", position = 63, section = "logicConfig")
	default int lootValue()
	{
		return 2000;
	}

	@ConfigItem(keyName = "supercombats", name = "Use (divine) super combats", description = "Enable to use Divine Super Combats. Disable to use regular Super Combat", position = 59, section = "potionConfig")
	default boolean supercombats()
	{
		return true;
	}

	@Range(min = 70, max = 118)
	@ConfigItem(keyName = "combatMin", name = "(Divine) super combats threshhold", description = "", position = 60, section = "potionConfig")
	default int combatMin()
	{
		return 99;
	}

	@ConfigItem(keyName = "superantifire", name = "Use extended (super) antifire", description = "Enable to use Extended Super Antifire. Disable to use regular Extended Antifire.", position = 66, section = "potionConfig")
	default boolean superantifire()
	{
		return true;
	}

	@ConfigItem(keyName = "disablePrayers", name = "Dont use prayers?", description = "TURN THIS ON -ONLY- TO USE A PRAY FLICK PLUGIN ALONGSIDE!!!", position = 68, section = "potionConfig")
	default boolean disablePrayers()
	{
		return false;
	}

	@ConfigItem(keyName = "praypotAmount", name = "Amount of Prayer Potions", description = "Amount of prayer potions to withdraw from the bank", position = 69, section = "potionConfig")
	default int praypotAmount()
	{
		return 2;
	}

	@Range(min = 0, max = 99)
	@ConfigItem(keyName = "prayerMin", name = "Prayer threshold (Min)", description = "", position = 70, section = "potionConfig")
	default int prayerMin()
	{
		return 20;
	}

	@Range(min = 0, max = 99)
	@ConfigItem(keyName = "prayerMax", name = "Prayer threshold (Max)", description = "", position = 71, section = "potionConfig")
	default int prayerMax()
	{
		return 35;
	}

	@ConfigItem(keyName = "foodID", name = "Food ID", description = "ID of food to withdraw.", position = 79, section = "potionConfig")
	default int foodID()
	{
		return 385;
	}

	@ConfigItem(keyName = "foodAmount", name = "Amount of food", description = "Amount of food to withdraw", position = 80, section = "potionConfig")
	default int foodAmount()
	{
		return 18;
	}

	@Range(min = 0, max = 99)
	@ConfigItem(keyName = "eatMin", name = "Food threshold (Min)", description = "", position = 81, section = "potionConfig")
	default int eatMin()
	{
		return 60;
	}

	@Range(min = 0, max = 99)
	@ConfigItem(keyName = "eatMin", name = "Food threshold (Max)", description = "", position = 82, section = "potionConfig")
	default int eatMax()
	{
		return 70;
	}

	@ConfigItem(keyName = "useVengeance", name = "Use Vengeance", description = "Enable to use Vengeance on purple hitsplat.", position = 85, section = "damageConfig")
	default boolean useVengeance()
	{
		return false;
	}

	@ConfigItem(keyName = "useSpec", name = "Use Spec Weapon", description = "Enable to use Spec Weapon", position = 90, section = "damageConfig")
	default boolean useSpec()
	{
		return false;
	}

	@ConfigItem(keyName = "specId", name = "Spec Weapon ID", description = "Spec weapon ID to use.", position = 91, section = "damageConfig")
	default int specId()
	{
		return 13576;
	}

	@ConfigItem(keyName = "mainId", name = "Main Weapon ID", description = "Main weapon ID to use.", position = 92, section = "damageConfig")
	default int mainId()
	{
		return 22978;
	}

	@ConfigItem(keyName = "shieldId", name = "Shield ID", description = "Shield weapon ID to use.", position = 93, section = "damageConfig")
	default int shieldId()
	{
		return 22322;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(keyName = "specTreshhold", name = "Spec threshhold", description = "Amount of spec % before using spec.", position = 96, section = "damageConfig")
	default int specTreshhold()
	{
		return 70;
	}

	@Range(min = 0, max = 330)
	@ConfigItem(keyName = "specHp", name = "Spec HP", description = "Min amount of HP before using spec.", position = 97, section = "damageConfig")
	default int specHp()
	{
		return 200;
	}

	@ConfigItem(keyName = "startButton", name = "Start/Stop", description = "Button to start or stop the plugin", position = 33)
	default boolean startButton()
	{
		return false;
	}

	@ConfigItem(keyName = "debugMode", name = "Debug", description = "Button to show debug message", position = 34)
	default boolean debugMode()
	{
		return true;
	}
}
