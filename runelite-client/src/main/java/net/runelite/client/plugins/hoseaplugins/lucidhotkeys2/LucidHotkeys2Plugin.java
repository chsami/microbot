package net.runelite.client.plugins.hoseaplugins.lucidhotkeys2;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.ETileItem;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.item.SlottedItem;
import net.runelite.client.plugins.hoseaplugins.api.spells.Spells;
import net.runelite.client.plugins.hoseaplugins.api.spells.WidgetInfo;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import net.runelite.client.plugins.hoseaplugins.lucidhotkeys2.overlay.TileMarkersOverlay;
import net.runelite.client.plugins.hoseaplugins.lucidhotkeys2.overlay.UserVariablesPanelOverlay;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.Deque;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=\"#32CD32\">Lucid </font>Hotkeys 2</html>",
        description = "Setup hotkeys that can do a variety of different actions.",
        enabledByDefault = false,
        tags = {"hotkeys", "lucid"}
)
public class LucidHotkeys2Plugin extends Plugin implements KeyListener
{

    @Inject
    private Client client;

    @Inject
    private LucidHotkeys2Config config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private UserVariablesPanelOverlay userVariablesPanelOverlay;

    @Inject
    private TileMarkersOverlay tileMarkersOverlay;

    @Inject
    private OverlayManager overlayManager;

    private Logger log = LoggerFactory.getLogger(getName());

    public static final String GROUP_NAME = "lucid-hotkeys2";

    public static final File PRESET_DIR = new File(RUNELITE_DIR, GROUP_NAME);

    public static final String FILENAME_SPECIAL_CHAR_REGEX = "[^a-zA-Z\\d:]";

    public final GsonBuilder builder = new GsonBuilder()
            .setPrettyPrinting();
    public final Gson gson = builder.create();

    @Getter
    private Map<String, String> userVariables = new HashMap<>();

    private List<String> playerNamesTracked = new ArrayList<>();
    private List<String> npcNamesTracked = new ArrayList<>();

    @Getter
    private List<Player> playersTracked = new ArrayList<>();

    @Getter
    private List<NPC> npcsTracked = new ArrayList<>();

    @Getter
    private Map<WorldPoint, String> worldPointTileMarkers = new HashMap<>();

    @Getter
    private Map<LocalRegionTile, String> regionPointTileMarkers = new HashMap<>();

    private List<Projectile> validProjectiles = new ArrayList<>();

    private Map<Integer, ArrayList<Integer>> hotkeysToRun = new HashMap<>();

    private Map<Integer, Integer> playerAnimationTimes = new HashMap<>();

    private int tickDelay = 0;
    private int tickMetronomeMaxValue = 5;
    private int tickMetronomeCount = tickMetronomeMaxValue;
    private boolean lastOpSuccessful = false;
    private String lastNpcNameYouTargeted = "";
    private String lastPlayerNameYouTargeted = "";
    private String lastNpcNameTargetedYou = "";
    private String lastPlayerNameTargetedYou = "";
    private int lastPlayerAnimationTick = 0;
    private int lastPlayerAnimationId = -1;

    private Random random = new Random();


    // Search settings NPCs
    private boolean nPartialMatching = false;
    private boolean nHasTarget = false;
    private boolean nNoTarget = false;
    private boolean nTargetingYou = false;
    private boolean nNotTargetingYou = false;
    private int nFurtherThanDistance = 0;
    private int nWithinDistance = 50;
    private int nPerformingAnim = 0;

    // Search settings Players

    private boolean pPartialMatching = false;
    private boolean pHasTarget = false;
    private boolean pNoTarget = false;
    private boolean pTargetingYou = false;
    private boolean pNotTargetingYou = false;
    private int pFurtherThanDistance = 0;
    private int pWithinDistance = 50;
    private int pPerformingAnim = 0;
    private String pToIgnore = "";

    // Search settings Objects

    private boolean oPartialMatching = false;
    private int oFurtherThanDistance = 0;
    private int oWithinDistance = 50;
    private String oHasAction = "Any";

    // Search settings Items
    private boolean iPartialMatching = false;
    private boolean iIsNoted = false;
    private boolean iIsNotNoted = false;
    private boolean iIsStackable = false;
    private boolean iIsNotStackable = false;
    private String iHasAction = "Any";

    // Search settings Tile Items

    private boolean tiPartialMatching = false;
    private boolean tiIsNoted = false;
    private boolean tiIsNotNoted = false;
    private boolean tiIsStackable = false;
    private boolean tiIsNotStackable = false;

    // Search settings Tiles

    private int tFurtherThanDistance = 0;
    private int tWithinDistance = 50;
    private Object tNotUnderNpcs = "null";
    private Object tNotHavingSpotAnimIds = "null";
    private Object tNotHavingProjectileIds = "null";
    private Object tNpcsToDistanceCheck = "null";
    private int tFurtherThanNpcDistance = 0;
    private int tWithinNpcDistance = 50;



