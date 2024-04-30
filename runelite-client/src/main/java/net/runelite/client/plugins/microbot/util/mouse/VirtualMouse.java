package net.runelite.client.plugins.microbot.util.mouse;

import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.math.Random;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class VirtualMouse extends Mouse {


    private final ScheduledExecutorService scheduledExecutorService;

    @Inject
    public VirtualMouse() {
        super();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(10);
        getCanvas().setFocusable(false);
    }

    public Mouse click(Point point, boolean rightClick) {

        if (point == null) return this;

        mouseEvent(MouseEvent.MOUSE_ENTERED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_EXITED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_MOVED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_PRESSED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_RELEASED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_FIRST, point, rightClick);

        return this;
    }

    public Mouse click(int x, int y) {
        return click(new Point(x, y), false);
    }

    public Mouse click(double x, double y) {
        return click(new Point((int) x, (int) y), false);
    }

    public Mouse click(Rectangle rectangle) {
        return click(new Point((int) rectangle.getCenterX() , (int) rectangle.getCenterY()), false);
    }

    @Override
    public Mouse click(int x, int y, boolean rightClick) {
        return click(new Point(x, y), rightClick);
    }

    @Override
    public Mouse click(Point point) {
        return click(point, false);
    }

    @Override
    public Mouse click() {
        return click(new Point((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY()));
    }

    public Mouse move(Point point) {
        long time = System.currentTimeMillis();

        MouseEvent mouseMove = new MouseEvent(getCanvas(), MouseEvent.MOUSE_MOVED, time, 0, point.getX(), point.getY(), 1, false, MouseEvent.BUTTON1);

        getCanvas().dispatchEvent(mouseMove);

        return this;
    }

    public Mouse move(Rectangle rect) {
        long time = System.currentTimeMillis();

        MouseEvent mouseMove = new MouseEvent(getCanvas(), MouseEvent.MOUSE_MOVED, time, 0, (int) rect.getCenterX(), (int) rect.getCenterY(), 1, false, MouseEvent.BUTTON1);

        getCanvas().dispatchEvent(mouseMove);

        return this;
    }

    public Mouse move(Polygon polygon) {
        long time = System.currentTimeMillis();
        Point point = new Point((int) polygon.getBounds().getCenterX(), (int) polygon.getBounds().getCenterY());

        MouseEvent mouseMove = new MouseEvent(getCanvas(), MouseEvent.MOUSE_MOVED, time, 0, point.getX(), point.getY(), 1, false, MouseEvent.BUTTON1);

        getCanvas().dispatchEvent(mouseMove);


        return this;
    }

    public Mouse scrollDown(Point point) {
        long time = System.currentTimeMillis();

        move(point);

        scheduledExecutorService.schedule(() -> {
            MouseEvent mouseScroll = new MouseWheelEvent(getCanvas(), MouseEvent.MOUSE_WHEEL, time, 0, point.getX(), point.getY(), 0, false,
                    0, 10, 2);

        getCanvas().dispatchEvent(mouseScroll);


        }, random(40, 100), TimeUnit.MILLISECONDS);
        return this;
    }

    public Mouse scrollUp(Point point) {
        long time = System.currentTimeMillis();

        MouseEvent mouseScroll = new MouseWheelEvent(getCanvas(), MouseEvent.MOUSE_WHEEL, time, 0, point.getX(), point.getY(), 0, false,
                0, -10, -2);

        getCanvas().dispatchEvent(mouseScroll);

        return this;
    }

    @Override
    public java.awt.Point getMousePosition() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();

        return pointerInfo != null ? pointerInfo.getLocation() : null;
    }

    @Override
    public Mouse move(int x, int y) {
        return move(new Point(x, y));
    }
    @Override
    public Mouse move(double x, double y) {
        return move(new Point((int) x, (int) y));
    }

    private void mouseEvent(int id, Point point, boolean rightClick)
    {
        int button = rightClick ? MouseEvent.BUTTON3 : MouseEvent.BUTTON1;

        MouseEvent e = new MouseEvent(Microbot.getClient().getCanvas(), id, System.currentTimeMillis(), 0, point.getX(), point.getY(), 1, false, button);

        getCanvas().dispatchEvent(e);
    }
}
