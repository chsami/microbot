package net.runelite.client.plugins.microbot.inventorysetups;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

// Class to assist with speeding up operations by caching names when the config is loaded
public class InventorySetupsCache
{
	public InventorySetupsCache()
	{
		this.inventorySetupNames = new HashMap<>();
		this.sectionNames = new HashMap<>();
		this.setupSectionsMap = new HashMap<>();
		this.sectionSetupsMap = new HashMap<>();
	}

	public void addSetup(final InventorySetup setup)
	{
		inventorySetupNames.put(setup.getName(), setup);
		setupSectionsMap.put(setup.getName(), new HashMap<>());
	}

	public void addSection(final InventorySetupsSection section)
	{
		sectionNames.put(section.getName(), section);
		sectionSetupsMap.put(section.getName(), new HashMap<>());

		// If we are importing a section, it can have setups already associated with it
		// In this case, this function assumes the setups already exist
		for (final String setupName : section.getSetups())
		{
			addSetupToSection(section, inventorySetupNames.get(setupName));
		}
	}

	public void updateSetupName(final InventorySetup setup, final String newName)
	{
		inventorySetupNames.remove(setup.getName());
		inventorySetupNames.put(newName, setup);

		// Update the setup in each section -> setups map
		for (final String sectionName : sectionSetupsMap.keySet())
		{
			if (sectionSetupsMap.get(sectionName).containsKey(setup.getName()))
			{
				sectionSetupsMap.get(sectionName).remove(setup.getName());
				sectionSetupsMap.get(sectionName).put(newName, setup);
			}
		}

		// Update the key with the new name
		setupSectionsMap.put(newName, setupSectionsMap.remove(setup.getName()));
	}

	public void updateSectionName(final InventorySetupsSection section, final String newName)
	{
		sectionNames.remove(section.getName());
		sectionNames.put(newName, section);

		// Update the section in each setup -> section map
		for (final String setupName : setupSectionsMap.keySet())
		{
			if (setupSectionsMap.get(setupName).containsKey(section.getName()))
			{
				setupSectionsMap.get(setupName).remove(section.getName());
				setupSectionsMap.get(setupName).put(newName, section);
			}
		}

		// Update the key with the new name
		sectionSetupsMap.put(newName, sectionSetupsMap.remove(section.getName()));
	}

	public void removeSetup(final InventorySetup setup)
	{
		inventorySetupNames.remove(setup.getName());
		setupSectionsMap.remove(setup.getName());

		// Remove the setup for each section in the section -> setups map
		for (final String sectionName : sectionSetupsMap.keySet())
		{
			sectionSetupsMap.get(sectionName).remove(setup.getName());
		}
	}

	public void removeSection(final InventorySetupsSection section)
	{
		sectionNames.remove(section.getName());
		sectionSetupsMap.remove(section.getName());

		// Remove the section for each setup in the setup -> sections map
		for (final String setupName : section.getSetups())
		{
			setupSectionsMap.get(setupName).remove(section.getName());
		}
	}

	public void addSetupToSection(final InventorySetupsSection section, final InventorySetup setup)
	{
		setupSectionsMap.get(setup.getName()).put(section.getName(), section);
		sectionSetupsMap.get(section.getName()).put(setup.getName(), setup);
	}

	public void removeSetupFromSection(final InventorySetupsSection section, final InventorySetup setup)
	{
		setupSectionsMap.get(setup.getName()).remove(section.getName());
		sectionSetupsMap.get(section.getName()).remove(setup.getName());
	}

	public void clearAll()
	{
		inventorySetupNames.clear();
		sectionNames.clear();
		setupSectionsMap.clear();
		sectionSetupsMap.clear();
	}

	// Mapping from inventory setup name -> inventory setup object
	@Getter
	private final Map<String, InventorySetup> inventorySetupNames;

	// Mapping from section name -> section object
	@Getter
	private final Map<String, InventorySetupsSection> sectionNames;

	// Mapping from setup name -> Map of name to section for each section the setup is a part of
	// Useful for determining if it should be added to the "unassigned" section
	@Getter
	private final Map<String, Map<String, InventorySetupsSection>> setupSectionsMap;

	// Mapping from section -> Map of name to setup for each setup that is part of the section
	// Useful for determining the intersection of setups to display and setups in a section
	@Getter
	private final Map<String, Map<String, InventorySetup>> sectionSetupsMap;
}
