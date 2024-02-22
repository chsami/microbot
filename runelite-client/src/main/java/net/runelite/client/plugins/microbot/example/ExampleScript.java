package net.runelite.client.plugins.microbot.example;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.concurrent.TimeUnit;


public class ExampleScript extends Script {
    public static double version = 1.0;

    public boolean run(ExampleConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                /*
                 * Important classes:
                 * Inventory
                 * Rs2GameObject
                 * Rs2GroundObject
                 * Rs2NPC
                 * Rs2Bank
                 * etc...
                 */
             /*  WorldResult worldResult = Microbot.getWorldService().getWorlds();
                World currentWorld = worldResult.findWorld(Microbot.getClient().getWorld());
                System.out.println(currentWorld);*/
          //      System.out.println(Login.getRandomMembersWorld());
                //GrandExchange.collect(true);
            //    Rs2Reflection.invoke(0, 9764864, WIDGET_TARGET_ON_WIDGET.getId(), 0,2347, "Use", "<col=ff9040>Hammer</col>", -1, -1);
                //MenuEntryImpl(getOption=Use, getTarget=<col=ff9040>Hammer</col><col=ffffff> -> <col=ff9040>Hammer</col>, getIdentifier=0, getType=WIDGET_TARGET_ON_WIDGET, getParam0=0, getParam1=9764864, getItemId=2347, isForceLeftClick=false, isDeprioritized=false)
//MenuEntryImpl(getOption=Use, getTarget=<col=ff9040>Hammer</col>, getIdentifier=0, getType=WIDGET_TARGET, getParam0=0, getParam1=9764864, getItemId=2347, isForceLeftClick=false, isDeprioritized=false)
//                Rs2GameObject.interact("tree");
                System.out.println(Rs2Inventory.onlyContains("earth talisman"));
                System.out.println(Rs2Inventory.getSelectedItemIndex());
                System.out.println(Rs2Inventory.get("earth talisman"));
                System.out.println(Rs2Inventory.get(ItemID.EARTH_TALISMAN));
                System.out.println(Rs2Inventory.get(ItemID.FIRE_CAPE));
//                System.out.println(Rs2Inventory.drop("earth talisman"));
                System.out.println(Rs2Inventory.use("earth talisman"));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1500, TimeUnit.MILLISECONDS);
        return true;
    }
}
