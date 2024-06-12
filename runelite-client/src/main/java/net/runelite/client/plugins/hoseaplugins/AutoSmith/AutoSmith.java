package net.runelite.client.plugins.hoseaplugins.AutoSmith;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.*;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.BankInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.NPCInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.TileObjectInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.WidgetInfoExtended;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.InventoryUtil;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PlayerUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Predicate;

@PluginDescriptor(name = "<html><font color=\"#FF9DF9\">[PP]</font> AutoSmith</html>",
        description = "",
        enabledByDefault = false,
        tags = {"poly", "plugin"})
@Slf4j
public class AutoSmith extends Plugin {
    public int timeout = 0;
    public int idleTicks = 0;
    @Inject
    PlayerUtil playerUtil;
    public boolean started = false;
    @Inject
    Client client;
    @Inject
    OverlayManager overlayManager;
    @Inject
    AutoSmithOverlay overlay;
    @Inject
    AutoSmithConfig config;
    @Inject
    private KeyManager keyManager;

    public boolean isSmithing;

    @Override
    @SneakyThrows
    public void startUp() {
        overlayManager.add(overlay);
        timeout = 0;
        isSmithing = false;
        keyManager.registerKeyListener(toggle);
        log.info(config.bar().getName() + " - " + config.item().toString());
    }

    @Override
    public void shutDown() {
        isSmithing = false;
        timeout = 0;
        idleTicks = 0;

        started = false;
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);

    }

    @Provides
    public AutoSmithConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoSmithConfig.class);
    }


    @Subscribe
    public void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }
        if (playerUtil.isInteracting() || client.getLocalPlayer().getAnimation() == -1) idleTicks++;
        else idleTicks = 0;

        if (timeout > 0) {
            timeout--;
            if (idleTicks > 10 || !hasEnoughBars()) {
                timeout = 0;
                isSmithing = false;
            }
            return;
        }


        if (isSmithing) {
            if (!hasEnoughBars()) {
                isSmithing = false;
            }
            if (hasEnoughBars())
                return;
        }

        checkRunEnergy();
        Optional<TileObject> anvil = TileObjects.search().withName("Anvil").nearestToPlayer();
        if (hasEnoughBars() && InventoryUtil.hasItem("Hammer")) {
            if (client.getWidget(WidgetInfo.SMITHING_INVENTORY_ITEMS_CONTAINER) != null) {
                log.info("smithing");
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(client.getWidget(config.item().getWidgetInfo().getPackedId()), "Smith", "Smith set");
                isSmithing = true;
                timeout = 5 * (27 / config.item().getBarsRequired());
            } else if (anvil.isPresent()) {
                log.info("interacting with anvil");
                boolean action = TileObjectInteraction.interact(anvil.get(), "Smith");
                if (!action)
                    log.info("failed anvil interaction");
                timeout = config.tickDelay() == 0 ? 1 : config.tickDelay();//must be at least 1
            }
        }

        if (!hasEnoughBars() || hasBarsButNotEnough() || !InventoryUtil.hasItem("Hammer")) {
            findBank();
            bankHandler();
        }

    }

    private boolean hasBarsButNotEnough() {
        return InventoryUtil.hasItem(config.bar().getName()) && !hasEnoughBars();
    }

    private boolean hasEnoughBars() {
        return (Inventory.getItemAmount(config.bar().getName()) >= config.item().getBarsRequired());
    }

    private boolean canSmithBars() {
        return Inventory.search().withName(config.bar().getName()).result().size() > 5 && !Inventory.search().withName("Hammer").empty();
    }

    private void findBank() {
        Optional<NPC> banker = NPCs.search().withAction("Bank").withId(2897).nearestToPlayer();
        Optional<TileObject> bank = TileObjects.search().withAction("Bank").nearestToPlayer();
        if (!Bank.isOpen()) {
            if (banker.isPresent()) {
                NPCInteraction.interact(banker.get(), "Bank");
                timeout = config.tickDelay() == 0 ? 1 : config.tickDelay();
            } else if (bank.isPresent()) {
                TileObjectInteraction.interact(bank.get(), "Bank");
                timeout = config.tickDelay() == 0 ? 1 : config.tickDelay();
            } else {
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Couldn't find bank or banker", null);
                EthanApiPlugin.stopPlugin(this);
            }
        }
    }

    private void bankHandler() {
        if (!Bank.isOpen()) return;

        Widget depositInventory = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
        if (depositInventory != null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(depositInventory, "Deposit inventory");
        }

        Bank.search().withName("Hammer").first().ifPresentOrElse(hammer -> {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(hammer, "Withdraw-1");
//            BankInteraction.withdrawX(hammer, 1);
        }, () -> {
            if (Inventory.getItemAmount("Hammer") > 0) return;
            EthanApiPlugin.sendClientMessage("No hammer in bank or inventory");
            EthanApiPlugin.stopPlugin(this);
        });
        Bank.search().withName(config.bar().getName()).first().ifPresentOrElse(bar -> {
//            BankInteraction.withdrawX(bar, 27);
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(bar, "Withdraw-All");
        }, () -> {
            EthanApiPlugin.sendClientMessage("No bars left");
            EthanApiPlugin.stopPlugin(this);
        });
        timeout = config.tickDelay();
    }

    private boolean runIsOff() {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0;
    }

    private void checkRunEnergy() {
        if (runIsOff() && client.getEnergy() >= 10 * 100) {
            log.info("turning run on");
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;
    }
}

