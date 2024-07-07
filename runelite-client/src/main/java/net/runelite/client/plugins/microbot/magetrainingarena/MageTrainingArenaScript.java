package net.runelite.client.plugins.microbot.magetrainingarena;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.TelekineticRooms;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.FireStaves;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.Points;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.Rewards;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.Rooms;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.mta.MTAPlugin;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MageTrainingArenaScript extends Script {
    public static double version = 1.0;

    boolean firstTime = false;

    WorldPoint portalPoint = new WorldPoint(3363, 3318, 0);
    WorldPoint bankPoint = new WorldPoint(3365, 3318, 1);

    MageTrainingArenaConfig config;
    public static MTAPlugin mtaPlugin;
    int nextHpThreshold = 50;

    public static Rooms currentRoom;
    public static Map<Points, Integer> currentPoints = Arrays.stream(Points.values()).collect(Collectors.toMap(x -> x, x -> -1));
    public static int bought = 0;
    public static int buyable = 0;

    public boolean run(MageTrainingArenaConfig config) {
        this.config = config;
        Microbot.enableAutoRunOn = false;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (mtaPlugin != null && !Microbot.getPluginManager().isActive(mtaPlugin)) return;

                if (mtaPlugin == null) {
                    if (Microbot.getPluginManager() == null) return;

                    mtaPlugin = (MTAPlugin) Microbot.getPluginManager().getPlugins()
                            .stream().filter(x -> x instanceof net.runelite.client.plugins.mta.MTAPlugin)
                            .findFirst().orElse(null);

                    return;
                }

                if (handleFirstTime())
                    return;

                currentRoom = getCurrentRoom();
                updatePoints();
                if (initPoints())
                    return;

                if (currentRoom == null){
                    if (ensureInventory())
                        return;

                    if (currentPoints.entrySet().stream().allMatch(x -> config.reward().getPoints().get(x.getKey()) * (config.buyRewards() ? 1 : (buyable + 1)) <= x.getValue())) {
                        if (config.buyRewards()){
                            // TODO multi step buying (wands)
                            buyReward(config.reward());
                        } else {
                            buyable = config.reward().getPoints().entrySet().stream()
                                    .mapToInt(x -> currentPoints.get(x.getKey()) / x.getValue())
                                    .min().orElseThrow();
                        }
                        return;
                    }

                    var missingPoints = currentPoints.entrySet().stream()
                            .filter(x -> config.reward().getPoints().get(x.getKey()) * (config.buyRewards() ? 1 : (buyable + 1)) > x.getValue()
                                && Arrays.stream(Rooms.values()).anyMatch(y -> y.getPoints() == x.getKey() && Rs2Inventory.contains(y.getRunesId())))
                            .map(Map.Entry::getKey).collect(Collectors.toList());

                    if (!missingPoints.isEmpty()){
                        var index = Random.random(0, missingPoints.size());
                        var nextRoom = Arrays.stream(Rooms.values())
                                .filter(x -> x.getPoints() == missingPoints.get(index))
                                .findFirst().orElseThrow();
                        enterRoom(nextRoom);
                    } else {
                        Microbot.showMessage("MTA: Out of runes! Please restart the plugin after you restocked on runes.");
                        sleep(500);
                        shutdown();
                    }
                } else if (!Rs2Inventory.contains(currentRoom.getRunesId())
                                || currentPoints.get(currentRoom.getPoints()) >= config.reward().getPoints().get(currentRoom.getPoints())  * (config.buyRewards() ? 1 : (buyable + 1))) {
                    leaveRoom();
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
                if (ex instanceof InterruptedException)
                    return;

                System.out.println(ex.getMessage());
                ex.printStackTrace(System.out);
                Microbot.log("MTA Exception: " + ex.getMessage());
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean ensureInventory() {
        Predicate<Rs2Item> additionalItemPredicate = x -> !x.name.toLowerCase().contains("rune") && !x.name.toLowerCase().contains("staff");

        if (Rs2Inventory.contains(additionalItemPredicate)){
            if (!Rs2Bank.walkToBankAndUseBank())
                return true;

            Rs2Bank.depositAll(additionalItemPredicate);
            return true;
        }

        return false;
    }

    private boolean initPoints() {
        if (currentPoints.values().stream().anyMatch(x -> x == -1)){
            if (currentRoom != null)
                leaveRoom();
            else
                Rs2Walker.walkTo(portalPoint);

            return true;
        }

        return false;
    }

    private void updatePoints() {
        for (var points : currentPoints.entrySet()){
            int gain = 0;
            if (points.getKey() == Points.ALCHEMIST && currentRoom == Rooms.ALCHEMIST && Rs2Inventory.hasItem("Coins"))
                gain = Rs2Inventory.get("Coins").quantity / 100;

            var widget = Rs2Widget.getWidget(points.getKey().getWidgetId(), points.getKey().getChildId());
            if (widget != null && !Microbot.getClientThread().runOnClientThread(widget::isHidden))
                currentPoints.put(points.getKey(), Integer.parseInt(widget.getText().replace(",", "")));
            else {
                var roomWidget = Rs2Widget.getWidget(points.getKey().getRoomWidgetId(), points.getKey().getRoomChildId());
                if (roomWidget != null)
                    currentPoints.put(points.getKey(), Integer.parseInt(roomWidget.getText().replace(",", "")) + gain);
            }
        }
    }

    private void handleEnchantmentRoom() {
        MagicAction enchant;
        int staffId;

        var magicLevel = Microbot.getClient().getBoostedSkillLevel(Skill.MAGIC);
        if (magicLevel >= 87 && (config.fireStaff() == FireStaves.LAVA_BATTLESTAFF || config.fireStaff() == FireStaves.MYSTIC_LAVA_STAFF)){
            enchant = MagicAction.ENCHANT_ONYX_JEWELLERY;
            staffId = config.fireStaff().getItemId();
        } else if (magicLevel >= 68){
            enchant = MagicAction.ENCHANT_DRAGONSTONE_JEWELLERY;
            staffId = config.waterStaff().getItemId();
        } else if (magicLevel >= 57){
            enchant = MagicAction.ENCHANT_DIAMOND_JEWELLERY;
            staffId = config.earthStaff().getItemId();
        } else if (magicLevel >= 49){
            enchant = MagicAction.ENCHANT_RUBY_JEWELLERY;
            staffId = config.fireStaff().getItemId();
        } else if (magicLevel >= 27){
            enchant = MagicAction.ENCHANT_EMERALD_JEWELLERY;
            staffId = config.airStaff().getItemId();
        } else {
            enchant = MagicAction.ENCHANT_SAPPHIRE_JEWELLERY;
            staffId = config.waterStaff().getItemId();
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

        if (pileId == -1) {
            var index = Random.random(0, 4);
            Rs2Walker.walkTo(new WorldPoint[]{
                    new WorldPoint(3347, 9655, 0),
                    new WorldPoint(3378, 9655, 0),
                    new WorldPoint(3379, 9624, 0),
                    new WorldPoint(3346, 9624, 0)
            }[index]);
            Rs2Player.waitForWalking();
            return;
        }


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
        if (!Rs2Equipment.isWearing(config.airStaff().getItemId()))
            Rs2Inventory.wear(config.airStaff().getItemId());

        var room = mtaPlugin.getTelekineticRoom();
        var teleRoom = Arrays.stream(TelekineticRooms.values())
                .filter(x -> Rs2Player.getWorldLocation().distanceTo(x.getArea()) == 0)
                .findFirst().orElseThrow();

        // Walk to maze if guardian is not visible
        WorldPoint target;
        if (room.getTarget() != null)
            target = room.getTarget();
        else {
            Rs2Walker.walkTo(teleRoom.getMaze());
            sleepUntil(() -> room.getTarget() != null, 10_000);
            Rs2Walker.setTarget(null);
            target = room.getTarget();
        }

        var localTarget = LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), target);
        var targetConverted = WorldPoint.fromLocalInstance(Microbot.getClient(), Objects.requireNonNull(localTarget));

        if (room.getGuardian().getWorldLocation().equals(room.getFinishLocation())){
            sleepUntil(() -> room.getGuardian().getId() == NpcID.MAZE_GUARDIAN_6779);
            sleep(200, 400);
            Rs2Npc.interact(room.getGuardian(), "New-maze");
            sleepUntil(() -> Rs2Player.getWorldLocation().distanceTo(teleRoom.getArea()) != 0);
        } else {
            if (!Rs2Player.getWorldLocation().equals(targetConverted)) {
                if (Rs2Camera.isTileOnScreen(localTarget)) {
                    Rs2Walker.walkCanvas(target);
                    sleep(300, 900);
                }
                else {
                    Rs2Walker.walkTo(targetConverted);
                    return;
                }
            }

            Rs2Magic.cast(MagicAction.TELEKINETIC_GRAB);
            sleepUntil(() -> room.getGuardian() == null
                    || room.getGuardian().getWorldLocation().equals(room.getLocation())
                        && room.getGuardian().getId() != NullNpcID.NULL_6778
                        && Rs2Player.getWorldLocation().equals(targetConverted), 10_000);
            if (!Rs2Player.getWorldLocation().equals(targetConverted)) return;

            sleep(400, 600);
            Rs2Npc.interact(room.getGuardian());
            sleepUntil(() -> !target.equals(room.getTarget()));
        }
    }

    private void handleGraveyardRoom() {
        if (!Rs2Equipment.isWearing(config.waterStaff().getItemId()))
            Rs2Inventory.wear(config.waterStaff().getItemId());

        var bonepile = Rs2GameObject.findObjectByLocation(new WorldPoint(3352, 9637, 1));
        var foodChute = Rs2GameObject.findObjectByLocation(new WorldPoint(3354, 9639, 1));

        var boneGoal = 28 - Rs2Inventory.items().stream().filter(x -> x.name.equalsIgnoreCase("Animals' bones")).count();
        if (Counter.getCount() >= boneGoal){
            // TODO Handle bones to peaches
            Rs2Magic.cast(MagicAction.BONES_TO_BANANAS);
            Rs2Player.waitForAnimation();
            return;
        }

        if (Rs2Inventory.contains(ItemID.BANANA, ItemID.PEACH)){
            if ((Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100) / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS) < nextHpThreshold){
                var amountToEat = Random.random(2, 6);
                nextHpThreshold = Random.random(config.healingThresholdMin(), config.healingThresholdMax());
                for (int i = 0; i < amountToEat; i++) {
                    Rs2Inventory.interact(Rs2Inventory.contains(ItemID.BANANA) ? ItemID.BANANA : ItemID.PEACH, "eat");
                    sleep(1400, 2000);
                }
            }

            Rs2GameObject.interact(foodChute, "Deposit");
            Rs2Inventory.waitForInventoryChanges();
            return;
        }

        Rs2GameObject.interact(bonepile, "Grab");
        if (Rs2Player.getWorldLocation().distanceTo(Objects.requireNonNull(bonepile).getWorldLocation()) > 1)
            Rs2Player.waitForWalking();
    }

    private void handleAlchemistRoom() {
        if (!Rs2Equipment.isWearing(config.fireStaff().getItemId()))
            Rs2Inventory.wear(config.fireStaff().getItemId());

        var room = mtaPlugin.getAlchemyRoom();
        var best = room.getBest();
        var item = Rs2Inventory.get(best.getId());
        if (item != null) {
            if (Rs2Player.isAnimating()) {
                sleepUntil(() -> !Rs2Player.isAnimating());
                sleep(100, 300);
            }
            Rs2Magic.alch(item);
            Rs2Inventory.waitForInventoryChanges();
            return;
        }

        if (room.getSuggestion() == null)
            Rs2GameObject.interact("Cupboard", "Search");
        else
            Rs2GameObject.interact(room.getSuggestion().getGameObject(), "Take-5");
        Rs2Inventory.waitForInventoryChanges();
    }

    private void buyReward(Rewards reward){
        if (!Rs2Walker.walkTo(bankPoint))
            return;

        if (!Rs2Widget.isWidgetVisible(197, 0)){
            Rs2Npc.interact(NpcID.REWARDS_GUARDIAN, "Trade-with");
            sleepUntil(() -> Rs2Widget.isWidgetVisible(197, 0));
            sleep(400, 600);
        }
        var rewardWidgets = Rs2Widget.getWidget(197, 11).getDynamicChildren();
        var widget = Arrays.stream(rewardWidgets).filter(x -> x.getItemId() == reward.getItemId()).findFirst().orElse(null);
        Rs2Widget.clickWidgetFast(widget, Arrays.asList(rewardWidgets).indexOf(widget));
        sleep(400, 600);
        Rs2Widget.clickWidget(197, 9);
        Rs2Inventory.waitForInventoryChanges();
        bought++;
    }

    private Rooms getCurrentRoom(){
        for (var room : Rooms.values()){
            if (room == Rooms.TELEKINETIC && Arrays.stream(TelekineticRooms.values()).anyMatch(x -> Rs2Player.getWorldLocation().distanceTo(x.getArea()) == 0)
                    || room.getArea() != null && Rs2Player.getWorldLocation().distanceTo(room.getArea()) == 0)
                return room;
        }

        return null;
    }

    private void enterRoom(Rooms room){
        if (!Rs2Walker.walkTo(portalPoint))
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
        if (room != Rooms.TELEKINETIC)
            exit = room.getExit();
        else {
            for (var teleRoom : TelekineticRooms.values()) {
                if (Rs2Player.getWorldLocation().distanceTo(teleRoom.getArea()) == 0){
                    exit = teleRoom.getExit();
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
