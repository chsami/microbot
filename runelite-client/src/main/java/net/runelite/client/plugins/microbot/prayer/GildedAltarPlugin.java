package net.runelite.client.plugins.microbot.prayer;

import java.awt.AWTException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.inject.Provides;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Gilded Altar",
        description = "Gilded Altar plugin",
        tags = {"prayer", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class GildedAltarPlugin extends Plugin {
    @Inject
    private GildedAltarConfig config;
    @Provides
    GildedAltarConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GildedAltarConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private GildedAltarOverlay gildedAltarOverlay;
    @Inject
    private Client client;

    @Getter
    @Setter
    int skipTicks;
    private final int HOUSE_PORTAL_OBJECT = 4525;

    GildedAltarPlayerState state = GildedAltarPlayerState.IDLE;

    private boolean inHouse() {
        return Rs2Npc.getNpc("Phials") == null;
    }

    private boolean hasUnNotedBones() {
        return Rs2Inventory.hasUnNotedItem("bones");
    }

    private boolean hasNotedBones() {
        return Rs2Inventory.hasNotedItem("bones");
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(gildedAltarOverlay);
        }
    }
    /**
     *
     * @param name plugin name
     * @return the plguin
     */
    public static Plugin findPlugin(String name) {
        return Microbot.getPluginManager().getPlugins().stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
    }
    @Override
    protected void shutDown() {
        overlayManager.remove(gildedAltarOverlay);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
            return;

        if (skipTicks > 0)
        {
            if (Random.random(1, 7) != 2) {
                skipTicks--;
            }
            return;
        }

        long startTime = System.currentTimeMillis();

        if (!Rs2Inventory.hasItem(995)) {
            Microbot.showMessage("No gp found in your inventory");
            setSkipTicks(10);
            return;
        }
        if (!hasNotedBones() && !hasUnNotedBones()) {
            Microbot.showMessage("No bones found in your inventory");
            setSkipTicks(10);
            return;
        }

        calculateState();

        switch (state) {
            case LEAVE_HOUSE:
                leaveHouse();
                break;
            case UNNOTE_BONES:
                unnoteBones();
                break;
            case ENTER_HOUSE:
                enterHouse();
                break;
            case BONES_ON_ALTAR:
                bonesOnAltar();
                break;
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Script loop took: " + totalTime);
    }

    private void calculateState() {
        if (hasUnNotedBones() && !inHouse()) {
            state = GildedAltarPlayerState.ENTER_HOUSE;
        } else if (hasUnNotedBones() && inHouse()) {
            state = GildedAltarPlayerState.BONES_ON_ALTAR;
        } else if (!hasUnNotedBones() && !inHouse()) {
            state = GildedAltarPlayerState.UNNOTE_BONES;
        } else if (!hasUnNotedBones() && inHouse()) {
            state = GildedAltarPlayerState.LEAVE_HOUSE;
        }
    }

    public void leaveHouse() {
        System.out.println("Attempting to leave house...");
        
        if (Rs2GameObject.findObjectById(HOUSE_PORTAL_OBJECT) == null) {
            System.out.println("Not in house, HOUSE_PORTAL_OBJECT not found.");
            return;
        }

        // Switch to Settings tab
        Rs2Tab.switchToSettingsTab();
        setSkipTicks(2);

        // Click House Options
        if (Rs2Widget.clickWidget(7602207)) {
            System.out.println("Clicked House Options button");
            setSkipTicks(2);
        } else {
            System.out.println("House Options button not found.");
            return;
        }

        // Click Leave House
        if (Rs2Widget.clickWidget(24248341)) {
            System.out.println("Clicked Leave House button");
            setSkipTicks(4);
        } else {
            System.out.println("Leave House button not found.");
        }
    }

    public void unnoteBones() {
        if (client.getWidget(14352385) == null) {
            if (!Rs2Inventory.isItemSelected()) {
                Rs2Inventory.use("bones");
            } else {
                Rs2Npc.interact("Phials", "Use");
                setSkipTicks(2);
            }
        } else if (client.getWidget(14352385) != null) {
            Rs2Keyboard.keyPress('3');
            setSkipTicks(2);
        }
    }

    private void enterHouse() {
        boolean isAdvertisementWidgetOpen = Rs2Widget.hasWidget("House advertisement");

        if (!isAdvertisementWidgetOpen) {
            Rs2GameObject.interact(ObjectID.HOUSE_ADVERTISEMENT, "View");
            setSkipTicks(2);
            return;
        }

        Widget container = Rs2Widget.getWidget(52, 9);
        if (container == null || container.getChildren() == null) return;

        for (String player : config.housePlayerName().split(",")) {
            Widget playerHouse = Rs2Widget.findWidget(player, Arrays.stream(container.getChildren()).collect(Collectors.toList()));
            if (playerHouse != null) {
                Rs2Widget.clickChildWidget(3407891, playerHouse.getIndex());
                setSkipTicks(2);
                return;
            }
        }
    }

    public void bonesOnAltar() {
        TileObject altar = Rs2GameObject.findObjectById(ObjectID.ALTAR_40878);
        if (altar == null) {
            altar = Rs2GameObject.findObjectById(ObjectID.ALTAR_13197);
        }
        if (altar != null) {
            Rs2Inventory.useUnNotedItemOnObject("bones", altar);
            setSkipTicks(1);
        }
    }
}
