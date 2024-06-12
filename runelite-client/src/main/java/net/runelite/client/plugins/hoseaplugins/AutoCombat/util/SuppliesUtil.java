package net.runelite.client.plugins.hoseaplugins.AutoCombat.util;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class SuppliesUtil {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;

    @Getter
    @Setter
    private List<String> foodNames = List.of("Cake", "cake","Herring", "Trout", "Lobster", "Swordfish",
            "Monkfish", "Shark", "Manta ray", "Dark crab", "Anglerfish", "Tuna potato", "karambwan");

    /**
     * Finds any teleport tab in the inventory
     * @return The first tab found, or null if none are found
     */
    public Widget findTeleport() {
        Optional<Widget> teleport = Inventory.search().withAction("Break").nameContains("eleport").first();
        return teleport.orElse(null);
    }

    /**
     * Finds any unnoted Prayer Potion or Super Restore in the inventory
     *
     * @return The first potion found, or null if none are found
     */
    public Widget findPrayerPotion() {
        Optional<Widget> potion = Inventory.search().onlyUnnoted().withAction("Drink").filter(pot -> {
            String name = pot.getName();
            return name.contains("rayer potion") || name.contains("uper restore");

        }).first();
        return potion.orElse(null);
    }

    /**
     * Finds any unnoted Combat Potion in the inventory
     *
     * @return The first potion found, or null if none are found
     */
    public Widget findRangingPotion() {
        Optional<Widget> potion = Inventory.search().onlyUnnoted().withAction("Drink").nameContains("anging potion").first();
        return potion.orElse(null);
    }

    /**
     * Finds any unnoted Combat Potion in the inventory
     *
     * @return The first potion found, or null if none are found
     */
    public Widget findCombatPotion() {
        Optional<Widget> potion = Inventory.search().onlyUnnoted().withAction("Drink").nameContains("ombat potion").first();
        return potion.orElse(findStrengthPotion());
    }

    /**
     * Finds any unnoted Strength Potion in the inventory
     *
     * @return The first potion found, or null if none are found
     */
    public Widget findStrengthPotion() {
        Optional<Widget> potion = Inventory.search().onlyUnnoted().withAction("Drink").nameContains("trength potion").first();
        return potion.orElse(null);
    }

    /**
     * Finds any edible food in the inventory, based on the SuppliesUtil#foodNames list
     *
     * @return The first item found, or null if none are found
     */
    public Widget findFood() {
        Optional<Widget> food = Inventory.search().withAction("Eat").filter(f -> {
            String name = f.getName();
            return foodNames.stream().anyMatch(name::contains);
        }).first();
        return food.orElse(null);
    }

    public Widget findBone(){
        Optional<Widget> bone = Inventory.search().withAction("Bury").nameContains("one").first();
        return bone.orElse(null);
    }
}
