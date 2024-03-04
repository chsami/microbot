package net.runelite.client.plugins.ogPlugins.ogPrayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.ObjectID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.ogPlugins.ogPrayer.enums.Bones;
import net.runelite.client.plugins.ogPlugins.ogPrayer.enums.Locations;
import net.runelite.client.plugins.ogPlugins.ogPrayer.enums.RestockMethod;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ogPrayerScript extends Script {

    public static double version = 1.0;
    private ogPrayerConfig currentConfig;
    private int delayMin;
    private int delayMax;
    private int delayChance;
    private int afkMin;
    private int afkMax;
    private int afkChance;
    private Locations location;
    private Bones bones;
    private RestockMethod restockMethod;
    private boolean oneTick;
    private List<String> preferredPOHs = new ArrayList<>();
    private List<String> badPOHs = new ArrayList<>();
    private boolean usePersonalPOH;
    private POHs currentHouse;
    private int bankPin;
    private boolean logging;
    private int gameTick = 0;
    private int lastActionTick;
    public enum ogPrayerStatus  { GO_TO_ALTER , USE_BONES_ON_ALTER , RESTOCK , LOGOUT }
    public static ogPrayerStatus status = ogPrayerStatus.GO_TO_ALTER;
    private boolean needToHop = false;
    private int playersInArea;
    private List<String> worldHopList = new ArrayList<>();
    private Queue<Integer> last3Worlds = new LinkedList<>();
    private List<POHs> listOFPOHs = new ArrayList<>();
    private final WorldPoint[] nextToChaosAlterTiles = new WorldPoint[]{new WorldPoint(2948,3820,0), new WorldPoint(2948,3820,0), new WorldPoint(2949,3821,0),new WorldPoint(2949,3821,0)};
    private boolean inPVPArea() {return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(Varbits.IN_WILDERNESS)) == 1 || Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(Varbits.PVP_SPEC_ORB)) == 1;}
    private boolean isAtAlter(){
        if(location == Locations.CHAOS_ALTAR){
            for (WorldPoint nextToChaosAlterTile : nextToChaosAlterTiles) {
                if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(nextToChaosAlterTile) == 1) {
                    return true;
                }
            }
        }
        else {
            for(int i = 0; i < location.getAlterID().length; i++){
                if(Rs2GameObject.findObjectByIdAndDistance(location.getAlterID()[i], 3) != null) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isNearAlter(){ for(int i = 0; i < location.getAlterID().length; i++){ if(Rs2GameObject.findObjectByIdAndDistance(location.getAlterID()[i], 20) != null) { return true;} } return false;}
    private int checkVarbit(int v) {return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(v));}
    private boolean isItemSelected(){return Microbot.getClient().isWidgetSelected();}
    private int selectWorldFromList(){
        int selectedWorld;
        do{selectedWorld = Integer.parseInt(worldHopList.get(Random.random(0, worldHopList.size())));
        } while (last3Worlds.contains(selectedWorld));
        last3Worlds.add(selectedWorld);
        log("Last 3 worlds: " + last3Worlds.toString());
        if(last3Worlds.size() > 3){if(logging){System.out.println("World removed: " + last3Worlds.poll());}else{last3Worlds.poll();}}
        log("Selected world: " + selectedWorld);
        return selectedWorld;
    }
    private boolean hasNotedBonesIfNeeded(){ Rs2Tab.switchToInventoryTab() ;if(restockMethod.getRestockType() == RestockMethod.RestockType.NOTING){ return Rs2Inventory.hasItem(bones.getNotedID()); } return true; } //Redo this cause this logic is stupid
    private void openChaosDoorsIfNeeded(){
        log("----------------------------------OPEN CHAOS DOOR FUNCTION WAS CALLED----------------------------------");
        if(Rs2GameObject.findDoor(1525) != null || Rs2GameObject.findDoor(1522) != null){return;}
        if(Rs2GameObject.findDoor(1524) != null || Rs2GameObject.findDoor(1521) != null){
            if(Random.random(1,Random.random(5,10)) == 3){
                Rs2GameObject.interact(Rs2GameObject.findDoor(1524));
            } else{Rs2GameObject.interact(Rs2GameObject.findDoor(1521));}
            sleepUntil(()-> Rs2GameObject.findDoor(1525) != null || Rs2GameObject.findDoor(1522) != null,Random.random(4500,6000));
        }
    }
    private void log(String logMessage){ if(logging){ System.out.println(logMessage); } }
    private void populatePOHList(){
        log("----------------------------------POPULATE POH FUNCTION WAS CALLED----------------------------------");
        int count = 0;
        for(int i = 1; i < 30; i++){
            if(Rs2Widget.getWidget(3407881).getChild(i).getText().length() > 1){
                count++;
                if(Rs2Widget.getChildWidgetText(3407885, i).toLowerCase().contains("y") && !this.badPOHs.contains(Rs2Widget.getChildWidgetText(3407881,i))) {
                    listOFPOHs.add(new POHs(i,
                            Rs2Widget.getChildWidgetText(3407881,i),
                            Rs2Widget.getChildWidgetText(3407885, i).toLowerCase().contains("y"),
                            Integer.parseInt(Rs2Widget.getChildWidgetText(3407887, i)),
                            Integer.parseInt(Rs2Widget.getChildWidgetText(3407888, i)),
                            Rs2Widget.getChildWidgetText(3407891, i).isEmpty()));
                }
            }
        }log("----------------------------------Added "+count+" Houses ---------------------------------");
        if(logging){
            for(POHs pohs : listOFPOHs){ log("Name: " + pohs.ownerName + ". Alter: " + pohs.hasAlter + ". Jewellery Tier: "+pohs.jewelleryBoxTier+". Pool Tier: "+pohs.poolTier+". Is Here: "+pohs.isHere); }
        }
    }
    private void leavePOH() {
        log("----------------------------------LEAVE FUNCTION WAS CALLED----------------------------------");
        if(restockMethod.getRestockType() == RestockMethod.RestockType.NOTING){
            if(Rs2GameObject.findObjectByIdAndDistance(ObjectID.PORTAL_4525,20) != null){
                Rs2GameObject.interact(ObjectID.PORTAL_4525, "Enter");
                sleepUntil(()-> Rs2GameObject.findObjectByIdAndDistance(ObjectID.PORTAL_4525,20) == null);
            }
        }
    }
    private void callDelay(){
        if(Random.random(1,this.delayChance) == 3) {
            int delayGeneratedMin = Random.random(this.delayMin - 20, this.delayMin);
            int delayGeneratedMax = Random.random(this.delayMax , this.delayMax + 20);
            int delayGenerated = Random.random(delayGeneratedMin,delayGeneratedMax);
            log("Delay range of: " + delayGeneratedMin + "-" + delayGeneratedMax + " generated a delay of " + delayGenerated + "ms.");
            sleep(delayGenerated);
        } else {
            int delayGenerated = Random.random(delayMin,delayMax);
            log("Delay range of: " + this.delayMin + "-" + this.delayMax + " generated a delay of " + delayGenerated + "ms.");
            sleep(delayGenerated);
        }
    }
    private void callAFK(){
        if(Random.random(0,this.afkChance) == 3) {
            int afkGenerated = Random.random( this.afkMin * 600 , this.afkMax * 600 );
            log("AFK range of: " + this.afkMin + "-" + this.afkMax + " generated a delay of " + afkGenerated + "minutes.");
            sleep(afkGenerated);
        }
    }
    public boolean run(ogPrayerConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                logging = true;
                log("Script rerunning.");
                setSettings(config);
                calcState();
                detectPvpPlayer();
                if( status == ogPrayerStatus.GO_TO_ALTER ){goToAlter();}
                else if( status == ogPrayerStatus.USE_BONES_ON_ALTER ){useBonesOnAltar();}
                else if( status == ogPrayerStatus.RESTOCK ){restock();}
                else if( status == ogPrayerStatus.LOGOUT ){
                    Rs2Tab.switchToLogout();
                    sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.LOGOUT);
                    sleep(30,80);
                    Rs2Widget.clickWidget("Click here to logout");
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
        return true;
    }
    public void detectPvpPlayer() {
        this.playersInArea =  Microbot.getClient().getPlayers().size();
        if(Microbot.getClient().getPlayers().size() > 1){
            log("===========================DETECTED THAT I NEED TO HOP===========================");
            needToHop = true;
            //if(Microbot.getClient().getLocalPlayer().isInteracting()){}
            //For now turn on attack players to right-click just in case one spawns under you
            Microbot.getMouse().click(Microbot.getClient().getLocalPlayer().getWorldLocation().getX(),Microbot.getClient().getLocalPlayer().getWorldLocation().getY());
            sleep(80,120);
            Microbot.hopToWorld(selectWorldFromList());
        } else {needToHop = false; Rs2Tab.switchToInventoryTab();}
    }
    public void onGameTick(GameTick gameTick){
        this.gameTick++;
    }


    private void setSettings(ogPrayerConfig config){
        log("Checking for updates in settings");
        if(this.currentConfig != config){this.currentConfig = config; log("Updated main config"); }
        if(this.delayMin != config.delayMin() ){ this.delayMin = config.delayMin(); log("Updated Delay Min: " + this.delayMin);}
        if(this.delayMax != config.delayMax()){ this.delayMax = config.delayMax(); log("Updated Delay Max: " + this.delayMax);}
        if(this.delayChance != config.delayChance()){ this.delayChance = config.delayChance(); log("Updated Delay Deviation Chance: " + this.delayChance);}
        if(this.afkMin != config.afkMin()){ this.afkMin = config.afkMin(); log("Updated AFK Min: " + this.afkMin);}
        if(this.afkMax != config.afkMax()){ this.afkMax = config.afkMax(); log("Updated AFK Max: " + this.afkMax);}
        if(this.afkChance != config.afkChance()){ this.afkChance = config.afkChance(); log("Updated AFK Chance: " + this.afkChance);}
        if(this.location != config.selectedLocation()){this.location = config.selectedLocation(); log("Updated location: " + this.location.getName());}
        if(this.bones != config.selectedBones()){this.bones = config.selectedBones(); log("Updated bones: " + this.bones.getName());}
        if(this.restockMethod != config.selectedRestockMethod()){this.restockMethod = config.selectedRestockMethod(); log("Updated restock method: " + this.restockMethod.name() + " - " + this.restockMethod.getRestockType());}
        if(this.oneTick != config.selectedTickOption()){this.oneTick = config.selectedTickOption(); log("Updated One Tick: " + this.oneTick);}
        if(!Objects.equals(this.preferredPOHs, new ArrayList<>(Arrays.asList(config.preferredPOH().split(","))))){this.preferredPOHs = Arrays.asList(config.preferredPOH().split(",")); log("Updated preferred POH: " + this.preferredPOHs);}
        if(!Objects.equals(this.badPOHs, new ArrayList<>(Arrays.asList(config.bannedPOHs().split(","))))){this.badPOHs = Arrays.asList(config.bannedPOHs().split(","));  log("Updated banned POHs: " + this.badPOHs); }
        if(this.usePersonalPOH != config.usePersonalPOH()){this.usePersonalPOH = config.usePersonalPOH(); log("Updated Use POH: " + this.usePersonalPOH); }
        if(!config.getBankPin().isEmpty() && Integer.parseInt(config.getBankPin()) != this.bankPin && config.getBankPin().length() == 4 && NumberUtils.isDigits(config.getBankPin())){this.bankPin = Integer.parseInt(config.getBankPin()); log("Updated Bank Pin: " + this.bankPin); }
        if(!config.worldHopList().isEmpty() && !Objects.equals(this.worldHopList, new ArrayList<>(Arrays.asList(config.worldHopList().split(","))))){this.worldHopList = Arrays.asList(config.worldHopList().split(","));  log("Updated Worlds: " + this.worldHopList); }
        if(this.logging != config.verboseLogging()){ if(logging || config.verboseLogging()){System.out.println("Updated Verbose Logging: " + config.verboseLogging() );}  this.logging = config.verboseLogging(); }
    }
    private void calcState(){
        if(Microbot.getClient().getGameState() != GameState.LOGIN_SCREEN){
            if(location == Locations.GILDED_ALTAR){sleepUntil(()-> checkVarbit(6719) == 0,Random.random(1300,1500));}
            if(location == Locations.CHAOS_ALTAR && inPVPArea()){sleepUntil(() -> this.playersInArea == 1, Random.random(20000,25000));}
            if(Rs2Tab.switchToInventoryTab()){
                if(location == Locations.CHAOS_ALTAR || location == Locations.GILDED_ALTAR){
                    if(Rs2Inventory.hasItem(bones.getItemID()) && isNearAlter() && !needToHop){ status = ogPrayerStatus.USE_BONES_ON_ALTER; }
                    else if (Rs2Inventory.hasItem(bones.getItemID()) && !isNearAlter() && !needToHop){ status = ogPrayerStatus.GO_TO_ALTER; }
                    else if (!Rs2Inventory.hasItem(bones.getItemID()) && hasNotedBonesIfNeeded() && !needToHop) { status = ogPrayerStatus.RESTOCK; }
                    else if (!needToHop && (!Rs2Inventory.hasItem(bones.getName()) || !Rs2Inventory.hasItemAmount("Coins",1000, true)) && !Rs2Inventory.hasItem(bones.getItemID()) ) { status = ogPrayerStatus.LOGOUT; }
                }
                if(!status.name().isEmpty()){log("Calculating State: " + status.name());}
                log("Bones in inventory:  " + Rs2Inventory.hasItem(bones.getItemID()));
                log("Has noted bones if needed:  " + hasNotedBonesIfNeeded());
                log("Near altar:  " + isAtAlter());
                log("Need to hop: " + needToHop);
            }
        }
    }
    private void goToAlter(){
        log("===========================GO TO ALTAR FUNCTION CALLED===========================");
        if(location == Locations.GILDED_ALTAR || location == Locations.CHAOS_ALTAR){
            if(location == Locations.GILDED_ALTAR && Rs2GameObject.findObjectByIdAndDistance(ObjectID.HOUSE_ADVERTISEMENT, 20) != null && !usePersonalPOH){findHouse();}
            else if (location == Locations.CHAOS_ALTAR){openChaosDoorsIfNeeded();}
        }
    }
    private void useBonesOnAltar(){
        log("===========================USE BONES ON ALTAR FUNCTION CALLED===========================");
        if(oneTick){
            boolean usedItem = false;
            while(Rs2Inventory.hasItem(bones.getItemID()) && !needToHop){
                if(location == Locations.CHAOS_ALTAR && !isAtAlter()){openChaosDoorsIfNeeded();}
                if(isItemSelected()){
                    if(location == Locations.GILDED_ALTAR) { Rs2GameObject.interact("Altar", "use"); lastActionTick = gameTick; usedItem = true;}
                    if(location == Locations.CHAOS_ALTAR) { Rs2GameObject.interact("Chaos Altar", "use"); lastActionTick = gameTick; usedItem = true;}
                }
                callDelay();
                while(!isItemSelected() && Rs2Inventory.hasItem(bones.getItemID()) && !needToHop){
                    Rs2Inventory.use(bones.getItemID());
                }
                if(!isAtAlter()){
                    if(usedItem){sleepUntil(this::isAtAlter, Random.random(5000,6000)); log("Going to altar");}
                } else { if(usedItem){sleepUntil(()-> gameTick > lastActionTick, Random.random(600,700));log("Waiting a tick");} }
                usedItem = false;
            }
        } else {
            useBones();
            sleepUntil(() -> !Microbot.isGainingExp); log("Player stopped gaining xp");
        }
    }
    private void useBones() {
        callDelay();
        while(!isItemSelected() && Rs2Inventory.hasItem(bones.getItemID()) && !needToHop){Rs2Inventory.useLast(bones.getItemID());}
        callDelay();
        if(isItemSelected()){
            if(location == Locations.GILDED_ALTAR) { Rs2GameObject.interact("Altar", "use"); }
            if(location == Locations.CHAOS_ALTAR) { Rs2GameObject.interact("Chaos Altar", "use"); lastActionTick = gameTick;}
        }
    }

    private void restock(){
        log("===========================RESTOCK FUNCTION CALLED===========================");
        if(restockMethod == RestockMethod.ELDER_CHAOS_DRUID || restockMethod == RestockMethod.PHIALS){
            if(restockMethod == RestockMethod.ELDER_CHAOS_DRUID) { openChaosDoorsIfNeeded(); }
            else { leavePOH(); }
            log("Using noted bones");
            if(isItemSelected()){Microbot.getMouse().click(Microbot.getClient().getLocalPlayer().getWorldLocation().getX(),Microbot.getClient().getLocalPlayer().getWorldLocation().getY());}
            Rs2Inventory.use(bones.getNotedID());
            sleep(80,160);
            log("Clicking restocker");
            Rs2Npc.interact(restockMethod.getID(),"use");
            log("Looking for Select an Option");
            sleepUntil(()-> Rs2Widget.findWidget("Select an Option") != null);
            if(Rs2Widget.findWidget("Select an Option") != null){
                callDelay();
                Rs2Widget.clickWidget("Exchange All:");
                log("Looking for Click here to continue");
                sleepUntil(()-> Rs2Widget.findWidget("Click here to continue") != null);
            }
            if(Rs2Widget.findWidget("Click here to continue") != null){ callDelay(); Rs2Widget.clickWidget("Click here to continue");}
        }
    }
    private void findHouse(){
        log("===========================FIND HOUSE FUNCTION CALLED===========================");
        if(Rs2Widget.findWidget("House Advertisement") == null) { Rs2GameObject.interact(Rs2GameObject.findObjectByIdAndDistance(29091, 20), "View"); }
        sleepUntil(()-> Rs2Widget.findWidget("House Advertisement") != null);
        callDelay();
        if(listOFPOHs.isEmpty() && Rs2Widget.findWidget("House Advertisement") != null){ populatePOHList(); }
        if(!listOFPOHs.isEmpty() && currentHouse == null){
            for(POHs house : listOFPOHs){if(preferredPOHs.contains(house.ownerName) && currentHouse == null){ currentHouse = house; listOFPOHs.remove(currentHouse);log("Found preferred house: " + currentHouse.ownerName);}}
            if(currentHouse == null){currentHouse = listOFPOHs.get(1); listOFPOHs.remove(currentHouse); log("Found a house: " + currentHouse.ownerName);}
        }
        if(currentHouse != null){ Rs2Widget.clickChildWidget(3407891, currentHouse.index); log("Going into POH"); sleepUntil(()-> checkVarbit(6719) == 2,Random.random(1300,1500));sleepUntil(()-> checkVarbit(6719) == 0,Random.random(1300,1500));}
    }
}
@Getter
@AllArgsConstructor
class POHs {
    int index;
    String ownerName;
    boolean hasAlter;
    int jewelleryBoxTier;
    int poolTier;
    boolean isHere;
}


// Portal - 15479
//inside house portal 4525
// House Advertisement - 29091
// Phials -1614 5 coins
//Gilded Altar 13199 13197

/*
    Elder Chaos druid = 7995 NPC 50 coins
    Chaos Alter = 411 GameObject
                Open    Closed
    North Door  1525    1524
    South Door  1522    1521
     */
//2948, 3821, 0
//2948, 3820, 0

//Inventory.useItemAction("superior dragon bones", "use");
//        Rs2GameObject.interact("chaos altar","Use");


/*
House Advertisement
Name Widget = 3407881          Text
Alter Y/N Widget = 3407885     Y
Jewellery 0-3 Widget = 3407887 3
Enter House Widget = 3407891
 */