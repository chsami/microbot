package net.runelite.client.plugins.ChinBreakHandler;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Widgets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.WidgetID;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.Point;
import net.runelite.api.VarClientInt;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.ChinBreakHandler.ui.ChinBreakHandlerPanel;
import net.runelite.client.plugins.ChinBreakHandler.ui.LoginMode;
import net.runelite.client.plugins.ChinBreakHandler.util.IntRandomNumberGenerator;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Chin Break Handler</html>",
        description = "Owain's Chin Break Handler ported to RuneLite & Extended",
        tags = {"ethan", "piggy", "break", "chin"}
)
@Slf4j
public class ChinBreakHandlerPlugin extends Plugin {

    private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;
    private static final int MAX_WORLD = 580;
    private static final BufferedImage icon = ImageUtil.loadImageResource(ChinBreakHandlerPlugin.class, "chin_special.png");

    @Inject
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private ClientThread clientThread;

    @Inject
    @Getter
    private ConfigManager configManager;

    @Inject
    private ChinBreakHandler chinBreakHandler;

    @Inject
    private OptionsConfig optionsConfig;

    @Inject
    private WorldService worldService;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Provides
    public NullConfig getConfig() {
        return configManager.getConfig(NullConfig.class);
    }

    @Provides
    public OptionsConfig getOptionsConfig() {
        return configManager.getConfig(OptionsConfig.class);
    }

    public static String data;

    private NavigationButton navButton;
    private ChinBreakHandlerPanel panel;
    private boolean logout;
    private int delay = -1;

    public final Map<Plugin, Disposable> disposables = new HashMap<>();
    public Disposable activeBreaks;
    public Disposable secondsDisposable;
    public Disposable activeDisposable;
    public Disposable logoutDisposable;
    public Disposable loginDisposable;

    private State state = State.NULL;
    private ExecutorService executorService;

    private net.runelite.api.World quickHopTargetWorld;
    private int displaySwitcherAttempts = 0;
    private boolean login = false;

    private int currentPinNumber = 0;

    protected void startUp() {
        executorService = Executors.newSingleThreadExecutor();

        panel = injector.getInstance(ChinBreakHandlerPanel.class);



        navButton = NavigationButton.builder()
                .tooltip("Chin break handler")
                .icon(icon)
                .priority(4)
                .panel(panel)
                .build();
        clientToolbar.addNavigation(navButton);

        activeBreaks = chinBreakHandler
                .getCurrentActiveBreaksObservable()
                .subscribe(this::breakActivated);

        secondsDisposable = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribe(this::seconds);

        activeDisposable = chinBreakHandler
                .getActiveObservable()
                .subscribe(
                        (plugins) ->
                        {
                            if (!plugins.isEmpty()) {
                                if (!navButton.getPanel().isVisible()) {
                                    clientToolbar.openPanel(navButton);
                                }
                            }
                        }
                );

        logoutDisposable = chinBreakHandler
                .getlogoutActionObservable()
                .subscribe(
                        (plugin) ->
                        {
                            if (plugin != null) {
                                if (state != State.LOGOUT && state != State.LOGOUT_TAB && state != State.LOGOUT_BUTTON && state != State.LOGOUT_WAIT) {
                                    logout = true;
                                    state = State.LOGOUT;
                                }
                            }
                        }
                );

        loginDisposable = chinBreakHandler
                .getLoginActionObservable()
                .subscribe(
                        (plugin -> {
                            if (plugin != null) {
                                login = true;
                            }
                        })
                );
    }

    protected void shutDown() {
        executorService.shutdown();

        clientToolbar.removeNavigation(navButton);

        panel.pluginDisposable.dispose();
        panel.activeDisposable.dispose();
        panel.currentDisposable.dispose();
        panel.startDisposable.dispose();
        panel.configDisposable.dispose();

        for (Disposable disposable : disposables.values()) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }

        if (activeBreaks != null && !activeBreaks.isDisposed()) {
            activeBreaks.dispose();
        }

        if (secondsDisposable != null && !secondsDisposable.isDisposed()) {
            secondsDisposable.dispose();
        }

        if (activeDisposable != null && !activeDisposable.isDisposed()) {
            activeDisposable.dispose();
        }

        if (logoutDisposable != null && !logoutDisposable.isDisposed()) {
            logoutDisposable.dispose();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        chinBreakHandler.configChanged.onNext(configChanged);
    }

