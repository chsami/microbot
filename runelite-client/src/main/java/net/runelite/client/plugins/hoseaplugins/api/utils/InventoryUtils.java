package net.runelite.client.plugins.hoseaplugins.api.utils;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InventoryInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import net.runelite.client.plugins.hoseaplugins.api.item.SlottedItem;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InventoryUtils
{
    static Client client = RuneLite.getInjector().getInstance(Client.class);

    public static void itemOnItem(Item item1, Item item2)
    {
        Optional<Widget> itemToUse = Inventory.search().withId(item1.getId()).first();
        Optional<Widget> itemToUseOn = Inventory.search().withId(item2.getId()).first();

        if (itemToUse.isPresent() && itemToUseOn.isPresent())
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetOnWidget(itemToUse.get(), itemToUseOn.get());
        }
    }

    public static List<SlottedItem> getAllSlotted()
    {
        return BankUtils.isOpen() ? getItemsSlotted(15, 3) : Inventory.search().result().stream().map(item -> new SlottedItem(item.getItemId(), item.getItemQuantity(), item.getIndex())).collect(Collectors.toList());
    }

    public static List<SlottedItem> getAllSlotted(Predicate<SlottedItem> filter)
    {
        return BankUtils.isOpen() ? getItemsSlotted(15, 3).stream().filter(filter).collect(Collectors.toList()) :
                Inventory.search().result().stream().map(item -> new SlottedItem(item.getItemId(), item.getItemQuantity(), item.getIndex())).filter(filter).collect(Collectors.toList());
    }

    public static List<Item> getAll()
    {
        return BankUtils.isOpen() ? getItems(15, 3) : Inventory.search().result().stream().map(item -> new Item(item.getItemId(), item.getItemQuantity())).collect(Collectors.toList());
    }

    public static List<Item> getAll(Predicate<Item> filter)
    {

        return BankUtils.isOpen() ? getItems(15, 3).stream().filter(filter).collect(Collectors.toList()) : Inventory.search().result().stream().map(item -> new Item(item.getItemId(), item.getItemQuantity())).filter(filter).collect(Collectors.toList());
    }

    public static boolean contains(String itemName)
    {
        return BankUtils.isOpen() ? getItemIndex(itemName, 15, 3) != -1 : Inventory.search().nameContains(itemName).first().isPresent();
    }

    public static boolean contains(int[] ids)
    {
        for (int id : ids)
        {
            if (BankUtils.isOpen() && getItemIndex(id, 15, 3) != -1)
            {
                return true;
            }
            else if (InventoryUtils.contains(id))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int id)
    {
        return BankUtils.isOpen() ? getItemIndex(id, 15, 3) != -1 : !Inventory.search().idInList(List.of(id)).result().isEmpty();
    }

    public static int getFreeSlots()
    {
        return BankUtils.isOpen() ? getInBankFreeSlots() : Inventory.getEmptySlots();
    }

    public static int getInBankFreeSlots()
    {
        return getFreeSlots(15, 3);
    }

    public static int getFreeSlots(int parentId, int childId)
    {
        Widget inventoryWidget = client.getWidget(parentId, childId);
        if (inventoryWidget == null)
        {
            return 0;
        }

        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems == null)
        {
            return 0;
        }

        int freeSlots = 0;

        for (Widget inventoryItem : inventoryItems)
        {
            if (inventoryItem == null)
            {
                freeSlots++;
                continue;
            }

            if (inventoryItem.getName().isBlank())
            {
                freeSlots++;
                continue;
            }

            if (inventoryItem.getItemId() == -1 || inventoryItem.getItemId() == 6512)
            {
                freeSlots++;
            }
        }

        return freeSlots;
    }

    public static int getItemCount(Object id, int parentId, int childId)
    {
        Widget inventoryWidget = client.getWidget(parentId, childId);
        if (inventoryWidget == null)
        {
            return 0;
        }

        int count = 0;

        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems == null)
        {
            return 0;
        }

        for (Widget inventoryItem : inventoryItems)
        {
            if (inventoryItem == null)
            {
                continue;
            }

            if (inventoryItem.getName().isBlank())
            {
                continue;
            }

            if (id instanceof Integer && inventoryItem.getItemId() != (int)id)
            {
                continue;
            }
            else if (id instanceof String && !inventoryItem.getName().equals(String.valueOf(id)))
            {
                continue;
            }

            count += inventoryItem.getItemQuantity();
        }

        return count;
    }

    public static List<Item> getItems(int parentId, int childId)
    {
        List<Item> items = new ArrayList<>();
        Widget inventoryWidget = client.getWidget(parentId, childId);
        if (inventoryWidget == null)
        {
            return List.of();
        }

        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems == null)
        {
            return List.of();
        }

        for (Widget widget : inventoryItems)
        {
            if (widget != null && widget.getName() != null && widget.getItemId() != -1)
            {
                items.add(new Item(widget.getItemId(), widget.getItemQuantity()));
            }
        }

        return items;
    }

    public static List<SlottedItem> getItemsSlotted(int parentId, int childId)
    {
        List<SlottedItem> items = new ArrayList<>();
        Widget inventoryWidget = client.getWidget(parentId, childId);
        if (inventoryWidget == null)
        {
            return List.of();
        }

        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems == null)
        {
            return List.of();
        }

        for (Widget widget : inventoryItems)
        {
            if (widget != null && widget.getName() != null && widget.getItemId() != -1)
            {
                items.add(new SlottedItem(widget.getItemId(), widget.getItemQuantity(), widget.getIndex()));
            }
        }

        return items;
    }

    public static int getItemIndex(Object id, int parentId, int childId)
    {
        Widget inventoryWidget = client.getWidget(parentId, childId);
        if (inventoryWidget == null)
        {
            return -1;
        }

        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems == null)
        {
            return -1;
        }

        for (int i = 0; i < inventoryItems.length; i++)
        {
            if (inventoryItems[i] == null)
            {
                continue;
            }

            if (inventoryItems[i].getName().isBlank())
            {
                continue;
            }

            if (id instanceof Integer && inventoryItems[i].getItemId() != (int)id)
            {
                continue;
            }
            else if (id instanceof String && !inventoryItems[i].getName().equals(String.valueOf(id)))
            {
                continue;
            }

            return i;
        }

        return -1;
    }

    public static boolean itemHasAction(int itemId, String action)
    {
        return Arrays.stream(client.getItemDefinition(itemId).getInventoryActions()).anyMatch(a -> a != null && a.equalsIgnoreCase(action));
    }

    public static void itemInteract(int itemId, String action)
    {
        InventoryInteraction.useItem(itemId, action);
    }

    public static void castAlchemyOnItem(int id, boolean highAlchemy)
    {
        Optional<Widget> itemWidget = Inventory.search().withId(id).first();
        final int alchemyWidgetId = highAlchemy ? 14286892 : 14286869;
        Widget alchemyWidget = client.getWidget(alchemyWidgetId);

        if (alchemyWidget != null)
        {
            itemWidget.ifPresent(widget -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(alchemyWidget, widget);
            });
        }
    }

    public static int calculateWidgetId(Item item)
    {
        Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY.getPackedId());
        if (inventoryWidget == null)
        {
            return -1;
        }
        else
        {
            Widget[] children = inventoryWidget.getChildren();
            return children == null ? -1 : Arrays.stream(children).filter((x) -> x.getItemId() == item.getId()).findFirst().map(Widget::getId).orElse(-1);
        }
    }

    public static SlottedItem getItemInSlot(int slot)
    {
        Widget itemWidget = Inventory.search().indexIs(slot).first().orElse(null);

        int id = -1;
        int amount = -1;

        if (itemWidget != null)
        {
            amount = itemWidget.getItemQuantity();
            id = itemWidget.getItemId();
        }

        if (id == -1 || amount == -1)
        {
            return null;
        }

        return new SlottedItem(itemWidget.getItemId(), amount, itemWidget.getIndex());
    }

    public static SlottedItem getFirstItemSlotted(int id)
    {
        Widget itemWidget = Inventory.search().withId(id).first().orElse(null);
        int amount = -1;

        if (itemWidget != null)
        {
            amount = itemWidget.getItemQuantity();
        }

        if (id == -1 || amount == -1)
        {
            return null;
        }

        return new SlottedItem(id, amount, itemWidget.getIndex());
    }

    public static SlottedItem getLastItemSlotted(int id)
    {
        List<Widget> itemWidgets = Inventory.search().withId(id).result();
        Widget itemWidget = itemWidgets.get(itemWidgets.size() - 1);
        int amount = -1;

        if (itemWidget != null)
        {
            amount = itemWidget.getItemQuantity();
        }

        if (id == -1 || amount == -1)
        {
            return null;
        }

        return new SlottedItem(id, amount, itemWidget.getIndex());
    }

    public static SlottedItem getFirstItemSlotted(int[] ids)
    {
        List<Integer> intIdList = Arrays.stream(ids).boxed().collect(Collectors.toList());

        Widget itemWidget = Inventory.search().idInList(intIdList).first().orElse(null);
        int amount = -1;

        if (itemWidget != null)
        {
            amount = itemWidget.getItemQuantity();
        }

        if (itemWidget.getItemId() == -1 || amount == -1)
        {
            return null;
        }

        return new SlottedItem(itemWidget.getItemId(), amount, itemWidget.getIndex());
    }

    public static int getItemId(String name)
    {
        final Widget itemWidget = Inventory.search().filter(item -> item.getName().contains(name)).first().orElse(null);
        return itemWidget != null ? itemWidget.getItemId() : -1;
    }

    public static Item getFirstItem(int id)
    {
        return getAll(item -> item.getId() == id).stream().findFirst().orElse(null);
    }

    public static Item getFirstItem(Predicate<Item> filter)
    {
        return getAll().stream().filter(filter).findFirst().orElse(null);
    }

    public static Item getFirstItem(String name)
    {
        return getAll(item2 -> {
           ItemComposition composition = client.getItemDefinition(item2.getId());
           return composition.getName().contains(name);
        }).stream().findFirst().orElse(null);
    }

    public static void wieldItem(int id)
    {
        itemInteract(id, "Wield");
    }

    public static int count(String name)
    {
        Predicate<SlottedItem> filter = (item) -> {
            final ItemComposition itemDef = client.getItemDefinition(item.getItem().getId());
            return itemDef.getName() != null && itemDef.getName().toLowerCase().contains(name.toLowerCase());
        };
        List<SlottedItem> itemsToCount = BankUtils.isOpen() ?
                getItemsSlotted(15, 3).stream().filter(filter).collect(Collectors.toList()) :
                getAllSlotted(filter);
        int count = 0;
        for (SlottedItem i : itemsToCount)
        {
            if (i != null)
            {
                count += i.getItem().getQuantity();
            }
        }
        return count;
    }

    public static int count(int id)
    {
        List<SlottedItem> itemsToCount = BankUtils.isOpen() ?
                getItemsSlotted(15, 3).stream().filter(item -> item.getItem().getId() == id).collect(Collectors.toList()) :
                getAllSlotted(item -> item.getItem().getId() == id);

        int count = 0;
        for (SlottedItem i : itemsToCount)
        {
            if (i != null)
            {
                count += i.getItem().getQuantity();
            }
        }

        return count;
    }

    public static void interactSlot(int slot, String action)
    {
        SlottedItem item = getItemInSlot(slot);
        if (item == null)
        {
            return;
        }

        InventoryInteraction.useItemIndex(slot, action);
    }
}
