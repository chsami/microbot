package net.runelite.client.plugins.microbot.qualityoflife.scripts;

import net.runelite.api.events.GameTick;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.awt.event.KeyEvent;

public class NeverLogoutScript {
    private static long randomDelay = Rs2Random.between(1000,3000);

    public static void onGameTick(GameTick event) {
        if (Rs2Player.checkIdleLogout(randomDelay)) {
            randomDelay = Rs2Random.between(1000,3000);
            Rs2Keyboard.keyPress(KeyEvent.VK_BACK_SPACE);
        }
    }
}
