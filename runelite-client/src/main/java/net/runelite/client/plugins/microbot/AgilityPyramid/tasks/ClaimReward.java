package net.runelite.client.plugins.microbot.AgilityPyramid.tasks;

import net.runelite.api.InventoryID;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.AgilityPyramid.AgilitypyramidScript;
import net.runelite.client.plugins.microbot.AgilityPyramid.PyramidConfig;
import net.runelite.client.plugins.microbot.AgilityPyramid.data.identifiers;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class ClaimReward {
    public static void HandleSimon(PyramidConfig config){

        WorldPoint SimonAnchorPoint = new WorldPoint(3339, 2825, 0);
        WorldArea SimonArea = new WorldArea(SimonAnchorPoint,9,6);
        if (!SimonArea.contains(Rs2Player.getWorldLocation())){
            System.out.println("Not inside Simon's area, walking");
            Rs2Walker.walkTo(3343, 2827, 0);
            Rs2Player.waitForWalking();
        }
        else if (SimonArea.contains(Rs2Player.getWorldLocation())) {
            Rs2Inventory.useItemOnNpc(identifiers.pyramidID, identifiers.SimonTempletonID);
            sleepUntil(() -> !Rs2Inventory.contains(identifiers.pyramidID), 4000);

        }

        //Switch state when no pyramids in inventory
        if (!Rs2Inventory.contains(identifiers.pyramidID)){
            System.out.println("Out of pyramids, switching to CompleteCourse");
            AgilitypyramidScript.state = "CompleteCourse";
        }
    }
}
