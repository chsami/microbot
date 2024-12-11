package net.runelite.client.plugins.microbot;

import com.google.inject.Injector;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.RuneLite;
import net.runelite.client.RuneLiteDebug;
import net.runelite.client.RuneLiteProperties;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.ProfileManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.loottracker.LootTrackerPlugin;
import net.runelite.client.plugins.loottracker.LootTrackerRecord;
import net.runelite.client.plugins.microbot.configs.SpecialAttackConfigs;
import net.runelite.client.plugins.microbot.dashboard.PluginRequestModel;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.pouch.PouchScript;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.misc.Rs2UiHelper;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.NaturalMouse;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.*;

public class Microbot {
    //Version path used to load the client faster when developing by checking version number
    //If the version is the same as the current version we do not download the latest .jar
    //Resulting in a faster startup
    private static final String VERSION_FILE_PATH = "debug_temp_version.txt";
    private static final ScheduledExecutorService xpSchedulor = Executors.newSingleThreadScheduledExecutor();
    @Getter
    private static final SpecialAttackConfigs specialAttackConfigs = new SpecialAttackConfigs();
    public static MenuEntry targetMenu;
    public static boolean debug = false;
    public static boolean isGainingExp = false;
    public static boolean pauseAllScripts = false;
    public static String status = "IDLE";
    public static boolean enableAutoRunOn = true;
    public static boolean useStaminaPotsIfNeeded = true;
    public static int runEnergyThreshold = 1000;
    @Getter
    @Setter
    public static NaturalMouse naturalMouse;
    @Getter
    @Setter
    private static Mouse mouse;
    @Getter
    @Setter
    private static Client client;
    @Getter
    @Setter
    private static ClientThread clientThread;
    @Getter
    @Setter
    private static WorldMapPointManager worldMapPointManager;
    @Getter
    @Setter
    private static SpriteManager spriteManager;
    @Getter
    @Setter
    private static ItemManager itemManager;
    @Getter
    @Setter
    private static Notifier notifier;
    @Getter
    @Setter
    private static NPCManager npcManager;
    @Getter
    @Setter
    private static ProfileManager profileManager;
    @Getter
    @Setter
    private static ConfigManager configManager;
    @Getter
    @Setter
    private static WorldService worldService;
    @Getter
    @Setter
    private static boolean disableWalkerUpdate;
    @Getter
    @Setter
    private static List<PluginRequestModel> botPlugins = new ArrayList<>();
    @Getter
    @Setter
    private static PluginManager pluginManager;
    @Getter
    @Setter
    private static WorldMapOverlay worldMapOverlay;
    @Getter
    @Setter
    private static InfoBoxManager infoBoxManager;
    @Getter
    @Setter
    private static ChatMessageManager chatMessageManager;
    private static ScheduledFuture<?> xpSchedulorFuture;
    private static net.runelite.api.World quickHopTargetWorld;
    /**
     * Pouchscript is injected in the main MicrobotPlugin as it's being used in multiple scripts
     */
    @Getter
    @Setter
    @Inject
    private static PouchScript pouchScript;
    public static boolean isCantReachTargetDetectionEnabled = false;

    public static boolean cantReachTarget = false;
    public static boolean cantHopWorld = false;

    public static int cantReachTargetRetries = 0;

    @Getter
    public static HashMap<String, Integer> scriptRuntimes = new HashMap<>();

