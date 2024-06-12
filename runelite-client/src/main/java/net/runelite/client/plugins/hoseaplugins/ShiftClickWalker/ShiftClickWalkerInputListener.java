package net.runelite.client.plugins.hoseaplugins.ShiftClickWalker;

import java.awt.event.KeyEvent;
import com.google.inject.Inject;
import net.runelite.client.input.KeyListener;

public class ShiftClickWalkerInputListener
        implements KeyListener {
    @Inject
    private ShiftClickWalkerPlugin plugin;
    @Inject
    private ShiftClickWalkerConfig config;

    public void keyTyped(KeyEvent event) {
    }

    public void keyPressed(KeyEvent event) {
        if (this.config.hotkey().matches(event)) {
            this.plugin.setHotKeyPressed(true);
        }
    }

    public void keyReleased(KeyEvent event) {
        if (this.config.hotkey().matches(event)) {
            this.plugin.setHotKeyPressed(false);
        }
    }
}