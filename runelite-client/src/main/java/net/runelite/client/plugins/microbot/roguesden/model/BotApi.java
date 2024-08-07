package net.runelite.client.plugins.microbot.roguesden.model;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.models.RS2Item;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import javax.inject.Named;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BooleanSupplier;

@Named
public class BotApi {

    public WorldPoint getCurrentPlayerLocation()
    {
        return Microbot.getClientThread().runOnClientThread(
                () -> Microbot.getClient().getLocalPlayer().getWorldLocation());
    }

    public WallObject findWallObject(final WorldPoint worldPoint)
    {
        return Rs2GameObject
                .getWallObjects()
                .stream()
                .filter(o -> o.getWorldLocation().equals(worldPoint))
                .findFirst()
                .orElse(null);
    }

    public DecorativeObject findDecorativeObject(final WorldPoint worldPoint)
    {
        return Rs2GameObject
                .getDecorationObjects()
                .stream()
                .filter(o -> o.getWorldLocation().equals(worldPoint))
                .findFirst()
                .orElse(null);
    }

    public GameObject findGameObject(final WorldPoint worldPoint)
    {
        return Rs2GameObject.getGameObject(worldPoint);
    }

    public boolean interact(final TileObject tileObject, final String action)
    {
        return Rs2GameObject.interact(tileObject, action);
    }

    public boolean walkTo(final WorldPoint worldPoint)
    {
        return walkTo(worldPoint, 0);
    }

    public boolean walkTo(final WorldPoint worldPoint, final int distance)
    {
        if (!Rs2Walker.walkTo(worldPoint, distance))
        {
            return Rs2Walker.walkCanvas(worldPoint) != null;
        }

        return true;
    }

    public boolean pickupItem(final WorldPoint worldPoint, final int itemId)
    {
        final Optional<RS2Item> item = Arrays.stream(Rs2GroundItem.getAllAt(worldPoint.getX(), worldPoint.getY()))
                .filter(i -> i.getItem().getId() == itemId)
                .findFirst();
        return Rs2GroundItem.interact(item.orElse(null));
    }

    public int getRunEnergy()
    {
        return Microbot.getClient().getEnergy() / 100;
    }

    public int getTick()
    {
        return Microbot.getClient().getTickCount();
    }

    public void sleepUntil(final BooleanSupplier booleanSupplier)
    {
        Global.sleepUntilOnClientThread(booleanSupplier);
    }

    public void sleepUntil(final BooleanSupplier booleanSupplier, final int time)
    {
        Global.sleepUntilOnClientThread(booleanSupplier, time);
    }

    public void sleepUntilNextTick()
    {
        final int tick = Microbot.getClientThread().runOnClientThread(this::getTick);
        Global.sleepUntilOnClientThread(() -> tick < this.getTick(), 1000);
    }

    public void sleepUntilPlayerIdle()
    {
        Global.sleepUntilOnClientThread(() -> !Rs2Player.isMoving() || Rs2Player.isAnimating(), 10000);
    }

    public NPC getNpc(final int id)
    {
        return Rs2Npc.getNpc(id);
    }

    public boolean useItemOnNpc(final int itemId, final NPC npc)
    {
        return Rs2Inventory.useItemOnNpc(itemId, npc);
    }

    public boolean isInventoryEmpty()
    {
        return Rs2Inventory.isEmpty();
    }

    public boolean inventoryContains(final int id)
    {
        return Rs2Inventory.contains(id);
    }

    public void depositInventory(final NPC npc)
    {
        Rs2Bank.openBank(npc);
        Rs2Bank.depositAll();
        Rs2Bank.closeBank();
    }

    public boolean getRunState()
    {
        return Rs2Player.isRunEnabled();
    }

    public void setRunState(final boolean runState)
    {
        Rs2Player.toggleRunEnergy(runState);
    }

    public Widget getWidget(final int id)
    {
        return Rs2Widget.getWidget(id);
    }

    public void clickWidget(final Widget widget)
    {
        Microbot.getMouse().click(widget.getBounds());
    }

    public int getAnimation()
    {
        return Rs2Player.getAnimation();
    }
}
