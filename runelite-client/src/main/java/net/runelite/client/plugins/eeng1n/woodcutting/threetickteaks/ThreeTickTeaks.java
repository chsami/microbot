package net.runelite.client.plugins.eeng1n.woodcutting.threetickteaks;


import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.natepainthelper.Info.*;

@PluginDescriptor(
        name = PluginDescriptor.Engin + " 3T Teaks",
        description = "Performs 3T Teaks",
        tags = { "woodcutting", "microbot", "skills", "eengin", "eeng1n" },
        enabledByDefault = false
)
@Slf4j
public class ThreeTickTeaks extends Plugin {
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ThreeTickTeaksOverlay threeTickTeaksOverlay;
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

            enabled = true;
            expstarted = Microbot.getClient().getSkillExperience(Skill.WOODCUTTING);
            startinglevel = Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING);
            timeBegan = System.currentTimeMillis();

            if (overlayManager != null) {
                overlayManager.add(threeTickTeaksOverlay);
            }

            executor.submit(this::checkRequirements);
        }
    }

    private void checkRequirements() {
        if(Microbot.getClient().getRealSkillLevel(Skill.WOODCUTTING) < 35) {
            enabled = false;
            Microbot.showMessage("The plugin has been disabled due to a not high enough Woodcutting level! You need at least level 35. Please make sure you have the required level and restart the script afterwards.");
        }

        if(!Inventory.hasItem("Guam leaf")
                || !Inventory.hasItem("Pestle and mortar")
                || !Inventory.hasItem("Swamp tar")
        ) {
            enabled = false;
            Microbot.showMessage("The plugin has been disabled due to missing items! Please make sure you have the required items and restart the script afterwards.");
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
                    Microbot.showMessage("ThreeTickTeaks stopped unexpectedly!");
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