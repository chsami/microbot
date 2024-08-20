package net.runelite.client.plugins.microbot.util.mouse.naturalmouse.support;


import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.mouse.naturalmouse.api.SystemCalls;

import java.awt.*;

public class DefaultSystemCalls implements SystemCalls {
    private final Robot robot;

    public DefaultSystemCalls(Robot robot) {
        this.robot = robot;
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public void sleep(long time) {
        Global.sleep((int) time);
    }

    @Override
    public Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    /**
     * <p>Moves the mouse to specified pixel using the provided Robot.</p>
     *
     * <p>It seems there is a certain delay, measurable in less than milliseconds,
     * before the mouse actually ends up on the requested pixel when using a Robot class.
     * this usually isn't a problem, but when we ask the mouse position right after this call,
     * there's extremely low but real chance we get wrong information back. I didn't add sleep
     * here as it would cause overhead to sleep always, even when we don't instantly use
     * the mouse position, but just acknowledged the issue with this warning.
     * (Use fast unrestricted loop of Robot movement and checking the position after every move to invoke the issue.)</p>
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    @Override
    public void setMousePosition(int x, int y) {
        robot.mouseMove(x, y);
    }
}
