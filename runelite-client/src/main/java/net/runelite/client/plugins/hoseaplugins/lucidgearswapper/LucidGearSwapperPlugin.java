package net.runelite.client.plugins.hoseaplugins.lucidgearswapper;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.item.SlottedItem;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
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
import java.util.stream.Collectors;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=\"#32CD32\">Lucid </font>Gear Swapper</html>",
        description = "Set-up up to 6 custom gear swaps with customizable hotkeys or trigger them via weapon equip",
        enabledByDefault = false,
        tags = {"gear", "swap", "swapper", "hotkey"}
)
public class LucidGearSwapperPlugin extends Plugin implements KeyListener
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidGearSwapperConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private KeyManager keyManager;

    private String[] configs = new String[6];

    private GearSwapState gearSwapState = GearSwapState.TICK_1;

    private int gearSwapSelected = -1;

    private int lastSwapSelected = -1;

    private List<Integer> lastItemsEquipped = new ArrayList<>();

    private List<String> lastEquipmentList = new ArrayList<>();

    private final List<Integer> slotOrderToCopy = List.of(EquipmentInventorySlot.WEAPON.getSlotIdx(), EquipmentInventorySlot.SHIELD.getSlotIdx(), EquipmentInventorySlot.HEAD.getSlotIdx(), EquipmentInventorySlot.BODY.getSlotIdx(),
            EquipmentInventorySlot.LEGS.getSlotIdx(), EquipmentInventorySlot.CAPE.getSlotIdx(), EquipmentInventorySlot.BOOTS.getSlotIdx(), EquipmentInventorySlot.AMULET.getSlotIdx(),
            EquipmentInventorySlot.GLOVES.getSlotIdx(), EquipmentInventorySlot.RING.getSlotIdx(), EquipmentInventorySlot.AMMO.getSlotIdx());

    public static final String GROUP_NAME = "lucid-gear-swapper";

    public static final File PRESET_DIR = new File(RUNELITE_DIR, GROUP_NAME);

    public static final String FILENAME_SPECIAL_CHAR_REGEX = "[^a-zA-Z\\d:]";

    public final GsonBuilder builder = new GsonBuilder()
            .setPrettyPrinting();
    public final Gson gson = builder.create();

    public Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Provides
    LucidGearSwapperConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidGearSwapperConfig.class);
    }

    @Override
    protected void startUp()
    {
        clientThread.invoke(this::pluginEnabled);
    }

    @Override
    protected void shutDown()
    {
        keyManager.unregisterKeyListener(this);
    }

    @Subscribe
    private void onConfigChanged(final ConfigChanged event)
    {
        if (!event.getGroup().equals("lucid-gear-swapper"))
        {
            return;
        }

        parseSwaps();
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        getEquipmentChanges();

        if (lastItemsEquipped.size() > 0)
        {
            for (int i = 0; i < configs.length; i++)
            {
                if (!isActivateOnFirstItem(i) || configs[i] == null)
                {
                    continue;
                }

                List<String> configList = parseList(configs[i]);
                if (configList == null || configList.size() == 0)
                {
                    continue;
                }

                String itemString = configList.get(0).strip();
                List<SlottedItem> firstItem = EquipmentUtils.getAll().stream().filter(item -> client.getItemDefinition(item.getItem().getId()).getName().contains(itemString)).collect(Collectors.toList());

                if (firstItem.size() > 0)
                {
                    if (lastItemsEquipped.contains(firstItem.get(0).getItem().getId()) && gearSwapSelected == -1)
                    {
                        if (isSlotEnabled(i))
                        {
                            gearSwapSelected = i;
                        }
                    }
                }
            }

            lastItemsEquipped.clear();
        }

        if (client.getTickCount() < 10)
        {
            gearSwapSelected = -1;
            return;
        }

        if (gearSwapState == GearSwapState.FINISHED)
        {
            gearSwapSelected = -1;
            gearSwapState = GearSwapState.TICK_1;
        }

        if (gearSwapSelected != -1)
        {
            if (gearSwapState == GearSwapState.TICK_1)
            {
                if (config.oneTickSwap())
                {
                    swap(gearSwapSelected, false);
                    gearSwapState = GearSwapState.FINISHED;
                    lastSwapSelected = gearSwapSelected;
                }
                else
                {
                    swap(gearSwapSelected, true);
                    gearSwapState = GearSwapState.TICK_2;
                    lastSwapSelected = -1;
                }
            }
            else if (gearSwapState == GearSwapState.TICK_2)
            {
                swap(gearSwapSelected, false);
                gearSwapState = GearSwapState.FINISHED;
                lastSwapSelected = gearSwapSelected;
            }
        }

        if (shouldActivateSpec())
        {
            CombatUtils.toggleSpec();
        }

        lastSwapSelected = -1;
    }

    private void getEquipmentChanges()
    {
        Widget bankWidget = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (bankWidget != null && !bankWidget.isSelfHidden())
        {
            return;
        }

        final List<SlottedItem> equippedItems = EquipmentUtils.getAll();
        final List<String> itemsMapped = equippedItems.stream().map(item -> client.getItemDefinition(item.getItem().getId()).getName()).collect(Collectors.toList());

        if (!listsMatch(itemsMapped, lastEquipmentList))
        {
            for (SlottedItem slottedItem : equippedItems)
            {
                String name = client.getItemDefinition(slottedItem.getItem().getId()).getName();
                if (!lastEquipmentList.contains(name))
                {
                    if (gearSwapSelected == -1)
                    {
                        lastItemsEquipped.add(slottedItem.getItem().getId());
                    }
                }
            }
            lastEquipmentList.clear();
            lastEquipmentList.addAll(itemsMapped);
        }
    }

    private boolean isActivateOnFirstItem(int configListIndex)
    {
        switch (configListIndex)
        {
            case 0:
                if (config.equipFirstItem1())
                {
                    return true;
                }
                return false;
            case 1:
                if (config.equipFirstItem2())
                {
                    return true;
                }
                return false;
            case 2:
                if (config.equipFirstItem3())
                {
                    return true;
                }
                return false;
            case 3:
                if (config.equipFirstItem4())
                {
                    return true;
                }
                return false;
            case 4:
                if (config.equipFirstItem5())
                {
                    return true;
                }
                return false;
            case 5:
                if (config.equipFirstItem6())
                {
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    private void pluginEnabled()
    {
        keyManager.registerKeyListener(this);
        parseSwaps();
    }

    private void parseSwaps()
    {
        configs[0] = config.swap1String();
        configs[1] = config.swap2String();
        configs[2] = config.swap3String();
        configs[3] = config.swap4String();
        configs[4] = config.swap5String();
        configs[5] = config.swap6String();
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (config.loadPresetHotkey().matches(e))
        {
            clientThread.invoke(this::loadPreset);
        }

        if (config.savePresetHotkey().matches(e))
        {
            clientThread.invoke(this::savePreset);
        }

        if (config.copyGearHotkey().matches(e))
        {
            if (client == null || client.getGameState() != GameState.LOGGED_IN)
            {
                return;
            }

            int slotSelected = getSlotFromGearSlotSelected(config.slotToCopyTo());

            if (slotSelected != 0)
            {
                clientThread.invoke(() -> {
                    final List<SlottedItem> equippedGear = EquipmentUtils.getAll();
                    equippedGear.sort(Comparator.comparing(item -> slotOrderToCopy.indexOf(item.getSlot())));

                    String equippedItemsString = equippedGear.stream().map(slottedItem -> client.getItemDefinition(slottedItem.getItem().getId()).getName()).collect(Collectors.joining(","));
                    String key = "swap" + slotSelected + "String";
                    configManager.setConfiguration("lucid-gear-swapper", key, equippedItemsString);
                    MessageUtils.addMessage("Copied Equipment to Preset Slot " + slotSelected, Color.RED);
                });
            }
        }

        if (config.swap1Hotkey().matches(e) && config.swap1Enabled())
        {
            clientThread.invoke(() -> {
                if (client.getGameState() != GameState.LOGGED_IN)
                {
                    return;
                }

                if (config.oneTickSwap())
                {
                    swap(0, false);
                    gearSwapState = GearSwapState.TICK_1;
                }
                else
                {
                    gearSwapSelected = 0;
                    swap(0, true);
                    gearSwapState = GearSwapState.TICK_2;
                }
                lastSwapSelected = 0;
            });
        }

        if (config.swap2Hotkey().matches(e) && config.swap2Enabled())
        {
            clientThread.invoke(() -> {
                if (client.getGameState() != GameState.LOGGED_IN)
                {
                    return;
                }

                if (config.oneTickSwap())
                {
                    swap(1, false);
                    gearSwapState = GearSwapState.TICK_1;
                }
                else
                {
                    gearSwapSelected = 1;
                    swap(1, true);
                    gearSwapState = GearSwapState.TICK_2;
                }
                lastSwapSelected = 1;
            });
        }

        if (config.swap3Hotkey().matches(e) && config.swap3Enabled())
        {
            clientThread.invoke(() -> {
                if (client.getGameState() != GameState.LOGGED_IN)
                {
                    return;
                }

                if (config.oneTickSwap())
                {
                    swap(2, false);
                    gearSwapState = GearSwapState.TICK_1;
                }
                else
                {
                    gearSwapSelected = 2;
                    swap(2, true);
                    gearSwapState = GearSwapState.TICK_2;
                }
                lastSwapSelected = 2;
            });
        }

        if (config.swap4Hotkey().matches(e) && config.swap4Enabled())
        {
            clientThread.invoke(() -> {
                if (client.getGameState() != GameState.LOGGED_IN)
                {
                    return;
                }

                if (config.oneTickSwap())
                {
                    swap(3, false);
                    gearSwapState = GearSwapState.TICK_1;
                }
                else
                {
                    gearSwapSelected = 3;
                    swap(3, true);
                    gearSwapState = GearSwapState.TICK_2;
                }
                lastSwapSelected = 3;
            });
        }

        if (config.swap5Hotkey().matches(e) && config.swap5Enabled())
        {
            clientThread.invoke(() -> {
                if (client.getGameState() != GameState.LOGGED_IN)
                {
                    return;
                }

                if (config.oneTickSwap())
                {
                    swap(4, false);
                    gearSwapState = GearSwapState.TICK_1;
                }
                else
                {
                    gearSwapSelected = 4;
                    swap(4, true);
                    gearSwapState = GearSwapState.TICK_2;
                }
                lastSwapSelected = 4;
            });
        }

        if (config.swap6Hotkey().matches(e) && config.swap6Enabled())
        {
            clientThread.invoke(() -> {
                if (client.getGameState() != GameState.LOGGED_IN)
                {
                    return;
                }

                if (config.oneTickSwap())
                {
                    swap(5, false);
                    gearSwapState = GearSwapState.TICK_1;
                }
                else
                {
                    gearSwapSelected = 5;
                    swap(5, true);
                    gearSwapState = GearSwapState.TICK_2;
                }
                lastSwapSelected = 5;
            });
        }

        if (gearSwapSelected != -1)
        {
            e.consume();
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    private void swap(int swapId, boolean swapFirstHalf)
    {
        List<String> itemList = parseList(configs[swapId]);
        List<SlottedItem> validItems = new ArrayList<>();
        for (String item : itemList)
        {
            Optional<SlottedItem> slottedItem = Inventory.search().nameContains(item.strip()).result().stream().map(widget -> new SlottedItem(widget.getItemId(), widget.getItemQuantity(), widget.getIndex())).findFirst();
            slottedItem.ifPresent(validItems::add);
        }

        if (!validItems.isEmpty())
        {
            if (swapFirstHalf)
            {
                for (int i = 0; i < validItems.size() / 2; i++)
                {
                    SlottedItem item = validItems.get(i);
                    if (InventoryUtils.itemHasAction(item.getItem().getId(), "Wield"))
                    {
                        InventoryUtils.itemInteract(item.getItem().getId(), "Wield");
                    }
                    else if (InventoryUtils.itemHasAction(item.getItem().getId(), "Wear"))
                    {
                        InventoryUtils.itemInteract(item.getItem().getId(), "Wear");
                    }
                }
            }
            else
            {
                for (SlottedItem item : validItems)
                {
                    if (InventoryUtils.itemHasAction(item.getItem().getId(), "Wield"))
                    {
                        InventoryUtils.itemInteract(item.getItem().getId(), "Wield");
                    }
                    else if (InventoryUtils.itemHasAction(item.getItem().getId(), "Wear"))
                    {
                        InventoryUtils.itemInteract(item.getItem().getId(), "Wear");
                    }
                }
            }
        }
    }

    private int getSlotFromGearSlotSelected(LucidGearSwapperConfig.GearSlot slot)
    {
        switch (slot)
        {
            case GEAR_SLOT_1:
                return 1;
            case GEAR_SLOT_2:
                return 2;
            case GEAR_SLOT_3:
                return 3;
            case GEAR_SLOT_4:
                return 4;
            case GEAR_SLOT_5:
                return 5;
            case GEAR_SLOT_6:
                return 6;
            default:
                return 0;
        }
    }

    private boolean isSlotEnabled(int slot)
    {
        switch (slot)
        {
            case 0:
                return config.swap1Enabled();
            case 1:
                return config.swap2Enabled();
            case 2:
                return config.swap3Enabled();
            case 3:
                return config.swap4Enabled();
            case 4:
                return config.swap5Enabled();
            case 5:
                return config.swap6Enabled();
            default:
                return false;
        }
    }

    public List<String> parseList(String items)
    {
        return Arrays.stream(items.split(",")).collect(Collectors.toList());
    }

    public boolean listsMatch(List<String> list1, List<String> list2)
    {
        if (list1.size() != list2.size())
        {
            return false;
        }

        List<String> list2Copy = Lists.newArrayList(list2);
        for (String element : list1)
        {
            if (!list2Copy.remove(element))
            {
                return false;
            }
        }

        return list2Copy.isEmpty();
    }

    enum GearSwapState
    {
        TICK_1, TICK_2, FINISHED
    }

    private void savePreset()
    {
        String presetName = config.presetName();
        String presetNameFormatted = presetName.replaceAll(FILENAME_SPECIAL_CHAR_REGEX, "").replaceAll(" ", "_").toLowerCase();

        if (presetNameFormatted.isEmpty())
        {
            return;
        }

        ExportableConfig exportableConfig = new ExportableConfig();

        exportableConfig.setSwap(0, config.swap1Enabled(), config.swap1String(), config.swap1Hotkey(), config.equipFirstItem1(), config.activateSpec1(), config.specThreshold1());
        exportableConfig.setSwap(1, config.swap2Enabled(), config.swap2String(), config.swap2Hotkey(), config.equipFirstItem2(), config.activateSpec2(), config.specThreshold2());
        exportableConfig.setSwap(2, config.swap3Enabled(), config.swap3String(), config.swap3Hotkey(), config.equipFirstItem3(), config.activateSpec3(), config.specThreshold3());
        exportableConfig.setSwap(3, config.swap4Enabled(), config.swap4String(), config.swap4Hotkey(), config.equipFirstItem4(), config.activateSpec4(), config.specThreshold4());
        exportableConfig.setSwap(4, config.swap5Enabled(), config.swap5String(), config.swap5Hotkey(), config.equipFirstItem5(), config.activateSpec5(), config.specThreshold5());
        exportableConfig.setSwap(5, config.swap6Enabled(), config.swap6String(), config.swap6Hotkey(), config.equipFirstItem6(), config.activateSpec6(), config.specThreshold6());

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

            for (int i = 0; i < 6; i++)
            {
                configManager.setConfiguration(GROUP_NAME, "swap" + (i + 1) + "Enabled", loadedConfig.getSwapEnabled()[i]);
                configManager.setConfiguration(GROUP_NAME, "swap" + (i + 1) + "String", loadedConfig.getSwapString()[i]);
                configManager.setConfiguration(GROUP_NAME, "swap" + (i + 1) + "Hotkey", loadedConfig.getSwapHotkey()[i]);
                configManager.setConfiguration(GROUP_NAME, "equipFirstItem" + (i + 1), loadedConfig.getEquipFirstItem()[i]);
                configManager.setConfiguration(GROUP_NAME, "activateSpec" + (i + 1), loadedConfig.getToggleSpecOnActivation()[i]);
                configManager.setConfiguration(GROUP_NAME, "specThreshold" + (i + 1), loadedConfig.getSpecThreshold()[i]);

            }

            InteractionUtils.showNonModalMessageDialog("Successfully loaded preset '" + presetNameFormatted + "'", "Preset Load Success");
        }
        catch (Exception e)
        {
            InteractionUtils.showNonModalMessageDialog(e.getMessage(), "Preset Load Error");
            log.error(e.getMessage());
        }
    }

    private boolean shouldActivateSpec()
    {
        switch (lastSwapSelected)
        {
            case 0:
                return config.activateSpec1() && (CombatUtils.getSpecEnergy() >= config.specThreshold1());
            case 1:
                return config.activateSpec2() && (CombatUtils.getSpecEnergy() >= config.specThreshold2());
            case 2:
                return config.activateSpec3() && (CombatUtils.getSpecEnergy() >= config.specThreshold3());
            case 3:
                return config.activateSpec4() && (CombatUtils.getSpecEnergy() >= config.specThreshold4());
            case 4:
                return config.activateSpec5() && (CombatUtils.getSpecEnergy() >= config.specThreshold5());
            case 5:
                return config.activateSpec6() && (CombatUtils.getSpecEnergy() >= config.specThreshold6());
            default:
                return false;
        }
    }
}