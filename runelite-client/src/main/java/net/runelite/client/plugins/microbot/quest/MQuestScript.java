package net.runelite.client.plugins.microbot.quest;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.cluescrolls.clues.emote.Emote;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.questhelper.QuestHelperPlugin;
import net.runelite.client.plugins.questhelper.requirements.Requirement;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.steps.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
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
                if (getQuestHelperPlugin().getSelectedQuest() == null) return;

                QuestStep questStep = getQuestHelperPlugin().getSelectedQuest().getCurrentStep().getActiveStep();
                if (questStep != null && Rs2Widget.isWidgetVisible(WidgetInfo.DIALOG_OPTION_OPTIONS)){
                    var dialogOptions = Rs2Widget.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS);
                    var dialogChoices = Arrays.asList(dialogOptions.getDynamicChildren());

                    for (var choice : questStep.getChoices().getChoices()){
                        if (choice.getExpectedPreviousLine() != null)
                            continue;

                        for (var dialogChoice : dialogChoices){
                            if (dialogChoice.getText().endsWith(choice.getChoice())){
                                Rs2Keyboard.keyPress(dialogChoice.getOnKeyListener()[7].toString().charAt(0));
                                return;
                            }
                        }
                    }
                }

                if (getQuestHelperPlugin().getSelectedQuest() != null && !Microbot.getClientThread().runOnClientThread(() -> getQuestHelperPlugin().getSelectedQuest().isCompleted())) {
                    Widget widget = Rs2Widget.findWidget("Start ");

                    if (Rs2Dialogue.isInDialogue()) {
                        Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                        return;
                    }

                    boolean isInCutscene = Microbot.getVarbitValue(4606) > 0;
                    if (isInCutscene) {
                        if (ShortestPathPlugin.getMarker() != null)
                            ShortestPathPlugin.exit();
                        return;
                    }

                    if (getQuestHelperPlugin().getSelectedQuest().getQuest().getId() == Quest.THE_RESTLESS_GHOST.getId()) {
                        if (Rs2Inventory.hasItem("ghostspeak amulet")) {
                            Rs2Inventory.wear("ghostspeak amulet");
                        }
                    }

                    if (getQuestHelperPlugin().getSelectedQuest().getQuest().getId() == Quest.RUNE_MYSTERIES.getId()) {
                        NPC aubury = Rs2Npc.getNpc("Aubury");
                        if (Rs2Inventory.hasItem("research package") && aubury != null) {
                            Rs2Npc.interact(aubury, "Talk-to");
                        }
                    }

                    if (getQuestHelperPlugin().getSelectedQuest().getQuest().getId() == Quest.COOKS_ASSISTANT.getId()) {
                        NPC aubury = Rs2Npc.getNpc("Aubury");
                        if (Rs2Inventory.hasItem("research package") && aubury != null) {
                            Rs2Npc.interact(aubury, "Talk-to");
                        }
                    }


                    /**
                     * This portion is needed when using item on another item in your inventory.
                     * If we do not prioritize this, the script will think we are missing items
                     */
                    if (questStep instanceof DetailedQuestStep && !(questStep instanceof NpcStep || questStep instanceof ObjectStep)) {
                        boolean result = applyDetailedQuestStep((DetailedQuestStep) getQuestHelperPlugin().getSelectedQuest().getCurrentStep().getActiveStep());
                        if (result) {
                            return;
                        }
                    }

                    if (getQuestHelperPlugin().getSelectedQuest().getCurrentStep() instanceof ConditionalStep) {
                        QuestStep conditionalStep = getQuestHelperPlugin().getSelectedQuest().getCurrentStep().getActiveStep();
                        applyStep(conditionalStep);
                    } else if (getQuestHelperPlugin().getSelectedQuest().getCurrentStep() instanceof NpcStep) {
                        applyNpcStep((NpcStep) getQuestHelperPlugin().getSelectedQuest().getCurrentStep());
                    } else if (getQuestHelperPlugin().getSelectedQuest().getCurrentStep() instanceof ObjectStep){
                        applyObjectStep((ObjectStep) getQuestHelperPlugin().getSelectedQuest().getCurrentStep());
                    }

                    sleepUntil(() -> Rs2Player.isInteracting() || Rs2Player.isMoving() || Rs2Player.isAnimating(), 1000);
                    sleepUntil(() -> !Rs2Player.isInteracting() && !Rs2Player.isMoving() && !Rs2Player.isAnimating());
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
        } else if (step instanceof WidgetStep){
            return applyWidgetStep((WidgetStep) step);
        } else if (step instanceof DetailedQuestStep) {
            return applyDetailedQuestStep((DetailedQuestStep) step);
        }
        return true;
    }

    public boolean applyNpcStep(NpcStep step) {
        for (var requirement : step.getRequirements()){
            if (requirement instanceof ItemRequirement){
                var itemRequirement = (ItemRequirement) requirement;

                if (Rs2Inventory.contains(itemRequirement.getId()) && ((ItemRequirement) requirement).isEquip())
                    Rs2Inventory.equip(itemRequirement.getId());
            }
        }

        net.runelite.api.NPC npc = Rs2Npc.getNpc(step.npcID);
        if (npc != null && Rs2Camera.isTileOnScreen(npc.getLocalLocation()) && Rs2Npc.hasLineOfSight(npc) && step.getText().stream().anyMatch(x -> x.contains("Kill"))) {
            if (!Rs2Combat.inCombat())
                Rs2Npc.interact(step.npcID, "Attack");
        } else if (npc != null && Rs2Camera.isTileOnScreen(npc.getLocalLocation()) && Rs2Npc.hasLineOfSight(npc)) {
            if (step instanceof NpcEmoteStep){
                var emoteStep = (NpcEmoteStep)step;

                for (Widget emoteWidget : Rs2Widget.getWidget(WidgetInfo.EMOTE_CONTAINER).getDynamicChildren())
                {
                    if (emoteWidget.getSpriteId() == emoteStep.getEmote().getSpriteId())
                    {
                        var id = emoteWidget.getOriginalX() / 42 + ((emoteWidget.getOriginalY() - 6) / 49) * 4;

                        Microbot.doInvoke(new NewMenuEntry("Perform", Emote.JIG.getName(), 1, MenuAction.CC_OP, id, ComponentID.EMOTES_EMOTE_CONTAINER, false), new Rectangle(0, 0, 1, 1));
                        Rs2Player.waitForAnimation();

                        if (Rs2Dialogue.isInDialogue())
                            return false;
                    }
                }
            }

            Rs2Npc.interact(step.npcID, "Talk-to");
        } else if (npc != null && !Rs2Camera.isTileOnScreen(npc.getLocalLocation())) {
            Rs2Walker.walkTo(npc.getWorldLocation(), 2);
        } else if (npc != null && !Rs2Npc.hasLineOfSight(npc)) {
            Rs2Walker.walkTo(npc.getWorldLocation(), 2);
        } else {
            if (step.getWorldPoint().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 3) {
                Rs2Walker.walkTo(step.getWorldPoint(), 2);
                return false;
            }
        }
        return true;
    }


    public boolean applyObjectStep(ObjectStep step) {
        var object = Rs2GameObject.getGameObjects(step.objectID, step.getWorldPoint()).stream().findFirst().orElse(null);
        var itemId = step.getIconItemID();

        if (object == null){
            var localPoint = LocalPoint.fromWorld(Microbot.getClient(), step.getWorldPoint());

            if (localPoint != null){
                var tile = Microbot.getClient().getScene().getTiles()[Microbot.getClient().getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];

                if (tile != null && tile.getWallObject() != null
                        && tile.getWallObject().getId() == step.objectID
                        && Rs2GameObject.hasLineOfSight(tile.getWallObject())
                        && (Rs2Camera.isTileOnScreen(tile.getWallObject()) || tile.getWallObject().getCanvasLocation() != null)){

                    if (itemId == -1)
                        Rs2GameObject.interact(tile.getWallObject());
                    else{
                        Rs2Inventory.use(itemId);
                        Rs2GameObject.interact(tile.getWallObject());
                    }
                    return false;
                }

                if (tile != null && tile.getDecorativeObject() != null
                        && tile.getDecorativeObject().getId() == step.objectID
                        && Rs2GameObject.hasLineOfSight(tile.getDecorativeObject())
                        && (Rs2Camera.isTileOnScreen(tile.getDecorativeObject()) || tile.getDecorativeObject().getCanvasLocation() != null)){
                    if (itemId == -1)
                        Rs2GameObject.interact(tile.getDecorativeObject());
                    else{
                        Rs2Inventory.use(itemId);
                        Rs2GameObject.interact(tile.getDecorativeObject());
                    }
                    return false;
                }
            }
        }

        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo2D(step.getWorldPoint()) > 1
                && (!Rs2GameObject.canWalkTo(object, 10) || !Rs2Camera.isTileOnScreen(object))) {
            Rs2Walker.walkTo(step.getWorldPoint(), 1);

            if (ShortestPathPlugin.getPathfinder() != null){
                var path = ShortestPathPlugin.getPathfinder().getPath();
                if (path.get(path.size() - 1).distanceTo(step.getWorldPoint()) <= 1)
                    return false;
            } else
                return false;
        }

        var success = false;
        if (Rs2GameObject.hasLineOfSight(object) || object != null && (Rs2Camera.isTileOnScreen(object) || object.getCanvasLocation() != null)){
            if (itemId == -1)
                success = Rs2GameObject.interact(object);
            else{
                Rs2Inventory.use(itemId);
                success = Rs2GameObject.interact(object);
            }
        }

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
                            Rs2Inventory.interact(item.id);
                        }
                    }

                    if (!itemRequirement.getAllIds().contains(item.id) && conditionalStep.getWorldPoint() != null) {
                        if (Rs2Walker.canReach(conditionalStep.getWorldPoint()) &&
                                (conditionalStep.getWorldPoint().distanceTo(Rs2Player.getWorldLocation()) < 2)
                                || conditionalStep.getWorldPoint().toWorldArea().hasLineOfSightTo(Microbot.getClient().getTopLevelWorldView(), Microbot.getClient().getLocalPlayer().getWorldLocation().toWorldArea())
                                && Rs2Camera.isTileOnScreen(LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), conditionalStep.getWorldPoint()))) {
                            Rs2GroundItem.loot(itemRequirement.getId());
                        } else {
                            Rs2Walker.walkTo(conditionalStep.getWorldPoint(), 2);
                        }
                        return true;
                    } else if (!itemRequirement.getAllIds().contains(item.id)){
                        Rs2GroundItem.loot(itemRequirement.getId());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean applyWidgetStep(WidgetStep step) {
        var widgetDetails = step.getWidgetDetails().get(0);
        var widget = Microbot.getClient().getWidget(widgetDetails.groupID, widgetDetails.childID);

        if (widgetDetails.childChildID != -1){
            var tmpWidget = widget.getChild(widgetDetails.childChildID);

            if (tmpWidget != null)
                widget = tmpWidget;
        }

        return Rs2Widget.clickWidget(widget.getId());
    }

    protected QuestHelperPlugin getQuestHelperPlugin() {
        return (QuestHelperPlugin)Microbot.getPluginManager().getPlugins().stream().filter(x -> x instanceof QuestHelperPlugin).findFirst().orElse(null);
    }
}
