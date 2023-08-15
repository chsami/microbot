package net.runelite.client.plugins.microbot.example;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.equipment.JewelleryLocationEnum;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class ExampleScript extends Script {

    public static double version = 1.0;


    public boolean run(ExampleConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
            Rs2Equipment.useAmuletAction(JewelleryLocationEnum.WOODCUTTING_GUILD);
            Rs2Equipment.useRingAction(JewelleryLocationEnum.CASTLE_WARS);

// [MenuEntryImpl(getOption=, getTarget=, getIdentifier=10823, getType=GAME_OBJECT_FIRST_OPTION, getParam0=53, getParam1=56, getItemId=0, isForceLeftClick=false, isDeprioritized=false), MenuEntryImpl(getOption=, getTarget=, getIdentifier=10823, getType=GAME_OBJECT_FIRST_OPTION, getParam0=53, getParam1=56, getItemId=-1, isForceLeftClick=false, isDeprioritized=false), MenuEntryImpl(getOption=, getTarget=, getIdentifier=10823, getType=GAME_OBJECT_FIRST_OPTION, getParam0=53, getParam1=56, getItemId=-1, isForceLeftClick=false, isDeprioritized=false), MenuEntryImpl(getOption=, getTarget=, getIdentifier=10823, getType=GAME_OBJECT_FIRST_OPTION, getParam0=53, getParam1=56, getItemId=-1, isForceLeftClick=false, isDeprioritized=false)]
              // Rs2GameObject.interact(14843);
               // Rs2Npc.pickpocket("master farmer");
               // Rs2Magic.cast(MagicAction.VARROCK_TELEPORT);
                //Rs2GroundItem.interact("box trap", "lay");
                //getOption=Take, getTarget=<col=ff9040>Manta ray, getIdentifier=391, getType=GROUND_ITEM_THIRD_OPTION, getParam0=51, getParam1=51, getItemId=-1, isForceLeftClick=false, isDeprioritized=false)
                System.out.println(Arrays.toString(Microbot.getClient().getMenuEntries()));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Rs2Npc.npcInteraction = null;
        Rs2GroundItem.itemInteraction = null;
        Rs2GameObject.objectToInteract = null;
        Rs2Equipment.widgetId = 0;
    }
}
