package net.runelite.client.plugins.microbot.eventdismiss;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class EventDismissScript extends Script {
    public static double version = 1.0;

    public boolean run(EventDismissConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                NPC npc = Rs2Npc.getRandomEventNPC();

                if (npc != null) {
                    if (shouldDismissNpc(npc, config)) {
                        Microbot.pauseAllScripts = true;
                        dismissNpc(npc);
                    } else if (!Rs2Inventory.isFull()) {
                        Microbot.pauseAllScripts = true;
                        talkToNPC(npc);
                    }
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

    private boolean shouldDismissNpc(NPC npc, EventDismissConfig config) {
        if (npc.getName() == null) return false;
        switch (npc.getName()) {
            case "Bee keeper":
                return config.dismissBeekeeper();
            case "Capt' Arnav":
                return config.dismissArnav();
            case "Niles":
            case "Miles":
            case "Giles":
                return config.dismissCerters();
            case "Count Check":
                return config.dismissCountCheck();
            case "Sergeant Damien":
                return config.dismissDrillDemon();
            case "Drunken Dwarf":
                return config.dismissDrunkenDwarf();
            case "Evil Bob":
                return config.dismissEvilBob();
            case "Postie Pete":
                return config.dismissEvilTwin();
            case "Freaky Forester":
                return config.dismissFreakyForester();
            case "Genie":
                return config.dismissGenie();
            case "Leo":
                return config.dismissGravedigger();
            case "Dr Jekyll":
                return config.dismissJekyllAndHyde();
            case "Frog":
                return config.dismissKissTheFrog();
            case "Mysterious Old Man":
                return config.dismissMysteriousOldMan();
            case "Pillory Guard":
                return config.dismissPillory();
            case "Flippa":
            case "Tilt":
                return config.dismissPinball();
            case "Quiz Master":
                return config.dismissQuizMaster();
            case "Rick Turpentine":
                return config.dismissRickTurpentine();
            case "Sandwich lady":
                return config.dismissSandwichLady();
            case "Strange plant":
                return config.dismissStrangePlant();
            case "Dunce":
                return config.dismissSurpriseExam();
            default:
                return false;
        }
    }
    private void dismissNpc(NPC npc) {
        // Interact with NPC to dismiss it
        Rs2Npc.interact(npc, "Dismiss");
        Microbot.pauseAllScripts = false;
    }

    private void talkToNPC(NPC npc) {
        // Interact with NPC to claim lamp
        Rs2Npc.interact(npc, "Talk-to");
        sleep(1200);
        Rs2Dialogue.clickContinue();
        Microbot.pauseAllScripts = false;
    }
}
