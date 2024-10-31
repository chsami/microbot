package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.Config;
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

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class actionHotkeyScript extends Script {
    public static double version = 1.2;
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
        if (firstConditional()) {
            switch (config.firstActionCategoryName()) {
                case RS2NPC:
                    handleCommonAction(config.firstARs2Npc(), config.firstParameterOne(), config.firstParameterTwo());
                    break;
                case RS2PLAYER:
                    handleCommonAction(config.firstARs2Player(), config.firstParameterOne(), config.firstParameterTwo());
                    break;
                case RS2INVENTORY:
                    handleCommonAction(config.firstARs2Inventory(), config.firstParameterOne(), config.firstParameterTwo());
                    break;
                case RS2WALKER:
                    handleCommonAction(config.firstARs2Walker(), config.firstParameterOne(), config.firstParameterTwo());
                    break;
                case RS2GAMEOBJECT:
                    handleCommonAction(config.firstARs2GameObject(), config.firstParameterOne(), config.firstParameterTwo());
                    break;
                case RS2WIDGET:
                    handleCommonAction(config.firstARs2Widget(), config.firstParameterOne(), config.firstParameterTwo());
                    break;
                case RS2BANK:
                    handleCommonAction(config.firstARs2Bank(), config.firstParameterOne(), config.firstParameterTwo());
                    break;
                case RS2MAGIC:
                    handleCommonAction(config.firstARs2Magic(), config.firstParameterOne(), config.firstParameterTwo());
                    break;
                case OTHER:
                    handleCommonAction(config.firstAOther(), config.firstParameterOne(), config.firstParameterTwo());
                    break;
                default:
                    String currentCategory = config.firstActionCategoryName().getAction();
                    if(key1isdown){key1isdown=false;} else {key2isdown=false;}
                    Microbot.showMessage("Unknown category: " + currentCategory);
                    while(this.isRunning()) {
                        if(!Objects.equals(config.firstActionCategoryName().getAction(), currentCategory) || !this.isRunning()){
                            break;
                        }
                        sleep(100,1000);
                    }
            }
        }
    }
    public void secondHotKey() {
        if (secondConditional()) {
            switch (config.secondActionCategoryName()) {
                case RS2NPC:
                    handleCommonAction(config.secondARs2Npc(), config.secondParameterOne(), config.secondParameterTwo());
                    break;
                case RS2PLAYER:
                    handleCommonAction(config.secondARs2Player(), config.secondParameterOne(), config.secondParameterTwo());
                    break;
                case RS2INVENTORY:
                    handleCommonAction(config.secondARs2Inventory(), config.secondParameterOne(), config.secondParameterTwo());
                    break;
                case RS2WALKER:
                    handleCommonAction(config.secondARs2Walker(), config.secondParameterOne(), config.secondParameterTwo());
                    break;
                case RS2GAMEOBJECT:
                    handleCommonAction(config.secondARs2GameObject(), config.secondParameterOne(), config.secondParameterTwo());
                    break;
                case RS2WIDGET:
                    handleCommonAction(config.secondARs2Widget(), config.secondParameterOne(), config.secondParameterTwo());
                    break;
                case RS2BANK:
                    handleCommonAction(config.secondARs2Bank(), config.secondParameterOne(), config.secondParameterTwo());
                    break;
                case RS2MAGIC:
                    handleCommonAction(config.secondARs2Magic(), config.secondParameterOne(), config.secondParameterTwo());
                    break;
                case OTHER:
                    handleCommonAction(config.secondAOther(), config.secondParameterOne(), config.secondParameterTwo());
                    break;
                default:
                    String currentCategory = config.secondActionCategoryName().getAction();
                    if(key1isdown){key1isdown=false;} else {key2isdown=false;}
                    Microbot.showMessage("Unknown category: " + currentCategory);
                    while(this.isRunning()) {
                        if(!Objects.equals(config.secondActionCategoryName().getAction(), currentCategory) || !this.isRunning()){
                            break;
                        }
                        sleep(100,1000);
                    }
            }
        }
    }
    public boolean firstConditional() {
            switch (config.firstConditionCategoryName()) {
                case NONE:
                    return handleCommonCondition(config.firstCOther(), config.firstConditionParameterOne(), config.firstConditionParameterTwo());
                case RS2INVENTORY:
                    return handleCommonCondition(config.firstCRs2Inventory(), config.firstConditionParameterOne(), config.firstConditionParameterTwo());
                default:
                    String currentCategory = config.firstConditionCategoryName().getAction();
                    if(key1isdown){key1isdown=false;} else {key2isdown=false;}
                    Microbot.showMessage("Unknown category: " + currentCategory);
                    while(this.isRunning()) {
                        if(!Objects.equals(config.firstConditionCategoryName().getAction(), currentCategory) || !this.isRunning()){
                            break;
                        }
                        sleep(100,1000);
                    }
            }
            return false;
    }
    public boolean secondConditional() {
        switch (config.secondConditionCategoryName()) {
            case NONE:
                return handleCommonCondition(config.secondCOther(), config.secondConditionParameterOne(), config.secondConditionParameterTwo());
            case RS2INVENTORY:
                return handleCommonCondition(config.secondCRs2Inventory(), config.secondConditionParameterOne(), config.secondConditionParameterTwo());
            default:
                String currentCategory = config.secondConditionCategoryName().getAction();
                if(key1isdown){key1isdown=false;} else {key2isdown=false;}
                Microbot.showMessage("Unknown category: " + currentCategory);
                while(this.isRunning()) {
                    if(!Objects.equals(config.secondActionCategoryName().getAction(), currentCategory) || !this.isRunning()){
                        break;
                    }
                    sleep(100,1000);
                }
        }
        return false;
    }
    public void action(Actionable action, int ID) {
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (action instanceof aRs2Bank) {
                    switch ((aRs2Bank) action) {
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
                } else if (action instanceof aRs2Walker) {
                    switch ((aRs2Walker) action) {
                        case WALK_FAST_CANVAS:
                            Rs2Walker.walkFastCanvas(new WorldPoint(ID, 0, Rs2Player.getWorldLocation().getPlane()));
                            break;
                    }
                } else if (action instanceof aRs2Inventory) {
                    switch ((aRs2Inventory) action) {
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
                            Rs2Inventory.use(Rs2Storm.getRandomItemWithLimit(ID, 4));
                            break;
                        case USE_LAST:
                            Rs2Inventory.useLast(ID);
                            break;
                        case DROP_ALL:
                            Rs2Inventory.dropAll(ID);
                            break;
                    }
                } else if (action instanceof aRs2GameObject) {
                    switch ((aRs2GameObject) action) {
                        case OBJ_INTERACT:
                            Rs2GameObject.interact(ID);
                            break;
                    }
                } else if (action instanceof aRs2Npc) {
                    switch ((aRs2Npc) action) {
                        case ATTACK:
                            Rs2Npc.attack(ID);
                            break;
                    }
                } else {
                    Config oldConfig = config;
                    if(key1isdown){key1isdown=false;} else {key2isdown=false;}
                    Microbot.showMessage("Unknown action : " + action.getAction() + "(int "+ID+");");
                    while(this.isRunning()) {
                        if(!Objects.equals(oldConfig, config) || !this.isRunning()){
                            break;
                        }
                        sleep(100,1000);
                    }
                }
            }
        }
    }

    public void action(Actionable action, String name) {
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (action instanceof aRs2Bank) {
                    switch ((aRs2Bank) action) {
                        case WITHDRAW_ALL:
                            Rs2Bank.withdrawAll(name);
                            break;
                        case WITHDRAW_ONE:
                            Rs2Bank.withdrawOne(name);
                            break;
                    }
                } else if (action instanceof aRs2Npc) {
                    switch ((aRs2Npc) action) {
                        case ATTACK:
                            Rs2Npc.attack(name);
                            break;
                    }
                } else if (action instanceof aRs2Inventory) {
                    switch ((aRs2Inventory) action) {
                        case DROP_ITEM:
                            Rs2Inventory.drop(name);
                            break;
                    }
                } else {
                    Config oldConfig = config;
                    if(key1isdown){key1isdown=false;} else {key2isdown=false;}
                    Microbot.showMessage("Unknown action : " + action.getAction() + "(String "+name+");");
                    while(this.isRunning()) {
                        if(!Objects.equals(oldConfig, config) || !this.isRunning()){
                            break;
                        }
                        sleep(100,1000);
                    }
                }
            }
        }
    }

    public void action(Actionable action, int ID, String menu) {
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (action instanceof aRs2Inventory) {
                    switch ((aRs2Inventory) action) {
                        case INV_INTERACT:
                            Rs2Inventory.interact(ID, menu);
                            break;
                    }
                } else if (action instanceof aRs2Npc) {
                    switch ((aRs2Npc) action) {
                        case NPC_INTERACT:
                            Rs2Npc.interact(ID, menu);
                            break;
                    }
                } else if (action instanceof aRs2GameObject) {
                    switch ((aRs2GameObject) action) {
                        case OBJ_INTERACT:
                            Rs2GameObject.interact(ID, menu);
                            break;
                    }
                } else if (action instanceof aOther) {
                    switch ((aOther) action) {
                        case PRINTLN:
                            //at some point add something to pass parameters to eachother so we can do things like print anything like we do here for widgets.
                            //System.out.println(Rs2Widget.getWidget(ID));
                            break;
                    }
                } else {
                    Config oldConfig = config;
                    if(key1isdown){key1isdown=false;} else {key2isdown=false;}
                    Microbot.showMessage("Unknown action : " + action.getAction() + "(int "+ID+", String "+menu+");");
                    while(this.isRunning()) {
                        if(!Objects.equals(oldConfig, config) || !this.isRunning()){
                            break;
                        }
                        sleep(100,1000);
                    }
                }
            }
        }
    }

    public void action(Actionable action, String menu, String ID){
        Config oldConfig = config;
        if(key1isdown){key1isdown=false;} else {key2isdown=false;}
        Microbot.showMessage("Unknown action : " + action.getAction() + "(String "+menu+", String "+ID+");");
        while(this.isRunning()) {
            if(!Objects.equals(oldConfig, config) || !this.isRunning()){
                break;
            }
            sleep(100,1000);
        }
    }
    public void action(Actionable action, String menu, int ID){
        Config oldConfig = config;
        if(key1isdown){key1isdown=false;} else {key2isdown=false;}
        Microbot.showMessage("Unknown action : " + action.getAction() + "(String "+menu+", int "+ID+");");
        while(this.isRunning()) {
            if(!Objects.equals(oldConfig, config) || !this.isRunning()){
                break;
            }
            sleep(100,1000);
        }
    }
    public void action(Actionable action, int ID, int value){
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (action instanceof aRs2Walker) {
                    switch ((aRs2Walker) action) {
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
                if (action instanceof aRs2Player) {
                    switch ((aRs2Player) action) {
                        case LOGOUT:
                            Rs2Player.logout();
                            break;
                        case USE_FOOD:
                            Rs2Player.useFood();
                            break;
                    }
                } else if (action instanceof aRs2Bank) {
                    switch ((aRs2Bank) action) {
                        case OPEN_BANK:
                            Rs2Bank.openBank();
                            break;
                    }
                } else {
                    Config oldConfig = config;
                    if(key1isdown){key1isdown=false;} else {key2isdown=false;}
                    Microbot.showMessage("Unknown action : " + action.getAction() + "();");
                    while(this.isRunning()) {
                        if(!Objects.equals(oldConfig, config) || !this.isRunning()){
                            break;
                        }
                        sleep(100,1000);
                    }
                }
            }
        }
    }
    public boolean condition(Actionable action){
        if (this.isRunning()) {
            if(action instanceof cOther) {
                switch ((cOther) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, int ID){
        if (this.isRunning()) {
            if(action instanceof cOther) {
                switch ((cOther) action) {
                    case NONE:
                        return true;
                }
            } else if (action instanceof cRs2Inventory)
                switch ((cRs2Inventory) action) {
                    case HAS_ITEM:
                        return Rs2Inventory.hasItem(ID);
                }
        }
        return false;
    }
    public boolean condition(Actionable action, String name){
        if (this.isRunning()) {
            if(action instanceof cOther) {
                switch ((cOther) action) {
                    case NONE:
                        return true;
                }
            } else if (action instanceof cRs2Inventory)
                switch ((cRs2Inventory) action) {
                    case HAS_ITEM:
                        return Rs2Inventory.hasItem(name);
                }
        }
        return false;
    }
    public boolean condition(Actionable action, int ID, String name){
        if (this.isRunning()) {
            if(action instanceof cOther) {
                switch ((cOther) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, String name, int value){
        if (this.isRunning()) {
            if(action instanceof cOther) {
                switch ((cOther) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, int ID, int value){
        if (this.isRunning()) {
            if(action instanceof cOther) {
                switch ((cOther) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    public boolean condition(Actionable action, String name, String ID){
        if (this.isRunning()) {
            if(action instanceof cOther) {
                switch ((cOther) action) {
                    case NONE:
                        return true;
                }
            }
        }
        return false;
    }
    void firstEmpty(Actionable action, String secondParameter){
        Config oldConfig = config;
        if(key1isdown){key1isdown=false;} else {key2isdown=false;}
        Microbot.showMessage("First parameter empty for : " + action.getAction() + "({empty}, "+ secondParameter +");");
        while(this.isRunning()){
            if(!Objects.equals(oldConfig, config) || !this.isRunning()){
                break;
            }
            sleep(100,1000);
        }
    }
    void handleCommonAction(Actionable actionCategory, String parameterOne, String parameterTwo){
        boolean isP1Numeric = Pattern.compile("[0-9]+").matcher(parameterOne).matches();
        boolean isP2Numeric = Pattern.compile("[0-9]+").matcher(parameterTwo).matches();
        if (parameterOne.isEmpty()) {
            if (parameterTwo.isEmpty()) {
                action(actionCategory);//(empty, empty)
            } else if (isP2Numeric) {//(empty, int)
                firstEmpty(actionCategory,"int");
            } else {//(empty, String)
                firstEmpty(actionCategory,"String");
            }
        } else {
            if (parameterTwo.isEmpty()) {
                if (isP1Numeric) {
                    action(actionCategory, Integer.parseInt(parameterOne));//(int, empty)
                } else {
                    action(actionCategory, parameterOne);//(String, empty)
                }
            } else {
                if (isP1Numeric && isP2Numeric) {
                    action(actionCategory, Integer.parseInt(parameterOne), Integer.parseInt(parameterTwo));//(int, int)
                } else if (isP1Numeric) {
                    action(actionCategory, Integer.parseInt(parameterOne), parameterTwo);//(int, String)
                } else if (isP2Numeric) {
                    action(actionCategory, parameterOne, Integer.parseInt(parameterTwo));//(String, int)
                } else {
                    action(actionCategory, parameterOne, parameterTwo);//(String, String)
                }
            }
        }
    }
    boolean handleCommonCondition(Actionable condition, String parameterOne, String parameterTwo){
        boolean isP1Numeric = Pattern.compile("[0-9]+").matcher(parameterOne).matches();
        boolean isP2Numeric = Pattern.compile("[0-9]+").matcher(parameterTwo).matches();
        if (parameterOne.isEmpty()) {
            if (parameterTwo.isEmpty()) {
                condition(condition);//(empty, empty)
            } else if (isP2Numeric) {//(empty, int)
                firstEmpty(condition,"int");
            } else {//(empty, String)
                firstEmpty(condition,"String");
            }
        } else {
            if (parameterTwo.isEmpty()) {
                if (isP1Numeric) {
                    return condition(condition, Integer.parseInt(parameterOne));//(int, empty)
                } else {
                    return condition(condition, parameterOne);//(String, empty)
                }
            } else {
                if (isP1Numeric && isP2Numeric) {
                    return condition(condition, Integer.parseInt(parameterOne), Integer.parseInt(parameterTwo));//(int, int)
                } else if (isP1Numeric) {
                    return condition(condition, Integer.parseInt(parameterOne), parameterTwo);//(int, String)
                } else if (isP2Numeric) {
                    return condition(condition, parameterOne, Integer.parseInt(parameterTwo));//(String, int)
                } else {
                    return condition(condition, parameterOne, parameterTwo);//(String, String)
                }
            }
        }
        Config oldConfig = config;
        if(key1isdown){key1isdown=false;} else {key2isdown=false;}
        Microbot.showMessage("Unknown condition : " + condition.getAction() + "();");
        while(this.isRunning()) {
            if(!Objects.equals(oldConfig, config) || !this.isRunning()){
                break;
            }
            sleep(100,1000);
        }
        return false;
    }
    @Override
    public void shutdown() {
        super.shutdown();
    }
}
