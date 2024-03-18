package net.runelite.client.plugins.microbot.quest;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.questhelper.requirements.Requirement;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.questhelper.steps.QuestStep;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class QuestScript extends Script {
    public static double version = 1.0;


    public static List<ItemRequirement> itemRequirements = new ArrayList<>();

    public static List<ItemRequirement> itemsMissing = new ArrayList<>();
    public static List<ItemRequirement> grandExchangeItems = new ArrayList<>();


    private QuestConfig config;


    public boolean run(QuestConfig config) {
        this.config = config;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (QuestHelperPlugin.getSelectedQuest() != null && !Microbot.getClientThread().runOnClientThread(() -> QuestHelperPlugin.getSelectedQuest().isCompleted())) {
                    if (Rs2Widget.hasWidget("click here to continue")) {
                        VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                        return;
                    }

                    if (Rs2Widget.hasWidget("select an option") || Rs2Widget.hasWidget("Start the")) {
                        VirtualKeyboard.keyPress('1');
                        return;
                    }

                    List<ItemRequirement> itemRequirements = new ArrayList<>();
                    for (PanelDetails panel: QuestHelperPlugin.getSelectedQuest().getCurrentStep().getActiveStep().getQuestHelper().getPanels()) {
                        if (panel.getHideCondition() == null || !panel.getHideCondition().check(Microbot.getClient())) {
                            for (QuestStep step : panel.getSteps())
                            {
                                if (panel.getRequirements().isEmpty())
                                    break;
                                QuestStep newStep = QuestHelperPlugin.getSelectedQuest().getCurrentStep().getActiveStep();
                                for(String text: newStep.getText()) {
                                    if (step.getText().contains(text)) {
                                        for (Requirement requirement: panel.getRequirements()) {
                                            if (requirement instanceof ItemRequirement)
                                            {
                                                itemRequirements.add((ItemRequirement) requirement);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    for (ItemRequirement itemRequirement : itemRequirements) {
                        if (!Rs2Inventory.hasItemAmount(itemRequirement.getId(), itemRequirement.getQuantity()) && itemsMissing.stream().noneMatch(x -> x.getId() == itemRequirement.getId())) {
                            itemsMissing.add(itemRequirement);
                        }
                    }

                    if (!itemsMissing.isEmpty()) {
                        Rs2Bank.useBank();
                        Rs2Bank.depositAll();
                        sleepUntil(Rs2Inventory::isEmpty);
                        for (ItemRequirement itemRequirement : itemsMissing) {
                            if (!Rs2Bank.hasItem(itemRequirement.getId())) {
                                if (grandExchangeItems.stream().noneMatch(x -> x.getId() == itemRequirement.getId())) {
                                    grandExchangeItems.add(itemRequirement);
                                }
                            } else {
                                Rs2Bank.withdrawX(true, itemRequirement.getName(), itemRequirement.getQuantity());
                                sleep(600);
                            }
                        }
                    }

                    if (config.useGrandExchange() && !grandExchangeItems.isEmpty()) {
                        final List<ItemRequirement> _grandExchangeItems = grandExchangeItems;
                        for (ItemRequirement itemRequirement : _grandExchangeItems) {
                            Rs2GrandExchange.buyItem(itemRequirement.getName(), itemRequirement.getName(), 5000, itemRequirement.getQuantity());
                        }
                        sleep(2000);
                        Rs2GrandExchange.collectToBank();
                        sleepUntil(Rs2GrandExchange::isAllSlotsEmpty);
                    }

                    if (itemsMissing.isEmpty()) {
                          if (!applyStep(null)) return;

                        if (QuestHelperPlugin.getSelectedQuest().getCurrentStep() instanceof ConditionalStep) {
                            QuestStep conditionalStep = QuestHelperPlugin.getSelectedQuest().getCurrentStep().getActiveStep();
                            applyStep(conditionalStep);
                            /*for (QuestStep step : conditionalStep.getSteps()) {
                                applyStep(step);
                                break;
                            }*/
                        } else if (QuestHelperPlugin.getSelectedQuest().getCurrentStep() instanceof NpcStep) {
                            applyNpcStep(QuestHelperPlugin.getSelectedQuest().getCurrentStep());
                        }
                    } else {
                        reset();
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
        itemsMissing = new ArrayList<>();
        itemRequirements = new ArrayList<>();
        grandExchangeItems = new ArrayList<>();
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
        if (npc != null && Rs2Camera.isTileOnScreen(npc.getLocalLocation()) && Microbot.getWalker().canReach(npc.getWorldLocation())) {
            Rs2Npc.interact(questStep.npcID, "Talk-to");
        } else if (npc != null && !Rs2Camera.isTileOnScreen(npc.getLocalLocation())) {
            Microbot.getWalker().hybridWalkTo(npc.getWorldLocation(), config.useNearest());
        } else if (npc != null && !Microbot.getWalker().canReach(npc.getWorldLocation())) {
            Microbot.getWalker().hybridWalkTo(npc.getWorldLocation(), config.useNearest());
        } else {
            if (questStep.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 3) {
                if (config.enableHybridWalking()) {
                    Microbot.getWalker().hybridWalkTo(questStep.getWorldPoint(), config.useNearest());
                } else {
                    Microbot.getWalker().walkTo(questStep.getWorldPoint(), true);
                }
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
                if (config.enableHybridWalking()) {
                    Microbot.getWalker().hybridWalkTo(questStep.getWorldPoint(), config.useNearest());
                } else {
                    Microbot.getWalker().walkTo(questStep.getWorldPoint(), true);
                }
                return false;
            }
        }
        return true;
    }

}
