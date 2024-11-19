package net.runelite.client.plugins.microbot.gabplugs.glassmake;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.plugins.menuentryswapper.MenuEntrySwapperConfig;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.magic.Rs2Spells;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.misc.Rs2UiHelper;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.gabplugs.glassmake.GabulhasGlassMakeInfo.botStatus;
import static net.runelite.client.plugins.microbot.gabplugs.glassmake.GabulhasGlassMakeInfo.states;
import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

@Slf4j
public class GabulhasGlassMakeScript extends Script {
    public static double version = 1.0;
    @Inject
    private Notifier notifier;

    private GabulhasGlassMakeInfo.items currentItem;

    public boolean run(GabulhasGlassMakeConfig config) {
       currentItem= config.ITEM();
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                switch (botStatus) {
                    case Starting:

                        botStatus=states.Banking;
                        break;
                    case Banking:
                        banking();
                        botStatus=states.Glassblowing;
                        break;
                    case Glassblowing:
                        glassblowing();
                        botStatus=states.Picking;
                        break;
                    case Picking:
                        picking();
                        botStatus=states.Banking;
                        break;

                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private void takeBreak() {
        if(Rs2Random.nextInt(0, 20, 1, true) == 30) {
                   sleep(1000, 20000);
        }


    }

    private void banking() {
        takeBreak();


        while(!Rs2Bank.isOpen()){
            Rs2Bank.openBank();
            sleep(100, 200);
        }
        Rs2Bank.depositAll("Molten Glass");
        sleepUntil(() -> !Rs2Inventory.contains("Molten Glass"),  100);
        if(currentItem== GabulhasGlassMakeInfo.items.GiantSeaweed ) {
            if(Rs2Bank.count("Giant seaweed") < 3 || Rs2Bank.count("Bucket of sand") < 3) {
                notifier.notify("Out of materials");
                while(super.isRunning()) {
                    sleep(1000);
                }
            }

            for(int i =0 ; i <3; i++)   {
                Rs2Bank.withdrawOne("Giant seaweed");
            }

            Rs2Bank.withdrawX("Bucket of sand", 18);
        } else {
            if(Rs2Bank.count("Seaweed") < 3 || Rs2Bank.count("Bucket of sand") < 3) {
                notifier.notify("Out of materials");
                while(super.isRunning()) {
                    sleep(1000);
                }
            }

            Rs2Bank.withdrawX("Bucket of sand", 13);
            Rs2Bank.withdrawX(401, 13);


        }


        sleep(60, 100);
        Rs2Bank.closeBank();
        while (Rs2Bank.isOpen()){
            sleep(40, 100);
        }


    }

    private void glassblowing(){
        Rs2Tab.switchToMagicTab();
        sleep(60, 100);
        superglassmake();
        sleep(60, 100);
        sleepUntil(()-> Rs2Inventory.contains("Molten Glass"), 100);
    }

    private void superglassmake() {
        sleepUntil(() -> {
            Rs2Tab.switchToMagicTab();
            sleep(50, 150);
            return Rs2Tab.getCurrentTab() == InterfaceTab.MAGIC;
        });
        Widget superglass = Rs2Widget.findWidget(MagicAction.SUPERGLASS_MAKE.getName());
        if (superglass.getSpriteId() == 1972) {
            Microbot.click(superglass.getBounds());
        } else {
            superglass = Rs2Widget.findWidget("<col=00ff00>Superglass Make</col>", false);
            System.out.println(superglass);
            Microbot.click(superglass.getBounds());
        }

    }


    private void picking() {
        while (!Rs2Bank.isOpen()) {
            Rs2Bank.openBank();
            sleep(60, 200);
        }
        Rs2Bank.depositAll("Molten Glass");
        sleepUntil(() -> !Rs2Inventory.contains("Molten Glass"), 100);
        if(Rs2GroundItem.exists("Molten Glass", 1)) {
            sleep(60, 100);
            Rs2Bank.closeBank();
            while(Rs2GroundItem.exists("Molten Glass", 1)) {
                Rs2GroundItem.loot("Molten Glass", 1);
                sleep(60, 100);
            }
        }

    }
}

