package net.runelite.client.plugins.hoseaplugins.PiggyUtils.API;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;

import java.util.*;

public class InventoryUtil {
    public static boolean useItemNoCase(String name, String... actions) {
        return nameContainsNoCase(name).first().flatMap(item ->
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(item, actions);
            return Optional.of(true);
        }).orElse(false);
    }

    public static ItemQuery nameContainsNoCase(String name) {
        return Inventory.search().filter(widget -> widget.getName().toLowerCase().contains(name.toLowerCase()));
    }

    public static Optional<Widget> getById(int id) {
        return Inventory.search().withId(id).first();
    }

    public static Optional<Widget> getItemNameContains(String name, boolean caseSensitive) {
        if (caseSensitive) {
            return Inventory.search().filter(widget -> widget.getName().contains(name)).first();
        } else {
            return Inventory.search().filter(widget -> widget.getName().toLowerCase().contains(name.toLowerCase())).first();
        }
    }

    public static Optional<Widget> getItemNameContains(String name) {
        return getItemNameContains(name, true);
    }

    public static Optional<Widget> getItem(String name, boolean caseSensitive) {
        if (caseSensitive) {
            return Inventory.search().filter(widget -> widget.getName().equals(name)).first();
        } else {
            return Inventory.search().filter(widget -> widget.getName().toLowerCase().equals(name.toLowerCase())).first();
        }
    }

    public static Optional<Widget> getItem(String name) {
        return getItem(name, true);
    }

    public static int getItemAmount(String name, boolean stacked) {
        if (stacked) {
            return nameContainsNoCase(name).first().isPresent() ? nameContainsNoCase(name).first().get().getItemQuantity() : 0;
        }

        return nameContainsNoCase(name).result().size();
    }

    public static int getItemAmount(int id) {
        return getItemAmount(id, false);
    }

    public static int getItemAmount(int id, boolean stacked) {
        if (stacked) {
            return getById(id).isPresent() ? getById(id).get().getItemQuantity() : 0;
        }

        return Inventory.search().withId(id).result().size();
    }

    public static boolean hasItem(String name) {
        return getItemAmount(name, false) > 0;
    }

    public static boolean hasItem(String name, boolean stacked) {
        return getItemAmount(name, stacked) > 0;
    }

    public static boolean hasItem(String name, int amount) {
        return getItemAmount(name, false) >= amount;
    }

    public static boolean hasItem(String name, int amount, boolean stacked) {
        return getItemAmount(name, stacked) >= amount;
    }

    public static boolean hasItems(String ...names) {
        for (String name : names) {
            if (!hasItem(name)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasAnyItems(String ...names) {
        for (String name : names) {
            if (hasItem(name)) {
                return true;
            }
        }

        return false;
    }
    public static boolean hasAnyItems(Collection<Integer> itemIds) {
        for (Integer id : itemIds) {
            if (hasItem(id)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasItem(int id) {
        return getItemAmount(id) > 0;
    }

    public static List<Widget> getItems() {
        return Inventory.search().result();
    }

    public static int emptySlots() {
        return 28 - Inventory.search().result().size();
    }

    //Credit to marcojacobsNL
    public static boolean runePouchContains(int id) {
        Set<Integer> runePouchIds = new HashSet<>();
        if (EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE1) != 0) {
            runePouchIds.add(Runes.getRune(EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE1)).getItemId());
        }
        if (EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE2) != 0) {
            runePouchIds.add(Runes.getRune(EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE2)).getItemId());
        }
        if (EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE3) != 0) {
            runePouchIds.add(Runes.getRune(EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE3)).getItemId());
        }
        if (EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE4) != 0) {
            runePouchIds.add(Runes.getRune(EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE4)).getItemId());
        }
        for (int runePouchId : runePouchIds) {
            if (runePouchId == id) {
                return true;
            }
        }
        return false;
    }

    //Credit to marcojacobsNL
    public static boolean runePouchContains(Collection<Integer> ids) {
        for (int runeId : ids) {
            if (!runePouchContains(runeId)) {
                return false;
            }
        }
        return true;
    }

    //Credit to marcojacobsNL
    public static int runePouchQuanitity(int id) {
        Map<Integer, Integer> runePouchSlots = new HashMap<>();
        if (EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE1) != 0) {
            runePouchSlots.put(Runes.getRune(EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE1)).getItemId(), EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_AMOUNT1));
        }
        if (EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE2) != 0) {
            runePouchSlots.put(Runes.getRune(EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE2)).getItemId(), EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_AMOUNT2));
        }
        if (EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE3) != 0) {
            runePouchSlots.put(Runes.getRune(EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE3)).getItemId(), EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_AMOUNT3));
        }
        if (EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE4) != 0) {
            runePouchSlots.put(Runes.getRune(EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_RUNE4)).getItemId(), EthanApiPlugin.getClient().getVarbitValue(Varbits.RUNE_POUCH_AMOUNT4));
        }
        if (runePouchSlots.containsKey(id)) {
            return runePouchSlots.get(id);
        }
        return 0;
    }

}
