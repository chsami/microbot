package net.runelite.client.plugins.microbot;

import net.runelite.api.GameObject;
import net.runelite.api.WallObject;

public interface IScript {
    boolean run();
    IScript click(GameObject gameObject);
    IScript click(WallObject gameObject);
}
