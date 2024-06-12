package net.runelite.client.plugins.hoseaplugins.api.utils;

import net.runelite.api.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.RuneLite;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Reachable
{
    static Client client = RuneLite.getInjector().getInstance(Client.class);

    public static boolean isInteractable(WorldArea locatableArea)
    {
        return getInteractable(locatableArea).stream().anyMatch(InteractionUtils::isWalkable);
    }

    public static List<WorldPoint> getInteractable(WorldArea locatableArea)
    {
        WorldArea surrounding = offset(locatableArea, 1);
        return (List)surrounding.toWorldPointList().stream().filter((p) ->
        {
            return !locatableArea.contains(p);
        }).filter((p) ->
        {
            return canMelee(locatableArea, p.toWorldArea());
        }).filter((p) ->
        {
            return !isObstacle(p);
        }).collect(Collectors.toList());
    }

    public static WorldArea offset(WorldArea toOffset, int offset) {
        return new WorldArea(toOffset.getX() - offset, toOffset.getY() - offset, toOffset.getWidth() + 2 * offset, toOffset.getHeight() + 2 * offset, toOffset.getPlane());
    }

    public static boolean canMelee(WorldArea first, WorldArea other)
    {
        if (first.isInMeleeDistance(other))
        {
            Point p1 = getComparisonPoint(first, other);
            Point p2 = getComparisonPoint(other, first);
            WorldArea w1 = new WorldArea(p1.getX(), p1.getY(), 1, 1, first.getPlane());
            return canTravelInDirection(w1, p2.getX() - p1.getX(), p2.getY() - p1.getY());
        }
        else
        {
            return false;
        }
    }

    public static Point getComparisonPoint(WorldArea first, WorldArea other)
    {
        int x;
        if (other.getX() <= first.getX())
        {
            x = first.getX();
        }
        else if (other.getX() >= first.getX() + first.getWidth() - 1)
        {
            x = first.getX() + first.getWidth() - 1;
        }
        else
        {
            x = other.getX();
        }

        int y;
        if (other.getY() <= first.getY())
        {
            y = first.getY();
        }
        else if (other.getY() >= first.getY() + first.getHeight() - 1)
        {
            y = first.getY() + first.getHeight() - 1;
        }
        else
        {
            y = other.getY();
        }

        return new Point(x, y);
    }

    public static boolean check(int flag, int checkFlag)
    {
        return (flag & checkFlag) != 0;
    }

    public static boolean isObstacle(int endFlag)
    {
        return check(endFlag, 19005696);
    }

    public static boolean isObstacle(WorldPoint worldPoint)
    {
        return isObstacle(getCollisionFlag(worldPoint));
    }

    public static int getCollisionFlag(WorldPoint point)
    {
        CollisionData[] collisionMaps = client.getTopLevelWorldView().getCollisionMaps();
        if (collisionMaps == null)
        {
            return 16777215;
        }
        else
        {
            CollisionData collisionData = collisionMaps[client.getTopLevelWorldView().getPlane()];
            if (collisionData == null)
            {
                return 16777215;
            }
            else
            {
                LocalPoint localPoint = LocalPoint.fromWorld(client.getTopLevelWorldView(), point);
                return localPoint == null ? 16777215 : collisionData.getFlags()[localPoint.getSceneX()][localPoint.getSceneY()];
            }
        }
    }

    public static boolean canTravelInDirection(WorldArea w1, int dx, int dy)
    {
        return canTravelInDirection(w1, dx, dy, (x) ->
        {
            return true;
        });
    }

    public static boolean canTravelInDirection(WorldArea w1, int dx, int dy, Predicate<? super WorldPoint> extraCondition)
    {
        dx = Integer.signum(dx);
        dy = Integer.signum(dy);
        if (dx == 0 && dy == 0)
        {
            return true;
        }
        else
        {
            LocalPoint lp = LocalPoint.fromWorld(client.getTopLevelWorldView(), w1.getX(), w1.getY());
            int startX = 0;
            if (lp != null)
            {
                startX = lp.getSceneX() + dx;
            }

            int startY = 0;
            if (lp != null)
            {
                startY = lp.getSceneY() + dy;
            }

            int checkX = startX + (dx > 0 ? w1.getWidth() - 1 : 0);
            int checkY = startY + (dy > 0 ? w1.getHeight() - 1 : 0);
            int endX = startX + w1.getWidth() - 1;
            int endY = startY + w1.getHeight() - 1;
            int xFlags = 2359552;
            int yFlags = 2359552;
            int xyFlags = 2359552;
            int xWallFlagsSouth = 2359552;
            int xWallFlagsNorth = 2359552;
            int yWallFlagsWest = 2359552;
            int yWallFlagsEast = 2359552;
            if (checkX < 104 && checkY < 104)
            {
                if (dx < 0)
                {
                    xFlags |= 8;
                    xWallFlagsSouth |= 48;
                    xWallFlagsNorth |= 6;
                }

                if (dx > 0)
                {
                    xFlags |= 128;
                    xWallFlagsSouth |= 96;
                    xWallFlagsNorth |= 3;
                }

                if (dy < 0)
                {
                    yFlags |= 2;
                    yWallFlagsWest |= 129;
                    yWallFlagsEast |= 12;
                }

                if (dy > 0)
                {
                    yFlags |= 32;
                    yWallFlagsWest |= 192;
                    yWallFlagsEast |= 24;
                }

                if (dx < 0 && dy < 0)
                {
                    xyFlags |= 4;
                }

                if (dx < 0 && dy > 0)
                {
                    xyFlags |= 16;
                }

                if (dx > 0 && dy < 0)
                {
                    xyFlags |= 1;
                }

                if (dx > 0 && dy > 0)
                {
                    xyFlags |= 64;
                }

                CollisionData[] collisionData = client.getTopLevelWorldView().getCollisionMaps();
                if (collisionData == null)
                {
                    return false;
                }
                else
                {
                    int[][] collisionDataFlags = collisionData[w1.getPlane()].getFlags();
                    int x;
                    if (dx != 0)
                    {
                        x = startY;

                        label183:
                        while(true)
                        {
                            if (x > endY)
                            {
                                for(x = startY + 1; x <= endY; ++x)
                                {
                                    if ((collisionDataFlags[checkX][x] & xWallFlagsSouth) != 0)
                                    {
                                        return false;
                                    }
                                }

                                x = endY - 1;

                                while(true)
                                {
                                    if (x < startY)
                                    {
                                        break label183;
                                    }

                                    if ((collisionDataFlags[checkX][x] & xWallFlagsNorth) != 0)
                                    {
                                        return false;
                                    }

                                    --x;
                                }
                            }

                            if ((collisionDataFlags[checkX][x] & xFlags) != 0 || !extraCondition.test(WorldPoint.fromScene(client.getTopLevelWorldView(), checkX, x, w1.getPlane())))
                            {
                                return false;
                            }

                            ++x;
                        }
                    }

                    if (dy != 0)
                    {
                        x = startX;

                        label156:
                        while(true)
                        {
                            if (x > endX)
                            {
                                for(x = startX + 1; x <= endX; ++x)
                                {
                                    if ((collisionDataFlags[x][checkY] & yWallFlagsWest) != 0)
                                    {
                                        return false;
                                    }
                                }

                                x = endX - 1;

                                while(true)
                                {
                                    if (x < startX)
                                    {
                                        break label156;
                                    }

                                    if ((collisionDataFlags[x][checkY] & yWallFlagsEast) != 0)
                                    {
                                        return false;
                                    }

                                    --x;
                                }
                            }

                            if ((collisionDataFlags[x][checkY] & yFlags) != 0 || !extraCondition.test(WorldPoint.fromScene(client.getTopLevelWorldView(), x, checkY, client.getLocalPlayer().getWorldLocation().getPlane())))
                            {
                                return false;
                            }

                            ++x;
                        }
                    }

                    if (dx != 0 && dy != 0)
                    {
                        if ((collisionDataFlags[checkX][checkY] & xyFlags) != 0 || !extraCondition.test(WorldPoint.fromScene(client.getTopLevelWorldView(), checkX, checkY, client.getLocalPlayer().getWorldLocation().getPlane())))
                        {
                            return false;
                        }

                        if (w1.getWidth() == 1 && (collisionDataFlags[checkX][checkY - dy] & xFlags) != 0 && extraCondition.test(WorldPoint.fromScene(client.getTopLevelWorldView(), checkX, startY, client.getLocalPlayer().getWorldLocation().getPlane())))
                        {
                            return false;
                        }

                        if (w1.getHeight() == 1)
                        {
                            return (collisionDataFlags[checkX - dx][checkY] & yFlags) == 0 || !extraCondition.test(WorldPoint.fromScene(client.getTopLevelWorldView(), startX, checkY, client.getLocalPlayer().getWorldLocation().getPlane()));
                        }
                    }

                    return true;
                }
            }
            else
            {
                return false;
            }
        }
    }
}
