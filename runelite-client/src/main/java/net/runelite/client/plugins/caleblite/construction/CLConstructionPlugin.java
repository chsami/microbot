package net.runelite.client.plugins.caleblite.construction;

import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.caleblite.construction.enums.State;
import net.runelite.client.plugins.caleblite.construction.enums.Buildables;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;

import java.awt.*;

@PluginDescriptor(
        name = "CL Construction",
        description = "Automated construction training plugin",
        tags = {"construction", "skilling", "automation"}
)
public class CLConstructionPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private CLConstructionConfig config;

    private State currentState = State.ENTERING_HOUSE;

    @Override
    protected void startUp() throws Exception {
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
    }

    @Override
    protected void shutDown() throws Exception {
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!config.enablePlugin() || client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        switch (currentState) {
            case ENTERING_HOUSE:
                enterPlayerHouse();
                break;
            case BUILDING:
                interactWithLarder();
                break;
            case REMOVING:
                removeFurniture();
                break;
        }
    }

    private void enterPlayerHouse() {
        if (Rs2Player.isMoving() || Rs2Player.isAnimating()) {
            return;
        }

        if (isInPlayerHouse()) {
            if (!isInBuildMode()) {
                enterBuildMode();
            } else {
                currentState = State.BUILDING;
            }
        } else {
            if (Rs2GameObject.interact("Portal", "Build mode")) {
                Global.sleepUntil(this::isInPlayerHouse, Random.random(5000, 10000));
            }
        }
    }

    private void interactWithLarder() {
        if (Rs2Player.isMoving() || Rs2Player.isAnimating()) {
            return;
        }

        Buildables selectedBuildable = config.buildable();
        TileObject hotspot = Rs2GameObject.findObjectByName(selectedBuildable.getSpotName());

        if (hotspot == null) {
            return;
        }

        if (Rs2GameObject.interact(hotspot, selectedBuildable.getAction())) {
            Global.sleepUntil(() -> !Rs2Player.isMoving() && !Rs2Player.isAnimating(), Random.random(5000, 8000));

            Global.sleepUntil(() -> Rs2GameObject.findObjectByName(selectedBuildable.getBuiltName()) != null,
                    Random.random(3000, 5000));

            if (Rs2GameObject.findObjectByName(selectedBuildable.getBuiltName()) != null) {
                currentState = State.REMOVING;
            }
        }
    }

    private boolean buildLarder() {
        Widget creationMenu = Rs2Widget.findWidget("Furniture Creation Menu");
        if (creationMenu == null) {
            return false;
        }

        Widget oakLarder = Rs2Widget.findWidget("<col=ff9040>Oak larder</col>");
        if (oakLarder == null) {
            return false;
        }

        if (!Rs2Widget.clickWidget("<col=ff9040>Oak larder</col>")) {
            return false;
        }

        Widget chatMessage = Rs2Widget.findWidget("You don't have the right materials");
        if (chatMessage != null) {
            Microbot.log("Out of materials. Shutting down plugin.");
            try {
                shutDown();
            } catch (Exception e) {
                Microbot.log("Error shutting down plugin: " + e.getMessage());
            }
        }

        if (Rs2GameObject.findObjectByName("Oak larder") != null) {
            Microbot.log("Successfully built: Oak larder");
            currentState = State.REMOVING;
            return true;
        } else {
            Microbot.log("Failed to build: Oak larder");
            return false;
        }
    }

    private void removeFurniture() {
        if (Rs2Player.isMoving() || Rs2Player.isAnimating()) {
            return;
        }

        Buildables selectedBuildable = config.buildable();
        if (Rs2GameObject.interact(selectedBuildable.getBuiltId(), "Remove")) {
            Global.sleepUntil(() -> Rs2GameObject.findObjectById(selectedBuildable.getHotspotId()) != null, Random.random(3000, 5000));
            currentState = State.BUILDING;
        }
    }

    private boolean isInPlayerHouse() {
        return Rs2GameObject.findObjectById(4525) != null;
    }

    private boolean isInBuildMode() {
        return client.getVarbitValue(2176) == 1;
    }

    private void enterBuildMode() {
        if (!Rs2Player.isMoving() && !Rs2Player.isAnimating()) {
            if (Rs2GameObject.interact("Portal", "Build mode")) {
                Global.sleepUntil(this::isInBuildMode, Random.random(3000, 5000));
            }
        }
    }

    @Provides
    CLConstructionConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CLConstructionConfig.class);
    }
}