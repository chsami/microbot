package net.runelite.client.plugins.microbot.util.player;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.globval.VarbitValues;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.security.Login;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.runelite.api.MenuAction.CC_OP;
import static net.runelite.client.plugins.microbot.util.Global.*;


public class Rs2Player {
    static int VENOM_VALUE_CUTOFF = -38;
    private static int antiFireTime = -1;
    private static int superAntiFireTime = -1;
    private static int divineRangedTime = -1;
    private static int divineBastionTime = -1;
    public static int antiVenomTime = -1;
    public static int staminaBuffTime = -1;
    public static int antiPoisonTime = -1;
    public static Instant lastAnimationTime = null;
    @Getter
    public static int lastAnimationID = AnimationID.IDLE;

    public static boolean hasAntiFireActive() {
        return antiFireTime > 0 || hasSuperAntiFireActive();
    }

    public static boolean hasSuperAntiFireActive() {
        return superAntiFireTime > 0;
    }

    public static boolean hasDivineRangedActive() {
        return divineRangedTime > 0 || hasDivineBastionActive();
    }

    public static boolean hasRangingPotionActive() {
        return Microbot.getClient().getBoostedSkillLevel(Skill.RANGED) - 5 > Microbot.getClient().getRealSkillLevel(Skill.RANGED);
    }

    public static boolean hasDivineBastionActive() {
        return divineBastionTime > 0;
    }

    public static boolean hasAntiVenomActive() {
        if (Rs2Equipment.isWearing("serpentine helm")) {
            return true;
        } else return antiVenomTime < VENOM_VALUE_CUTOFF;
    }

    public static boolean hasAntiPoisonActive() {
        return antiPoisonTime > 0;
    }

    public static boolean hasStaminaBuffActive() {
        return staminaBuffTime > 0;
    }

    private static final Map<Player, Long> playerDetectionTimes = new ConcurrentHashMap<>();

    public static void handlePotionTimers(VarbitChanged event) {
        if (event.getVarbitId() == Varbits.ANTIFIRE) {
            antiFireTime = event.getValue();
        }
        if (event.getVarbitId() == Varbits.SUPER_ANTIFIRE) {
            superAntiFireTime = event.getValue();
        }
        if (event.getVarbitId() == Varbits.DIVINE_RANGING) {
            divineRangedTime = event.getValue();
        }
        if (event.getVarbitId() == Varbits.DIVINE_BASTION) {
            divineBastionTime = event.getValue();
        }
        if (event.getVarbitId() == Varbits.STAMINA_EFFECT) {
            staminaBuffTime = event.getValue();
        }
        if (event.getVarpId() == VarPlayer.POISON) {
            if (event.getValue() >= VENOM_VALUE_CUTOFF) {
                antiVenomTime = 0;
            } else {
                antiVenomTime = event.getValue();
            }
            final int poisonVarp = event.getValue();

            if (poisonVarp == 0) {
                antiPoisonTime = -1;
            } else {
                antiPoisonTime = poisonVarp;
            }
        }
    }

