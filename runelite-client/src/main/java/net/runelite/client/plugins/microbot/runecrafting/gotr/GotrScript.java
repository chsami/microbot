package net.runelite.client.plugins.microbot.runecrafting.gotr;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.runecrafting.gotr.data.CellType;
import net.runelite.client.plugins.microbot.runecrafting.gotr.data.GuardianPortalInfo;
import net.runelite.client.plugins.microbot.runecrafting.gotr.data.Mode;
import net.runelite.client.plugins.microbot.runecrafting.gotr.data.RuneType;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.Microbot.log;
import static net.runelite.client.plugins.microbot.util.math.Random.randomGaussian;


public class GotrScript extends Script {

    public static String version = "1.1.0";
    public static long totalTime = 0;
    public static boolean shouldMineGuardianRemains = true;
    public static final String rewardPointRegex = "Total elemental energy:[^>]+>([\\d,]+).*Total catalytic energy:[^>]+>([\\d,]+).";
    public static final Pattern rewardPointPattern = Pattern.compile(rewardPointRegex);

    public static boolean isInMiniGame = false;
    public static final int portalId = ObjectID.PORTAL_43729;
    public static final int depositPoolId = 43696;
    public static final int elementalEssencePileId = 43722;
    public static final int catalyticEssencePileId = 43723;
    public static final int unchargedCellsTableId = 43732;
    public static final int greatGuardianId = 11403;
    public static final Map<Integer, GuardianPortalInfo> guardianPortalInfo = new HashMap<>();
    public static Optional<Instant> nextGameStart = Optional.empty();
    public static final Set<GameObject> guardians = new HashSet<>();
    public static final List<GameObject> activeGuardianPortals = new ArrayList<>();
    public static GameObject minePortal;
    public static GameObject depositPool;
    public static GameObject unchargedCellTable;
    public static GameObject catalyticEssencePile;
    public static GameObject elementalEssencePile;
    public static NPC greatGuardian;
    public static int elementalRewardPoints;
    public static int catalyticRewardPoints;
    public static GameObject rcAltar;
    public static GameObject rcPortal;
    public static GotrState state;
    static GotrConfig config;
    String GUARDIAN_FRAGMENTS = "guardian fragments";
    String GUARDIAN_ESSENCE = "guardian essence";

    boolean initCheck = false;

