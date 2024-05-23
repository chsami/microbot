package net.runelite.client.plugins.ogPlugins.ogConstruction;

import net.runelite.api.ObjectID;
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
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.ogPlugins.ogConstruction.enums.Butler;
import net.runelite.client.plugins.ogPlugins.ogConstruction.enums.Furniture;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ogConstScript extends Script {

    public static double version = 1.0;
    private enum State{ ENABLE_BUILDING_MODE , BUILDING, DESTROY , FILL_MONEY_BAG , SEND_BUTLER , LOGOUT }
    private State status;
    private int gameTickLastSentButler = -12;
    private int currentGameTick;
    private int coinsLeftInMoneyBag = 0;
    private int lastActionTick = 0;

    //Settings
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
    private String moneyBagTopUpAmount = "3m";
    private boolean logging;


    private void callDelay(){
        if(Random.random(1,this.delayChance) == 3) {
            int delayGeneratedMin = Random.random(this.delayMin - 20, this.delayMin);
            int delayGeneratedMax = Random.random(this.delayMax , this.delayMax + 20);
            int delayGenerated = Random.random(delayGeneratedMin,delayGeneratedMax);
            if(logging){System.out.println("Delay range of: " + delayGeneratedMin + "-" + delayGeneratedMax + " generated a delay of " + delayGenerated + "ms.");}
            sleep(delayGenerated);
        } else {
            int delayGenerated = Random.random(delayMin,delayMax);
            if(logging){System.out.println("Delay range of: " + this.delayMin + "-" + this.delayMax + " generated a delay of " + delayGenerated + "ms.");}
            sleep(delayGenerated);
        }
    }
    private void callAFK(){
        if(Random.random(0,this.afkChance) == 3) {
            int afkGenerated = Random.random( this.afkMin * 600 , this.afkMax * 600 );
            if(logging){System.out.println("AFK range of: " + this.afkMin + "-" + this.afkMax + " generated a delay of " + afkGenerated + "minutes.");}
            sleep(afkGenerated);
        }
    }
    private boolean moneyBagTopUpNeeded(){return (moneyBagRefillThreshold + Random.random(-50000,200000)) >= coinsLeftInMoneyBag;}
    private boolean checkFurnitureBuilt(){ return Rs2GameObject.exists(furniture.getBuiltID()); }
    private boolean checkIfButlerHere(){ return Rs2Npc.getNpc(butler.getButlerID()) != null; }
    private boolean checkButlerNearPlayer(){ if(!checkIfButlerHere()){return false;} return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(Rs2Npc.getNpc(butler.getButlerID()).getWorldLocation()) < 3; }
    public boolean run(ogConstConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if(logging){System.out.println("Script rerunning");}
                setSettings(config);
                calcState();
                if(status == State.BUILDING){build();}
                else if (status == State.ENABLE_BUILDING_MODE){enableBuildingMode();}
                else if(status == State.DESTROY){destroy();}
                else if(status == State.SEND_BUTLER){sendButler();}
                else if(status == State.FILL_MONEY_BAG){fillMoneyBag();}
                else if(status == State.LOGOUT){
                    Microbot.status = "Logging Out";
                    Rs2Tab.switchToLogout();
                    Rs2Widget.clickWidget("Click here to logout");
                    super.shutdown();
                }
            } catch (Exception ex) {System.out.println(ex.getMessage());}
        }, 0, Random.random(0,100), TimeUnit.MILLISECONDS);
        return true;
    }
    private void setSettings(ogConstConfig config){
            if(logging){System.out.println("Checking for updates in Settings");}
            if(this.currentconfig != config){this.currentconfig = config; if(logging){System.out.println("Updated main config"); } }
            if(this.delayMin != config.delayMin() ){ this.delayMin = config.delayMin(); if(logging){System.out.println("Updated Delay Min: " + this.delayMin);} }
            if(this.delayMax != config.delayMax()){ this.delayMax = config.delayMax(); if(logging){System.out.println("Updated Delay Max: " + this.delayMax);} }
            if(this.delayChance != config.delayChance()){ this.delayChance = config.delayChance(); if(logging){System.out.println("Updated Delay Deviation Chance: " + this.delayChance);} }
            if(this.afkMin != config.afkMin()){ this.afkMin = config.afkMin(); if(logging){System.out.println("Updated AFK Min: " + this.afkMin);} }
            if(this.afkMax != config.afkMax()){ this.afkMax = config.afkMax(); if(logging){System.out.println("Updated AFK Max: " + this.afkMax);} }
            if(this.afkChance != config.afkChance()){ this.afkChance = config.afkChance(); if(logging){System.out.println("Updated AFK Chance: " + this.afkChance);}}
            if(this.furniture != config.selectedFurniture()){ this.furniture = config.selectedFurniture(); if(logging){System.out.println("Updated selected furniture: " + this.furniture.getName());} }
            if(this.butler != config.selectedButler()){ this.butler = config.selectedButler(); if(logging){System.out.println("Updated selected butler: " + this.butler.getName());} }
            if(this.useServentsBag != config.useMoneyBag()){ this.useServentsBag = config.useMoneyBag(); if(logging){System.out.println("Updated use Servants Moneybag:" + this.useServentsBag);} }
            if(this.moneyBagRefillThreshold != config.getMinMoneybagAmount()){ this.moneyBagRefillThreshold = config.getMinMoneybagAmount(); if(logging){System.out.println("Updated Servants Moneybag Refill Amount: " + this.moneyBagRefillThreshold);} }
            if(this.logging != config.verboseLogging()){ if(logging || config.verboseLogging()){System.out.println("Updated Verbose Logging: " + config.verboseLogging() );}  this.logging = config.verboseLogging(); }
    }
    private void build(){
        Microbot.status = "Building " + furniture.getName();
        if(logging){System.out.println("===========================BUILD FUNCTION CALLED===========================");}
        Rs2GameObject.interact(furniture.getUnBuiltID(), "Build");
        sleepUntil(() -> Rs2Widget.getWidget(30015493) != null, Random.random(1000,2000));
        if(Rs2Widget.getWidget(30015493) != null){
            callDelay();
            if(logging){System.out.println("Selecting build option");}
            Rs2Keyboard.typeString(String.valueOf(furniture.getBuildOption()));
            callDelay();
        }
        sleepUntil(this::checkFurnitureBuilt, Random.random(800,900));
    }
    private void destroy(){
        Microbot.status = "Destroying " + furniture.getName();
        if(logging){System.out.println("===========================DESTROY FUNCTION CALLED===========================");}
        if(logging){System.out.println("Destroying:" + furniture.getBuiltID());}
        if(furniture.getBuiltID() == ObjectID.DOOR_13344 &&
                !Rs2Inventory.hasItemAmount(furniture.getPlankNeeded(), furniture.getPlankAmountNeeded()) &&
                !checkIfButlerHere() &&
                !(currentGameTick > gameTickLastSentButler + (butler.getTicksNeededToBank() + 2))){
                    if(logging){System.out.println("Sleeping until: checkButlerNearPlayer");}
                    sleepUntil(this::checkButlerNearPlayer);
                    sleepUntil(() -> Rs2Widget.findWidget("Master, I have returned with what you asked me to") != null);
        }
        if(logging){System.out.println("Clicked Remove");}
        Rs2GameObject.interact(furniture.getBuiltID(), "Remove");
        if(logging){System.out.println("Sleeping until: Really Remove it?, 800-1200");}
        sleepUntil(() -> Rs2Widget.findWidget("Really remove it?") != null, Random.random(1000,1500));
        if(Rs2Widget.findWidget("Really remove it?") != null){
            if(Random.random(1,29) == 3){
                callDelay();
                Rs2Widget.clickWidget("Yes");
                if(logging){System.out.println("Selecting Yes");}
            }
            else{
                callDelay();
                Rs2Keyboard.typeString("1");
                if(logging){System.out.println("Selecting Yes");}
            }
        }
        sleepUntil(() -> !checkFurnitureBuilt(), Random.random(600,700));
    }
    private void fillMoneyBag(){
        Microbot.status = "Checking Servant's Moneybag";
        if(logging){System.out.println("===========================FILL MONEYBAG FUNCTION CALLED===========================");}
        Rs2GameObject.interact(ObjectID.SERVANTS_MONEY_BAG, "Use");
        sleepUntil(()-> Rs2Widget.findWidget("The moneybag ") != null,Random.random(5000,9000));
        if(Rs2Widget.findWidget("The moneybag ") != null){
            if(Random.random(1,100) == 3){
                Rs2Widget.clickWidget("Click here to continue");
            } else {
                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);}
        }
        sleepUntil(()-> Rs2Widget.findWidget("Select an Option") != null,Random.random(600,700));
        if(Rs2Widget.findWidget("Select an Option") != null){
            Rs2Keyboard.typeString("1");}
        sleepUntil(()-> Rs2Widget.findWidget("How many coins do you wish to deposit?") != null,Random.random(600,700));
        if(Rs2Widget.findWidget("How many coins do you wish to deposit?") != null){
            Rs2Keyboard.typeString(moneyBagTopUpAmount);}
        Rs2Keyboard.keyPress(KeyEvent.VK_ENTER);
        sleepUntil(()-> Rs2Widget.findWidget("The moneybag ") != null,Random.random(600,700));
        if(logging){System.out.println("String detected: " + (Rs2Widget.findWidget("The moneybag ")).getText());}
        this.coinsLeftInMoneyBag = extractNumber((Rs2Widget.findWidget("The moneybag ")).getText());
        if(logging){System.out.println("Coins now in money bag: "+ coinsLeftInMoneyBag);}
        Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
    }
    private void sendButler(){
        Microbot.status = "Sending Butler Out";
        if(logging){System.out.println("===========================SEND BUTLER FUNCTION CALLED===========================");}
        if(Rs2Widget.findWidget("Master, I have returned with") != null){
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);return;}
        if(logging){System.out.println("Chatbox message: " + Rs2Widget.getChildWidgetText(10616888,0));}
        if(!checkIfButlerHere() ||
                !checkButlerNearPlayer() || (Rs2Widget.getChildWidgetText(10616888,0)).contains("I can't reach that!")){
            if(logging){System.out.println("Switching to Settings Tab");}
            navigateToHouseSettings();
            if(Rs2Widget.getWidget(24248342) != null){
                callDelay();
                if(logging){System.out.println("Clicked Call Servant");}
                Rs2Widget.clickWidget(24248342);
                callDelay();
                if(logging){System.out.println("Pressing ESC");}
                Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
            }
            if(Rs2Widget.findWidget("Master, I have returned with") != null){
                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);return;}
            if(logging){System.out.println("Sleeping until butler near player, 1000 - 2000");}
            sleepUntil(this::checkButlerNearPlayer, Random.random(1000,2000));
        } else if (checkButlerNearPlayer()){
            Rs2Npc.interact(butler.getName(), "Talk-to");
        }
        if(Rs2Widget.findWidget("The moneybag does not contain enough.")!= null){
            this.coinsLeftInMoneyBag = 0;
            if(Random.random(1,10) == 3){
                Rs2Widget.clickWidget("Click here to continue");
            } else {
                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);}
            sleepUntil(()-> Rs2Widget.findWidget("Select an option") != null);
            Rs2Keyboard.typeString("1");
        }
        if(logging){System.out.println("Looking for: Repeat last task?");}
        sleepUntil(()-> Rs2Widget.findWidget("Repeat last task?") != null || (Rs2Widget.getChildWidgetText(10616888,0)).contains("I can't reach that!"), Random.random(800,900));
        if(Rs2Widget.findWidget("Repeat last task?") != null){
            callDelay();
            Rs2Keyboard.typeString("1");
            callDelay();
            sleepUntil(() -> !checkIfButlerHere(),Random.random(600,700));
            gameTickLastSentButler = currentGameTick;
        }
        Rs2Tab.switchToInventoryTab();
        sleepUntil(() -> Rs2Inventory.hasItemAmount(furniture.getPlankNeeded(), furniture.getPlankAmountNeeded()) || checkButlerNearPlayer(),Random.random(10000,13000));
    }
    //TODO Update butlers fetch to correct planks if needed - more regex
    private void updateButlerAction(){}
    private void enableBuildingMode(){
        Microbot.status = "Enabling Building Mode";
        if(logging){System.out.println("===========================ENABLE BUILDING MODE FUNCTION CALLED===========================");}
        navigateToHouseSettings();
        if(Rs2Widget.getWidget(24248325) != null){
            callDelay();
            if(logging){System.out.println("Clicking Enable Building Mode");}
            Rs2Widget.clickWidget(24248325);
            sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(6719)) == 2, Random.random(5000,10000));
            sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(6719)) == 0, Random.random(5000,10000));
            //sleep(1000,3000);
        }
    }
    private void navigateToHouseSettings() {
        Rs2Tab.switchToSettingsTab();
        if(Rs2Widget.getWidget(7602207) == null){
            sleep(30,40);
            Rs2Widget.clickWidget(7602241);
            if(logging){System.out.println("Selected Correct Settings Tab");}
            sleepUntil(()-> Rs2Widget.getWidget(7602207) != null);
        }
        if(Rs2Widget.getWidget(24248342) == null){
            sleep(30,40);
            Rs2Widget.clickWidget(7602207);
            if(logging){System.out.println("Clicked House Options");}
            sleepUntil(()-> Rs2Widget.getWidget(24248342) != null);
        }
    }
    public void onGameObjectSpawned(GameObjectSpawned event) {
        try{
            if(event.getGameObject().getId() == furniture.getBuiltID() || event.getGameObject().getId() == furniture.getUnBuiltID()){
                if(logging){System.out.println("Game Object Spawned:" + event.getGameObject().getId());}
            }
        } catch (Exception e){}
    }
    public void onGameTick(GameTick gameTick) {
        this.currentGameTick++;
        if((Rs2Widget.getChildWidgetText(10616888,0)).contains("Your servant takes some payment from the moneybag")){
            this.coinsLeftInMoneyBag = extractNumber(((Rs2Widget.getChildWidgetText(10616888,0))));
        }
        if(logging){System.out.println("Game Tick: " + currentGameTick);}
        //callAFK();
    }
    private int extractNumber(String input){
        Pattern pattern = Pattern.compile("([0-9].{0,9}?(?= coins)|empty|full)");
        Matcher matcher = pattern.matcher(input);
        if(matcher.find()){
            String matchedNumber = matcher.group();
            if(matchedNumber.equals("empty")){return 0;}
            if(matchedNumber.equals("full")){return 3000000;}
            String extractedNumber = matchedNumber.replaceAll(",", "");
            int numberValue = Integer.parseInt(extractedNumber);
            if(logging){System.out.println("Extracted Number: " + extractedNumber);}
            return numberValue;
        } else {
            if(logging){System.out.println("No number found in the input string.");};
            return 0;
        }
    }
    private void calcState(){
        Microbot.status = "Calculating State";
        if(logging){System.out.println("===========================CALC STATE FUNCTION CALLED===========================");}
        if(Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(2176)) == 0){ status = State.ENABLE_BUILDING_MODE; }
        else if(!(Rs2Inventory.hasItemAmount(furniture.getNotedPlankNameNeeded(),furniture.getPlankAmountNeeded(), true) || Rs2Inventory.hasItemAmount("Coins",10000, true))){status = State.LOGOUT;}
        else if(moneyBagTopUpNeeded()){status = State.FILL_MONEY_BAG;}
        else if(Rs2GameObject.findObjectByIdAndDistance(furniture.getBuiltID(),20) != null){status = State.DESTROY;}
        else if(!Rs2Inventory.hasItemAmount(furniture.getPlankNeeded(),(furniture.getPlankAmountNeeded()*2)+1) && (currentGameTick > gameTickLastSentButler + (butler.getTicksNeededToBank() + 2) )){status = State.SEND_BUTLER;}
        else if(Rs2GameObject.findObjectByIdAndDistance(furniture.getUnBuiltID(),20) != null && Rs2Inventory.hasItemAmount(furniture.getPlankNeeded(), furniture.getPlankAmountNeeded())){status = State.BUILDING;}
        if(logging){System.out.println("Calculating State: " + status.name());}
        if(logging){System.out.println("Current Game Tick: " + currentGameTick);}
        if(logging){System.out.println("Game Tick last sent butler: " + gameTickLastSentButler);}
        if(logging){System.out.println("Should send butler: " + (currentGameTick > gameTickLastSentButler + (butler.getTicksNeededToBank() + 2)));}
        if(logging){System.out.println("Calculating Noted Planks: " + Rs2Inventory.hasItemAmount(furniture.getNotedPlankNameNeeded(), furniture.getPlankAmountNeeded(), true));}
        if(logging){System.out.println("Calculating Coins: " + Rs2Inventory.hasItemAmount("Coins",10000, true));}
        if(logging){System.out.println("Need to refill money bag?: " + (moneyBagRefillThreshold >= coinsLeftInMoneyBag) );}
        if(logging){System.out.println("Refill amount: " + moneyBagRefillThreshold);}
        if(logging){System.out.println("Money Bag amount: " + coinsLeftInMoneyBag);}
    }
}
