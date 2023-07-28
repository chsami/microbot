package net.runelite.client.plugins.microbot;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ProfileManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;
import net.runelite.client.plugins.microbot.util.walker.Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class Microbot {
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
    public static boolean isGainingExp = false;
    public static boolean pauseAllScripts = false;
    public static String status = "IDLE";

    private static ScheduledExecutorService xpSchedulor = Executors.newSingleThreadScheduledExecutor();
    private static ScheduledFuture<?> xpSchedulorFuture;
    private static net.runelite.api.World quickHopTargetWorld;

    public static boolean isWalking() {
        return Microbot.getClientThread().runOnClientThread(() -> getClient().getLocalPlayer().getPoseAnimation() != 813 && getClient().getLocalPlayer().getPoseAnimation() != 808);
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

    public static void toggleSpecialAttack(int energyRequired) {
        int currentSpecEnergy = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
        if (currentSpecEnergy >= 999 && (client.getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED) == 0)) {
            for (int i = 0; i < 3; i++) {
                Rs2Widget.clickWidget("special attack");
                sleep(5000);
            }
        }
    }

    public static void hopToWorld(int worldNumber){
        if (quickHopTargetWorld != null) return;
        if(Microbot.getClient().getWorld() == worldNumber){return;}
        World newWorld = Microbot.getWorldService().getWorlds().findWorld(worldNumber);
        if(newWorld == null){
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
        if (rsWorld == null){
            return;
        }
        if (Microbot.getClient().getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            Microbot.getClient().openWorldHopper();
        }
        Microbot.getClient().hopToWorld(rsWorld);
        quickHopTargetWorld = null;
    }
}
