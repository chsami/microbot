package net.runelite.client.plugins.microbot.util.models;

import net.runelite.api.ItemComposition;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;

@Deprecated(since="use inventory.rs2item")
public class RS2Item {
    private final ItemComposition item;
    private final Tile tile;
    private final TileItem tileItem;

    public RS2Item(ItemComposition item, Tile tile, TileItem tileItem) {
        this.item = item;
        this.tile = tile;
        this.tileItem = tileItem;
    }

    public ItemComposition getItem() {
        return item;
    }

    public Tile getTile() {
        return tile;
    }

    public TileItem getTileItem() {
        return tileItem;
    }
}
