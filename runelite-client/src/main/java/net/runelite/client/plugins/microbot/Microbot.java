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
import net.runelite.client.plugins.microbot.scripts.cannon.CannonScript;
import net.runelite.client.plugins.microbot.scripts.combat.attack.AttackNpc;
import net.runelite.client.plugins.microbot.scripts.combat.combatpotion.CombatPotion;
import net.runelite.client.plugins.microbot.scripts.combat.food.Food;
import net.runelite.client.plugins.microbot.scripts.combat.jad.Jad;
import net.runelite.client.plugins.microbot.scripts.combat.prayer.PrayerPotion;
import net.runelite.client.plugins.microbot.scripts.construction.Construction;
import net.runelite.client.plugins.microbot.scripts.fletching.Fletcher;
import net.runelite.client.plugins.microbot.scripts.loot.LootScript;
import net.runelite.client.plugins.microbot.scripts.magic.boltenchanting.BoltEnchanter;
import net.runelite.client.plugins.microbot.scripts.magic.highalcher.HighAlcher;
import net.runelite.client.plugins.microbot.scripts.magic.housetabs.HouseTabs;
import net.runelite.client.plugins.microbot.scripts.movie.UsernameHiderScript;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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

    //scripts
    @Getter
    @Setter
    private static HouseTabs houseTabScript;
    @Getter
    @Setter
    private static BoltEnchanter boltEnchanterScript;
    @Getter
    @Setter
    private static HighAlcher highAlcherScript;
    @Getter
    @Setter
    private static LootScript lootScript;
    @Getter
    @Setter
    private static CannonScript cannonScript;
    @Getter
    @Setter
    private static Food foodScript;
    @Getter
    @Setter
    private static PrayerPotion prayerPotionScript;
    @Getter
    @Setter
    private static AttackNpc attackNpcScript;
    @Getter
    @Setter
    private static NPCManager npcManager;
    @Getter
    @Setter
    private static Fletcher fletcherScript;
    @Getter
    @Setter
    private static CombatPotion combatPotion;
    @Getter
    @Setter
    private static Jad jad;
    @Getter
    @Setter
    private static Construction constructionScript;
    @Getter
    @Setter
    private static UsernameHiderScript usernameHiderScript;

    public static boolean isGainingExp = false;
    public static boolean isBussy = false;

    public static void setIsGainingExp(boolean value) {
        isGainingExp = value;
        scheduleIsGainingExp();
    }

    public static void scheduleIsGainingExp() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(() -> {
            isGainingExp = false;
        }, 3000, TimeUnit.MILLISECONDS);
    }

    public static boolean isLoggedIn() {
        GameState idx = client.getGameState();
        return idx == GameState.LOGGED_IN;
    }
}
