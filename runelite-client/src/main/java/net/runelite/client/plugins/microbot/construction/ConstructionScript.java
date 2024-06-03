package net.runelite.client.plugins.microbot.construction;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.construction.enums.ConstructionState;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
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
        return Rs2Widget.findWidget("Un-note", null) != null;
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
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                Rs2Tab.switchToInventoryTab();
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
        boolean hasRequiredPlanks = Rs2Inventory.hasItemAmount(ItemID.OAK_PLANK, Random.random(8, 16)); //oak plank
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
        if (Rs2GameObject.interact(oakLarderSpace, "Build")) {
            sleepUntilOnClientThread(() -> hasFurnitureInterfaceOpen(), 5000);
            Rs2Keyboard.keyPress('2');
            sleepUntilOnClientThread(() -> getOakLarder() != null, 5000);
        }
    }

    private void remove() {
        TileObject oaklarder = getOakLarder();
        if (oaklarder == null) return;
        if (Rs2GameObject.interact(oaklarder, "Remove")) {
            sleepUntilOnClientThread(() -> hasRemoveLarderInterfaceOpen(), 5000);
            Rs2Keyboard.keyPress('1');
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
            Rs2Tab.switchToSettingsTab();
            sleep(800, 1800);
            Widget houseOptionWidget = Rs2Widget.findWidget(SpriteID.OPTIONS_HOUSE_OPTIONS, null);
            if (houseOptionWidget != null)
                Microbot.getMouse().click(houseOptionWidget.getCanvasLocation());
            sleep(800, 1800);
            Widget callServantWidget = Rs2Widget.findWidget("Call Servant", null);
            if (callServantWidget != null)
                Microbot.getMouse().click(callServantWidget.getCanvasLocation());
        }


        if (Rs2Dialogue.isInDialogue() || Rs2Npc.interact(butler, "Talk-to")) {
            sleep(1200);
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
            sleep(1200, 2000);
            if (Rs2Widget.findWidget("Go to the bank...", null) != null) {
                Rs2Inventory.useItemOnNpc(ItemID.OAK_PLANK + 1, butler.getId()); // + 1  for noted item
                sleepUntilOnClientThread(() -> Rs2Widget.hasWidget("Dost thou wish me to exchange that certificate"));
                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                sleepUntilOnClientThread(() -> Rs2Widget.hasWidget("Select an option"));
                Rs2Keyboard.typeString("1");
                sleepUntilOnClientThread(() -> Rs2Widget.hasWidget("Enter amount:"));
                Rs2Keyboard.typeString("28");
                Rs2Keyboard.enter();
            } else if (hasDialogueOptionToUnnote()) {
                Rs2Keyboard.keyPress('1');
                sleepUntilOnClientThread(() -> !hasDialogueOptionToUnnote());
            } else if (hasPayButlerDialogue() || hasDialogueOptionToPay()) {
                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                sleep(1200, 2000);
                if (hasDialogueOptionToPay()) {
                    Rs2Keyboard.keyPress('1');
                }
            }
        }
    }
}
