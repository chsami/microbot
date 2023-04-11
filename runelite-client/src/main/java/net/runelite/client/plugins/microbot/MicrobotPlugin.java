package net.runelite.client.plugins.microbot;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.*;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
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
import net.runelite.client.plugins.microbot.scripts.magic.housetabs.HOUSETABS_CONFIG;
import net.runelite.client.plugins.microbot.scripts.magic.housetabs.HouseTabs;
import net.runelite.client.plugins.microbot.scripts.movie.UsernameHiderScript;
import net.runelite.client.plugins.microbot.util.mouse.HardwareMouse;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.WorldUtil;

import javax.inject.Inject;
import javax.inject.Named;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

@PluginDescriptor(
        name = "1.MicrobotPlugin",
        description = "Microbot plugin",
        tags = {"combat"}
)
@Slf4j
public class MicrobotPlugin extends Plugin {
    @Inject
    @Named("developerMode")
    boolean developerMode;

    @Inject
    private Client client;

    @Inject
    private MicrobotConfig config;

    @Provides
    MicrobotConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MicrobotConfig.class);
    }

    @Inject
    private WorldService worldService;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ScheduledExecutorService scheduledExecutorService;
    @Inject
    private ClientThread clientThread;
    @Inject
    private MicrobotOverlay microbotOverlay;
    @Inject
    WorldMapPointManager worldMapPointManager;
    @Inject
    SpriteManager spriteManager;
    @Inject
    ItemManager itemManager;
    @Inject
    Notifier notifier;
    @Inject
    NPCManager npcManager;

    @Override
    protected void startUp() throws AWTException {
        Microbot.isBussy = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setWorldMapPointManager(worldMapPointManager);
        Microbot.setSpriteManager(spriteManager);
        Microbot.setItemManager(itemManager);
        Microbot.setNotifier(notifier);
        Microbot.setNpcManager(npcManager);

        if (config.toggleHardwareMouse()) {
            Microbot.setMouse(new HardwareMouse());
        } else {
            Microbot.setMouse(new VirtualMouse());
        }
        if (overlayManager != null) {
            overlayManager.add(microbotOverlay);
        }

//START COMBAT SCRIPTS
        if (config.toggleCannon()) {
            Microbot.setCannonScript(new CannonScript());
            Microbot.getCannonScript().run();
        }

        if (config.toggleCombat()) {
            if (Microbot.getAttackNpcScript() == null)
                Microbot.setAttackNpcScript(new AttackNpc());
            Microbot.getAttackNpcScript().run(config.combatNpcList());
        }

        if (config.toggleFood()) {
            Microbot.setFoodScript(new Food());
            Microbot.getFoodScript().run();
        }

        if (config.togglePrayer()) {
            Microbot.setPrayerPotionScript(new PrayerPotion());
            Microbot.getPrayerPotionScript().run();
        }

        if (config.isLootItems()) {
            if (Microbot.getLootScript() == null) Microbot.setLootScript(new LootScript());
            Microbot.getLootScript().run(config.itemsToLoot());
        }
//END COMBAT SCRIPTS
        if (config.HouseTabBotToggle()) {
            Microbot.setHouseTabScript(new HouseTabs(
                    HOUSETABS_CONFIG.HOUSE_ADVERTISEMENT,
                    new String[]{"xGrace", "workless", "Lego Batman", "Batman 321", "Batman Chest"}));
            Microbot.getHouseTabScript().run();
        }
        if (config.BoltEnchanterBotToggle()) {
            Microbot.setBoltEnchanterScript(new BoltEnchanter());
            Microbot.getBoltEnchanterScript().run();
        }

        if (config.HighAlcherBotToggle()) {
            Microbot.setHighAlcherScript(new HighAlcher());
            Microbot.getHighAlcherScript().run();
        }
        if (config.toggleFletcher()) {
            Microbot.setFletcherScript(new Fletcher());
            if (config.toggleBowstrings()) {
                Microbot.getFletcherScript().runBowstrings(config);
            } else {
                Microbot.getFletcherScript().run(config);
            }
        }
        if (config.toggleCombatPotion()) {
            Microbot.setCombatPotion(new CombatPotion());
            Microbot.getCombatPotion().run();
        }
        if (config.toggleJad()) {
            Microbot.setJad(new Jad());
            Microbot.getJad().run();
        }
        if (config.toggleConstruction()) {
            Microbot.setConstructionScript(new Construction());
            Microbot.getConstructionScript().run();
        }
        if (config.toggleHideUserName()) {
            Microbot.setUsernameHiderScript(new UsernameHiderScript());
            Microbot.getUsernameHiderScript().run();
        }
    }

    protected void shutDown() {
        overlayManager.remove(microbotOverlay);

        //shutdown scripts
        if (Microbot.getHouseTabScript() != null)
            Microbot.getHouseTabScript().shutdown();
        if (Microbot.getBoltEnchanterScript() != null)
            Microbot.getBoltEnchanterScript().shutdown();
        if (Microbot.getHighAlcherScript() != null)
            Microbot.getHighAlcherScript().shutdown();
        if (Microbot.getCannonScript() != null)
            Microbot.getCannonScript().shutdown();
        if (Microbot.getFoodScript() != null)
            Microbot.getFoodScript().shutdown();
        if (Microbot.getCannonScript() != null)
            Microbot.getCannonScript().shutdown();
        if (Microbot.getPrayerPotionScript() != null)
            Microbot.getPrayerPotionScript().shutdown();
        if (Microbot.getLootScript() != null)
            Microbot.getLootScript().shutdown();
        if (Microbot.getAttackNpcScript() != null)
            Microbot.getAttackNpcScript().shutdown();
        if (Microbot.getFletcherScript() != null)
            Microbot.getFletcherScript().shutdown();
        if (Microbot.getCombatPotion() != null)
            Microbot.getCombatPotion().shutdown();
        if (Microbot.getJad() != null)
            Microbot.getJad().shutdown();
        if (Microbot.getConstructionScript() != null)
            Microbot.getConstructionScript().shutdown();
        if (Microbot.getUsernameHiderScript() != null)
            Microbot.getUsernameHiderScript().shutdown();
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned itemSpawned) {
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getMessage().contains("You do not have enough") && event.getType() == ChatMessageType.GAMEMESSAGE && Microbot.getHighAlcherScript() != null) {
            Microbot.getHighAlcherScript().shutdown();
        }
        if (event.getMessage().contains("reach that")) {
            AttackNpc.skipNpc();
        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {

    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {

    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
    }


    @Subscribe
    public void onGameTick(GameTick event) {
    }

    public static final List<NPC> fishingSpots = new ArrayList<>();

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        final NPC npc = event.getNpc();

        if (FishingSpot.findSpot(npc.getId()) == null) {
            return;
        }

        fishingSpots.add(npc);
    }

    public void setWorld(int worldNumber) {
        net.runelite.http.api.worlds.World world = worldService.getWorlds().findWorld(worldNumber);
        final net.runelite.api.World rsWorld = client.createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
        client.changeWorld(rsWorld);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) throws AWTException, InterruptedException {
        // client.setUsername("sami.chkhachkhi@gmail.com");
       /* client.setUsername("grover@langworth.name");
        client.setPassword("test123");
        setWorld(330);*/
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        Microbot.setIsGainingExp(true);
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
    }

    @Subscribe
    public void onCommandExecuted(CommandExecuted commandExecuted) {
        if (developerMode && commandExecuted.getCommand().equals("stop")) {
            shutDown();
        }
    }
}