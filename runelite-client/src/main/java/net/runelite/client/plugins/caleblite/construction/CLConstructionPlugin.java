package net.runelite.client.plugins.caleblite.construction;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.api.events.GameTick;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import javax.inject.Inject;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.microbot.util.math.Random;

@PluginDescriptor(
        name = "CL Construction",
        description = "Automated construction training plugin",
        tags = {"construction", "skilling"}
)
public class CLConstructionPlugin extends Plugin {

    @Inject
    private CLConstructionConfig config;

    @Provides
    CLConstructionConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CLConstructionConfig.class);
    }

    @Inject
    private ConfigManager configManager;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    private ConstructionState state = ConstructionState.IDLE;
    private int tickCounter = 0;
    private int ticksUntilRemove = 0;

    @Override
    protected void startUp() throws Exception {
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.log("CL Construction plugin started!");
        state = ConstructionState.IDLE;
    }

    @Override
    protected void shutDown() throws Exception {
        Microbot.log("CL Construction plugin stopped!");
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!config.enablePlugin()) {
            return;
        }

        tickCounter++;
        updateState();
        performAction();
    }

    private void updateState() {
        if (state == ConstructionState.REMOVE && ticksUntilRemove > 0) {
            ticksUntilRemove--;
            return;
        }

        if (!hasRequiredMaterials()) {
            state = ConstructionState.CALL_BUTLER;
        } else if (isFurnitureBuilt()) {
            state = ConstructionState.REMOVE;
        } else {
            state = ConstructionState.BUILD;
        }
    }

    private void performAction() {
        switch (state) {
            case BUILD:
                buildFurniture();
                break;
            case REMOVE:
                removeFurniture();
                break;
            case CALL_BUTLER:
                callButler();
                break;
            case IDLE:
                break;
        }
    }

    private void buildFurniture() {
        if (!isPlayerAtBuildLocation()) {
            walkToBuildLocation();
            return;
        }

        GameObject buildSpace = findBuildSpace();
        if (buildSpace == null) {
            Microbot.log("Cannot find build space for " + config.furnitureType());
            return;
        }

        Rs2GameObject.interact(buildSpace, "Build");

        if (!Global.sleepUntilTrue(this::checkForBuildMenu, 100, Random.random(3000, 5000))) {
            Microbot.log("Build menu did not appear");
            return;
        }

        selectFurnitureFromMenu();
        waitForBuildingToComplete();

        if (isFurnitureBuilt()) {
            ticksUntilRemove = config.ticksBetweenActions();
            state = ConstructionState.REMOVE;
        } else {
            Microbot.log("Failed to build " + config.furnitureType());
        }
    }

    private void removeFurniture() {
        GameObject furniture = (GameObject) Rs2GameObject.findObjectById(config.furnitureType().getBuiltId());
        if (furniture == null) {
            Microbot.log("Cannot find built furniture to remove");
            return;
        }

        Rs2GameObject.interact(furniture, "Remove");

        if (!Global.sleepUntilTrue(this::checkForRemovePrompt, 100, Random.random(3000, 5000))) {
            Microbot.log("Remove prompt did not appear");
            return;
        }

        confirmRemove();
        waitForRemovalToComplete();

        if (!isFurnitureBuilt()) {
            state = ConstructionState.BUILD;
        } else {
            Microbot.log("Failed to remove " + config.furnitureType());
        }
    }

    private void callButler() {
        Microbot.log("Calling butler - not implemented yet");
    }

    private boolean isPlayerAtBuildLocation() {
        WorldPoint buildTile = findBuildTile();
        return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(buildTile) <= 1;
    }

    private void walkToBuildLocation() {
        WorldPoint buildTile = findBuildTile();
        Rs2Walker.walkTo(buildTile);
    }

    private WorldPoint findBuildTile() {
        GameObject buildSpace = findBuildSpace();
        if (buildSpace == null) {
            return null;
        }

        WorldPoint buildSpaceLocation = buildSpace.getWorldLocation();
        WorldPoint[] adjacentTiles = new WorldPoint[] {
                buildSpaceLocation.dx(1),
                buildSpaceLocation.dx(-1),
                buildSpaceLocation.dy(1),
                buildSpaceLocation.dy(-1)
        };

        for (WorldPoint tile : adjacentTiles) {
            if (Rs2Walker.canReach(tile)) {
                return tile;
            }
        }

        return null;
    }

    private GameObject findBuildSpace() {
        return (GameObject) Rs2GameObject.findObjectById(config.furnitureType().getBuildSpaceId());
    }

    private boolean hasRequiredMaterials() {
        return Rs2Inventory.hasItemAmount(config.furnitureType().getRequiredPlank(), config.furnitureType().getRequiredAmount());
    }

    private boolean checkForBuildMenu() {
        return Rs2Widget.findWidget("What would you like to build?") != null;
    }

    private void selectFurnitureFromMenu() {
        Rs2Widget.clickWidget(config.furnitureType().getMenuOption());
    }

    private void waitForBuildingToComplete() {
        Global.sleepUntil(() -> Microbot.getClient().getLocalPlayer().getAnimation() == -1, Random.random(3000, 5000));
    }

    private boolean isFurnitureBuilt() {
        return Rs2GameObject.findObjectById(config.furnitureType().getBuiltId()) != null;
    }

    private boolean checkForRemovePrompt() {
        return Rs2Widget.findWidget("Are you sure you want to remove this?") != null;
    }

    private void confirmRemove() {
        Rs2Widget.clickWidget("Yes");
    }

    private void waitForRemovalToComplete() {
        Global.sleepUntil(() -> Microbot.getClient().getLocalPlayer().getAnimation() == -1, Random.random(3000, 5000));
    }

    private enum ConstructionState {
        IDLE, BUILD, REMOVE, CALL_BUTLER
    }
}