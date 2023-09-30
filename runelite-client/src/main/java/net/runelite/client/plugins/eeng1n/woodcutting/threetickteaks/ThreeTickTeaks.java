package net.runelite.client.plugins.eeng1n.woodcutting.threetickteaks;


import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
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
        name = PluginDescriptor.Engin + " 3T Teaks",
        description = "Performs 3T Teaks",
        enabledByDefault = false
)
@Slf4j
public class ThreeTickTeaks extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ThreeTickTeaksOverlay threeTickTeaksOverlay;
    @Inject
    private Notifier notifier;

    @Inject
    private ThreeTickTeaksConfig config;

    private boolean enabled;
    private boolean inProgress;

    private boolean overlayActive = false;

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    ThreeTickTeaksState state = ThreeTickTeaksState.Idle;

    @Provides
    ThreeTickTeaksConfig getConfig(ConfigManager manager) {
        return manager.getConfig(ThreeTickTeaksConfig.class);
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
            expstarted = Microbot.getClient().getSkillExperience(Skill.WOODCUTTING);
            startinglevel = Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING);
            timeBegan = System.currentTimeMillis();
            if (overlayManager != null) {
                overlayManager.add(threeTickTeaksOverlay);
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {

        if(config.overlay() && !overlayActive) {
            overlayManager.add(threeTickTeaksOverlay);
            overlayActive = true;
        }
        if(!config.overlay() && overlayActive) {
            overlayManager.remove(threeTickTeaksOverlay);
            overlayActive = false;
        }


        if (enabled && !inProgress) {

            switch (state) {
                case UseGuam:
                    executor.submit(this::useGuam);
                    break;
                case UseTarAndDrop:
                    executor.submit(this::useTarAndDropLog);
                    break;
                case ClickTeakTree:
                case Idle:
                    executor.submit(this::clickTeakTree);
                    break;
                default:
                    notifier.notify("ThreeTickTeaks stopped unexpectedly!");
                    break;
            }
        }
    }

    private void useGuam() {
        inProgress = true;
        sleep(13, 167);
        Widget guamLeafWidget = Inventory.findItem("Guam leaf");
        Microbot.getMouse().click(guamLeafWidget.getBounds());

        state = ThreeTickTeaksState.UseTarAndDrop;
        inProgress = false;
    }

    private void useTarAndDropLog() {
        inProgress = true;
        sleep(18, 132);

        Inventory.useItemFast(ItemID.SWAMP_TAR, "Use");

        Inventory.useItemFast(ItemID.TEAK_LOGS, "Drop");

        state = ThreeTickTeaksState.ClickTeakTree;
        inProgress = false;
    }

    private void clickTeakTree() {
        inProgress = true;
        sleep(23, 213);

        GameObject teakTree = getTeakTree();

        Rs2GameObject.interact(teakTree, "Chop down");

        state = ThreeTickTeaksState.UseGuam;
        inProgress = false;
    }

    @Override
    protected void shutDown() {
        enabled = false;

        state = ThreeTickTeaksState.Idle;
        overlayManager.remove(threeTickTeaksOverlay);
    }

    private GameObject getTeakTree() {
        return Rs2GameObject.findObject("Teak tree");
    }
}