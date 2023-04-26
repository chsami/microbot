package net.runelite.client.plugins.microbot;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.microbot.construction.ConstructionScript;
import net.runelite.client.plugins.microbot.farming.tithefarm.TitheFarmScript;
import net.runelite.client.plugins.microbot.giantsfoundry.GiantsFoundryScript;
import net.runelite.client.plugins.microbot.magic.housetab.HouseTabScript;
import net.runelite.client.plugins.microbot.playerassist.cannon.CannonScript;
import net.runelite.client.plugins.microbot.playerassist.combat.AttackNpcScript;
import net.runelite.client.plugins.microbot.playerassist.combat.CombatPotionScript;
import net.runelite.client.plugins.microbot.playerassist.combat.FoodScript;
import net.runelite.client.plugins.microbot.playerassist.combat.PrayerPotionScript;
import net.runelite.client.plugins.microbot.playerassist.loot.LootScript;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
    public static boolean isGainingExp = false;
    public static boolean pauseAllScripts = false;

    private static ScheduledExecutorService xpSchedulor = Executors.newSingleThreadScheduledExecutor();

    private static ScheduledFuture<?> xpSchedulorFuture;

    public static boolean isWalking() {
        return getClient().getLocalPlayer().getPoseAnimation() != 813 && getClient().getLocalPlayer().getPoseAnimation() != 808;
    }


    public static int getVarbitValue(int varbit) {
        return getClientThread().runOnClientThread(() -> getClient().getVarbitValue(varbit));
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
        }, 3000, TimeUnit.MILLISECONDS);
    }

    public static boolean isLoggedIn() {
        if (client == null) return false;
        GameState idx = client.getGameState();
        return idx == GameState.LOGGED_IN;
    }
}