    public static void handleAnimationChanged(AnimationChanged event) {
        if (!(event.getActor() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getActor();
        if (player != Microbot.getClient().getLocalPlayer()) {
            return;
        }

        if (player.getAnimation() != AnimationID.IDLE) {
            lastAnimationTime = Instant.now();
            lastAnimationID = player.getAnimation();
        }
    }

    /**
     * Wait for walking
     */
    public static void waitForWalking() {
        boolean result = sleepUntilTrue(Rs2Player::isWalking, 100, 5000);
        if (!result) return;
        sleepUntil(() -> !Rs2Player.isWalking());
    }

    /**
     * Wait for walking in time
     *
     * @param time
     */
    public static void waitForWalking(int time) {
        boolean result = sleepUntilTrue(Rs2Player::isWalking, 100, time);
        if (!result) return;
        sleepUntil(() -> !Rs2Player.isWalking(), time);
    }

    /**
     * Wait for XP Drop
     *
     * @param skill
     *
     * @return
     */
    public static boolean waitForXpDrop(Skill skill) {
        return waitForXpDrop(skill, 5000, false);
    }

    /**
     * Wait for XP Drop or if inventory is full
     *
     * @param skill
     * @param time
     *
     * @return
     */
    public static boolean waitForXpDrop(Skill skill, int time) {
        return waitForXpDrop(skill, time, false);
    }

    /**
     * Wait for XP Drop or if inventory is full
     *
     * @param skill
     * @param inventoryFullCheck
     *
     * @return
     */
    public static boolean waitForXpDrop(Skill skill, boolean inventoryFullCheck) {
        return waitForXpDrop(skill, 5000, inventoryFullCheck);
    }

    /**
     * Wait for XP Drop in time or if inventory is full
     *
     * @param skill
     * @param time
     * @param inventoryFullCheck
     *
     * @return
     */
    public static boolean waitForXpDrop(Skill skill, int time, boolean inventoryFullCheck) {
        final int skillExp = Microbot.getClient().getSkillExperience(skill);
        return sleepUntilTrue(() -> skillExp != Microbot.getClient().getSkillExperience(skill) || (inventoryFullCheck && Rs2Inventory.isFull()), 100, time);
    }

    /**
     * Wait for animation
     */
    public static void waitForAnimation() {
        boolean result = sleepUntilTrue(Rs2Player::isAnimating, 100, 5000);
        if (!result) return;
        sleepUntil(() -> !Rs2Player.isAnimating());
    }

    /**
     * Wait for animation
     *
     * @param time
     */
    public static void waitForAnimation(int time) {
        boolean result = sleepUntilTrue(Rs2Player::isAnimating, 100, time);
        if (!result) return;
        sleepUntil(() -> !Rs2Player.isAnimating(), time);
    }

    /**
     * Chek if the player is animating within the past ms
     *
     * @param ms
     *
     * @return
     */
    public static boolean isAnimating(int ms) {
        return (lastAnimationTime != null && Duration.between(lastAnimationTime, Instant.now()).toMillis() < ms) || getAnimation() != AnimationID.IDLE;
    }

    /**
     * Check if the player is animating within the past 600ms
     *
     * @return
     */
    public static boolean isAnimating() {
        return isAnimating(600);
    }

    /**
     * Check if the player is walking
     *
     * @return
     */
    public static boolean isWalking() {
        return Rs2Player.isMoving();
    }

    /**
     * Checks if the player is moving
     *
     * @return
     */
    public static boolean isMoving() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().getPoseAnimation()
                != Microbot.getClient().getLocalPlayer().getIdlePoseAnimation());
    }

