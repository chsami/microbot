package net.runelite.client.plugins.hoseaplugins.lucidhotkeys;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.item.SlottedItem;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import net.runelite.client.plugins.hoseaplugins.lucidhotkeys.overlay.TileMarkersOverlay;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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
import java.util.regex.Pattern;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@Slf4j
@PluginDescriptor(
        name = PluginDescriptor.Lucid + "Hotkeys 1</html>",
        description = "Setup hotkeys that can do a variety of different actions.",
        enabledByDefault = false,
        tags = {"hotkeys", "lucid"}
)
public class LucidHotkeysPlugin extends Plugin implements KeyListener
{

    @Inject
    private Client client;

    @Inject
    private LucidHotkeysConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private TileMarkersOverlay tileMarkersOverlay;

    @Inject
    private OverlayManager overlayManager;

    private Logger log = LoggerFactory.getLogger(getName());

    public static final String GROUP_NAME = "lucid-hotkeys";

    public static final File PRESET_DIR = new File(RUNELITE_DIR, GROUP_NAME);

    public static final String FILENAME_SPECIAL_CHAR_REGEX = "[^a-zA-Z\\d:]";

    public final GsonBuilder builder = new GsonBuilder()
            .setPrettyPrinting();
    public final Gson gson = builder.create();

    private int tickDelay = 0;

    private int lastPlayerAnimationTick = 0;

    private int lastPlayerAnimationId = -1;

    private boolean lastActionSucceeded = false;

    private Map<Integer, ArrayList<Integer>> hotkeysToRun = new HashMap<>();

    private List<String> playerNamesTracked = new ArrayList<>();
    private List<String> npcNamesTracked = new ArrayList<>();

    @Getter
    private List<Player> playersTracked = new ArrayList<>();

    @Getter
    private List<NPC> npcsTracked = new ArrayList<>();

    private Map<String, String> userVariables = new HashMap<>();
    private Map<Integer, Integer> playerAnimationTimes = new HashMap<>();

    private List<Projectile> validProjectiles = new ArrayList<>();

    @Getter
    private Map<WorldPoint, String> worldPointTileMarkers = new HashMap<>();

    @Getter
    private Map<LocalRegionTile, String> regionPointTileMarkers = new HashMap<>();

    private int tickMetronomeMaxValue = 5;
    private int tickMetronomeCount = tickMetronomeMaxValue;

    private String lastNpcNameYouTargeted = "";

    private String lastPlayerNameYouTargeted = "";

    private String lastNpcNameTargetedYou = "";

    private String lastPlayerNameTargetedYou = "";

