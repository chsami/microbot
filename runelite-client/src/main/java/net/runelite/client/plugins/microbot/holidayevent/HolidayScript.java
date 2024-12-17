package net.runelite.client.plugins.microbot.holidayevent;

import com.google.inject.Inject;
import net.runelite.api.TileObject;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.swing.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class HolidayScript extends Script {

    private final HolidayPlugin plugin;

    @Inject
    public HolidayScript(HolidayPlugin plugin) {
        this.plugin = plugin;
    }

    private long idleStartTime = -1; // Tracks when the player started idling

    private static final long COOLDOWN_MILLIS = 5000; // 5 seconds cooldown
    private long lastSnowTakenTime = 0;

    private int currentStep = 0;
    public static boolean test = false;
    public boolean run(HolidayConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();
                System.out.println("break");

                if (!config.collectSnow()) {

                    switch (currentStep) {
                        case 0:
                            Microbot.log("Current Step: 0 - talk to cecilia and start the event");
                            Rs2Walker.walkTo(2990, 3379, 0);
                            Rs2Npc.interact("Cecilia", "Talk-to");
                            sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                            Rs2Dialogue.keyPressForDialogueOption(1);
                            sleep(900, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(600, 900);
                            }
                            sleepUntil(() -> Rs2Dialogue.hasQuestion("Start the Christmas event?"));
                            Rs2Dialogue.keyPressForDialogueOption(1);
                            sleep(600, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(900, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(600, 900);
                            }
                            sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                            currentStep++;
                            break;
                        case 1:
                            Microbot.log("Current Step: 1 - talk to party pete");
                            Rs2Walker.walkTo(3050, 3376, 0);
                            Rs2Npc.interact("Party Pete", "Talk-to");
                            sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                            Rs2Dialogue.keyPressForDialogueOption(1);
                            sleep(600, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(600, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                            currentStep++;
                            break;
                        case 2:
                            Microbot.log("Current Step: 2 - Get invitations from Cecilia");
                            Rs2Walker.walkTo(2990, 3379, 0);
                            Rs2Npc.interact("Cecilia", "Talk-to");
                            sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                            Rs2Dialogue.keyPressForDialogueOption(1);
                            dialogueCycle();
                            sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                            if (!Rs2Inventory.contains("Invitations")) {
                                Rs2Npc.interact("Cecilia", "Talk-to");
                                sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                                Rs2Dialogue.keyPressForDialogueOption(1);
                                dialogueCycle();
                                sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                            }
                            currentStep++;
                            break;
                        case 3:
                            Microbot.log("Current Step: 3 - talk to sir amik varz");
                            if (!Rs2Inventory.contains("Invitations")) {
                                Microbot.log("Invitations missing in Step 3. Resetting to Step 0.");
                                currentStep = 0;
                                return;
                            }
                            // Walk and interact with Sir Amik
                            interactWithSirAmik();

                            // Set expected message and check if received
                            if (plugin.isMessageReceived() && isRunning()) {
                                Microbot.log("Message received successfully.");
                                plugin.setMessageReceived(false); // Reset for the next case
                            } else {
                                Microbot.log("Message not received, retrying interaction with Sir Amik.");
                                interactWithSirAmik(); // Retry if message not received
                            }

                            currentStep++;
                            break;

                        case 4:
                            Microbot.log("Current Step: 4 - talk to hairdresser");
                            if (!Rs2Inventory.contains("Invitations")) {
                                Microbot.log("Invitations missing in Step 4. Resetting to Step 0.");
                                currentStep = 0;
                                return;
                            }
                            // Walk and interact with Hairdresser
                            interactWithHairdresser();

                            // Set expected message and check if received
                            if (plugin.isMessageReceived() && isRunning()) {
                                Microbot.log("Message received successfully.");
                                plugin.setMessageReceived(false); // Reset for the next case
                            } else {
                                Microbot.log("Message not received, retrying interaction with hairdresser.");
                                interactWithHairdresser(); // Retry if message not received
                            }

                            currentStep++;
                            break;
                        case 5:
                            Microbot.log("Current Step: 5 - talk to Sarah");
                            if (!Rs2Inventory.contains("Invitations")) {
                                Microbot.log("Invitations missing in Step 5. Resetting to Step 0.");
                                currentStep = 0;
                                return;
                            }

                            // Walk and interact with Sarah
                            interactWithSarah();

                            // Set expected message and check if received
                            if (plugin.isMessageReceived() && isRunning()) {
                                Microbot.log("Message received successfully.");
                                plugin.setMessageReceived(false); // Reset for the next case
                            } else {
                                Microbot.log("Message not received, retrying interaction with Sarah.");
                                interactWithSarah(); // Retry if message not received
                            }

                            currentStep++;
                            break;
                        case 6:
                            Microbot.log("Current Step: 6 - talk to Gertrude");
                            if (!Rs2Inventory.contains("Invitations")) {
                                Microbot.log("Invitations missing in Step 6. Resetting to Step 0.");
                                currentStep = 0;
                                return;
                            }

                            // Walk and interact with Gertrude
                            interactWithGertrude();

                            // Set expected message and check if received
                            if (plugin.isMessageReceived() && isRunning()) {
                                Microbot.log("Message received successfully.");
                                plugin.setMessageReceived(false); // Reset for the next case
                            } else {
                                Microbot.log("Message not received, retrying interaction with gertrude.");
                                interactWithGertrude(); // Retry if message not received
                            }

                            currentStep++;
                            break;
                        case 7:
                            Microbot.log("Current Step: 7 - talk to charlie");
                            if (!Rs2Inventory.contains("Invitations")) {
                                Microbot.log("Invitations missing in Step 7. Resetting to Step 0.");
                                currentStep = 0;
                                return;
                            }
                            Rs2Walker.walkTo(3211, 3392, 0);
                            Rs2Npc.interact("Charlie the tramp", "Talk-to");
                            sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                            Rs2Dialogue.keyPressForDialogueOption(1);
                            sleep(1200, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }


                            if (config.showCutscenePopup()) {


                                int result = JOptionPane.showConfirmDialog(
                                        null,
                                        "This part of the Event can cause Crashes. Press OK to shut down the plugin here and finish the event on another client, or press Cancel to continue running.",
                                        "Client Update Required",
                                        JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.WARNING_MESSAGE
                                );

                                if (result == JOptionPane.OK_OPTION) {
                                    shutdown(); // Shut down the plugin if OK is pressed
                                } else {
                                    // Continue running if Cancel is pressed
                                    Microbot.log("Plugin will continue running despite potential crashes.");
                                }
                            }
                            sleep(1200, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(1200, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleepUntil(() -> Rs2Dialogue.hasDialogueOption("Yes"), 60000);
                            Rs2Dialogue.keyPressForDialogueOption(1);
                            sleep(3600, 4400);
                            sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleepUntil(() -> !Rs2Dialogue.isInCutScene() || !Rs2Dialogue.isInDialogue(), 30000);
                            sleep(3600, 4400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(3600, 4400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(3600, 4400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(3600, 4400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            currentStep++;
                            break;
                        case 8:
                            Microbot.log("Current Step: 8 - hairdresser dog");
                            Rs2Npc.interact("Hairdresser", "Talk-to");
                            sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                            sleep(1200, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(1200, 2400);
                            Rs2Dialogue.keyPressForDialogueOption(2);
                            sleep(1200, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                            currentStep++;
                            break;
                        case 9:
                            Microbot.log("Current Step: 9 - amik dog");
                            Rs2Npc.interact("Sir Amik Varze", "Talk-to");
                            sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                            sleep(1200, 1800);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(1200, 2400);
                            Rs2Dialogue.keyPressForDialogueOption(1);
                            sleep(1200, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                            currentStep++;
                            break;
                        case 10:
                            Microbot.log("Current Step: 10 - Sarah dog");
                            Rs2Npc.interact("Sarah", "Talk-to");
                            sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                            sleep(1200, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(1200, 2400);
                            Rs2Dialogue.keyPressForDialogueOption(3);
                            sleep(1200, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                            currentStep++;
                            break;
                        case 11:
                            Microbot.log("Current Step: 11 - Gertrude dog");
                            Rs2Npc.interact("Gertrude", "Talk-to");
                            sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                            sleep(1200, 1800);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(1200, 2400);
                            Rs2Dialogue.keyPressForDialogueOption(4);
                            sleep(1200, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                            sleep(2400, 3600);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(2400, 3600);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(4200, 4800);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            currentStep++;
                            break;
                        case 12:
                            Microbot.log("Current Step: 12 - Charlie's dog and finish");
                            checkAndTalkToCecilia();
                            sleepUntil(Rs2Dialogue::isInDialogue, 30000);
                            sleep(600, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(3600, 4200);
                            sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                            sleep(2400, 3600);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(3600, 6200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(1600, 2400);
                            sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                            sleep(3200, 4200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(2800, 4800);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(1600, 2400);
                            sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                            sleep(1600, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            if (!Rs2Dialogue.isInCutScene()
                                    && !Rs2Dialogue.hasContinue()) {
                                checkAndTalkToCecilia();
                            }
                            sleepUntil(Rs2Dialogue::isInDialogue, 6000);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleep(3600, 4900);
                            if (!Rs2Dialogue.isInCutScene()
                                    && !Rs2Dialogue.hasContinue()) {
                                checkAndTalkToCecilia();
                            }
                            checkAndTalkToCecilia();
                            sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 6000);
                            sleep(1600, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                            sleep(1600, 2400);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300, 600);
                            }
                            currentStep++;
                            break;
                        default:
                            shutdown();
                    }
                } else if(config.collectSnow()) {
                    System.out.println("Entering snow collection");
                    if (!takingTheSnow() & isIdle() & nearTheSnow()) {
                        takeTheSnow();
                    }
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    public boolean takingTheSnow() {
        int currentAnimationId = Rs2Player.getAnimation();

        return (currentAnimationId == 5067);
    }

    private boolean isIdle() {
        int currentAnimation = Rs2Player.getAnimation(); // Assuming Rs2Player.getAnimation() returns the current animation
        if (currentAnimation == -1) { // Idle animation
            if (idleStartTime == -1) {
                idleStartTime = System.currentTimeMillis();
            }
            long idleDuration = System.currentTimeMillis() - idleStartTime;
            return idleDuration >= 4000; // 4 seconds in milliseconds
        } else {
            idleStartTime = -1; // Reset idle timer if no longer idle
            return false;
        }
    }


    public void takeTheSnow() {
        long now = Instant.now().toEpochMilli();

        if (now - lastSnowTakenTime < COOLDOWN_MILLIS) {
            System.out.println("DEBUG: Cooldown active, skipping snow collection.");
            return;
        }

        System.out.println("DEBUG: Attempting to take the snow.");
        boolean success = Rs2GameObject.interact(19035, "Take");

        if (success) {
            System.out.println("DEBUG: Successfully took the snow.");
            lastSnowTakenTime = now;
        } else {
            System.out.println("DEBUG: Take the snow interaction failed.");
        }
    }
    public boolean nearTheSnow() {
        TileObject snowObject = Rs2GameObject.findObjectByIdAndDistance(19035, 1);
        System.out.println("DEBUG: Found snow object near? " + (snowObject != null));
        return snowObject != null;
    }


    public void dialogueCycle() {
        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(300, 600);
        }
        sleep(600, 1200);

        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(300, 600);
        }
        sleep(900, 1200);

        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(600, 900);
        }

        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(300, 600);
        }
        sleep(600, 1200);

        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(300, 600);
        }
        sleep(900, 1200);

        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(600, 900);
        }
    }


    public void checkAndTalkToCecilia() {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < 20000) {
            // Check if the dialogue widget is visible
            if (Rs2Dialogue.hasContinue()) {
                // Reset the timer if dialogue is found
                startTime = System.currentTimeMillis();
            }
            // Sleep briefly to reduce CPU usage
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // No dialogue detected for 15 seconds, interact with Cecilia
        Rs2Npc.interact("Cecilia", "Talk-to");
    }

    private void interactWithSirAmik() {
        Rs2Walker.walkTo(2958, 3341, 2);
        Rs2Npc.interact("Sir Amik Varze", "Talk-to");
        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
        Rs2Dialogue.keyPressForDialogueOption(1);
        sleep(1200, 2400);

        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(300, 600);
        }

        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
    }

    private void interactWithHairdresser() {
        Rs2Walker.walkTo(2949, 3379, 0);
        Rs2Npc.interact("Hairdresser", "Talk-to");
        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
        Rs2Dialogue.keyPressForDialogueOption(1);
        sleep(1200, 2400);

        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(300, 600);
        }

        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
    }

    private void interactWithSarah() {
        Rs2Walker.walkTo(3034, 3294, 0);
        Rs2Npc.interact("Sarah", "Talk-to");
        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
        Rs2Dialogue.keyPressForDialogueOption(1);
        sleep(1200, 2400);

        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(300, 600);
        }

        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
    }

    private void interactWithGertrude() {
        Rs2Walker.walkTo(3152, 3409, 0);
        Rs2Npc.interact("Gertrude", "Talk-to");
        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
        Rs2Dialogue.keyPressForDialogueOption(1);
        sleep(1200, 2400);

        while (Rs2Dialogue.hasContinue() && isRunning()) {
            Rs2Dialogue.clickContinue();
            sleep(300, 600);
        }

        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
    }


    @Override
    public void shutdown() {
        currentStep = 0;
        super.shutdown();
    }
}