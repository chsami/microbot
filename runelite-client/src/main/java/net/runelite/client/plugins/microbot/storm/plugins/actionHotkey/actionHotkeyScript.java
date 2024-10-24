package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums.Actions;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class actionHotkeyScript extends Script {
    public static double version = 1.0;
    public static int previousKey;
    public static boolean key1isdown;
    public static boolean key2isdown;
    boolean alternating;
    boolean toggled;
    private int minInterval;
    private int previousAction;
    private actionHotkeyConfig config;
    public boolean run(actionHotkeyConfig config){
        alternating = false;
        toggled = false;
        previousAction = 0;
        minInterval = 0;
        previousKey = 0;
        key1isdown = false;
        key2isdown = false;
        this.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (key1isdown || (config.toggle() && toggled && previousKey == config.key1().getKeyCode())) {
                while ((toggled || key1isdown) && this.isRunning()) {
                    if (!alternating) {
                        System.out.println("Should be doing first thing");
                        firstHotKey();
                        if (config.alternate()) { alternating = !alternating; }
                        sleep(config.sleepMin(), config.sleepMax());
                    }
                    if (alternating && key1isdown || toggled) {
                        System.out.println("Should be doing second thing");
                        secondHotKey();
                        if (config.alternate()) { alternating = !alternating; }
                        sleep(config.sleepMin(), config.sleepMax());
                    }
                }
            } else if (key2isdown || config.toggle() && toggled && previousKey == config.key2().getKeyCode()) {
                while ((toggled || key2isdown) && this.isRunning()) {
                    if (!alternating) {
                        System.out.println("Should be doing second thing");
                        secondHotKey();
                        if (config.alternate()) { alternating = !alternating; }
                        sleep(config.sleepMin(), config.sleepMax());
                    }

                    if (alternating && key2isdown || toggled) {
                        System.out.println("Should be doing first thing");
                        firstHotKey();
                        if (config.alternate()) { alternating = !alternating; }
                        sleep(config.sleepMin(), config.sleepMax());
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
        // Check first action category
        switch (config.firstCategoryName().getCategory()) {
            case "Rs2Npc":
                if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                        action(config.firstRs2Npc().getAction(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                    } else {
                        action(config.firstRs2Npc().getAction(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                    }
                } else {
                    if (config.firstActionMenu().isEmpty()) {
                        action(config.firstRs2Npc().getAction(), config.firstActionIDEntry());
                    } else {
                        action(config.firstRs2Npc().getAction(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                }
                break;

            case "Rs2Inventory":
                if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                        action(config.firstRs2Inventory().getAction(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                    } else {
                        action(config.firstRs2Inventory().getAction(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                    }
                } else {
                    if (config.firstActionMenu().isEmpty()) {
                        action(config.firstRs2Inventory().getAction(), config.firstActionIDEntry());
                    } else {
                        action(config.firstRs2Inventory().getAction(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                }
                break;

            case "Rs2Walker":
                if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                        action(config.firstRs2Walker().getAction(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                    } else {
                        action(config.firstRs2Walker().getAction(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                    }
                } else {
                    if (config.firstActionMenu().isEmpty()) {
                        action(config.firstRs2Walker().getAction(), config.firstActionIDEntry());
                    } else {
                        action(config.firstRs2Walker().getAction(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                }
                break;

            case "Rs2GameObject":
                if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                        action(config.firstRs2GameObject().getAction(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                    } else {
                        action(config.firstRs2GameObject().getAction(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                    }
                } else {
                    if (config.firstActionMenu().isEmpty()) {
                        action(config.firstRs2GameObject().getAction(), config.firstActionIDEntry());
                    } else {
                        action(config.firstRs2GameObject().getAction(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                }
                break;

            case "Rs2Widget":
                if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                        action(config.firstRs2Widget().getAction(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                    } else {
                        action(config.firstRs2Widget().getAction(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                    }
                } else {
                    if (config.firstActionMenu().isEmpty()) {
                        action(config.firstRs2Widget().getAction(), config.firstActionIDEntry());
                    } else {
                        action(config.firstRs2Widget().getAction(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                }
                break;

            case "Rs2Bank":
                if (Pattern.compile("[0-9]+").matcher(config.firstActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.firstActionIDEntry()).matches()) {
                        action(config.firstRs2Bank().getAction(), Integer.parseInt(config.firstActionIDEntry()), Integer.parseInt(config.firstActionMenu()));
                    } else {
                        action(config.firstRs2Bank().getAction(), config.firstActionIDEntry(), Integer.parseInt(config.firstActionMenu()));
                    }
                } else {
                    if (config.firstActionMenu().isEmpty()) {
                        action(config.firstRs2Bank().getAction(), config.firstActionIDEntry());
                    } else {
                        action(config.firstRs2Bank().getAction(), config.firstActionIDEntry(), config.firstActionMenu());
                    }
                }
                break;

            case "Other":
                if (config.firstActionMenu().isEmpty()) {
                    action(config.firstOther().getAction(), config.firstActionIDEntry());
                } else {
                    action(config.firstOther().getAction(), config.firstActionIDEntry(), config.firstActionMenu());
                }
                break;
            default:
                System.out.println("Unknown category: " + config.firstCategoryName().getCategory());
        }
    }
    public void secondHotKey() {
        // Check second action category
        switch (config.secondCategoryName().getCategory()) {
            case "Rs2Npc":
                if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                        action(config.secondRs2Npc().getAction(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                    } else {
                        action(config.secondRs2Npc().getAction(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                    }
                } else {
                    if (config.secondActionMenu().isEmpty()) {
                        action(config.secondRs2Npc().getAction(), config.secondActionIDEntry());
                    } else {
                        action(config.secondRs2Npc().getAction(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                }
                break;

            case "Rs2Inventory":
                if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                        action(config.secondRs2Inventory().getAction(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                    } else {
                        action(config.secondRs2Inventory().getAction(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                    }
                } else {
                    if (config.secondActionMenu().isEmpty()) {
                        action(config.secondRs2Inventory().getAction(), config.secondActionIDEntry());
                    } else {
                        action(config.secondRs2Inventory().getAction(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                }
                break;

            case "Rs2Walker":
                if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                        action(config.secondRs2Walker().getAction(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                    } else {
                        action(config.secondRs2Walker().getAction(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                    }
                } else {
                    if (config.secondActionMenu().isEmpty()) {
                        action(config.secondRs2Walker().getAction(), config.secondActionIDEntry());
                    } else {
                        action(config.secondRs2Walker().getAction(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                }
                break;

            case "Rs2GameObject":
                if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                        action(config.secondRs2GameObject().getAction(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                    } else {
                        action(config.secondRs2GameObject().getAction(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                    }
                } else {
                    if (config.secondActionMenu().isEmpty()) {
                        action(config.secondRs2GameObject().getAction(), config.secondActionIDEntry());
                    } else {
                        action(config.secondRs2GameObject().getAction(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                }
                break;

            case "Rs2Widget":
                if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                        action(config.secondRs2Widget().getAction(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                    } else {
                        action(config.secondRs2Widget().getAction(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                    }
                } else {
                    if (config.secondActionMenu().isEmpty()) {
                        action(config.secondRs2Widget().getAction(), config.secondActionIDEntry());
                    } else {
                        action(config.secondRs2Widget().getAction(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                }
                break;

            case "Rs2Bank":
                if (Pattern.compile("[0-9]+").matcher(config.secondActionMenu()).matches()) {
                    if (Pattern.compile("[0-9]+").matcher(config.secondActionIDEntry()).matches()) {
                        action(config.secondRs2Bank().getAction(), Integer.parseInt(config.secondActionIDEntry()), Integer.parseInt(config.secondActionMenu()));
                    } else {
                        action(config.secondRs2Bank().getAction(), config.secondActionIDEntry(), Integer.parseInt(config.secondActionMenu()));
                    }
                } else {
                    if (config.secondActionMenu().isEmpty()) {
                        action(config.secondRs2Bank().getAction(), config.secondActionIDEntry());
                    } else {
                        action(config.secondRs2Bank().getAction(), config.secondActionIDEntry(), config.secondActionMenu());
                    }
                }
                break;

            case "Other":
                if (config.secondActionMenu().isEmpty()) {
                    action(config.secondOther().getAction(), config.secondActionIDEntry());
                } else {
                    action(config.secondOther().getAction(), config.secondActionIDEntry(), config.secondActionMenu());
                }
                break;
            default:
                System.out.println("Unknown category: " + config.secondCategoryName().getCategory());
        }
    }
    public void action(String action, int ID){
        if(minInterval==0 || System.currentTimeMillis()>(previousAction+minInterval)){
            if(this.isRunning()) {
                if (Objects.equals(action, Actions.WITHDRAW_ALL.getAction())) { Rs2Bank.withdrawAll(ID); }
                if (Objects.equals(action, Actions.WITHDRAW_ONE.getAction())) { Rs2Bank.withdrawOne(ID); }
                if (Objects.equals(action, Actions.OBJ_INTERACT.getAction())) { Rs2GameObject.interact(ID); }
            }
        }
    }
    public void action(String action, String name){
        if(minInterval==0 || System.currentTimeMillis()>(previousAction+minInterval)) {
            if (this.isRunning()) {
                if (Objects.equals(action, Actions.ATTACK.getAction())) { Rs2Npc.attack(name); }
                if (Objects.equals(action, Actions.WITHDRAW_ALL.getAction())) { Rs2Bank.withdrawAll(name); }
                if (Objects.equals(action, Actions.DEPOSIT_ALL.getAction())) { Rs2Bank.depositAll(name); }
            }
        }
    }
    public void action(String action, int ID, String menu) {
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (Objects.equals(action, Actions.NPC_INTERACT.getAction())) { Rs2Npc.interact(ID, menu); }
                if (Objects.equals(action, Actions.OBJ_INTERACT.getAction())) { Rs2GameObject.interact(ID, menu); }
                if (Objects.equals(action, Actions.INV_INTERACT.getAction())) { Rs2Inventory.interact(ID, menu); }
                if (Objects.equals(action, Actions.PRINTLN.getAction()) && Objects.equals(menu, Actions.GET_WIDGET.getAction())) { System.out.println(Rs2Widget.getWidget(ID)); }
            }
        }
    }
    public void action(String action, int ID, int numerical){
        if (minInterval == 0 || System.currentTimeMillis() > (previousAction + minInterval)) {
            if (this.isRunning()) {
                if (Objects.equals(action, Actions.WALK_FAST_CANVAS.getAction())) { Rs2Walker.walkFastCanvas(new WorldPoint(ID, numerical, Rs2Player.getWorldLocation().getPlane())); }
            }
        }
    }
    public void action(String action, String menu, String ID){

    }
    public void action(String action, String menu, int ID){

    }
    @Override
    public void shutdown() {
        super.shutdown();
    }
}
