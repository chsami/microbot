package net.runelite.client.plugins.microbot;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ProfileManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.microbot.util.event.EventHandler;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;
import net.runelite.client.plugins.microbot.util.walker.Walker;
import net.runelite.client.plugins.microbot.util.widget.models.ItemWidget;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;

import javax.swing.*;
import java.util.concurrent.*;

public class Microbot {
    @Getter
    @Setter
    private static EventHandler eventHandler;
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
    private static Walker walker;
    @Getter
    @Setter
    private static ProfileManager profileManager;
    @Getter
    @Setter
    private static WorldService worldService;
    @Getter
    @Setter
    private static boolean disableWalkerUpdate;

    public static boolean debug = false;

    public static boolean isGainingExp = false;
    public static boolean pauseAllScripts = false;
    public static String status = "IDLE";

    public static boolean enableAutoRunOn = true;

    private static ScheduledExecutorService xpSchedulor = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> xpSchedulorFuture;
    private static net.runelite.api.World quickHopTargetWorld;

    public static Walker getWalkerForKotlin() {
        return walker;
    }

    public static Client getClientForKotlin() {
        return client;
    }

    public static ClientThread getClientThreadForKotlin() {
        return clientThread;
    }

    public static Mouse getMouseForKotlin() { return mouse; }
    public static WorldService getWorldServiceForKotlin() { return worldService; }

    public static boolean getDisableWalkerUpdateForKotlin() { return disableWalkerUpdate; }

    @Deprecated(since = "Use isMoving", forRemoval = true)
    public static boolean isWalking() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().getPoseAnimation()
                != Microbot.getClient().getLocalPlayer().getIdlePoseAnimation());
    }

    public static boolean isMoving() {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().getPoseAnimation()
                != Microbot.getClient().getLocalPlayer().getIdlePoseAnimation());
    }


    public static boolean isAnimating() {
        return Microbot.getClientThread().runOnClientThread(() -> getClient().getLocalPlayer().getAnimation() != -1);
    }

    public static int getVarbitValue(int varbit) {
        return getClientThread().runOnClientThread(() -> getClient().getVarbitValue(varbit));
    }

    public static int getVarbitPlayerValue(int varbit) {
        return getClientThread().runOnClientThread(() -> getClient().getVarpValue(varbit));
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
        return idx != GameState.LOGIN_SCREEN;
    }

    public static boolean hasLevel(int levelRequired, Skill skill) {
        return Microbot.getClient().getRealSkillLevel(skill) >= levelRequired;
    }

    public static void hopToWorld(int worldNumber) {
        if (quickHopTargetWorld != null || Microbot.getClient().getGameState() != GameState.LOGGED_IN) return;
        if (Microbot.getClient().getWorld() == worldNumber) {
            return;
        }
        World newWorld = Microbot.getWorldService().getWorlds().findWorld(worldNumber);
        if (newWorld == null) {
            Microbot.getNotifier().notify("Invalid World");
            System.out.println("Tried to hop to an invalid world");
            return;
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
            return;
        }
        Microbot.getClient().openWorldHopper();
        Microbot.getClient().hopToWorld(rsWorld);
        quickHopTargetWorld = null;
    }

    public static void showMessage(String message) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            try {
                SwingUtilities.invokeAndWait(() ->
                {
                    JOptionPane.showConfirmDialog(null, message, "Message",
                            JOptionPane.DEFAULT_OPTION);
                });
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        });
    }

    public static CopyOnWriteArrayList<ItemWidget> updateItemContainer(int id, ItemContainerChanged e) {
        if (e.getContainerId() == id) {
            CopyOnWriteArrayList<ItemWidget> list = new CopyOnWriteArrayList<>();
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

                list.add(new ItemWidget(composition.getName(), item.getId(), item.getQuantity(), i));
            }
            return list;
        }
        return null;
    }
}
