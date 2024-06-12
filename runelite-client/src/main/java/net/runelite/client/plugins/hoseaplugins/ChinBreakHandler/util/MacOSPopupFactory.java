package net.runelite.client.plugins.ChinBreakHandler.util;

import javax.swing.*;
import java.awt.*;

class MacOSPopupFactory extends PopupFactory
{
    @Override
    protected Popup getPopup(Component owner, Component contents, int x, int y, boolean isHeavyWeightPopup) throws IllegalArgumentException
    {
        return super.getPopup(owner, contents, x, y, true);
    }
}