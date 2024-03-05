package net.runelite.client.plugins.danplugins.fishing.threetickbarb;


import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation.CutEatTickManipulationData;
import net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation.TickManipulationData;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntilOnClientThread;
import static net.runelite.client.plugins.natepainthelper.Info.*;

@PluginDescriptor(
        name = PluginDescriptor.Dan + "3T Barb Fishing",
        description = "Performs 3T Barb Fishing flawlessly",
        enabledByDefault = false
)
@Slf4j
public class ThreeTickBarb extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ThreeTickBarbOverlay threeTickBarbOverlay;
    @Inject
    private Notifier notifier;
    @Inject
    private EventBus eventBus;
    @Inject
    private ThreeTickBarbConfig config;

    private boolean enabled;
    private boolean inProgress;

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    ThreeTickFishingState state = ThreeTickFishingState.Idle;
    TickManipulationData tickManipulationData;

    @Provides
    ThreeTickBarbConfig getConfig(ConfigManager manager) {
        return manager.getConfig(ThreeTickBarbConfig.class);
    }

    @Override
    protected void startUp() {
        if (Microbot.getClient().getGameState() == GameState.LOGGED_IN) {
            Microbot.pauseAllScripts = false;
            Microbot.setClient(client);
            Microbot.setClientThread(clientThread);
            Microbot.setNotifier(notifier);
            Microbot.setMouse(new VirtualMouse());

            enabled = true;
            expstarted = Microbot.getClient().getSkillExperience(Skill.FISHING);
            startinglevel = Microbot.getClient().getRealSkillLevel(Skill.FISHING);
            timeBegan = System.currentTimeMillis();
            tickManipulationData = getTickManipulationData();

            eventBus.register(tickManipulationData);
            if (overlayManager != null) {
                overlayManager.add(threeTickBarbOverlay);
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        if (enabled && !inProgress) {

            switch (state) {
                case UseGuam:
                    executor.submit(this::useGuam);
                    break;
                case UseTarAndDrop:
                    executor.submit(this::useTarAndDropFish);
                    break;
                case ClickFishingSpot:
                case Idle:
                    executor.submit(this::clickFishingSpot);
                    break;
                case LocatingFishingSpot:
                    break;
                default:
                    notifier.notify("ThreeTickBarb stopped unexpectedly!");
                    break;
            }
        }
    }

    private void useGuam() {
        inProgress = true;
        sleep(13, 167);
        tickManipulationData.getFirstTickRunnable().run();

        state = ThreeTickFishingState.UseTarAndDrop;
        inProgress = false;
    }

    private void useTarAndDropFish() {
        inProgress = true;
        sleep(18, 132);

        tickManipulationData.getSecondTickRunnable().run();

        for (Integer itemId : tickManipulationData.getItemIdsToDrop())
        {
            Inventory.useItemFast(itemId, "drop");
        }

        state = ThreeTickFishingState.ClickFishingSpot;
        inProgress = false;
    }

    private void clickFishingSpot() {
        inProgress = true;
        sleep(23, 213);

        NPC fishingSpot = getFishingSpot();

        Rs2Npc.interact(fishingSpot, "Use-rod");

        state = ThreeTickFishingState.UseGuam;
        inProgress = false;
    }

    private void locateFishingSpot(NPC fishingSpot) {
        inProgress = true;
        sleep(11, 254);

        Rs2Menu.doAction("Use-rod", fishingSpot.getCanvasTilePoly());
        sleepUntilOnClientThread(() -> {
            int distance = fishingSpot.getWorldLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation());
            return distance <= 1;
        });

        state = ThreeTickFishingState.UseGuam;
        inProgress = false;
    }

    @Override
    protected void shutDown() {
        enabled = false;
        eventBus.unregister(tickManipulationData);
        state = ThreeTickFishingState.Idle;
        overlayManager.remove(threeTickBarbOverlay);
    }

    private boolean isFish(Widget inventoryItem) {
        return inventoryItem.getItemId() == 11328 ||
                inventoryItem.getItemId() == 11330 ||
                inventoryItem.getItemId() == 11332;
    }

    private NPC getFishingSpot() {
        return Rs2Npc.getNpc("Fishing spot");
    }

    private TickManipulationData getTickManipulationData()
    {
        {
            final TickManipulationData normalMethod = config.tickManipulateMode().getTickManipulationData();
            if (config.cutEat())
            {
                return new CutEatTickManipulationData(normalMethod);
            }

            return normalMethod;
        }
    }
}