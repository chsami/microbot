package net.runelite.client.plugins.ogPlugins.ogblastfurnace;


import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.plugins.microbot.util.walker.Walker;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;



public class ogBlastFurnaceScript extends Script {

    public static double version = 1.0;
    private enum State{LOADING_ORE,RETRIEVING_ORE,BANKING,TOMUCHSUPLIES}
    private State botState;
    private final int[] blastFurnaceWorlds = new int[] {355,356,357,358,386,381,395,424,466,494,495,496,515,516};
    private final WorldPoint topOfConveyorBelt = new WorldPoint(1942,4967,0);
    private final WorldPoint[] nextToBarDespensor = new WorldPoint[] { new WorldPoint(1939,4963,0), new WorldPoint(1940,4962,0)};
    //Why is ObjectID.BAR_DISPENSER set 9093, object ids need update maybe?
    private WorldPoint playerLocation(){return Microbot.getClient().getLocalPlayer().getWorldLocation();}
    private int getBFGoldBars() {return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(Varbits.BLAST_FURNACE_GOLD_BAR));}
    private int getBFGoldOres() {return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(Varbits.BLAST_FURNACE_GOLD_ORE));}
    private int getBFDispenserState() {return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(Varbits.BAR_DISPENSER));}
    private void iceGlovesEquip(){if(Rs2Equipment.hasEquipped(ItemID.ICE_GLOVES)){return;} Rs2Equipment.equipItemFast(ItemID.ICE_GLOVES); sleepUntil(() -> Rs2Equipment.hasEquipped(ItemID.ICE_GLOVES));}
    private void goldGlovesEquip(){if(Rs2Equipment.hasEquipped(ItemID.GOLDSMITH_GAUNTLETS)){return;} Rs2Equipment.equipItemFast(ItemID.GOLDSMITH_GAUNTLETS); sleepUntil(() -> Rs2Equipment.hasEquipped(ItemID.GOLDSMITH_GAUNTLETS));}
    private void openChest() {Rs2Bank.openCertainBank(Rs2GameObject.findObjectById(ObjectID.BANK_CHEST_26707)); /*implement randX and randY currently does nothing with values*/ }
    private boolean playerAtRetrieveLocation() { return Arrays.asList(nextToBarDespensor).contains(playerLocation());}
    private int getRunEnergy(){ return Integer.parseInt(Rs2Widget.getWidget(10485788).getText());}
    private int getStam() {return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(Varbits.STAMINA_EFFECT));}
    private void grabStam() {
        //Rs2Bank.withdrawItem("Stamina potion(1)");
        Microbot.getMouse().click(Rs2Widget.findWidget("Stamina potion(1)").getBounds());
        sleepUntil(() -> Inventory.hasItem(ItemID.STAMINA_POTION1),2000);
        Rs2Widget.clickChildWidget(983043,1);
    }

    public boolean run(ogBlastFurnaceConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                calcState();
                if(botState == State.TOMUCHSUPLIES) {depositOverflow();}
                else if(botState == State.BANKING){restock();}
                else if(botState == State.LOADING_ORE){loadConveyor();}
                else if(botState == State.RETRIEVING_ORE){retrieveBars();}




            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, Random.random(0,50), TimeUnit.MILLISECONDS);
        return true;
    }
    private void calcState(){
        Microbot.status = "Calculating State";
        if(Inventory.isFull() && getBFGoldOres() > 20 && getBFGoldBars() > 20){botState = State.TOMUCHSUPLIES;}
        else if(Inventory.hasItemAmount(ItemID.GOLD_BAR, 20) || ((getBFGoldBars() < 26 || getBFGoldOres() < 26) && !Inventory.hasItem(ItemID.GOLD_ORE))){botState = State.BANKING;}
        else if(getBFGoldBars() >= 26 && getBFGoldOres() >= 26){botState = State.RETRIEVING_ORE;}
        else if(Inventory.hasItemAmount(ItemID.GOLD_ORE,26)) {botState = State.LOADING_ORE;}
    }
    private void loadConveyor() {
        Microbot.status = "Loading Conveyor";
        if(Inventory.hasItem(ItemID.GOLD_ORE)){
            Rs2GameObject.interact(9100);
            sleepUntil(() -> !Inventory.hasItem(ItemID.GOLD_ORE), 20000);
            callAFK(36,1000,3000);
            Microbot.status = "Loaded Conveyor";
            if(!Inventory.hasItem(ItemID.GOLD_ORE)){
                if(Random.random(1,5) == 3){
                    iceGlovesEquip();
                    sleep(120,200);
                    Microbot.getWalker().walkCanvas(new WorldPoint(1940,4962,0));
                    callAFK(36,1000,6183);
                    sleepUntil(() -> playerAtRetrieveLocation());
                    sleep(120,200);
                } else {
                    Microbot.getWalker().walkCanvas(new WorldPoint(1940,4962,0));
                    callAFK(38,1000,6258);
                    sleepUntil(() -> playerAtRetrieveLocation());
                    sleep(120,200);
                    iceGlovesEquip();
                    sleep(120,200);
                }
            }

        }
    }
    private void retrieveBars() {
        //Color color = dispenserState == 2 && hasIceGloves ? Color.GREEN : (dispenserState == 3 ? Color.GREEN : Color.RED);
        Microbot.status = "Retrieving Bars";
        sleep(140,170);
        iceGlovesEquip();
        System.out.println("ice gloves");
        if(getBFDispenserState() == 1){sleepUntil(() -> getBFDispenserState() == 2 || getBFDispenserState() == 3);}
        Rs2GameObject.interact(9092);
        sleepUntil(() -> Rs2Widget.findWidget("How many would you like to take?") != null);
        VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
        sleep(40,90);
        goldGlovesEquip();
        sleep(120,200);
        sleepUntil(() -> Inventory.hasItem(ItemID.GOLD_BAR));
    }
    private void restock() {
        openChest();
        callAFK(27,1000,6000);
        sleepUntil(() -> Rs2Bank.isOpen());
        if( Rs2Bank.isOpen()) {
            if(Inventory.hasItem(ItemID.GOLD_BAR)){ Microbot.getMouse().click(Inventory.findItem(ItemID.GOLD_BAR).getBounds());}
            if(Rs2Widget.getChildWidgetSpriteID(786443, 5) != 1079 ){Rs2Widget.clickChildWidget(786443, 5);}
            if( getStam() <= 10|| getRunEnergy() <= 40){grabStam();}
            Microbot.getMouse().click(Rs2Widget.findWidgetExact("Gold ore").getBounds());
            //Rs2Bank.withdrawItem("Gold ore");
            sleepUntil(() -> Inventory.hasItem(ItemID.GOLD_ORE));
            sleep(50,80);
            if(Random.random(0,10) == 3){VirtualKeyboard.keyPress(KeyEvent.VK_ESCAPE);}
        }
    }
    private void depositOverflow() {
        openChest();
        sleepUntil(() -> Rs2Bank.isOpen());
        Rs2Widget.clickChildWidget(983043,Random.random(3,27));
        sleep(50,80);
        VirtualKeyboard.keyPress(KeyEvent.VK_ESCAPE);

    }
    private void callAFK(int chance, int min, int max){
        Microbot.status = "Called an AFK";
        if(Random.random(1,chance) == 1){
            sleep(min,max);
        }
    }
}
