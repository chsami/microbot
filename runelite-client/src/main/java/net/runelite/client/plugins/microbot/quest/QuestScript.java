package net.runelite.client.plugins.microbot.quest;

import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.questhelper.questhelpers.QuestHelper;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.questhelper.steps.QuestStep;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuestScript extends Script {
    public static double version = 1.0;


    public static List<ItemRequirement> itemRequirements = new ArrayList<>();

    public static List<ItemRequirement> itemsMissing = new ArrayList<>();


    public boolean run(QuestConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (QuestHelperPlugin.getSelectedQuest() != null && !Microbot.getClientThread().runOnClientThread(() -> QuestHelperPlugin.getSelectedQuest().isCompleted())) {
                    if (Rs2Widget.hasWidget("click here to continue")) {
                        VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                        return;
                    }

                    if (Rs2Widget.hasWidget("select an option")) {
                        VirtualKeyboard.keyPress('1');
                        return;
                    }

                    if (!applyStep(null)) return;

                    if (QuestHelperPlugin.getSelectedQuest().getCurrentStep() instanceof ConditionalStep) {
                        ConditionalStep conditionalStep = (ConditionalStep) QuestHelperPlugin.getSelectedQuest().getCurrentStep();
                        for (QuestStep step : conditionalStep.getSteps()) {
                            applyStep(step);
                        }
                    }

                    for (ItemRequirement itemRequirement : QuestHelperPlugin.getSelectedQuest().getItemRequirements()) {
                        if (!Inventory.hasItemAmount(itemRequirement.getId(), itemRequirement.getQuantity())) {
                            itemsMissing.add(itemRequirement);
                        }
                    }
                    if (itemsMissing.size() > 0) {
                        Rs2Bank.useBank();
                        Rs2Bank.depositAll();
                        for (ItemRequirement itemRequirement : QuestScript.itemsMissing) {
                            Rs2Bank.withdrawItemX(true, itemRequirement.getName(), itemRequirement.getQuantity());
                        }
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
        reset();
    }

    public static void reset() {
        itemRequirements = new ArrayList<>();
    }

    public boolean applyStep(QuestStep step) {
        QuestStep questStep = null;
        if (step != null)
            questStep = step;
        else
            questStep = QuestHelperPlugin.getSelectedQuest().getCurrentStep();

        if (questStep instanceof ObjectStep) {
            return applyObjectStep(step);
        } else if (questStep instanceof NpcStep) {
            return applyNpcStep(step);
        }
        return true;
    }

    public boolean applyNpcStep(QuestStep step) {
        NpcStep questStep = null;
        if (step != null)
            questStep = (NpcStep) step;
        else
            questStep = (NpcStep) QuestHelperPlugin.getSelectedQuest().getCurrentStep();

        net.runelite.api.NPC npc = Rs2Npc.getNpc(questStep.npcID);
        if (npc != null) {
            Rs2Npc.interact(questStep.npcID, "Talk-to");
        } else {
            if (questStep.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 3) {
                Microbot.getWalker().walkTo(questStep.getWorldPoint(), true);
                return false;
            }
        }
        return true;
    }


    public boolean applyObjectStep(QuestStep step) {
        ObjectStep questStep = null;
        if (step != null)
            questStep = (ObjectStep) step;
        else
            questStep = (ObjectStep) QuestHelperPlugin.getSelectedQuest().getCurrentStep();

        boolean success = Rs2GameObject.interact(questStep.objectID);
        if (!success) {
            if (questStep.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 3) {
                Microbot
                        .getWalker()
                        .walkTo(questStep.getWorldPoint(), true);
                return false;
            }
        }
        return true;
    }

}
