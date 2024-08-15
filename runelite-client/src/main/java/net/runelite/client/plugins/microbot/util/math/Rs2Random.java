package net.runelite.client.plugins.microbot.util.math;

import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.util.Global;

import java.awt.*;
import java.util.Random;

public class Rs2Random {

    private static final double GAUSS_CUTOFF = 4.0;
    private static final Random RANDOM = new Random();

    // Non-zero random
    public static double nzRandom() {
        return Math.max(RANDOM.nextDouble(), 1.0e-320);
    }

    // Generates a random gaussian/normal number
    public static double gaussRand(double mean, double dev) {
        double len = dev * Math.sqrt(-2 * Math.log(nzRandom()));
        return mean + len * Math.cos(2 * Math.PI * RANDOM.nextDouble());
    }

    // Generates a truncated gaussian/normal number within the given range
    public static double truncatedGauss(double left, double right, double cutoff) {
        if (cutoff <= 0) {
            cutoff = GAUSS_CUTOFF;
        }

        double result;
        do {
            result = Math.abs(Math.sqrt(-2 * Math.log(nzRandom())) * Math.cos(2 * Math.PI * RANDOM.nextDouble()));
        } while (result >= cutoff);

        return result / cutoff * (right - left) + left;
    }

    public static long truncatedGauss(long left, long right, double cutoff) {
        return Math.round(truncatedGauss((double) left, (double) right, cutoff));
    }

    // Random skewed distribution generation
    public static double skewedRand(double mode, double lo, double hi, double cutoff) {
        if (cutoff <= 0) {
            cutoff = GAUSS_CUTOFF;
        }

        double top = lo;
        if (RANDOM.nextDouble() * (hi - lo) > mode - lo) {
            top = hi;
        }

        double result;
        do {
            result = Math.abs(Math.sqrt(-2 * Math.log(nzRandom())) * Math.cos(2 * Math.PI * RANDOM.nextDouble()));
        } while (result >= cutoff);

        return result / cutoff * (top - mode) + mode;
    }

    public static long skewedRand(long mode, long lo, long hi, double cutoff) {
        return Math.round(skewedRand((double) mode, (double) lo, (double) hi, cutoff));
    }

    // Generates a random float in the given range, weighted towards the mean
    public static double normalRange(double min, double max, double cutoff) {
        if (cutoff <= 0) {
            cutoff = GAUSS_CUTOFF;
        }

        switch (RANDOM.nextInt(2)) {
            case 0:
                return (max + min) / 2.0 + truncatedGauss(0, (max - min) / 2, cutoff);
            case 1:
                return (max + min) / 2.0 - truncatedGauss(0, (max - min) / 2, cutoff);
            default:
                throw new IllegalStateException("Unexpected value: " + RANDOM.nextInt(2));
        }
    }

    public static long normalRange(long min, long max, double cutoff) {
        if (cutoff <= 0) {
            cutoff = GAUSS_CUTOFF;
        }

        switch (RANDOM.nextInt(2)) {
            case 0:
                return Math.round((max + min) / 2.0 + truncatedGauss(0, (max - min) / 2, cutoff));
            case 1:
                return Math.round((max + min) / 2.0 - truncatedGauss(0, (max - min) / 2, cutoff));
            default:
                throw new IllegalStateException("Unexpected value: " + RANDOM.nextInt(2));
        }
    }

    // Generates a random point weighted around Mean with a max distance from Mean defined by MaxRad
    public static Point randomPoint(Point mean, int maxRad, double cutoff) {
        int x = (int) normalRange(mean.getX() - maxRad, mean.getX() + maxRad, cutoff);
        int y = (int) normalRange(mean.getY() - maxRad, mean.getY() + maxRad, cutoff);
        return new Point(x, y);
    }

    // Generates a random point within the bounds of the given rectangle, weighted towards the middle
    public static Point randomPoint(Rectangle rect, double cutoff) {
        double x1 = rect.getX();
        double y1 = rect.getY();
        double x2 = rect.getX() + rect.getWidth();
        double y2 = rect.getY() + rect.getHeight();
        double a = Math.atan2(rect.getHeight(), rect.getWidth());

        int x = (int) normalRange(x1 + 1, x2 - 1, cutoff);
        int y = (int) normalRange(y1 + 1, y2 - 1, cutoff);
        return rotatePoint(new Point(x, y), a, (x2 + x1) / 2 + RANDOM.nextDouble() - 0.5, (y2 + y1) / 2 + RANDOM.nextDouble() - 0.5);
    }

    // Generates a random point in the bounds of the given box, the point generated is skewed towards From-point
    public static Point randomPointEx(Point from, Rectangle rect, double force) {
        Point p = from;
        p = new Point(Math.min(Math.max(p.getX(), (int) rect.getX()), (int) (rect.getX() + rect.getWidth())), Math.min(Math.max(p.getY(), (int) rect.getY()), (int) (rect.getY() + rect.getHeight())));

        Point c = new Point((int) (rect.getX() + rect.getWidth() / 2), (int) (rect.getY() + rect.getHeight() / 2));
        double r = Math.hypot(p.getX() - c.getX(), p.getY() - c.getY()) * force;
        double x = Math.atan2(c.getY() - p.getY(), c.getX() - p.getX());
        p = new Point((int) (p.getX() + Math.round(Math.cos(x) * r)), (int) (p.getY() + Math.round(Math.sin(x) * r)));

        int resultX = (int) skewedRand(p.getX(), (int) rect.getX(), (int) (rect.getX() + rect.getWidth()), GAUSS_CUTOFF);
        int resultY = (int) skewedRand(p.getY(), (int) rect.getY(), (int) (rect.getY() + rect.getHeight()), GAUSS_CUTOFF);
        return new Point(resultX, resultY);
    }

    // Dice function: Generates a random number and returns true if within the chance percentage
    public static boolean dice(double chancePercent) {
        return RANDOM.nextDouble() < (chancePercent <= 0.99 ? chancePercent * 100 : chancePercent) / 100;
    }

    // Wait function weighted towards the mean of Min and Max
    public static void wait(double min, double max, EWaitDir weight) {
        switch (weight) {
            case wdLeft:
                systemWait(Math.round(truncatedGauss(min, max, 0)));
                break;
            case wdMean:
                systemWait(Math.round(normalRange(min, max, 0)));
                break;
            case wdRight:
                systemWait(Math.round(truncatedGauss(max, min, 0)));
                break;
        }
    }

    // WaitEx function: waits with regular Gaussian randomness
    public static void waitEx(double mean, double dev) {
        wait(Math.abs(Math.round(gaussRand(mean, dev))), 0, EWaitDir.wdMean);
    }

    private static void systemWait(long time) {
        Global.sleep((int) time);
    }

    private static Point rotatePoint(Point point, double angle, double originX, double originY) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double dx = point.getX() - originX;
        double dy = point.getY() - originY;
        int newX = (int) (cos * dx - sin * dy + originX);
        int newY = (int) (sin * dx + cos * dy + originY);
        return new Point(newX, newY);
    }

    enum EWaitDir {
        wdLeft, wdMean, wdRight
    }
}
