package net.runelite.client.plugins.microbot.holidayevent;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

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
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> Rs2Dialogue.hasQuestion("Start the Christmas event?"));
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 1:
                        Rs2Walker.walkTo(3045, 3372, 0);
                        Rs2Npc.interact("Party Pete", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
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
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 3:
                        Rs2Walker.walkTo(2958, 3341, 2);
                        Rs2Npc.interact("Sir Amik Varze", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 4:
                        Rs2Walker.walkTo(2949, 3379, 0);
                        Rs2Npc.interact("Hairdresser", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 5:
                        Rs2Walker.walkTo(3034, 3294, 0);
                        Rs2Npc.interact("Sarah", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 6:
                        Rs2Walker.walkTo(3151, 3410, 0);
                        Rs2Npc.interact("Gertrude", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 7:
                        Rs2Walker.walkTo(3211, 3392, 0);
                        Rs2Npc.interact("Charlie the tramp", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> Rs2Dialogue.hasDialogueOption("Yes"));
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() && !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 8:
                        Rs2Npc.interact("Hairdresser", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleep(600, 1200);
                        Rs2Dialogue.keyPressForDialogueOption(2);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 9:
                        Rs2Npc.interact("Sir Amik Varze", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleep(600, 1200);
                        Rs2Dialogue.keyPressForDialogueOption(1);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 10:
                        Rs2Npc.interact("Sarah", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleep(600, 1200);
                        Rs2Dialogue.keyPressForDialogueOption(3);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 11:
                        Rs2Npc.interact("Gertrude", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleep(600, 1200);
                        Rs2Dialogue.keyPressForDialogueOption(4);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInDialogue(), 30000);
                        currentStep++;
                        break;
                    case 12:
                        Rs2Npc.interact("Cecilia", "Talk-to");
                        sleepUntil(() -> Rs2Dialogue.isInDialogue() && !Rs2Player.isMoving(), 30000);
                        sleep(600, 1200);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        sleep(1600, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        sleep(1600, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        sleep(1600, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        sleep(1600, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
                        }
                        sleepUntil(() -> !Rs2Dialogue.isInCutScene() || Rs2Dialogue.hasContinue(), 30000);
                        sleep(1600, 2400);
                        while (Rs2Dialogue.hasContinue() && isRunning()) {
                            Rs2Dialogue.clickContinue();
                            sleep(300);
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

    @Override
    public void shutdown() {
        super.shutdown();
    }
}