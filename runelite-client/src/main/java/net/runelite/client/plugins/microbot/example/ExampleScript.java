package net.runelite.client.plugins.microbot.example;

import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class ExampleScript extends Script {

    public static double version = 1.0;
boolean reachedEndLine = false;
    LocalPoint position1 = null;

    public boolean run(ExampleConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
//                MicrobotInventorySetup.loadEquipment("test");
//                MicrobotInventorySetup.loadInventory("test");
//                Rs2Bank.withdrawAndEquipFast(ItemID.VOID_KNIGHT_GLOVES);
//                Rs2Bank.withdrawAndEquipFast(ItemID.VOID_KNIGHT_ROBE);
//                Rs2Bank.withdrawAndEquipFast(ItemID.VOID_KNIGHT_TOP);
//                Rs2Bank.withdrawAndEquipFast(ItemID.VOID_RANGER_HELM);
//                Rs2Bank.withdrawAndEquipFast(ItemID.DRAGONFIRE_WARD);
//                Rs2Bank.withdrawAndEquipFast(ItemID.DRAGON_HUNTER_CROSSBOW);
//                Rs2Bank.withdrawAndEquipFast(ItemID.NECKLACE_OF_ANGUISH);
//                Rs2Bank.withdrawAndEquipFast(ItemID.PEGASIAN_BOOTS);
//                Rs2Bank.withdrawAndEquipFast(ItemID.ARCHERS_RING_I);
//                Rs2Bank.withdrawAndEquipFast(ItemID.AVAS_ACCUMULATOR);
//                Rs2Bank.withdrawAllAndEquipFast(ItemID.RUBY_DRAGON_BOLTS_E);
//                Rs2Bank.withdrawFast(ItemID.ANTIVENOM4_12913);
//                Rs2Bank.withdrawFast(ItemID.DIVINE_RANGING_POTION4);
//                Rs2Bank.withdrawFast(ItemID.EXTENDED_SUPER_ANTIFIRE4);
//                Rs2Bank.withdrawFast(ItemID.TELEPORT_TO_HOUSE, 1);
//                Rs2Bank.withdrawFast(ItemID.RELLEKKA_TELEPORT);
//                Rs2Bank.withdrawFast(ItemID.RUNE_POUCH);
//                Rs2Bank.withdrawFast(ItemID.SLAYERS_STAFF);
//                Rs2Bank.withdrawFast(ItemID.PRAYER_POTION4, 2);
//                Rs2Bank.withdrawFast(ItemID.COOKED_KARAMBWAN, 4);
//                Rs2Bank.withdrawAllFast(ItemID.MANTA_RAY);

                //Rs2Bank.widgetId = 786445;
                //Rs2Bank.itemId = ItemID.ECTOTOKEN;
                //MenuEntryImpl(getOption=Withdraw-1, getTarget=<col=ff9040>Ecto-token</col>, getIdentifier=1, getType=CC_OP, getParam0=623, getParam1=786445, getItemId=4278, isForceLeftClick=false, isDeprioritized=false)]
//                System.out.println();
//                for (InventorySetupsItem inventorySetupsItem:
//                InventorySetupsPlugin.inventorySetups.get(0).getInventory()) {
//                    System.out.println(inventorySetupsItem.getName());
//                }
//                if (position1 == null) {
//                    for (int i = 0; i < 4; i++) {
//                        int x = i * 128;
//                        GameObject gameObject = Rs2GameObject.getGameObject(new LocalPoint(5824 + x, 7104));
//
//                        if (gameObject == null) {
//                            position1 = new LocalPoint(5824 + x, 7104);
//                        }
//
//                    }
//                    return;
//                }
//
//
//
//                if (Microbot.getClient().getLocalPlayer().getLocalLocation().getY() <= 7300 && !reachedEndLine) {
//                    reachedEndLine = true;
//                }
//
//                if (!reachedEndLine) {
//                    Microbot.getWalker().walkFastCanvas(position1);
//                    return;
//                }
//
//                Microbot.getWalker().walkFastCanvas(position1);
//                sleep(300);
//                Rs2Npc.interact("vorkath", "attack");
                //getParam0=828, getParam1=582

                //getParam0=-5146, getParam1=-4228,
                //getParam0=-5350, getParam1=-4235
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
        Rs2Bank.widgetId = 0;
        reachedEndLine = false;
    }
}
