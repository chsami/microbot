package net.runelite.client.plugins.microbot.prayer;

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
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

import static net.runelite.api.MenuAction.CC_OP;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Gilded Altar",
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
            skipTicks--;
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
        Rs2Reflection.invokeMenu(-1, 7602207, CC_OP.getId(), 1, -1, "House Option", "", -1, -1);
        Rs2Reflection.invokeMenu(-1, 24248341, CC_OP.getId(), 1, -1, "Leave House", "", -1, -1);
        setSkipTicks(4);
    }

    public void unnoteBones() {
        if (Microbot.getClient().getWidget(14352385) == null) {
            Rs2Inventory.use("bones");
            Rs2Npc.interact("Phials", "Use");
        } else if (Microbot.getClient().getWidget(14352385) != null) {
            VirtualKeyboard.keyPress('3');
            setSkipTicks(2);
        }
    }

    private void enterHouse() {
        boolean isAdvertismentWidgetOpen = Rs2Widget.hasWidget("House advertisement");

        if (!isAdvertismentWidgetOpen) {
            Rs2GameObject.interact(ObjectID.HOUSE_ADVERTISEMENT, "View");
            return;
        }

        Widget container = Rs2Widget.getWidget(52, 9);
        if (container == null || container.getChildren() == null) return;
        Widget playerHouse = null;
        for (String player : config.housePlayerName().split(",")) {
            playerHouse = Rs2Widget.findWidget(player, Arrays.stream(container.getChildren()).collect(Collectors.toList()));
            if (playerHouse != null) break;
        }

        if (playerHouse != null) {
            Rs2Reflection.invokeMenu(playerHouse.getIndex(), 3407891, CC_OP.getId(), 1, -1,
                    "", "", (int) playerHouse.getBounds().getCenterX(), (int) playerHouse.getBounds().getCenterY());
        }
    }

    public void bonesOnAltar() {
        TileObject altar = Rs2GameObject.findObjectById(ObjectID.ALTAR_40878);
        if (altar == null) {
            altar = Rs2GameObject.findObjectById(ObjectID.ALTAR_13197);
        }
        if (altar != null) {
            Rs2Inventory.useUnNotedItemOnObject("bones", altar);
            setSkipTicks(2);
        }
    }

}