    public void scheduleBreak(Plugin plugin) {
        int from = Integer.parseInt(configManager.getConfiguration("piggyBreakHandler", sanitizedName(plugin) + "-thresholdfrom")) * 60;
        int to = Integer.parseInt(configManager.getConfiguration("piggyBreakHandler", sanitizedName(plugin) + "-thresholdto")) * 60;

        int random = new IntRandomNumberGenerator(from, to).nextInt();

        chinBreakHandler.planBreak(plugin, Instant.now().plus(random, ChronoUnit.SECONDS));
    }

    private void breakActivated(Pair<Plugin, Instant> pluginInstantPair) {
        Plugin plugin = pluginInstantPair.getKey();

        if (!chinBreakHandler.getPlugins().get(plugin) || Boolean.parseBoolean(configManager.getConfiguration("piggyBreakHandler", sanitizedName(plugin) + "-logout"))) {
            logout = true;
            state = State.LOGOUT;
        }
    }

    private void seconds(long ignored) {
        Map<Plugin, Instant> activeBreaks = chinBreakHandler.getActiveBreaks();

        if (!login && (activeBreaks.isEmpty() || client.getGameState() != GameState.LOGIN_SCREEN)) {
            return;
        }

        boolean finished = true;

        for (Instant duration : activeBreaks.values()) {
            if (Instant.now().isBefore(duration)) {
                finished = false;
            }
        }

        if (finished) {
            LoginMode loginMode = LoginMode.parse(configManager.getConfiguration("piggyBreakHandler", "accountselection"));

            String username = null;
            String password = null;

            if (loginMode.equals(LoginMode.MANUAL)) {
                username = configManager.getConfiguration("piggyBreakHandler", "accountselection-manual-username");
                password = configManager.getConfiguration("piggyBreakHandler", "accountselection-manual-password");
            } else if (loginMode.equals(LoginMode.PROFILES)) {
                String account = configManager.getConfiguration("piggyBreakHandler", "accountselection-profiles-account");

                if (data == null) {
                    return;
                }

                Optional<String> accountData = Arrays.stream(data.split("\\n"))
                        .filter(s -> s.startsWith(account))
                        .findFirst();

                if (accountData.isPresent()) {
                    String[] parts = accountData.get().split(":");
                    username = parts[1];
                    if (parts.length == 4) {
                        password = parts[2];
                    }
                }
            } else if (loginMode.equals(LoginMode.LAUNCHER)) {
                clientThread.invoke(() -> {
                    client.setGameState(GameState.LOGGING_IN);
                });
                return;
            }

            if (username != null && password != null) {
                String finalUsername = username;
                String finalPassword = password;

                clientThread.invoke(() ->
                        {
                            client.setUsername(finalUsername);
                            client.setPassword(finalPassword);

                            sendKey(KeyEvent.VK_ENTER);
                            sendKey(KeyEvent.VK_ENTER);
                            sendKey(KeyEvent.VK_ENTER);
                            client.setGameState(GameState.LOGGING_IN);
                        }
                );

            }
        }
    }

