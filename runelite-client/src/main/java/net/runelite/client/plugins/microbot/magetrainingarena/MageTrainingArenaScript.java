package net.runelite.client.plugins.microbot.magetrainingarena;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.mta.MTAPlugin;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;


public class MageTrainingArenaScript extends Script {
    public static double version = 1.0;

    boolean firstTime = false;

    WorldPoint teleportersPoint = new WorldPoint(3363, 3318, 0);
    WorldPoint bankPoint = new WorldPoint(3365, 3318, 1);

    MTAPlugin mtaPlugin;

    public boolean run(MageTrainingArenaConfig config) {
        Microbot.enableAutoRunOn = false;
        mtaPlugin = (MTAPlugin)Microbot.getPluginManager().getPlugins()
                .stream().filter(x -> x instanceof net.runelite.client.plugins.mta.MTAPlugin)
                .findFirst().orElse(null);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (handleFirstTime())
                    return;

                var currentRoom = getCurrentRoom();
                if (currentRoom == null){
                    enterRoom(Room.ALCHEMIST);
                } else {
                    switch (currentRoom){
                        case ALCHEMIST:
                            handleAlchemistRoom();
                            break;
                        case GRAVEYARD:
                            handleGraveyardRoom();
                            break;
                        case ENCHANTMENT:
                            handleEnchantmentRoom();
                            break;
                        case TELEKINETIC:
                            handleTelekineticRoom();
                            break;
                    }
                }

                sleep(500, 1000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
        return true;
    }

    private void handleEnchantmentRoom() {
        MagicAction enchant;
        int staffId;

        var magicLevel = Microbot.getClient().getBoostedSkillLevel(Skill.MAGIC);
        if (magicLevel >= 68){
            enchant = MagicAction.ENCHANT_DRAGONSTONE_JEWELLERY;
            staffId = ItemID.MUD_BATTLESTAFF;
        } else if (magicLevel >= 57){
            enchant = MagicAction.ENCHANT_DIAMOND_JEWELLERY;
            staffId = ItemID.MUD_BATTLESTAFF;
        } else if (magicLevel >= 49){
            enchant = MagicAction.ENCHANT_RUBY_JEWELLERY;
            staffId = ItemID.STAFF_OF_FIRE;
        } else if (magicLevel >= 27){
            enchant = MagicAction.ENCHANT_EMERALD_JEWELLERY;
            staffId = ItemID.STAFF_OF_AIR;
        } else {
            enchant = MagicAction.ENCHANT_SAPPHIRE_JEWELLERY;
            staffId = ItemID.STAFF_OF_WATER;
        }

        if (!Rs2Equipment.isWearing(staffId))
            Rs2Inventory.wear(staffId);

        if (Rs2Inventory.isFull()){
            if (!Rs2Walker.walkTo(new WorldPoint(3363, 9640, 0)))
                return;

            Rs2GameObject.interact(ObjectID.HOLE_23698, "Deposit");
            Rs2Inventory.waitForInventoryChanges();
            return;
        }

        if (Rs2GroundItem.loot(ItemID.DRAGONSTONE_6903) && Rs2Inventory.getEmptySlots() > 0){
            Rs2Inventory.waitForInventoryChanges();
            return;
        }

        int pileId = -1;
        int itemId = -1;
        if (Rs2Widget.isWidgetVisible(195, 14)) {
            pileId = ObjectID.PENTAMID_PILE;
            itemId = ItemID.PENTAMID;
        }
        else if (Rs2Widget.isWidgetVisible(195, 16)) {
            pileId = ObjectID.ICOSAHEDRON_PILE;
            itemId = ItemID.ICOSAHEDRON;
        }
        else if (Rs2Widget.isWidgetVisible(195, 10)) {
            pileId = ObjectID.CUBE_PILE;
            itemId = ItemID.CUBE;
        }
        else if (Rs2Widget.isWidgetVisible(195, 12)) {
            pileId = ObjectID.CYLINDER_PILE;
            itemId = ItemID.CYLINDER;
        }

        if (pileId == -1)
            return;


        if (Rs2Inventory.contains(ItemID.DRAGONSTONE_6903))
            itemId = ItemID.DRAGONSTONE_6903;

        if (Rs2Inventory.contains(itemId)){
            Rs2Magic.cast(enchant);
            sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.INVENTORY);
            sleep(200, 500);
            Rs2Inventory.interact(itemId);
        } else {
            Rs2GameObject.interact(pileId, "Take-from");
            Rs2Inventory.waitForInventoryChanges();
        }
    }

