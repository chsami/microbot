package net.runelite.client.plugins.hoseaplugins.lucidgauntlet;

import net.runelite.client.plugins.hoseaplugins.api.utils.GameObjectUtils;
import net.runelite.client.plugins.hoseaplugins.api.utils.MessageUtils;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class GauntletInstanceGrid
{
    public static final int ROOM_SIZE = 16; // gauntlet rooms are 16x16 tiles in size
    public static final int GRID_SIZE = 7;
    private GauntletRoom[][] rooms = new GauntletRoom[GRID_SIZE][GRID_SIZE];

    @Getter
    private WorldPoint startLocation;

    @Getter
    private WorldPoint cookingRangeLocation;

    @Getter
    private WorldPoint basePoint;

    private Client client;

    private LucidGauntletPlugin plugin;

    private LucidGauntletConfig config;

    private final List<Point> firstSearchFromGrid3_2 = List.of(new Point(2, 2), new Point(2, 1), new Point(3, 1), new Point(4, 1), new Point(4, 2), new Point(5, 2), new Point(5, 3), new Point(4, 3));

    private final List<Point> firstSearchFromGrid2_3 = List.of(new Point(2, 4), new Point(1, 4), new Point(1, 3), new Point(1, 2), new Point(2, 2), new Point(2, 1), new Point(3, 1), new Point(3, 2));

    private final List<Point> firstSearchFromGrid4_3 = List.of(new Point(4, 2), new Point(5, 2), new Point(5, 3), new Point(5, 4), new Point(4, 4), new Point(4, 5), new Point(3, 5), new Point(3, 4));

    private final List<Point> firstSearchFromGrid3_4 = List.of(new Point(4, 4), new Point(4, 5), new Point(3, 5), new Point(2, 5), new Point(2, 4), new Point(1, 4), new Point(1, 3), new Point(2, 3));

    private List<Point> beginningSearchPath;

    private final List<Point> secondSearchFromGrid3_2 = List.of(new Point(3, 1), new Point(3, 0), new Point(2, 0), new Point(4, 0),
            new Point(4, 1), new Point(5, 1), new Point(5, 2), new Point(6, 2), new Point(6, 3), new Point(6, 4),
            new Point(5, 4), new Point(5, 5), new Point(4, 5), new Point(4, 6), new Point(3, 6), new Point(2, 6),
            new Point(2, 5), new Point(1, 5), new Point(1, 4), new Point(0, 4), new Point(0, 3), new Point(0, 2),
            new Point(1, 2), new Point(2, 2));

    private final List<Point> secondSearchFromGrid2_3 = List.of(new Point(1, 3), new Point(0, 3), new Point(0, 4), new Point(0, 2),
            new Point(1, 2), new Point(1, 1), new Point(2, 1), new Point(2, 0), new Point(3, 0), new Point(4, 0),
            new Point(4, 1), new Point(5, 1), new Point(5, 2), new Point(6, 2), new Point(6, 3), new Point(6, 4),
            new Point(5, 4), new Point(5, 5), new Point(4, 5), new Point(4, 6), new Point(3, 6), new Point(2, 6),
            new Point(2, 5), new Point(2, 4));

    private final List<Point> secondSearchFromGrid4_3 = List.of(new Point(5, 3), new Point(6, 3), new Point(6, 2), new Point(6, 4),
            new Point(5, 4), new Point(5, 5), new Point(4, 5), new Point(4, 6), new Point(3, 6), new Point(2, 6),
            new Point(2, 5), new Point(1, 5), new Point(1, 4), new Point(0, 4), new Point(0, 3), new Point(0, 2),
            new Point(1, 2), new Point(1, 1), new Point(2, 1), new Point(2, 0), new Point(3, 0), new Point(4, 0),
            new Point(4, 1), new Point(4, 2));

    private final List<Point> secondSearchFromGrid3_4 = List.of(new Point(3, 5), new Point(3, 6), new Point(4, 6), new Point(2, 6),
            new Point(2, 5), new Point(1, 5), new Point(1, 4), new Point(0, 4), new Point(0, 3), new Point(0, 2),
            new Point(1, 2), new Point(1, 1), new Point(2, 1), new Point(2, 0), new Point(3, 0), new Point(4, 0),
            new Point(4, 1), new Point(5, 1), new Point(5, 2), new Point(6, 2), new Point(6, 3), new Point(6, 4),
            new Point(5, 4), new Point(4, 4));

    private List<Point> secondRoundSearchPath;

    private final List<Point> lastSearchFromGrid3_2 = List.of(new Point(2, 1), new Point(1, 1), new Point(1, 2), new Point(1, 3),
            new Point(2, 3), new Point(2, 4), new Point(1, 4), new Point(3, 4), new Point(4, 4), new Point(4, 3), new Point(4, 2));

    private final List<Point> lastSearchFromGrid2_3 = List.of(new Point(1, 4), new Point(1, 5), new Point(2, 5), new Point(3, 5),
            new Point(3, 4), new Point(4, 4), new Point(4, 5), new Point(4, 3), new Point(4, 2), new Point(3, 2), new Point(2, 2));

    private final List<Point> lastSearchFromGrid4_3 = List.of(new Point(5, 2), new Point(5, 1), new Point(4, 1), new Point(3, 1),
            new Point(3, 2), new Point(2, 2), new Point(2, 1), new Point(2, 3), new Point(2, 4), new Point(3, 4), new Point(4, 4));

    private final List<Point> lastSearchFromGrid3_4 = List.of(new Point(4, 5), new Point(5, 5), new Point(5, 4), new Point(5, 3),
            new Point(4, 3), new Point(4, 2), new Point(5, 2), new Point(3, 2), new Point(2, 2), new Point(2, 3), new Point(2, 4));

    private List<Point> lastRoundSearchPath;



    @Getter
    private boolean initialized = false;

    @Inject
    public GauntletInstanceGrid(final Client client, final LucidGauntletPlugin plugin, final LucidGauntletConfig config)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    public void reset()
    {
        rooms = new GauntletRoom[GRID_SIZE][GRID_SIZE];
        initialized = false;
    }

    public void initialize()
    {
        if (client == null || plugin == null)
        {
            return;
        }

        // First find the Hunllef barriers to use as our starting guide to get the grid mapped out
        // The barriers act like a landmark to be able to know where the middle of the instance grid is
        List<TileObject> barriers = getHunllefBarriers();
        if (barriers == null || barriers.size() != 4)
        {
            if (client.getGameState() == GameState.LOGGED_IN)
            {
                MessageUtils.addMessage("Can't initialize, unable to find barriers. Found: " + barriers.size(), Color.RED);
            }
            return;
        }

        GauntletRoom hunllefRoom = getHunllefRoomFromBarriers(barriers);

        rooms[3][3] = hunllefRoom;

        basePoint = new WorldPoint(hunllefRoom.getBaseX() - (ROOM_SIZE * 3), hunllefRoom.getBaseY() + (ROOM_SIZE * 3), client.getLocalPlayer().getWorldLocation().getPlane());

        TileObject toolStorage = getToolStorage();
        TileObject singingBowl = getSingingBowl();
        TileObject cookingRange = getCookingRange();
        if (toolStorage == null || singingBowl == null || cookingRange == null)
        {
            return;
        }

        Point startingRoomPoint = getStartingRoomGridLocation(toolStorage);

        setPaths(startingRoomPoint);
        final int startMinX = basePoint.getX() + (startingRoomPoint.getX() * ROOM_SIZE);
        final int startMinY = basePoint.getY() - (startingRoomPoint.getY() * ROOM_SIZE);

        GauntletRoom startingRoom = new GauntletRoom(startMinX, startMinY);
        startingRoom.setLit(true);
        rooms[startingRoomPoint.getX()][startingRoomPoint.getY()] = startingRoom;

        startLocation = singingBowl.getWorldLocation();
        cookingRangeLocation = cookingRange.getWorldLocation();

        // Initialize rest of the grid
        for (int x = 0; x < GRID_SIZE; x++)
        {
            for (int y = 0; y < GRID_SIZE; y++)
            {
                if (getRoom(x, y) == null)
                {
                    rooms[x][y] = new GauntletRoom(basePoint.getX() + (x * ROOM_SIZE), basePoint.getY() - (y * ROOM_SIZE));
                }
            }
        }
        initialized = true;
    }

    private void setPaths(Point startPoint)
    {
        if (Objects.equals(startPoint, new Point(2, 3)))
        {
            beginningSearchPath = firstSearchFromGrid2_3;
            secondRoundSearchPath = secondSearchFromGrid2_3;
            lastRoundSearchPath = lastSearchFromGrid2_3;
        }

        if (Objects.equals(startPoint, new Point(3, 2)))
        {
            beginningSearchPath = firstSearchFromGrid3_2;
            secondRoundSearchPath = secondSearchFromGrid3_2;
            lastRoundSearchPath = lastSearchFromGrid3_2;
        }

        if (Objects.equals(startPoint, new Point(4, 3)))
        {
            beginningSearchPath = firstSearchFromGrid4_3;
            secondRoundSearchPath = secondSearchFromGrid4_3;
            lastRoundSearchPath = lastSearchFromGrid4_3;
        }

        if (Objects.equals(startPoint, new Point(3, 4)))
        {
            beginningSearchPath = firstSearchFromGrid3_4;
            secondRoundSearchPath = secondSearchFromGrid3_4;
            lastRoundSearchPath = lastSearchFromGrid3_4;
        }
    }

    public GauntletRoom getNextUnlitRoomFirstPass()
    {
        for (Point p : beginningSearchPath)
        {
            GauntletRoom room = getRoom(p.getX(), p.getY());
            if (room != null && !room.isLit())
            {
                return room;
            }
        }
        return null;
    }

    public GauntletRoom getNextUnlitRoomSecondPass()
    {
        for (Point p : secondRoundSearchPath)
        {
            GauntletRoom room = getRoom(p.getX(), p.getY());
            if (room != null && !room.isLit())
            {
                return room;
            }
        }
        return null;
    }

    public GauntletRoom getNextUnlitRoomLastPass()
    {
        for (Point p : lastRoundSearchPath)
        {
            GauntletRoom room = getRoom(p.getX(), p.getY());
            if (room != null && !room.isLit())
            {
                return room;
            }
        }
        return null;
    }


    private List<TileObject> getHunllefBarriers()
    {
        return GameObjectUtils.getAll(obj -> (obj.getId() == 37339 || obj.getId() == 37337)).stream().distinct().collect(Collectors.toList());
    }

    private TileObject getToolStorage()
    {
        return GameObjectUtils.nearest("Tool Storage");
    }

    private TileObject getSingingBowl()
    {
        return GameObjectUtils.nearest("Singing Bowl");
    }

    private TileObject getCookingRange()
    {
        return GameObjectUtils.nearest("Range");
    }

    private GauntletRoom getHunllefRoomFromBarriers(List<TileObject> barriers)
    {
        int minX = -1;
        int maxY = -1;

        for (TileObject object : barriers)
        {
            if (minX == -1 || object.getWorldLocation().getX() < minX)
            {
                minX = object.getWorldLocation().getX();
            }

            if (maxY == -1 || object.getWorldLocation().getY() > maxY)
            {
                maxY = object.getWorldLocation().getY();
            }
        }

        // We add 1 to the perimeter because the barriers aren't on the very edge of the room
        return new GauntletRoom(minX - 1, maxY + 1);
    }

    private Point getStartingRoomGridLocation(TileObject toolStorage)
    {
        WorldPoint toolStoragePosition = toolStorage.getWorldLocation();
        GauntletRoom hunllefRoom = getRoom(3, 3);

        if (toolStoragePosition.getX() > hunllefRoom.getBaseX() + ROOM_SIZE)
        {
            return new Point(4, 3);
        }

        if (toolStoragePosition.getX() < hunllefRoom.getBaseX())
        {
            return new Point(2, 3);
        }

        if (toolStoragePosition.getY() < (hunllefRoom.getBaseY() - ROOM_SIZE))
        {
            return new Point(3, 4);
        }

        if (toolStoragePosition.getY() > hunllefRoom.getBaseY())
        {
            return new Point (3, 2);
        }

        return new Point(0, 0);
    }

    public Point getGridLocationByWorldPoint(final WorldPoint worldPoint)
    {
        int gridX = (-(basePoint.getX() - worldPoint.getX())) / ROOM_SIZE;
        int gridY = (basePoint.getY() - worldPoint.getY()) / ROOM_SIZE;

        return new Point(gridX, gridY);
    }


    public GauntletRoom getRoom(int gridX, int gridY)
    {
        return rooms[gridX][gridY];
    }
}