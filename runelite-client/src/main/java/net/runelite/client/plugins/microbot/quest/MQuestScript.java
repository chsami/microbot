package net.runelite.client.plugins.microbot.quest;

import net.runelite.api.NPC;
import net.runelite.api.Quest;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.MQuestHelperPlugin;
import net.runelite.client.plugins.questhelper.requirements.Requirement;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MQuestScript extends Script {
    public static double version = 0.2;


    public static List<ItemRequirement> itemRequirements = new ArrayList<>();

    public static List<ItemRequirement> itemsMissing = new ArrayList<>();
    public static List<ItemRequirement> grandExchangeItems = new ArrayList<>();


    private MQuestConfig config;


    public boolean run(MQuestConfig config) {
        this.config = config;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (MQuestHelperPlugin.getSelectedQuest() != null && !Microbot.getClientThread().runOnClientThread(() -> MQuestHelperPlugin.getSelectedQuest().isCompleted())) {
                    Widget widget = Rs2Widget.findWidget("Start ");
                    if (Rs2Widget.hasWidget("select an option") && MQuestHelperPlugin.getSelectedQuest().getQuest().getId() != Quest.COOKS_ASSISTANT.getId() || (widget != null &&
                            Microbot.getClientThread().runOnClientThread(() -> widget.getParent().getId()) != 10616888)) {
                        Rs2Keyboard.keyPress('1');
                        Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                        return;
                    }

                    if (Rs2Dialogue.isInDialogue()) {
                        Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                        return;
                    }

                    boolean isInCutscene = Microbot.getVarbitValue(4606) > 0;
                    if (isInCutscene) {
                        return;
                    }

                    if (MQuestHelperPlugin.getSelectedQuest().getQuest().getId() == Quest.THE_RESTLESS_GHOST.getId()) {
                        if (Rs2Inventory.hasItem("ghostspeak amulet")) {
                            Rs2Inventory.wear("ghostspeak amulet");
                        }
                    }

                    if (MQuestHelperPlugin.getSelectedQuest().getQuest().getId() == Quest.RUNE_MYSTERIES.getId()) {
                        NPC aubury = Rs2Npc.getNpc("Aubury");
                        if (Rs2Inventory.hasItem("research package") && aubury != null) {
                            Rs2Npc.interact(aubury, "Talk-to");
                        }
                    }

                    if (MQuestHelperPlugin.getSelectedQuest().getQuest().getId() == Quest.COOKS_ASSISTANT.getId()) {
                        NPC aubury = Rs2Npc.getNpc("Aubury");
                        if (Rs2Inventory.hasItem("research package") && aubury != null) {
                            Rs2Npc.interact(aubury, "Talk-to");
                        }
                    }


                    /**
                     * This portion is needed when using item on another item in your inventory.
                     * If we do not prioritize this, the script will think we are missing items
                     */
                    QuestStep questStep = MQuestHelperPlugin.getSelectedQuest().getCurrentStep().getActiveStep();
                    if (questStep instanceof DetailedQuestStep && !(questStep instanceof NpcStep || questStep instanceof ObjectStep)) {
                        boolean result = applyDetailedQuestStep((DetailedQuestStep) MQuestHelperPlugin.getSelectedQuest().getCurrentStep().getActiveStep());
                        if (result) {
                            return;
                        }
                    }

                    if (MQuestHelperPlugin.getSelectedQuest().getCurrentStep() instanceof ConditionalStep) {
                        QuestStep conditionalStep = MQuestHelperPlugin.getSelectedQuest().getCurrentStep().getActiveStep();
                        applyStep(conditionalStep);
                    } else if (MQuestHelperPlugin.getSelectedQuest().getCurrentStep() instanceof NpcStep) {
                        applyNpcStep((NpcStep) MQuestHelperPlugin.getSelectedQuest().getCurrentStep());
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
        if (npc != null && Rs2Camera.isTileOnScreen(npc.getLocalLocation()) && Rs2Npc.hasLineOfSight(npc)) {
            Rs2Npc.interact(step.npcID, "Talk-to");
        } else if (npc != null && !Rs2Camera.isTileOnScreen(npc.getLocalLocation())) {
            Rs2Walker.walkTo(npc.getWorldLocation());
        } else if (npc != null && !Rs2Npc.hasLineOfSight(npc)) {
            Rs2Walker.walkTo(npc.getWorldLocation());
        } else {
            if (step.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 3) {
                Rs2Walker.walkTo(step.getWorldPoint());
                return false;
            }
        }
        return true;
    }


    public boolean applyObjectStep(ObjectStep step) {
        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo2D(step.getWorldPoint()) > 5) {
            Rs2Walker.walkTo(step.getWorldPoint());
            return false;
        }
        boolean success = Rs2GameObject.interact(step.objectID, true);
        if (!success) {
            for (int objectId : step.getAlternateObjectIDs()) {
                success = Rs2GameObject.interact(objectId, true);
                if (success) break;
            }
        }
//        if (!success) {
//            if (step.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 3) {
//                if (config.enableHybridWalking()) {
//                    Rs2Walker.walkTo(step.getWorldPoint(), config.useNearest());
//                } else {
//                    Rs2Walker.walkTo(step.getWorldPoint(), true);
//                }
//                return false;
//            }
//        }
        return true;
    }

    private boolean applyDetailedQuestStep(DetailedQuestStep conditionalStep) {
        if (conditionalStep instanceof NpcStep) return false;
        for (Rs2Item item : Rs2Inventory.items()) {
            for (Requirement requirement : conditionalStep.getRequirements()) {
                if (requirement instanceof ItemRequirement) {
                    ItemRequirement itemRequirement = (ItemRequirement) requirement;

                    if (itemRequirement.getAllIds().contains(item.id)) {
                        if (itemRequirement.shouldHighlightInInventory(Microbot.getClient())) {
                            Rs2Inventory.use(item.id);
                        }
                    }

                    if (!itemRequirement.getAllIds().contains(item.id) && conditionalStep.getWorldPoint() != null) {
                        if (Rs2Walker.canReach(conditionalStep.getWorldPoint())) {
                            Rs2GroundItem.loot(itemRequirement.getId());
                        } else {
                            Rs2Walker.walkTo(conditionalStep.getWorldPoint());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
