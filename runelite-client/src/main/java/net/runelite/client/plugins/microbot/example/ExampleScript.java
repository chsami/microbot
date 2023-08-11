package net.runelite.client.plugins.microbot.example;

import net.runelite.api.Prayer;
import net.runelite.api.TileObject;
import net.runelite.api.Varbits;
import net.runelite.api.World;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.GroundItem;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.CheckedNode;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.CollisionMap;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.globval.VarbitIndices.BANK_WITHDRAW_QUANTITY;


public class ExampleScript extends Script {

    public static double version = 1.0;


    public boolean run(ExampleConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;

            try {
                //write your code here
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

}