    @Provides
    LucidHotkeys2Config getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidHotkeys2Config.class);
    }

    @Override
    protected void startUp()
    {
        clientThread.invoke(this::pluginEnabled);

        resetObjectFilter();
        resetPlayerFilter();
        resetNpcFilter();
        resetItemFilter();
        resetTItemFilter();
        resetTileFilter();
        resetTrackedPlayers();
        resetTrackedNpcs();
    }

    private void pluginEnabled()
    {
        keyManager.registerKeyListener(this);

        initUserVariables();

        if (!overlayManager.anyMatch(p -> p == tileMarkersOverlay))
        {
            overlayManager.add(tileMarkersOverlay);
        }

        if (!overlayManager.anyMatch(p -> p == userVariablesPanelOverlay))
        {
            overlayManager.add(userVariablesPanelOverlay);
        }
    }

    @Override
    protected void shutDown()
    {
        log.info(getName() + " Stopped");

        keyManager.unregisterKeyListener(this);

        if (overlayManager.anyMatch(p -> p == tileMarkersOverlay))
        {
            overlayManager.remove(tileMarkersOverlay);
        }

        if (overlayManager.anyMatch(p -> p == userVariablesPanelOverlay))
        {
            overlayManager.remove(userVariablesPanelOverlay);
        }
    }

    @Subscribe
    private void onProjectileMoved(final ProjectileMoved event)
    {
        if (!validProjectiles.contains(event.getProjectile()))
        {
            validProjectiles.add(event.getProjectile());
        }
    }

    @Subscribe
    private void onAnimationChanged(final AnimationChanged event)
    {
        if (event.getActor() == client.getLocalPlayer())
        {
            if (client.getLocalPlayer().getAnimation() != -1)
            {
                lastPlayerAnimationTick = client.getTickCount();
                lastPlayerAnimationId = client.getLocalPlayer().getAnimation();
                playerAnimationTimes.put(client.getLocalPlayer().getAnimation(), client.getTickCount());
            }
        }
    }

    @Subscribe
    private void onInteractingChanged(final InteractingChanged event)
    {
        if (event.getSource() == client.getLocalPlayer())
        {
            Actor interacting = client.getLocalPlayer().getInteracting();
            if (interacting != null)
            {
                if (interacting instanceof NPC)
                {
                    NPC intNpc = (NPC) interacting;
                    lastNpcNameYouTargeted = intNpc.getName();
                }
                if (interacting instanceof Player)
                {
                    Player intPlayer = (Player) interacting;
                    lastPlayerNameYouTargeted = intPlayer.getName();
                }
            }
        }

        if (event.getTarget() != null && event.getTarget() == client.getLocalPlayer())
        {
            Actor target = event.getSource();
            if (target != null)
            {
                if (target instanceof NPC)
                {
                    NPC targNpc = (NPC) target;
                    lastNpcNameTargetedYou = targNpc.getName();
                }
                if (target instanceof Player)
                {
                    Player targPlayer = (Player) target;
                    lastPlayerNameTargetedYou = targPlayer.getName();
                }
            }
        }
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        if (tickDelay > 0)
        {
            tickDelay--;
        }

        validProjectiles.removeIf(projectile -> projectile.getRemainingCycles() <= 0);

        tickMetronome();

        updateTrackedPlayers();

        updateTrackedNpcs();

        tickBotkeys();

        evalHotkeysToRun();
    }

    private void tickBotkeys()
    {
        for (int i = 1; i < 16; i++)
        {
            if (isUseAsBot(i))
            {
                handleHotkey(i);
            }
        }
    }

    private void evalHotkeysToRun()
    {
        ArrayList<Integer> hotkeysToEvaluate = hotkeysToRun.get(client.getTickCount());
        if (hotkeysToEvaluate != null)
        {
            for (int hotkey : hotkeysToEvaluate)
            {
                handleHotkey(hotkey);
            }
        }

        hotkeysToRun.keySet().removeIf(i -> i <= client.getTickCount());
    }

    private void tickMetronome()
    {
        if (tickMetronomeCount > 0)
        {
            tickMetronomeCount--;
        }
        else if (tickMetronomeCount == 0)
        {
            tickMetronomeCount = tickMetronomeMaxValue;
        }
    }

    private void updateTrackedPlayers()
    {
        if (playersTracked != null)
        {
            playersTracked.clear();
        }

        if (playerNamesTracked.size() > 0)
        {
            for (String s : playerNamesTracked)
            {
                Player p = nearestPlayer(s);
                if (p != null)
                {
                    playersTracked.add(p);
                }
            }
        }
    }

    private void updateTrackedNpcs()
    {
        if (npcsTracked != null)
        {
            npcsTracked.clear();
        }

        if (npcNamesTracked.size() > 0)
        {
            for (String s : npcNamesTracked)
            {
                List<NPC> tracked = NpcUtils.getAll(npc -> npc.getName() != null && npc.getName().toLowerCase().contains(s.toLowerCase()));
                if (tracked != null && !tracked.isEmpty())
                {
                    npcsTracked.addAll(tracked);
                }
            }
        }
    }

    /**
     *
     * @param hotkeyIndex Index of hotkey, starting at 1 instead of 0
     */
    private void handleHotkey(int hotkeyIndex)
    {
        String rawConfig = cleanseInput(getHotkeyExpression(hotkeyIndex));

        if (!rawConfig.contains(";") || !rawConfig.contains("?"))
        {
            debugMessage("Missing a ; or ? in hotkey " + hotkeyIndex);
            return;
        }

        List<String> expressions = new ArrayList<>(Arrays.asList(rawConfig.split(";")));

        List<String> activationExpressions = new ArrayList<>();
        List<String> actionExpressions = new ArrayList<>();

        int expressionCount = 0;
        for (String expression : expressions)
        {
            if (expression.isEmpty() || expression.replaceAll(" ", "").equals(""))
            {
                debugMessage("Nothing to evaluate for expression " + expressionCount);
                continue;
            }

            if (expression.indexOf('?') <= 0)
            {
                debugMessage("Expression " + expressionCount + " is missing a ?");
                continue;
            }

            String activationExpression = expression.substring(0, expression.indexOf('?')).strip();
            String actionExpression = expression.substring(expression.indexOf('?') + 1).strip();

            activationExpressions.add(activationExpression);
            actionExpressions.add(actionExpression);

            expressionCount++;
        }

        if (activationExpressions.size() != actionExpressions.size())
        {
            debugMessage("Amount of activation conditions doesn't match amount of actions");
            return;
        }

        lastOpSuccessful = false;

        for (int expressionIndex = 0; expressionIndex < activationExpressions.size(); expressionIndex++)
        {
            String activationExpression = activationExpressions.get(expressionIndex);
            String actionExpression = actionExpressions.get(expressionIndex);

            int expressionActivationSuccessful = evaluateActivationCondition(activationExpression);

            String activationCleansed = activationExpression.replaceAll("<=", "less than or equal to").replaceAll("<", "less than");
            debugMessage("Hotkey[" + hotkeyIndex + "][" + expressionIndex + "]={" + activationCleansed + "} success = " + (expressionActivationSuccessful != -1 ? "false - error index = [" + expressionActivationSuccessful + "]" : "true"));

            lastOpSuccessful = expressionActivationSuccessful == -1;

            if (expressionActivationSuccessful != -1)
            {
                continue;
            }

            String[] actionExpressionSplit = actionExpression.split("&");
            for (String actionExpress : actionExpressionSplit)
            {
                evaluateAction(actionExpress.strip());
            }
        }
    }

    public int evaluateActivationCondition(String expression)
    {
        if (!containsComparisonOperator(expression))
        {
            debugMessage("Doesn't contain an expression to evaluate. Must contain " + "==, !=, >, >=, <, or <=");
            return -2;
        }

        if (expression.contains("&") && expression.contains("|"))
        {
            debugMessage("Expressions cannot use both a & and a | logical operator in 1 single expression.");
            return -2;
        }


        if (expression.contains("&"))
        {
            int index = 0;
            for (String comparison : expression.split("&"))
            {
                boolean comparisonSuccess = evaluateComparison(comparison);
                if (!comparisonSuccess)
                {
                    return index;
                }
                index++;
            }
            return -1;
        }
        else if (expression.contains("|"))
        {
            int index = 0;
            for (String comparison : expression.split("\\|"))
            {
                boolean comparisonSuccess = evaluateComparison(comparison);
                if (comparisonSuccess)
                {
                    return -1;
                }
                index++;
            }
            return index;
        }
        else
        {
            return evaluateComparison(expression) ? -1 : 0;
        }
    }

    private boolean evaluateComparison(String comparison)
    {
        String comparisonOperator = getComparisonOperatorFromExpression(comparison);
        String[] values = comparison.split(comparisonOperator);

        Object value1 = getValueFromString(values[0].strip());
        Object value2 = getValueFromString(values[1].strip());

        boolean bothValuesIntegers = value1 instanceof Integer && value2 instanceof Integer;

        switch (comparisonOperator)
        {
            case "==":
                return bothValuesIntegers ? (int) value1 == (int) value2 : String.valueOf(value1).equals(String.valueOf(value2));
            case "!=":
                return bothValuesIntegers ? (int) value1 != (int) value2 : !String.valueOf(value1).equals(String.valueOf(value2));
            case "~=":
                return String.valueOf(value1).contains(String.valueOf(value2));
            case ">":
                if (!bothValuesIntegers)
                {
                    debugMessage("Trying to use > comparison on values that arent both numbers.<br> Value 1=" + value1 + ", value2=" + value2);
                    return false;
                }
                return (int) value1 > (int) value2;
            case ">=":
                if (!bothValuesIntegers)
                {
                    debugMessage("Trying to use >= comparison on values that arent both numbers.<br> Value 1=" + value1 + ", value2=" + value2);
                    return false;
                }
                return (int) value1 >= (int) value2;
            case "<":
                if (!bothValuesIntegers)
                {
                    debugMessage("Trying to use < comparison on values that arent both numbers.<br> Value 1=" + value1 + ", value2=" + value2);
                    return false;
                }
                return (int) value1 < (int) value2;
            case "<=":
                if (!bothValuesIntegers)
                {
                    debugMessage("Trying to use <= comparison on values that arent both numbers.<br> Value 1=" + value1 + ", value2=" + value2);
                    return false;
                }
                return (int) value1 <= (int) value2;
        }

        return false;
    }

    public void evaluateAction(String action)
    {
        if (action.startsWith("do"))
        {
            handleAction(action.substring(2).strip());
        }
        else if (action.startsWith("set"))
        {
            handleSetExpression(action.substring(3).strip());
        }
        else
        {
            debugMessage("Action: " + action + " does not start with the keyword 'do' or 'set'");
        }
    }

    private void handleAction(String actionText)
    {
        // Split the action's params up
        String[] actionParams = actionText.split(",");

        String actionParam0 = actionParams[0].strip();
        Action action;
        if (isInteger(actionParam0))
        {
            action = Action.forId(Integer.parseInt(actionParam0));
        }
        else
        {
            action = Action.forNickname(actionParam0);
        }

        if (action == null)
        {
            debugMessage("Action couldn't be parsed from text: " + actionParam0);
            return;
        }

        if (action.getParamsNeeded() != actionParams.length)
        {
            if (action != Action.SEND_CLIENTSCRIPT || actionParams.length < 2)
            {
                debugMessage("Invalid param length for action: " + actionText);
                return;
            }
        }

        List<Object> params = new ArrayList<>();
        List<String> paramsRaw = new ArrayList<>();
        if (actionParams.length > 1)
        {
            for (int i = 1; i < actionParams.length; i++)
            {
                params.add(getValueFromString(actionParams[i].strip()));
                paramsRaw.add(actionParams[i].strip());
            }
        }

        executeAction(action, params, paramsRaw);
    }

    private void executeAction(Action action, List<Object> params, List<String> paramsRaw)
    {
        if (tickDelay > 0)
        {
            if (action == Action.RESET_DELAY || action == Action.PRINT_VARIABLE)
            {
                debugMessage("Tick delay is " + tickDelay + ", but executing action anyways");
            }
            else
            {
                debugMessage("Tick delay is " + tickDelay + ", not executing action");
                return;
            }
        }

        switch (action)
        {
            case PRINT_VARIABLE:
                handleVariablePrinting(paramsRaw.get(0));
                break;
            case SET_DELAY:
                tickDelay = (int) params.get(0);
                break;
            case RESET_DELAY:
                tickDelay = 0;
                break;
            case CLOSE_BANK:
                BankUtils.close();
                break;
            case WALK_RELATIVE_TO_SELF:
                InteractionUtils.walk(client.getLocalPlayer().getWorldLocation().dx((int) params.get(0)).dy((int) params.get(1)));
                break;
            case WALK_ABSOLUTE_LOCATION:
                InteractionUtils.walk(new WorldPoint((int) params.get(0), (int) params.get(1), client.getLocalPlayer().getWorldLocation().getPlane()));
                break;
            case INTERACT_NEAREST_NPC:
                interactNpc(params);
                break;
            case INTERACT_NEAREST_OBJECT:
                interactObject(params);
                break;
            case INTERACT_INVENTORY_ITEM:
                interactInventoryItem(params);
                break;
            case RELOAD_VARS:
                initUserVariables();
                break;
            case INTERACT_NEAREST_TILE_ITEM:
                interactTileItem(params);
                break;
            case SET_TICK_METRONOME_MAX_VALUE:
                tickMetronomeMaxValue = (int) params.get(0);
                break;
            case SET_TICK_METRONOME_VALUE:
                tickMetronomeCount = (int) params.get(0);
                break;
            case SET_TICK_METRONOME_TO_MAX:
                tickMetronomeCount = tickMetronomeMaxValue;
                break;
            case INTERACT_PLAYER:
                interactPlayer(params);
                break;
            case INTERACT_LAST_PLAYER_YOU_TARGETED:
                if (lastPlayerNameYouTargeted.isEmpty())
                {
                    break;
                }
                PlayerUtils.interactPlayer(lastPlayerNameYouTargeted, String.valueOf(params.get(0)));
                break;
            case INTERACT_LAST_PLAYER_TARGETED_YOU:
                if (lastPlayerNameTargetedYou.isEmpty())
                {
                    break;
                }
                PlayerUtils.interactPlayer(lastPlayerNameTargetedYou, String.valueOf(params.get(0)));
                break;
            case INTERACT_LAST_NPC_YOU_TARGETED:
                if (lastNpcNameYouTargeted.isEmpty())
                {
                    break;
                }
                NPC toTargetNpc = NpcUtils.getNearestNpc(lastNpcNameYouTargeted);
                if (toTargetNpc != null)
                {
                    NpcUtils.interact(toTargetNpc, String.valueOf(params.get(0)));
                }
                break;
            case INTERACT_LAST_NPC_TARGETED_YOU:
                if (lastNpcNameTargetedYou.isEmpty())
                {
                    break;
                }
                NPC toTargetNpc2 = NpcUtils.getNearestNpc(lastNpcNameTargetedYou);
                if (toTargetNpc2 != null)
                {
                    NpcUtils.interact(toTargetNpc2, String.valueOf(params.get(0)));
                }
                break;
            case ACTIVATE_PRAYER:
                Prayer pToActivate = CombatUtils.prayerForName(String.valueOf(params.get(0)));
                if (pToActivate != null)
                {
                    CombatUtils.activatePrayer(pToActivate);
                }
                break;
            case TOGGLE_PRAYER:
                Prayer pToToggle = CombatUtils.prayerForName(String.valueOf(params.get(0)));
                if (pToToggle != null)
                {
                    CombatUtils.togglePrayer(pToToggle);
                }
                break;
            case TOGGLE_SPEC:
                CombatUtils.toggleSpec();
                break;
            case WALK_SCENE_LOCATION:
                LocalPoint lp = LocalPoint.fromScene((int) params.get(0), (int) params.get(1), client.getTopLevelWorldView());
                InteractionUtils.walk(WorldPoint.fromLocalInstance(client, lp));
                break;
            case INTERACT_INVENTORY_SLOT:
                InventoryUtils.interactSlot((int) params.get(0), String.valueOf(params.get(1)));
                break;
            case ADD_TILE_MARKER_WORLD_POINT:
                addWorldPointTileMarker((int) params.get(0), (int) params.get(1), "");
                break;
            case ADD_TILE_MARKER_WORLD_POINT_WITH_TEXT:
                addWorldPointTileMarker((int) params.get(0), (int) params.get(1), String.valueOf(params.get(2)));
                break;
            case REMOVE_TILE_MARKER_WORLD_POINT:
                removeWorldPointTileMarker((int) params.get(0), (int) params.get(1));
                break;
            case ADD_TILE_MARKER_REGION_POINT:
                addRegionPointTileMarker((int) params.get(0), (int) params.get(1), (int) params.get(2), "");
                break;
            case ADD_TILE_MARKER_REGION_POINT_WITH_TEXT:
                addRegionPointTileMarker((int) params.get(0), (int) params.get(1), (int) params.get(2), String.valueOf(params.get(3)));
                break;
            case REMOVE_TILE_MARKER_REGION_POINT:
                removeRegionTileMarkerAt((int) params.get(0), (int) params.get(1), (int) params.get(2));
                break;
            case REMOVE_ALL_REGION_POINT_TILE_MARKERS:
                regionPointTileMarkers.clear();
                break;
            case REMOVE_ALL_WORLD_POINT_TILE_MARKERS:
                worldPointTileMarkers.clear();
                break;
            case ADD_TILE_MARKER_FOR_NPC_NAME:
                trackNpcName(String.valueOf(params.get(0)));
                break;
            case REMOVE_TILE_MARKER_FOR_NPC_NAME:
                npcNamesTracked.remove(String.valueOf(params.get(0)));
                break;
            case ADD_TILE_MARKER_FOR_PLAYER_NAME:
                trackPlayerName(String.valueOf(params.get(0)));
                break;
            case REMOVE_TILE_MARKER_FOR_PLAYER_NAME:
                playerNamesTracked.remove(String.valueOf(params.get(0)));
                break;
            case REMOVE_ALL_NPC_MARKERS:
                resetTrackedNpcs();
                break;
            case REMOVE_ALL_PLAYER_MARKERS:
                resetTrackedPlayers();
                break;
            case WALK_REGION_LOCATION:
                WorldPoint wp = WorldPoint.fromRegion((int) params.get(0), (int) params.get(1), (int) params.get(2), client.getLocalPlayer().getWorldLocation().getPlane());
                Collection<WorldPoint> localInstanceWp = WorldPoint.toLocalInstance(client.getTopLevelWorldView(), wp);
                localInstanceWp.stream().findFirst().ifPresent(InteractionUtils::walk);
                break;
            case ITEM_ON_ITEM:
                Item first = firstItem(String.valueOf(params.get(0)));
                Item second = firstItem(String.valueOf(params.get(1)));

                if (first == null)
                {
                    debugMessage("First item to be used is null");
                    return;
                }

                if (second == null)
                {
                    debugMessage("Second item to be used is null");
                    return;
                }

                InventoryUtils.itemOnItem(first, second);
                break;
            case ITEM_ON_NPC:
                Item firstItem = firstItem(String.valueOf(params.get(0)));
                NPC firstNpc = nearestNpc(String.valueOf(params.get(1)));

                if (firstItem == null)
                {
                    debugMessage("Item to use on npc is null");
                    return;
                }

                if (firstNpc == null)
                {
                    debugMessage("Npc to use item on is null");
                    return;
                }

                InteractionUtils.useItemOnNPC(firstItem.getId(), firstNpc);
                break;
            case ITEM_ON_OBJECT:
                Item itemToUse = firstItem(String.valueOf(params.get(0)));
                TileObject objectToUse = nearestObject(String.valueOf(params.get(1)));

                if (itemToUse == null)
                {
                    debugMessage("Item to use on object is null");
                    return;
                }

                if (objectToUse == null)
                {
                    debugMessage("Object to use item on is null");
                    return;
                }

                InteractionUtils.useItemOnWallObject(itemToUse, objectToUse);
                break;
            case WIDGET_ACTION:
                InteractionUtils.widgetInteract((int) params.get(0), (int) params.get(1), String.valueOf(params.get(2)));
                break;
            case WIDGET_SUBCHILD_ACTION:
                InteractionUtils.widgetInteract((int) params.get(0), (int) params.get(1), (int) params.get(2), String.valueOf(params.get(3)));
                break;
            case WIDGET_RESUME_PAUSE:
                InteractionUtils.queueResumePause((int) params.get(0), (int) params.get(1), (int) params.get(2));
                break;
            case EVALUATE_HOTKEY_IN_X_TICKS:
                ArrayList<Integer> hotkeysToAdd = new ArrayList<>();
                ArrayList<Integer> hotkeysPending = hotkeysToRun.get(client.getTickCount() + (int) params.get(1));
                if (hotkeysPending != null)
                {
                    hotkeysToAdd.addAll(hotkeysPending);
                }

                hotkeysToAdd.add((int) params.get(0));

                hotkeysToRun.put(client.getTickCount() + (int) params.get(1), hotkeysToAdd);
                break;
            case DEACTIVATE_PRAYER:
                Prayer pToDeactivate = CombatUtils.prayerForName(String.valueOf(params.get(0)));
                if (pToDeactivate != null)
                {
                    CombatUtils.deactivatePrayer(pToDeactivate);
                }
                break;
            case EVAL_HOTKEY_IMMEDIATELY:
                int hotkeyToEval = (int) params.get(0);
                if (hotkeyToEval > 0 && hotkeyToEval < 16)
                {
                    handleHotkey(hotkeyToEval);
                }
                break;
            case SET_NPC_FILTER:
                setNpcFilter((int) params.get(0) == 1, (int) params.get(1) == 1,
                        (int) params.get(2) == 1, (int) params.get(3) == 1,
                        (int) params.get(4) == 1, (int) params.get(5), (int) params.get(6), (int) params.get(7));
                break;
            case RESET_NPC_FILTER:
                resetNpcFilter();
                break;
            case SET_PLAYER_FILTER:
                setPlayerFilter((int) params.get(0) == 1, (int) params.get(1) == 1,
                        (int) params.get(2) == 1, (int) params.get(3) == 1,
                        (int) params.get(4) == 1, (int) params.get(5), (int) params.get(6), (int) params.get(7), String.valueOf(params.get(8)));
                break;
            case RESET_PLAYER_FILTER:
                resetPlayerFilter();
                break;
            case SET_OBJECT_FILTER:
                setObjectFilter((int) params.get(0) == 1, (int) params.get(1), (int) params.get(2), String.valueOf(params.get(3)));
                break;
            case RESET_OBJECT_FILTER:
                resetObjectFilter();
                break;
            case SET_ITEM_FILTER:
                setItemFilter((int) params.get(0) == 1, (int) params.get(1) == 1, (int) params.get(2) == 1, (int) params.get(3) == 1, (int) params.get(4) == 1, String.valueOf(params.get(5)));
                break;
            case RESET_ITEM_FILTER:
                resetItemFilter();
                break;
            case SET_TILE_ITEM_FILTER:
                setTItemFilter((int) params.get(0) == 1, (int) params.get(1) == 1, (int) params.get(2) == 1, (int) params.get(3) == 1, (int) params.get(4) == 1);
                break;
            case RESET_TILE_ITEM_FILTER:
                resetTItemFilter();
                break;
            case CAST_SPELL_ON_NPC:
                WidgetInfo spellInfo = Spells.spellInfoForName(String.valueOf(params.get(0)));
                if (spellInfo != null)
                {
                    if (!Spells.onCorrectSpellbook(String.valueOf(params.get(0))))
                    {
                        debugMessage("You're not on that spellbook!");
                        return;
                    }

                    Widget widget = client.getWidget(spellInfo.getPackedId());
                    NPC toCast = nearestNpc(String.valueOf(params.get(1)));

                    if (widget == null || toCast == null)
                    {
                        return;
                    }

                    InteractionUtils.useWidgetOnNPC(widget, toCast);
                }
                else
                {
                    debugMessage("Spell not recognized: " + params.get(0));
                }
                break;
            case CAST_SPELL:
                WidgetInfo spellInfo2 = Spells.spellInfoForName(String.valueOf(params.get(0)));
                if (spellInfo2 != null)
                {
                    if (!Spells.onCorrectSpellbook(String.valueOf(params.get(0))))
                    {
                        debugMessage("You're not on that spellbook!");
                        return;
                    }

                    Widget widget = client.getWidget(spellInfo2.getPackedId());
                    if (widget == null)
                    {
                        return;
                    }
                    MousePackets.queueClickPacket();
                    EthanApiPlugin.invoke(-1, spellInfo2.getPackedId(), MenuAction.CC_OP.getId(), 1, -1, client.getTopLevelWorldView().getId(), "", "", -1, -1);
                }
                else
                {
                    debugMessage("Spell not recognized: " + params.get(0));
                }
                break;
            case CAST_SPELL_ALT:
                WidgetInfo spellInfo3 = Spells.spellInfoForName(String.valueOf(params.get(0)));
                if (spellInfo3 != null)
                {
                    if (!Spells.onCorrectSpellbook(String.valueOf(params.get(0))))
                    {
                        debugMessage("You're not on that spellbook!");
                        return;
                    }

                    Widget widget = client.getWidget(spellInfo3.getPackedId());
                    if (widget == null)
                    {
                        return;
                    }
                    //EthanApiPlugin.invoke(int param0, int param1, int opcode, int id, int itemId, String option, String target, int canvasX, int canvasY)
                    MousePackets.queueClickPacket();
                    EthanApiPlugin.invoke(-1, spellInfo3.getPackedId(), MenuAction.CC_OP.getId(), 2, -1, client.getTopLevelWorldView().getId(), "", "", -1, -1);
                }
                else
                {
                    debugMessage("Spell not recognized: " + params.get(0));
                }
                break;
            case CAST_SPELL_ON_PLAYER:
                WidgetInfo spellInfo4 = Spells.spellInfoForName(String.valueOf(params.get(0)));
                if (spellInfo4 != null)
                {
                    if (!Spells.onCorrectSpellbook(String.valueOf(params.get(0))))
                    {
                        debugMessage("You're not on that spellbook!");
                        return;
                    }

                    Widget widget = client.getWidget(spellInfo4.getPackedId());
                    Player player = nearestPlayer(String.valueOf(params.get(1)));
                    if (widget == null || player == null)
                    {
                        return;
                    }
                    InteractionUtils.useWidgetOnPlayer(widget, player);
                }
                else
                {
                    debugMessage("Spell not recognized: " + params.get(0));
                }
                break;
            case CAST_SPELL_ON_OBJECT:
                WidgetInfo spellInfo5 = Spells.spellInfoForName(String.valueOf(params.get(0)));
                if (spellInfo5 != null)
                {
                    if (!Spells.onCorrectSpellbook(String.valueOf(params.get(0))))
                    {
                        debugMessage("You're not on that spellbook!");
                        return;
                    }

                    Widget widget = client.getWidget(spellInfo5.getPackedId());
                    TileObject object = nearestObject(String.valueOf(params.get(1)));
                    if (widget == null || object == null)
                    {
                        return;
                    }
                    InteractionUtils.useWidgetOnTileObject(widget, object);
                }
                else
                {
                    debugMessage("Spell not recognized: " + params.get(0));
                }
                break;
            case CAST_SPELL_ON_TILE_ITEM:
                WidgetInfo spellInfo6 = Spells.spellInfoForName(String.valueOf(params.get(0)));
                if (spellInfo6 != null)
                {
                    if (!Spells.onCorrectSpellbook(String.valueOf(params.get(0))))
                    {
                        debugMessage("You're not on that spellbook!");
                        return;
                    }

                    Widget widget = client.getWidget(spellInfo6.getPackedId());
                    ETileItem tileItem = nearestTileItem(String.valueOf(params.get(1)));
                    if (widget == null || tileItem == null)
                    {
                        return;
                    }
                    InteractionUtils.useWidgetOnTileItem(widget, tileItem);
                }
                else
                {
                    debugMessage("Spell not recognized: " + params.get(0));
                }
                break;
            case CAST_SPELL_ON_ITEM:
                WidgetInfo spellInfo7 = Spells.spellInfoForName(String.valueOf(params.get(0)));
                if (spellInfo7 != null)
                {
                    if (!Spells.onCorrectSpellbook(String.valueOf(params.get(0))))
                    {
                        debugMessage("You're not on that spellbook!");
                        return;
                    }

                    Item item = firstItem(String.valueOf(params.get(1)));
                    if (item == null)
                    {
                        return;
                    }

                    Widget widget = client.getWidget(spellInfo7.getPackedId());
                    Widget widget2 = InteractionUtils.getItemWidget(item);
                    if (widget == null || widget2 == null)
                    {
                        return;
                    }

                    InteractionUtils.useWidgetOnWidget(widget, widget2);
                }
                else
                {
                    debugMessage("Spell not recognized: " + params.get(0));
                }
                break;
            case WIDGET_CC_OP_1:
                int packedId = ((int) params.get(0) << 16)| (int) params.get(1);
                MousePackets.queueClickPacket();
                EthanApiPlugin.invoke(-1, packedId, MenuAction.CC_OP.getId(), 1, -1, client.getTopLevelWorldView().getId(), "", "", -1, -1);
                break;
            case WIDGET_CC_OP_2:
                int packedId2 = ((int) params.get(0) << 16)| (int) params.get(1);
                MousePackets.queueClickPacket();
                EthanApiPlugin.invoke(-1, packedId2, MenuAction.CC_OP.getId(), 2, -1, client.getTopLevelWorldView().getId(), "", "", -1, -1);
                break;
            case INVOKE_MENU_ACTION:
                MousePackets.queueClickPacket();
                EthanApiPlugin.invoke((int) params.get(0), (int) params.get(1), (int) params.get(2), (int) params.get(3), -1, client.getTopLevelWorldView().getId(), "", "", -1, -1);
                break;
            case SEND_CLIENTSCRIPT:
                debugMessage("Running script : " + params.get(0));
                client.runScript(params.toArray());
                break;
            case SET_TILE_FILTER:
                setTileFilter((int) params.get(0), (int) params.get(1), params.get(2), params.get(3), params.get(4), params.get(5), (int) params.get(6), (int) params.get(7));
                break;
            case RESET_TILE_FILTER:
                resetTileFilter();
                break;
            case SET_AUTO_COMBAT:
                setAutoCombat((int) params.get(0) == 1, (int) params.get(1) == 1, (int) params.get(2) == 1, (int) params.get(3) == 1, (int) params.get(4) == 1, String.valueOf(params.get(5)), String.valueOf(params.get(6)));
                break;
            case SET_PLUGIN_CONFIG:
                boolean isBool = params.get(2) instanceof Integer && ((int)params.get(2) == 1 || (int)params.get(2) == 0);
                boolean isInt = isInteger(String.valueOf(params.get(2)));
                configManager.setConfiguration(String.valueOf(params.get(0)), String.valueOf(params.get(1)), isBool ? (int) params.get(2) == 1 : isInt ? (int) params.get(2) : String.valueOf(params.get(1)));
                debugMessage("Set config: Group: " + params.get(0) + ", key: " + params.get(1) + ", value: " + params.get(2));
                break;
            case CLAIM_CLOSEST_CANNON:
                claimClosestCannon();
                break;
            case UNCLAIM_CANNON:
                unclaimCannon();
                break;
        }
    }

    private void claimClosestCannon()
    {
        TileObject closestCannon = GameObjectUtils.nearest("Dwarf multicannon");
        if (closestCannon != null)
        {
            String formattedLocation = (closestCannon.getWorldLocation().getX() - 1) + "|" + (closestCannon.getWorldLocation().getY() - 1) + "|" + closestCannon.getPlane();
            configManager.setConfiguration("lucid-cannon-reloader", "cannonLocation", formattedLocation);
        }
        else
        {
            unclaimCannon();
        }
    }

    private void unclaimCannon()
    {
        configManager.setConfiguration("lucid-cannon-reloader", "cannonLocation", "-1|-1|-1");
    }

    private void setAutoCombat(boolean running, boolean safespot, boolean looting, boolean alching, boolean thralls, String npcNames, String lootNames)
    {
        String group = "lucid-combat";
        configManager.setConfiguration(group, "autocombatEnabled", running);
        configManager.setConfiguration(group, "useSafespot", safespot);
        configManager.setConfiguration(group, "enableLooting", looting);
        configManager.setConfiguration(group, "alchStuff", alching);
        configManager.setConfiguration(group, "enableThralls", thralls);
        configManager.setConfiguration(group, "npcToFight", npcNames.equals("null") ? "" : npcNames);
        configManager.setConfiguration(group, "lootNames", lootNames.equals("null") ? "" : lootNames);
    }

    private boolean isAutoCombatEnabled()
    {
        return configManager.getConfiguration("lucid-combat", "autocombatEnabled") != null && Boolean.parseBoolean(configManager.getConfiguration("lucid-combat", "autocombatEnabled"));
    }

    private boolean isCannonClaimed()
    {
        return configManager.getConfiguration("lucid-cannon-reloader", "cannonLocation") != null && !configManager.getConfiguration("lucid-cannon-reloader", "cannonLocation").equals("-1|-1|-1");
    }

    private void resetNpcFilter()
    {
        nPartialMatching = false;
        nHasTarget = false;
        nNoTarget = false;
        nTargetingYou = false;
        nNotTargetingYou = false;
        nFurtherThanDistance = 0;
        nWithinDistance = 50;
        nPerformingAnim = 0;
    }

    private void setNpcFilter(boolean partialMatching, boolean hasTarget, boolean noTarget, boolean targetingYou, boolean notTargetingYou, int furtherThanDistance, int withinDistance, int performingAnim)
    {
        this.nPartialMatching = partialMatching;
        this.nHasTarget = hasTarget;
        this.nNoTarget = noTarget;
        this.nTargetingYou = targetingYou;
        this.nNotTargetingYou = notTargetingYou;
        this.nFurtherThanDistance = furtherThanDistance;
        this.nWithinDistance = withinDistance;
        this.nPerformingAnim = performingAnim;
    }

    private Predicate<NPC> npcFilter(Object nameOrId)
    {
        return (npc) -> {
            boolean any = nameOrId instanceof String && String.valueOf(nameOrId).equals("Any");
            NPCComposition comp = npc.getComposition();
            if (comp != null && npc.getComposition() != null && npc.getComposition().getConfigs() != null)
            {
                comp = npc.getComposition().transform();
            }

            boolean nameMatches = !(nameOrId instanceof String) || any || (nPartialMatching ? comp != null && comp.getName() != null &&  comp.getName().contains(String.valueOf(nameOrId)) : comp != null && comp.getName() != null && comp.getName().equals(String.valueOf(nameOrId)));
            boolean idMatches = !(nameOrId instanceof Integer) || (npc.getId() == (int) nameOrId);
            return (nameMatches &&
                    idMatches &&
                    (!nHasTarget || npc.getInteracting() != null) &&
                    (!nNoTarget || npc.getInteracting() == null) &&
                    (!nTargetingYou || npc.getInteracting() == client.getLocalPlayer()) &&
                    (!nNotTargetingYou || npc.getInteracting() != client.getLocalPlayer()) &&
                    InteractionUtils.distanceTo2DHypotenuse(npc.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), npc.getWorldArea().getWidth(), 1) > nFurtherThanDistance &&
                    InteractionUtils.distanceTo2DHypotenuse(npc.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), npc.getWorldArea().getWidth(), 1) < nWithinDistance) &&
                    (nPerformingAnim == 0 || npc.getAnimation() == nPerformingAnim);
        };
    }

    private void resetPlayerFilter()
    {
        pPartialMatching = false;
        pHasTarget = false;
        pNoTarget = false;
        pTargetingYou = false;
        pNotTargetingYou = false;
        pFurtherThanDistance = 0;
        pWithinDistance = 50;
        pPerformingAnim = 0;
        pToIgnore = "None";
    }

    private void setPlayerFilter(boolean partialMatching, boolean hasTarget, boolean noTarget, boolean targetingYou, boolean notTargetingYou, int furtherThanDistance, int withinDistance, int performingAnim, String toIgnore)
    {
        this.pPartialMatching = partialMatching;
        this.pHasTarget = hasTarget;
        this.pNoTarget = noTarget;
        this.pTargetingYou = targetingYou;
        this.pNotTargetingYou = notTargetingYou;
        this.pFurtherThanDistance = furtherThanDistance;
        this.pWithinDistance = withinDistance;
        this.pPerformingAnim = performingAnim;
        this.pToIgnore = toIgnore;
    }

    private Predicate<Player> playerFilter(String name)
    {
        return (player) -> {
            boolean any = name.equals("Any");
            boolean ignorePlayer = nameInArray(player.getName(), pToIgnore);
            boolean nameMatches = any || (pPartialMatching ? player.getName() != null && player.getName().contains(name) : player.getName() != null && player.getName().equals(name));
            return (nameMatches && !ignorePlayer &&
                    (!pHasTarget || player.getInteracting() != null) &&
                    (!pNoTarget || player.getInteracting() == null) &&
                    (!pTargetingYou || player.getInteracting() == client.getLocalPlayer()) &&
                    (!pNotTargetingYou || player.getInteracting() != client.getLocalPlayer()) &&
                    InteractionUtils.distanceTo2DHypotenuse(player.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) > pFurtherThanDistance &&
                    InteractionUtils.distanceTo2DHypotenuse(player.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < pWithinDistance) &&
                    (pPerformingAnim == 0 || player.getAnimation() == pPerformingAnim);
        };
    }

    private boolean nameInArray(String name, String array)
    {
        if (array.isBlank() || array.equals("None"))
        {
            return false;
        }

        String[] split = array.split(",");
        for (String p : split)
        {
            if (p.strip().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    private void resetObjectFilter()
    {
        oPartialMatching = false;
        oFurtherThanDistance = 0;
        oWithinDistance = 50;
        oHasAction = "Any";
    }

    private void setObjectFilter(boolean partialMatching, int furtherThanDistance, int withinDistance, String withAction)
    {
        this.oPartialMatching = partialMatching;
        this.oFurtherThanDistance = furtherThanDistance;
        this.oWithinDistance = withinDistance;
        this.oHasAction = withAction;
    }

    private Predicate<TileObject> objectFilter(Object nameOrId)
    {
        return (obj) -> {
            boolean any = nameOrId instanceof String && String.valueOf(nameOrId).equals("Any");
            ObjectComposition objComp = client.getObjectDefinition(obj.getId());
            if (objComp != null && objComp.getImpostorIds() != null)
            {
                objComp = objComp.getImpostor();
            }

            int sizeX = 1;
            int sizeY = 1;
            if (obj instanceof GameObject)
            {
                sizeX = ((GameObject) obj).sizeX();
                sizeY = ((GameObject) obj).sizeY();
            }

            boolean idMatches = !(nameOrId instanceof Integer) || (obj.getId() == (int) nameOrId);
            boolean nameMatches = !(nameOrId instanceof String) || any || (oPartialMatching ? objComp != null && objComp.getName() != null && objComp.getName().contains(String.valueOf(nameOrId)) : objComp != null && objComp.getName() != null && objComp.getName().equals(String.valueOf(nameOrId)));
            return  nameMatches &&
                    idMatches &&
                    InteractionUtils.distanceTo2DHypotenuse(obj.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), sizeX, sizeY, 1) > oFurtherThanDistance &&
                    InteractionUtils.distanceTo2DHypotenuse(obj.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), sizeX, sizeY, 1) < oWithinDistance &&
                    (oHasAction.equals("Any") || GameObjectUtils.hasAction(objComp != null ? objComp.getId() : obj.getId(), oHasAction));
        };
    }

    private void resetItemFilter()
    {
        iPartialMatching = false;
        iIsNoted = false;
        iIsNotNoted = false;
        iIsStackable = false;
        iIsNotStackable = false;
        iHasAction = "Any";
    }

    private void setItemFilter(boolean partialMatching, boolean isNoted, boolean isNotNoted, boolean isStackable, boolean isNotStackable, String hasAction)
    {
        this.iPartialMatching = partialMatching;
        this.iIsNoted = isNoted;
        this.iIsNotNoted = isNotNoted;
        this.iIsStackable = isStackable;
        this.iIsNotStackable = isNotStackable;
        this.iHasAction = hasAction;
    }

    private Predicate<Item> itemFilter(Object nameOrId)
    {
        return (item) -> {
            boolean any = nameOrId instanceof String && String.valueOf(nameOrId).equals("Any");
            ItemComposition comp = client.getItemDefinition(item.getId());
            boolean idMatches = !(nameOrId instanceof Integer) || (item.getId() == (int) nameOrId);
            boolean nameMatches = !(nameOrId instanceof String) || any || (iPartialMatching ? comp.getName() != null && comp.getName().contains(String.valueOf(nameOrId)) : comp.getName() != null && comp.getName().equals(String.valueOf(nameOrId)));

            return nameMatches &&
                    idMatches &&
                    (!iIsNoted || comp.getNote() != -1) &&
                    (!iIsNotNoted || comp.getNote() == -1) &&
                    (!iIsStackable || comp.isStackable()) &&
                    (!iIsNotStackable || !comp.isStackable()) &&
                    (iHasAction.equals("Any") || InventoryUtils.itemHasAction(item.getId(), iHasAction));
        };
    }

    private Predicate<SlottedItem> slottedItemFilter(Object nameOrId)
    {
        return (item) -> {
            boolean any = nameOrId instanceof String && String.valueOf(nameOrId).equals("Any");
            ItemComposition comp = client.getItemDefinition(item.getItem().getId());
            boolean idMatches = !(nameOrId instanceof Integer) || (item.getItem().getId() == (int) nameOrId);
            boolean nameMatches = !(nameOrId instanceof String) || any || (iPartialMatching ? comp.getName() != null && comp.getName().contains(String.valueOf(nameOrId)) : comp.getName() != null && comp.getName().equals(String.valueOf(nameOrId)));

            return nameMatches &&
                    idMatches &&
                    (!iIsNoted || comp.getNote() != 1) &&
                    (!iIsNotNoted || comp.getNote() == -1) &&
                    (!iIsStackable || comp.isStackable()) &&
                    (!iIsNotStackable || !comp.isStackable()) &&
                    (iHasAction.equals("Any") || InventoryUtils.itemHasAction(item.getItem().getId(), iHasAction));
        };
    }

    private void resetTileFilter()
    {
        tFurtherThanDistance = 0;
        tWithinDistance = 50;
        tNotUnderNpcs = "null";
        tNotHavingSpotAnimIds = "null";
        tNotHavingProjectileIds = "null";
        tNpcsToDistanceCheck = "null";
        tFurtherThanNpcDistance = 0;
        tWithinNpcDistance = 50;
    }

    private void setTileFilter(int tFurtherThanDistance, int tWithinDistance, Object tNotUnderNpcs, Object tNotHavingSpotAnimIds, Object tNotHavingProjectileIds, Object npcsToDistanceCheck, int tFurtherThanNpcDistance, int tWithinNpcDistance)
    {
        this.tFurtherThanDistance = tFurtherThanDistance;
        this.tWithinDistance = tWithinDistance;
        this.tNotUnderNpcs = tNotUnderNpcs;
        this.tNotHavingSpotAnimIds = tNotHavingSpotAnimIds;
        this.tNotHavingProjectileIds = tNotHavingProjectileIds;
        this.tNpcsToDistanceCheck = npcsToDistanceCheck;
        this.tFurtherThanNpcDistance = tFurtherThanNpcDistance;
        this.tWithinNpcDistance = tWithinNpcDistance;
    }

    private WorldPoint closestTile()
    {
        return InteractionUtils.getClosestFiltered(tileFilter());
    }

    private Predicate<Tile> tileFilter()
    {
        Deque<Projectile> projectiles = client.getTopLevelWorldView().getProjectiles();
        Deque<GraphicsObject> graphicsObjects = client.getTopLevelWorldView().getGraphicsObjects();
        List<NPC> notUnderNpcs;
        List<NPC> distanceNpcs;

        if (!tNotUnderNpcs.equals("null"))
        {
            notUnderNpcs = NpcUtils.getAll(npcFilter(tNotUnderNpcs));
        }
        else
        {
            notUnderNpcs = null;
        }

        if (!tNpcsToDistanceCheck.equals("null"))
        {
            distanceNpcs = NpcUtils.getAll(npcFilter(tNpcsToDistanceCheck));
        }
        else
        {
            distanceNpcs = null;
        }

        return (tile) -> {
            boolean furtherThanDistance = InteractionUtils.approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) > tFurtherThanDistance;
            boolean withinDistance = InteractionUtils.approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < tWithinDistance;

            boolean notUnderAnyNpcs = true;
            if (notUnderNpcs != null)
            {
                for (NPC npc : notUnderNpcs)
                {
                    if (npc.getWorldArea().contains(tile.getWorldLocation()))
                    {
                        notUnderAnyNpcs = false;
                        break;
                    }
                }
            }

            if (!notUnderAnyNpcs)
            {
                return false;
            }

            boolean distanceToNpcsGood = false;
            if (distanceNpcs != null)
            {
                for (NPC npc : distanceNpcs)
                {
                    int distance = (int) InteractionUtils.distanceTo2DHypotenuse(npc.getWorldLocation(), tile.getWorldLocation(), npc.getWorldArea().getWidth(), 1);
                    if (distance < tWithinNpcDistance && distance > tFurtherThanNpcDistance)
                    {
                        MessageUtils.addMessage("NPC " + npc.getName() + " Dist: " + distance, Color.RED);
                        distanceToNpcsGood = true;
                        break;
                    }

                }
            }

            if (!distanceToNpcsGood)
            {
                return false;
            }

            boolean notContainingSpotAnims = true;
            if (graphicsObjects != null)
            {
                for (GraphicsObject graphicsObject : graphicsObjects)
                {
                    if (graphicsObject.getLocation() == null)
                    {
                        continue;
                    }

                    String toAvoid = String.valueOf(tNotHavingSpotAnimIds);
                    String[] split = toAvoid.split(",");
                    boolean match = false;
                    for (String s : split)
                    {
                        if (isInteger(s.strip()))
                        {
                            int id = Integer.parseInt(s.strip());
                            if (graphicsObject.getLocation().equals(LocalPoint.fromWorld(client.getTopLevelWorldView(), tile.getWorldLocation())) && graphicsObject.getId() == id)
                            {
                                match = true;
                                break;
                            }
                        }
                    }

                    if (match)
                    {
                        notContainingSpotAnims = false;
                        break;
                    }
                }
            }

            if (!notContainingSpotAnims)
            {
                return false;
            }

            boolean notContainingProjectiles = true;
            if (projectiles != null)
            {
                for (Projectile projectile : projectiles)
                {
                    if (projectile.getTarget() == null)
                    {
                        continue;
                    }

                    String toAvoid = String.valueOf(tNotHavingProjectileIds);
                    String[] split = toAvoid.split(",");
                    boolean match = false;
                    for (String s : split)
                    {
                        if (isInteger(s.strip()))
                        {
                            int id = Integer.parseInt(s.strip());
                            if (projectile.getTarget().equals(LocalPoint.fromWorld(client.getTopLevelWorldView(), tile.getWorldLocation())) && projectile.getId() == id)
                            {
                                match = true;
                                break;
                            }
                        }
                    }

                    if (match)
                    {
                        notContainingProjectiles = false;
                        break;
                    }
                }
            }

            if (!notContainingProjectiles)
            {
                return false;
            }

            return InteractionUtils.isWalkable(tile.getWorldLocation()) &&
                    furtherThanDistance &&
                    withinDistance;
        };
    }

    private void resetTItemFilter()
    {
        this.tiPartialMatching = false;
        this.tiIsNoted = false;
        this.tiIsNotNoted = false;
        this.tiIsStackable = false;
        this.tiIsNotStackable = false;
    }

    private void setTItemFilter(boolean partialMatching, boolean isNoted, boolean isNotNoted, boolean isStackable, boolean isNotStackable)
    {
        this.tiPartialMatching = partialMatching;
        this.tiIsNoted = isNoted;
        this.tiIsNotNoted = isNotNoted;
        this.tiIsStackable = isStackable;
        this.tiIsNotStackable = isNotStackable;
    }

    private Predicate<ETileItem> tileItemFilter(Object nameOrId)
    {
        return (tItem) -> {
            boolean any = nameOrId instanceof String && String.valueOf(nameOrId).equals("Any");
            ItemComposition comp = client.getItemDefinition(tItem.getTileItem().getId());
            boolean idMatches = !(nameOrId instanceof Integer) || (tItem.getTileItem().getId() == (int) nameOrId);
            boolean nameMatches = !(nameOrId instanceof String) || any || (tiPartialMatching ? comp.getName() != null && comp.getName().contains(String.valueOf(nameOrId)) : comp.getName() != null && comp.getName().equals(String.valueOf(nameOrId)));

            return nameMatches &&
                  idMatches &&
                  (!tiIsNoted || comp.getNote() != 1) &&
                  (!tiIsNotNoted || comp.getNote() == -1) &&
                  (!tiIsStackable || comp.isStackable()) &&
                  (!tiIsNotStackable || !comp.isStackable());
        };
    }

    private NPC nearestNpc(String toSplit)
    {
        List<NPC> npcs = new ArrayList<>();

        String[] nSplit = toSplit.split(",");
        for (String s : nSplit)
        {
            NPC nearest;
            if (isInteger(s.strip()))
            {
                nearest = NpcUtils.getNearestNpc(npcFilter(Integer.parseInt(s.strip())));
            }
            else
            {
                nearest = NpcUtils.getNearestNpc(npcFilter(String.valueOf(s.strip())));
            }

            if (nearest != null)
            {
                npcs.add(nearest);
            }
        }

        if (npcs.size() > 1)
        {
            npcs.sort((n1, n2) -> Float.compare(InteractionUtils.distanceTo2DHypotenuse(n1.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), n1.getWorldArea().getWidth(), 1), InteractionUtils.distanceTo2DHypotenuse(n2.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), n2.getWorldArea().getWidth(), 1)));
        }

        if (npcs.size() > 0)
        {
            return npcs.get(0);
        }

        return null;
    }

    private void interactNpc(List<Object> params)
    {
        NPC toInteract = nearestNpc(String.valueOf(params.get(0)));
        if (toInteract != null)
        {
            NpcUtils.interact(toInteract, String.valueOf(params.get(1)));
        }
    }

    private TileObject nearestDynamicObject(String toSplit)
    {
        List<TileObject> tileObjects = new ArrayList<>();

        String[] nSplit = String.valueOf(toSplit).split(",");
        for (String s : nSplit)
        {
            TileObject tileObject;
            if (isInteger(s.strip()))
            {
                tileObject = GameObjectUtils.nearest(objectFilter(Integer.parseInt(s.strip())));
            }
            else
            {
                tileObject = GameObjectUtils.nearest(objectFilter(String.valueOf(s.strip())));
            }

            if (tileObject instanceof GameObject && ((GameObject) tileObject).getRenderable() instanceof DynamicObject)
            {
                tileObjects.add(tileObject);
            }
        }

        if (tileObjects.size() > 1)
        {
            tileObjects.sort((t1, t2) -> Float.compare(InteractionUtils.distanceTo2DHypotenuse(t1.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), ((GameObject)t1).sizeX(), ((GameObject)t1).sizeY(), 1), InteractionUtils.distanceTo2DHypotenuse(t2.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), ((GameObject) t2).sizeX(), ((GameObject) t2).sizeY(), 1)));
        }

        if (tileObjects.size() > 0)
        {
            return tileObjects.get(0);
        }

        return null;
    }
    
    private TileObject nearestObject(String toSplit)
    {
        List<TileObject> tileObjects = new ArrayList<>();

        String[] nSplit = String.valueOf(toSplit).split(",");
        for (String s : nSplit)
        {
            TileObject tileObject;
            if (isInteger(s.strip()))
            {
                tileObject = GameObjectUtils.nearest(objectFilter(Integer.parseInt(s.strip())));
            }
            else
            {
                tileObject = GameObjectUtils.nearest(objectFilter(s.strip()));
            }

            if (tileObject != null)
            {
                tileObjects.add(tileObject);
            }
        }

        if (tileObjects.size() > 1)
        {
            tileObjects.sort((t1, t2) -> Float.compare(InteractionUtils.distanceTo2DHypotenuse(t1.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), ((GameObject)t1).sizeX(), ((GameObject)t1).sizeY(), 1), InteractionUtils.distanceTo2DHypotenuse(t2.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), ((GameObject) t2).sizeX(), ((GameObject) t2).sizeY(), 1)));
        }

        if (tileObjects.size() > 0)
        {
            return tileObjects.get(0);
        }

        return null;
    }

    private void interactObject(List<Object> params)
    {
        TileObject object = nearestObject(String.valueOf(params.get(0)));

        if (object != null)
        {
            GameObjectUtils.interact(object, String.valueOf(params.get(1)));
        }
    }

    private Item firstItem(String toSplit)
    {
        List<Item> items = new ArrayList<>();

        String[] nSplit = toSplit.split(",");
        for (String s : nSplit)
        {
            Item item;
            if (isInteger(s.strip()))
            {
                debugMessage("Item id: " + s.strip());
                item = InventoryUtils.getFirstItem(itemFilter(Integer.parseInt(s.strip())));
            }
            else
            {
                debugMessage("Item name: " + s.strip());
                item = InventoryUtils.getFirstItem(itemFilter(s.strip()));
            }

            if (item != null)
            {
                items.add(item);
            }
        }

        if (items.size() > 1)
        {
            items.sort((i1, i2) -> getItemIndex(i1.getId(), 149, 0) - getItemIndex(i2.getId(), 149, 0));
        }

        if (items.size() > 0)
        {
            return items.get(0);
        }

        return null;
    }

    private void interactInventoryItem(List<Object> params)
    {
        Item toInteract = firstItem(String.valueOf(params.get(0)));
        if (toInteract != null)
        {
            InventoryUtils.itemInteract(toInteract.getId(), String.valueOf(params.get(1)));
        }
    }

    private Player nearestPlayer(String playerName)
    {
        List<Player> players = new ArrayList<>();

        String[] nSplit = playerName.split(",");

        for (String pName : nSplit)
        {
            Player p = PlayerUtils.getNearest(playerFilter(pName));

            if (p != null)
            {
                players.add(p);
            }
        }

        if (players.size() > 1)
        {
            players.sort((p1, p2) -> Float.compare(InteractionUtils.distanceTo2DHypotenuse(p1.getWorldLocation(), client.getLocalPlayer().getWorldLocation()), InteractionUtils.distanceTo2DHypotenuse(p2.getWorldLocation(), client.getLocalPlayer().getWorldLocation())));
        }

        if (players.size() > 0)
        {
            return players.get(0);
        }

        return null;
    }

    private void interactPlayer(List<Object> params)
    {
        Player player = nearestPlayer(String.valueOf(params.get(0)));
        if (player != null)
        {
            PlayerUtils.interactPlayer(player.getName(), String.valueOf(params.get(1)));
        }
    }

    private ETileItem nearestTileItem(String toSplit)
    {
        List<ETileItem> tileItems = new ArrayList<>();

        String[] nSplit = toSplit.split(",");
        for (String s : nSplit)
        {
            ETileItem tItem;

            if (isInteger(s.strip()))
            {
                tItem = InteractionUtils.nearestTileItem(tileItemFilter(Integer.parseInt(s.strip()))).orElse(null);
            }
            else
            {
                tItem = InteractionUtils.nearestTileItem(tileItemFilter(String.valueOf(s.strip()))).orElse(null);
            }

            if (tItem != null)
            {
                tileItems.add(tItem);
            }
        }

        if (tileItems.size() > 1)
        {
            tileItems.sort((t1, t2) -> Float.compare(InteractionUtils.distanceTo2DHypotenuse(t1.getLocation(), client.getLocalPlayer().getWorldLocation()), InteractionUtils.distanceTo2DHypotenuse(t2.getLocation(), client.getLocalPlayer().getWorldLocation())));
        }

        if (tileItems.size() > 0)
        {
            return tileItems.get(0);
        }

        return null;
    }

    private void interactTileItem(List<Object> params)
    {
        ETileItem tileItem = nearestTileItem(String.valueOf(params.get(0)));

        if (tileItem != null)
        {
            InteractionUtils.interactWithTileItem(tileItem, String.valueOf(params.get(1)));
        }
    }

    private void trackPlayerName(String playerName)
    {
        if (!playerNamesTracked.contains(playerName))
        {
            playerNamesTracked.add(playerName);
        }
    }

    private void trackNpcName(String npcName)
    {
        if (!npcNamesTracked.contains(npcName))
        {
            npcNamesTracked.add(npcName);
        }
    }

    private void resetTrackedPlayers()
    {
        playerNamesTracked.clear();
    }

    private void resetTrackedNpcs()
    {
        npcNamesTracked.clear();
    }

    private void addRegionPointTileMarker(int regionId, int x, int y, String text)
    {
        boolean inList = false;
        for (LocalRegionTile p : regionPointTileMarkers.keySet())
        {
            if (p.getLpX() == x && p.getLpY() == y)
            {
                inList = true;
                break;
            }
        }

        if (!inList)
        {
            regionPointTileMarkers.put(new LocalRegionTile(regionId, x, y), text);
        }
    }

    private void addWorldPointTileMarker(int worldX, int worldY, String text)
    {
        boolean inList = false;
        for (WorldPoint wp : worldPointTileMarkers.keySet())
        {
            if (wp.getX() == worldX && wp.getY() == worldY)
            {
                inList = true;
                break;
            }
        }

        if (!inList)
        {
            worldPointTileMarkers.put(new WorldPoint(worldX, worldY, client.getLocalPlayer().getWorldLocation().getPlane()), text);
        }
    }

    private void removeRegionTileMarkerAt(int regionId, int x, int y)
    {
        regionPointTileMarkers.keySet().removeIf(p -> p.getRegionId() == regionId && p.getLpX() == x && p.getLpY() == y);
    }

    private void removeWorldPointTileMarker(int worldX, int worldY)
    {
        worldPointTileMarkers.keySet().removeIf(wp -> wp.getX() == worldX && wp.getY() == worldY);
    }

    private void handleSetExpression(String expression)
    {
        if (!expression.contains("="))
        {
            debugMessage("No = sign in expression: " + expression);
            return;
        }

        if (expression.startsWith("$"))
        {
            debugMessage("You can't set the value of global variables");
            return;
        }

        String[] expParams = expression.split("=");

        if (!expression.startsWith("%"))
        {
            debugMessage(expParams[0].strip() + " is not a valid variable name");
            return;
        }

        String varName = expParams[0].strip().substring(1);
        String arithExp = expParams[1].strip();
        String arithSign = getArithmeticSign(arithExp);

        String valToSetTo = "null";
        if (!arithSign.isEmpty())
        {
            String[] vals = splitExpression(arithSign, arithExp);
            if (vals.length != 2)
            {
                debugMessage("Invalid arithmetic expression: " + arithExp);
                return;
            }

            valToSetTo = evalArithmeticExpression(arithSign, getValueFromString(vals[0].strip()), getValueFromString(vals[1].strip()));

            debugMessage("Exp eval for:" + arithExp + " = " + valToSetTo);
        }
        else if (arithExp.startsWith("!"))
        {
            arithExp = arithExp.substring(1);

            String varName2 = arithExp.strip();
            Object var2Val = getValueFromString(varName2);
            if (var2Val instanceof Integer)
            {
                int varVal = (int) var2Val;
                if (varVal == 0)
                {
                    valToSetTo = "1";
                }
                else if (varVal == 1)
                {
                    valToSetTo = "0";
                }
                else
                {
                    debugMessage("Can't flip boolean value of a non-binary number");
                    return;
                }
            }
        }
        else if (arithExp.startsWith("-"))
        {
            String varName3 = arithExp.substring(1).strip();
            Object value = getValueFromString(varName3);
            if (value instanceof Integer)
            {
                valToSetTo = intAsString(-(int) value);
            }
        }
        else
        {
            String varName3 = arithExp.strip();
            valToSetTo = String.valueOf(getValueFromString(varName3));
        }

        setUserVariableValue(varName.strip(), valToSetTo);
    }

    private String[] splitExpression(String split, String expression)
    {
        boolean negRemoved = expression.startsWith("-");
        boolean moduloAtBeggining = expression.startsWith("%");
        expression = expression.substring(negRemoved || moduloAtBeggining ? 1 : 0);

        int indexOf = expression.indexOf(split);
        String leftSide = expression.substring(0, indexOf);
        String rightSide = expression.substring(indexOf + 1);

        if (moduloAtBeggining)
        {
            leftSide = "%" + leftSide;
        }

        if (negRemoved)
        {
            leftSide = "-" + leftSide;
        }

        return new String[]{leftSide, rightSide};
    }

    private String evalArithmeticExpression(String sign, Object val1, Object val2)
    {
        String reason = "";
        switch (sign)
        {
            case "+":
                if (val1 instanceof Integer && val2 instanceof Integer)
                {
                    return intAsString((int)val1 + (int)val2);
                }
                else
                {
                    return String.valueOf(val1).concat(String.valueOf(val2));
                }
            case "-":
                if (val1 instanceof Integer && val2 instanceof Integer)
                {
                    return intAsString((int)val1 - (int)val2);
                }
                else
                {
                    if (!(val1 instanceof Integer))
                    {
                        reason = "Val1 isn't an int.";
                    }
                    if (!(val2 instanceof Integer))
                    {
                        reason = "Val2 isn't an int.";
                    }
                }
                break;
            case "*":
                if (val1 instanceof Integer && val2 instanceof Integer)
                {
                    return intAsString((int)val1 * (int)val2);
                }
                else
                {
                    if (!(val1 instanceof Integer))
                    {
                        reason = "Val1 isn't an int.";
                    }
                    if (!(val2 instanceof Integer))
                    {
                        reason = "Val2 isn't an int.";
                    }
                }
                break;
            case "/":
                if (val1 instanceof Integer && val2 instanceof Integer)
                {
                    if ((int) val2 == 0)
                    {
                        return "Naughty naughty, can't divide by 0";
                    }
                    return intAsString((int)val1 / (int)val2);
                }
                else
                {
                    if (!(val1 instanceof Integer))
                    {
                        reason = "Val1 isn't an int.";
                    }
                    if (!(val2 instanceof Integer))
                    {
                        reason = "Val2 isn't an int.";
                    }
                }
                break;
            case "%":
                if (val1 instanceof Integer && val2 instanceof Integer)
                {
                    if ((int) val2 == 0)
                    {
                        return "Naughty naughty, can't divide by 0";
                    }
                    return intAsString((int)val1 % (int)val2);
                }
                else
                {
                    if (!(val1 instanceof Integer))
                    {
                        reason = "Val1 isn't an int.";
                    }
                    if (!(val2 instanceof Integer))
                    {
                        reason = "Val2 isn't an int.";
                    }
                }
                break;
        }

        return "Cannot do " + sign + " on " + val1 + " and " + val2 + (reason.isEmpty() ? "" : " because " + reason);
    }

    private String getArithmeticSign(String eval)
    {
        if (eval.startsWith("!%"))
        {
            eval = eval.substring(2);
        }

        if (eval.startsWith("-") || eval.startsWith("%") || eval.startsWith("!"))
        {
            eval = eval.substring(1);
        }

        if (eval.contains(" + "))
        {
            return "+";
        }
        else if (eval.contains(" - "))
        {
            return "-";
        }
        else if (eval.contains(" * "))
        {
            return "*";
        }
        else if (eval.contains(" / "))
        {
            return "/";
        }
        else if (eval.contains(" % "))
        {
            return "%";
        }

        return "";
    }

    public Object getValueFromString(String varToken)
    {
        String varValue = "";

        if (isGlobalVariable(varToken) || isUserVariable(varToken))
        {
            varValue = getVariableValue(varToken);
        }
        else
        {
            varValue = varToken;
        }

        if (varValue.equals("true"))
        {
            varValue = "1";
        }
        else if(varValue.equals("false"))
        {
            varValue = "0";
        }

        if (isInteger(varValue))
        {
            return Integer.parseInt(varValue);
        }

        return varValue;
    }

    private void handleVariablePrinting(String var)
    {
        Object val = getValueFromString(var);
        MessageUtils.addMessage("Var: " + var + " value {" + val + "}", Color.RED);
    }

    private int ticksSinceLastPlayerAnimation()
    {
        return client.getTickCount() - lastPlayerAnimationTick;
    }

    private int ticksSinceAnimation(int id)
    {
        int animTick = playerAnimationTimes.getOrDefault(id, client.getTickCount() + 1);
        return client.getTickCount() - animTick;
    }

    private boolean isGlobalVariable(String varName)
    {
        if (!varName.startsWith("$"))
        {
            return false;
        }

        // Trim off the $
        varName = varName.substring(1);

        // Remove coordinate references
        if (varName.endsWith(".x") || varName.endsWith(".y") || varName.endsWith(".z"))
        {
            varName = varName.substring(0, varName.length() - 2);
        }

        // Remove array index ending
        if (varName.contains("[") && varName.contains("]"))
        {
            varName = varName.substring(0, varName.indexOf("["));
        }

        if (globalVars.contains(varName))
        {
            return true;
        }
        else
        {
            for (String dynamicVar : dynamicGlobalVars)
            {
                if (varName.startsWith(dynamicVar) && varName.length() > dynamicVar.length() + 1)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private final List<String> globalVars = List.of(
        "lastOpSuccessful", "pwLoc", "prLoc", "psLoc", "runEnergy", "specEnergy", "isMoving",
        "isRunEnabled", "isSpecEnabled", "tickCount", "tickDelay", "isBankOpen", "isGeOpen", "metronomeMax", "metronomeTick",
        "regionId", "iRegionId", "prayPoints", "curHpPercent", "curHpLvl", "maxHpLvl", "currentAnimId", "lastAnimId",
        "lastAnimTick", "invSlotsUsed", "invSlotsFree", "tSinceLastAnim", "npcTargetingYou", "npcsTargetingYou",
        "npcBeingTargeted", "playerTargetingYou", "playersTargetingYou", "playerBeingTargeted", "targetHpPercent",
        "targetAnimId", "projsTargetYou", "projsTargetYourTile", "lastNpcYouTargeted", "lastPlayerYouTargeted",
        "lastNpcTargetedYou", "lastPlayerTargetedYou", "numNpcsTargetYou", "numPlayersTargetYou", "numProjsTargetYou",
        "numProjsTargetYourTile", "equipNames", "invNames", "equipIds", "invIds", "hintArrowWLoc", "hintArrowRLoc",
        "hintArrowNpcWLoc", "hintArrowNpcRLoc", "filteredTile", "distToFilteredTile", "isAutoCombatEnabled", "isCannonClaimed"
    );

    private String getGlobalVariableValue(String varName)
    {
        WorldPoint lpwp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());

        switch (varName)
        {
            case "lastOpSuccessful":
                return booleanAsString(lastOpSuccessful);
            case "pwLoc":
                return lpwp.getX() + "|" + lpwp.getY() + "|" + lpwp.getPlane();
            case "prLoc":
                return lpwp.getRegionX() + "|" + lpwp.getRegionY() + "|" + lpwp.getPlane();
            case "psLoc":
                return client.getLocalPlayer().getLocalLocation().getSceneX() + "|" + client.getLocalPlayer().getLocalLocation().getSceneY() + "|" + client.getLocalPlayer().getWorldLocation().getPlane();
            case "runEnergy":
                return intAsString(InteractionUtils.getRunEnergy());
            case "specEnergy":
                return intAsString(CombatUtils.getSpecEnergy());
            case "isMoving":
                return booleanAsString(InteractionUtils.isMoving());
            case "isRunEnabled":
                return booleanAsString(InteractionUtils.isRunEnabled());
            case "isSpecEnabled":
                return booleanAsString(CombatUtils.isSpecEnabled());
            case "tickCount":
                return intAsString(client.getTickCount());
            case "tickDelay":
                return intAsString(tickDelay);
            case "isBankOpen":
                return booleanAsString(BankUtils.isOpen());
            case "isGeOpen":
                return booleanAsString(!InteractionUtils.isWidgetHidden(465, 1));
            case "metronomeMax":
                return intAsString(tickMetronomeMaxValue);
            case "metronomeTick":
                return intAsString(tickMetronomeCount);
            case "regionId":
                return intAsString(lpwp.getRegionID());
            case "iRegionId":
                return intAsString(client.getLocalPlayer().getWorldLocation().getRegionID());
            case "prayPoints":
                return intAsString(client.getBoostedSkillLevel(Skill.PRAYER));
            case "curHpPercent":
                int hp1 = client.getBoostedSkillLevel(Skill.HITPOINTS);
                int hp2 = client.getRealSkillLevel(Skill.HITPOINTS);
                int playerHpPercent = hp1 * 100 / hp2;
                return intAsString(playerHpPercent);
            case "curHpLvl":
                return intAsString(client.getBoostedSkillLevel(Skill.HITPOINTS));
            case "maxHpLvl":
                return intAsString(client.getRealSkillLevel(Skill.HITPOINTS));
            case "currentAnimId":
                return intAsString(client.getLocalPlayer().getAnimation());
            case "lastAnimId":
                return intAsString(lastPlayerAnimationId);
            case "lastAnimTick":
                return intAsString(lastPlayerAnimationTick);
            case "invSlotsUsed":
                return intAsString(28 - InventoryUtils.getFreeSlots());
            case "invSlotsFree":
                return intAsString(InventoryUtils.getFreeSlots());
            case "tSinceLastAnim":
                return intAsString(ticksSinceLastPlayerAnimation());
            case "npcTargetingYou":
                NPC targetingYou = NpcUtils.getNearestNpc(n -> n.getInteracting() == client.getLocalPlayer());
                if (targetingYou != null && targetingYou.getName() != null)
                {
                    return targetingYou.getName();
                }
                else
                {
                    return "null";
                }
            case "npcsTargetingYou":
                List<NPC> allTargetingYou = NpcUtils.getAll(n -> n.getInteracting() == client.getLocalPlayer());
                if (!allTargetingYou.isEmpty())
                {
                    StringBuilder arrayBuilder = new StringBuilder();
                    for (int i = 0; i < allTargetingYou.size(); i++)
                    {
                        arrayBuilder.append(allTargetingYou.get(i).getName());
                        if (i < allTargetingYou.size() - 1)
                        {
                            arrayBuilder.append(", ");
                        }
                    }
                    return arrayBuilder.toString();
                }
                else
                {
                    return "null";
                }
            case "npcBeingTargeted":
                Actor interacting = client.getLocalPlayer().getInteracting();
                if (interacting != null)
                {
                    if (interacting instanceof NPC)
                    {
                        return interacting.getName();
                    }
                    else
                    {
                        return "null";
                    }
                }
                else
                {
                    return "null";
                }
            case "playerTargetingYou":
                Player pTargetingYou = PlayerUtils.getNearest(pl -> pl.getInteracting() == client.getLocalPlayer());
                if (pTargetingYou != null && pTargetingYou.getName() != null)
                {
                    return pTargetingYou.getName();
                }
                else
                {
                    return "null";
                }
            case "playersTargetingYou":
                List<Player> allPTargetingYou = PlayerUtils.getAll(pl -> pl.getInteracting() == client.getLocalPlayer());
                if (!allPTargetingYou.isEmpty())
                {
                    StringBuilder arrayBuilder = new StringBuilder();
                    for (int i = 0; i < allPTargetingYou.size(); i++)
                    {
                        arrayBuilder.append(allPTargetingYou.get(i).getName());
                        if (i < allPTargetingYou.size() - 1)
                        {
                            arrayBuilder.append(", ");
                        }
                    }
                    return arrayBuilder.toString();
                }
                else
                {
                    return "null";
                }
            case "playerBeingTargeted":
                Actor pInteracting = client.getLocalPlayer().getInteracting();
                if (pInteracting != null)
                {
                    if (pInteracting instanceof Player)
                    {
                        return pInteracting.getName();
                    }
                    else
                    {
                        return "null";
                    }
                }
                else
                {
                    return "null";
                }
            case "targetHpPercent":
                Actor target = client.getLocalPlayer().getInteracting();
                if (target == null)
                {
                    return "-1";
                }

                int ratio = target.getHealthRatio();
                int scale = target.getHealthScale();

                int targetHpPercent = (int) ((double) ratio  / (double) scale * 100);
                return intAsString(targetHpPercent);
            case "targetAnimId":
                Actor targetA = client.getLocalPlayer().getInteracting();
                if (targetA == null)
                {
                    return "-1";
                }
                return intAsString(targetA.getAnimation());
            case "projsTargetYou":
                List<Projectile> projectilesT = validProjectiles.stream().filter(projectile -> projectile.getInteracting() == client.getLocalPlayer()).collect(Collectors.toList());
                if (!projectilesT.isEmpty())
                {
                    StringBuilder arrayBuilder = new StringBuilder();
                    for (int i = 0; i < projectilesT.size(); i++)
                    {
                        arrayBuilder.append(projectilesT.get(i).getId());
                        if (i < projectilesT.size() - 1)
                        {
                            arrayBuilder.append(", ");
                        }
                    }
                    return arrayBuilder.toString();
                }
                else
                {
                    return "null";
                }
            case "projsTargetYourTile":
                List<Projectile> projectilesTile = validProjectiles.stream().filter(projectile -> projectile.getTarget() == client.getLocalPlayer().getLocalLocation()).collect(Collectors.toList());
                if (!projectilesTile.isEmpty())
                {
                    StringBuilder arrayBuilder = new StringBuilder();
                    for (int i = 0; i < projectilesTile.size(); i++)
                    {
                        arrayBuilder.append(projectilesTile.get(i).getId());
                        if (i < projectilesTile.size() - 1)
                        {
                            arrayBuilder.append(", ");
                        }
                    }
                    return arrayBuilder.toString();
                }
                else
                {
                    return "null";
                }
            case "lastNpcYouTargeted":
                return lastNpcNameYouTargeted.isBlank() ? "null" : lastNpcNameYouTargeted;
            case "lastPlayerYouTargeted":
                return lastPlayerNameYouTargeted.isBlank() ? "null" : lastPlayerNameYouTargeted;
            case "lastNpcTargetedYou":
                return lastNpcNameTargetedYou.isBlank() ? "null" : lastNpcNameTargetedYou;
            case "lastPlayerTargetedYou":
                return lastPlayerNameTargetedYou.isBlank() ? "null" : lastPlayerNameTargetedYou;
            case "numNpcsTargetYou":
                List<NPC> numTargetingYou = NpcUtils.getAll(n -> n.getInteracting() == client.getLocalPlayer());
                if (!numTargetingYou.isEmpty())
                {
                    return intAsString(numTargetingYou.size());
                }
                return "0";
            case "numPlayersTargetYou":
                List<Player> numPTargetingYou = PlayerUtils.getAll(pl -> pl.getInteracting() == client.getLocalPlayer());
                if (!numPTargetingYou.isEmpty())
                {
                    return intAsString(numPTargetingYou.size());
                }
                return "0";
            case "numProjsTargetYou":
                List<Projectile> numProjectilesT = validProjectiles.stream().filter(projectile -> projectile.getInteracting() == client.getLocalPlayer()).collect(Collectors.toList());
                if (!numProjectilesT.isEmpty())
                {
                    return intAsString(numProjectilesT.size());
                }
                return "0";
            case "numProjsTargetYourTile":
                List<Projectile> projectilesTTile = validProjectiles.stream().filter(projectile -> projectile.getTarget() == client.getLocalPlayer().getLocalLocation()).collect(Collectors.toList());
                if (!projectilesTTile.isEmpty())
                {
                    return intAsString(projectilesTTile.size());
                }
                return "0";
            case "equipNames":
                StringBuilder equipArrayBuilder = new StringBuilder();
                for (int i = 0; i < 14; i++)
                {
                    Item inSlot = EquipmentUtils.getItemInSlot(i);
                    if (inSlot == null)
                    {
                        equipArrayBuilder.append("null");
                    }
                    else
                    {
                        equipArrayBuilder.append(client.getItemDefinition(inSlot.getId()).getName());
                    }

                    if (i < 13)
                    {
                        equipArrayBuilder.append(", ");
                    }
                }
                return equipArrayBuilder.toString();
            case "invNames":
                StringBuilder invArrayBuilder = new StringBuilder();
                for (int i = 0; i < 28; i++)
                {
                    SlottedItem inSlot = InventoryUtils.getItemInSlot(i);
                    if (inSlot == null)
                    {
                        invArrayBuilder.append("null");
                    }
                    else
                    {
                        invArrayBuilder.append(client.getItemDefinition(inSlot.getItem().getId()).getName());
                    }

                    if (i < 27)
                    {
                        invArrayBuilder.append(", ");
                    }
                }
                return invArrayBuilder.toString();
            case "equipIds":
                StringBuilder equipIdArrayBuilder = new StringBuilder();
                for (int i = 0; i < 14; i++)
                {
                    Item inSlot = EquipmentUtils.getItemInSlot(i);
                    if (inSlot == null)
                    {
                        equipIdArrayBuilder.append("null");
                    }
                    else
                    {
                        equipIdArrayBuilder.append(inSlot.getId());
                    }

                    if (i < 13)
                    {
                        equipIdArrayBuilder.append(", ");
                    }
                }
                return equipIdArrayBuilder.toString();
            case "invIds":
                StringBuilder invIdArrayBuilder = new StringBuilder();
                for (int i = 0; i < 28; i++)
                {
                    SlottedItem inSlot = InventoryUtils.getItemInSlot(i);
                    if (inSlot == null)
                    {
                        invIdArrayBuilder.append("null");
                    }
                    else
                    {
                        invIdArrayBuilder.append(inSlot.getItem().getId());
                    }

                    if (i < 27)
                    {
                        invIdArrayBuilder.append(", ");
                    }
                }
                return invIdArrayBuilder.toString();
            case "hintArrowWLoc":
                WorldPoint hawp = client.getHintArrowPoint();
                if (hawp == null)
                {
                    return "null";
                }
                return hawp.getX() + "|" + hawp.getY() + "|" + hawp.getPlane();
            case "hintArrowRLoc":
                WorldPoint harp = client.getHintArrowPoint();
                if (harp == null)
                {
                    return "null";
                }
                return harp.getRegionX() + "|" + harp.getRegionY() + "|" + harp.getPlane();
            case "hintArrowNpcWLoc":
                NPC hawn = client.getHintArrowNpc();
                if (hawn == null)
                {
                    return "null";
                }
                return hawn.getWorldLocation().getX() + "|" + hawn.getWorldLocation().getY() + "|" + hawn.getWorldLocation().getPlane();
            case "hintArrowNpcRLoc":
                NPC harn = client.getHintArrowNpc();
                if (harn == null)
                {
                    return "null";
                }
                return harn.getWorldLocation().getRegionX() + "|" + harn.getWorldLocation().getRegionY() + "|" + harn.getWorldLocation().getPlane();
            case "filteredTile":
                WorldPoint closest = closestTile();
                if (closest == null)
                {
                    return "null";
                }
                return closest.getX() + "|" + closest.getY() + "|" + closest.getPlane();
            case "distToFilteredTile":
                WorldPoint closest2 = closestTile();
                if (closest2 == null)
                {
                    return "9999";
                }
                return intAsString(InteractionUtils.approxDistanceTo(closest2, client.getLocalPlayer().getWorldLocation()));
            case "isAutoCombatEnabled":
                return booleanAsString(isAutoCombatEnabled());
            case "isCannonClaimed":
                return booleanAsString(isCannonClaimed());
            default:
                return getDynamicGlobalVariableValue(varName);
        }
    }

    private final List<String> dynamicGlobalVars = List.of(
            "bIndex", "iIndex", "sIndex", "eIndex", "biIndex", "bCount", "iCount", "sCount", "biCount", "prayer", "boostLvl", "realLvl",
            "itemEquipped", "distToPlayer", "distToWLoc", "distToNpc", "distToObj", "distToTItem", "tSinceAnim",
            "projsTargetRTile", "widgetHidden", "widgetSubHidden", "widgetText", "widgetSubText", "varbit", "varp",
            "npcWLoc", "objWLoc", "playerWLoc", "titemWLoc", "npcRLoc", "objRLoc", "playerRLoc", "titemRLoc",
            "widgetSpriteId", "widgetSubSpriteId", "random", "objAnimId", "npcAnimId", "invSlotContains", "widgetValue",
            "widgetSubValue", "isWLocReachable", "isWLocInteractable", "isObjInteractable"
    );

    private String getDynamicGlobalVariableValue(String varName)
    {
        String subVar = varName.substring(varName.indexOf(".") + 1).strip();
        Object val = getValueFromString(subVar);
        String valAsString = String.valueOf(val);
        int parentId = varName.startsWith("bi") ? 15 : varName.startsWith("b") ? 12 : varName.startsWith("i") ? 149 : 300;
        int childId =  varName.startsWith("bi") ? 3 : varName.startsWith("b") ? 13 : varName.startsWith("i") ?   0 : 2;

        if (varName.substring(1).startsWith("Index"))
        {
            if (varName.startsWith("e"))
            {
                return intAsString(getEquipIndex(val));
            }
            else
            {
                return intAsString(getItemIndex(val, parentId, childId));
            }
        }
        else if (varName.substring(2).startsWith("Index"))
        {
            if (varName.startsWith("bi"))
            {
                return intAsString(getItemIndex(val, parentId, childId));
            }
        }

        if (varName.substring(1).startsWith("Count"))
        {
            return intAsString(getItemCount(val, parentId, childId));
        }
        else if (varName.substring(2).startsWith("Count"))
        {
            if (varName.startsWith("bi"))
            {
                return intAsString(getItemCount(val, parentId, childId));
            }
        }

        if (varName.startsWith("prayer"))
        {
            Prayer prayer = CombatUtils.prayerForName(valAsString);
            if (prayer != null)
            {
                return booleanAsString(client.isPrayerActive(prayer));
            }
        }

        if (varName.startsWith("boostLvl"))
        {
            Skill skill = CombatUtils.skillForName(valAsString);

            if (skill != null)
            {
                return intAsString(client.getBoostedSkillLevel(skill));
            }
        }

        if (varName.startsWith("realLvl"))
        {
            Skill skill = CombatUtils.skillForName(valAsString);
            if (skill != null)
            {
                return intAsString(client.getRealSkillLevel(skill));
            }
        }

        if (varName.startsWith("itemEquipped"))
        {
            if (val instanceof Integer)
            {
                return booleanAsString(!EquipmentUtils.getAll(slottedItem -> slottedItem.getItem().getId() == (int) val).isEmpty());
            }
            else
            {
                return booleanAsString(!EquipmentUtils.getAll(item -> {
                    String name = client.getItemDefinition(item.getItem().getId()).getName();
                    return name != null && name.toLowerCase().contains(valAsString.toLowerCase());
                }).isEmpty());
            }
        }

        if (varName.startsWith("distToPlayer"))
        {
            Player player = nearestPlayer(valAsString);
            if (player != null)
            {
                return intAsString(InteractionUtils.approxDistanceTo(player.getWorldLocation(),
                        client.getLocalPlayer().getWorldLocation()));
            }
            else
            {
                return "9999";
            }
        }

        if (varName.startsWith("distToWLoc"))
        {
            String[] params = valAsString.split("\\|");
            WorldPoint worldPoint = new WorldPoint((int) getValueFromString(params[0]), (int) getValueFromString(params[1]), params.length > 2 ? (int) getValueFromString(params[2]) : client.getLocalPlayer().getWorldLocation().getPlane());

            return intAsString(InteractionUtils.approxDistanceTo(worldPoint, client.getLocalPlayer().getWorldLocation()));
        }

        if (varName.startsWith("distToNpc"))
        {
            NPC nearest = nearestNpc(valAsString);
            if (nearest != null)
            {
                return intAsString(InteractionUtils.approxDistanceTo(nearest.getWorldLocation(), client.getLocalPlayer().getWorldLocation()));
            }
            else
            {
                return "9999";
            }
        }

        if (varName.startsWith("distToObj"))
        {
            TileObject object = nearestObject(valAsString);

            if (object != null)
            {
                return intAsString(InteractionUtils.approxDistanceTo(object.getWorldLocation(), client.getLocalPlayer().getWorldLocation()));
            }
            else
            {
                return "9999";
            }
        }

        if (varName.startsWith("distToTItem"))
        {
            ETileItem nearest = nearestTileItem(valAsString);
            if (nearest != null)
            {
                return intAsString(InteractionUtils.approxDistanceTo(nearest.getLocation(), client.getLocalPlayer().getWorldLocation()));
            }
            else
            {
                return "9999";
            }
        }

        if (varName.startsWith("tSinceAnim"))
        {
            return intAsString(ticksSinceAnimation((int) val));
        }

        if (varName.contains("projsTargetRTile"))
        {
            String[] params = valAsString.split("\\|");
            List<Projectile> projectilesT = validProjectiles.stream().filter(projectile -> {
               WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, projectile.getTarget());
               return projectile.getTarget() != null && worldPoint.getRegionX() == Integer.parseInt(params[0]) &&
                       worldPoint.getRegionY() == Integer.parseInt(params[1]);
            }).collect(Collectors.toList());

            if (!projectilesT.isEmpty())
            {
                StringBuilder arrayBuilder = new StringBuilder();
                for (int i = 0; i < projectilesT.size(); i++)
                {
                    arrayBuilder.append(projectilesT.get(i).getId());
                    if (i < projectilesT.size() - 1)
                    {
                        arrayBuilder.append(", ");
                    }
                }
                return arrayBuilder.toString();
            }
            else
            {
                return "null";
            }
        }

        if (varName.startsWith("widgetHidden"))
        {
            String[] params = valAsString.split("\\.");
            return booleanAsString(InteractionUtils.isWidgetHidden((int) getValueFromString(params[0]), (int) getValueFromString(params[1])));
        }

        if (varName.startsWith("widgetSubHidden"))
        {
            String[] params = valAsString.split("\\.");
            return booleanAsString(InteractionUtils.isWidgetHidden((int) getValueFromString(params[0]), (int) getValueFromString(params[1]), (int) getValueFromString(params[2])));
        }

        if (varName.startsWith("widgetText"))
        {
            String[] params = valAsString.split("\\.");
            return InteractionUtils.getWidgetText((int) getValueFromString(params[0]), (int) getValueFromString(params[1]));
        }

        if (varName.startsWith("widgetSubText"))
        {
            String[] params = valAsString.split("\\.");
            return InteractionUtils.getWidgetText((int) getValueFromString(params[0]), (int) getValueFromString(params[1]), (int) getValueFromString(params[2]));
        }

        if (varName.startsWith("varbit"))
        {
            return intAsString(client.getVarbitValue((int) val));
        }

        if (varName.startsWith("varp"))
        {
            return intAsString(client.getVarpValue((int) val));
        }

        boolean isWLoc = varName.contains("WLoc") && !varName.contains("Reachable");
        boolean isRLoc = varName.contains("RLoc") && !varName.contains("Reachable");
        boolean loc = isWLoc || isRLoc;

        if (loc)
        {
            WorldPoint nearestLoc = null;
            if (varName.startsWith("npc"))
            {
                NPC npcToLoc = nearestNpc(valAsString);

                if (npcToLoc != null)
                {
                    nearestLoc = npcToLoc.getWorldLocation();
                }
            }
            else if (varName.startsWith("obj"))
            {
                TileObject objToLoc = nearestObject(valAsString);

                if (objToLoc != null)
                {
                    nearestLoc = objToLoc.getWorldLocation();
                }
            }
            else if (varName.startsWith("player"))
            {
                Player ploc = nearestPlayer(valAsString);
                if (ploc != null)
                {
                    nearestLoc = ploc.getWorldLocation();
                }
            }
            else if (varName.startsWith("titem"))
            {
                ETileItem nearest = nearestTileItem(valAsString);

                if (nearest != null)
                {
                    nearestLoc = nearest.getLocation();
                }
            }

            if (nearestLoc == null)
            {
                return "null";
            }

            if (isRLoc)
            {
                return (nearestLoc.getRegionX() + "|" + nearestLoc.getRegionY() + "|" + nearestLoc.getPlane());
            }

            return (nearestLoc.getX() + "|" + nearestLoc.getY() + "|" + nearestLoc.getPlane());
        }

        if (varName.startsWith("widgetSpriteId"))
        {
            String[] params = valAsString.split("\\.");
            return intAsString(InteractionUtils.getWidgetSpriteId((int) getValueFromString(params[0]), (int) getValueFromString(params[1])));
        }

        if (varName.startsWith("widgetSubSpriteId"))
        {
            String[] params = valAsString.split("\\.");
            return intAsString(InteractionUtils.getWidgetSpriteId((int) getValueFromString(params[0]), (int) getValueFromString(params[1]), (int) getValueFromString(params[2])));
        }

        if (varName.startsWith("random"))
        {
            String[] params = valAsString.split("\\.");
            return intAsString(randomIntInclusive((int) getValueFromString(params[0]), (int) getValueFromString(params[1])));
        }

        if (varName.startsWith("objAnimId"))
        {
            TileObject dynamicObject = nearestDynamicObject(valAsString);

            if (dynamicObject != null)
            {
                DynamicObject dobj = (DynamicObject) ((GameObject)dynamicObject).getRenderable();
                return intAsString(dobj.getAnimation().getId());
            }
            else
            {
                return "-1";
            }
        }

        if (varName.startsWith("npcAnimId"))
        {
            NPC toGetAnim = nearestNpc(valAsString);
            if (toGetAnim != null)
            {
                return intAsString(toGetAnim.getAnimation());
            }
            else
            {
                return "-1";
            }
        }

        if (varName.startsWith("invSlotContains"))
        {
            String[] params = valAsString.split("\\.");
            int slot = (int) getValueFromString(params[0].strip());
            String[] its = String.valueOf(getValueFromString(params[1].strip())).split(",");

            for(String it : its)
            {
                SlottedItem item = InventoryUtils.getItemInSlot(slot);
                if (item == null)
                {
                    continue;
                }

                if (isInteger(it.strip()))
                {
                    int id = Integer.parseInt(it.strip());
                    if (item.getItem().getId() == id)
                    {
                        return booleanAsString(true);
                    }
                }
                else
                {
                    ItemComposition comp = client.getItemDefinition(item.getItem().getId());
                    if (comp.getName().contains(it.strip()))
                    {
                        return booleanAsString(true);
                    }
                }
            }

            return booleanAsString(false);

        }

        if (varName.startsWith("widgetValue"))
        {
            String[] params = valAsString.split("\\.");
            String text = InteractionUtils.getWidgetText((int) getValueFromString(params[0]), (int) getValueFromString(params[1]));
            text = text.replaceAll("[^0-9]", "");
            if (isInteger(text))
            {
                return text;
            }

            return "-1";
        }

        if (varName.startsWith("widgetSubValue"))
        {
            String[] params = valAsString.split("\\.");
            String text =  InteractionUtils.getWidgetText((int) getValueFromString(params[0]), (int) getValueFromString(params[1]), (int) getValueFromString(params[2]));
            text = text.replaceAll("[^0-9]", "");
            if (isInteger(text))
            {
                return text;
            }

            return "-1";
        }

        if (varName.startsWith("isWLocReachable"))
        {
            String[] params = valAsString.split("\\|");
            WorldPoint worldPoint = new WorldPoint((int) getValueFromString(params[0]), (int) getValueFromString(params[1]), params.length > 2 ? (int) getValueFromString(params[2]) : client.getLocalPlayer().getWorldLocation().getPlane());

            return booleanAsString(InteractionUtils.isWalkable(worldPoint));
        }

        if (varName.startsWith("isWLocInteractable"))
        {
            String[] params = valAsString.split("\\|");
            WorldPoint worldPoint = new WorldPoint((int) getValueFromString(params[0]), (int) getValueFromString(params[1]), params.length > 2 ? (int) getValueFromString(params[2]) : client.getLocalPlayer().getWorldLocation().getPlane());

            return booleanAsString(Reachable.isInteractable(worldPoint.toWorldArea()));
        }

        if (varName.startsWith("isObjInteractable"))
        {
            TileObject object = nearestObject(valAsString);

            if (object != null)
            {
                return booleanAsString(Reachable.isInteractable(new WorldArea(object.getWorldLocation(), ((GameObject)object).sizeX(), ((GameObject)object).sizeY())));
            }
            else
            {
                return booleanAsString(false);
            }
        }

        return "null";
    }

    public int getEquipIndex(Object idOrName)
    {
        for (int i = 0; i < 14; i++)
        {
            Item item = EquipmentUtils.getItemInSlot(i);
            if (item == null)
            {
                continue;
            }

            if (idOrName instanceof Integer)
            {
                if (item.getId() == (int) idOrName)
                {
                    return i;
                }
            }
            else
            {
                String[] split = String.valueOf(idOrName).split(",");

                for (String anIdOrName : split)
                {
                    if (isInteger(anIdOrName.strip()))
                    {
                        if (item.getId() == Integer.parseInt(anIdOrName.strip()))
                        {
                            return i;
                        }
                    }
                    else
                    {
                        ItemComposition comp = client.getItemDefinition(item.getId());
                        if (comp.getName().contains(anIdOrName.strip()))
                        {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    public int getItemIndex(Object idOrName, int parentId, int childId)
    {
        Widget inventoryWidget = client.getWidget(parentId, childId);
        if (inventoryWidget == null)
        {
            return -1;
        }

        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems != null)
        {
            for (int i = 0; i < inventoryItems.length; i++)
            {
                if (inventoryItems[i] != null)
                {
                    if (inventoryItems[i].getName().isBlank())
                    {
                        continue;
                    }
                    // apply filter conditions
                    ItemComposition comp = client.getItemDefinition(inventoryItems[i].getItemId());

                    String[] split = String.valueOf(idOrName).split(",");
                    for (String anIdOrName : split)
                    {
                        boolean any = String.valueOf(anIdOrName.strip()).equals("Any");
                        boolean nameMatches = isInteger(anIdOrName.strip()) || any || (iPartialMatching ? comp.getName() != null && comp.getName().contains(String.valueOf(anIdOrName.strip())) : comp.getName() != null && comp.getName().equals(String.valueOf(anIdOrName.strip())));
                        boolean idMatches = !isInteger(anIdOrName.strip()) || inventoryItems[i].getItemId() == Integer.parseInt(anIdOrName.strip());
                        boolean matchesFilterConditions = nameMatches &&
                                idMatches &&
                                (!iIsNoted || comp.getNote() != -1) &&
                                (!iIsNotNoted || comp.getNote() == -1) &&
                                (!iIsStackable || comp.isStackable()) &&
                                (!iIsNotStackable || !comp.isStackable()) &&
                                (iHasAction.equals("Any") || InventoryUtils.itemHasAction(inventoryItems[i].getItemId(), iHasAction));
                        if (matchesFilterConditions)
                        {
                            return i;
                        }
                    }
                }
            }
        }

        return -1;
    }

    public int getItemCount(Object idOrName, int parentId, int childId)
    {
        int count = 0;
        Widget inventoryWidget = client.getWidget(parentId, childId);
        if (inventoryWidget == null)
        {
            return 0;
        }

        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems != null)
        {
            for (Widget inventoryItem : inventoryItems)
            {

                if (inventoryItem != null)
                {
                    if (inventoryItem.getName().isBlank())
                    {
                        continue;
                    }

                    // apply filter conditions
                    ItemComposition comp = client.getItemDefinition(inventoryItem.getItemId());

                    String[] split = String.valueOf(idOrName).split(",");
                    for (String anIdOrName : split)
                    {
                        boolean any = String.valueOf(anIdOrName.strip()).equals("Any");
                        boolean nameMatches = isInteger(anIdOrName.strip()) || any || (iPartialMatching ? comp.getName() != null && comp.getName().contains(String.valueOf(anIdOrName.strip())) : comp.getName() != null && comp.getName().equals(String.valueOf(anIdOrName.strip())));
                        boolean idMatches = !isInteger(anIdOrName.strip()) || inventoryItem.getItemId() == Integer.parseInt(anIdOrName.strip());
                        boolean matchesFilterConditions = nameMatches &&
                                idMatches &&
                                (!iIsNoted || comp.getNote() != -1) &&
                                (!iIsNotNoted || comp.getNote() == -1) &&
                                (!iIsStackable || comp.isStackable()) &&
                                (!iIsNotStackable || !comp.isStackable()) &&
                                (iHasAction.equals("Any") || InventoryUtils.itemHasAction(inventoryItem.getItemId(), iHasAction));
                        if (matchesFilterConditions)
                        {
                            count += inventoryItem.getItemQuantity();
                        }
                    }
                }
            }
        }

        return count;
    }

    private String intAsString(int value)
    {
        return String.valueOf(value);
    }

    private String booleanAsString(boolean condition)
    {
        return condition ? "1" : "0";
    }

    private boolean isUserVariable(String varName)
    {
        if (!varName.startsWith("%"))
        {
            return false;
        }

        // Trim off the %
        varName = varName.substring(1);

        // Remove coordinate references
        if (varName.endsWith(".x") || varName.endsWith(".y") || varName.endsWith(".z"))
        {
            varName = varName.substring(0, varName.length() - 2);
        }

        // Remove array index ending
        if (varName.contains("[") && varName.contains("]"))
        {
            varName = varName.substring(0, varName.indexOf("["));
        }

        return userVariables.getOrDefault(varName, null) != null;
    }

    private String getVariableValue(String varName)
    {
        boolean isUserVar = varName.startsWith("%");
        String varValue;

        // Trim off the % or $
        varName = varName.substring(1);

        // Remove ending coordinate references
        String coordRef = null;
        if (varName.endsWith(".x") || varName.endsWith(".y") || varName.endsWith(".z"))
        {
            coordRef = varName.substring(varName.length() - 1);
            varName = varName.substring(0, varName.length() - 2);
        }

        // Find array index if using array
        String index = "null";
        if (varName.contains("[") && varName.contains("]") && !varName.contains("."))
        {
            index = varName.substring(varName.indexOf("[") + 1, varName.lastIndexOf("]"));
            varName = varName.substring(0, varName.indexOf("["));
        }

        // Set the var value to the correct variable type
        varValue = isUserVar ? userVariables.get(varName) : getGlobalVariableValue(varName);

        // Checking if the returned user var value is a global variable and do the index/coords check recursively
        if (varValue.startsWith("$") || varValue.startsWith("%"))
        {
            varName = varValue.substring(1);
            varValue = getVariableValue(varName);
        }

        // Set the value to the coordinate which was referenced, if necessary
        if (coordRef != null)
        {
            String[] coords = varValue.split("\\|");
            switch (coordRef) {
                case "x":
                    varValue = coords.length > 0 ? coords[0].strip() : "-1";
                    break;
                case "y":
                    varValue = coords.length > 1 ? coords[1].strip() : "-1";
                    break;
                case "z":
                    varValue = coords.length > 2 ? coords[2].strip() : "-1";
                    break;
            }
        }

        // Parse the index value
        if (!index.equals("null"))
        {
            int indexInt = -1;
            if (isGlobalVariable(index) || isUserVariable(index))
            {
                String value = getVariableValue(index);
                if (isInteger(value))
                {
                    indexInt = Integer.parseInt(value);
                }
            }
            else if (isInteger(index))
            {
                indexInt = Integer.parseInt(index);
            }

            String[] arrayVars = varValue.split(",");
            if (indexInt < arrayVars.length && indexInt >= 0)
            {
                return arrayVars[indexInt].strip();
            }
            else
            {
                debugMessage("Index Out of bounds at index: " + indexInt + " array size = " + arrayVars.length);
                return "null";
            }
        }

        return varValue;
    }

    private void setUserVariableValue(String varName, String value)
    {
        String coordRef = null;
        if (varName.endsWith(".x") || varName.endsWith(".y") || varName.endsWith(".z"))
        {
            coordRef = varName.substring(varName.length() - 1);
            varName = varName.substring(0, varName.length() - 2);
        }

        String index = "null";
        if (varName.contains("[") && varName.contains("]"))
        {
            index = varName.substring(varName.indexOf("[") + 1, varName.lastIndexOf("]"));
            varName = varName.substring(0, varName.indexOf("["));
        }

        String varValue = userVariables.get(varName);

        // Set the value to the coordinate which was referenced, if necessary
        if (coordRef != null)
        {
            String[] coords = varValue.split("\\|");
            switch (coordRef) {
                case "x":
                    coords[0] = value;
                    break;
                case "y":
                    coords[1] = value;
                    break;
                case "z":
                    coords[2] = value;
                    break;
            }
            value = coords[0] + "|" + coords[1] + "|" + (coords.length > 2 ? coords[2] : "-1");
        }

        // Parse the index value
        if (!index.equals("null"))
        {
            int indexInt = -1;
            if (isGlobalVariable(index) || isUserVariable(index))
            {
                String val = getVariableValue(index);
                if (isInteger(val))
                {
                    indexInt = Integer.parseInt(val);
                }
            }
            else if (isInteger(index))
            {
                indexInt = Integer.parseInt(index);
            }

            String[] arrayVars = varValue.split(",");
            if (indexInt < arrayVars.length && indexInt >= 0)
            {
                arrayVars[indexInt] = value;
            }
            else
            {
                debugMessage("Setting Index Out of bounds at index: " + indexInt + " array size = " + arrayVars.length);
            }

            StringBuilder arrayBuilder = new StringBuilder();
            for (int i = 0; i < arrayVars.length; i++)
            {
                arrayBuilder.append(arrayVars[i].strip());
                if (i < arrayVars.length - 1)
                {
                    arrayBuilder.append(", ");
                }
            }

            value = arrayBuilder.toString();
        }

        userVariables.put(varName, value.strip());
    }

    private String getComparisonOperatorFromExpression(String expression)
    {
        if (expression.contains("=="))
        {
            return "==";
        }
        else if (expression.contains("!="))
        {
            return "!=";
        }
        else if (expression.contains("~="))
        {
            return "~=";
        }
        else if (expression.contains(">="))
        {
            return ">=";
        }
        else if (expression.contains("<="))
        {
            return "<=";
        }
        else if (expression.contains(">"))
        {
            return ">";
        }
        else if (expression.contains("<"))
        {
            return "<";
        }

        return "";
    }

    private boolean containsComparisonOperator(String expression)
    {
        return expression.contains("==") || expression.contains("!=") ||
                expression.contains(">") || expression.contains(">=") ||
                expression.contains("<") || expression.contains("<=") ||
                expression.contains("~=");
    }

    private String cleanseInput(String input)
    {
        if (input.contains(":"))
        {
            StringBuilder commentRemovedSB = new StringBuilder();
            boolean commentStarted = false;
            boolean escapeCharActivatedLastIteration = false;
            for (char c : input.toCharArray())
            {
                boolean commentStateFlipped = false;
                if (c == ':' && !escapeCharActivatedLastIteration)
                {
                    commentStarted = !commentStarted;
                    commentStateFlipped = true;
                }

                if (!commentStarted && c != '`')
                {
                    if (!commentStateFlipped)
                    {
                        commentRemovedSB.append(c);
                    }
                }

                escapeCharActivatedLastIteration = c == '`';
            }

            input = commentRemovedSB.toString();
        }

        return input.replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
    }

    private String getHotkeyExpression(int index)
    {
        switch (index)
        {
            case 1:
                return config.hotkeyExpression1();
            case 2:
                return config.hotkeyExpression2();
            case 3:
                return config.hotkeyExpression3();
            case 4:
                return config.hotkeyExpression4();
            case 5:
                return config.hotkeyExpression5();
            case 6:
                return config.hotkeyExpression6();
            case 7:
                return config.hotkeyExpression7();
            case 8:
                return config.hotkeyExpression8();
            case 9:
                return config.hotkeyExpression9();
            case 10:
                return config.hotkeyExpression10();
            case 11:
                return config.hotkeyExpression11();
            case 12:
                return config.hotkeyExpression12();
            case 13:
                return config.hotkeyExpression13();
            case 14:
                return config.hotkeyExpression14();
            case 15:
                return config.hotkeyExpression15();
            default:
                return "";
        }
    }

    private boolean isUseAsBot(int index)
    {
        switch (index)
        {
            case 1:
                return config.useAsBot1();
            case 2:
                return config.useAsBot2();
            case 3:
                return config.useAsBot3();
            case 4:
                return config.useAsBot4();
            case 5:
                return config.useAsBot5();
            case 6:
                return config.useAsBot6();
            case 7:
                return config.useAsBot7();
            case 8:
                return config.useAsBot8();
            case 9:
                return config.useAsBot9();
            case 10:
                return config.useAsBot10();
            case 11:
                return config.useAsBot11();
            case 12:
                return config.useAsBot12();
            case 13:
                return config.useAsBot13();
            case 14:
                return config.useAsBot14();
            case 15:
                return config.useAsBot15();
            default:
                return false;
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals(GROUP_NAME))
        {
            return;
        }

        if (event.getKey().equals("customVariables") && config.initWhenChanged())
        {
            initUserVariables();
        }
    }

    private void initUserVariables()
    {
        userVariables.clear();
        if (config.customVariables().isBlank())
        {
            return;
        }

        String[] vars = cleanseInput(config.customVariables()).strip().split(";");

        for (String token : vars)
        {
            String[] tokens = token.split("=");
            String varName = tokens[0].strip();
            String varValue = tokens[1].strip();

            userVariables.put(varName, varValue);
        }
    }

    private void loadPreset()
    {
        String presetName = config.presetName();
        String presetNameFormatted = presetName.replaceAll(FILENAME_SPECIAL_CHAR_REGEX, "").replaceAll(" ", "_").toLowerCase();

        if (presetNameFormatted.isEmpty())
        {
            return;
        }

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(PRESET_DIR + "/" + presetNameFormatted + ".json"));
            ExportableConfig2 loadedConfig = gson.fromJson(br, ExportableConfig2.class);
            br.close();
            if (loadedConfig != null)
            {
                debugMessage("Loading preset: " + presetNameFormatted);
            }
            else
            {
                debugMessage("Couldn't find preset: " + presetNameFormatted);
                return;
            }

            configManager.setConfiguration(GROUP_NAME, "customVariables", loadedConfig.getUserVars() != null ? loadedConfig.getUserVars() : "");

            for (int i = 0; i < 15; i++)
            {
                configManager.setConfiguration(GROUP_NAME, "hotkey" + (i + 1), loadedConfig.getHotkey()[i]);
                configManager.setConfiguration(GROUP_NAME, "hotkeyExpression" + (i + 1), loadedConfig.getHotkeyExpressions()[i]);
                if (!config.disableRunAsBot())
                {
                    configManager.setConfiguration(GROUP_NAME, "useAsBot" + (i + 1), loadedConfig.getUseAsBot()[i]);
                }
                else
                {
                    configManager.setConfiguration(GROUP_NAME, "useAsBot" + (i + 1), false);

                }
            }

            InteractionUtils.showNonModalMessageDialog("Successfully loaded preset '" + presetNameFormatted + "'", "Preset Load Success");
            initUserVariables();
        }
        catch (Exception e)
        {
            InteractionUtils.showNonModalMessageDialog(e.getMessage(), "Preset Load Error");
            log.error(e.getMessage());
        }
    }

    private void savePreset()
    {
        String presetName = config.presetName();
        String presetNameFormatted = presetName.replaceAll(FILENAME_SPECIAL_CHAR_REGEX, "").replaceAll(" ", "_").toLowerCase();

        if (presetNameFormatted.isEmpty())
        {
            return;
        }

        ExportableConfig2 exportableConfig2 = new ExportableConfig2(config.customVariables() != null ? config.customVariables() : "");

        exportableConfig2.setHotkeyConfig(0, config.hotkey1(), config.hotkeyExpression1(), config.useAsBot1());
        exportableConfig2.setHotkeyConfig(1, config.hotkey2(), config.hotkeyExpression2(), config.useAsBot2());
        exportableConfig2.setHotkeyConfig(2, config.hotkey3(), config.hotkeyExpression3(), config.useAsBot3());
        exportableConfig2.setHotkeyConfig(3, config.hotkey4(), config.hotkeyExpression4(), config.useAsBot4());
        exportableConfig2.setHotkeyConfig(4, config.hotkey5(), config.hotkeyExpression5(), config.useAsBot5());
        exportableConfig2.setHotkeyConfig(5, config.hotkey6(), config.hotkeyExpression6(),config.useAsBot6());
        exportableConfig2.setHotkeyConfig(6, config.hotkey7(), config.hotkeyExpression7(),config.useAsBot7());
        exportableConfig2.setHotkeyConfig(7, config.hotkey8(), config.hotkeyExpression8(),config.useAsBot8());
        exportableConfig2.setHotkeyConfig(8, config.hotkey9(), config.hotkeyExpression9(),config.useAsBot9());
        exportableConfig2.setHotkeyConfig(9, config.hotkey10(), config.hotkeyExpression10(), config.useAsBot10());
        exportableConfig2.setHotkeyConfig(10, config.hotkey11(), config.hotkeyExpression11(),config.useAsBot11());
        exportableConfig2.setHotkeyConfig(11, config.hotkey12(), config.hotkeyExpression12(),config.useAsBot12());
        exportableConfig2.setHotkeyConfig(12, config.hotkey13(), config.hotkeyExpression13(),config.useAsBot13());
        exportableConfig2.setHotkeyConfig(13, config.hotkey14(), config.hotkeyExpression14(),config.useAsBot14());
        exportableConfig2.setHotkeyConfig(14, config.hotkey15(),  config.hotkeyExpression15(), config.useAsBot15());

        if (!PRESET_DIR.exists())
        {
            PRESET_DIR.mkdirs();
        }

        File saveFile = new File(PRESET_DIR, presetNameFormatted + ".json");
        try (FileWriter fw = new FileWriter(saveFile))
        {
            fw.write(gson.toJson(exportableConfig2));
            fw.close();
            InteractionUtils.showNonModalMessageDialog("Successfully saved preset '" + presetNameFormatted + "' at " + saveFile.getAbsolutePath(), "Preset Save Success");
        }
        catch (Exception e)
        {
            InteractionUtils.showNonModalMessageDialog(e.getMessage(), "Save Preset Error");
            log.error(e.getMessage());
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (client == null || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        if (config.loadPresetHotkey().matches(e))
        {
            clientThread.invoke(this::loadPreset);
        }

        if (config.savePresetHotkey().matches(e))
        {
            clientThread.invoke(this::savePreset);
        }

        if (config.hotkey1().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(1));
        }

        if (config.hotkey2().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(2));
        }

        if (config.hotkey3().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(3));
        }

        if (config.hotkey4().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(4));
        }

        if (config.hotkey5().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(5));
        }

        if (config.hotkey6().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(6));
        }

        if (config.hotkey7().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(7));
        }

        if (config.hotkey8().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(8));
        }

        if (config.hotkey9().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(9));
        }

        if (config.hotkey10().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(10));
        }

        if (config.hotkey11().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(11));
        }

        if (config.hotkey12().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(12));
        }

        if (config.hotkey13().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(13));
        }

        if (config.hotkey14().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(14));
        }

        if (config.hotkey15().matches(e))
        {
            clientThread.invoke(() -> handleHotkey(15));
        }

    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    private boolean isInteger(String str)
    {
        return str.matches("-?\\d+?");
    }

    private void debugMessage(String message)
    {
        if (!config.debugOutput())
        {
            return;
        }

        if (client.getGameState() == GameState.LOGGED_IN)
        {
            MessageUtils.addMessage(message, config.debugColor());
        }
        else
        {
            log.debug(message);
        }
    }

    public int randomIntInclusive(int min, int max)
    {
        return random.nextInt((max - min) + 1) + min;
    }
}