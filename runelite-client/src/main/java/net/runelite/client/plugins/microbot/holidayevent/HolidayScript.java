package net.runelite.client.plugins.microbot.holidayevent;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class HolidayScript extends Script {

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

                switch (currentStep) {
                    case 0:
                        Rs2Walker.walkTo(2990, 3379, 0);
                        Rs2Npc.interact("Cecilia", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(900, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(600,900);
                        }
                        sleepUntil(() -> Rs2Dialogue.hasQuestion("Start the Christmas event?"));
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(900, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(600,900);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 1:
                        Rs2Walker.walkTo(3050, 3376, 0);
                        Rs2Npc.interact("Party Pete", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 2:
                        Rs2Walker.walkTo(2990, 3379, 0);
                        Rs2Npc.interact("Cecilia", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(900, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(600,900);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        if(!Rs2Inventory.contains("Invitations")) {
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300,600);
                            }
                            sleep(600, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300,600);
                            }
                            sleep(900, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(600,900);
                            }
                            Rs2Dialogue.keyPressForDialogueOption(1);
                            sleep(600, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300,600);
                            }
                            sleep(600, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(300,600);
                            }
                            sleep(900, 1200);
                            while (Rs2Dialogue.hasContinue() && isRunning()) {
                                Rs2Dialogue.clickContinue();
                                sleep(600,900);
                            }
                            sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        }
                        currentStep++;
                        break;
                    case 3:
                        Rs2Walker.walkTo(2958, 3341, 2);
                        Rs2Npc.interact("Sir Amik Varze", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 4:
                        Rs2Walker.walkTo(2949, 3379, 0);
                        Rs2Npc.interact("Hairdresser", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 5:
                        Rs2Walker.walkTo(3034, 3294, 0);
                        Rs2Npc.interact("Sarah", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 6:
                        Rs2Walker.walkTo(3151, 3410, 0);
                        Rs2Npc.interact("Gertrude", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 7:
                        Rs2Walker.walkTo(3211, 3392, 0);
                        Rs2Npc.interact("Charlie the tramp", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }

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

                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> Rs2Dialogue.hasDialogueOption("Yes"),60000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(3600,4400);
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || !Rs2Dialogue.isInDialogue(), 30000);
                        sleep(3600,4400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(3600,4400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(3600,4400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(3600,4400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        currentStep++;
                        break;
                    case 8:
                        Rs2Npc.interact("Hairdresser", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(1200, 2400);
                        Rs2Dialogue.keyPressForDialogueOption(2);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 9:
                        Rs2Npc.interact("Sir Amik Varze", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        sleep(1200, 1800);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(1200, 2400);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 10:
                        Rs2Npc.interact("Sarah", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(1200, 2400);
                        Rs2Dialogue.keyPressForDialogueOption(3);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 11:
                        Rs2Npc.interact("Gertrude", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        sleep(1200, 1800);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(1200, 2400);
                        Rs2Dialogue.keyPressForDialogueOption(4);
                        sleep(1200, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        sleep(2400, 3600);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(2400, 3600);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(4200, 4800);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        currentStep++;
                        break;
                    case 12:
                        checkAndTalkToCecilia();
                        sleepUntil(Rs2Dialogue::isInDialogue, 30000);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(3600, 4200);
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        sleep(2400, 3600);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(3600, 6200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(1600, 2400);
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        sleep(3200, 4200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(2800, 4800);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(1600, 2400);
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        sleep(1600, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        if (!Rs2Dialogue.isInCutScene()
                            && !Rs2Dialogue.hasContinue()) {
                                    checkAndTalkToCecilia();}
                        sleepUntil(Rs2Dialogue::isInDialogue, 6000);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleep(3600,4900);
                        if (!Rs2Dialogue.isInCutScene()
                                && !Rs2Dialogue.hasContinue()) {
                            checkAndTalkToCecilia();}
                        checkAndTalkToCecilia();
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 6000);
                        sleep(1600, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        sleep(1600, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300,600);
                        }
                        currentStep++;
                        break;
                    default:
                        shutdown();
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


    @Override
    public void shutdown() {
        currentStep = 0;
        super.shutdown();
    }
}