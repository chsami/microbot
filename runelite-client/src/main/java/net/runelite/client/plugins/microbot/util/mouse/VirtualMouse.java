package net.runelite.client.plugins.microbot.util.mouse;

import net.runelite.api.Point;

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
    }

    public Mouse click(Point point, boolean rightClick) {

        if (point == null) return this;

        mouseEvent(MouseEvent.MOUSE_MOVED, point, rightClick);
        sleep(200, 300);
        mouseEvent(MouseEvent.MOUSE_PRESSED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_RELEASED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_CLICKED, point, rightClick);

        mousePositions.add(point);
        return this;
    }

    public Mouse clickFast(Point point, boolean rightClick) {

        mouseEvent(MouseEvent.MOUSE_MOVED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_PRESSED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_RELEASED, point, rightClick);
        mouseEvent(MouseEvent.MOUSE_CLICKED, point, rightClick);

        mousePositions.add(point);
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
    public Mouse click(Point point) {
        return click(point, false);
    }

    @Override
    public Mouse clickFast(Point point) {
        return clickFast(point, false);
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

    public Mouse move(Point point) {
        long time = System.currentTimeMillis();

        MouseEvent mouseMove = new MouseEvent(getCanvas(), MouseEvent.MOUSE_MOVED, time, 0, point.getX(), point.getY(), 1, false, MouseEvent.BUTTON1);
        getCanvas().dispatchEvent(mouseMove);

        mousePositions.add(point);
        return this;
    }

    public Mouse move(Rectangle rect) {
        long time = System.currentTimeMillis();

        MouseEvent mouseMove = new MouseEvent(getCanvas(), MouseEvent.MOUSE_MOVED, time, 0, (int) rect.getCenterX(), (int) rect.getCenterY(), 1, false, MouseEvent.BUTTON1);
        getCanvas().dispatchEvent(mouseMove);

        mousePositions.add(new Point((int) rect.getCenterX(), (int) rect.getCenterY()));
        return this;
    }

    public Mouse move(Polygon polygon) {
        long time = System.currentTimeMillis();
        Point point = new Point((int) polygon.getBounds().getCenterX(), (int) polygon.getBounds().getCenterY());

        MouseEvent mouseMove = new MouseEvent(getCanvas(), MouseEvent.MOUSE_MOVED, time, 0, point.getX(), point.getY(), 1, false, MouseEvent.BUTTON1);
        getCanvas().dispatchEvent(mouseMove);

        mousePositions.add(point);
        return this;
    }

    public Mouse scrollDown(Point point) {
        long time = System.currentTimeMillis();

        move(point);

        scheduledExecutorService.schedule(() -> {
            MouseEvent mouseScroll = new MouseWheelEvent(getCanvas(), MouseEvent.MOUSE_WHEEL, time, 0, point.getX(), point.getY(), 0, false,
                    0, 10, 2);

            getCanvas().dispatchEvent(mouseScroll);

            mousePositions.add(point);
        }, random(40, 100), TimeUnit.MILLISECONDS);
        return this;
    }

    public Mouse scrollUp(Point point) {
        long time = System.currentTimeMillis();

        MouseEvent mouseScroll = new MouseWheelEvent(getCanvas(), MouseEvent.MOUSE_WHEEL, time, 0, point.getX(), point.getY(), 0, false,
                0, -10, -2);

        getCanvas().dispatchEvent(mouseScroll);

        mousePositions.add(point);

        return this;
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

        MouseEvent e = new MouseEvent(
                getCanvas(), id,
                System.currentTimeMillis(),
                0, point.getX(), point.getY(),
                1, false, button
        );

        getCanvas().dispatchEvent(e);
    }
}
