package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.storm.common.Rs2Storm;
import net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums.*;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class actionHotkeyScript extends Script {
    public static double version = 1.1;
    public static int previousKey;
    public static boolean key1isdown;
    public static boolean key2isdown;
    boolean alternating;
    boolean toggled;
    private int minInterval;
    private int previousAction;
    private int randomMin;
    private int randomMax;
    private actionHotkeyConfig config;
    public boolean run(actionHotkeyConfig config){
        randomMin = config.sleepMin();
        if(config.sleepMax()>config.sleepMin()+60){
            randomMax=config.sleepMax();
        } else {
            randomMax=config.sleepMax()+random(60-(config.sleepMax()-config.sleepMin()),91);
        }
        alternating = false;
        toggled = false;
        previousAction = 0;
        minInterval = 0;
        previousKey = 0;
        key1isdown = false;
        key2isdown = false;
        this.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if(randomMin!=config.sleepMin()){
                randomMin = config.sleepMin();
                if(config.sleepMax()>config.sleepMin()+60){
                    randomMax=config.sleepMax();
                } else {
                    randomMax=config.sleepMax()+random(60-(config.sleepMax()-config.sleepMin()),91);
                }
            }
            if (key1isdown || (config.toggle() && toggled && previousKey == config.key1().getKeyCode())) {
                while ((toggled || key1isdown) && this.isRunning()) {
                    if (!alternating) {
                        System.out.println("Should be doing first thing");
                        firstHotKey();
                        if (config.alternate()) { alternating = !alternating; }
                        sleep(randomMin, randomMax);
                    }
                    if (alternating && key1isdown || toggled) {
                        System.out.println("Should be doing second thing");
                        secondHotKey();
                        if (config.alternate()) { alternating = !alternating; }
                        sleep(randomMin, randomMax);
                    }
                }
            } else if (key2isdown || config.toggle() && toggled && previousKey == config.key2().getKeyCode()) {
                while ((toggled || key2isdown) && this.isRunning()) {
                    if (!alternating) {
                        System.out.println("Should be doing second thing");
                        secondHotKey();
                        if (config.alternate()) { alternating = !alternating; }
                        sleep(randomMin, randomMax);
                    }

                    if (alternating && key2isdown || toggled) {
                        System.out.println("Should be doing first thing");
                        firstHotKey();
                        if (config.alternate()) { alternating = !alternating; }
                        sleep(randomMin, randomMax);
                    }
                }
            }
            else if(!config.doAction()) {
                isRunningDemo();
            }
        }, 0, 30, TimeUnit.MILLISECONDS);
        return true;
    }

    public void isRunningDemo(){
        sleepUntil(this::isRunning,2000);
        if(this.isRunning()) { System.out.println("1"); }
        sleep(1000);
        if(this.isRunning()) { System.out.println("2"); }
        sleep(1000);
        if(this.isRunning()) { System.out.println("3"); }
        sleep(1000);
        if(this.isRunning()) { System.out.println("4"); }
        sleep(1000);
        if(this.isRunning()) { System.out.println("5"); }
        sleep(1000);
        if(this.isRunning()) { System.out.println("6"); }
        sleep(1000);
        if(this.isRunning()) { System.out.println("7"); }
        sleep(1000);
        if(this.isRunning()) { System.out.println("8"); }
        sleep(1000);
        if(this.isRunning()) { System.out.println("9"); }
        sleep(1000);
        if(this.isRunning()) { System.out.println("you should see this if the script is running"); }
    }
    public void firstHotKey() {
        if (conditional(config.conditionsForTwo())) {
            switch (config.firstCategoryName()) {
                case RS2NPC:
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                            action(config.firstRs2Npc(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                        } else if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Npc()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Npc(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                        }
                    } else if (config.firstActionMenu().isEmpty()) {
                        if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Npc()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Npc(), config.firstActionIDEntry());
                        }
                    } else {
                        action(config.firstRs2Walker(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                    break;

                case RS2PLAYER:
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                            action(config.firstRs2Player(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                        } else if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Player()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Player(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                        }
                    } else if (config.firstActionMenu().isEmpty()) {
                        if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Player()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Player(), config.firstActionIDEntry());
                        }
                    } else {
                        action(config.firstRs2Player(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                    break;
                case RS2INVENTORY:
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                            action(config.firstRs2Inventory(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                        } else if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Inventory()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Inventory(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                        }
                    } else if (config.firstActionMenu().isEmpty()) {
                        if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Inventory()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Inventory(), config.firstActionIDEntry());
                        }
                    } else {
                        action(config.firstRs2Inventory(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                    break;

                case RS2WALKER:
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                            action(config.firstRs2Walker(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                        } else if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Walker()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Walker(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                        }
                    } else if (config.firstActionMenu().isEmpty()) {
                        if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Walker()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Walker(), config.firstActionIDEntry());
                        }
                    } else {
                        action(config.firstRs2Walker(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                    break;

                case RS2GAMEOBJECT:
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                            action(config.firstRs2GameObject(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                        } else if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2GameObject()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2GameObject(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                        }
                    } else if (config.firstActionMenu().isEmpty()) {
                        if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2GameObject()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2GameObject(), config.firstActionIDEntry());
                        }
                    } else {
                        action(config.firstRs2GameObject(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                    break;

                case RS2WIDGET:
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                            action(config.firstRs2Widget(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                        } else if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Widget()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Widget(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                        }
                    } else if (config.firstActionMenu().isEmpty()) {
                        if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Widget()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Widget(), config.firstActionIDEntry());
                        }
                    } else {
                        action(config.firstRs2Widget(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                    break;

                case RS2BANK:
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                            action(config.firstRs2Bank(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                        } else if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Bank()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Bank(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                        }
                    } else if (config.firstActionMenu().isEmpty()) {
                        if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstRs2Bank()); // Call action with only the first parameter
                        } else {
                            action(config.firstRs2Bank(), config.firstActionIDEntry());
                        }
                    } else {
                        action(config.firstRs2Bank(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                    break;

                case RS2MAGIC:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.firstRs2Magic(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.firstRs2Magic()); // Call action with only the second parameter
                        } else {
                            action(config.firstRs2Magic(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.firstRs2Magic()); // Call action with only the second parameter
                        } else {
                            action(config.firstRs2Magic(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.firstRs2Magic(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;

                case OTHER:
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                            action(config.firstOther(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                        } else if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstOther()); // Call action with only the first parameter
                        } else {
                            action(config.firstOther(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                        }
                    } else if (config.firstActionMenu().isEmpty()) {
                        if (config.firstActionIDEntry().isEmpty()) {
                            action(config.firstOther()); // Call action with only the first parameter
                        } else {
                            action(config.firstOther(), config.firstActionIDEntry());
                        }
                    } else {
                        action(config.firstOther(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                    break;
                default:
                    Microbot.showMessage("Unknown category: " + config.firstCategoryName().getAction());
            }
        }
    }
    public void secondHotKey() {
        if (conditional(config.conditionsForTwo())) {
            switch (config.secondCategoryName()) {
                case RS2NPC:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.secondRs2Npc(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Npc()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Npc(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Npc()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Npc(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.secondRs2Walker(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;

                case RS2PLAYER:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.secondRs2Player(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Player()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Player(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Player()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Player(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.secondRs2Player(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;

                case RS2INVENTORY:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.secondRs2Inventory(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Inventory()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Inventory(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Inventory()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Inventory(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.secondRs2Inventory(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;

                case RS2WALKER:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.secondRs2Walker(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Walker()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Walker(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Walker()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Walker(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.secondRs2Walker(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;

                case RS2GAMEOBJECT:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.secondRs2GameObject(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2GameObject()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2GameObject(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2GameObject()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2GameObject(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.secondRs2GameObject(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;

                case RS2WIDGET:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.secondRs2Widget(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Widget()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Widget(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Widget()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Widget(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.secondRs2Widget(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;

                case RS2BANK:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.secondRs2Bank(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Bank()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Bank(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Bank()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Bank(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.secondRs2Bank(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;

                case RS2MAGIC:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.secondRs2Magic(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Magic()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Magic(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondRs2Magic()); // Call action with only the second parameter
                        } else {
                            action(config.secondRs2Magic(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.secondRs2Magic(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;

                case OTHER:
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                            action(config.secondOther(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                        } else if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondOther()); // Call action with only the second parameter
                        } else {
                            action(config.secondOther(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                        }
                    } else if (config.secondActionMenu().isEmpty()) {
                        if (config.secondActionIDEntry().isEmpty()) {
                            action(config.secondOther()); // Call action with only the second parameter
                        } else {
                            action(config.secondOther(), config.secondActionIDEntry());
                        }
                    } else {
                        action(config.secondOther(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                    break;
                default:
                    Microbot.showMessage("Unknown category: " + config.secondCategoryName().getAction());
            }
        }
    }
    public boolean conditional(Actionable actionable) {
        if (actionable == config.conditionsForOne()) {
            switch (config.conditionsForOne()) {
                case NONE:
                    return true;
                case HAS_ITEM:
                    if (Pattern.compile("[0-9]+").matcher(config.firstConditionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.firstConditionIDEntry()).matches()) {
                            return condition(config.conditionsForOne(), Integer.parseInt(config.firstConditionIDEntry()), Integer.parseInt(config.firstConditionMenu()));
                        } else if (config.firstConditionIDEntry().isEmpty()) {
                            return condition(config.conditionsForOne()); // Call condition with only the second parameter
                        } else {
                            return condition(config.conditionsForOne(), config.firstConditionIDEntry(), Integer.parseInt(config.firstConditionMenu()));
                        }
                    } else if (config.firstConditionMenu().isEmpty()) {
                        if (config.firstConditionIDEntry().isEmpty()) {
                            return condition(config.conditionsForOne()); // Call condition with only the second parameter
                        } else {
                            return condition(config.conditionsForOne(), config.firstConditionIDEntry());
                        }
                    } else {
                        return condition(config.conditionsForOne(), config.firstConditionIDEntry(), config.firstConditionMenu());
                    }
                default:
                    Microbot.showMessage("Unknown condition: " + config.conditionsForOne().getAction());
                    return false;
            }
        } else {
            switch (config.conditionsForTwo()) {
                case NONE:
                    return true;
                case HAS_ITEM:
                    if (Pattern.compile("[0-9]+").matcher(config.secondConditionMenu()).matches()) {
                        if (Pattern.compile("[0-9]+").matcher(config.secondConditionIDEntry()).matches()) {
                            return condition(config.conditionsForTwo(), Integer.parseInt(config.secondConditionIDEntry()), Integer.parseInt(config.secondConditionMenu()));
                        } else if (config.secondConditionIDEntry().isEmpty()) {
                            return condition(config.conditionsForTwo()); // Call condition with only the second parameter
                        } else {
                            return condition(config.conditionsForTwo(), config.secondConditionIDEntry(), Integer.parseInt(config.secondConditionMenu()));
                        }
                    } else if (config.secondConditionMenu().isEmpty()) {
                        if (config.secondConditionIDEntry().isEmpty()) {
                            return condition(config.conditionsForTwo()); // Call condition with only the second parameter
                        } else {
                            return condition(config.conditionsForTwo(), config.secondConditionIDEntry());
                        }
                    } else {
                        return condition(config.conditionsForTwo(), config.secondConditionIDEntry(), config.secondConditionMenu());
                    }
                default:
                    Microbot.showMessage("Unknown condition: " + config.conditionsForTwo().getAction());
                    return false;
            }
        }
    }
    public void action(Actionable action, int ID) {
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {

                if (action instanceof sRs2Bank) {
                    switch ((sRs2Bank) action) {
                        case WITHDRAW_ALL:
                            Rs2Bank.withdrawAll(ID);
                            break;
                        case WITHDRAW_ONE:
                            Rs2Bank.withdrawOne(ID);
                            break;
                        case DEPOSIT_ALL:
                            Rs2Bank.depositAll(ID);
                            break;
                    }
                } else if (action instanceof sRs2Walker) {
                    switch ((sRs2Walker) action) {
                        case WALK_FAST_CANVAS:
                            Rs2Walker.walkFastCanvas(new WorldPoint(ID, 0, Rs2Player.getWorldLocation().getPlane()));
                            break;
                    }
                } else if (action instanceof sRs2Inventory) {
                    switch ((sRs2Inventory) action) {
                        case DROP_ITEM:
                            Rs2Inventory.drop(ID);
                            break;
                        case INV_INTERACT:
                            Rs2Inventory.interact(ID);
                            break;
                        case WIELD:
                            Rs2Inventory.wield(ID);
                            break;
                        case WEAR:
                            Rs2Inventory.wear(ID);
                            break;
                        case EQUIP:
                            Rs2Inventory.equip(ID);
                            break;
                        case USE_RANDOM:
                            Rs2Inventory.use(Rs2Storm.getRandomItemWithLimit(ID));
                            break;
                        case USE_LAST:
                            Rs2Inventory.useLast(ID);
                            break;
                        case DROP_ALL:
                            Rs2Inventory.dropAll(ID);
                            break;
                    }
                } else if (action instanceof sRs2GameObject) {
                    switch ((sRs2GameObject) action) {
                        case OBJ_INTERACT:
                            Rs2GameObject.interact(ID);
                            break;
                    }
                } else if (action instanceof sRs2Npc) {
                    switch ((sRs2Npc) action) {
                        case ATTACK:
                            Rs2Npc.attack(ID);
                            break;
                    }
                } else {
                    Microbot.showMessage("Unknown action : " + action.getAction() + "(int "+ID+");");
                }
            }
        }
    }

    public void action(Actionable action, String name) {
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (action instanceof sRs2Bank) {
                    switch ((sRs2Bank) action) {
                        case WITHDRAW_ALL:
                            Rs2Bank.withdrawAll(name);
                            break;
                        case WITHDRAW_ONE:
                            Rs2Bank.withdrawOne(name);
                            break;
                    }
                } else if (action instanceof sRs2Npc) {
                switch ((sRs2Npc) action) {
                    case ATTACK:
                        Rs2Npc.attack(name);
                        break;
                }
            } else {
                    Microbot.showMessage("Unknown action : " + action.getAction() + "(String "+name+");");
                }
            }
        }
    }

    public void action(Actionable action, int ID, String menu) {
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (action instanceof sRs2Inventory) {
                    switch ((sRs2Inventory) action) {
                        case INV_INTERACT:
                            Rs2Inventory.interact(ID, menu);
                            break;
                    }
                } else if (action instanceof sRs2Npc) {
                    switch ((sRs2Npc) action) {
                        case NPC_INTERACT:
                            Rs2Npc.interact(ID, menu);
                            break;
                    }
                } else if (action instanceof sRs2GameObject) {
                    switch ((sRs2GameObject) action) {
                        case OBJ_INTERACT:
                            Rs2GameObject.interact(ID, menu);
                            break;
                    }
                } else if (action instanceof Other) {
                    switch ((Other) action) {
                        case PRINTLN:
                            //at some point add something to pass parameters to eachother so we can do things like print anything like we do here for widgets.
                            //System.out.println(Rs2Widget.getWidget(ID));
                            break;
                    }
                } else {
                    Microbot.showMessage("Unknown action : " + action.getAction() + "(int "+ID+", String "+menu+");");
                }
            }
        }
    }

    public void action(Actionable action, String menu, String ID){
        Microbot.showMessage("Unknown action : " + action.getAction() + "(String "+menu+", String "+ID+");");
    }
    public void action(Actionable action, String menu, int ID){
        Microbot.showMessage("Unknown action : " + action.getAction() + "(String "+menu+", int "+ID+");");
    }
    public void action(Actionable action, int ID, int value){
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (action instanceof sRs2Walker) {
                    switch ((sRs2Walker) action) {
                        case WALK_FAST_CANVAS:
                            Rs2Walker.walkFastCanvas(new WorldPoint(ID, value, Rs2Player.getWorldLocation().getPlane()));
                    }
                }
            }
        }
    }
    public void action(Actionable action){
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (action instanceof sRs2Player) {
                    switch ((sRs2Player) action) {
                        case LOGOUT:
                            Rs2Player.logout();
                            break;
                        case USE_FOOD:
                            Rs2Player.useFood();
                            break;
                    }
                } else {
                    Microbot.showMessage("Unknown action : " + action.getAction() + "();");
                }
            }
        }
    }
    public boolean condition(Actionable action){
        if (this.isRunning()) {
            if(action instanceof Conditionals) {
                switch ((Conditionals) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, int ID){
        if (this.isRunning()) {
            if(action instanceof Conditionals) {
                switch ((Conditionals) action) {
                    case NONE:
                        return true;
                    case HAS_ITEM:
                        return Rs2Inventory.hasItem(ID);
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, String name){
        if (this.isRunning()) {
            if(action instanceof Conditionals) {
                switch ((Conditionals) action) {
                    case NONE:
                        return true;
                    case HAS_ITEM:
                        return Rs2Inventory.hasItem(name);
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, int ID, String name){
        if (this.isRunning()) {
            if(action instanceof Conditionals) {
                switch ((Conditionals) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, String name, int value){
        if (this.isRunning()) {
            if(action instanceof Conditionals) {
                switch ((Conditionals) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, int ID, int value){
        if (this.isRunning()) {
            if(action instanceof Conditionals) {
                switch ((Conditionals) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, String name, String ID){
        if (this.isRunning()) {
            if(action instanceof Conditionals) {
                switch ((Conditionals) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    @Override
    public void shutdown() {
        super.shutdown();
    }
}
