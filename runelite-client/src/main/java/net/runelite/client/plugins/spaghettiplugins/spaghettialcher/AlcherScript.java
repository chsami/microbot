package net.runelite.client.plugins.spaghettiplugins.spaghettialcher;

import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import java.util.concurrent.TimeUnit;


public class AlcherScript extends Script {

    private AlcherState botState;

    public static double version = 1.0;

    private Items item;

    boolean orState;

    AlcherState stateToOr;

    Widget alchSpellWidget;

    String itemList;

    String[] itemListArray;

    public boolean run(AlcherConfig config) {

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if(item == Items.LIST){
                    itemListArray = itemList.split(",");
                    System.out.println(itemListArray[0]);
                }
                setupConfig(config);
                calcState();
                afkBreak(128, 2000, 20000);
                switch(botState){
                    case ALCH_FIXED_SPOT:
                        Rs2Magic.highAlch();
                        break;
                    case ALCHING:
                        alchItem();
                        break;
                    case BOT_DONE:
                        shutdown();
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0,  300, TimeUnit.MILLISECONDS);
        return true;
    }

    private void alchItem() {
        alchSpellWidget = Rs2Widget.getWidget(14286888);
        if (alchSpellWidget == null) return;
        Point point = new Point((int) alchSpellWidget.getBounds().getCenterX(), (int) alchSpellWidget.getBounds().getCenterY());
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Rs2Tab.getCurrentTab() == InterfaceTab.MAGIC), 5000);
        sleep(300, 600);
        Microbot.getMouse().click(point);
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Rs2Tab.getCurrentTab() == InterfaceTab.INVENTORY), 5000);
        if(item == Items.AUTOMATIC){
            item = findItems();
            if(item == null){
                System.out.println("No items found!");
                shutdown();
            }
        }
        if(item == Items.LIST){
            String listItem = findItemsInArray(itemListArray);
            if(listItem == null) shutdown();
            sleep(300,600);
            Inventory.useItemAction(listItem, "cast");
            return;
        }
        sleep(300, 600);
        Inventory.useItemAction(item.getName(), "cast");
    }

    private void setupConfig(AlcherConfig cfg){
        this.item = cfg.Item();
        if(this.botState == null)this.botState = AlcherState.ALCHING;
        this.itemList = cfg.ItemList();
        this.orState = cfg.overrideState();
        this.stateToOr = cfg.stateToOverrideWith();
    }
    private void calcState(){
        if(orState) botState = stateToOr;
    }

    private String findItemsInArray(String[] itemListArray){
        for(String str : itemListArray){
            if(Inventory.contains(str.toLowerCase())){
                return str.toLowerCase();
            }
        }
        return null;
    }
    private Items findItems(){
        for (Items i: Items.values()) {
            if(Inventory.contains(i.getName())){
                return i;
            }
        }
        return null;
    }

    private void afkBreak(int chance, int min, int max){
        if(Random.random(1,chance) == 1){
            Microbot.status = "AFK...";
            sleep(min,max);
        }
    }
}
