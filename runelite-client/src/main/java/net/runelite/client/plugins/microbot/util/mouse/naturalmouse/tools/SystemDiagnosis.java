package net.runelite.client.plugins.microbot.util.mouse.naturalmouse.tools;


import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.api.MouseInfoAccessor;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.api.SystemCalls;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.support.DefaultMouseInfoAccessor;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.support.DefaultSystemCalls;

import java.awt.*;

public class SystemDiagnosis {

    /**
     * Runs a diagnosis with default configuration, by setting mouse all over your screen and expecting to receive
     * correct coordinates back.
     * If java.awt.Robot cannot be constructed, then new RuntimeException is thrown.
     * If no issues are found, then this method completes without throwing an error, otherwise IllegalStateException is
     * thrown.
     */
    public static void validateMouseMovement() {
        try {
            Robot robot = new Robot();
            validateMouseMovement(new DefaultSystemCalls(robot), new DefaultMouseInfoAccessor());
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Runs a diagnosis, by setting mouse all over your screen and expecting to receive correct coordinates back.
     * If no issues are found, then this method completes without throwing an error, otherwise IllegalStateException is
     * thrown.
     *
     * @param system   a SystemCalls class which is used for setting the mouse position
     * @param accessor a MouseInfoAccessor which is used for querying mouse position
     */
    public static void validateMouseMovement(SystemCalls system, MouseInfoAccessor accessor) {
        Dimension dimension = system.getScreenSize();
        for (int y = 0; y < dimension.height; y += 50) {
            for (int x = 0; x < dimension.width; x += 50) {
                system.setMousePosition(x, y);

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Point p = accessor.getMousePosition();
                if (x != p.x || y != p.y) {
                    throw new IllegalStateException(
                            "Tried to move mouse to (" + x + ", " + y + "). Actually moved to (" + p.x + ", " + p.y + ")" +
                                    "This means NaturalMouseMotion is not able to work optimally on this system as the cursor move " +
                                    "calls may miss the target pixels on the screen."
                    );
                }
            }
        }
    }
}
