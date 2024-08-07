package net.runelite.client.plugins.microbot.thieving.stalls.model;

import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

public class BotApi {
    private static final String STEAL_ACTION = "Steal-from";

    public boolean walkTo(final WorldPoint worldPoint)
    {
        return Rs2Walker.walkTo(worldPoint, 0);
    }

    public GameObject getGameObject(final int id, final WorldPoint worldPoint)
    {
        return Rs2GameObject.findObject(id, worldPoint);
    }

    public void steal(final GameObject gameObject)
    {
        Rs2GameObject.interact(gameObject, STEAL_ACTION);
    }

    public void dropAll(Integer... ids)
    {
        Rs2Inventory.dropAll(ids);
    }

    public void sleepUntilNextTick()
    {
        final int tick = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getTickCount());
        Global.sleepUntilOnClientThread(() -> Microbot.getClient().getTickCount() > tick);
    }

    public boolean isInventoryFull()
    {
        return Rs2Inventory.isFull();
    }
}
