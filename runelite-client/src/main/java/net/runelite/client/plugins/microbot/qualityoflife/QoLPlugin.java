package net.runelite.client.plugins.microbot.qualityoflife;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.config.ConfigPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.qualityoflife.enums.WintertodtActions;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.AutoRunScript;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.CameraScript;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.NeverLogoutScript;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.SpecialAttackScript;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.pouch.PouchOverlay;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.pouch.PouchScript;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.wintertodt.WintertodtOverlay;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.wintertodt.WintertodtScript;
import net.runelite.client.plugins.microbot.util.antiban.FieldUtil;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.SplashScreen;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static net.runelite.client.plugins.microbot.qualityoflife.scripts.wintertodt.WintertodtScript.isInWintertodtRegion;
import static net.runelite.client.plugins.microbot.util.Global.awaitExecutionUntil;

@PluginDescriptor(
        name = PluginDescriptor.See1Duck + "QoL",
        description = "Quality of Life Plugin",
        tags = {"QoL", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class QoLPlugin extends Plugin {
    public static final List<NewMenuEntry> bankMenuEntries = new LinkedList<>();
    public static final List<NewMenuEntry> furnaceMenuEntries = new LinkedList<>();
    public static final List<NewMenuEntry> anvilMenuEntries = new LinkedList<>();
    private static final AtomicReference<List<?>> pluginList = new AtomicReference<>();
    private static final int HALF_ROTATION = 1024;
    private static final int FULL_ROTATION = 2048;
    private static final int PITCH_INDEX = 0;
    private static final int YAW_INDEX = 1;
    private static final BufferedImage SWITCHER_ON_IMG = getImageFromConfigResource("switcher_on");
    private static final BufferedImage STAR_ON_IMG = getImageFromConfigResource("star_on");
    public static String loadoutToLoad = "";
    private static GameState lastGameState = GameState.UNKNOWN;
    private final int[] deltaCamera = new int[3];
    private final int[] previousCamera = new int[3];

    public static NewMenuEntry workbenchMenuEntry;
    public static boolean recordActions = false;
    public static boolean executeBankActions = false;
    public static boolean executeFurnaceActions = false;
    public static boolean executeAnvilActions = false;
    public static boolean executeWorkbenchActions = false;
    public static boolean executeLoadoutActions = false;
    private final String BANK_OPTION = "Bank";
    private final String SMELT_OPTION = "Smelt";
    private final String SMITH_OPTION = "Smith";
    @Inject
    public ConfigManager configManager;
    @Inject
    WintertodtScript wintertodtScript;
    @Inject
    PouchScript pouchScript;
    @Inject
    private QoLConfig config;
    @Inject
    private QoLScript qoLScript;
    @Inject
    private AutoRunScript autoRunScript;
    @Inject
    private SpecialAttackScript specialAttackScript;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private QoLOverlay qoLOverlay;
    @Inject
    private PouchOverlay pouchOverlay;
    @Inject
    private WintertodtOverlay wintertodtOverlay;

    @Provides
    QoLConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(QoLConfig.class);
    }

    private static BufferedImage getImageFromConfigResource(String imgName) {
        try {
            Class<?> clazz = Class.forName("net.runelite.client.plugins.config.ConfigPanel");
            return ImageUtil.loadImageResource(clazz, imgName.concat(".png"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ImageIcon remapImage(BufferedImage image, Color color) {
        if (color != null) {
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), 2);
            Graphics2D graphics = img.createGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.setColor(color);
            graphics.setComposite(AlphaComposite.getInstance(10, 1));
            graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
            graphics.dispose();
            return new ImageIcon(img);
        } else {
            return null;
        }
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(pouchOverlay);
            overlayManager.add(qoLOverlay);
            overlayManager.add(wintertodtOverlay);
        }
        if (config.displayPouchCounter()) {
            overlayManager.add(pouchOverlay);
            pouchScript.startUp();
        }
        if (config.useSpecWeapon()) {
            Microbot.getSpecialAttackConfigs().setSpecialAttack(true);
            Microbot.getSpecialAttackConfigs().setSpecialAttackWeapon(config.specWeapon());
            Microbot.getSpecialAttackConfigs().setMinimumSpecEnergy(config.specWeapon().getEnergyRequired());
        }
        if (config.autoRun()) {
            Microbot.enableAutoRunOn = true;
        }
        if (config.autoStamina()) {
            Microbot.useStaminaPotsIfNeeded = true;
            Microbot.runEnergyThreshold = config.staminaThreshold() * 100;
        }
        autoRunScript.run(config);
        specialAttackScript.run(config);
        qoLScript.run(config);
        wintertodtScript.run(config);
        awaitExecutionUntil(() ->Microbot.getClientThread().invokeLater(this::updateUiElements), () -> !SplashScreen.isOpen(), 600);
    }

    @Override
    protected void shutDown() {
        qoLScript.shutdown();
        autoRunScript.shutdown();
        specialAttackScript.shutdown();
        overlayManager.remove(pouchOverlay);
        overlayManager.remove(qoLOverlay);
        overlayManager.remove(wintertodtOverlay);
    }

    @Subscribe(
            priority = -999
    )
    public void onProfileChanged(ProfileChanged event) {
        log.info("Profile changed");
        log.info("Updating UI elements");
        // Wait for the splash screen to close before updating the UI elements
        awaitExecutionUntil(() ->Microbot.getClientThread().invokeLater(this::updateUiElements), () -> !SplashScreen.isOpen(), 1000);

    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        ChatMessageType chatMessageType = chatMessage.getType();

        if (!Microbot.isLoggedIn()) return;
        if (isInWintertodtRegion()
                && (chatMessageType == ChatMessageType.GAMEMESSAGE || chatMessageType == ChatMessageType.SPAM)) {
            wintertodtScript.onChatMessage(chatMessage);
        }


    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!Microbot.isLoggedIn()) return;
        if (config.neverLogout()) {
            NeverLogoutScript.onGameTick(event);
        }
        if (config.useSpecWeapon()) {
            if (Microbot.getSpecialAttackConfigs().getSpecialAttackWeapon() != config.specWeapon()) {
                Microbot.getSpecialAttackConfigs().setSpecialAttack(true);
                Microbot.getSpecialAttackConfigs().setSpecialAttackWeapon(config.specWeapon());
                Microbot.getSpecialAttackConfigs().setMinimumSpecEnergy(config.specWeapon().getEnergyRequired());
            }
        }
    }

    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.UNKNOWN && lastGameState == GameState.UNKNOWN) {
            updateUiElements();
        }
        if (event.getGameState() == GameState.LOGGED_IN) {
            if (config.fixCameraPitch())
                CameraScript.fixPitch();
            if (config.fixCameraZoom())
                CameraScript.fixZoom();
        }
        lastGameState = event.getGameState();
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if ((config.resumeFletchingKindling() || config.resumeFeedingBrazier())) {
            if (event.getActor() == Microbot.getClient().getLocalPlayer()) {
                if (config.wintertodtActions() != WintertodtActions.NONE) {
                    updateWintertodtInterupted(true);
                }

            }
        }
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event) {
        pouchScript.onMenuOptionClicked(event);
        MenuEntry menuEntry = event.getMenuEntry();
        if (recordActions) {
            if (Rs2Bank.isOpen() && config.useDoLastBank()) {
                handleMenuOptionClicked(menuEntry, bankMenuEntries, "Close");
            } else if ((Rs2Widget.isProductionWidgetOpen() || Rs2Widget.isGoldCraftingWidgetOpen() || Rs2Widget.isSilverCraftingWidgetOpen()) && config.useDoLastFurnace()) {
                handleMenuOptionClicked(menuEntry, furnaceMenuEntries, "Make", "Smelt", "Craft");
            } else if (Rs2Widget.isSmithingWidgetOpen() && config.useDoLastAnvil()) {
                handleMenuOptionClicked(menuEntry, anvilMenuEntries, "Smith");
            }
        }

        if ("Track".equals(event.getMenuOption())) {
            event.consume();
        }

        if ((config.resumeFletchingKindling() || config.resumeFeedingBrazier()) && isInWintertodtRegion()) {
            if (event.getMenuOption().contains("Fletch") && event.getMenuTarget().isEmpty() && config.resumeFletchingKindling()) {
                WintertodtActions action = WintertodtActions.FLETCH;
                action.setMenuEntry(createCachedMenuEntry(menuEntry));
                updateLastWinthertodtAction(action);
                updateWintertodtInterupted(false);
                Microbot.log("Setting action to Fletch Kindle");
            }
            if (event.getMenuOption().contains("Feed") && config.resumeFeedingBrazier()) {
                WintertodtActions action = WintertodtActions.FEED;
                action.setMenuEntry(createCachedMenuEntry(menuEntry));
                updateLastWinthertodtAction(action);
                updateWintertodtInterupted(false);
            }
            if (event.getMenuOption().contains("Chop") || event.getMenuOption().contains("Walk")) {
                updateLastWinthertodtAction(WintertodtActions.NONE);
                updateWintertodtInterupted(false);
            }
        }
    }

    private void handleMenuOptionClicked(MenuEntry menuEntry, List<NewMenuEntry> menuEntriesList, String... stopOptions) {
        NewMenuEntry cachedMenuEntry = createCachedMenuEntry(menuEntry);
        menuEntriesList.add(cachedMenuEntry);
        if (Arrays.stream(stopOptions).anyMatch(menuEntry.getOption()::contains)) {
            recordActions = false;
            Microbot.log("<col=5F1515>Stopped recording actions</col>");
        }
    }

    private NewMenuEntry createCachedMenuEntry(MenuEntry menuEntry) {
        NewMenuEntry cachedMenuEntry = new NewMenuEntry(
                menuEntry.getOption(),
                menuEntry.getTarget(),
                menuEntry.getIdentifier(),
                menuEntry.getType(),
                menuEntry.getParam0(),
                menuEntry.getParam1(),
                menuEntry.isForceLeftClick()
        );
        cachedMenuEntry.setItemId(menuEntry.getItemId());
        cachedMenuEntry.setWidget(menuEntry.getWidget());
        return cachedMenuEntry;
    }

    @Subscribe
    private void onNpcChanged(NpcChanged event) {
        if (isInWintertodtRegion()) {
            wintertodtScript.onNpcChanged(event);
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event) {
        if (isInWintertodtRegion()) {
            wintertodtScript.onNpcDespawned(event);
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event) {
        if (isInWintertodtRegion()) {
            wintertodtScript.onNpcSpawned(event);
        }
    }

    @Subscribe
    private void onMenuEntryAdded(MenuEntryAdded event) {
        String option = event.getOption();
        String target = event.getTarget();
        MenuEntry menuEntry = event.getMenuEntry();
        boolean bankChestCheck = "Bank".equals(option) || ("Use".equals(option) && target.contains("Bank chest"));

        if (config.quickFletchKindling() && isInWintertodtRegion() && event.getItemId() == ItemID.KNIFE && "Use".equals(option)) {
            menuEntry.setOption("<col=FFA500>Fletch Kindle</col>");
            menuEntry.setTarget("");
            menuEntry.onClick(this::fletchBrumaRootsOnClicked);

        }

        if (config.rightClickCameraTracking() && menuEntry.getNpc() != null && menuEntry.getNpc().getId() > 0) {
            addMenuEntry(event, "Track", target, this::customTrackOnClicked);
        }


        if (config.useDoLastBank()) {
            if ("Talk-to".equals(option)) {
                for (MenuEntry e : Microbot.getClient().getMenuEntries()) {
                    if ("Bank".equals(e.getOption()) && e.getTarget().equals(target)) {
                        menuEntry.setDeprioritized(true);
                        break;
                    }
                }
            }
            if (bankChestCheck && event.getItemId() == -1) {
                menuEntry.onClick(this::recordNewActions);
                addMenuEntry(event, "<col=FFA500>Do-Last</col>", target, this::customBankingOnClicked);
            }
        }

        if (config.useDoLastFurnace() && "Smelt".equals(option) && target.contains("Furnace")) {
            menuEntry.onClick(this::recordNewActions);
            addMenuEntry(event, "<col=FFA500>Do-Last</col>", target, this::customFurnaceOnClicked);
        }

        if (config.useDoLastAnvil() && "Smith".equals(option) && target.contains("Anvil")) {
            menuEntry.onClick(this::recordNewActions);
            addMenuEntry(event, "<col=FFA500>Do-Last</col>", target, this::customAnvilOnClicked);
        }

        if (config.useDoLastWorkbench() && "Work-at".equals(option)) {
            menuEntry.onClick(this::customWorkbenchOnClicked);
        }

        if (config.displayInventorySetups() && bankChestCheck && event.getItemId() == -1) {
            addLoadoutMenuEntries(event, target);
        }
    }

    private void addLoadoutMenuEntries(MenuEntryAdded event, String target) {
        if (config.displaySetup1()) {
            addLoadoutMenuEntry(event, "<col=FFA500>Equip: " + config.Setup1() + "</col>", target, e -> customLoadoutOnClicked(e, config.Setup1()));
        }
        if (config.displaySetup2()) {
            addLoadoutMenuEntry(event, "<col=FFA500>Equip: " + config.Setup2() + "</col>", target, e -> customLoadoutOnClicked(e, config.Setup2()));
        }
        if (config.displaySetup3()) {
            addLoadoutMenuEntry(event, "<col=FFA500>Equip: " + config.Setup3() + "</col>", target, e -> customLoadoutOnClicked(e, config.Setup3()));
        }
        if (config.displaySetup4()) {
            addLoadoutMenuEntry(event, "<col=FFA500>Equip: " + config.Setup4() + "</col>", target, e -> customLoadoutOnClicked(e, config.Setup4()));
        }
    }


    @Subscribe
    public void onConfigChanged(ConfigChanged ev) {
        if ("smoothRotation".equals(ev.getKey()) && config.smoothCameraTracking() && Microbot.isLoggedIn()) {
            previousCamera[YAW_INDEX] = Microbot.getClient().getMapAngle();
        }
        if (ev.getKey().equals("accentColor") || ev.getKey().equals("toggleButtonColor") || ev.getKey().equals("pluginLabelColor")) {
            updateUiElements();
        }
        if (ev.getKey().equals("resumeFletchingKindling")) {
            if (config.resumeFletchingKindling()) {
                configManager.setConfiguration("QoL", "quickFletchKindling", true);
            }
        }
        if (ev.getKey().equals("displayPouchCounter")) {
            if (ev.getNewValue() == "true") {
                overlayManager.add(pouchOverlay);
            } else {
                overlayManager.remove(pouchOverlay);
            }
        }
        if (ev.getKey().equals("useSpecWeapon") || ev.getKey().equals("specWeapon")) {
            if (config.useSpecWeapon()) {
                Microbot.getSpecialAttackConfigs().setSpecialAttack(true);
                Microbot.getSpecialAttackConfigs().setSpecialAttackWeapon(config.specWeapon());
                Microbot.getSpecialAttackConfigs().setMinimumSpecEnergy(config.specWeapon().getEnergyRequired());
            } else {
                Microbot.getSpecialAttackConfigs().reset();
            }
        }

        if (ev.getKey().equals("autoRun")) {
            Microbot.enableAutoRunOn = config.autoRun();
        }
        if (ev.getKey().equals("autoStamina")) {
            Microbot.useStaminaPotsIfNeeded = config.autoStamina();
        }
    }

    @Subscribe
    public void onBeforeRender(BeforeRender render) {
        if (!Microbot.isLoggedIn() || !config.smoothCameraTracking()) {
            return;
        }
        applySmoothingToAngle(YAW_INDEX);
    }

    @Subscribe
    public void onItemContainerChanged(final ItemContainerChanged event)
    {
        pouchScript.onItemContainerChanged(event);
    }

    private void customLoadoutOnClicked(MenuEntry event, String loadoutName) {
        recordActions = false;
        loadoutToLoad = loadoutName;
        executeLoadoutActions = true;
    }

    private void customBankingOnClicked(MenuEntry event) {
        recordActions = false;
        if (bankMenuEntries.isEmpty()) {
            Microbot.log("<col=5F1515>No actions recorded</col>");
            return;
        }
        Microbot.log("<col=245C2D>Banking</col>");
        executeBankActions = true;
    }

    private void customTrackOnClicked(MenuEntry event) {
        if (Rs2Camera.isTrackingNpc()) {
            Rs2Camera.stopTrackingNpc();
            Microbot.log("<col=5F1515>Stopped tracking old NPC, try again to track new NPC</col>");
            return;
        }
        Rs2Camera.trackNpc(Objects.requireNonNull(event.getNpc()).getId());
    }

    private void customFurnaceOnClicked(MenuEntry event) {
        recordActions = false;
        if (furnaceMenuEntries.isEmpty()) {
            Microbot.log("<col=5F1515>No actions recorded</col>");
            return;
        }
        Microbot.log("<col=245C2D>Furnace</col>");
        executeFurnaceActions = true;
    }

    private void customAnvilOnClicked(MenuEntry event) {
        recordActions = false;
        if (anvilMenuEntries.isEmpty()) {
            Microbot.log("<col=5F1515>No actions recorded</col>");
            return;
        }
        Microbot.log("<col=245C2D>Anvil</col>");
        executeAnvilActions = true;
    }

    private void customWorkbenchOnClicked(MenuEntry event) {
        Microbot.log("<col=245C2D>Workbench</col>");
        executeWorkbenchActions = true;
    }

    private void recordNewActions(MenuEntry event) {
        recordActions = true;
        String option = event.getOption();
        if (BANK_OPTION.equals(option) || "Use".equals(option) && event.getTarget().contains("Bank chest")){
            bankMenuEntries.clear();
        } else if (SMELT_OPTION.equals(option)) {
            furnaceMenuEntries.clear();
        } else if (SMITH_OPTION.equals(option)) {
            anvilMenuEntries.clear();
        }
        Microbot.log("<col=245C2D>Recording actions for: </col>" + option);
    }

    private void fletchBrumaRootsOnClicked(MenuEntry event) {
        int brumaRootSlot = Rs2Inventory.slot(ItemID.BRUMA_ROOT);
        if (brumaRootSlot == -1) {
            Microbot.log("<col=5F1515>Bruma root not found in inventory</col>");
            return;
        }
        Microbot.log("<col=245C2D>Fletching Kindling</col>");
        NewMenuEntry combinedMenuEntry = new NewMenuEntry("Fletch", "Bruma root", 0, MenuAction.WIDGET_TARGET_ON_WIDGET, brumaRootSlot, event.getParam1(), false);
        combinedMenuEntry.setItemId(ItemID.BRUMA_ROOT);
        Microbot.doInvoke(combinedMenuEntry, new Rectangle(1, 1));
    }

    private void applySmoothingToAngle(int index) {
        int currentAngle = index == YAW_INDEX ? Microbot.getClient().getMapAngle() : 0;
        int newDeltaAngle = getSmallestAngle(previousCamera[index], currentAngle);
        deltaCamera[index] += newDeltaAngle;

        int deltaChange = lerp(deltaCamera[index], 0, 0.8f);
        int changed = previousCamera[index] + deltaChange;

        deltaCamera[index] -= deltaChange;
        if (index == YAW_INDEX) {
            Microbot.getClient().setCameraYawTarget(changed);
        }
        previousCamera[index] += deltaChange;
    }

    private int lerp(int x, int y, double alpha) {
        return x + (int) Math.round((y - x) * alpha);
    }

    private int getSmallestAngle(int x, int y) {
        return ((y - x + HALF_ROTATION) % FULL_ROTATION) - HALF_ROTATION;
    }

    private void addMenuEntry(MenuEntryAdded event, String option, String target, Consumer<MenuEntry> callback) {
        int index = Microbot.getClient().getMenuEntries().length;
        Microbot.getClient().createMenuEntry(index)
                .setOption(option)
                .setTarget(target)
                .setParam0(event.getActionParam0())
                .setParam1(event.getActionParam1())
                .setIdentifier(event.getIdentifier())
                .setType(event.getMenuEntry().getType())
                .onClick(callback);
    }

    private void addLoadoutMenuEntry(MenuEntryAdded event, String option, String target, Consumer<MenuEntry> callback) {
        int index = Microbot.getClient().getMenuEntries().length - 1;
        Microbot.getClient().createMenuEntry(index)
                .setOption(option)
                .setTarget(target)
                .setParam0(event.getActionParam0())
                .setParam1(event.getActionParam1())
                .setIdentifier(event.getIdentifier())
                .setType(event.getMenuEntry().getType())
                .onClick(callback);
    }

    public void updateLastWinthertodtAction(WintertodtActions action) {
        configManager.setConfiguration("QoL", "wintertodtActions", action);
    }

    public void updateWintertodtInterupted(boolean interupted) {
        configManager.setConfiguration("QoL", "interrupted", interupted);
    }


    /**
     * Updates the UI elements by modifying various fields and components based on the provided configuration.
     *
     * This method updates the accent color, the plugin toggle button's ON_SWITCHER field, and modifies the color of labels
     * and toggle buttons for plugins in the plugin list. If any part of the process fails (e.g., the ConfigPlugin is not found),
     * it logs an appropriate error message and returns false.
     *
     * @return true if the UI elements are successfully updated, false otherwise.
     */
    private boolean updateUiElements() {
        try {
            // Get the Field object for the accent color (BRAND_ORANGE) in the ColorScheme class
            Field accentColorField = ColorScheme.class.getDeclaredField("BRAND_ORANGE");
            // Update the accent color with the value from the config
            FieldUtil.setFinalStatic(accentColorField, config.accentColor());

            // Get the PluginToggleButton class to access its ON_SWITCHER field
            Class<?> pluginButton = Class.forName("net.runelite.client.plugins.config.PluginToggleButton");
            Field onSwitcherPluginPanel = pluginButton.getDeclaredField("ON_SWITCHER");
            onSwitcherPluginPanel.setAccessible(true);
            // Update the ON_SWITCHER field with a remapped image based on the config toggle button color
            FieldUtil.setFinalStatic(onSwitcherPluginPanel, remapImage(SWITCHER_ON_IMG, config.toggleButtonColor()));

            // Find the ConfigPlugin instance from the plugin manager
            ConfigPlugin configPlugin = (ConfigPlugin) Microbot.getPluginManager().getPlugins().stream()
                    .filter(plugin -> plugin instanceof ConfigPlugin)
                    .findAny().orElse(null);

            // If ConfigPlugin is not found, log an error and return false
            if (configPlugin == null) {
                Microbot.log("Config Plugin not found");
                return false;
            }

            // Get the plugin list panel from the ConfigPlugin instance
            JPanel pluginListPanel = getPluginListPanel(configPlugin);
            // Set the plugin list using the retrieved plugin list panel
            pluginList.set(getPluginList(pluginListPanel));

            // If the plugin list is still null, log an error and return false
            if (pluginList.get() == null) {
                Microbot.log("Plugin list is null, waiting for it to be initialized");
                return false;
            }

            // Iterate through each plugin in the plugin list
            for (Object plugin : pluginList.get()) {
                // If the plugin is a JPanel, update the color of any JLabel components within it
                if (plugin instanceof JPanel) {
                    for (Component component : ((JPanel) plugin).getComponents()) {
                        if (component instanceof JLabel) {
                            // Set the label color based on the config
                            component.setForeground(config.pluginLabelColor());
                        }
                    }
                }

                // Get the on/off toggle button for the plugin and update its selected icon
                JToggleButton onOffToggle = (JToggleButton) FieldUtils.readDeclaredField(plugin, "onOffToggle", true);
                onOffToggle.setSelectedIcon(remapImage(SWITCHER_ON_IMG, config.toggleButtonColor()));
            }

            return true;
        } catch (Exception e) {
            // Log any exceptions that occur during the UI update process
            String errorMessage = "QoL Error updating UI elements: " + e.getMessage();
            log.error(errorMessage);
            Microbot.log(errorMessage);
            return false;
        }
    }

    private JPanel getPluginListPanel(ConfigPlugin configPlugin) throws ClassNotFoundException {

        Class<?> pluginListPanelClass = Class.forName("net.runelite.client.plugins.config.PluginListPanel");
        assert configPlugin != null;
        return (JPanel) configPlugin.getInjector().getProvider(pluginListPanelClass).get();
    }

    private List<?> getPluginList(JPanel pluginListPanel) throws IllegalAccessException {
        return (List<?>) FieldUtils.readDeclaredField(pluginListPanel, "pluginList", true);
    }

}
