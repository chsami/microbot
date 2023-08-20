package net.runelite.client.plugins.ogPlugins.ogfiremaking;

import net.runelite.api.NPC;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.ogPlugins.ogfiremaking.enums.FiremakingStatus;
import net.runelite.client.plugins.ogPlugins.ogfiremaking.enums.Logs;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.concurrent.TimeUnit;
public class firemakingScript extends Script {
    public static double version = 1.0;
    private boolean hasTinderBox() {return Inventory.hasItem("tinderbox");}
    private boolean doesNeedReturn(firemakingConfig config){ if(config.selectedLocation().getReturnPoints() != null){return true;} return false;}
    private boolean hasLogs(Logs getLogs){if (Inventory.hasItemAmount(getLogs.getName(),27)) {return true;} else{return false;}}
    private boolean isClosetoBanker(NPC banker){if(Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(banker.getWorldLocation()) <= 6){return true;} else {return false;}}
    private WorldPoint lastSpot = null;
    private WorldPoint secondToLastSpot = null; //Really didn't want to rewrite code lmaoooo
    private WorldPoint startPoint;
    private FiremakingStatus firemakingStatus = FiremakingStatus.FETCH_SUPPLIES;
    private Logs calcedLogs = null;
    public boolean run(firemakingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                calcedLogs =  progressLogsIfLevel(config);
                Microbot.status = firemakingStatus.toString();
                fetchSupplies(config, calcedLogs);
                goToSpot(config);
                burnShit(calcedLogs);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 300, TimeUnit.MILLISECONDS);
        return true;
    }
    private void fetchSupplies(firemakingConfig config, Logs getLog){
        //A lil bot like but will work on it. Will need to get Widget tabs and click on tab instead of scroll
        if(firemakingStatus == FiremakingStatus.FETCH_SUPPLIES){
            //Checks for logs
            if(!hasLogs(getLog)){
                //Open bank if not open
                int[] Bankers = config.selectedLocation().getBankers();
                NPC SelectedBanker = Rs2Npc.getNpc(Bankers[Random.random(0,Bankers.length)]);
                if(doesNeedReturn(config) && !isClosetoBanker(SelectedBanker)){
                    WorldPoint[] returnPoints = config.selectedLocation().getReturnPoints();
                    WorldPoint goBack = returnPoints[Random.random(0,returnPoints.length)];
                    Microbot.getWalker().walkCanvas(goBack);
                    sleepUntil(()-> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(goBack) <= 4);
                    sleep(1800,2500);
                }
                if(!Rs2Bank.isOpen()){

                    //Gets a random banker
                    sleepUntil(() -> Rs2Bank.openCertainBank(SelectedBanker));
                    callAFK(27,50,5000);
                    if(!hasTinderBox()){
                        if(!Rs2Bank.hasItem("tinderbox")){Microbot.getNotifier().notify("Get more tinderbox ya bum!");super.shutdown();}
                        Rs2Bank.withdrawItem("tinderbox");
                        sleepUntil(()-> Inventory.hasItem("tinderbox"));
                        sleep(30,80);
                    }
                    if(!hasLogs(getLog)){
                        if(!Rs2Bank.hasItem(getLog.getName())){Microbot.getNotifier().notify("Get more "+getLog.getName().toLowerCase()+" ya bum!");super.shutdown();}
                        Rs2Bank.withdrawItemAll(true, getLog.getName());
                        sleepUntil(() -> hasLogs(getLog));
                        callAFK(67,50,786);
                        Rs2Bank.closeBank();
                        sleep(30,80);
                    }
                }
            }
        }
        if(hasLogs(getLog) && hasTinderBox()){firemakingStatus = FiremakingStatus.FIND_EMPTY_SPOT;}
    }
    private void goToSpot(firemakingConfig config){
        if(firemakingStatus == FiremakingStatus.FIND_EMPTY_SPOT){
            WorldPoint[] startPositions = config.selectedLocation().getFiremakingStartingSpots();
            while(startPoint == lastSpot || startPoint == secondToLastSpot){startPoint = startPositions[Random.random(0,startPositions.length)];}
            Microbot.getWalker().walkFastMinimap(startPoint);
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(startPositions[0]) < 3);
            if (Rs2GameObject.findObject(ObjectID.FIRE_26185, startPoint) == null) {
             Microbot.getWalker().walkCanvas(startPoint);
             sleepUntil(()->Microbot.getClient().getLocalPlayer().getWorldLocation().equals(startPoint));
                firemakingStatus = FiremakingStatus.FIREMAKING;
                if(lastSpot != null){secondToLastSpot = lastSpot;}
                lastSpot = startPoint;
            }
            callAFK(19,50,97);
        }
    }
    private void burnShit(Logs getLog){
        if (firemakingStatus == FiremakingStatus.FIREMAKING) {
            if (!Inventory.hasItem(getLog.getName())) {
                firemakingStatus = FiremakingStatus.FETCH_SUPPLIES;
            }
            if (Rs2GameObject.findObject(ObjectID.FIRE_26185, Microbot.getClient().getLocalPlayer().getWorldLocation()) != null) {
                firemakingStatus = FiremakingStatus.FIND_EMPTY_SPOT;
            }
            while (firemakingStatus == FiremakingStatus.FIREMAKING) {
                if (!Inventory.hasItem(getLog.getName()))
                    break;
                if (Rs2GameObject.findObject(ObjectID.FIRE_26185, Microbot.getClient().getLocalPlayer().getWorldLocation()) != null) {
                    firemakingStatus = FiremakingStatus.FIND_EMPTY_SPOT;
                }
                Inventory.useItemUnsafe("tinderbox");
                Inventory.useItemUnsafe(getLog.getName());
                sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getPoseAnimation() == 819, 30000);
                sleep(30,80);
            }
            callAFK(34,50,400);
        }
    }
    private Logs progressLogsIfLevel(firemakingConfig config){
        if(config.getProgressionMode().isBoolAnswer()){
            int firemakingLevel = Microbot.getClient().getBoostedSkillLevel(Skill.FIREMAKING);
            if(firemakingLevel >= 90){return Logs.REDWOOD;}
            else if(firemakingLevel >= 75){return Logs.MAGIC;}
            else if(firemakingLevel >= 60){return Logs.YEW;}
            else if(firemakingLevel >= 50){return Logs.MAHOGANY;}
            else if(firemakingLevel >= 45){return Logs.MAPLE;}
            else if(firemakingLevel >= 42){return Logs.ARCTIC_PINE;}
            else if(firemakingLevel >= 35){return Logs.TEAK;}
            else if(firemakingLevel >= 30){return Logs.WILLOW;}
            else if(firemakingLevel >= 15){return Logs.OAK;}
            else if(firemakingLevel >= 1){return Logs.RED_LOGS;}
        } return config.selectedLogs();
    }
    private void callAFK(int chance, int min, int max){
        if(Random.random(1,chance) == 1){
            sleep(min,max);
        }
    }
}
