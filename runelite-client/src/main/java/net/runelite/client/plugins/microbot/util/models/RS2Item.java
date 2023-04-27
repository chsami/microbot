package net.runelite.client.plugins.microbot.util.models;

import lombok.Getter;
import net.runelite.api.ItemComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;

import java.awt.*;

public class RS2Item {
    @Getter
    private ItemComposition item;
    @Getter
    private final Tile tile;
    @Getter
    private final TileItem tileItem;

    public RS2Item(ItemComposition item, Tile tile, TileItem tileItem) {
        this.item = item;
        this.tile = tile;
        this.tileItem = tileItem;
    }

    public boolean lootItem(String lootItem) {
        LocalPoint groundPoint = LocalPoint.fromWorld(Microbot.getClient(), getTile().getWorldLocation());
        Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), groundPoint, getTile().getItemLayer().getHeight());
        if (Camera.isTileOnScreen(getTile().getLocalLocation())) {
            return Rs2Menu.doAction("Take", poly, new String[]{lootItem.toLowerCase()});
        } else {
            Camera.turnTo(getTile().getLocalLocation());
        }
        return false;
    }
}
