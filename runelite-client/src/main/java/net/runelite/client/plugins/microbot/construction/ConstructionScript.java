package net.runelite.client.plugins.microbot.construction;

import net.runelite.api.NPC;
import net.runelite.api.SpriteID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.construction.enums.ConstructionState;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;



public class ConstructionScript extends Script {

    ConstructionState state = ConstructionState.Idle;


    public TileObject getOakLarderSpace() {
        return Rs2GameObject.findObjectById(15403);
    }

    public TileObject getOakLarder() {
        return Rs2GameObject.findObjectById(13566);
    }

    public NPC getButler() {
        return Rs2Npc.getNpc("Demon butler");
    }

    public boolean hasDialogueOptionToUnnote() {
        return Rs2Widget.findWidget("Fetch", null) != null;
    }

    public boolean hasPayButlerDialogue() {
        return Rs2Widget.findWidget("must render unto me the 10,000 coins that are due", null) != null;
    }

    public boolean hasDialogueOptionToPay() {
        return Rs2Widget.findWidget("Okay, here's 10,000 coins.", null) != null;
    }

    public boolean hasFurnitureInterfaceOpen() {
        return Rs2Widget.findWidget("Furniture", null) != null;
    }

    public boolean hasRemoveLarderInterfaceOpen() {
        return Rs2Widget.findWidget("Really remove it?", null) != null;
    }

    public boolean run(ConstructionConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            super.run();
            try {
                calculateState();
                if (state == ConstructionState.Build) {
                    build();
                } else if (state == ConstructionState.Remove) {
                    remove();
                } else if (state == ConstructionState.Butler) {
                    butler();
                }
                //System.out.println(hasPayButlerDialogue());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private void calculateState() {
        TileObject oakLarderSpace = getOakLarderSpace();
        TileObject oakLarder = getOakLarder();
        NPC butler = getButler();
        boolean hasRequiredPlanks = Inventory.hasItemAmount(8778, Random.random(8, 16)); //oak plank
        if (oakLarderSpace == null && oakLarder != null) {
            state = ConstructionState.Remove;
        } else if (oakLarderSpace != null && oakLarder == null && hasRequiredPlanks) {
            state = ConstructionState.Build;
        } else if (oakLarderSpace != null && oakLarder == null && butler != null) {
            state = ConstructionState.Butler;
        } else if (oakLarderSpace == null && oakLarder == null) {
            state = ConstructionState.Idle;
            Microbot.getNotifier().notify("Looks like we are no longer in our house.");
            shutdown();
        }
    }

    private void build() {
        TileObject oakLarderSpace = getOakLarderSpace();
        if (oakLarderSpace == null) return;
        if (Rs2Menu.doAction("Build", oakLarderSpace.getCanvasTilePoly())) {
            sleepUntilOnClientThread(() -> hasFurnitureInterfaceOpen(), 5000);
            VirtualKeyboard.keyPress('2');
            sleepUntilOnClientThread(() -> getOakLarder() != null, 5000);
        }
    }

    private void remove() {
        TileObject oaklarder = getOakLarder();
        if (oaklarder == null) return;
        if (Rs2Menu.doAction("Remove", oaklarder.getCanvasTilePoly())) {
            sleepUntilOnClientThread(() -> hasRemoveLarderInterfaceOpen(), 5000);
            VirtualKeyboard.keyPress('1');
            sleepUntilOnClientThread(() -> getOakLarderSpace() != null, 5000);
        }
    }

    private void butler() {
        NPC butler = getButler();
        boolean butlerIsToFar;
        if (butler == null) return;
        butlerIsToFar = Microbot.getClientThread().runOnClientThread(() -> {
            int distance = butler.getWorldLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation());
            return distance > 3;
        });
        if (butlerIsToFar) {
            Tab.switchToSettingsTab();
            sleep(800, 1800);
            Widget houseOptionWidget = Rs2Widget.findWidget(SpriteID.OPTIONS_HOUSE_OPTIONS, null);
            if (houseOptionWidget != null)
                Microbot.getMouse().click(houseOptionWidget.getCanvasLocation());
            sleep(800, 1800);
            Widget callServantWidget = Rs2Widget.findWidget("Call Servant", null);
            if (callServantWidget != null)
                Microbot.getMouse().click(callServantWidget.getCanvasLocation());
        }

        if (Rs2Menu.doAction("Talk-to", butler.getCanvasTilePoly())) {
            sleep(1200, 2000);
            if (hasDialogueOptionToUnnote()) {
                VirtualKeyboard.keyPress('1');
                sleepUntilOnClientThread(() -> !hasDialogueOptionToUnnote());
            } else if (hasPayButlerDialogue()) {
                VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                sleep(1200, 2000);
                if (hasDialogueOptionToPay()) {
                    VirtualKeyboard.keyPress('1');
                }
            }
        }
    }
}
