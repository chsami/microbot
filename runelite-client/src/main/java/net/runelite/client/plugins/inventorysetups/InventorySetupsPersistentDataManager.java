package net.runelite.client.plugins.inventorysetups;


import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.inventorysetups.serialization.InventorySetupItemSerializable;
import net.runelite.client.plugins.inventorysetups.serialization.InventorySetupItemSerializableTypeAdapter;
import net.runelite.client.plugins.inventorysetups.serialization.InventorySetupSerializable;
import net.runelite.client.plugins.inventorysetups.serialization.LongTypeAdapter;
import net.runelite.client.plugins.inventorysetups.ui.InventorySetupsPluginPanel;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin.CONFIG_GROUP;

@Slf4j
public class InventorySetupsPersistentDataManager
{

	private final MInventorySetupsPlugin plugin;
	private final InventorySetupsPluginPanel panel;
	private final ConfigManager configManager;
	private final InventorySetupsCache cache;

	private Gson gson;

	private final List<InventorySetup> inventorySetups;
	private final List<InventorySetupsSection> sections;

	public static final String CONFIG_KEY_SETUPS_MIGRATED_V2 = "migratedV2";
	public static final String CONFIG_KEY_SETUPS = "setups";
	public static final String CONFIG_KEY_SETUPS_V2 = "setupsV2";
	public static final String CONFIG_KEY_SECTIONS = "sections";

	@Inject
	public InventorySetupsPersistentDataManager(final MInventorySetupsPlugin plugin,
												final InventorySetupsPluginPanel panel,
												final ConfigManager manager,
												final InventorySetupsCache cache,
												final Gson gson,
												final List<InventorySetup> inventorySetups,
												final List<InventorySetupsSection> sections)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.configManager = manager;
		this.cache = cache;
		this.gson = gson;
		this.inventorySetups = inventorySetups;
		this.sections = sections;

