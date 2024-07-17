package net.runelite.client.plugins.ogPlugins.ogConstruction;

import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.Config;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.ogPlugins.ogConstruction.enums.Butler;
import net.runelite.client.plugins.ogPlugins.ogConstruction.enums.Furniture;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject.getObjectComposition;
import static net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject.getTileObjects;
import static net.runelite.client.plugins.microbot.util.walker.Rs2Walker.walkTo;

public class ogConstScript extends Script {

    public static double version = 1.0;

    private enum State {
        ENABLE_BUILDING_MODE,
        LOGOUT,
        FILL_MONEY_BAG,
        DESTROY,
        SEND_BUTLER,
        BUILDING,
        WALK_TO_PORTAL
    }

    private State status;
    private int gameTickLastSentButler = -12;
    private int currentGameTick;
    private int coinsLeftInMoneyBag = 0;
    private int lastActionTick = 0;

    // Settings
    private Config currentconfig;
    public int delayMin;
    public int delayMax;
    public int delayChance;
    public int afkMin;
    public int afkMax;
    public int afkChance;
    private Furniture furniture;
    private Butler butler;
    private boolean useServentsBag;
    private int moneyBagRefillThreshold;
    private boolean logging;

    // Declare the moneyBag variable
    private GameObject moneyBag;

    private void callDelay() {
        if (Random.random(1, this.delayChance) == 3) {
            int delayGeneratedMin = Random.random(this.delayMin - 20, this.delayMin);
            int delayGeneratedMax = Random.random(this.delayMax, this.delayMax + 20);
            int delayGenerated = Random.random(delayGeneratedMin, delayGeneratedMax);
            sleep(delayGenerated);
        } else {
            int delayGenerated = Random.random(delayMin, delayMax);
            sleep(delayGenerated);
        }
    }

    private void callAFK() {
        if (Random.random(0, this.afkChance) == 3) {
            int afkGenerated = Random.random(this.afkMin * 600, this.afkMax * 600);
            sleep(afkGenerated);
        }
    }

    private boolean moneyBagTopUpNeeded() {
        return (moneyBagRefillThreshold + Random.random(-50000, 200000)) >= coinsLeftInMoneyBag;
    }

    private boolean checkFurnitureBuilt() {
        return Rs2GameObject.exists(furniture.getBuiltID());
    }

    private boolean checkIfButlerHere() {
        return Rs2Npc.getNpc(butler.getButlerID()) != null;
    }

    private boolean checkButlerNearPlayer() {
        if (!checkIfButlerHere()) {
            return false;
        }
        return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(Rs2Npc.getNpc(butler.getButlerID()).getWorldLocation()) < 3;
    }

