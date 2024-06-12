package net.runelite.client.plugins.hoseaplugins.ShiftClickWalker;

import com.google.inject.Provides;
import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(name="<html><font color=\"#FF9DF9\">[PP]</font> Spoon Walk Under</html>", description="Use the hotkey to toggle the Walk Here menu option. While pressed you will Walk rather than interact with objects.", tags={"npcs", "items", "objects", "shift", "walk", "under", "walker"}, enabledByDefault=false)
public class ShiftClickWalkerPlugin
        extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(ShiftClickWalkerPlugin.class);
    @Inject
    private Client client;
    @Inject
    private ShiftClickWalkerInputListener inputListener;
    @Inject
    private ConfigManager configManager;
    @Inject
    private KeyManager keyManager;
    private boolean hotKeyPressed = false;
    private final Point invalidMouseLocation = new Point(-1, -1);

    @Provides
    ShiftClickWalkerConfig getConfig(ConfigManager configManager) {
        return (ShiftClickWalkerConfig)configManager.getConfig(ShiftClickWalkerConfig.class);
    }

    public void startUp() {
        this.keyManager.registerKeyListener(this.inputListener);
    }

    public void shutDown() {
        this.keyManager.unregisterKeyListener(this.inputListener);
    }

    @Subscribe
    public void onFocusChanged(FocusChanged event) {
        if (!event.isFocused()) {
            this.hotKeyPressed = false;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.HOPPING) {
            this.hotKeyPressed = false;
        }
    }

    @Subscribe(priority=-1.0f)
    public void onClientTick(ClientTick event) {
        if (this.client.getGameState() == GameState.LOGGED_IN && !this.client.isMenuOpen()) {
            Point mousePosition = this.client.getMouseCanvasPosition();
            if (mousePosition.equals(this.invalidMouseLocation)) {
                this.hotKeyPressed = false;
            }
            if (this.hotKeyPressed) {
                MenuEntry[] entries = this.client.getMenuEntries();
                int entryIndex = -1;
                for (int i = 0; i < entries.length; ++i) {
                    MenuEntry entry = entries[i];
                    int opId = entry.getType().getId();
                    if (opId >= 2000) {
                        opId -= 2000;
                    }
                    if (opId != MenuAction.WALK.getId()) continue;
                    entryIndex = i;
                }
                if (entryIndex < 0) {
                    return;
                }
                for (MenuEntry menuEntry : entries) {
                    if (menuEntry.getType().getId() >= MenuAction.WALK.getId()) continue;
                    menuEntry.setDeprioritized(true);
                }
                MenuEntry first = entries[entries.length - 1];
                entries[entries.length - 1] = entries[entryIndex];
                entries[entryIndex] = first;
                this.client.setMenuEntries(entries);
            }
        }
    }

    @Subscribe(priority=-1.0f)
    public void onMenuEntryAdded(MenuEntryAdded event) {
        if (this.client.getGameState() == GameState.LOGGED_IN && this.hotKeyPressed) {
            boolean hasWalkHere = false;
            for (MenuEntry menuEntry : this.client.getMenuEntries()) {
                int opId = menuEntry.getType().getId();
                if (opId >= 2000) {
                    opId -= 2000;
                }
                hasWalkHere |= opId == MenuAction.WALK.getId();
            }
            if (!hasWalkHere) {
                return;
            }
            if (event.getType() < MenuAction.WALK.getId()) {
                this.deprioritizeEntry(event.getIdentifier(), event.getType());
            }
        }
    }

    private void deprioritizeEntry(int id, int op_id) {
        MenuEntry[] menuEntries = this.client.getMenuEntries();
        for (int i = menuEntries.length - 1; i >= 0; --i) {
            MenuEntry entry = menuEntries[i];
            if (entry.getType().getId() != op_id || entry.getIdentifier() != id) continue;
            entry.setDeprioritized(true);
            menuEntries[i] = menuEntries[menuEntries.length - 1];
            menuEntries[menuEntries.length - 1] = entry;
            this.client.setMenuEntries(menuEntries);
            break;
        }
    }

    public void setHotKeyPressed(boolean hotKeyPressed) {
        this.hotKeyPressed = hotKeyPressed;
    }
}