package net.runelite.client.plugins.microbot.util.camera;

import net.runelite.api.Actor;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Calculations;

import java.awt.event.KeyEvent;

import static net.runelite.client.plugins.microbot.util.math.Calculations.angleToTile;

public class Camera extends Script {

    public static void turnTo(final Actor actor) {
        int angle = getCharacterAngle(actor);
        setAngle(angle, actor);
    }

    public static void turnTo(final TileObject tileObject) {
        int angle = getObjectAngle(tileObject);
        setAngle(angle, tileObject);
    }

    public static void turnTo(final LocalPoint localPoint) {
        int angle = angleToTile(localPoint);
        setAngle(angle, localPoint);
    }

    public static int getCharacterAngle(Actor actor) {
        return getTileAngle(actor);
    }

    public static int getObjectAngle(TileObject tileObject) {
        return getTileAngle(tileObject);
    }

    public static int getTileAngle(Actor actor) {
        int a = (angleToTile(actor) - 90) % 360;
        return a < 0 ? a + 360 : a;
    }

    public static int getTileAngle(TileObject tileObject) {
        int a = (angleToTile(tileObject) - 90) % 360;
        return a < 0 ? a + 360 : a;
    }

    public static void setAngle(int degrees) {
        if (getAngleTo(degrees) > 5) {
            VirtualKeyboard.keyHold(KeyEvent.VK_LEFT);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_LEFT),
                    () -> Math.abs(getAngleTo(degrees)) <= 5, 10);
        } else if (getAngleTo(degrees) < -5) {
            VirtualKeyboard.keyHold(KeyEvent.VK_RIGHT);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_RIGHT),
                    () -> Math.abs(getAngleTo(degrees)) <= 5, 10);
        }
    }


    public static void setAngle(int degrees, Actor actor) {
        if (getAngleTo(degrees) > 5) {
            VirtualKeyboard.keyHold(KeyEvent.VK_LEFT);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_LEFT),
                    () -> Calculations.tileOnScreen(actor), 10);
        } else if (getAngleTo(degrees) < -5) {
            VirtualKeyboard.keyHold(KeyEvent.VK_RIGHT);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_RIGHT),
                    () -> Calculations.tileOnScreen(actor), 10);
        }
    }

    public static void setAngle(int degrees, TileObject tileObject) {
        if (getAngleTo(degrees) > 5) {
            VirtualKeyboard.keyHold(KeyEvent.VK_LEFT);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_LEFT),
                    () -> Calculations.tileOnScreen(tileObject), 600);
        } else if (getAngleTo(degrees) < -5) {
            VirtualKeyboard.keyHold(KeyEvent.VK_RIGHT);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_RIGHT),
                    () -> Calculations.tileOnScreen(tileObject), 600);
        }
    }

    public static void setAngle(int degrees, LocalPoint localPoint) {
        if (getAngleTo(degrees) > 5) {
            VirtualKeyboard.keyHold(KeyEvent.VK_LEFT);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_LEFT),
                    () -> Calculations.tileOnScreen(localPoint), 600);
        } else if (getAngleTo(degrees) < -5) {
            VirtualKeyboard.keyHold(KeyEvent.VK_RIGHT);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_RIGHT),
                    () -> Calculations.tileOnScreen(localPoint), 600);
        }
    }

    public static void setPitch(float percentage) {
        float currentPitchPercentage = cameraPitchPercentage();

        if (currentPitchPercentage < percentage) {
            VirtualKeyboard.keyHold(KeyEvent.VK_UP);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_UP),
                    () -> cameraPitchPercentage() >= percentage, 600);
        } else {
            VirtualKeyboard.keyHold(KeyEvent.VK_RIGHT);
            Global.awaitExecutionUntil(() -> VirtualKeyboard.keyRelease((char) KeyEvent.VK_RIGHT),
                    () -> cameraPitchPercentage() <= percentage, 600);
        }
    }

    private static float cameraPitchPercentage() {
        int minPitch = 128;
        int maxPitch = 383;
        int currentPitch = Microbot.getClient().getCameraPitch();

        int adjustedPitch = currentPitch - minPitch;
        int adjustedMaxPitch = maxPitch - minPitch;

        return (float)adjustedPitch / (float)adjustedMaxPitch;
    }

    public static int getAngleTo(int degrees) {
        int ca = getAngle();
        if (ca < degrees) {
            ca += 360;
        }
        int da = ca - degrees;
        if (da > 180) {
            da -= 360;
        }
        return da;
    }

    public static int getAngle() {
        // the client uses fixed point radians 0 - 2^14
        // degrees = yaw * 360 / 2^14 = yaw / 45.5111...
        // This leaves it on a scale of 45 versus a scale of 360 so we multiply it by 8 to fix that.
        return (int) Math.abs(Microbot.getClient().getCameraYaw() / 45.51 * 8);
    }

    public static boolean isTileOnScreen(TileObject tileObject) {
        return Calculations.tileOnScreen(tileObject);
    }

    public static boolean isTileOnScreen(LocalPoint localPoint) {
        return Calculations.tileOnScreen(localPoint);
    }

    @Override
    public boolean run() {
        return true;
    }
}
