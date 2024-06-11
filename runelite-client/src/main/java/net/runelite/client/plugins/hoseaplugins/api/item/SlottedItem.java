package net.runelite.client.plugins.hoseaplugins.api.item;

import lombok.Getter;
import net.runelite.api.Item;

public class SlottedItem
{
    @Getter
    private int slot = -1;

    @Getter
    private Item item;

    public SlottedItem(int id, int quantity, int slot)
    {
        this.item = new Item(id, quantity);
        this.slot = slot;
    }
}