    /**
     * Checks if the player is interacting
     *
     * @return
     */
    public static boolean isInteracting() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().isInteracting());
    }

    /**
     * Checks if the player is a member
     *
     * @return
     */
    public static boolean isMember() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarpValue(VarPlayer.MEMBERSHIP_DAYS) > 0);
    }

    @Deprecated(since = "Use the Rs2Combat.specState method", forRemoval = true)
    public static void toggleSpecialAttack(int energyRequired) {
        int currentSpecEnergy = Microbot.getClient().getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
        if (currentSpecEnergy >= energyRequired && (Microbot.getClient().getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED) == 0)) {
            Rs2Widget.clickWidget("special attack");
        }
    }

    /**
     * Toggles player run
     *
     * @param toggle
     *
     * @return
     */
    public static boolean toggleRunEnergy(boolean toggle) {
        if (Microbot.getVarbitPlayerValue(173) == 0 && !toggle) return true;
        if (Microbot.getVarbitPlayerValue(173) == 1 && toggle) return true;
        Widget widget = Rs2Widget.getWidget(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getId());
        if (widget == null) return false;
        if (Microbot.getClient().getEnergy() > 1000 && toggle) {
            Microbot.getMouse().click(widget.getCanvasLocation());
            sleep(150, 300);
            return true;
        } else if (!toggle) {
            Microbot.getMouse().click(widget.getCanvasLocation());
            sleep(150, 300);
            return true;
        }
        return false;
    }

    /**
     * Checks if run is enabled
     *
     * @return
     */
    public static boolean isRunEnabled() {
        return Microbot.getVarbitPlayerValue(173) == 1;
    }

    /**
     * Logs the player out of the game
     */
    public static void logout() {
        if (Microbot.isLoggedIn()) {
            //logout from main tab
            Microbot.doInvoke(new NewMenuEntry(-1, 11927560, CC_OP.getId(), 1, -1, "Logout"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
            //logout from world hopper
            Microbot.doInvoke(new NewMenuEntry(-1, 4522009, CC_OP.getId(), 1, -1, "Logout"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));
        }

        //Rs2Reflection.invokeMenu(-1, 11927560, CC_OP.getId(), 1, -1, "Logout", "", -1, -1);
    }

    /**
     * Logouts out the player is found in an area around the player for time
     *
     * @param amountOfPlayers to detect before triggering logout
     * @param time            in milliseconds
     * @param distance        from the player
     *
     * @return
     */
    public static boolean logoutIfPlayerDetected(int amountOfPlayers, int time, int distance) {
        List<Player> players = getPlayers();
        long currentTime = System.currentTimeMillis();

        if (distance > 0) {
            players = players.stream()
                    .filter(x -> x != null && x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) <= distance)
                    .collect(Collectors.toList());
        }
        if (time > 0 && players.size() > amountOfPlayers) {
            // Update detection times for currently detected players
            for (Player player : players) {
                playerDetectionTimes.putIfAbsent(player, currentTime);
            }

            // Remove players who are no longer detected
            playerDetectionTimes.keySet().retainAll(players);

            // Check if any player has been detected for longer than the specified time
            for (Player player : players) {
                long detectionTime = playerDetectionTimes.getOrDefault(player, 0L);
                if (currentTime - detectionTime >= time) { // convert time to milliseconds
                    logout();
                    return true;
                }
            }
        } else if (players.size() >= amountOfPlayers) {
            logout();
            return true;
        }
        return false;
    }

    /**
     * @param amountOfPlayers
     * @param time
     *
     * @return
     */
    public static boolean logoutIfPlayerDetected(int amountOfPlayers, int time) {
        return logoutIfPlayerDetected(amountOfPlayers, time, 0);
    }

    /**
     * @param amountOfPlayers
     *
     * @return
     */
    public static boolean logoutIfPlayerDetected(int amountOfPlayers) {
        return logoutIfPlayerDetected(amountOfPlayers, 0, 0);
    }

    /**
     * Hop if player is detected
     *
     * @param amountOfPlayers, time, distance
     *
     * @return true if player is detected and hopped
     */
    public static boolean hopIfPlayerDetected(int amountOfPlayers, int time, int distance) {
        List<Player> players = getPlayers();
        long currentTime = System.currentTimeMillis();

        if (distance > 0) {
            players = players.stream()
                    .filter(x -> x != null && x.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) <= distance)
                    .collect(Collectors.toList());
        }
        if (time > 0 && players.size() >= amountOfPlayers) {
            // Update detection times for currently detected players
            for (Player player : players) {
                playerDetectionTimes.putIfAbsent(player, currentTime);
            }

            // Remove players who are no longer detected
            playerDetectionTimes.keySet().retainAll(players);

            // Check if any player has been detected for longer than the specified time
            for (Player player : players) {
                long detectionTime = playerDetectionTimes.getOrDefault(player, 0L);
                if (currentTime - detectionTime >= time) { // convert time to milliseconds
                    int randomWorld = Login.getRandomWorld(isMember());
                    Microbot.hopToWorld(randomWorld);
                    return true;
                }
            }
        } else if (players.size() >= amountOfPlayers) {
            int randomWorld = Login.getRandomWorld(isMember());
            Microbot.hopToWorld(randomWorld);
            return true;
        }
        return false;
    }

    /**
     * Eat food at a certain health percentage, will search inventory for first possible food item.
     *
     * @param percentage
     *
     * @return
     */
    public static boolean eatAt(int percentage) {
        double treshHold = (double) (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100) / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
        if (treshHold <= percentage) {
            List<Rs2Item> foods = Rs2Inventory.getInventoryFood();
            if (!foods.isEmpty()) {
                if (foods.get(0).getName().toLowerCase().contains("jug of wine")) {
                    return Rs2Inventory.interact(foods.get(0), "drink");
                } else {
                    return Rs2Inventory.interact(foods.get(0), "eat");
                }
            }
        }
        return false;
    }

    public static List<Player> getPlayers() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getPlayers()
                .stream()
                .filter(x -> x != Microbot.getClient().getLocalPlayer())
                .collect(Collectors.toList()));
    }

    /**
     * Gets the players current world location
     *
     * @return worldpoint
     */
    public static WorldPoint getWorldLocation() {
        if (Microbot.getClient().isInInstancedRegion()) {
            LocalPoint l = LocalPoint.fromWorld(Microbot.getClient(), Microbot.getClient().getLocalPlayer().getWorldLocation());
            WorldPoint playerInstancedWorldLocation = WorldPoint.fromLocalInstance(Microbot.getClient(), l);
            return playerInstancedWorldLocation;
        } else {
            return Microbot.getClient().getLocalPlayer().getWorldLocation();
        }
    }

    /**
     * Checks if the player is near a worldpoint
     *
     * @return
     */
    public static boolean isNearArea(WorldPoint worldPoint, int distance) {
        WorldArea worldArea = new WorldArea(worldPoint, distance, distance);
        return worldArea.contains(getWorldLocation());
    }

    /**
     * Gets the player's local point (commonly used in instanced areas)
     *
     * @return localpoint
     */
    public static LocalPoint getLocalLocation() {
        return Microbot.getClient().getLocalPlayer().getLocalLocation();
    }

    /**
     * Checks if the player has full health
     *
     * @return
     */
    public static boolean isFullHealth() {
        return Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) >= Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
    }

    /**
     * Checks if the player is in multi-combat area
     *
     * @return
     */
    public static boolean isInMulti() {
        return Microbot.getVarbitValue(Varbits.MULTICOMBAT_AREA) == VarbitValues.INSIDE_MULTICOMBAT_ZONE.getValue();
    }

    /**
     * Drink prayer potion at prayer point level
     *
     * @param prayerPoints
     *
     * @return
     */
    public static boolean drinkPrayerPotionAt(int prayerPoints) {
        if (Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) <= prayerPoints) {
            return Rs2Inventory.interact("prayer potion", "drink");
        }
        return false;
    }

    /**
     * Checks if the player has prayer points remaining
     *
     * @return
     */
    public static boolean hasPrayerPoints() {
        return Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) > 0;
    }

    /**
     * Checks if the player is standing on a game object
     *
     * @return
     */
    public static boolean isStandingOnGameObject() {
        WorldPoint playerPoint = getWorldLocation();
        return Rs2GameObject.getGameObject(playerPoint) != null && Rs2GroundItem.getAllAt(getWorldLocation().getX(), getWorldLocation().getY()) != null;
    }

    /**
     * Checks if the player is standing on a ground item
     *
     * @return
     */
    public static boolean isStandingOnGroundItem() {
        WorldPoint playerPoint = getWorldLocation();
        return Arrays.stream(Rs2GroundItem.getAllAt(playerPoint.getX(), playerPoint.getY())).findAny().isPresent();
    }

    /**
     * Gets the player's current animation ID
     *
     * @return
     */
    public static int getAnimation() {
        if (Microbot.getClient().getLocalPlayer() == null) return -1;
        return Microbot.getClient().getLocalPlayer().getAnimation();
    }

    /**
     * Gets player's current pose animation ID
     *
     * @return
     */
    public static int getPoseAnimation() {
        return Microbot.getClient().getLocalPlayer().getPoseAnimation();
    }

    /**
     * Gets player's current QuestState for quest
     *
     * @param quest
     *
     * @return queststate
     */
    public static QuestState getQuestState(Quest quest) {
        Client client = Microbot.getClient();
        return Microbot.getClientThread().runOnClientThread(() -> quest.getState(client));
    }

    /**
     * Gets player's real level for skill
     *
     * @param skill
     *
     * @return level
     */
    public static int getRealSkillLevel(Skill skill) {
        return Microbot.getClient().getRealSkillLevel(skill);
    }

    /**
     * Gets player's boosted level for skill
     *
     * @param skill
     *
     * @return level
     */
    public static int getBoostedSkillLevel(Skill skill) {
        return Microbot.getClient().getBoostedSkillLevel(skill);
    }

    /**
     * Check if the player meets the level requirement for skill
     *
     * @param skill
     * @param levelRequired
     * @param isBoosted
     *
     * @return
     */
    public static boolean getSkillRequirement(Skill skill, int levelRequired, boolean isBoosted) {
        if (isBoosted) return getBoostedSkillLevel(skill) >= levelRequired;
        return getRealSkillLevel(skill) >= levelRequired;
    }

    /**
     * Check if the player meets the level requirement for skill
     *
     * @param skill
     * @param levelRequired
     *
     * @return
     */
    public static boolean getSkillRequirement(Skill skill, int levelRequired) {
        return getSkillRequirement(skill, levelRequired, false);
    }

    /**
     * Checks if the player is ironman or hardcore ironman
     *
     * @return
     */
    public static boolean isIronman() {
        int accountType = Microbot.getVarbitValue(Varbits.ACCOUNT_TYPE);
        return accountType > 0 && accountType <= 3;
    }

    /**
     * Check if the player is group ironman
     *
     * @return
     */
    public static boolean isGroupIronman() {
        int accountType = Microbot.getVarbitValue(Varbits.ACCOUNT_TYPE);
        return accountType >= 4;
    }

    /**
     * Gets the players current world
     *
     * @return world
     */
    public static int getWorld() {
        return Microbot.getClient().getWorld();
    }

    /**
     * Gets the distance from current player location to endpoint using ShortestPath (does not work in instanced regions)
     *
     * @param endpoint
     *
     * @return distance
     */
    public static int distanceTo(WorldPoint endpoint) {
        if (Microbot.getClient().isInInstancedRegion()) {
            return getWorldLocation().distanceTo(endpoint);
        }
        return Rs2Walker.getDistanceBetween(getWorldLocation(), endpoint);
    }

    /**
     * Checks whether a player is about to logout
     *
     * @return
     */
    public static boolean checkIdleLogout() {
        int idleClientTicks = Microbot.getClient().getKeyboardIdleTicks();

        return (long) idleClientTicks >= Random.randomDelay();
    }

    /**
     * Checks whether a player is about to logout
     *
     * @param randomDelay
     *
     * @return
     */
    public static boolean checkIdleLogout(long randomDelay) {
        int idleClientTicks = Microbot.getClient().getKeyboardIdleTicks();

        return (long) idleClientTicks >= randomDelay;
    }
}
