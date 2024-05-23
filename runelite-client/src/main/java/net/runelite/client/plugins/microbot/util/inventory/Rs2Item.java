package net.runelite.client.plugins.microbot.util.inventory;

import lombok.Getter;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;

import java.awt.*;
import java.util.Arrays;

public class Rs2Item {
    public  int id;
    public  int quantity;
    @Getter
    public int slot = -1;
    public String name;
    String[] actions;
    boolean isStackable;
    boolean isNoted;
    @Getter
    boolean isTradeable;

    public Rs2Item(Item item, ItemComposition itemComposition, int slot) {
        this.id = item.getId();
        this.quantity = item.getQuantity();
        this.name = itemComposition.getName();
        this.actions = itemComposition.getInventoryActions();
        this.slot = slot;
        this.isStackable = itemComposition.isStackable();
        this.isNoted = itemComposition.getNote() == 799;
        this.isTradeable = itemComposition.isTradeable();
    }
    public Rs2Item(Widget item, int slot) {
        this.id = item.getItemId();
        this.quantity = item.getItemQuantity();
        this.slot = slot;
        this.name = item.getName().split(">")[1].split("</")[0];
        this.actions = item.getActions();
    }

    public boolean isFood() {
        return Arrays.stream(actions).anyMatch(x -> x != null && x.equalsIgnoreCase("eat"));
    }
}