    private void handleTelekineticRoom() {
        if (!Rs2Equipment.isWearing(ItemID.STAFF_OF_AIR))
            Rs2Inventory.wear(ItemID.STAFF_OF_AIR);

        var room = mtaPlugin.getTelekineticRoom();
        var target = room.getTarget();

        if (target == null) {
            // TODO walk to maze
            return;
        }

        var localTarget = LocalPoint.fromWorld(Microbot.getClient(), target);
        var targetConverted = WorldPoint.fromLocalInstance(Microbot.getClient(), localTarget);

        if (room.getGuardian().getWorldLocation().equals(room.getFinishLocation())){
            Rs2Npc.interact(room.getGuardian());
            Rs2Player.waitForAnimation();
        } else if (!Rs2Player.getWorldLocation().equals(targetConverted)) {
            if (Rs2Camera.isTileOnScreen(localTarget))
                Rs2Walker.walkCanvas(target);
            else
                Rs2Walker.walkTo(targetConverted);
            Rs2Player.waitForWalking();
        } else {
            Rs2Magic.cast(MagicAction.TELEKINETIC_GRAB);
            sleep(100, 300);
            Rs2Npc.interact(room.getGuardian());
            sleepUntil(() -> room.getGuardian().getId() == NullNpcID.NULL_6778);
            sleep(50);
            sleepUntil(() -> room.getGuardian().getId() == NpcID.MAZE_GUARDIAN || room.getGuardian().getId() == NpcID.MAZE_GUARDIAN_6779);
        }
    }

    private void handleGraveyardRoom() {

    }

    private void handleAlchemistRoom() {
        if (!Rs2Equipment.isWearing(ItemID.STAFF_OF_FIRE))
            Rs2Inventory.wear(ItemID.STAFF_OF_FIRE);

        var room = mtaPlugin.getAlchemyRoom();
        var best = room.getBest();
        var item = Rs2Inventory.get(best.getId());
        if (item != null) {
            Rs2Magic.alch(item);
            Rs2Player.waitForAnimation();
            return;
        }

        if (room.getSuggestion() == null)
            Rs2GameObject.interact("Cupboard", "Search");
        else
            Rs2GameObject.interact(room.getSuggestion().getGameObject(), "Take-5");
        Rs2Inventory.waitForInventoryChanges();
    }

    private Map<WorldArea, WorldPoint> telekineticRooms = Map.ofEntries(
            entry(new WorldArea(3324, 9697, 34, 30, 0), new WorldPoint(3334, 9718, 0)),
            entry(new WorldArea(3358, 9704, 34, 23, 0), new WorldPoint(3379, 9716, 0)),
            entry(new WorldArea(3327, 9669, 31, 27, 0), new WorldPoint(3352, 9690, 0)),
            entry(new WorldArea(3361, 9664, 28, 39, 0), new WorldPoint(3373, 9696, 0)),
            entry(new WorldArea(3331, 9702, 37, 24, 1), new WorldPoint(3362, 9713, 1)),
            entry(new WorldArea(3368, 9704, 22, 22, 1), new WorldPoint(3377, 9706, 1)),
            entry(new WorldArea(3332, 9671, 35, 29, 1), new WorldPoint(3355, 9693, 1)),
            entry(new WorldArea(3367, 9669, 25, 34, 1), new WorldPoint(3382, 9698, 1)),
            entry(new WorldArea(3332, 9696, 42, 31, 2), new WorldPoint(3359, 9701, 2)),
            entry(new WorldArea(3331, 9667, 45, 27, 2), new WorldPoint(3368, 9680, 2))
    );

    private Room getCurrentRoom(){
        for (var room : Room.values()){
            if (room == Room.TELEKINETIC && telekineticRooms.keySet().stream().anyMatch(x -> Rs2Player.getWorldLocation().distanceTo(x) == 0)
                    || room.getArea() != null && Rs2Player.getWorldLocation().distanceTo(room.getArea()) == 0)
                return room;
        }

        return null;
    }

    private void enterRoom(Room room){
        if (!Rs2Walker.walkTo(teleportersPoint))
            return;

        Rs2GameObject.interact(room.getTeleporter(), "Enter");
        Rs2Player.waitForAnimation();
        if (Rs2Widget.hasWidget("You must talk to the Entrance Guardian"))
            firstTime = true;
    }

    private void leaveRoom(){
        var room = getCurrentRoom();

        if (room == null)
            return;

        WorldPoint exit = null;
        if (room != Room.TELEKINETIC)
            exit = room.getExit();
        else {
            for (var kvp : telekineticRooms.entrySet()) {
                if (Rs2Player.getWorldLocation().distanceTo(kvp.getKey()) == 0){
                    exit = kvp.getValue();
                    break;
                }
            }
        }

        if (!Rs2Walker.walkTo(exit))
            return;

        Rs2Walker.setTarget(null);
        Rs2GameObject.interact(ObjectID.EXIT_TELEPORT, "Enter");
        Rs2Player.waitForWalking();
        sleep(500);
    }

    private boolean handleFirstTime(){
        if (firstTime){
            if (!Rs2Walker.walkTo(new WorldPoint(3363, 3304, 0)))
                return true;

            if (!Rs2Dialogue.isInDialogue())
                Rs2Npc.interact(NpcID.ENTRANCE_GUARDIAN, "Talk-to");
            else if (Rs2Dialogue.hasSelectAnOption() && Rs2Widget.hasWidget("I'm new to this place"))
                Rs2Widget.clickWidget("I'm new to this place");
            else if (Rs2Dialogue.hasSelectAnOption() && Rs2Widget.hasWidget("Thanks, bye!")){
                Rs2Widget.clickWidget("Thanks, bye!");
                firstTime = false;
            }
            else
                Rs2Dialogue.clickContinue();

            return true;
        }

        return false;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
