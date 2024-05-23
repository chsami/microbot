package net.runelite.client.plugins.microbot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.ProfileManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.microbot.cooking.CookingScript;
import net.runelite.client.plugins.microbot.mining.MiningScript;
import net.runelite.client.plugins.microbot.thieving.ThievingScript;
import net.runelite.client.plugins.microbot.thieving.summergarden.SummerGardenConfig;
import net.runelite.client.plugins.microbot.thieving.summergarden.SummerGardenPlugin;
import net.runelite.client.plugins.microbot.thieving.summergarden.SummerGardenScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapOverlay;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Consumer;

import static net.runelite.client.plugins.microbot.Microbot.updateItemContainer;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Microbot",
        description = "Microbot",
        tags = {"main", "microbot", "parent"},
        alwaysOn = true,
        hidden = true
)
@Slf4j
public class MicrobotPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    Notifier notifier;
    @Inject
    WorldService worldService;
    @Inject
    ProfileManager profileManager;
    @Inject
    ItemManager itemManager;
    @Inject
    NPCManager npcManager;
    @Inject
    ConfigManager configManager;
    @Inject
    ChatMessageManager chatMessageManager;
    @Inject
    PluginManager pluginManager;

    @Inject
    private MicrobotOverlay microbotOverlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SpriteManager spriteManager;
    @Inject
    private WorldMapOverlay worldMapOverlay;
    @Inject
    @Named("disableWalkerUpdate")
    private boolean disableWalkerUpdate;

    @Inject
    private Rs2NpcManager rs2NpcManager;
    @Inject
    private WorldMapPointManager worldMapPointManager;

    private Plugin summerGardenPlugin = null;

    public ThievingScript thievingScript;
    public CookingScript cookingScript;
    public MiningScript miningScript;
    public SummerGardenScript summerGardenScript;

    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setWorldService(worldService);
        Microbot.setProfileManager(profileManager);
        Microbot.setItemManager(itemManager);
        Microbot.setNpcManager(npcManager);
        Microbot.setMouse(new VirtualMouse());
        Microbot.setSpriteManager(spriteManager);
        Microbot.setDisableWalkerUpdate(disableWalkerUpdate);
        Microbot.setPluginManager(pluginManager);
        Microbot.setWorldMapOverlay(worldMapOverlay);
        Microbot.setWorldMapPointManager(worldMapPointManager);
        if (overlayManager != null) {
            overlayManager.add(microbotOverlay);
        }

        new InputSelector(clientToolbar);

        //TODO: Rs2NpcManager.loadJson();

        for (Plugin plugin : pluginManager.getPlugins()) {
            if (plugin.getClass() == SummerGardenPlugin.class) {
                summerGardenPlugin = plugin;
            }
        }
    }

    protected void shutDown() {
        overlayManager.remove(microbotOverlay);
        if (cookingScript != null) {
            cookingScript.shutdown();
            cookingScript = null;
        }
        if (thievingScript != null) {
            thievingScript.shutdown();
            thievingScript = null;
        }
    }


    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        Microbot.setIsGainingExp(true);
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        System.out.println("Event Container ID: " + event.getContainerId());

        if (event.getContainerId() == InventoryID.BANK.getId()) {
            Rs2Bank.storeBankItemsInMemory(event);
        }
        if (event.getContainerId() == InventoryID.INVENTORY.getId()) {
            Rs2Inventory.storeInventoryItemsInMemory(event);
        }
        if (event.getContainerId() == InventoryID.EQUIPMENT.getId()) {
            Rs2Equipment.storeEquipmentItemsInMemory(event);
        }

        if (event.getContainerId() == 64) { // Mage Guild Shop
            //Code for Bank's Rs2Shop
            java.util.List<Rs2Item> shopItems = updateItemContainer(64, event);
            System.out.println(shopItems.size());
            Rs2Shop.storeShopItemsInMemory(event, 64);
        }
        if (event.getContainerId() == 435) { // Charter Shop
            //Code for Bank's Rs2Shop
            java.util.List<Rs2Item> shopItems = updateItemContainer(435, event);
            System.out.println(shopItems.size());
            Rs2Shop.storeShopItemsInMemory(event, 435);
        }
        if (event.getContainerId() == 131) { // Lundail Rune Shop
            //Code for Bank's Rs2Shop
            java.util.List<Rs2Item> shopItems = updateItemContainer(131, event);
            System.out.println(shopItems.size());
            Rs2Shop.storeShopItemsInMemory(event, 131);
        }
        if (event.getContainerId() == 419) { // Baba Yaga Rune Shop
            //Code for Bank's Rs2Shop
            java.util.List<Rs2Item> shopItems = updateItemContainer(419, event);
            System.out.println(shopItems.size());
            Rs2Shop.storeShopItemsInMemory(event, 419);
        }
        if (event.getContainerId() == 318) { // Ordan (Blast Furnace Shop)
            //Code for Bank's Rs2Shop
            java.util.List<Rs2Item> shopItems = updateItemContainer(318, event);
            System.out.println(shopItems.size());
            Rs2Shop.storeShopItemsInMemory(event, 318);
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.HOPPING || gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.CONNECTION_LOST) {
            if (Rs2Bank.bankItems != null)
                Rs2Bank.bankItems.clear();
        }
    }

    private Consumer<MenuEntry> menuActionNpcConsumer(boolean shift, net.runelite.api.NPC npc) {
        return e ->
        {
            if (thievingScript == null) {
                thievingScript = new ThievingScript();
            } else {
                thievingScript.shutdown();
                thievingScript = null;
            }
        };
    }

    private Consumer<MenuEntry> menuActionCookingConsumer(int gameObjectId) {
        return e ->
        {
            if (cookingScript == null) {
                cookingScript = new CookingScript();
                cookingScript.run(gameObjectId);
            } else {
                cookingScript.shutdown();
                cookingScript = null;
            }
        };
    }

    private Consumer<MenuEntry> menuActionMinerConsumer(int gameObjectId) {
        return e ->
        {
            if (miningScript == null) {
                miningScript = new MiningScript();
                miningScript.run(gameObjectId);
            } else {
                miningScript.shutdown();
                miningScript = null;
            }
        };
    }

    private Consumer<MenuEntry> menuActionSummerGarden() {
        return e ->
        {
            if (summerGardenScript == null) {
                summerGardenScript = new SummerGardenScript();
                summerGardenScript.run(configManager.getConfig(SummerGardenConfig.class), chatMessageManager);
                startPlugin(summerGardenPlugin);

            } else {
                summerGardenScript.shutdown();
                summerGardenScript = null;
            }
        };
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event) {
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        Rs2Player.handlePotionTimers(event);
    }

    @Subscribe
    protected void onClientTick(ClientTick t) {
        if (!pluginManager.isActive(summerGardenPlugin) && summerGardenScript != null) {
            summerGardenScript.shutdown();
            summerGardenScript = null;
        } else if (pluginManager.isActive(summerGardenPlugin) && summerGardenScript == null) {
            summerGardenScript = new SummerGardenScript();
            summerGardenScript.run(configManager.getConfig(SummerGardenConfig.class), chatMessageManager);
            startPlugin(summerGardenPlugin);
        }
    }

    @Subscribe(priority = 999)
    private void onMenuEntryAdded(MenuEntryAdded event) {
        if (Microbot.targetMenu != null && event.getType() != Microbot.targetMenu.getType().getId()) {
            this.client.setMenuEntries(new MenuEntry[]{});
        }

        if (Microbot.targetMenu != null) {
            MenuEntry entry =
                    this.client.createMenuEntry(-1)
                            .setOption(Microbot.targetMenu.getOption())
                            .setTarget(Microbot.targetMenu.getTarget())
                            .setIdentifier(Microbot.targetMenu.getIdentifier())
                            .setType(Microbot.targetMenu.getType())
                            .setParam0(Microbot.targetMenu.getParam0())
                            .setParam1(Microbot.targetMenu.getParam1())
                            .setForceLeftClick(true);

            if (Microbot.targetMenu.getItemId() > 0) {
                try {
                    Rs2Reflection.setItemId(entry, Microbot.targetMenu.getItemId());
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.out.println(e.getMessage());
                }
            }
            this.client.setMenuEntries(new MenuEntry[]{entry});
        }
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event) {
        Microbot.targetMenu = null;
        System.out.println(event.getMenuEntry());
    }

    @SneakyThrows
    private void startPlugin(Plugin p) {
        SwingUtilities.invokeAndWait(() ->
        {
            try {
                pluginManager.setPluginEnabled(p, true);
                pluginManager.startPlugin(p);
            } catch (PluginInstantiationException e) {
                System.out.printf("Failed to start plugin: %s%n", p.getName());
            }
        });
    }
}