    public boolean run(ogConstConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                calcState();
                if (status == State.BUILDING) {
                    build();
                } else if (status == State.ENABLE_BUILDING_MODE) {
                    enableBuildingMode();
                } else if (status == State.DESTROY) {
                    destroy();
                } else if (status == State.SEND_BUTLER) {
                    sendButler();
                } else if (status == State.FILL_MONEY_BAG) {
                    fillMoneyBag();
                } else if (status == State.LOGOUT) {
                    Rs2Tab.switchToLogout();
                    Rs2Widget.clickWidget("Click here to logout");
                    super.shutdown();
                }
            } catch (Exception ex) {
            }
        }, 0, Random.random(0, 100), TimeUnit.MILLISECONDS);
        return true;
    }


    private void build() {
        Rs2GameObject.interact(furniture.getUnBuiltID(), "Build");
        sleepUntil(() -> Rs2Widget.getWidget(30015493) != null, Random.random(1000, 2000));
        if (Rs2Widget.getWidget(30015493) != null) {
            callDelay();
            Rs2Keyboard.typeString(String.valueOf(furniture.getBuildOption()));
            callDelay();
        }
        sleepUntil(this::checkFurnitureBuilt, Random.random(800, 900));
    }

    private void destroy() {
        if (furniture.getBuiltID() == ObjectID.DOOR_13344 &&
                !Rs2Inventory.hasItemAmount(furniture.getPlankNeeded(), furniture.getPlankAmountNeeded()) &&
                !checkIfButlerHere() &&
                !(currentGameTick > gameTickLastSentButler + (butler.getTicksNeededToBank() + 2))) {
            sleepUntil(this::checkButlerNearPlayer);
            sleepUntil(() -> Rs2Widget.findWidget("Master, I have returned with what you asked me to") != null);
        }
        Rs2GameObject.interact(furniture.getBuiltID(), "Remove");
        sleepUntil(() -> Rs2Widget.findWidget("Really remove it?") != null, Random.random(1000, 1500));
        if (Rs2Widget.findWidget("Really remove it?") != null) {
            if (Random.random(1, 29) == 3) {
                callDelay();
                Rs2Widget.clickWidget("Yes");
            } else {
                callDelay();
                Rs2Keyboard.typeString("1");
            }
        }
        sleepUntil(() -> !checkFurnitureBuilt(), Random.random(600, 700));
    }

    private void fillMoneyBag() {
        if (Rs2GameObject.findObjectById(ObjectID.SERVANTS_MONEY_BAG) != null) {
            Rs2GameObject.interact(Rs2GameObject.findObjectById(ObjectID.SERVANTS_MONEY_BAG), "Use");
            sleepUntil(() -> Rs2Widget.findWidget("Continue") != null, Random.random(2000, 9000));

            if (Rs2Widget.findWidget("Click here to continue") != null) {
                Rs2Widget.clickWidget("Click here to continue");
            }

            sleepUntil(() -> Rs2Widget.findWidget("Deposit coins") != null, Random.random(2000, 9000));
            if (Rs2Widget.findWidget("Deposit coins") != null) {
                Rs2Widget.clickWidget("Deposit coins");
            }

            sleepUntil(() -> Rs2Widget.findWidget("How many coins do you wish to deposit?") != null, Random.random(600, 700));
            if (Rs2Widget.findWidget("How many coins do you wish to deposit?") != null) {
            }

            Rs2Keyboard.keyPress(KeyEvent.VK_ENTER);
            sleepUntil(() -> Rs2Widget.findWidget("The moneybag ") != null, Random.random(2000, 9000));
            this.coinsLeftInMoneyBag = extractNumber((Rs2Widget.findWidget("The moneybag ")).getText());
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
        }
    }

    public static boolean checkAndWalkToPortal() {
        // Check if we're near a Portal
        TileObject portal = findObjectByName("Portal");
        if (portal != null && Rs2Player.getWorldLocation().distanceTo(portal.getWorldLocation()) <= 3) {
            return true; // We're already near a Portal
        }

        // If not near a Portal, walk to the specified tile
        WorldPoint targetTile = new WorldPoint(2953, 3224, 0);
        return walkTo(targetTile);
    }

    public static TileObject findObjectByName(String name) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            List<TileObject> tileObjects = getTileObjects();
            for (TileObject tileObject : tileObjects) {
                ObjectComposition objectComposition = getObjectComposition(tileObject.getId());
                if (objectComposition != null && objectComposition.getName().equalsIgnoreCase(name)) {
                    return tileObject;
                }
            }
            return null;
        });
    }

    private void sendButler() {
        if (Rs2Widget.findWidget("Master, I have returned with") != null) {
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
            return;
        }
        if (!checkIfButlerHere() ||
                !checkButlerNearPlayer() || (Rs2Widget.getChildWidgetText(10616888, 0)).contains("I can't reach that!")) {
            navigateToHouseSettings();
            if (Rs2Widget.getWidget(24248342) != null) {
                callDelay();
                Rs2Widget.clickWidget(24248342);
                callDelay();
                Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
            }
            if (Rs2Widget.findWidget("Master, I have returned with") != null) {
                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                return;
            }
            sleepUntil(this::checkButlerNearPlayer, Random.random(1000, 2000));
        } else if (checkButlerNearPlayer()) {
            Rs2Npc.interact(butler.getName(), "Talk-to");
        }
        if (Rs2Widget.findWidget("The moneybag does not contain enough.") != null) {
            this.coinsLeftInMoneyBag = 0;
            if (Random.random(1, 10) == 3) {
                Rs2Widget.clickWidget("Click here to continue");
            } else {
                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
            }
            sleepUntil(() -> Rs2Widget.findWidget("Select an option") != null);
            Rs2Keyboard.typeString("1");
        }
        sleepUntil(() -> Rs2Widget.findWidget("Repeat last task?") != null || (Rs2Widget.getChildWidgetText(10616888, 0)).contains("I can't reach that!"), Random.random(800, 900));
        if (Rs2Widget.findWidget("Repeat last task?") != null) {
            callDelay();
            Rs2Keyboard.typeString("1");
            callDelay();
            sleepUntil(() -> !checkIfButlerHere(), Random.random(600, 700));
            gameTickLastSentButler = currentGameTick;
        }
        Rs2Tab.switchToInventoryTab();
        sleepUntil(() -> Rs2Inventory.hasItemAmount(furniture.getPlankNeeded(), furniture.getPlankAmountNeeded()) || checkButlerNearPlayer(), Random.random(10000, 13000));
    }

    //TODO Update butlers fetch to correct planks if needed - more regex
    private void updateButlerAction() {
    }

    private void enterPlayerHouse() {
        if (Rs2GameObject.interact("Portal", "Build mode")) {
            sleepUntil(this::isInPlayerHouse, Random.random(5000, 10000));
        }
    }

    private boolean isInPlayerHouse() {
        return Rs2GameObject.findObjectById(4525) != null;
    }

    private void enableBuildingMode() {
        if (!isInPlayerHouse()) {
            enterPlayerHouse();
        } else {
            navigateToHouseSettings();
            if (Rs2Widget.getWidget(24248325) != null) {
                callDelay();
                Rs2Widget.clickWidget(24248325);
                sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(6719)) == 2, Random.random(5000, 10000));
                sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(6719)) == 0, Random.random(5000, 10000));
            }
        }
    }

    private void navigateToHouseSettings() {
        Rs2Tab.switchToSettingsTab();
        if (Rs2Widget.getWidget(7602207) == null) {
            sleep(30, 40);
            Rs2Widget.clickWidget(7602241);
            sleepUntil(() -> Rs2Widget.getWidget(7602207) != null);
        }
        if (Rs2Widget.getWidget(24248342) == null) {
            sleep(30, 40);
            Rs2Widget.clickWidget(7602207);
            sleepUntil(() -> Rs2Widget.getWidget(24248342) != null);
        }
    }

    public void onGameObjectSpawned(GameObjectSpawned event) {
        try {
            if (event.getGameObject().getId() == ObjectID.SERVANTS_MONEY_BAG) {
                moneyBag = event.getGameObject();
            }
        } catch (Exception e) {
        }
    }

    public void onGameTick(GameTick gameTick) {
        this.currentGameTick++;
        if ((Rs2Widget.getChildWidgetText(10616888, 0)).contains("Your servant takes some payment from the moneybag")) {
            this.coinsLeftInMoneyBag = extractNumber(((Rs2Widget.getChildWidgetText(10616888, 0))));
        }
    }

    private int extractNumber(String input) {
        Pattern pattern = Pattern.compile("([0-9].{0,9}?(?= coins)|empty|full)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String matchedNumber = matcher.group();
            if (matchedNumber.equals("empty")) {
                return 0;
            }
            if (matchedNumber.equals("full")) {
                return 3000000;
            }
            String extractedNumber = matchedNumber.replaceAll(",", "");
            int numberValue = Integer.parseInt(extractedNumber);
            return numberValue;
        } else {
            return 0;
        }
    }

    private void calcState() {
        if (!checkAndWalkToPortal()) {
            status = State.WALK_TO_PORTAL;
        } else if (Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(2176)) == 0) {
            status = State.ENABLE_BUILDING_MODE;
        } else if (!(Rs2Inventory.hasItemAmount(furniture.getNotedPlankNameNeeded(), furniture.getPlankAmountNeeded(), true) || Rs2Inventory.hasItemAmount("Coins", 10000, true))) {
            status = State.LOGOUT;
        } else if (moneyBagTopUpNeeded()) {
            status = State.FILL_MONEY_BAG;
        } else if (Rs2GameObject.findObjectByIdAndDistance(furniture.getBuiltID(), 20) != null) {
            status = State.DESTROY;
        } else if (!Rs2Inventory.hasItemAmount(furniture.getPlankNeeded(), (furniture.getPlankAmountNeeded() * 2) + 1) && (currentGameTick > gameTickLastSentButler + (butler.getTicksNeededToBank() + 2))) {
            status = State.SEND_BUTLER;
        } else if (Rs2GameObject.findObjectByIdAndDistance(furniture.getUnBuiltID(), 20) != null && Rs2Inventory.hasItemAmount(furniture.getPlankNeeded(), furniture.getPlankAmountNeeded())) {
            status = State.BUILDING;
        }
    }
}
