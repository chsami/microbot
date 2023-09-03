package net.runelite.client.plugins.microbot.util.inventory;

public class Rs2Item {
    public  int id;
    public  int quantity;
    public int slot = -1;
    public String name;
    public Rs2Item(int id, int quantity, String name) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
    }
    public Rs2Item(int id, int quantity, String name, int slot) {
        this.id = id;
        this.quantity = quantity;
        this.slot = slot;
        this.name = name;
    }
}