    public static String sanitizedName(Plugin plugin) {
        return plugin.getName().toLowerCase().replace(" ", "");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.CONNECTION_LOST) {
            state = State.LOGIN_SCREEN;

            if (!chinBreakHandler.getActivePlugins().isEmpty()) {
                if (optionsConfig.hopAfterBreak() && (optionsConfig.american()
                        || optionsConfig.unitedKingdom()
                        || optionsConfig.german()
                        || optionsConfig.australian())) {
                    hop();
                }
            }

            if (optionsConfig.stopAfterBreaks() != 0 && chinBreakHandler.getTotalAmountOfBreaks() >= optionsConfig.stopAfterBreaks()) {
                for (Plugin plugin : Set.copyOf(chinBreakHandler.getActivePlugins())) {
                    chinBreakHandler.stopPlugin(plugin);
                }
            }
        }
    }

    public void typeString(char c) {
        pressShitKey(c);
    }

    public void pressShitKey(char key) {
        skeyEvent(401, key);
        skeyEvent(402, key);
        skeyEvent(400, key);
    }

    private void skeyEvent(int id, char key) {
        KeyEvent e = new KeyEvent(client.getCanvas(), id, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, key);
        client.getCanvas().dispatchEvent(e);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (client.getGameState() == GameState.LOGGED_IN && optionsConfig.autoBankPin()) {
            Widget bankPinWidget = client.getWidget(213, 0);
            if (bankPinWidget != null && !bankPinWidget.isHidden()) {
                String pin = ChinBreakHandler.getBankPin(configManager);
                if (pin != null && pin.length() == 4) {
                    typeString(pin.charAt(currentPinNumber));
                    if (currentPinNumber < 3) {
                        currentPinNumber++;
                    } else {
                        currentPinNumber = 0;
                    }
                    client.setVarcIntValue(VarClientInt.BLOCK_KEYPRESS, client.getGameCycle() + 1);
                }
            } else if (bankPinWidget == null && currentPinNumber != 0) {
                currentPinNumber = 0;
            }
        }

        if (state == State.NULL && logout && delay == 0) {
            state = State.LOGOUT;
        } else if (state == State.LOGIN_SCREEN && (!chinBreakHandler.getActiveBreaks().isEmpty() || login)) {

            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 24772680, -1, -1);
            logout = false;

            Widget loginScreen = client.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN);
            Widget playButtonText = client.getWidget(WidgetID.LOGIN_CLICK_TO_PLAY_GROUP_ID, 87);


            if (playButtonText != null && playButtonText.getText().equals("CLICK HERE TO PLAY")) {
                click(playButtonText);
            } else if (loginScreen == null) {
                state = State.INVENTORY;
            }
        } else if (state == State.LOGOUT) {
            sendKey(KeyEvent.VK_ESCAPE);

            state = State.LOGOUT_TAB;
        } else if (state == State.LOGOUT_TAB) {
            // Logout tab
            if (client.getVar(VarClientInt.INVENTORY_TAB) != 10) {
                client.runScript(915, 10);
            }

            Widget logoutButton = client.getWidget(182, 8);
            Widget logoutDoorButton = client.getWidget(69, 23);
            Optional<Widget> widget = Widgets.search().withId(4522009).first();
            if (widget.isPresent()) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 4522009, -1, -1);
            } else {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 11927560, -1, -1);
            }

            if (logoutButton != null || logoutDoorButton != null) {
                state = State.LOGOUT_BUTTON;
            }
        } else if (state == State.LOGOUT_BUTTON) {
            Widget logoutButton = client.getWidget(182, 8);
            click(logoutButton);
            delay = new IntRandomNumberGenerator(20, 25).nextInt();
        } else if (state == State.INVENTORY) {
            // Inventory
            if (client.getVar(VarClientInt.INVENTORY_TAB) != 3) {
                client.runScript(915, 3);
            }
            state = State.RESUME;
        } else if (state == State.RESUME) {
            for (Plugin plugin : chinBreakHandler.getActiveBreaks().keySet()) {
                chinBreakHandler.stopBreak(plugin);
            }

            state = State.NULL;
        } else if (!chinBreakHandler.getActiveBreaks().isEmpty()) {
            Map<Plugin, Instant> activeBreaks = chinBreakHandler.getActiveBreaks();

            if (activeBreaks
                    .keySet()
                    .stream()
                    .anyMatch(e ->
                            !Boolean.parseBoolean(configManager.getConfiguration("piggyBreakHandler", sanitizedName(e) + "-logout")))) {
                if (client.getKeyboardIdleTicks() > 14900) {
                    KeyEvent keyEvent = new KeyEvent(
                            client.getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis(),
                            0, KeyEvent.VK_0, KeyEvent.CHAR_UNDEFINED
                    );
                    client.getCanvas().dispatchEvent(keyEvent);
                }
                if (client.getMouseIdleTicks() > 14900) {
                    Point point = new Point(0, 0);
                    MouseEvent mouseEvent = new MouseEvent(
                            client.getCanvas(), MouseEvent.MOUSE_MOVED,
                            System.currentTimeMillis(),
                            0, point.getX(), point.getY(),
                            0, false, 0
                    );
                    client.getCanvas().dispatchEvent(mouseEvent);
                }

                boolean finished = true;

                for (Instant duration : activeBreaks.values()) {
                    if (Instant.now().isBefore(duration)) {
                        finished = false;
                    }
                }

                if (finished) {
                    state = State.INVENTORY;
                }
            }
        }

        if (delay > 0) {
            delay--;
        }

        if (quickHopTargetWorld == null) {
            return;
        }

        if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            client.openWorldHopper();

            if (++displaySwitcherAttempts >= DISPLAY_SWITCHER_MAX_ATTEMPTS) {
                String chatMessage = new ChatMessageBuilder()
                        .append(ChatColorType.NORMAL)
                        .append("Failed to quick-hop after ")
                        .append(ChatColorType.HIGHLIGHT)
                        .append(Integer.toString(displaySwitcherAttempts))
                        .append(ChatColorType.NORMAL)
                        .append(" attempts.")
                        .build();

                chatMessageManager
                        .queue(QueuedMessage.builder()
                                .type(ChatMessageType.CONSOLE)
                                .runeLiteFormattedMessage(chatMessage)
                                .build());

                resetQuickHopper();
            }
        } else {
            client.hopToWorld(quickHopTargetWorld);
            resetQuickHopper();
        }
    }

    private void resetQuickHopper() {
        displaySwitcherAttempts = 0;
        quickHopTargetWorld = null;
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
        if (state == State.LOGIN_SCREEN) {
            Widget playButton = client.getWidget(WidgetID.LOGIN_CLICK_TO_PLAY_GROUP_ID, 78);

            if (playButton == null) {
                return;
            }

            menuAction(
                    menuOptionClicked,
                    "Play",
                    "",
                    1,
                    MenuAction.CC_OP,
                    -1,
                    playButton.getId()
            );

            state = State.INVENTORY;
        } else if (state == State.LOGOUT_BUTTON) {
            Widget logoutButton = client.getWidget(182, 8);
            Widget logoutDoorButton = client.getWidget(69, 23);
            int param1 = -1;

            if (logoutButton != null) {
                param1 = logoutButton.getId();
            } else if (logoutDoorButton != null) {
                param1 = logoutDoorButton.getId();
            }

            if (param1 == -1) {
                menuOptionClicked.consume();
                return;
            }

            menuAction(
                    menuOptionClicked,
                    "Logout",
                    "",
                    1,
                    MenuAction.CC_OP,
                    -1,
                    param1
            );

            state = State.NULL;
        }
    }

    private void click(Widget widget) {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(widget, widget.getActions()[0]);
    }

    private void click() {
        executorService.submit(() ->
        {
            Point point = new Point(0, 0);

            mouseEvent(MouseEvent.MOUSE_ENTERED, point);
            mouseEvent(MouseEvent.MOUSE_EXITED, point);
            mouseEvent(MouseEvent.MOUSE_MOVED, point);

            mouseEvent(MouseEvent.MOUSE_PRESSED, point);
            mouseEvent(MouseEvent.MOUSE_RELEASED, point);
            mouseEvent(MouseEvent.MOUSE_CLICKED, point);
        });
    }

    private void mouseEvent(int id, Point point) {
        MouseEvent mouseEvent = new MouseEvent(
                client.getCanvas(), id,
                System.currentTimeMillis(),
                0, point.getX(), point.getY(),
                1, false, 1
        );

        client.getCanvas().dispatchEvent(mouseEvent);
    }

    @SuppressWarnings("SameParameterValue")
    private void sendKey(int key) {
        keyEvent(KeyEvent.KEY_PRESSED, key);
        keyEvent(KeyEvent.KEY_RELEASED, key);
    }

    private void keyEvent(int id, int key) {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, key, KeyEvent.CHAR_UNDEFINED
        );

        client.getCanvas().dispatchEvent(e);
    }

    public boolean isValidBreak(Plugin plugin) {
        Map<Plugin, Boolean> plugins = chinBreakHandler.getPlugins();

        if (!plugins.containsKey(plugin)) {
            return false;
        }

        if (!plugins.get(plugin)) {
            return true;
        }

        String thresholdfrom = configManager.getConfiguration("piggyBreakHandler", sanitizedName(plugin) + "-thresholdfrom");
        String thresholdto = configManager.getConfiguration("piggyBreakHandler", sanitizedName(plugin) + "-thresholdto");
        String breakfrom = configManager.getConfiguration("piggyBreakHandler", sanitizedName(plugin) + "-breakfrom");
        String breakto = configManager.getConfiguration("piggyBreakHandler", sanitizedName(plugin) + "-breakto");

        return isNumeric(thresholdfrom) &&
                isNumeric(thresholdto) &&
                isNumeric(breakfrom) &&
                isNumeric(breakto) &&
                Integer.parseInt(thresholdfrom) <= Integer.parseInt(thresholdto) &&
                Integer.parseInt(breakfrom) <= Integer.parseInt(breakto);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void menuAction(MenuOptionClicked menuOptionClicked, String option, String target, int identifier, MenuAction menuAction, int param0, int param1) {
//        menuOptionClicked.setMenuOption(option);
//        menuOptionClicked.setMenuTarget(target);
//        menuOptionClicked.setId(identifier);
//        menuOptionClicked.setMenuAction(menuAction);
//        menuOptionClicked.setActionParam(param0);
//        menuOptionClicked.setWidgetId(param1);
    }

    private World findWorld(List<World> worlds, EnumSet<WorldType> currentWorldTypes, int totalLevel) {
        World world = worlds.get(new Random().nextInt(worlds.size()));

        EnumSet<WorldType> types = world.getTypes().clone();

        types.remove(WorldType.LAST_MAN_STANDING);

        if (types.contains(WorldType.SKILL_TOTAL)) {
            try {
                int totalRequirement = Integer.parseInt(world.getActivity().substring(0, world.getActivity().indexOf(" ")));

                if (totalLevel >= totalRequirement) {
                    types.remove(WorldType.SKILL_TOTAL);
                }
            } catch (NumberFormatException ex) {
                log.warn("Failed to parse total level requirement for target world", ex);
            }
        }

        if (currentWorldTypes.equals(types)) {
            int worldLocation = world.getLocation();

            if (Boolean.parseBoolean(configManager.getConfiguration("piggyBreakHandler", "american")) && worldLocation == 0) {
                return world;
            } else if (Boolean.parseBoolean(configManager.getConfiguration("piggyBreakHandler", "united-kingdom")) && worldLocation == 1) {
                return world;
            } else if (Boolean.parseBoolean(configManager.getConfiguration("piggyBreakHandler", "australian")) && worldLocation == 3) {
                return world;
            } else if (Boolean.parseBoolean(configManager.getConfiguration("piggyBreakHandler", "german")) && worldLocation == 7) {
                return world;
            }
        }

        return null;
    }

    private void hop() {
        clientThread.invoke(() -> {
            WorldResult worldResult = worldService.getWorlds();
            if (worldResult == null) {
                return;
            }

            World currentWorld = worldResult.findWorld(client.getWorld());

            if (currentWorld == null) {
                return;
            }

            EnumSet<WorldType> currentWorldTypes = currentWorld.getTypes().clone();

            currentWorldTypes.remove(WorldType.PVP);
            currentWorldTypes.remove(WorldType.HIGH_RISK);
            currentWorldTypes.remove(WorldType.BOUNTY);
            currentWorldTypes.remove(WorldType.SKILL_TOTAL);
            currentWorldTypes.remove(WorldType.LAST_MAN_STANDING);
            currentWorldTypes.remove(WorldType.QUEST_SPEEDRUNNING);
            currentWorldTypes.remove(WorldType.FRESH_START_WORLD);
            currentWorldTypes.remove(WorldType.DEADMAN);
            currentWorldTypes.remove(WorldType.BETA_WORLD);
            currentWorldTypes.remove(WorldType.NOSAVE_MODE);
            currentWorldTypes.remove(WorldType.TOURNAMENT);
            currentWorldTypes.remove(WorldType.SEASONAL);
            currentWorldTypes.remove(WorldType.PVP_ARENA);

            List<World> worlds = worldResult.getWorlds();

            String[] badWorldIds = optionsConfig.avoidWorldsNumbers().replace(", ", ",").split(",");
            List<Integer> badWorlds = new ArrayList<>();
            for (String worldIdString : badWorldIds) {
                if (!isNumeric(worldIdString)) {
                    continue;
                } else {
                    int id = Integer.valueOf(worldIdString);
                    if (id <= MAX_WORLD) {
                        badWorlds.add(id);
                    }
                }
            }

            worlds.removeIf(world -> world.getPlayers() >= optionsConfig.avoidWorldsPlayerCount() || badWorlds.contains(world.getId()));

            int totalLevel = client.getTotalLevel();

            World world;
            do {
                world = findWorld(worlds, currentWorldTypes, totalLevel);
            }
            while (world == null || world == currentWorld);

            hop(world.getId());
        });
    }

    private void hop(int worldId) {
        WorldResult worldResult = worldService.getWorlds();
        // Don't try to hop if the world doesn't exist
        World world = worldResult.findWorld(worldId);
        if (world == null) {
            return;
        }

        final net.runelite.api.World rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

        if (client.getGameState() == GameState.LOGIN_SCREEN) {
            client.changeWorld(rsWorld);
            return;
        }

        String chatMessage = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append("Hopping away from a player. New world: ")
                .append(ChatColorType.HIGHLIGHT)
                .append(Integer.toString(world.getId()))
                .append(ChatColorType.NORMAL)
                .append("..")
                .build();

        chatMessageManager
                .queue(QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());

        quickHopTargetWorld = rsWorld;
        displaySwitcherAttempts = 0;
    }

    public JFrame getFrame() {
        return (JFrame) SwingUtilities.getRoot(client.getCanvas());
    }
}