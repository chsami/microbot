package net.runelite.client.plugins.microbot;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
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
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;
import net.runelite.client.plugins.timers.GameTimer;
import net.runelite.client.plugins.timers.TimersPlugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class Microbot {
    public static MenuEntry targetMenu;
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

    public static boolean debug = false;

    public static boolean isGainingExp = false;
    public static boolean pauseAllScripts = false;
    public static String status = "IDLE";

    public static boolean enableAutoRunOn = true;

    private static final ScheduledExecutorService xpSchedulor = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> xpSchedulorFuture;
    private static net.runelite.api.World quickHopTargetWorld;
    @Getter
    private static final SpecialAttackConfigs specialAttackConfigs = new SpecialAttackConfigs();

    @Deprecated(since = "Use isMoving", forRemoval = true)
    public static boolean isWalking() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().getPoseAnimation()
                != Microbot.getClient().getLocalPlayer().getIdlePoseAnimation());
    }

    @Deprecated(since = "1.2.4 - use Rs2Player variant", forRemoval = true)
    public static boolean isMoving() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().getPoseAnimation()
                != Microbot.getClient().getLocalPlayer().getIdlePoseAnimation());
    }

    @Deprecated(since = "1.2.4 - use Rs2Player variant", forRemoval = true)
    public static boolean isAnimating() {
        return Microbot.getClientThread().runOnClientThread(() -> getClient().getLocalPlayer().getAnimation() != -1);
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
    
    @Deprecated(since = "1.4.0 - use Rs2Player variant", forRemoval = true)
    public static boolean hasLevel(int levelRequired, Skill skill) {
        return Microbot.getClient().getRealSkillLevel(skill) >= levelRequired;
    }

    public static boolean hopToWorld(int worldNumber) {
        return Microbot.getClientThread().runOnClientThread(() -> {
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
            return true;
        });
    }

    public static void showMessage(String message) {
        Microbot.getClientThread().runOnSeperateThread(() -> {
            SwingUtilities.invokeAndWait(() ->
            {
                JOptionPane.showConfirmDialog(null, message, "Message",
                        JOptionPane.DEFAULT_OPTION);
            });
            return null;
        });
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

    }

    public static Point calculateClickingPoint(Rectangle rect) {
        if (rect.getX() == 1 && rect.getY() == 1) return new Point(1, 1);
        int x = (int) (rect.getX() + (double) Random.random((int) rect.getWidth() / 6 * -1, (int) rect.getWidth() / 6) + rect.getWidth() / 2.0);
        int y = (int) (rect.getY() + (double) Random.random((int) rect.getHeight() / 6 * -1, (int) rect.getHeight() / 6) + rect.getHeight() / 2.0);
        return new Point(x, y);
    }

    public static void doInvoke(MenuEntry entry, Rectangle rectangle) {
        targetMenu = entry;
        int viewportHeight = client.getViewportHeight();
        int viewportWidth = client.getViewportWidth();
        if (!(rectangle.getX() > (double) viewportWidth) && !(rectangle.getY() > (double) viewportHeight) && !(rectangle.getX() < 0.0) && !(rectangle.getY() < 0.0)) {
            click(rectangle);
        } else {
            click(new Rectangle(1, 1));
        }
    }

    public static void click(Rectangle rectangle) {

        Point point = calculateClickingPoint(rectangle);
        if (client.isStretchedEnabled()) {
            Dimension stretched = client.getStretchedDimensions();
            Dimension real = client.getRealDimensions();
            double width = (double) stretched.width / real.getWidth();
            double height = (double) stretched.height / real.getHeight();
            point = new Point((int) ((double) point.getX() * width), (int) ((double) point.getY() * height));
        }

        mouseEvent(504, point);
        mouseEvent(505, point);
        mouseEvent(503, point);
        mouseEvent(501, point);
        mouseEvent(502, point);
        mouseEvent(500, point);

        if (!Microbot.getClient().isClientThread()) {
            sleep(50, 100);
        }
    }

    private static void mouseEvent(int id, Point point) {
        MouseEvent e = new MouseEvent(client.getCanvas(), id, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, 1);
        client.getCanvas().dispatchEvent(e);
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

    public static boolean isTimerActive(GameTimer gameTimer) {
        if (!isPluginEnabled(TimersPlugin.class.getName())) {
            log("Please enable the timers plugin to make sure the script is working properly.");
            return true;
        }
        for (InfoBox key : infoBoxManager.getInfoBoxes()) {
            if (key.getName().equals(gameTimer.name())) {
                return true;
            }
        }
        return false;
    }

    public static void log(String message) {
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
}