		this.gson = this.gson.newBuilder().registerTypeAdapter(long.class, new LongTypeAdapter()).create();
		this.gson = this.gson.newBuilder().registerTypeAdapter(InventorySetupItemSerializable.class, new InventorySetupItemSerializableTypeAdapter()).create();
	}

	public void loadConfig()
	{
		inventorySetups.clear();
		sections.clear();
		cache.clearAll();

		// Handles migration of old setup data
		handleMigrationOfOldData();

		Type setupTypeV2 = new TypeToken<ArrayList<InventorySetupSerializable>>()
		{

		}.getType();
		List<InventorySetupSerializable> issList = new ArrayList<>(loadData(CONFIG_KEY_SETUPS_V2, setupTypeV2));
		for (final InventorySetupSerializable iss : issList)
		{
			inventorySetups.add(InventorySetupSerializable.convertToInventorySetup(iss));
		}
		processSetupsFromConfig();

		Type sectionType = new TypeToken<ArrayList<InventorySetupsSection>>()
		{

		}.getType();
		sections.addAll(loadData(CONFIG_KEY_SECTIONS, sectionType));
		for (final InventorySetupsSection section : sections)
		{
			final String newName = InventorySetupUtilities.findNewName(section.getName(), cache.getSectionNames().keySet());
			section.setName(newName);

			// Remove any duplicates that exist
			List<String> uniqueSetups = section.getSetups().stream().distinct().collect(Collectors.toList());
			section.setSetups(uniqueSetups);

			// Remove setups which don't exist in a section
			section.getSetups().removeIf(s -> !cache.getInventorySetupNames().containsKey(s));
			cache.addSection(section);
		}

	}

	public void updateConfig(boolean updateSetups, boolean updateSections)
	{
		if (updateSetups)
		{
			List<InventorySetupSerializable> issList = new ArrayList<>();
			for (final InventorySetup setup : inventorySetups)
			{
				issList.add(InventorySetupSerializable.convertFromInventorySetup(setup));
			}

			final String data = gson.toJson(issList);
			configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_SETUPS_V2, data);
		}

		if (updateSections)
		{
			final String jsonSections = gson.toJson(sections);
			configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_SECTIONS, jsonSections);
		}

	}

	private <T> List<T> loadData(final String configKey, Type type)
	{
		final String storedData = configManager.getConfiguration(CONFIG_GROUP, configKey);
		if (Strings.isNullOrEmpty(storedData))
		{
			return new ArrayList<>();
		}
		else
		{
			try
			{
				// serialize the internal data structure from the json in the configuration
				return gson.fromJson(storedData, type);
			}
			catch (Exception e)
			{
				log.error("Exception occurred while loading data", e);
				return new ArrayList<>();
			}
		}
	}

	private void processSetupsFromConfig()
	{
		for (final InventorySetup setup : inventorySetups)
		{
			final InventorySetupsRunePouchType runePouchType = plugin.getRunePouchTypeFromContainer(setup.getInventory());
			if (setup.getRune_pouch() == null && runePouchType != InventorySetupsRunePouchType.NONE)
			{
				setup.updateRunePouch(plugin.getRunePouchData(runePouchType));
			}
			if (setup.getBoltPouch() == null && plugin.containerContainsBoltPouch(setup.getInventory()))
			{
				setup.updateBoltPouch(plugin.getBoltPouchData());
			}
			if (setup.getNotes() == null)
			{
				setup.updateNotes("");
			}
			if (setup.getAdditionalFilteredItems() == null)
			{
				setup.updateAdditionalItems(new HashMap<>());
			}

			final String newName = InventorySetupUtilities.findNewName(setup.getName(), cache.getInventorySetupNames().keySet());
			setup.setName(newName);
			cache.addSetup(setup);

			// add Item names
			addItemNames(setup.getInventory());
			addItemNames(setup.getEquipment());
			addItemNames(setup.getRune_pouch());
			addItemNames(setup.getBoltPouch());
			for (final Integer key : setup.getAdditionalFilteredItems().keySet())
			{
				addItemName(setup.getAdditionalFilteredItems().get(key));
			}

		}

	}

	private void addItemNames(final List<InventorySetupsItem> items)
	{
		if (items != null)
		{
			for (final InventorySetupsItem item : items)
			{
				addItemName(item);
			}
		}
	}

	private void addItemName(final InventorySetupsItem item)
	{
		item.setName(plugin.getItemManager().getItemComposition(item.getId()).getName());
	}

	private void handleMigrationOfOldData()
	{
		String hasMigratedToV2 = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_SETUPS_MIGRATED_V2);
		if (Strings.isNullOrEmpty(hasMigratedToV2))
		{
			log.info("Migrating data to V2");
			// Perform migration of old data
			Type setupType = new TypeToken<ArrayList<InventorySetup>>()
			{

			}.getType();
			inventorySetups.addAll(loadData(CONFIG_KEY_SETUPS, setupType));
			updateConfig(true, false);
			inventorySetups.clear();
			configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY_SETUPS_MIGRATED_V2, "True");
		}

		// TODO Don't unset configuration until new version is stable
//		hasMigratedToV2 = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_SETUPS_MIGRATED_V2);
//		if (!Strings.isNullOrEmpty(hasMigratedToV2))
//		{
//			String oldData = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY_SETUPS);
//			if (oldData != null)
//			{
//				log.info("Removing old data key");
//				configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY_SETUPS);
//			}
//		}
	}

	private String fixOldJSONData(final String json)
	{
		JsonElement je = this.gson.fromJson(json, JsonElement.class);
		JsonArray ja = je.getAsJsonArray();
		for (JsonElement elem : ja)
		{
			JsonObject setup = elem.getAsJsonObject();

			// Example if needed in the future
//			if (setup.getAsJsonPrimitive("stackDifference").isBoolean())
//			{
//				int stackDiff = setup.get("stackDifference").getAsBoolean() ? 1 : 0;
//				setup.remove("stackDifference");
//				setup.addProperty("stackDifference", stackDiff);
//			}
		}
		return je.toString();
	}


}
