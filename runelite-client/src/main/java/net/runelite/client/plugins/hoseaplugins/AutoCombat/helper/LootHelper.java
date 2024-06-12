package net.runelite.client.plugins.hoseaplugins.AutoCombat.helper;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.ItemQuery;
import com.google.inject.Inject;
import net.runelite.client.plugins.hoseaplugins.AutoCombat.AutoCombatConfig;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class LootHelper {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private AutoCombatConfig config;

    @Inject
    public ItemManager itemManager;

    /**
     * Name, Price
     */
    public HashMap<String, Integer> lootCache = new HashMap<>();

    @Setter
    private List<String> lootNames = null;


    /**
     * Looks for the item by name and returns if there are any noted or stackable in the inventory
     *
     * @param
     * @return
     */
    public boolean hasStackableLoot(ItemComposition comp) {
        String name = comp.getName();
        ItemQuery itemQry = Inventory.search().withName(name);
        if (itemQry.first().isEmpty()) {
            return false;
        }
        return itemQry.onlyNoted().first().isPresent() || itemQry.quantityGreaterThan(1).first().isPresent();
    }

    /**
     * Takes the loot names and returns as a list with trimmed names
     *
     * @return
     */
    public List<String> getLootNames() {
//        if (lootNames == null)
            lootNames = Arrays.stream(config.lootNames().split(",")).map(String::trim).collect(Collectors.toList());
        return lootNames;
    }

    /**
     * Gets the cached price or wiki price if not yet cached
     *
     * @param name Exact name of item
     * @return
     */
    public int getPrice(String name) {
        if (lootCache.containsKey(name)) {
            return lootCache.get(name);
        }
        int price = itemManager.search(name).get(0).getWikiPrice();
        lootCache.put(name, price);
        return price;
    }

}