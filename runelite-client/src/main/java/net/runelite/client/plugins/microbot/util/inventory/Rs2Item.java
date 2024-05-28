package net.runelite.client.plugins.microbot.util.inventory;

import lombok.Getter;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ParamID;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rs2Item {
    public  int id;
    public  int quantity;
    @Getter
    public int slot = -1;
    public String name;
    @Getter
    String[] inventoryActions;
    @Getter
    List<String> equipmentActions = new ArrayList();
    boolean isStackable;
    boolean isNoted;
    @Getter
    boolean isTradeable;
    int[] wearableActionIndexes = new int[] {
            ParamID.OC_ITEM_OP1,
            ParamID.OC_ITEM_OP2,
            ParamID.OC_ITEM_OP3,
            ParamID.OC_ITEM_OP4,
            ParamID.OC_ITEM_OP5,
            ParamID.OC_ITEM_OP6,
            ParamID.OC_ITEM_OP7,
            ParamID.OC_ITEM_OP8};


    public Rs2Item(Item item, ItemComposition itemComposition, int slot) {
        this.id = item.getId();
        this.quantity = item.getQuantity();
        this.name = itemComposition.getName();
        this.slot = slot;
        this.isStackable = itemComposition.isStackable();
        this.isNoted = itemComposition.getNote() == 799;
        this.isTradeable = this.isNoted
                ? Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(this.id - 1)).isTradeable()
                : itemComposition.isTradeable();
        Widget widget = Rs2Widget.getWidget(ComponentID.INVENTORY_CONTAINER).getChild(slot);
        if (widget != null) {
            this.inventoryActions = Rs2Widget.getWidget(ComponentID.INVENTORY_CONTAINER).getChild(slot).getActions();
        }
        addEquipmentActions(itemComposition);
    }

    public boolean isFood() {
        return Arrays.stream(inventoryActions).anyMatch(x -> x != null && x.equalsIgnoreCase("eat"));
    }

    private void addEquipmentActions(ItemComposition itemComposition) {
        for (int i = 0; i < wearableActionIndexes.length; i++) {
            try {
                String value = itemComposition.getStringValue(wearableActionIndexes[i]);
                this.equipmentActions.add(value);
            } catch(Exception ex) {
                this.equipmentActions.add("");
            }
        }
    }
}
