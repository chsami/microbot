package net.runelite.client.plugins.microbot.inventorysetups;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemVariationMapping;

public class InventorySetupsVariationMapping
{
	private static final Map<Integer, Integer> mappings;

	public InventorySetupsVariationMapping()
	{
	}

	public static int map(final Integer id)
	{
		int mappedId = ItemVariationMapping.map(id);

		// if the mapped ID is equal to the original id
		// this means there was no mapping for this id. Try the extra custom mappings
		if (mappedId == id)
		{
			mappedId = mappings.getOrDefault(id, id);
		}

		return mappedId;
	}

	static
	{
		mappings = new HashMap<>();

		// Granite Cannonball -> Cannonball
		mappings.put(ItemID.GRANITE_CANNONBALL, ItemID.CANNONBALL);

		// Smith Gloves (i) act as ice gloves
		mappings.put(ItemID.SMITHS_GLOVES_I, ItemID.ICE_GLOVES);

		// Divine rune pouch -> Rune Pouch
		mappings.put(ItemID.DIVINE_RUNE_POUCH, ItemID.RUNE_POUCH);

		// Make god capes the same
		final int itemIDGodCape = 1000000001;
		mappings.put(ItemID.SARADOMIN_CAPE, itemIDGodCape);
		mappings.put(ItemID.GUTHIX_CAPE, itemIDGodCape);
		mappings.put(ItemID.ZAMORAK_CAPE, itemIDGodCape);
		final int itemIDImbuedGodCape = 1000000002;
		mappings.put(ItemID.IMBUED_SARADOMIN_CAPE, itemIDImbuedGodCape);
		mappings.put(ItemID.IMBUED_GUTHIX_CAPE, itemIDImbuedGodCape);
		mappings.put(ItemID.IMBUED_ZAMORAK_CAPE, itemIDImbuedGodCape);
		final int itemIDGodMaxCape = 1000000003;
		mappings.put(ItemID.SARADOMIN_MAX_CAPE, itemIDGodMaxCape);
		mappings.put(ItemID.GUTHIX_MAX_CAPE, itemIDGodMaxCape);
		mappings.put(ItemID.ZAMORAK_MAX_CAPE, itemIDGodMaxCape);
		final int itemIDImbuedGodMaxCape = 1000000004;
		mappings.put(ItemID.IMBUED_SARADOMIN_MAX_CAPE, itemIDImbuedGodMaxCape);
		mappings.put(ItemID.IMBUED_GUTHIX_MAX_CAPE, itemIDImbuedGodMaxCape);
		mappings.put(ItemID.IMBUED_ZAMORAK_MAX_CAPE, itemIDImbuedGodMaxCape);

		// Make god d'hides the same
		final int itemIDGodCoif = 1000000005;
		mappings.put(ItemID.ANCIENT_COIF, itemIDGodCoif);
		mappings.put(ItemID.ARMADYL_COIF, itemIDGodCoif);
		mappings.put(ItemID.BANDOS_COIF, itemIDGodCoif);
		mappings.put(ItemID.GUTHIX_COIF, itemIDGodCoif);
		mappings.put(ItemID.SARADOMIN_COIF, itemIDGodCoif);
		mappings.put(ItemID.ZAMORAK_COIF, itemIDGodCoif);

		final int itemIDGodDhideBody = 1000000006;
		mappings.put(ItemID.ANCIENT_DHIDE_BODY, itemIDGodDhideBody);
		mappings.put(ItemID.ARMADYL_DHIDE_BODY, itemIDGodDhideBody);
		mappings.put(ItemID.BANDOS_DHIDE_BODY, itemIDGodDhideBody);
		mappings.put(ItemID.GUTHIX_DHIDE_BODY, itemIDGodDhideBody);
		mappings.put(ItemID.SARADOMIN_DHIDE_BODY, itemIDGodDhideBody);
		mappings.put(ItemID.ZAMORAK_DHIDE_BODY, itemIDGodDhideBody);

		final int itemIDGodChaps = 1000000007;
		mappings.put(ItemID.ANCIENT_CHAPS, itemIDGodChaps);
		mappings.put(ItemID.ARMADYL_CHAPS, itemIDGodChaps);
		mappings.put(ItemID.BANDOS_CHAPS, itemIDGodChaps);
		mappings.put(ItemID.GUTHIX_CHAPS, itemIDGodChaps);
		mappings.put(ItemID.SARADOMIN_CHAPS, itemIDGodChaps);
		mappings.put(ItemID.ZAMORAK_CHAPS, itemIDGodChaps);

		final int itemIDGodBracers = 1000000008;
		mappings.put(ItemID.ANCIENT_BRACERS, itemIDGodBracers);
		mappings.put(ItemID.ARMADYL_BRACERS, itemIDGodBracers);
		mappings.put(ItemID.BANDOS_BRACERS, itemIDGodBracers);
		mappings.put(ItemID.GUTHIX_BRACERS, itemIDGodBracers);
		mappings.put(ItemID.SARADOMIN_BRACERS, itemIDGodBracers);
		mappings.put(ItemID.ZAMORAK_BRACERS, itemIDGodBracers);

		final int itemIDGodDhideBoots = 1000000009;
		mappings.put(ItemID.ANCIENT_DHIDE_BOOTS, itemIDGodDhideBoots);
		mappings.put(ItemID.ARMADYL_DHIDE_BOOTS, itemIDGodDhideBoots);
		mappings.put(ItemID.BANDOS_DHIDE_BOOTS, itemIDGodDhideBoots);
		mappings.put(ItemID.GUTHIX_DHIDE_BOOTS, itemIDGodDhideBoots);
		mappings.put(ItemID.SARADOMIN_DHIDE_BOOTS, itemIDGodDhideBoots);
		mappings.put(ItemID.ZAMORAK_DHIDE_BOOTS, itemIDGodDhideBoots);

		final int itemIDGodDhideShield = 1000000010;
		mappings.put(ItemID.ANCIENT_DHIDE_SHIELD, itemIDGodDhideShield);
		mappings.put(ItemID.ARMADYL_DHIDE_SHIELD, itemIDGodDhideShield);
		mappings.put(ItemID.BANDOS_DHIDE_SHIELD, itemIDGodDhideShield);
		mappings.put(ItemID.GUTHIX_DHIDE_SHIELD, itemIDGodDhideShield);
		mappings.put(ItemID.SARADOMIN_DHIDE_SHIELD, itemIDGodDhideShield);
		mappings.put(ItemID.ZAMORAK_DHIDE_SHIELD, itemIDGodDhideShield);

		// Twisted Ancestral -> Regular Ancestral
		mappings.put(ItemID.TWISTED_ANCESTRAL_HAT, ItemID.ANCESTRAL_HAT);
		mappings.put(ItemID.TWISTED_ANCESTRAL_ROBE_BOTTOM, ItemID.ANCESTRAL_ROBE_BOTTOM);
		mappings.put(ItemID.TWISTED_ANCESTRAL_ROBE_TOP, ItemID.ANCESTRAL_ROBE_TOP);

		// Golden Prospectors -> Regular Prospectors
		mappings.put(ItemID.GOLDEN_PROSPECTOR_BOOTS, ItemID.PROSPECTOR_BOOTS);
		mappings.put(ItemID.GOLDEN_PROSPECTOR_HELMET, ItemID.PROSPECTOR_HELMET);
		mappings.put(ItemID.GOLDEN_PROSPECTOR_JACKET, ItemID.PROSPECTOR_JACKET);
		mappings.put(ItemID.GOLDEN_PROSPECTOR_LEGS, ItemID.PROSPECTOR_LEGS);

		// Spirit Anglers -> Regular Anglers
		mappings.put(ItemID.SPIRIT_ANGLER_BOOTS, ItemID.ANGLER_BOOTS);
		mappings.put(ItemID.SPIRIT_ANGLER_HEADBAND, ItemID.ANGLER_HAT);
		mappings.put(ItemID.SPIRIT_ANGLER_TOP, ItemID.ANGLER_TOP);
		mappings.put(ItemID.SPIRIT_ANGLER_WADERS, ItemID.ANGLER_WADERS);
		
		// ToB ornament kits -> base version
		mappings.put(ItemID.SANGUINE_SCYTHE_OF_VITUR, ItemID.SCYTHE_OF_VITUR);
		mappings.put(ItemID.HOLY_SCYTHE_OF_VITUR, ItemID.SCYTHE_OF_VITUR);
		mappings.put(ItemID.HOLY_SANGUINESTI_STAFF, ItemID.SANGUINESTI_STAFF);
		mappings.put(ItemID.HOLY_GHRAZI_RAPIER, ItemID.GHRAZI_RAPIER);

		mappings.put(ItemID.GHOMMALS_AVERNIC_DEFENDER_5, ItemID.AVERNIC_DEFENDER);
		mappings.put(ItemID.GHOMMALS_AVERNIC_DEFENDER_5_L, ItemID.AVERNIC_DEFENDER);
		mappings.put(ItemID.GHOMMALS_AVERNIC_DEFENDER_6, ItemID.AVERNIC_DEFENDER);
		mappings.put(ItemID.GHOMMALS_AVERNIC_DEFENDER_6_L, ItemID.AVERNIC_DEFENDER);

		mappings.put(ItemID.DRAGONFIRE_WARD_22003, ItemID.DRAGONFIRE_WARD);

	}

}
