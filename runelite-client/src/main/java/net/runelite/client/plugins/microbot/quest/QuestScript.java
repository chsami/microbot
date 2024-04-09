package net.runelite.client.plugins.microbot.quest;

import net.runelite.api.ItemComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.questhelper.requirements.Requirement;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class QuestScript extends Script {
    public static double version = 0.2;


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
                    Widget widget = Rs2Widget.findWidget("Start ");
                    if (Rs2Widget.hasWidget("select an option") || (widget != null &&
                            Microbot.getClientThread().runOnClientThread( () -> widget.getParent().getId()) != 10616888)) {
                        VirtualKeyboard.keyPress('1');
                        VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                        return;
                    }

                    if (Rs2Widget.hasWidget("click here to continue")) {
                        VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                        return;
                    }

                    boolean isInCutscene = Microbot.getVarbitValue(4606) > 0;
                    if (isInCutscene) {
                        return;
                    }

                    /**
                     * This portion is needed when using item on another item in your inventory.
                     * If we do not prioritize this, the script will think we are missing items
                     */
                    QuestStep questStep = QuestHelperPlugin.getSelectedQuest().getCurrentStep().getActiveStep();
                    if (questStep instanceof DetailedQuestStep) {
                        boolean result = applyDetailedQuestStep((DetailedQuestStep) QuestHelperPlugin.getSelectedQuest().getCurrentStep().getActiveStep());
                        if (result) {
                            return;
                        }
                    }

                    if (QuestHelperPlugin.getSelectedQuest().getCurrentStep() instanceof ConditionalStep) {
                        QuestStep conditionalStep = QuestHelperPlugin.getSelectedQuest().getCurrentStep().getActiveStep();
                        applyStep(conditionalStep);
                    } else if (QuestHelperPlugin.getSelectedQuest().getCurrentStep() instanceof NpcStep) {
                        applyNpcStep((NpcStep) QuestHelperPlugin.getSelectedQuest().getCurrentStep());
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, Random.random(400, 1000), TimeUnit.MILLISECONDS);
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
        if (step == null) return false;

        if (step instanceof ObjectStep) {
            return applyObjectStep((ObjectStep) step);
        } else if (step instanceof NpcStep) {
            return applyNpcStep((NpcStep) step);
        } else if (step instanceof DetailedQuestStep) {
            return applyDetailedQuestStep((DetailedQuestStep) step);
        }
        return true;
    }

    public boolean applyNpcStep(NpcStep step) {
        net.runelite.api.NPC npc = Rs2Npc.getNpc(step.npcID);
        if (npc != null && Rs2Camera.isTileOnScreen(npc.getLocalLocation()) && Microbot.getWalker().canReach(npc.getWorldLocation())) {
            Rs2Npc.interact(step.npcID, "Talk-to");
        } else if (npc != null && !Rs2Camera.isTileOnScreen(npc.getLocalLocation())) {
            Microbot.getWalker().hybridWalkTo(npc.getWorldLocation(), config.useNearest());
        } else if (npc != null && !Microbot.getWalker().canReach(npc.getWorldLocation())) {
            Microbot.getWalker().hybridWalkTo(npc.getWorldLocation(), config.useNearest());
        } else {
            if (step.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 3) {
                Microbot.getWalker().hybridWalkTo(step.getWorldPoint(), config.useNearest());
                return false;
            }
        }
        return true;
    }


    public boolean applyObjectStep(ObjectStep step) {
        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo2D(step.getWorldPoint()) > 5) {
            Microbot.getWalker().hybridWalkTo(step.getWorldPoint(), config.useNearest());
            return false;
        }
        boolean success = Rs2GameObject.interact(step.objectID, true);
        if (!success) {
            for (int objectId: step.getAlternateObjectIDs()) {
                success = Rs2GameObject.interact(objectId, true);
                if (success) break;
            }
        }
//        if (!success) {
//            if (step.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 3) {
//                if (config.enableHybridWalking()) {
//                    Microbot.getWalker().hybridWalkTo(step.getWorldPoint(), config.useNearest());
//                } else {
//                    Microbot.getWalker().walkTo(step.getWorldPoint(), true);
//                }
//                return false;
//            }
//        }
        return true;
    }

    private boolean applyDetailedQuestStep(DetailedQuestStep conditionalStep) {
        if (conditionalStep instanceof NpcStep) return false;
        for(Rs2Item item: Rs2Inventory.items()) {
            for (Requirement requirement : conditionalStep.getRequirements())
            {
                if (requirement instanceof ItemRequirement) {
                    ItemRequirement itemRequirement = (ItemRequirement) requirement;

                    if (itemRequirement.getAllIds().contains(item.id)) {
                        if (itemRequirement.shouldHighlightInInventory(Microbot.getClient())) {
                            Rs2Inventory.use(item.id);
                        }
                    }

                    if (!itemRequirement.getAllIds().contains(item.id) && conditionalStep.getWorldPoint() != null) {
                        if (Microbot.getWalker().canReach(conditionalStep.getWorldPoint())) {
                            Rs2GroundItem.loot(itemRequirement.getId());
                        } else {
                            Microbot.getWalker().hybridWalkTo(conditionalStep.getWorldPoint());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