    private void initializeGuardianPortalInfo() {
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_AIR, new GuardianPortalInfo("AIR", 1, ItemID.AIR_RUNE, 26887, 4353, RuneType.ELEMENTAL, CellType.WEAK, QuestState.FINISHED));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_MIND, new GuardianPortalInfo("MIND", 2, ItemID.MIND_RUNE, 26891, 4354, RuneType.CATALYTIC, CellType.WEAK, QuestState.FINISHED));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_WATER, new GuardianPortalInfo("WATER", 5, ItemID.WATER_RUNE, 26888, 4355, RuneType.ELEMENTAL, CellType.MEDIUM, QuestState.FINISHED));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_EARTH, new GuardianPortalInfo("EARTH", 9, ItemID.EARTH_RUNE, 26889, 4356, RuneType.ELEMENTAL, CellType.STRONG, QuestState.FINISHED));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_FIRE, new GuardianPortalInfo("FIRE", 14, ItemID.FIRE_RUNE, 26890, 4357, RuneType.ELEMENTAL, CellType.OVERCHARGED, QuestState.FINISHED));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_BODY, new GuardianPortalInfo("BODY", 20, ItemID.BODY_RUNE, 26895, 4358, RuneType.CATALYTIC, CellType.WEAK, QuestState.FINISHED));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_COSMIC, new GuardianPortalInfo("COSMIC", 27, ItemID.COSMIC_RUNE, 26896, 4359, RuneType.CATALYTIC, CellType.MEDIUM, Microbot.getClientThread().runOnClientThread(() -> Quest.LOST_CITY.getState(Microbot.getClient()))));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_CHAOS, new GuardianPortalInfo("CHAOS", 35, ItemID.CHAOS_RUNE, 26892, 4360, RuneType.CATALYTIC, CellType.MEDIUM, QuestState.FINISHED));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_NATURE, new GuardianPortalInfo("NATURE", 44, ItemID.NATURE_RUNE, 26897, 4361, RuneType.CATALYTIC, CellType.STRONG, QuestState.FINISHED));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_LAW, new GuardianPortalInfo("LAW", 54, ItemID.LAW_RUNE, 26898, 4362, RuneType.CATALYTIC, CellType.STRONG, Microbot.getClientThread().runOnClientThread(() -> Quest.TROLL_STRONGHOLD.getState(Microbot.getClient()))));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_DEATH, new GuardianPortalInfo("DEATH", 65, ItemID.DEATH_RUNE, 26893, 4363, RuneType.CATALYTIC, CellType.OVERCHARGED, Microbot.getClientThread().runOnClientThread(() -> Quest.MOURNINGS_END_PART_II.getState(Microbot.getClient()))));
        guardianPortalInfo.put(ObjectID.GUARDIAN_OF_BLOOD, new GuardianPortalInfo("BLOOD", 77, ItemID.BLOOD_RUNE, 26894, 4364, RuneType.CATALYTIC, CellType.OVERCHARGED, Microbot.getClientThread().runOnClientThread(() -> Quest.SINS_OF_THE_FATHER.getState(Microbot.getClient()))));
    }

    public boolean run(GotrConfig config) {
        this.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                if (!initCheck) {
                    initializeGuardianPortalInfo();
                    initCheck = true;
                }

                Rs2Walker.setTarget(null);

                if (!Rs2Inventory.hasItem("pickaxe") && !Rs2Equipment.isWearing("pickaxe")) {
                    log("You need to have a pickaxe before you can participate in this minigame.");
                    return;
                }

                checkPouches(Rs2Inventory.anyPouchUnknown(), 1500, 300);

                //IS INSIDE THE MINIGAME
                int timeToStart = 0;
                if (nextGameStart.isPresent()) {
                    timeToStart = ((int) ChronoUnit.SECONDS.between(Instant.now(), nextGameStart.get()));
                }

                if (Rs2Inventory.hasItem("portal talisman")) {
                    Rs2Inventory.drop("portal talisman");
                    log("Dropping portal talisman...");
                }

                boolean isInMinigame = !isOutsideBarrier() && isInMainRegion();


                if (isInMinigame) {

                    if (lootChisel()) return;

                    if (waitingForGameToStart(timeToStart)) return;

                    if (powerUpGreatGuardian()) return;
                    if (repairCells()) return;

                    if (usePortal()) return;
                    //mine huge guardian remains
                    if (mineHugeGuardianRemain()) return;
                    //deposit runes
                    if (depositRunesIntoPool()) return;

                    if (!shouldMineGuardianRemains) {
                        //Create fragments into whatever
                        if (isOutOfFragments()) return;

                        if (fillPouches()) {
                            craftGuardianEssences();
                            return;
                        }
                        if (!Rs2Inventory.isFull()) {
                            if (leaveLargeMine()) return;

                            if (state == GotrState.CRAFT_GUARDIAN_ESSENCE && (Rs2Player.isAnimating() || Rs2Player.isWalking())) return;

                            if (craftGuardianEssences()) return;

                        } else if (Rs2Inventory.hasItem(GUARDIAN_ESSENCE)) {
                            if (leaveLargeMine()) return;
                            if (enterAltar()) return;
                        }
                    } else {
                        if (getGuardiansPower() > 70) {
                            if (Rs2Inventory.hasItemAmount(GUARDIAN_FRAGMENTS, Random.random(25, 35))) {
                                shouldMineGuardianRemains = false;
                            }
                        } else {
                            if (Rs2Inventory.hasItemAmount(GUARDIAN_FRAGMENTS, config.maxFragmentAmount())) {
                                shouldMineGuardianRemains = false;
                            }
                        }
                        mineGuardianRemains();
                    }
                    return;
                }


                //IS NOT IN THE MINIGAME

                if (craftRunes()) return;

                if (enterMinigame()) return;

                if (waitForMinigameToStart()) return;


                long endTime = System.currentTimeMillis();
                totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean waitingForGameToStart(int timeToStart) {
        if (getStartTimer() > randomGaussian(Random.random(20, 30), Random.random(1, 10)) || getStartTimer() == -1 || timeToStart > 10) {

            takeUnchargedCells();
            repairPouches();

            if (!shouldMineGuardianRemains) return true;

            mineGuardianRemains();
            return true;
        }
        return false;
    }

    private boolean repairCells() {
        Rs2Item cell = Rs2Inventory.get(CellType.PoweredCellList().toArray(Integer[]::new));
        if (cell != null && isInMainRegion() && isInMiniGame() && !shouldMineGuardianRemains && !isInLargeMine() && !isInHugeMine()) {
            int cellTier = CellType.GetCellTier(cell.getId());
            List<Integer> shieldCellIds = Rs2GameObject.getObjectIdsByName("cell_tile");

            if (Rs2Inventory.hasItemAmount(GUARDIAN_ESSENCE, 10)) {
                for (int shieldCellId : shieldCellIds) {
                    TileObject shieldCell = Rs2GameObject.getTileObject(shieldCellId);
                    if (shieldCell == null) continue;
                    if (CellType.GetShieldTier(shieldCell.getId()) < cellTier) {
                        Microbot.log("Upgrading power cell at " + shieldCell.getWorldLocation());
                        Rs2GameObject.interact(shieldCell, "Place-cell");
                        sleepUntil(() -> !Rs2Player.isWalking());
                        return true;
                    }
                }
            }
            shieldCellIds = shieldCellIds.stream().filter(id -> id != ObjectID.CELL_TILE_BROKEN).collect(Collectors.toList());
            int interactedObjectId = Rs2GameObject.interact(shieldCellIds);
            if (interactedObjectId != -1) {
                log("Using cell with id " + interactedObjectId);
                sleepUntil(() -> !Rs2Player.isWalking());
            }
            return true;
        }
        return false;
    }

    private boolean powerUpGreatGuardian() {
        if (Rs2Inventory.hasItem("guardian stone") && !shouldMineGuardianRemains && !isInLargeMine() && !isInHugeMine()) {
            state = GotrState.POWERING_UP;
            Rs2Npc.interact("The great guardian", "power-up");
            log("Powering up the great guardian...");
            sleepUntil(Rs2Player::isAnimating);
            sleep(randomGaussian(Random.random(1000, 2000), Random.random(100, 300)));
            return true;
        }
        return false;
    }

    private static void takeUnchargedCells() {
        if (!Rs2Inventory.hasItem("Uncharged cell")) {
            Rs2GameObject.interact(ObjectID.UNCHARGED_CELLS_43732, "Take-10");
            log("Taking uncharged cells...");
            Rs2Player.waitForAnimation();
        }
    }

    private static boolean lootChisel() {
        if (!Rs2Inventory.hasItem("Chisel")) {
            Rs2GameObject.interact("chisel", "take");
            Rs2Player.waitForWalking();
            log("Looking for chisel...");
            return true;
        }
        return false;
    }

    private boolean usePortal() {
        if (Microbot.getClient().hasHintArrow() && Rs2Inventory.size() < config.maxAmountEssence()) {
            if (leaveLargeMine()) return true;
            if (!isInLargeMine()) {
                Rs2Walker.walkFastCanvas(Microbot.getClient().getHintArrowPoint());
                Rs2GameObject.interact(Microbot.getClient().getHintArrowPoint());
                log("Found a portal spawn...interacting with it...");
                Rs2Player.waitForWalking();
                sleep(randomGaussian(Random.random(1000, 2000), Random.random(100, 300)));
            }
            return true;
        }
        return false;
    }

    private boolean depositRunesIntoPool() {
        if (Rs2Inventory.hasItem("rune pouch")) {
            if (Rs2Inventory.hasItemAmount("rune", 2) && !isInLargeMine() && !isInHugeMine() && !Rs2Inventory.isFull()) {
                if (Rs2Player.isWalking()) return true;
                if (Rs2GameObject.interact(ObjectID.DEPOSIT_POOL)) {
                    log("Deposit runes into pool...");
                    sleep(600, 2400);
                }
                return true;
            }
        } else {
            if (Rs2Inventory.hasItem("rune") && !isInLargeMine() && !isInHugeMine() && !Rs2Inventory.isFull()) {
                if (Rs2Player.isWalking()) return true;
                if (Rs2GameObject.interact(ObjectID.DEPOSIT_POOL)) {
                    log("Deposit runes into pool...");
                    sleep(600, 2400);
                }
                return true;
            }
        }
        return false;
    }

    private boolean enterAltar() {
        GameObject availableAltar = getAvailableAltars().stream().findFirst().orElse(null);
        if (availableAltar != null && !Rs2Player.isWalking()) {
            log("Entering with altar " + availableAltar.getId());
            Rs2GameObject.interact(availableAltar);
            sleepUntil(() -> !isInMainRegion(), 5000);
            sleep(Random.randomGaussian(1000, 300));
            state = GotrState.ENTER_ALTAR;
            return true;
        }
        return false;
    }

    private boolean craftGuardianEssences() {
        if (Rs2GameObject.interact(ObjectID.WORKBENCH_43754)) {
            state = GotrState.CRAFT_GUARDIAN_ESSENCE;
            sleep(Random.randomGaussian(Random.random(600, 900), Random.random(150, 300)));
            log("Crafting guardian essences...");
            return true;
        }
       return false;
    }

    private boolean leaveLargeMine() {
        if (isInLargeMine()) {
            Rs2GameObject.interact(ObjectID.RUBBLE_43726);
            Rs2Player.waitForAnimation();
            log("Leaving large mine...");
            state = GotrState.LEAVING_LARGE_MINE;
            return true;
        }
        return false;
    }

    private boolean fillPouches() {
        if (Rs2Inventory.isFull() && Rs2Inventory.anyPouchEmpty() && getGuardiansPower() < 90) {
            Rs2Inventory.fillPouches();
            sleep(Random.randomGaussian(Random.random(600, 900), Random.random(150, 300)));
            return true;
        }
        return false;
    }

    private boolean isOutOfFragments() {
        if (!Rs2Inventory.hasItem(GUARDIAN_FRAGMENTS) && !Rs2Inventory.isFull()) {
            shouldMineGuardianRemains = true;
            log("Memorize that we no longer have guardian fragments...");
            return true;
        }
        return false;
    }

    private boolean craftRunes() {
        if (isInMiniGame() && !isInMainRegion()) {
            if (rcAltar != null) {
                if (Rs2Player.isWalking()) return true;
                if (Rs2Inventory.anyPouchFull() && !Rs2Inventory.isFull()) {
                    Rs2Inventory.emptyPouches();
                    sleep(Random.randomGaussian(600, 150));
                }
                if (Rs2Inventory.hasItem(GUARDIAN_ESSENCE)) {
                    state = GotrState.CRAFTING_RUNES;
                    Rs2GameObject.interact(rcAltar.getId());
                    log("Crafting runes...");
                    sleep(Random.randomGaussian(Random.random(1000, 1500), 300));
                } else if (rcPortal != null && Rs2GameObject.interact(rcPortal.getId()) && !Rs2Player.isWalking()) {
                    state = GotrState.LEAVING_ALTAR;
                    Rs2GameObject.interact(rcPortal.getId());
                    log("Leaving the altar...");
                    sleepUntil(GotrScript::isInMainRegion, 5000);
                }
            }
            return true;
        }
        return false;
    }

    private static boolean waitForMinigameToStart() {
        if (shouldMineGuardianRemains) {
            if (rcPortal != null && Rs2GameObject.interact(rcPortal.getId())) {
                state = GotrState.LEAVING_ALTAR;
                return true;
            }
            resetPlugin();
            if (state != GotrState.WAITING) {
                state = GotrState.WAITING;
                log("Make sure to start the script near the minigame barrier.");
                Rs2GameObject.interact(ObjectID.BARRIER_43849, "Peek");
            }
        }
        return false;
    }

    private static boolean enterMinigame() {
        if (Rs2GameObject.interact(ObjectID.BARRIER_43700, "quick-pass")) {
            Rs2Player.waitForWalking();
            state = GotrState.ENTER_GAME;
            GotrScript.shouldMineGuardianRemains = true;
            log("Entering game...");
            return true;
        }
        return false;
    }

    private void checkPouches(boolean anyPouchUnknown, int mean, int stddev) {
        if (anyPouchUnknown) {
            Rs2Inventory.checkPouches();
            sleep(randomGaussian(mean, stddev));
        }
    }

    private boolean mineHugeGuardianRemain() {
        if (isInHugeMine()) {
            if (getStartTimer() == -1) {
                repairPouches();
                return false;
            }
            if (!Rs2Inventory.isFull()) {
                if (!Rs2Player.isAnimating()) {
                    Rs2GameObject.interact(ObjectID.HUGE_GUARDIAN_REMAINS);
                    Rs2Player.waitForAnimation();
                    Rs2GameObject.interact(ObjectID.HUGE_GUARDIAN_REMAINS);
                }
            } else {
                if (Rs2Inventory.allPouchesFull()) {
                    Rs2GameObject.interact(38044);
                    Rs2Player.waitForWalking();
                } else {
                    Rs2Inventory.fillPouches();
                    sleep(randomGaussian(Random.random(1000, 1500), Random.random(100, 300)));
                    Rs2GameObject.interact(ObjectID.HUGE_GUARDIAN_REMAINS);
                }
            }
            return true;
        }
        return false;
    }

    private void mineGuardianRemains() {
        if (Rs2Inventory.isFull()) {
            shouldMineGuardianRemains = false;
            return;
        }
        state = GotrState.MINE_LARGE_GUARDIAN_REMAINS;
        if (isInHugeMine()) {
            Rs2GameObject.interact(38044);
            Rs2Player.waitForWalking();
            log("Leave huge mine...");
            return;
        }
        if (Rs2Player.getSkillRequirement(Skill.AGILITY, 56)) {
            if (!isInLargeMine() && (!Rs2Inventory.hasItem(GUARDIAN_FRAGMENTS) || getStartTimer() == -1)) {
                if (Rs2Walker.walkTo(new WorldPoint(3632, 9503, 0), 20)) {
                    log("Traveling to large mine...");
                    Rs2GameObject.interact(ObjectID.RUBBLE_43724);
                    if (sleepUntil(Rs2Player::isAnimating)) {
                        sleepUntil(this::isInLargeMine);
                        if (isInLargeMine()) {
                            sleep(randomGaussian(Random.random(2500, 3000), Random.random(100, 300)));
                            log("Interacting with large guardian remains...");
                            Rs2GameObject.interact(ObjectID.LARGE_GUARDIAN_REMAINS);
                        }
                    }
                }
            } else {
                if (!Rs2Player.isAnimating() && getStartTimer() != -1) {
                    if (Rs2Equipment.isWearing("dragon pickaxe")) {
                        Rs2Combat.setSpecState(true, 1000);
                    }
                    checkPouches(Random.random(1, 20) == 2, Random.random(100, 600), Random.random(100, 300));

                    repairPouches();
                    Rs2GameObject.interact(ObjectID.LARGE_GUARDIAN_REMAINS);
                    // we can assume that if the player is mining within the startTimer range, he will get enough guardian remains for the game
                    shouldMineGuardianRemains = false;
                }
            }
        } else {
            //guardian parts
            if (!Rs2Player.isAnimating() && getStartTimer() != -1) {
                if (Rs2Equipment.isWearing("dragon pickaxe")) {
                    Rs2Combat.setSpecState(true, 1000);
                }
                repairPouches();
                Rs2GameObject.interact(ObjectID.GUARDIAN_PARTS_43716);
                // we can assume that if the player is mining within the startTimer range, he will get enough guardian remains for the game
                shouldMineGuardianRemains = false;
            }
        }
    }

    private static void repairPouches() {
        if (Rs2Inventory.hasDegradedPouch()) {
            Rs2Magic.repairPouchesWithLunar();
        }
    }

    @Override
    public void shutdown() {
        state = null;
        super.shutdown();
    }

    public boolean isOutsideBarrier() {
        int outsideBarrierY = 9482;
        return Rs2Player.getWorldLocation().getY() <= outsideBarrierY
                && Rs2Player.getWorldLocation().getRegionID() == 14484;
    }

    public boolean isInLargeMine() {
        int largeMineX = 3637;
        return Rs2Player.getWorldLocation().getRegionID() == 14484
                && Microbot.getClient().getLocalPlayer().getWorldLocation().getX() >= largeMineX;
    }

    public boolean isInHugeMine() {
        int hugeMineX = 3593;
        return Rs2Player.getWorldLocation().getRegionID() == 14484
                && Microbot.getClient().getLocalPlayer().getWorldLocation().getX() <= hugeMineX;
    }

    public static boolean isGuardianPortal(GameObject gameObject) {
        return guardianPortalInfo.containsKey(gameObject.getId());
    }

    public ItemManager getItemManager() {
        return Microbot.getItemManager();
    }

    public boolean isInMiniGame() {
        int parentWidgetId = 48889857;
        Widget elementalRuneWidget = Microbot.getClient().getWidget(parentWidgetId);
        return elementalRuneWidget != null;
    }

    public static boolean isInMainRegion() {
        return Rs2Player.getWorldLocation().getRegionID() == 14484;
    }

    public static int getStartTimer() {
        Widget timerWidget = Rs2Widget.getWidget(48889861);
        if (timerWidget != null) {
            String timer = Rs2Widget.getWidget(48889861).getText();
            if (timer == null) return -1;
            // Split the timer string into minutes and seconds
            String[] timeParts = timer.split(":");

            // Ensure there are two parts (minutes and seconds)
            if (timeParts.length == 2) {
                int minutes = Integer.parseInt(timeParts[0]);
                int seconds = Integer.parseInt(timeParts[1]);

                // Convert the timer to total seconds
                int totalSeconds = (minutes * 60) + seconds;
                return totalSeconds;
            }
        }
        return -1;
    }

    public static List<GameObject> getAvailableAltars() {
        int elementalPoints = Microbot.getVarbitValue(13686);
        int catalyticPoints = Microbot.getVarbitValue(13685);
        if (config.Mode() == Mode.BALANCED) {
            Microbot.log(elementalPoints < catalyticPoints ? "We have " + elementalPoints + " elemental points, looking for elemental altar..." : "We have " + catalyticPoints +" catalytic points, looking for catalytic altar...");
        }
        return Rs2GameObject.getGameObjects().stream()
                .filter(x -> {

                    if (!guardianPortalInfo.containsKey(x.getId())) return false;
                    if (GotrScript.guardianPortalInfo.get(x.getId()).getRequiredLevel()
                            > Microbot.getClient().getBoostedSkillLevel(Skill.RUNECRAFT)) {
                        return false;
                    }
                    if (GotrScript.guardianPortalInfo.get(x.getId()).getQuestState() != QuestState.FINISHED) {
                        return false;
                    }

                    if (((DynamicObject) x.getRenderable()).getAnimation() == null) {
                        return false;
                    }
                    if (((DynamicObject) x.getRenderable()).getAnimation().getId() != 9363) {
                        return false;
                    }

                    return true;

                })
                .sorted((config.Mode() == Mode.BALANCED && elementalPoints < catalyticPoints) || config.Mode() == Mode.ELEMENTAL ? Comparator.comparingInt(TileObject::getId) : Comparator.comparingInt(TileObject::getId).reversed())
                .collect(Collectors.toList());
    }

    private int getGuardiansPower() {
        Widget pWidget = Rs2Widget.getWidget(48889874);
        if (pWidget == null) {
            return 0;
        }

        Matcher matcher = Pattern.compile("(\\d+)%").matcher(pWidget.getText());

        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }

    public static void resetPlugin() {
        guardians.clear();
        activeGuardianPortals.clear();
        unchargedCellTable = null;
        greatGuardian = null;
        catalyticEssencePile = null;
        elementalEssencePile = null;
        depositPool = null;
        minePortal = null;
        rcPortal = null;
        rcAltar = null;
        Microbot.getClient().clearHintArrow();
    }
}