    @Provides
    LucidHotkeysConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidHotkeysConfig.class);
    }

    @Override
    protected void startUp()
    {
        clientThread.invoke(this::pluginEnabled);
    }

    private void pluginEnabled()
    {
        log.info(getName() + " Started");

        if (client.getGameState() == GameState.LOGGED_IN)
        {
            MessageUtils.addMessage(getName() + " Started", Color.RED);
        }

        keyManager.registerKeyListener(this);

        initUserVariables();

        if (!overlayManager.anyMatch(p -> p == tileMarkersOverlay))
        {
            overlayManager.add(tileMarkersOverlay);
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
            ExportableConfig loadedConfig = gson.fromJson(br, ExportableConfig.class);
            br.close();
            if (loadedConfig != null)
            {
                log.info("Loaded preset: " + presetNameFormatted);
            }

            configManager.setConfiguration(GROUP_NAME, "customVariables", loadedConfig.getUserVars() != null ? loadedConfig.getUserVars() : "");

            for (int i = 0; i < 15; i++)
            {
                configManager.setConfiguration(GROUP_NAME, "hotkey" + (i + 1), loadedConfig.getHotkey()[i]);
                configManager.setConfiguration(GROUP_NAME, "actions" + (i + 1), loadedConfig.getActions()[i]);
                configManager.setConfiguration(GROUP_NAME, "preconditions" + (i + 1), loadedConfig.getPreconditions()[i]);
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

        ExportableConfig exportableConfig = new ExportableConfig(config.customVariables() != null ? config.customVariables() : "");

        exportableConfig.setHotkeyConfig(0, config.hotkey1(), config.actions1(), config.preconditions1());
        exportableConfig.setHotkeyConfig(1, config.hotkey2(), config.actions2(), config.preconditions2());
        exportableConfig.setHotkeyConfig(2, config.hotkey3(), config.actions3(), config.preconditions3());
        exportableConfig.setHotkeyConfig(3, config.hotkey4(), config.actions4(), config.preconditions4());
        exportableConfig.setHotkeyConfig(4, config.hotkey5(), config.actions5(), config.preconditions5());
        exportableConfig.setHotkeyConfig(5, config.hotkey6(), config.actions6(), config.preconditions6());
        exportableConfig.setHotkeyConfig(6, config.hotkey7(), config.actions7(), config.preconditions7());
        exportableConfig.setHotkeyConfig(7, config.hotkey8(), config.actions8(), config.preconditions8());
        exportableConfig.setHotkeyConfig(8, config.hotkey9(), config.actions9(), config.preconditions9());
        exportableConfig.setHotkeyConfig(9, config.hotkey10(), config.actions10(), config.preconditions10());
        exportableConfig.setHotkeyConfig(10, config.hotkey11(), config.actions11(), config.preconditions11());
        exportableConfig.setHotkeyConfig(11, config.hotkey12(), config.actions12(), config.preconditions12());
        exportableConfig.setHotkeyConfig(12, config.hotkey13(), config.actions13(), config.preconditions13());
        exportableConfig.setHotkeyConfig(13, config.hotkey14(), config.actions14(), config.preconditions14());
        exportableConfig.setHotkeyConfig(14, config.hotkey15(), config.actions15(), config.preconditions15());

        if (!PRESET_DIR.exists())
        {
            PRESET_DIR.mkdirs();
        }

        File saveFile = new File(PRESET_DIR, presetNameFormatted + ".json");
        try (FileWriter fw = new FileWriter(saveFile))
        {
            fw.write(gson.toJson(exportableConfig));
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

    /**
     *
     * @param index Index of preset, starting at 1 (instead of 0)
     */
    private void handleHotkey(int index)
    {
        String actions = getActions(index).replaceAll("[ \\n]", "").replaceAll("_", " ");
        String preconditions = getPreconditions(index).replaceAll("[ \\n]", "").replaceAll("_", " ");

        if (actions.isEmpty())
        {
            return;
        }

        // If there's no "/" we assume its a single action, so we add the / to make the split work properly
        if (!actions.contains("/"))
        {
            actions += "/"; // Make it so that it splits properly
        }

        if (!preconditions.contains("/"))
        {
            preconditions += "/";
        }

        // First split the String into a 2d array of actionParams + 2d array of preconditionParams (which is also a 2d array)
        String[] splitActions = actions.split("/");
        String[] splitPreconditions = preconditions.split("/");

        if (splitActions.length != splitPreconditions.length)
        {
            if (!Strings.isNullOrEmpty(preconditions))
            {
                MessageUtils.addMessage("Pre-conditions length (" + (splitPreconditions.length ) + ") must match actions length (" + splitActions.length + ") for preset " + index + "!", Color.RED);
                return;
            }
        }

        // Loop through all the actions
        for (int i = 0; i < splitActions.length; i++)
        {

            //Split the action's params up
            String[] actionParams = splitActions[i].split(",");

            //String of precondition param arrays
            String preconditionsArray = splitPreconditions[i];

            // If only 1 precondition, we add a & to make the split work
            if (!preconditionsArray.contains("&"))
            {
                preconditionsArray += "&";
            }

            Action action = Action.forId(Integer.parseInt(actionParams[0]));

            String[] preconditionParams = preconditionsArray.split("&");
            boolean preconditionsOkay = true;

            int failedIndex = -1;
            for (int j = 0; j < preconditionParams.length; j++)
            {
                String[] params = preconditionParams[j].split(",");

                if (!Strings.isNullOrEmpty(params[0]))
                {
                    Precondition precondition = Precondition.forId(Integer.parseInt(params[0]));

                    if (precondition.getParamsNeeded() != params.length)
                    {
                        MessageUtils.addMessage("Invalid param length for pre-condition index [" + i + "][" + j + "] in preset " + index, Color.RED);
                        return;
                    }

                    if (!satisfiesPreconditions(precondition, params))
                    {
                        preconditionsOkay = false;
                        failedIndex = j;
                        break;
                    }
                }
            }

            if (!preconditionsOkay)
            {
                lastActionSucceeded = false;
                if (config.debugOutput())
                {
                    String failMessage = "Hotkey " + index + " [" + i + "][" + failedIndex + "] pre-condition failed for params<br>" + preconditionsArray;
                    MessageUtils.addMessage(failMessage, Color.RED);
                    System.out.println(failMessage);
                }
                continue;
            }

            lastActionSucceeded = true;

            if (action.getParamsNeeded() != actionParams.length)
            {
                MessageUtils.addMessage("Invalid param length for action index " + i + " in preset " + index + "!", Color.RED);
                return;
            }

            if (config.debugOutput())
            {
                String successMessage = "Hotkey " + index + " [" + i + "] pre-condition succeeded. Executing action:<br>" + splitActions[i];
                MessageUtils.addMessage(successMessage, Color.RED);
                System.out.println("Hotkey " + successMessage);
            }

            processAction(action, actionParams);
        }
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        if (tickDelay > 0)
        {
            tickDelay--;
        }

        ArrayList<Integer> hotkeysToEvaluate = hotkeysToRun.get(client.getTickCount());
        if (hotkeysToEvaluate != null)
        {
            for (int hotkey : hotkeysToEvaluate)
            {
                handleHotkey(hotkey);
            }
        }

        hotkeysToRun.keySet().removeIf(i -> i == client.getTickCount());

        if (tickMetronomeCount > 0)
        {
            tickMetronomeCount--;
        }
        else if (tickMetronomeCount == 0)
        {
            tickMetronomeCount = tickMetronomeMaxValue;
        }

        if (playersTracked != null)
        {
            playersTracked.clear();
        }

        if (npcsTracked != null)
        {
            npcsTracked.clear();
        }

        if (playerNamesTracked.size() > 0)
        {
            for (String s : playerNamesTracked)
            {
                Player p = PlayerUtils.getNearest(s);
                if (p != null)
                {
                    playersTracked.add(p);
                }

            }
        }

        if (npcNamesTracked.size() > 0)
        {
            for (String s : npcNamesTracked)
            {
                List<NPC> tracked = NpcUtils.getAll(npc -> npc.getName().toLowerCase().contains(s.toLowerCase()));
                if (tracked != null && !tracked.isEmpty())
                {
                    npcsTracked.addAll(tracked);
                }
            }
        }

        if (config.useAsBot())
        {
            for (int i = 1; i < 16; i++)
            {
                handleHotkey(i);
            }
        }

        validProjectiles.removeIf(projectile -> projectile.getRemainingCycles() <= 0);
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
    private void onProjectileMoved(final ProjectileMoved event)
    {
        if (!validProjectiles.contains(event.getProjectile()))
        {
            validProjectiles.add(event.getProjectile());
        }
    }

    private int ticksSinceLastPlayerAnimation()
    {
        return client.getTickCount() - lastPlayerAnimationTick;
    }

    private int ticksSinceAnimation(int id)
    {
        int animTick = playerAnimationTimes.getOrDefault(id, 0);
        return client.getTickCount() - animTick;
    }

    /**
     *
     * @param index Index of preset, starting at 1 (instead of 0)
     */
    private String getActions(int index)
    {
        switch (index)
        {
            case 1:
                return config.actions1();
            case 2:
                return config.actions2();
            case 3:
                return config.actions3();
            case 4:
                return config.actions4();
            case 5:
                return config.actions5();
            case 6:
                return config.actions6();
            case 7:
                return config.actions7();
            case 8:
                return config.actions8();
            case 9:
                return config.actions9();
            case 10:
                return config.actions10();
            case 11:
                return config.actions11();
            case 12:
                return config.actions12();
            case 13:
                return config.actions13();
            case 14:
                return config.actions14();
            case 15:
                return config.actions15();
            default:
                return "";
        }
    }

    /**
     *
     * @param index Index of preset, starting at 1 (instead of 0)
     */
    private String getPreconditions(int index)
    {
        switch (index)
        {
            case 1:
                return config.preconditions1();
            case 2:
                return config.preconditions2();
            case 3:
                return config.preconditions3();
            case 4:
                return config.preconditions4();
            case 5:
                return config.preconditions5();
            case 6:
                return config.preconditions6();
            case 7:
                return config.preconditions7();
            case 8:
                return config.preconditions8();
            case 9:
                return config.preconditions9();
            case 10:
                return config.preconditions10();
            case 11:
                return config.preconditions11();
            case 12:
                return config.preconditions12();
            case 13:
                return config.preconditions13();
            case 14:
                return config.preconditions14();
            case 15:
                return config.preconditions15();
            default:
                return "";
        }
    }

    private boolean satisfiesPreconditions(Precondition precondition, String[] preconditionParams)
    {
        int param1Int = -69420;
        int param2Int = -69420;
        int param3Int = -69420;
        int param4Int = -69420;

        if (preconditionParams.length >= 2)
        {
            if (preconditionParams[1].contains("%"))
            {
                String varValue = getVarValue(preconditionParams[1].replaceAll("%", ""));
                if (varValue != null)
                {
                    if (isNumeric(varValue))
                    {
                        param1Int = Integer.parseInt(varValue);
                    }
                    else
                    {
                        preconditionParams[1] = varValue;
                    }
                }
            }
            else if (isNumeric(preconditionParams[1]))
            {
                param1Int = Integer.parseInt(preconditionParams[1]);
            }
        }

        if (preconditionParams.length >= 3)
        {
            if (preconditionParams[2].contains("%"))
            {
                String varValue = getVarValue(preconditionParams[2].replaceAll("%", ""));
                if (varValue != null)
                {
                    if (isNumeric(varValue))
                    {
                        param2Int = Integer.parseInt(varValue);
                    }
                    else
                    {
                        preconditionParams[2] = varValue;
                    }
                }
            }
            else if (isNumeric(preconditionParams[2]))
            {
                param2Int = Integer.parseInt(preconditionParams[2]);
            }
        }

        if (preconditionParams.length >= 4)
        {
            if (preconditionParams[3].contains("%"))
            {
                String varValue = getVarValue(preconditionParams[3].replaceAll("%", ""));
                if (varValue != null)
                {
                    if (isNumeric(varValue))
                    {
                        param3Int = Integer.parseInt(varValue);
                    }
                    else
                    {
                        preconditionParams[3] = varValue;
                    }
                }
            }
            else if (isNumeric(preconditionParams[3]))
            {
                param3Int = Integer.parseInt(preconditionParams[3]);
            }
        }

        if (preconditionParams.length >= 5)
        {
            if (preconditionParams[4].contains("%"))
            {
                String varValue = getVarValue(preconditionParams[4].replaceAll("%", ""));
                if (varValue != null)
                {
                    if (isNumeric(varValue))
                    {
                        param4Int = Integer.parseInt(varValue);
                    }
                    else
                    {
                        preconditionParams[4] = varValue;
                    }
                }
            }
            else if (isNumeric(preconditionParams[4]))
            {
                param4Int = Integer.parseInt(preconditionParams[4]);
            }
        }

        if (precondition.getId() <= 100)
        {
            return preconditionUnder101True(precondition, preconditionParams, param1Int, param2Int, param3Int, param4Int);
        }
        else
        {
            return preconditionOver100True(precondition, preconditionParams, param1Int, param2Int, param3Int, param4Int);
        }
    }

    private boolean preconditionUnder101True(Precondition precondition, String[] preconditionParams, int param1Int, int param2Int, int param3Int, int param4Int)
    {
        final int inventoryItemCount = 28 - InventoryUtils.getFreeSlots();

        final WorldPoint lpWp = client.getLocalPlayer().getWorldLocation();
        final int lpWorldX = lpWp.getX();
        final int lpWorldY = lpWp.getY();

        switch (precondition)
        {
            case NONE:
                return true;
            case INVENTORY_COUNT_EQUALS:
                return inventoryItemCount == param1Int;
            case INVENTORY_COUNT_NOT_EQUALS:
                return inventoryItemCount != param1Int;
            case INVENTORY_COUNT_GREATER_THAN:
                return inventoryItemCount > param1Int;
            case INVENTORY_COUNT_GREATER_THAN_EQUAL_TO:
                return inventoryItemCount >= param1Int;
            case INVENTORY_COUNT_LESS_THAN:
                return inventoryItemCount < param1Int;
            case INVENTORY_COUNT_LESS_THAN_EQUAL_TO:
                return inventoryItemCount <= param1Int;
            case INVENTORY_NAMED_COUNT_EQUALS:
                return InventoryUtils.count(preconditionParams[1]) == param2Int;
            case INVENTORY_NAMED_COUNT_NOT_EQUALS:
                return InventoryUtils.count(preconditionParams[1]) != param2Int;
            case INVENTORY_NAMED_COUNT_GREATER_THAN:
                return InventoryUtils.count(preconditionParams[1]) > param2Int;
            case INVENTORY_NAMED_COUNT_GREATER_THAN_EQUAL_TO:
                return InventoryUtils.count(preconditionParams[1]) >= param2Int;
            case INVENTORY_NAMED_COUNT_LESS_THAN:
                return InventoryUtils.count(preconditionParams[1]) < param2Int;
            case INVENTORY_NAMED_COUNT_LESS_THAN_EQUAL_TO:
                return InventoryUtils.count(preconditionParams[1]) <= param2Int;
            case INVENTORY_ID_COUNT_EQUALS:
                return InventoryUtils.count(param1Int) == param2Int;
            case INVENTORY_ID_COUNT_NOT_EQUALS:
                return InventoryUtils.count(param1Int) != param2Int;
            case INVENTORY_ID_COUNT_GREATER_THAN:
                return InventoryUtils.count(param1Int) > param2Int;
            case INVENTORY_ID_COUNT_GREATER_THAN_EQUAL_TO:
                return InventoryUtils.count(param1Int) >= param2Int;
            case INVENTORY_ID_COUNT_LESS_THAN:
                return InventoryUtils.count(param1Int) < param2Int;
            case INVENTORY_ID_COUNT_LESS_THAN_EQUAL_TO:
                return InventoryUtils.count(param1Int) <= param2Int;
            case INVENTORY_NAMED_CONTAINS:
                return InventoryUtils.count(preconditionParams[1]) > 0;
            case INVENTORY_NAMED_NOT_CONTAINS:
                return InventoryUtils.count(preconditionParams[1]) == 0;
            case INVENTORY_ID_CONTAINS:
                return InventoryUtils.count(param1Int) > 0;
            case INVENTORY_ID_NOT_CONTAINS:
                return InventoryUtils.count(param1Int) == 0;
            case BANK_OPEN:
                return BankUtils.isOpen();
            case BANK_NOT_OPEN:
                return !BankUtils.isOpen();

            case LAST_ACTION_SUCCEEDED:
                return lastActionSucceeded;
            case LAST_ACTION_FAILED:
                return !lastActionSucceeded;

            case LOCAL_PLAYER_WORLD_LOCATION_EQUALS:
                return lpWorldX == param1Int && lpWorldY == param2Int;
            case LOCAL_PLAYER_WORLD_LOCATION_NOT_EQUALS:
                return lpWorldX != param1Int || lpWorldY != param2Int;
            case LOCAL_PLAYER_WORLD_LOCATION_X_EQUALS:
                return lpWorldX == param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_X_NOT_EQUALS:
                return lpWorldX != param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_X_GREATER_THAN:
                return lpWorldX > param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_X_GREATER_THAN_EQUAL_TO:
                return lpWorldX >= param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_X_LESS_THAN:
                return lpWorldX < param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_X_LESS_THAN_EQUAL_TO:
                return lpWorldX <= param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_Y_EQUALS:
                return lpWorldY == param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_Y_NOT_EQUALS:
                return lpWorldY != param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_Y_GREATER_THAN:
                return lpWorldY > param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_Y_GREATER_THAN_EQUAL_TO:
                return lpWorldY >= param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_Y_LESS_THAN:
                return lpWorldY < param1Int;
            case LOCAL_PLAYER_WORLD_LOCATION_Y_LESS_THAN_EQUAL_TO:
                return lpWorldY <= param1Int;

            case TICKS_SINCE_LAST_PLAYER_ANIMATION_EQUALS:
                return ticksSinceLastPlayerAnimation() == param1Int;
            case TICKS_SINCE_LAST_PLAYER_ANIMATION_NOT_EQUALS:
                return ticksSinceLastPlayerAnimation() != param1Int;
            case TICKS_SINCE_LAST_PLAYER_ANIMATION_GREATER_THAN:
                return ticksSinceLastPlayerAnimation() > param1Int;
            case TICKS_SINCE_LAST_PLAYER_ANIMATION_GREATER_THAN_EQUAL_TO:
                return ticksSinceLastPlayerAnimation() >= param1Int;
            case TICKS_SINCE_LAST_PLAYER_ANIMATION_LESS_THAN:
                return ticksSinceLastPlayerAnimation() < param1Int;
            case TICKS_SINCE_LAST_PLAYER_ANIMATION_LESS_THAN_EQUAL_TO:
                return ticksSinceLastPlayerAnimation() <= param1Int;

            case TICKS_SINCE_SPECIFIC_PLAYER_ANIMATION_EQUALS:
                return ticksSinceAnimation(param1Int) == param2Int;
            case TICKS_SINCE_SPECIFIC_PLAYER_ANIMATION_NOT_EQUALS:
                return ticksSinceAnimation(param1Int) != param2Int;
            case TICKS_SINCE_SPECIFIC_PLAYER_ANIMATION_GREATER_THAN:
                return ticksSinceAnimation(param1Int) > param2Int;
            case TICKS_SINCE_SPECIFIC_PLAYER_ANIMATION_GREATER_THAN_EQUAL_TO:
                return ticksSinceAnimation(param1Int) >= param2Int;
            case TICKS_SINCE_SPECIFIC_PLAYER_ANIMATION_LESS_THAN:
                return ticksSinceAnimation(param1Int) < param2Int;
            case TICKS_SINCE_SPECIFIC_PLAYER_ANIMATION_LESS_THAN_EQUAL_TO:
                return ticksSinceAnimation(param1Int) <= param2Int;

            case DISTANCE_TO_NAMED_NPC_MIDDLE_EQUALS:
                NPC dtnme = NpcUtils.getNearestNpc(preconditionParams[1]);
                if (dtnme != null)
                {
                    WorldPoint np = dtnme.getWorldArea().getWidth() > 1 ? InteractionUtils.getCenterTileFromWorldArea(dtnme.getWorldArea()) : dtnme.getWorldLocation();
                    return InteractionUtils.distanceTo2DHypotenuse(np, client.getLocalPlayer().getWorldLocation()) == param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_NPC_MIDDLE_NOT_EQUALS:
                NPC dtnmne = NpcUtils.getNearestNpc(preconditionParams[1]);
                if (dtnmne != null)
                {
                    WorldPoint np = dtnmne.getWorldArea().getWidth() > 1 ? InteractionUtils.getCenterTileFromWorldArea(dtnmne.getWorldArea()) : dtnmne.getWorldLocation();
                    return InteractionUtils.distanceTo2DHypotenuse(np, client.getLocalPlayer().getWorldLocation()) != param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_NPC_MIDDLE_GREATER_THAN:
                NPC dtnnmgt = NpcUtils.getNearestNpc(preconditionParams[1]);
                if (dtnnmgt != null)
                {
                    WorldPoint np = dtnnmgt.getWorldArea().getWidth() > 1 ? InteractionUtils.getCenterTileFromWorldArea(dtnnmgt.getWorldArea()) : dtnnmgt.getWorldLocation();
                    return InteractionUtils.distanceTo2DHypotenuse(np, client.getLocalPlayer().getWorldLocation()) > param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_NPC_MIDDLE_LESS_THAN:
                NPC dtnnmlt = NpcUtils.getNearestNpc(preconditionParams[1]);
                if (dtnnmlt != null)
                {
                    WorldPoint np = dtnnmlt.getWorldArea().getWidth() > 1 ? InteractionUtils.getCenterTileFromWorldArea(dtnnmlt.getWorldArea()) : dtnnmlt.getWorldLocation();
                    return InteractionUtils.distanceTo2DHypotenuse(np, client.getLocalPlayer().getWorldLocation()) < param2Int;
                }
                return false;
            case DISTANCE_TO_ID_NPC_MIDDLE_EQUALS:
                NPC dtine = NpcUtils.getNearestNpc(param1Int);
                if (dtine != null)
                {
                    WorldPoint np = dtine.getWorldArea().getWidth() > 1 ? InteractionUtils.getCenterTileFromWorldArea(dtine.getWorldArea()) : dtine.getWorldLocation();
                    return InteractionUtils.distanceTo2DHypotenuse(np, client.getLocalPlayer().getWorldLocation()) == param2Int;
                }
                return false;
            case DISTANCE_TO_ID_NPC_MIDDLE_NOT_EQUALS:
                NPC dtinne = NpcUtils.getNearestNpc(param1Int);
                if (dtinne != null)
                {
                    WorldPoint np = dtinne.getWorldArea().getWidth() > 1 ? InteractionUtils.getCenterTileFromWorldArea(dtinne.getWorldArea()) : dtinne.getWorldLocation();
                    return InteractionUtils.distanceTo2DHypotenuse(np, client.getLocalPlayer().getWorldLocation()) != param2Int;
                }
                return false;
            case DISTANCE_TO_ID_NPC_MIDDLE_GREATER_THAN:
                NPC dtingt = NpcUtils.getNearestNpc(param1Int);
                if (dtingt != null)
                {
                    WorldPoint np = dtingt.getWorldArea().getWidth() > 1 ? InteractionUtils.getCenterTileFromWorldArea(dtingt.getWorldArea()) : dtingt.getWorldLocation();
                    return InteractionUtils.distanceTo2DHypotenuse(np, client.getLocalPlayer().getWorldLocation()) > param2Int;
                }
                return false;
            case DISTANCE_TO_ID_NPC_MIDDLE_LESS_THAN:
                NPC dtinlt = NpcUtils.getNearestNpc(param1Int);
                if (dtinlt != null)
                {
                    WorldPoint np = dtinlt.getWorldArea().getWidth() > 1 ? InteractionUtils.getCenterTileFromWorldArea(dtinlt.getWorldArea()) : dtinlt.getWorldLocation();
                    return InteractionUtils.distanceTo2DHypotenuse(np, client.getLocalPlayer().getWorldLocation()) < param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_OBJECT_EQUALS:
                TileObject dtnoe = GameObjectUtils.nearest(preconditionParams[1]);
                if (dtnoe != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtnoe.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) == param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_OBJECT_NOT_EQUALS:
                TileObject dtnone = GameObjectUtils.nearest(preconditionParams[1]);
                if (dtnone != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtnone.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) != param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_OBJECT_GREATER_THAN:
                TileObject dtnogt = GameObjectUtils.nearest(preconditionParams[1]);
                if (dtnogt != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtnogt.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) > param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_OBJECT_LESS_THAN:
                TileObject dtnolt = GameObjectUtils.nearest(preconditionParams[1]);
                if (dtnolt != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtnolt.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) < param2Int;
                }
                return false;
            case DISTANCE_TO_ID_OBJECT_EQUALS:
                TileObject dtioe = GameObjectUtils.nearest(param1Int);
                if (dtioe != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtioe.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) == param2Int;
                }
                return false;
            case DISTANCE_TO_ID_OBJECT_NOT_EQUALS:
                TileObject dtione = GameObjectUtils.nearest(param1Int);
                if (dtione != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtione.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) != param2Int;
                }
                return false;
            case DISTANCE_TO_ID_OBJECT_GREATER_THAN:
                TileObject dtiogt = GameObjectUtils.nearest(param1Int);
                if (dtiogt != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtiogt.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) > param2Int;
                }
                return false;
            case DISTANCE_TO_ID_OBJECT_LESS_THAN:
                TileObject dtiolt = GameObjectUtils.nearest(param1Int);
                if (dtiolt != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtiolt.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) < param2Int;
                }
                return false;
            case DISTANCE_TO_WORLD_LOCATION_EQUALS:
                WorldPoint dtwle = new WorldPoint(param1Int, param2Int, client.getLocalPlayer().getWorldLocation().getPlane());
                return InteractionUtils.distanceTo2DHypotenuse(dtwle, client.getLocalPlayer().getWorldLocation()) == param3Int;
            case DISTANCE_TO_WORLD_LOCATION_NOT_EQUALS:
                WorldPoint dtwlne = new WorldPoint(param1Int, param2Int, client.getLocalPlayer().getWorldLocation().getPlane());
                return InteractionUtils.distanceTo2DHypotenuse(dtwlne, client.getLocalPlayer().getWorldLocation()) != param3Int;
            case DISTANCE_TO_WORLD_LOCATION_GREATER_THAN:
                WorldPoint dtwlgt = new WorldPoint(param1Int, param2Int, client.getLocalPlayer().getWorldLocation().getPlane());
                return InteractionUtils.distanceTo2DHypotenuse(dtwlgt, client.getLocalPlayer().getWorldLocation()) > param3Int;
            case DISTANCE_TO_WORLD_LOCATION_LESS_THAN:
                WorldPoint dtwllt = new WorldPoint(param1Int, param2Int, client.getLocalPlayer().getWorldLocation().getPlane());
                return InteractionUtils.distanceTo2DHypotenuse(dtwllt, client.getLocalPlayer().getWorldLocation()) < param3Int;
            case DISTANCE_TO_NAMED_PLAYER_EQUALS:
                Player dtnpe = PlayerUtils.getNearest(preconditionParams[1]);
                if (dtnpe != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtnpe.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) == param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_PLAYER_NOT_EQUALS:
                Player dtnpne = PlayerUtils.getNearest(preconditionParams[1]);
                if (dtnpne != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtnpne.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) != param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_PLAYER_GREATER_THAN:
                Player dtnpgt = PlayerUtils.getNearest(preconditionParams[1]);
                if (dtnpgt != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtnpgt.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) > param2Int;
                }
                return false;
            case DISTANCE_TO_NAMED_PLAYER_LESS_THAN:
                Player dtnplt = PlayerUtils.getNearest(preconditionParams[1]);
                if (dtnplt != null)
                {
                    return InteractionUtils.distanceTo2DHypotenuse(dtnplt.getWorldLocation(),
                            client.getLocalPlayer().getWorldLocation()) < param2Int;
                }
                return false;
            case HAS_NAMED_ITEM_EQUIPPED:
                return !EquipmentUtils.getAll(item -> {
                    String name = client.getItemDefinition(item.getItem().getId()).getName();
                    return name != null && name.toLowerCase().contains(preconditionParams[1].toLowerCase());
                }).isEmpty();
            case DOESNT_HAVE_NAMED_ITEM_EQUIPPED:
                return EquipmentUtils.getAll(item -> {
                    String name = client.getItemDefinition(item.getItem().getId()).getName();
                    return name != null && name.toLowerCase().contains(preconditionParams[1].toLowerCase());
                }).isEmpty();
            case HAS_ID_ITEM_EQUIPPED:
                final int p1Int2 = param1Int;
                return !EquipmentUtils.getAll(slottedItem -> slottedItem.getItem().getId() == p1Int2).isEmpty();
            case DOESNT_HAVE_ID_ITEM_EQUIPPED:
                final int p1Int3 = param1Int;
                return EquipmentUtils.getAll(slottedItem -> slottedItem.getItem().getId() == p1Int3).isEmpty();

            case TICK_METRONOME_VALUE_EQUALS:
                return tickMetronomeCount == param1Int;
            case TICK_METRONOME_VALUE_NOT_EQUALS:
                return tickMetronomeCount != param1Int;
            case TICK_METRONOME_VALUE_GREATER_THAN:
                return tickMetronomeCount > param1Int;
            case TICK_METRONOME_VALUE_LESS_THAN:
                return tickMetronomeCount < param1Int;

            case NAMED_NPC_TARGETING_YOU:
                return NpcUtils.getNearestNpc(npc -> npc.getName() != null && npc.getName().toLowerCase().contains(preconditionParams[1].toLowerCase())
                        && npc.getInteracting() == client.getLocalPlayer()) != null;
            case ID_NPC_TARGETING_YOU:
                final int p1Int4 = param1Int;
                return NpcUtils.getNearestNpc(npc -> npc.getId() == p1Int4
                        && npc.getInteracting() == client.getLocalPlayer()) != null;
            case ANY_NPC_TARGETING_YOU:
                return NpcUtils.getNearestNpc(npc -> npc.getInteracting() == client.getLocalPlayer()) != null;
            case TARGETING_NAMED_NPC:
                return client.getLocalPlayer().getInteracting() != null &&
                        client.getLocalPlayer().getInteracting() instanceof NPC &&
                        ((NPC) client.getLocalPlayer().getInteracting()).getName().toLowerCase().contains(preconditionParams[1].toLowerCase());
            case TARGETING_ANY_NPC:
                return client.getLocalPlayer().getInteracting() != null &&
                        client.getLocalPlayer().getInteracting() instanceof NPC;
            case NAMED_PLAYER_TARGETING_YOU:
                return PlayerUtils.getNearest(player -> player.getName() != null &&
                        player.getName().toLowerCase().contains(preconditionParams[1].toLowerCase())
                        && player.getInteracting() == client.getLocalPlayer()) != null;
            case ANY_PLAYER_TARGETING_YOU:
                return PlayerUtils.getNearest(player -> player.getInteracting() == client.getLocalPlayer()) != null;
            case TARGETING_NAMED_PLAYER:
                return client.getLocalPlayer().getInteracting() != null &&
                        client.getLocalPlayer().getInteracting() instanceof Player &&
                        ((Player) client.getLocalPlayer().getInteracting()).getName().toLowerCase().contains(preconditionParams[1].toLowerCase());
            case TARGETING_ANY_PLAYER:
                return client.getLocalPlayer().getInteracting() != null &&
                        client.getLocalPlayer().getInteracting() instanceof Player;
            case NAMED_NPC_NOT_TARGETING_YOU:
                return NpcUtils.getNearestNpc(npc -> npc.getName() != null && npc.getName().toLowerCase().contains(preconditionParams[1].toLowerCase())
                        && npc.getInteracting() == client.getLocalPlayer()) == null;
            case ID_NPC_NOT_TARGETING_YOU:
                final int p1Int5 = param1Int;
                return NpcUtils.getNearestNpc(npc -> npc.getId() == p1Int5
                        && npc.getInteracting() == client.getLocalPlayer()) == null;
            case NO_NPC_TARGETING_YOU:
                return NpcUtils.getNearestNpc(npc -> npc.getInteracting() == client.getLocalPlayer()) == null;
            case NAMED_PLAYER_NOT_TARGETING_YOU:
                return PlayerUtils.getNearest(player -> player.getName() != null &&
                        player.getName().toLowerCase().contains(preconditionParams[1].toLowerCase())
                        && player.getInteracting() == client.getLocalPlayer()) == null;
            case NO_PLAYER_TARGETING_YOU:
                return PlayerUtils.getNearest(player -> player.getInteracting() == client.getLocalPlayer()) == null;
            case VAR_VALUE_EQUALS:
                if (param1Int != -69420 && param2Int != -69420)
                {
                    return param1Int == param2Int;
                }
                return preconditionParams[1].equals(preconditionParams[2]);
            case VAR_VALUE_NOT_EQUAL:
                if (param1Int != -69420 && param2Int != -69420)
                {
                    return param1Int != param2Int;
                }
                return !preconditionParams[1].equals(preconditionParams[2]);
            }
            return false;
    }

    private boolean preconditionOver100True(Precondition precondition, String[] preconditionParams, int param1Int, int param2Int, int param3Int, int param4Int)
    {
        int hp1 = client.getBoostedSkillLevel(Skill.HITPOINTS);
        int hp2 = client.getRealSkillLevel(Skill.HITPOINTS);
        int playerHpPercent = hp1 * 100 / hp2;

        Actor target = client.getLocalPlayer().getInteracting();
        int ratio = target != null ? target.getHealthRatio() : -1;
        int scale = target != null ? target.getHealthScale() : -1;

        int targetHpPercent = target != null ? (int) Math.floor((double) ratio  / (double) scale * 100) : -1;

        int targetAnimation = target != null ? target.getAnimation() : -2;

        switch (precondition)
        {
            case VAR_VALUE_LESS_THAN:
                return param1Int < param2Int;
            case VAR_VALUE_GREATER_THAN:
                return param1Int > param2Int;
            case LOCAL_PLAYER_SCENE_LOCATION_EQUALS:
                return client.getLocalPlayer().getLocalLocation().getSceneX() == param1Int &&
                        client.getLocalPlayer().getLocalLocation().getSceneY() == param2Int;
            case LOCAL_PLAYER_SCENE_LOCATION_NOT_EQUALS:
                return client.getLocalPlayer().getLocalLocation().getSceneX() != param1Int ||
                        client.getLocalPlayer().getLocalLocation().getSceneY() != param2Int;
            case LOCAL_PLAYER_SCENE_LOCATION_X_EQUALS:
                return client.getLocalPlayer().getLocalLocation().getSceneX() == param1Int;
            case LOCAL_PLAYER_SCENE_LOCATION_X_NOT_EQUALS:
                return client.getLocalPlayer().getLocalLocation().getSceneX() != param1Int;
            case LOCAL_PLAYER_SCENE_LOCATION_X_GREATER_THAN:
                return client.getLocalPlayer().getLocalLocation().getSceneX() > param1Int;
            case LOCAL_PLAYER_SCENE_LOCATION_X_LESS_THAN:
                return client.getLocalPlayer().getLocalLocation().getSceneX() < param1Int;
            case LOCAL_PLAYER_SCENE_LOCATION_Y_EQUALS:
                return client.getLocalPlayer().getLocalLocation().getSceneY() == param1Int;
            case LOCAL_PLAYER_SCENE_LOCATION_Y_NOT_EQUALS:
                return client.getLocalPlayer().getLocalLocation().getSceneY() != param1Int;
            case LOCAL_PLAYER_SCENE_LOCATION_Y_GREATER_THAN:
                return client.getLocalPlayer().getLocalLocation().getSceneY() > param1Int;
            case LOCAL_PLAYER_SCENE_LOCATION_Y_LESS_THAN:
                return client.getLocalPlayer().getLocalLocation().getSceneY() < param1Int;
            case CURRENT_ANIMATION_EQUALS:
                return client.getLocalPlayer().getAnimation() == param1Int;
            case CURRENT_ANIMATION_NOT_EQUALS:
                return client.getLocalPlayer().getAnimation() != param1Int;
            case SPEC_ENERGY_EQUALS:
                return CombatUtils.getSpecEnergy() == param1Int;
            case SPEC_ENERGY_NOT_EQUALS:
                return CombatUtils.getSpecEnergy() != param1Int;
            case SPEC_ENERGY_GREATER_THAN:
                return CombatUtils.getSpecEnergy() > param1Int;
            case SPEC_ENERGY_LESS_THAN:
                return CombatUtils.getSpecEnergy() < param1Int;
            case PLAYER_HP_PERCENT_EQUALS:
                return playerHpPercent == param1Int;
            case PLAYER_HP_PERCENT_NOT_EQUALS:
                return playerHpPercent != param1Int;
            case PLAYER_HP_PERCENT_GREATER_THAN:
                return playerHpPercent > param1Int;
            case PLAYER_HP_PERCENT_LESS_THAN:
                return playerHpPercent < param1Int;
            case TARGET_HP_PERCENT_EQUALS:
                return targetHpPercent == param1Int;
            case TARGET_HP_PERCENT_NOT_EQUALS:
                return targetHpPercent != param1Int;
            case TARGET_HP_PERCENT_GREATER_THAN:
                return targetHpPercent > param1Int;
            case TARGET_HP_PERCENT_LESS_THAN:
                return targetHpPercent < param1Int;
            case TARGET_ANIMATION_EQUALS:
                return targetAnimation == param1Int;
            case TARGET_ANIMATION_NOT_EQUALS:
                return targetAnimation != param1Int;
            case IS_PROJECTILE_ID_TARGETING_PLAYER:
                return isProjectileIdTargetingPlayer(param1Int);
            case IS_PROJECTILE_ID_NOT_TARGETING_PLAYER:
                return !isProjectileIdTargetingPlayer(param1Int);
            case IS_PROJECTILE_ID_TARGETING_LOCAL_TILE:
                return isProjectileIdTargetingTile(param1Int, param2Int, param3Int);
            case IS_PROJECTILE_ID_NOT_TARGETING_LOCAL_TILE:
                return !isProjectileIdTargetingTile(param1Int, param2Int, param3Int);
            case IS_PROJECTILE_ID_TARGETING_PLAYERS_TILE:
                return isProjectileIdTargetingTile(param1Int, client.getLocalPlayer().getLocalLocation().getSceneX(), client.getLocalPlayer().getLocalLocation().getSceneY());
            case IS_PROJECTILE_ID_NOT_TARGETING_PLAYERS_TILE:
                return !isProjectileIdTargetingTile(param1Int, client.getLocalPlayer().getLocalLocation().getSceneX(), client.getLocalPlayer().getLocalLocation().getSceneY());
            case ID_TILE_ITEM_EXISTS_WITHIN_DISTANCE:
                return InteractionUtils.tileItemIdExistsWithinDistance(param1Int, param2Int);
            case NAMED_TILE_ITEM_EXISTS_WITHIN_DISTANCE:
                return InteractionUtils.tileItemNameExistsWithinDistance(preconditionParams[1], param2Int);
            case ID_TILE_ITEM_NOT_EXISTS_WITHIN_DISTANCE:
                return !InteractionUtils.tileItemIdExistsWithinDistance(param1Int, param2Int);
            case NAMED_TILE_ITEM_NOT_EXISTS_WITHIN_DISTANCE:
                return !InteractionUtils.tileItemNameExistsWithinDistance(preconditionParams[1], param2Int);
            case PRAYER_POINTS_EQUALS:
                return client.getBoostedSkillLevel(Skill.PRAYER) == param1Int;
            case PRAYER_POINTS_NOT_EQUALS:
                return client.getBoostedSkillLevel(Skill.PRAYER) != param1Int;
            case PRAYER_POINTS_GREATER_THAN:
                return client.getBoostedSkillLevel(Skill.PRAYER) > param1Int;
            case PRAYER_POINTS_LESS_THAN:
                return client.getBoostedSkillLevel(Skill.PRAYER) < param1Int;
            case LOCAL_PLAYER_REGION_LOCATION_EQUALS:
                return client.getLocalPlayer().getWorldLocation().getRegionX() == param1Int &&
                        client.getLocalPlayer().getWorldLocation().getRegionY() == param2Int;
            case LOCAL_PLAYER_REGION_LOCATION_NOT_EQUALS:
                return client.getLocalPlayer().getWorldLocation().getRegionX() != param1Int ||
                        client.getLocalPlayer().getWorldLocation().getRegionY() != param2Int;
            case LOCAL_PLAYER_REGION_LOCATION_X_EQUALS:
                return client.getLocalPlayer().getWorldLocation().getRegionX() == param1Int;
            case LOCAL_PLAYER_REGION_LOCATION_X_NOT_EQUALS:
                return client.getLocalPlayer().getWorldLocation().getRegionX() != param1Int;
            case LOCAL_PLAYER_REGION_LOCATION_X_GREATER_THAN:
                return client.getLocalPlayer().getWorldLocation().getRegionX() > param1Int;
            case LOCAL_PLAYER_REGION_LOCATION_X_LESS_THAN:
                return client.getLocalPlayer().getWorldLocation().getRegionX() < param1Int;
            case LOCAL_PLAYER_REGION_LOCATION_Y_EQUALS:
                return client.getLocalPlayer().getWorldLocation().getRegionY() == param1Int;
            case LOCAL_PLAYER_REGION_LOCATION_Y_NOT_EQUALS:
                return client.getLocalPlayer().getWorldLocation().getRegionY() != param1Int;
            case LOCAL_PLAYER_REGION_LOCATION_Y_GREATER_THAN:
                return client.getLocalPlayer().getWorldLocation().getRegionY() > param1Int;
            case LOCAL_PLAYER_REGION_LOCATION_Y_LESS_THAN:
                return client.getLocalPlayer().getWorldLocation().getRegionY() < param1Int;
            case LOCAL_PLAYER_REGION_ID_EQUALS:
                return client.getLocalPlayer().getWorldLocation().getRegionID() == param1Int;
            case LOCAL_PLAYER_REGION_ID_NOT_EQUALS:
                return client.getLocalPlayer().getWorldLocation().getRegionID() != param1Int;
            case LOCAL_PLAYER_IS_MOVING:
                return InteractionUtils.isMoving();
            case LOCAL_PLAYER_IS_NOT_MOVING:
                return !InteractionUtils.isMoving();
            case WIDGET_IS_HIDDEN:
                return InteractionUtils.isWidgetHidden(param1Int, param2Int);
            case WIDGET_IS_SHOWING:
                return !InteractionUtils.isWidgetHidden(param1Int, param2Int);
            case WIDGET_SUB_IS_HIDDEN:
                return InteractionUtils.isWidgetHidden(param1Int, param2Int, param3Int);
            case WIDGET_SUB_IS_SHOWING:
                return !InteractionUtils.isWidgetHidden(param1Int, param2Int, param3Int);
        }
        return false;
    }
    private void processAction(Action action, String[] actionParams)
    {
        if (tickDelay > 0 && action != Action.RESET_DELAY && action != Action.PRINT_VARIABLE)
        {
            if (config.debugOutput())
            {
                MessageUtils.addMessage("Tick delay of " + tickDelay + " not processing actions", Color.RED);
            }
            return;
        }

        int param1Int = -69420;
        int param2Int = -69420;
        int param3Int = -69420;
        int param4Int = -69420;

        if (actionParams.length >= 2)
        {
            if (actionParams[1].contains("%"))
            {
                if (action != Action.PRINT_VARIABLE && action != Action.ADD_VALUE_TO_VARIABLE && action != Action.SUBTRACT_VALUE_FROM_VARIABLE && action != Action.SET_VAR_VALUE)
                {
                    String varValue = getVarValue(actionParams[1].replaceAll("%", ""));
                    if (varValue != null)
                    {
                        if (isNumeric(varValue))
                        {
                            param1Int = Integer.parseInt(varValue);
                        }
                        else
                        {
                            actionParams[1] = varValue;
                        }
                    }
                }
            }
            else if (isNumeric(actionParams[1]))
            {
                param1Int = Integer.parseInt(actionParams[1]);
            }
        }

        if (actionParams.length >= 3)
        {
            if (actionParams[2].contains("%"))
            {
                String varValue = getVarValue(actionParams[2].replaceAll("%", ""), action == Action.SET_VAR_VALUE || action == Action.ADD_VALUE_TO_VARIABLE);
                if (varValue != null)
                {
                    if (isNumeric(varValue) && !varValue.contains("|"))
                    {
                        param2Int = Integer.parseInt(varValue);
                    }
                    else
                    {
                        actionParams[2] = varValue;
                    }
                }
            }
            else if (isNumeric(actionParams[2]))
            {
                param2Int = Integer.parseInt(actionParams[2]);
            }
        }

        if (actionParams.length >= 4)
        {
            if (actionParams[3].contains("%"))
            {
                String varValue = getVarValue(actionParams[3].replaceAll("%", ""));
                if (varValue != null)
                {
                    if (isNumeric(varValue))
                    {
                        param3Int = Integer.parseInt(varValue);
                    }
                    else
                    {
                        actionParams[3] = varValue;
                    }
                }
            }
            else if (isNumeric(actionParams[3]))
            {
                param3Int = Integer.parseInt(actionParams[3]);
            }
        }

        if (actionParams.length >= 5)
        {
            if (actionParams[4].contains("%"))
            {
                String varValue = getVarValue(actionParams[4].replaceAll("%", ""));
                if (varValue != null)
                {
                    if (isNumeric(varValue))
                    {
                        param4Int = Integer.parseInt(varValue);
                    }
                    else
                    {
                        actionParams[4] = varValue;
                    }
                }
            }
            else if (isNumeric(actionParams[4]))
            {
                param4Int = Integer.parseInt(actionParams[4]);
            }
        }

        switch (action)
        {
            case PRINT_VARIABLE:
                handleVariablePrinting(actionParams[1]);
                break;
            case SET_DELAY:
                tickDelay = param1Int;
                break;
            case RESET_DELAY:
                tickDelay = 0;
                break;
            case CLOSE_BANK:
                BankUtils.close();
                break;
            case WALK_RELATIVE_TO_SELF:
                InteractionUtils.walk(client.getLocalPlayer().getWorldLocation().dx(param1Int).dy(param2Int));
                break;
            case WALK_ABSOLUTE_LOCATION:
                InteractionUtils.walk(new WorldPoint(param1Int, param2Int, client.getLocalPlayer().getWorldLocation().getPlane()));
                break;
            case INTERACT_NEAREST_NAMED_NPC:
                final NPC toInteract = NpcUtils.getNearestNpc(actionParams[1]);

                if (toInteract != null)
                {
                    NpcUtils.interact(toInteract, actionParams[2]);
                }
                break;
            case INTERACT_NEAREST_NAMED_OBJECT:
                final TileObject tileObject = GameObjectUtils.nearest(actionParams[1]);

                if (tileObject != null)
                {
                    GameObjectUtils.interact(tileObject, actionParams[2]);
                }
                break;
            case INTERACT_NAMED_INVENTORY_ITEM:
                final Item item = InventoryUtils.getFirstItem(actionParams[1]);

                if (item != null)
                {
                    InventoryUtils.itemInteract(item.getId(), actionParams[2]);
                }
                break;
            case INTERACT_NEAREST_ID_NPC:
                final NPC npcIdToInteract = NpcUtils.getNearestNpc(param1Int);

                if (npcIdToInteract != null)
                {
                    NpcUtils.interact(npcIdToInteract, actionParams[2]);
                }
                break;
            case INTERACT_NEAREST_ID_OBJECT:
                final TileObject tileObjectId = GameObjectUtils.nearest(param1Int);

                if (tileObjectId != null)
                {
                    GameObjectUtils.interact(tileObjectId, actionParams[2]);
                }
                break;
            case INTERACT_ID_INVENTORY_ITEM:
                final int p1Int = param1Int;
                InventoryUtils.getAllSlotted(it -> it.getItem().getId() == p1Int).stream().findFirst().ifPresent(idItem -> InventoryUtils.itemInteract(p1Int, actionParams[2]));
                break;
            case RELOAD_VARS:
                initUserVariables();
                break;
            case SET_VAR_VALUE:
                String varValue = getVarValue(actionParams[2].replaceAll("%", ""), true);
                if (isReservedVar(actionParams[1]))
                {
                    break;
                }

                if (getVarValue(actionParams[1].replaceAll("%", "")) == null)
                {
                    break;
                }

                if (!varValue.isEmpty())
                {
                    userVariables.put(actionParams[1].replaceAll("%", ""), varValue);
                }
                else
                {
                    userVariables.put(actionParams[1].replaceAll("%", ""), actionParams[2]);
                }
                break;
            case INTERACT_NEAREST_NAMED_TILE_ITEM:
                InteractionUtils.interactWithTileItem(actionParams[1], actionParams[2]);
                break;
            case INTERACT_NEAREST_ID_TILE_ITEM:
                InteractionUtils.interactWithTileItem(param1Int, actionParams[2]);
                break;
            case SET_TICK_METRONOME_MAX_VALUE:
                tickMetronomeMaxValue = param1Int;
                break;
            case SET_TICK_METRONOME_VALUE:
                tickMetronomeCount = param1Int;
                break;
            case SET_TICK_METRONOME_TO_MAX:
                tickMetronomeCount = tickMetronomeMaxValue;
                break;
            case ADD_VALUE_TO_VARIABLE:
                String varVal = getVarValue(actionParams[1].replaceAll("%", ""), true);
                if (varVal.isEmpty())
                {
                    break;
                }

                if (isNumeric(varVal))
                {
                    int intVar = Integer.parseInt(varVal);
                    intVar += param2Int;
                    userVariables.put(actionParams[1].replaceAll("%", ""), String.valueOf(intVar));
                }
                else
                {
                    userVariables.put(actionParams[1].replaceAll("%", ""), varVal + actionParams[2]);
                }
                break;
            case INTERACT_NAMED_PLAYER:
                PlayerUtils.interactPlayer(actionParams[1], actionParams[2]);
                break;
            case INTERACT_LAST_PLAYER_YOU_TARGETED:
                if (lastPlayerNameYouTargeted.isEmpty())
                {
                    break;
                }
                PlayerUtils.interactPlayer(lastPlayerNameYouTargeted, actionParams[1]);
                break;
            case INTERACT_LAST_PLAYER_TARGETED_YOU:
                if (lastPlayerNameTargetedYou.isEmpty())
                {
                    break;
                }
                PlayerUtils.interactPlayer(lastPlayerNameTargetedYou, actionParams[1]);
                break;
            case INTERACT_LAST_NPC_YOU_TARGETED:
                if (lastNpcNameYouTargeted.isEmpty())
                {
                    break;
                }
                NPC toTargetNpc = NpcUtils.getNearestNpc(lastNpcNameYouTargeted);
                if (toTargetNpc != null)
                {
                    NpcUtils.interact(toTargetNpc, actionParams[1]);
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
                    NpcUtils.interact(toTargetNpc2, actionParams[1]);
                }
                break;
            case ACTIVATE_PRAYER:
                Prayer pToActivate = CombatUtils.prayerForName(actionParams[1]);
                if (pToActivate != null)
                {
                    CombatUtils.activatePrayer(pToActivate);
                }
                break;
            case TOGGLE_PRAYER:
                Prayer pToToggle = CombatUtils.prayerForName(actionParams[1]);
                if (pToToggle != null)
                {
                    CombatUtils.togglePrayer(pToToggle);
                }
                break;
            case TOGGLE_SPEC:
                CombatUtils.toggleSpec();
                break;
            case WALK_SCENE_LOCATION:
                LocalPoint lp = LocalPoint.fromScene(param1Int, param2Int, client.getTopLevelWorldView());
                InteractionUtils.walk(WorldPoint.fromLocalInstance(client, lp));
                break;
            case INTERACT_INVENTORY_SLOT:
                InventoryUtils.interactSlot(param1Int, actionParams[2]);
                break;
            case ADD_TILE_MARKER_WORLD_POINT:
                addWorldPointTileMarker(param1Int, param2Int, "");
                break;
            case ADD_TILE_MARKER_WORLD_POINT_WITH_TEXT:
                addWorldPointTileMarker(param1Int, param2Int, actionParams[3]);
                break;
            case REMOVE_TILE_MARKER_WORLD_POINT:
                removeWorldPointTileMarker(param1Int, param2Int);
                break;
            case ADD_TILE_MARKER_REGION_POINT:
                addRegionPointTileMarker(param1Int, param2Int, param3Int, "");
                break;
            case ADD_TILE_MARKER_REGION_POINT_WITH_TEXT:
                addRegionPointTileMarker(param1Int, param2Int, param3Int, actionParams[4]);
                break;
            case REMOVE_TILE_MARKER_REGION_POINT:
                removeRegionTileMarkerAt(param1Int, param2Int, param3Int);
                break;
            case RESET_ALL_REGION_POINT_TILE_MARKERS:
                regionPointTileMarkers.clear();
                break;
            case RESET_ALL_WORLD_POINT_TILE_MARKERS:
                worldPointTileMarkers.clear();
                break;
            case ADD_TILE_MARKER_FOR_NPC_NAME:
                trackNpcName(actionParams[1]);
                break;
            case REMOVE_TILE_MARKER_FOR_NPC_NAME:
                npcNamesTracked.remove(actionParams[1]);
                break;
            case ADD_TILE_MARKER_FOR_PLAYER_NAME:
                trackPlayerName(actionParams[1]);
                break;
            case REMOVE_TILE_MARKER_FOR_PLAYER_NAME:
                playerNamesTracked.remove(actionParams[1]);
                break;
            case REMOVE_ALL_NPC_MARKERS:
                resetTrackedNpcs();
                break;
            case REMOVE_ALL_PLAYER_MARKERS:
                resetTrackedPlayers();
                break;
            case WALK_REGION_LOCATION:
                WorldPoint wp = WorldPoint.fromRegion(param1Int, param2Int, param3Int, client.getLocalPlayer().getWorldLocation().getPlane());
                Collection<WorldPoint> localInstanceWp = WorldPoint.toLocalInstance(client, wp);
                localInstanceWp.stream().findFirst().ifPresent(InteractionUtils::walk);
                break;
            case NAMED_ITEM_ON_ITEM:
                Item first = InventoryUtils.getFirstItem(actionParams[1]);
                Item second = InventoryUtils.getFirstItem(actionParams[2]);
                InventoryUtils.itemOnItem(first, second);
                break;
            case ID_ITEM_ON_ITEM:
                SlottedItem firstId = InventoryUtils.getFirstItemSlotted(param1Int);
                SlottedItem secondId = InventoryUtils.getFirstItemSlotted(param2Int);
                InventoryUtils.itemOnItem(firstId.getItem(), secondId.getItem());
                break;
            case NAMED_ITEM_ON_NPC:
                Item firstNamedItem = InventoryUtils.getFirstItem(actionParams[1]);
                NPC firstNamedNpc = NpcUtils.getNearestNpc(actionParams[2]);
                InteractionUtils.useItemOnNPC(firstNamedItem.getId(), firstNamedNpc);
                break;
            case ID_ITEM_ON_NPC:
                SlottedItem firstIdItem = InventoryUtils.getFirstItemSlotted(param1Int);
                NPC firstIDNpc = NpcUtils.getNearestNpc(param2Int);
                InteractionUtils.useItemOnNPC(firstIdItem.getItem().getId(), firstIDNpc);
                break;
            case NAMED_ITEM_ON_OBJECT:
                Item firstNamedItem1 = InventoryUtils.getFirstItem(actionParams[1]);
                TileObject namedTileObject = GameObjectUtils.nearest(actionParams[2]);
                InteractionUtils.useItemOnWallObject(firstNamedItem1, namedTileObject);
                break;
            case ID_ITEM_ON_OBJECT:
                SlottedItem firstIdItem1 = InventoryUtils.getFirstItemSlotted(param1Int);
                TileObject idObject = GameObjectUtils.nearest(param2Int);
                InteractionUtils.useItemOnWallObject(firstIdItem1.getItem(), idObject);
                break;
            case WIDGET_ACTION:
                InteractionUtils.widgetInteract(param1Int, param2Int, actionParams[3]);
                break;
            case WIDGET_SUBCHILD_ACTION:
                InteractionUtils.widgetInteract(param1Int, param2Int, param3Int, actionParams[4]);
                break;
            case WIDGET_RESUME_PAUSE:
                InteractionUtils.queueResumePause(param1Int, param2Int, param3Int);
                break;
            case SUBTRACT_VALUE_FROM_VARIABLE:
                String varVal2 = getVarValue(actionParams[1].replaceAll("%", ""));
                if (varVal2.isEmpty())
                {
                    break;
                }

                if (isNumeric(varVal2))
                {
                    int intVar = Integer.parseInt(varVal2);
                    intVar -= param2Int;
                    userVariables.put(actionParams[1].replaceAll("%", ""), String.valueOf(intVar));
                }
                break;
            case EVALUATE_HOTKEY_IN_X_TICKS:
                ArrayList<Integer> hotkeysToAdd = new ArrayList<>();
                ArrayList<Integer> hotkeysPending = hotkeysToRun.get(client.getTickCount() + param2Int);
                if (hotkeysPending != null)
                {
                    hotkeysToAdd.addAll(hotkeysPending);
                }

                hotkeysToAdd.add(param1Int);

                hotkeysToRun.put(client.getTickCount() + param2Int, hotkeysToAdd);
                break;
        }
    }

    private boolean isProjectileIdTargetingPlayer(int id)
    {
        if (validProjectiles.isEmpty())
        {
            return false;
        }

        for (Projectile proj : validProjectiles)
        {
            if (proj == null || proj.getId() != id || proj.getRemainingCycles() <= 0 || proj.getInteracting() == null)
            {
                continue;
            }

            if (proj.getInteracting() == client.getLocalPlayer())
            {
                return true;
            }
        }
        return false;
    }

    private boolean isProjectileIdTargetingTile(int id, int x, int y)
    {
        if (validProjectiles.isEmpty())
        {
            return false;
        }

        for (Projectile proj : validProjectiles)
        {
            if (proj == null || proj.getId() != id || proj.getRemainingCycles() <= 0 || proj.getTarget() == null)
            {
                continue;
            }

            LocalPoint target = proj.getTarget();
            if (target.getSceneX() == x && target.getSceneY() == y)
            {
                return true;
            }
        }
        return false;
    }


    private void handleVariablePrinting(String variable)
    {
        String userVar = variable.replaceAll("%", "");

        String varValue = getVarValue(userVar, true);

        if (!varValue.isEmpty())
        {
            MessageUtils.addMessage("Var " + userVar + ": " + varValue, Color.RED);
        }
        else
        {
            MessageUtils.addMessage("Var " + userVar + " doesn't exist.", Color.RED);
        }

    }

    private boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private void initUserVariables()
    {
        String varsString = config.customVariables().replaceAll("[ \\n]", "");

        if (varsString.isEmpty())
        {
            return;
        }

        userVariables.clear();

        if (!varsString.contains("/"))
        {
            varsString += "/";
        }

        String[] varsSplit = varsString.split("/");

        for (int i = 0; i < varsSplit.length; i++)
        {
            if (!varsSplit[i].contains("="))
            {
                if (client.getGameState() == GameState.LOGGED_IN)
                {
                    MessageUtils.addMessage(varsSplit[i] + " is not a valid variable assignment.", Color.RED);
                }
                continue;
            }

            String[] varParts = varsSplit[i].split("=");

            if (varParts.length != 2)
            {
                continue;
            }

            String varName = varParts[0];
            String varValue = varParts[1];

            if (isReservedVar(varName))
            {
                if (client.getGameState() == GameState.LOGGED_IN)
                {
                    MessageUtils.addMessage(varName + " is part of a reserved var name and cannot be used", Color.RED);
                }
                continue;
            }

            userVariables.put(varName, varValue);
        }
    }

    public String getVarValue(String varName, boolean rawValue)
    {
        if (isReservedVar(varName))
        {
            return reservedVarValue(varName);
        }
        else
        {
            String varValue = getVarCheckCoords(varName);
            if (varValue.startsWith("*"))
            {
                return rawValue ? varValue : varValue.substring(1);
            }
            else
            {
                if (varValue.startsWith("%") && isDynamicReservedVar(varValue.substring(1)))
                {
                    return getDynamicReservedVarValue(varValue.substring(1));
                }

                return rawValue ? varValue : varValue.replaceAll("_", " ");
            }
        }
    }

    public String getVarValue(String varName)
    {
        return getVarValue(varName, false);
    }

    private String getVarCheckCoords(String varName)
    {
        String varValue = "";
        if (varName.contains(".x") || varName.contains(".y")|| varName.contains(".z"))
        {
            final String newVarName = varName.substring(0, varName.length() - 2);
            varValue = userVariables.getOrDefault(newVarName, "");
            String[] coords = varValue.split(Pattern.quote("|"));
            if (varName.contains(".x"))
            {
                return coords.length > 0 ? coords[0] : "-1";
            }
            else if (varName.contains(".y"))
            {
                return coords.length > 1 ? coords[1] : "-1";
            }
            else if (varName.contains(".z"))
            {
                return coords.length > 2 ? coords[2] : "-1";
            }
        }
        else
        {
            varValue = userVariables.getOrDefault(varName, "");
        }
        return varValue;
    }

    private boolean isReservedVar(String varName)
    {
        final List<String> reservedVars = List.of(
                "mypos",
                "lastPlayerAnimationTick",
                "ticksSinceLastAnimation",
                "lastPlayerAnimationId",
                "lpWorldX",
                "lpWorldY",
                "lpRegionX",
                "lpRegionY",
                "lpSceneX",
                "lpSceneY",
                "tickCount",
                "metronomeCount",
                "tickDelay",
                "lastNpcNameYouTargeted",
                "lastPlayerNameYouTargeted",
                "lastNpcNameTargetedYou",
                "lastPlayerNameTargetedYou",
                "regionId",
                "worldPosF",
                "regionPosF",
                "lpPlane"
        );

        return reservedVars.contains(varName) || isDynamicReservedVar(varName);
    }

    private boolean isDynamicReservedVar(String varName)
    {
        final List<String> dynamicVars = List.of(
                "nbi",
                "ibi",
                "nii",
                "iii",
                "nsi",
                "isi",
                "nnnwl",
                "ninwl",
                "nnnrl",
                "ninrl"
        );

        for (String var : dynamicVars)
        {
            if (varName.startsWith(var) && varName.length() > var.length() + 1)
            {
                return true;
            }
        }

        return false;
    }

    private String getDynamicReservedVarValue(String varName)
    {
        if (varName.startsWith("nbi"))
        {
            String namedBankItem = varName.substring(4).replaceAll("_", " ");
            return String.valueOf(getNamedItemIndex(namedBankItem, 12, 13));
        }

        if (varName.startsWith("ibi"))
        {
            int itemId = Integer.parseInt(varName.substring(4));
            return String.valueOf(getIdItemIndex(itemId, 12, 13));
        }

        if (varName.startsWith("nii"))
        {
            String namedBankItem = varName.substring(4).replaceAll("_", " ");
            return String.valueOf(getNamedItemIndex(namedBankItem, 149, 0));
        }

        if (varName.startsWith("iii"))
        {
            int itemId = Integer.parseInt(varName.substring(4));
            return String.valueOf(getIdItemIndex(itemId, 149, 0));
        }

        if (varName.startsWith("nsi"))
        {
            String namedBankItem = varName.substring(4).replaceAll("_", " ");
            return String.valueOf(getNamedItemIndex(namedBankItem, 300, 2));
        }

        if (varName.startsWith("isi"))
        {
            int itemId = Integer.parseInt(varName.substring(4));
            return String.valueOf(getIdItemIndex(itemId, 300, 2));
        }

        if (varName.startsWith("nnnwl") || varName.startsWith("ninwl") ||
                varName.startsWith("nnnrl") || varName.startsWith("ninrl"))
        {
            NPC nearestNpc = null;
            boolean varNameHasCoords = varName.endsWith(".x") || varName.endsWith(".y") || varName.endsWith(".z");
            String varNameNoCoords = varName.substring(6, varNameHasCoords ? varName.length() - 2 : varName.length());

            if (varName.startsWith("nnnwl") || varName.startsWith("nnnrl"))
            {
                nearestNpc = NpcUtils.getNearestNpc(varNameNoCoords.replaceAll("_", " "));
            }
            else
            {
                nearestNpc = NpcUtils.getNearestNpc(Integer.parseInt(varNameNoCoords));
            }

            if (nearestNpc != null)
            {
                final WorldPoint nnwp = nearestNpc.getWorldLocation();
                if (varName.endsWith(".x"))
                {
                    return varName.contains("wl") ? String.valueOf(nnwp.getX()) : String.valueOf(nnwp.getRegionX());
                }
                else if (varName.endsWith(".y"))
                {
                    return varName.contains("wl") ? String.valueOf(nnwp.getY()) : String.valueOf(nnwp.getRegionY());
                }
                else if (varName.endsWith(".z"))
                {
                    return String.valueOf(nnwp.getPlane());
                }
                else
                {
                    return varName.contains("wl") ?  (nnwp.getX() + "|" + nnwp.getY() + "|" + nnwp.getPlane()) : (nnwp.getRegionX() + "|" + nnwp.getRegionY() + "|" + nnwp.getPlane());
                }
            }

        }
        return "undefined";
    }

    public int getNamedItemIndex(String name, int parentId, int childId)
    {
        Widget inventoryWidget = client.getWidget(parentId, childId);
        if (inventoryWidget == null || inventoryWidget.isSelfHidden())
        {
            return -1;
        }

        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        int itemIndex = -1;
        if (inventoryItems != null && inventoryItems.length > 0)
        {
            for (int i = 0; i < inventoryItems.length; i++)
            {
                if (inventoryItems[i] != null)
                {
                    ItemComposition comp = client.getItemDefinition(inventoryItems[i].getItemId());
                    if (comp.getName() != null && comp.getName().contains(name))
                    {
                        itemIndex = i;
                        break;
                    }
                }
            }
        }

        return itemIndex;
    }

    public int getIdItemIndex(int id, int parentId, int childId)
    {
        Widget inventoryWidget = client.getWidget(parentId, childId);
        if (inventoryWidget == null || inventoryWidget.isSelfHidden())
        {
            return -1;
        }

        Widget[] inventoryItems = inventoryWidget.getDynamicChildren();
        if (inventoryItems != null && inventoryItems.length > 0)
        {
            for (int i = 0; i < inventoryItems.length; i++)
            {
                if (inventoryItems[i] != null)
                {
                    if (inventoryItems[i].getItemId() == id)
                    {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    public String reservedVarValue(String varName)
    {
        if (isDynamicReservedVar(varName))
        {
            return getDynamicReservedVarValue(varName);
        }

        final WorldPoint worldLocation = client.getLocalPlayer().getWorldLocation();
        final int worldX = worldLocation.getX();
        final int worldY = worldLocation.getY();
        final int plane = client.getLocalPlayer().getWorldLocation().getPlane();

        final LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();
        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        final int sceneX = localLocation.getSceneX();
        final int sceneY = localLocation.getSceneY();

        int regionX = worldLocation.getRegionX();
        int regionY = worldLocation.getRegionY();

        if (client.getTopLevelWorldView().isInstance())
        {
            final WorldPoint fromInstance = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
            regionX = fromInstance.getRegionX();
            regionY = fromInstance.getRegionY();
            regionId = fromInstance.getRegionID();
        }

        switch (varName)
        {
            case "mypos":
                return "Location - World(" + worldX + ", " + worldY + ", " + plane + ") "
                        + "Region(" + regionX + ", " + regionY + ") Region: " + regionId;
            case "lastPlayerAnimationTick":
                return String.valueOf(lastPlayerAnimationTick);
            case "ticksSinceLastAnimation":
                return String.valueOf(ticksSinceLastPlayerAnimation());
            case "lastPlayerAnimationId":
                return String.valueOf(lastPlayerAnimationId);
            case "lpWorldX":
                return String.valueOf(worldX);
            case "lpWorldY":
                return String.valueOf(worldY);
            case "lpRegionX":
                return String.valueOf(regionX);
            case "lpRegionY":
                return String.valueOf(regionY);
            case "lpSceneX":
                return String.valueOf(sceneX);
            case "lpSceneY":
                return String.valueOf(sceneY);
            case "tickCount":
                return String.valueOf(client.getTickCount());
            case "metronomeCount":
                return String.valueOf(tickMetronomeCount);
            case "tickDelay":
                return String.valueOf(tickDelay);
            case "lastNpcNameYouTargeted":
                return String.valueOf(lastNpcNameYouTargeted);
            case "lastPlayerNameYouTargeted":
                return String.valueOf(lastPlayerNameYouTargeted);
            case "lastNpcNameTargetedYou":
                return String.valueOf(lastNpcNameTargetedYou);
            case "lastPlayerNameTargetedYou":
                return String.valueOf(lastPlayerNameTargetedYou);
            case "regionId":
                if (client.getTopLevelWorldView().isInstance())
                {
                    WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
                    return String.valueOf(worldPoint.getRegionID());
                }
                else
                {
                    return String.valueOf(client.getLocalPlayer().getWorldLocation().getRegionID());
                }
            case "worldPosF":
                return worldX + "|" + worldY + "|" + plane;
            case "regionPosF":
                return regionX + "|" + regionY;
            case "lpPlane":
                return String.valueOf(plane);
            default:
                return "";
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

}