package net.runelite.client.plugins.microbot.util.mouse.naturalmouse.support;

import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.api.MouseInfoAccessor;

import java.awt.*;

public class DefaultMouseInfoAccessor implements MouseInfoAccessor {

    @Override
    public Point getMousePosition() {
        return MouseInfo.getPointerInfo().getLocation();
    }
}
