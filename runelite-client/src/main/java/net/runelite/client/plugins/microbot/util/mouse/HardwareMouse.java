package net.runelite.client.plugins.microbot.util.mouse;

import com.github.joonasvali.naturalmouse.api.MouseMotionFactory;
import com.github.joonasvali.naturalmouse.api.SystemCalls;
import com.github.joonasvali.naturalmouse.support.DefaultSystemCalls;
import com.github.joonasvali.naturalmouse.support.MouseMotionNature;
import com.github.joonasvali.naturalmouse.util.FactoryTemplates;
import net.runelite.api.Point;
import org.apache.commons.lang3.NotImplementedException;

import java.awt.*;
import java.awt.event.InputEvent;

import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class HardwareMouse extends Mouse {

    MouseMotionFactory factory;
    Robot robot;

    public HardwareMouse() {
        super();
        factory = FactoryTemplates.createAverageComputerUserMotionFactory();
        MouseMotionNature nature = factory.getNature();
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        SystemCalls systemCalls = new DefaultSystemCalls(robot);
        nature.setSystemCalls(systemCalls);
    }

    @Override
    public Mouse click(int x, int y) {
        return click(new Point(x, y));
    }

    @Override
    public Mouse click(double x, double y) {
        return click(new Point((int) x, (int) y));
    }

    @Override
    public Mouse click(Rectangle rectangle) {
        return click(new Point((int) rectangle.getCenterX(), (int) rectangle.getCenterY()));
    }

    @Override
    public Mouse click(Point point) {
        return click(point, false);
    }

    @Override
    public Mouse click(Point point, boolean rightClick) {
        Robot robot;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        final int randomizer = random(20, 80);
        try {
            factory.move(point.getX(), point.getY() + 20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        int mouseType = rightClick ? InputEvent.BUTTON3_DOWN_MASK : InputEvent.BUTTON1_DOWN_MASK;
        robot.mousePress(mouseType);
        robot.setAutoDelay(randomizer);
        robot.mouseRelease(mouseType);
        mousePositions.add(point);
        return this;
    }

    @Override
    public Mouse click() {
        return click(new Point((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY()));
    }

    @Override
    public Mouse rightClick(Point point) {
        return click(point, true);
    }

    @Override
    public Mouse rightClick() {
        return click(new Point(getLastMousePosition().getX(), getLastMousePosition().getY()), true);
    }

    @Override
    public Mouse move(Point point) {
        try {
            factory.move(point.getX(), point.getY());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            return this;
        }
    }

    @Override
    public Mouse move(int x, int y) {
        return move(new Point(x, y));
    }

    @Override
    public Mouse scrollDown(Point point) {
        throw new NotImplementedException("");
    }
    @Override
    public Mouse scrollUp(Point point) {
        throw new NotImplementedException("");
    }
    @Override
    public Mouse move(double x, double y) {
        return move(new Point((int) x, (int) y));
    }

    @Override
    public Mouse move(Polygon polygon) {
        return null;
    }
}
