package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@PluginDescriptor(
        name = PluginDescriptor.eXioStorm + "Action Hotkey",
        description = "Storm's Action Hotkey plugin",
        tags = {"action", "hotkey", "microbot", "storm", "eXiostorm"},
        enabledByDefault = false
)
@Slf4j
public class actionHotkeyPlugin extends Plugin {
    public static int previousKey = 0;
    @Inject
    private actionHotkeyConfig config;
    @Provides
    actionHotkeyConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(actionHotkeyConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private actionHotkeyOverlay actionHotkeyOverlay;

    @Inject
    actionHotkeyScript actionHotkeyScript;

    @Inject
    private KeyManager keyManager;


    // This boolean variable keeps track of whether the hotkey is currently pressed
    private boolean isKey1Pressed = false;
    private boolean isKey2Pressed = false;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
        }
        keyManager.registerKeyListener(hotkeyListener);
        actionHotkeyScript.run(config);
    }
    protected void shutDown() {
        keyManager.unregisterKeyListener(hotkeyListener);
        actionHotkeyScript.shutdown();
    }
    private final KeyListener hotkeyListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == config.key1().getKeyCode()) {
                    if (!isKey1Pressed) {
                        isKey1Pressed = true;
                        actionHotkeyScript.key1isdown = true;
                    }
                } else if (e.getKeyCode() == config.key2().getKeyCode()) {
                    if (!isKey2Pressed) {
                        isKey2Pressed = true;
                        actionHotkeyScript.key2isdown = true;
                    }
                }
                if (e.getKeyCode() != previousKey) {
                    previousKey = e.getKeyCode();
                }
                actionHotkeyScript.previousKey = e.getKeyCode();
                if(config.toggle()) {
                    actionHotkeyScript.toggled = !actionHotkeyScript.toggled;
                }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Check if the key released is the hotkey we are tracking
            if (e.getKeyCode() == config.key1().getKeyCode()) {
                if(actionHotkeyScript.alternating){ actionHotkeyScript.alternating = false;}
                isKey1Pressed = false; // Mark the key as not pressed
                actionHotkeyScript.key1isdown = false;
            }
            if (e.getKeyCode() == config.key2().getKeyCode()) {
                if(actionHotkeyScript.alternating){ actionHotkeyScript.alternating = false;}
                isKey2Pressed = false;
                actionHotkeyScript.key2isdown = false;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            // Not used
        }
    };
}
