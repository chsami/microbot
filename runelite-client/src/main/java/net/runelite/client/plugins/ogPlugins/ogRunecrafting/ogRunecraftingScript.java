package net.runelite.client.plugins.ogPlugins.ogRunecrafting;

import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;


public class ogRunecraftingScript extends Script {

    public static double version = 1.0;

    private ogRunecraftingConfig config;
    private enum State {CRAFTING_RUNES,WALKING_TO_ALTER,RESTOCKING,WAITING}
    private State botState;
    private int bindingNeckAmmount; //VarPlayer 487
    private int gameTick;
    private int lastActionTick;
    private int pouchMaxCycle = 0; //Varplayer 262? or 261? VarclientInt 44?
    private int soundEffect;
    private boolean hasEssence() { return (Inventory.hasItem(config.selectRuneToMake().getEssenceTypeRequired()) || Inventory.hasItem(ItemID.DAEYALT_ESSENCE));}
    private WorldPoint playerLocation() {return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().getWorldLocation());}
    private boolean atAlter() {return playerLocation().distanceTo(config.selectAlter().getAlterLocation()) < 15;}
    private int distanceToRuin() {return playerLocation().distanceTo(config.selectAlter().getRuinLocation());}
    private void openChest() {Rs2Bank.openBank(Rs2GameObject.findObjectById(config.selectBank().getBankID()));}
    private void teletoCraftingGuild(){Tab.switchToEquipmentTab(); sleepUntil(()-> Tab.getCurrentTab() == InterfaceTab.EQUIPMENT);sleep(100,150);Rs2Widget.clickWidget(25362448);}
    private void teletoFireAlter(){Tab.switchToEquipmentTab(); sleepUntil(()-> Tab.getCurrentTab() == InterfaceTab.EQUIPMENT);sleep(200,300);Rs2Widget.clickWidget(25362456);}
    private void combinationCraft(){
        Inventory.useItemOnObjectFast(config.selectRuneToMake().getPrimaryRequiredRune(),config.selectAlter().getAlterID());
        sleepUntil(()-> !Inventory.hasItem(config.selectRuneToMake().getEssenceTypeRequired()));
    }
    private int getRunEnergy(){ return Integer.parseInt(Rs2Widget.getWidget(10485788).getText());}
    private int getStamEffect() {return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(Varbits.STAMINA_EFFECT));}
    private int getMagicImbue() {return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(Varbits.MAGIC_IMBUE));}
    private int getBindingNeckAmmount() {
        String i = (Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarpValue(487))).toString();
        snitch(i);
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarpValue(487));}
    private void waitNextTick(){sleepUntil(()-> gameTick > lastActionTick); lastActionTick = gameTick;}
    private void stamPotUp() {
        Rs2Bank.withdrawItem(true,"Stamina potion(1)");
        //Rs2Widget.clickChildWidget(786445,329);
    }
    private void snitch(String snitchStatement){if(config.getVerboseLogging()){System.out.println(snitchStatement);}}
    private boolean soundEffectCheck(){return soundEffect == 2710;}
    public boolean run(ogRunecraftingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

                if(this.config != config){this.config = config;bindingNeckAmmount = getBindingNeckAmmount();}
                calcState();
                if (botState == State.RESTOCKING){grabRunes();}
                else if (botState == State.WALKING_TO_ALTER){walkToAlter();}
                else if (botState == State.CRAFTING_RUNES){craftRunes();}



            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
    private void grabRunes() {

        if(playerLocation().distanceTo( new WorldPoint(2935,3280,0)) > 10){teletoCraftingGuild();sleepUntil(() -> playerLocation().distanceTo( new WorldPoint(2931,3286,0)) < 10);}
        if(Tab.getCurrentTab() != InterfaceTab.INVENTORY){Tab.switchToInventoryTab(); sleepUntil(()-> Tab.getCurrentTab() == InterfaceTab.INVENTORY);}
        if(!Rs2Bank.isOpen()){
            while(!Rs2Bank.isOpen()){
                openChest();
                sleepUntil(()-> Rs2Bank.isOpen(),Random.random(4000,6000));
            }
            //TODO Broken below
            if(!Rs2Widget.getWidget(786435).getText().contains("runecraft")){
                while(!Rs2Widget.childWidgetExits(786442,3)){Rs2Widget.clickChildWidget(786442,0);}
                waitNextTick();
                Rs2Widget.clickChildWidget(786442,3);
                waitNextTick();
            }
            if(Inventory.hasItem("fire rune")){/*Rs2Bank.depositAllFast("fire rune");*/Rs2Widget.clickChildWidget(983043,1);}
            if(Inventory.hasItem("lava rune")){
                Rs2Widget.clickChildWidget(983043,1);
                //Inventory.useItemUnsafe("Lava rune");
                //Rs2Bank.depositAllFast("lava rune");
            }
            if((config.keepStaminaActive() && getStamEffect() <= 10) || getRunEnergy() <= config.getMinStaminaEnergy()){stamPotUp();}
            if(!Rs2Equipment.hasEquipped(5521)){
                //Rs2Bank.withdrawAndEquipFast(5521); bindingNeckAmmount = 16;
            }
            if(bindingNeckAmmount < 4){
                //Rs2Bank.withdrawItem(false, "Binding Necklace" );sleepUntil(() -> Inventory.hasItem(ItemID.BINDING_NECKLACE), Random.random(601,620));
                //Rs2Widget.clickChildWidget(786445,478);
                Rs2Bank.withdrawItem(true,"Binding Necklace");
                sleep(240,300);
            }
            Rs2Bank.withdrawItem(true,"Pure essence");
            Rs2Widget.clickChildWidget(786445,477);
            sleep(240,300);
            Rs2Widget.clickChildWidget(983043,0);
            sleep(120,160);
            Rs2Widget.clickChildWidget(786445,477);
            sleep(240,300);
            Rs2Widget.clickChildWidget(983043,0);
            sleep(120,160);
            Rs2Widget.clickChildWidget(786445,477);
            sleep(240,300);
//            while(!Inventory.hasItemAmount(config.selectRuneToMake().getEssenceTypeRequired(), 23) && pouchMaxCycle < 2){
//                Rs2Widget.clickChildWidget(786445,474);
//                //sleepUntil(()-> Inventory.hasItemAmount(config.selectRuneToMake().getEssenceTypeRequired(), 23));
//                Inventory.useItemAction("Colossal pouch","Fill");
//                sleep(200,300);
//                pouchMaxCycle++;
//            } pouchMaxCycle = 0;
        }
        VirtualKeyboard.keyPress(KeyEvent.VK_ESCAPE);

        
    }
    private void walkToAlter() {
        while (distanceToRuin() > 30){
            teletoFireAlter();
            System.out.println("Tele to fire alter");
            if(Tab.getCurrentTab() != InterfaceTab.INVENTORY){Tab.switchToInventoryTab(); sleepUntil(()-> Tab.getCurrentTab() == InterfaceTab.INVENTORY);}
            sleepUntil(()-> distanceToRuin() < 30,4000);
        }
        sleepUntil(()-> distanceToRuin() < 30);
        //Microbot.getWalker().walkTo(config.selectAlter().getNextToAlter());
        //sleepUntil(() -> distanceToRuin() < 5);
        //Rs2GameObject.interact(config.selectAlter().getRuinAlterID());
        myInteract();

        sleep(80,120);
        if(Inventory.hasItem(ItemID.BINDING_NECKLACE)){
            Inventory.useItem("Binding Necklace");
            sleepUntil(()-> Rs2Widget.findWidget("Destroy necklace of binding?") != null);
            Rs2Widget.clickWidget("Yes");
            bindingNeckAmmount = getBindingNeckAmmount();
        }
        if(Inventory.hasItem("Stamina potion(1)")){if(Tab.getCurrentTab() != InterfaceTab.INVENTORY){Tab.switchToInventoryTab(); sleepUntil(()-> Tab.getCurrentTab() == InterfaceTab.INVENTORY);}Inventory.useItemAction("Stamina potion(1)","drink");}
        sleepUntil(() -> distanceToRuin() < Random.random(2,3));
        castMagicImbue();
        while(!atAlter()){
            //Rs2GameObject.interact(config.selectAlter().getRuinAlterID());
            myInteract();
            waitNextTick();waitNextTick();waitNextTick();if(Random.random(0,10) == 3){waitNextTick();}if(Random.random(0,10) == 3){waitNextTick();}}

    }

    private void myInteract() {
        TileObject alter = Rs2GameObject.findObjectById(config.selectAlter().getRuinAlterID());
        Microbot.getMouse().click(alter.getCanvasLocation().getX() + Random.randomDouble(-5,5),alter.getCanvasLocation().getY() + Random.randomDouble(-5,5));
    }

    private void craftRunes(){
        //sleepUntil(()-> atAlter() == true);
        //TODO Check if Magic imbue worked and if not recast
        if(config.selectRuneToMake().isCombinationRune() && hasEssence()){
            while(hasEssence() && pouchMaxCycle < 3){
                if(getMagicImbue() == 0){
                    castMagicImbue();
                    sleep(120,150);
                }
                combinationCraft();
                bindingNeckAmmount = getBindingNeckAmmount();
                pouchMaxCycle++;
                sleepUntil(()-> soundEffectCheck() || !hasEssence());
                Inventory.useItemAction("Colossal pouch","Empty");
            } pouchMaxCycle = 0;

        }
        if((Inventory.hasItem(5515) && config.useGiantPouch()) ||
                (Inventory.hasItem(5513) && config.useLargePouch()) ||
                (Inventory.hasItem(6819) && config.useLargePouch()) ||
                (Inventory.hasItem(5511) && config.useMediumPouch()) ||
                (Inventory.hasItem(26786) && config.useColossalPouch()) ||
                (Inventory.hasItem(26906) && config.useColossalPouch())
            ){
            sleep(100,120);
            repairPouches();
        }

    }

    private void calcState() {
        Tab.switchToInventoryTab();
        sleepUntil(() -> Tab.getCurrentTab() == InterfaceTab.INVENTORY);
        if(!hasEssence()){botState = State.RESTOCKING;}
        else if(hasEssence() && !atAlter()) {botState = State.WALKING_TO_ALTER;}
        else if(hasEssence() && atAlter()) {botState = State.CRAFTING_RUNES;}
        else {System.out.println("huuuuuuuh");}

    }
    private void repairPouches() {
        if(Tab.getCurrentTab() != InterfaceTab.MAGIC){Tab.switchToMagicTab(); sleepUntil(()-> Tab.getCurrentTab() == InterfaceTab.MAGIC);}
        Rs2Widget.clickWidget(14286953);
        sleepUntil(() -> Rs2Widget.getWidget(4915212) != null,10000);
        Rs2Widget.clickWidget(4915212);
        sleepUntil(()-> Rs2Widget.findWidget("Click here to continue") != null);
        sleep(60,100);
        //VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
        Rs2Widget.clickWidget("Click here to continue");
        sleepUntil(()-> Rs2Widget.findWidget("Can you repair my pouches?") != null);
        sleep(60,100);
        //VirtualKeyboard.keyPress(KeyEvent.VK_1);
        Rs2Widget.clickWidget("Can you repair my pouches?");
        sleepUntil(()-> Rs2Widget.findWidget("Click here to continue") != null);
        sleep(60,100);
        //VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
        Rs2Widget.clickWidget("Click here to continue");
        sleep(60,100);
        //VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
        Rs2Widget.clickWidget("Click here to continue");
        if(Tab.getCurrentTab() != InterfaceTab.INVENTORY){Tab.switchToInventoryTab(); sleepUntil(()-> Tab.getCurrentTab() == InterfaceTab.INVENTORY);}


    }
    private void castMagicImbue(){
        if(Tab.getCurrentTab() != InterfaceTab.MAGIC){Tab.switchToMagicTab(); sleepUntil(()-> Tab.getCurrentTab() == InterfaceTab.MAGIC);}
        Rs2Widget.clickWidget(14286973);
        sleep(120,180);
        if(Tab.getCurrentTab() != InterfaceTab.INVENTORY){Tab.switchToInventoryTab(); sleepUntil(()-> Tab.getCurrentTab() == InterfaceTab.INVENTORY);}
    }
    void onGameTick(GameTick gameTick){
        this.gameTick++;
    }
    void onSoundEffectPlayed(SoundEffectPlayed event){
        this.soundEffect =  event.getSoundId();
    }
}
//                Rs2Bank.withdrawAllAndEquipFast(ItemID.RUBY_DRAGON_BOLTS_E);
//                Rs2Bank.withdrawFast(ItemID.ANTIVENOM4_12913);