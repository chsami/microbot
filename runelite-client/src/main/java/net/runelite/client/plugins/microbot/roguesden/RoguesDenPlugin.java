package net.runelite.client.plugins.microbot.roguesden;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashMap;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Rogues' Den",
        description = "Mark tiles and clickboxes to help traverse the maze",
        tags = {"agility", "maze", "minigame", "overlay", "thieving", "microbot", "rogue", "den"},
        enabledByDefault = false
)
public class RoguesDenPlugin extends Plugin {
    private final HashMap<TileObject, Tile> obstaclesHull = new HashMap();
    private final HashMap<TileObject, Tile> obstaclesTile = new HashMap();
    private boolean hasGem;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private RoguesDenOverlay overlay;
    @Inject
    private RoguesDenMinimapOverlay minimapOverlay;
    @Inject
    private RoguesDenConfig roguesDenConfig;
    @Provides
    RoguesDenConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(RoguesDenConfig.class);
    }
    @Inject
    RoguesDenScript roguesDenScript;

    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.minimapOverlay);

        roguesDenScript.run();
    }

    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.minimapOverlay);
        this.obstaclesHull.clear();
        this.obstaclesTile.clear();
        this.hasGem = false;
        roguesDenScript.shutdown();
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getItemContainer() == this.client.getItemContainer(InventoryID.INVENTORY)) {
            Item[] var2 = event.getItemContainer().getItems();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Item item = var2[var4];
                if (item.getId() == 5561) {
                    this.hasGem = true;
                    return;
                }
            }

            this.hasGem = false;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOADING) {
            this.obstaclesHull.clear();
            this.obstaclesTile.clear();
        }

    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        this.onTileObject(event.getTile(), (TileObject)null, event.getGameObject());
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        this.onTileObject(event.getTile(), event.getGameObject(), (TileObject)null);
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        this.onTileObject(event.getTile(), (TileObject)null, event.getGroundObject());
    }

    @Subscribe
    public void onGroundObjectDespawned(GroundObjectDespawned event) {
        this.onTileObject(event.getTile(), event.getGroundObject(), (TileObject)null);
    }

    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event) {
        this.onTileObject(event.getTile(), (TileObject)null, event.getWallObject());
    }

    @Subscribe
    public void onWallObjectDespawned(WallObjectDespawned event) {
        this.onTileObject(event.getTile(), event.getWallObject(), (TileObject)null);
    }

    @Subscribe
    public void onDecorativeObjectSpawned(DecorativeObjectSpawned event) {
        this.onTileObject(event.getTile(), (TileObject)null, event.getDecorativeObject());
    }

    @Subscribe
    public void onDecorativeObjectDespawned(DecorativeObjectDespawned event) {
        this.onTileObject(event.getTile(), event.getDecorativeObject(), (TileObject)null);
    }

    private void onTileObject(Tile tile, TileObject oldObject, TileObject newObject) {
        this.obstaclesHull.remove(oldObject);
        if (newObject != null) {
            WorldPoint point = tile.getWorldLocation();
            Obstacles.Obstacle obstacle = (Obstacles.Obstacle) Obstacles.TILE_MAP.get(point);
            if (obstacle != null && obstacle.getObjectId() == newObject.getId()) {
                this.obstaclesHull.put(newObject, tile);
            }
        }

    }

    HashMap<TileObject, Tile> getObstaclesHull() {
        return this.obstaclesHull;
    }

    HashMap<TileObject, Tile> getObstaclesTile() {
        return this.obstaclesTile;
    }

    boolean isHasGem() {
        return this.hasGem;
    }
}