    public static boolean isDebug() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().contains("-agentlib:jdwp");
    }

    public static int getVarbitValue(int varbit) {
        return getClientThread().runOnClientThread(() -> getClient().getVarbitValue(varbit));
    }

    public static int getVarbitPlayerValue(int varbit) {
        return getClientThread().runOnClientThread(() -> getClient().getVarpValue(varbit));
    }

    public static EnumComposition getEnum(int id) {
        return getClientThread().runOnClientThread(() -> getClient().getEnum(id));
    }

    public static StructComposition getStructComposition(int structId) {
        return getClientThread().runOnClientThread(() -> getClient().getStructComposition(structId));
    }

    public static void setIsGainingExp(boolean value) {
        isGainingExp = value;
        scheduleIsGainingExp();
    }

    public static void scheduleIsGainingExp() {
        if (xpSchedulorFuture != null && !xpSchedulorFuture.isDone())
            xpSchedulorFuture.cancel(true);
        xpSchedulorFuture = xpSchedulor.schedule(() -> {
            isGainingExp = false;
        }, 4000, TimeUnit.MILLISECONDS);
    }

    public static boolean isLoggedIn() {
        if (client == null) return false;
        GameState idx = client.getGameState();
        return idx == GameState.LOGGED_IN;
    }

    public static boolean isHopping() {
        if (client == null) return false;
        GameState idx = client.getGameState();
        return idx == GameState.HOPPING;
    }

    public static boolean hopToWorld(int worldNumber) {
        if (!Microbot.isLoggedIn()) return false;
        if (Microbot.isHopping()) return true;
        if (Microbot.cantHopWorld) return false;
        boolean isHopping = Microbot.getClientThread().runOnClientThread(() -> {
            if (Microbot.getClient().getLocalPlayer() != null && Microbot.getClient().getLocalPlayer().isInteracting())
                return false;
            if (quickHopTargetWorld != null || Microbot.getClient().getGameState() != GameState.LOGGED_IN) return false;
            if (Microbot.getClient().getWorld() == worldNumber) {
                return false;
            }
            World newWorld = Microbot.getWorldService().getWorlds().findWorld(worldNumber);
            if (newWorld == null) {
                Microbot.getNotifier().notify("Invalid World");
                System.out.println("Tried to hop to an invalid world");
                return false;
            }
            final net.runelite.api.World rsWorld = Microbot.getClient().createWorld();
            quickHopTargetWorld = rsWorld;
            rsWorld.setActivity(newWorld.getActivity());
            rsWorld.setAddress(newWorld.getAddress());
            rsWorld.setId(newWorld.getId());
            rsWorld.setPlayerCount(newWorld.getPlayers());
            rsWorld.setLocation(newWorld.getLocation());
            rsWorld.setTypes(WorldUtil.toWorldTypes(newWorld.getTypes()));
            if (rsWorld == null) {
                return false;
            }
            Microbot.getClient().openWorldHopper();
            Microbot.getClient().hopToWorld(rsWorld);
            quickHopTargetWorld = null;
            sleep(600);
            sleepUntil(() -> Microbot.isHopping() || Rs2Widget.getWidget(193, 0) != null, 2000);
            return Microbot.isHopping();
        });
        if (!isHopping && Rs2Widget.getWidget(193, 0) != null) {
            List<Widget> areYouSureToSwitchWorldWidget = Arrays.stream(Rs2Widget.getWidget(193, 0).getDynamicChildren()).collect(Collectors.toList());
            Widget switchWorldWidget = sleepUntilNotNull(() -> Rs2Widget.findWidget("Switch world", areYouSureToSwitchWorldWidget, true), 2000);
            return Rs2Widget.clickWidget(switchWorldWidget);
        }
        return false;
    }

    public static void showMessage(String message) {
        try {
            SwingUtilities.invokeAndWait(() ->
            {
                JOptionPane.showConfirmDialog(null, message, "Message",
                        JOptionPane.DEFAULT_OPTION);
            });
        } catch(Exception ex) {
            ex.getStackTrace();
            Microbot.log(ex.getMessage());
        }
    }


    public static List<Rs2Item> updateItemContainer(int id, ItemContainerChanged e) {
        if (e.getContainerId() == id) {
            List<Rs2Item> list = new ArrayList<>();
            int i = -1;
            for (Item item : e.getItemContainer().getItems()) {
                if (item == null) {
                    i++;
                    continue;
                }
                i++; //increment before checking if it is a placeholder. This way the index will match the slots in the bank
                ItemComposition composition = Microbot.getItemManager().getItemComposition(item.getId());
                boolean isPlaceholder = composition.getPlaceholderTemplateId() > 0;
                if (isPlaceholder) continue;

                list.add(new Rs2Item(item, composition, i));
            }
            return list;
        }
        return null;
    }

    public static void startPlugin(Plugin plugin) {
        if (plugin == null) return;
        Microbot.getPluginManager().setPluginEnabled(plugin, true);
        Microbot.getPluginManager().startPlugins();


    }

    public static void doInvoke(NewMenuEntry entry, Rectangle rectangle) {

        try {
            if (Rs2UiHelper.isRectangleWithinCanvas(rectangle)) {
                click(rectangle, entry);
            } else {
                click(new Rectangle(1, 1), entry);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            // Handle the error as needed
        }
    }

    public static void drag(Rectangle start, Rectangle end) {
        if (start == null || end == null) return;
        if (!Rs2UiHelper.isRectangleWithinCanvas(start) || !Rs2UiHelper.isRectangleWithinCanvas(end)) return;
        Point startPoint = Rs2UiHelper.getClickingPoint(start, true);
        Point endPoint = Rs2UiHelper.getClickingPoint(end, true);
        mouse.drag(startPoint, endPoint);
        if (!Microbot.getClient().isClientThread()) {
            sleep(50, 80);
        }
    }

    public static void click(Rectangle rectangle, NewMenuEntry entry) {
        if (entry.getType() == MenuAction.WALK) {
            mouse.click(new Point(entry.getParam0(), entry.getParam1()), entry);
        } else {
            Point point = Rs2UiHelper.getClickingPoint(rectangle, true);
            mouse.click(point, entry);
        }

        if (!Microbot.getClient().isClientThread()) {
            sleep(50, 100);
        }
    }

    public static void click(Rectangle rectangle) {

        Point point = Rs2UiHelper.getClickingPoint(rectangle, true);
        mouse.click(point);


        if (!Microbot.getClient().isClientThread()) {
            sleep(50, 80);
        }
    }

    public static List<LootTrackerRecord> getAggregateLootRecords() {
        return LootTrackerPlugin.panel.aggregateRecords;
    }

    public static LootTrackerRecord getAggregateLootRecords(String npcName) {
        return getAggregateLootRecords()
                .stream()
                .filter(x -> x.getTitle().equalsIgnoreCase(npcName))
                .findFirst()
                .orElse(null);
    }

    public static void log(String message) {
        if (!Microbot.isLoggedIn()) {
            System.out.println(message);
            return;
        }
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
        Microbot.getClientThread().runOnClientThread(() ->
                Microbot.getClient().addChatMessage(ChatMessageType.ENGINE, "", "[" + formattedTime + "]: " + message, "", false)
        );
    }

    private static boolean isPluginEnabled(String name) {
        Plugin dashboard = Microbot.getPluginManager().getPlugins().stream()
                .filter(x -> x.getClass().getName().equals(name))
                .findFirst()
                .orElse(null);

        if (dashboard == null) return false;

        return Microbot.getPluginManager().isPluginEnabled(dashboard);
    }

    public static boolean isPluginEnabled(Class c) {
        return isPluginEnabled(c.getName());
    }

    @Deprecated(since = "1.6.2 - Use Rs2Player variant")
    public static QuestState getQuestState(Quest quest) {
        return getClientThread().runOnClientThread(() -> quest.getState(client));
    }

    public static void writeVersionToFile(String version) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VERSION_FILE_PATH))) {
            writer.write(version);
        }
    }

    public static boolean isFirstRun() {
        File file = new File(VERSION_FILE_PATH);
        // Check if the version file exists
        return !file.exists();
    }

    public static String readVersionFromFile() throws IOException {
        try (Scanner scanner = new Scanner(new File(VERSION_FILE_PATH))) {
            return scanner.hasNextLine() ? scanner.nextLine() : "";
        }
    }

     public static boolean shouldSkipVanillaClientDownload() {
         if (isDebug()) {
             try {
                 String currentVersion = RuneLiteProperties.getVersion();
                 if (Microbot.isFirstRun()) {
                     Microbot.writeVersionToFile(currentVersion);
                     System.out.println("First run in debug mode. Version written to file.");
                 } else {
                     String storedVersion = Microbot.readVersionFromFile();
                     if (currentVersion.equals(storedVersion)) {
                         System.out.println("Running in debug mode. Version matches stored version.");
                         return true;
                     } else {
                         System.out.println("Version mismatch detected...updating client.");
                         Microbot.writeVersionToFile(currentVersion);
                     }
                 }
             } catch(Exception ex) {
                 ex.printStackTrace();
                 System.out.println(ex.getMessage());
             }
         }
         return false;
     }

     public static Injector getInjector() {
        if (RuneLiteDebug.getInjector() != null) {
            return RuneLiteDebug.getInjector();
        }
        return RuneLite.getInjector();
     }

    /**
     * Retrieves a list of active plugins that are part of the "microbot" package, excluding specific plugins.
     *
     * This method filters the active plugins managed by the `pluginManager` to include only those whose
     * package name contains "microbot" (case-insensitive). It further excludes certain plugins based on
     * their class names, such as "QuestHelperPlugin", "MInventorySetupsPlugin", "MicrobotPlugin",
     * "MicrobotConfigPlugin", "ShortestPathPlugin", "AntibanPlugin", and "ExamplePlugin".
     *
     * @return a list of active plugins belonging to the "microbot" package, excluding the specified plugins.
     */
     public static List<Plugin> getActiveMicrobotPlugins() {
         return pluginManager.getActivePlugins().stream()
                 .filter(x -> x.getClass().getPackage().getName().toLowerCase().contains("microbot"))
                 .filter(x -> !x.getClass().getSimpleName().equalsIgnoreCase("QuestHelperPlugin")
                         && !x.getClass().getSimpleName().equalsIgnoreCase("MInventorySetupsPlugin")
                         && !x.getClass().getSimpleName().equalsIgnoreCase("MicrobotPlugin")
                         && !x.getClass().getSimpleName().equalsIgnoreCase("MicrobotConfigPlugin")
                         && !x.getClass().getSimpleName().equalsIgnoreCase("ShortestPathPlugin")
                         && !x.getClass().getSimpleName().equalsIgnoreCase("AntibanPlugin")
                         && !x.getClass().getSimpleName().equalsIgnoreCase("ExamplePlugin"))
                 .collect(Collectors.toList());
     }

    /**
     * Retrieves a list of active `Script` instances from the currently active microbot plugins.
     *
     * This method iterates through all active microbot plugins and inspects their fields using reflection
     * to find fields of type `Script` or its subclasses. The identified `Script` fields are extracted
     * and returned as a list.
     *
     * Key Details:
     * - The method uses reflection to access the fields of each plugin class.
     * - Only fields that are assignable to the `Script` type are included.
     * - Private fields are made accessible via `field.setAccessible(true)` to retrieve their values.
     * - Any exceptions encountered during field access (e.g., `IllegalAccessException`) are logged, and
     *   the corresponding field is skipped.
     * - Null values resulting from inaccessible or uninitialized fields are filtered out.
     *
     * @return a list of active `Script` instances extracted from the microbot plugins.
     */
    public static List<Script> getActiveScripts() {
        return getActiveMicrobotPlugins().stream()
                .flatMap(x -> {
                    // Get all fields of the class
                    Field[] fields = x.getClass().getDeclaredFields();

                    // Filter fields that are assignable to Script
                    return java.util.Arrays.stream(fields)
                            .filter(field -> Script.class.isAssignableFrom(field.getType()))
                            .map(field -> {
                                field.setAccessible(true); // Allow access to private fields
                                try {
                                    return (Script) field.get(x); // Map the field to a Script instance
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    return null; // Handle exception if field cannot be accessed
                                }
                            });
                })
                .filter(Objects::nonNull) // Exclude nulls
                .collect(Collectors.toList());
    }
}

