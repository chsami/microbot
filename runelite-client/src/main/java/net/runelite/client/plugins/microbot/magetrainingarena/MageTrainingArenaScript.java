package net.runelite.client.plugins.microbot.magetrainingarena;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.*;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.FireStaves;
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
import net.runelite.client.plugins.mta.alchemy.AlchemyRoomTimer;
import net.runelite.client.plugins.mta.telekinetic.TelekineticRoom;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.Timer;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

public class MageTrainingArenaScript extends Script {
    public static double version = 1.0;

    private static boolean firstTime = false;

    private static final WorldPoint portalPoint = new WorldPoint(3363, 3318, 0);
    private static final WorldPoint bankPoint = new WorldPoint(3365, 3318, 1);

    private MageTrainingArenaConfig config;
    private Rooms currentRoom;
    private int nextHpThreshold = 50;
    private Boolean btp = null;
    private int lastAlchTick = 0;
    private int shapesToPick = 3;

    @Getter
    private static MTAPlugin mtaPlugin;
    @Getter
    private static final Map<Points, Integer> currentPoints = Arrays.stream(Points.values()).collect(Collectors.toMap(x -> x, x -> -1));
    @Getter
    private static int bought;
    @Getter
    private static int buyable;

    public boolean run(MageTrainingArenaConfig config) {
        this.config = config;
        Microbot.enableAutoRunOn = true;
        bought = 0;
        buyable = 0;

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

                    if (currentPoints.entrySet().stream().allMatch(x -> getRequiredPoints().get(x.getKey()) * (config.buyRewards() ? 1 : (buyable + 1)) <= x.getValue())) {
                        if (config.buyRewards()){
                            var rewardToBuy = config.reward();
                            while (rewardToBuy.getPreviousReward() != null && !Rs2Inventory.contains(rewardToBuy.getPreviousReward().getItemId()))
                                rewardToBuy = rewardToBuy.getPreviousReward();

                            buyReward(rewardToBuy);
                        } else {
                            buyable = getRequiredPoints().entrySet().stream()
                                    .mapToInt(x -> currentPoints.get(x.getKey()) / x.getValue())
                                    .min().orElseThrow();
                        }
                        return;
                    }

                    var missingPoints = currentPoints.entrySet().stream()
                            .filter(x -> getRequiredPoints().get(x.getKey()) * (config.buyRewards() ? 1 : (buyable + 1)) > x.getValue()
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
                                || currentPoints.get(currentRoom.getPoints()) >= getRequiredPoints().get(currentRoom.getPoints())  * (config.buyRewards() ? 1 : (buyable + 1))) {
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
        var reward = config.reward();
        var previousRewards = new ArrayList<Integer>();
        while (reward.getPreviousReward() != null){
            reward = reward.getPreviousReward();
            previousRewards.add(reward.getItemId());
        }

        Predicate<Rs2Item> additionalItemPredicate = x -> !x.name.toLowerCase().contains("rune")
                && !x.name.toLowerCase().contains("staff")
                && !previousRewards.contains(x.id);

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

    private Map<Points, Integer> getRequiredPoints(){
        return getRequiredPoints(config);
    }

    public static Map<Points, Integer> getRequiredPoints(MageTrainingArenaConfig config){
        var currentReward = config.reward();
        var requiredPoints = currentReward.getPoints().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        while (currentReward.getPreviousReward() != null){
            currentReward = currentReward.getPreviousReward();
            if (Rs2Inventory.contains(currentReward.getItemId()))
                break;

            for (var points : requiredPoints.entrySet())
                points.setValue(points.getValue() + currentReward.getPoints().get(points.getKey()));
        }

        return requiredPoints;
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

        if (!Rs2Equipment.isWearing(staffId)){
            Rs2Inventory.wear(staffId);
            return;
        }

        if (Rs2Inventory.isFull()){
            if (!Rs2Walker.walkTo(new WorldPoint(3363, 9640, 0)))
                return;

            Rs2Walker.setTarget(null);
            Rs2GameObject.interact(ObjectID.HOLE_23698, "Deposit");
            Rs2Player.waitForWalking();
            return;
        }

        if (Rs2GroundItem.loot(ItemID.DRAGONSTONE_6903) && Rs2Inventory.getEmptySlots() > 0){
            Rs2Inventory.waitForInventoryChanges();
            return;
        }

        var bonusShape = getBonusShape();
        if (bonusShape == null) return;

        var object = Rs2GameObject.getGameObjects(bonusShape.getObjectId()).stream()
                .filter(Rs2Camera::isTileOnScreen)
                .min(Comparator.comparing(x -> x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation())))
                .orElse(null);

        if (object == null) {
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

        int itemId;
        if (Rs2Inventory.contains(ItemID.DRAGONSTONE_6903))
            itemId = ItemID.DRAGONSTONE_6903;
        else
            itemId = bonusShape.getItemId();

        if (Rs2Inventory.contains(ItemID.DRAGONSTONE_6903) || Rs2Inventory.count(itemId) >= shapesToPick){
            shapesToPick = Random.random(2, 4);

            Rs2Magic.cast(enchant);
            sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.INVENTORY);
            sleep(200, 500);
            Rs2Inventory.interact(itemId);

            sleepUntil(() -> !Rs2Inventory.contains(itemId) || itemId != ItemID.DRAGONSTONE_6903 && bonusShape != getBonusShape(), 20_000);
        } else if (Rs2GameObject.interact(object, "Take-from")) {
            Rs2Walker.setTarget(null);
            Rs2Inventory.waitForInventoryChanges();
        } else
            Rs2Walker.walkFastCanvas(object.getWorldLocation());
    }

    private EnchantmentShapes getBonusShape(){
        for (var shape : EnchantmentShapes.values())
            if (Rs2Widget.isWidgetVisible(shape.getWidgetId(), shape.getWidgetChildId()))
                return shape;

        return null;
    }

    private void handleTelekineticRoom() {
        if (!Rs2Equipment.isWearing(config.airStaff().getItemId())){
            Rs2Inventory.wear(config.airStaff().getItemId());
            return;
        }

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
            sleep(400, 600);
        }

        var localTarget = LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), target);
        var targetConverted = WorldPoint.fromLocalInstance(Microbot.getClient(), Objects.requireNonNull(localTarget));

        if (room.getGuardian().getWorldLocation().equals(room.getFinishLocation())){
            sleepUntil(() -> room.getGuardian().getId() == NpcID.MAZE_GUARDIAN_6779);
            sleep(200, 400);
            Rs2Npc.interact(room.getGuardian(), "New-maze");
            sleepUntil(() -> Rs2Player.getWorldLocation().distanceTo(teleRoom.getArea()) != 0);
        } else {
            if (!Rs2Player.getWorldLocation().equals(targetConverted)
                    && (Microbot.getClient().getLocalDestinationLocation() == null
                        || !Microbot.getClient().getLocalDestinationLocation().equals(localTarget))) {
                if (Rs2Camera.isTileOnScreen(localTarget)) {
                    Rs2Walker.walkFastCanvas(targetConverted);
                    Rs2Walker.setTarget(null);
                    sleep(200, 400);
                } else {
                    Rs2Walker.walkTo(targetConverted);
                }
            }

            if (!Rs2Player.isAnimating()
                    && StreamSupport.stream(Microbot.getClient().getTopLevelWorldView().getProjectiles().spliterator(), false).noneMatch(x -> x.getId() == GraphicID.TELEKINETIC_SPELL)
                    && !TelekineticRoom.getMoves().isEmpty()
                    && TelekineticRoom.getMoves().peek() == room.getPosition()
                    && room.getGuardian().getId() != NullNpcID.NULL_6778
                    && !room.getGuardian().getLocalLocation().equals(room.getDestination())){
                Rs2Magic.cast(MagicAction.TELEKINETIC_GRAB);
                sleep(200, 800);
                Rs2Npc.interact(room.getGuardian());
            }
        }
    }

    private void handleGraveyardRoom() {
        if (!Rs2Equipment.isWearing(config.waterStaff().getItemId())) {
            Rs2Inventory.wear(config.waterStaff().getItemId());
            return;
        }

        if (btp == null)
            btp = Rs2Magic.canCast(MagicAction.BONES_TO_PEACHES);

        var bonepile = Rs2GameObject.findObjectByLocation(new WorldPoint(3352, 9637, 1));
        var foodChute = Rs2GameObject.findObjectByLocation(new WorldPoint(3354, 9639, 1));

        var boneGoal = 28 - Rs2Inventory.items().stream().filter(x -> x.name.equalsIgnoreCase("Animals' bones")).count();
        if (Counter.getCount() >= boneGoal){
            Rs2Magic.cast(btp ? MagicAction.BONES_TO_PEACHES : MagicAction.BONES_TO_BANANAS);
            Rs2Player.waitForAnimation();
            return;
        }

        if (Rs2Inventory.contains(ItemID.BANANA, ItemID.PEACH)){
            var currentHp = Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS);
            var maxHp = Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
            if ((currentHp * 100) / maxHp < nextHpThreshold){
                var maxAmountToEat = (maxHp - currentHp) / (btp ? 8 : 2);
                var amountToEat = Random.random(Math.min(2, maxAmountToEat), Math.min(6, maxAmountToEat));
                nextHpThreshold = Random.random(config.healingThresholdMin(), config.healingThresholdMax());
                for (int i = 0; i < amountToEat; i++) {
                    Rs2Inventory.interact(btp ? ItemID.PEACH : ItemID.BANANA, "eat");

                    if (i < amountToEat - 1)
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
        if (!Rs2Equipment.isWearing(config.fireStaff().getItemId())){
            Rs2Inventory.wear(config.fireStaff().getItemId());
            return;
        }

        var room = mtaPlugin.getAlchemyRoom();
        var best = room.getBest();
        var item = Rs2Inventory.get(best.getId());
        if (item != null) {
            if (lastAlchTick + 3 > Microbot.getClient().getTickCount()) {
                sleepUntil(() -> lastAlchTick + 3 <= Microbot.getClient().getTickCount());
                sleep(50, 200);
            }
            Rs2Magic.alch(item);
            lastAlchTick = Microbot.getClient().getTickCount();
            return;
        }

        var timer = (AlchemyRoomTimer) Microbot.getInfoBoxManager().getInfoBoxes().stream()
                .filter(x -> x instanceof AlchemyRoomTimer)
                .findFirst().orElse(null);
        if (timer == null || Integer.parseInt(timer.getText().split(":")[1]) < 2)
            return;

        if (room.getSuggestion() == null) {
            Rs2GameObject.interact("Cupboard", "Search");

            if (sleepUntilTrue(Rs2Player::isWalking, 100, 1000))
                sleepUntil(() -> !Rs2Player.isWalking());
        }
        else {
            Rs2GameObject.interact(room.getSuggestion().getGameObject(), "Take-5");
            Rs2Inventory.waitForInventoryChanges();
        }
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

        if (reward == config.reward())
            bought++;
    }

    public static Rooms getCurrentRoom(){
        for (var room : Rooms.values()){
            if (room == Rooms.TELEKINETIC && Arrays.stream(TelekineticRooms.values()).anyMatch(x -> Rs2Player.getWorldLocation().distanceTo(x.getArea()) == 0)
                    || room.getArea() != null && Rs2Player.getWorldLocation().distanceTo(room.getArea()) == 0)
                return room;
        }

        return null;
    }

    public static void enterRoom(Rooms room){
        if (!Rs2Walker.walkTo(portalPoint))
            return;

        Rs2GameObject.interact(room.getTeleporter(), "Enter");
        Rs2Player.waitForAnimation();
        if (Rs2Widget.hasWidget("You must talk to the Entrance Guardian"))
            firstTime = true;
    }

    public static void leaveRoom(){
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
