package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.PathFinding;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.api.coords.WorldPoint;
import org.roaringbitmap.RoaringBitmap;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GlobalCollisionMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private static RoaringBitmap bitmap = init();

    public GlobalCollisionMap() {
        // Initialize the bitmap only if it's not already initialized
        if (bitmap == null) {
            bitmap = new RoaringBitmap();
        }
    }

    static byte[] load() {
        try {
            InputStream is = GlobalCollisionMap.class.getResourceAsStream("map");
            return new GZIPInputStream(is).readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RoaringBitmap init() {
        RoaringBitmap bitmap = new RoaringBitmap();
        try {
            bitmap.deserialize(ByteBuffer.wrap(load()));
            bitmap.runOptimize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bitmap;
    }

    public static void save() {
        try {
            String resourcePath = "/net/runelite/client/plugins/hoseaplugins/ethanapi/EthanApiPlugin/PathFinding/map";
            File file = new File(GlobalCollisionMap.class.getResource(resourcePath).toURI());
            try (OutputStream os = new FileOutputStream(file);
                 GZIPOutputStream gos = new GZIPOutputStream(os)) {
                ByteBuffer buffer = ByteBuffer.allocate(bitmap.serializedSizeInBytes());
                bitmap.serialize(buffer);
                buffer.flip();
                gos.write(buffer.array());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving resource 'map'", e);
        }
    }

    public static boolean east(WorldPoint wp) {
        return bitmap.contains(packed(wp) | (1 << 30));
    }

    public static boolean north(WorldPoint wp) {
        return bitmap.contains(packed(wp));
    }

    public static boolean south(WorldPoint wp) {
        return north(wp.dy(-1));
    }

    public static boolean west(WorldPoint wp) {
        return east(wp.dx(-1));
    }

    public static int packed(int x, int y, int plane) {
        return (x & 16383) | ((y & 16383) << 14) | (plane << 28);
    }

    public static WorldPoint unpack(int packed) {
        return new WorldPoint(packed & 16383, (packed >> 14) & 16383, packed >> 28);
    }

    public static int packed(WorldPoint wp) {
        return (wp.getX() & 16383) | ((wp.getY() & 16383) << 14) | (wp.getPlane() << 28);
    }

    public static List<WorldPoint> findPath(WorldPoint p) {
        long start = System.currentTimeMillis();
        WorldPoint starting = EthanApiPlugin.getClient().getLocalPlayer().getWorldLocation();
        HashSet<WorldPoint> visited = new HashSet<>();
        ArrayDeque<Node> queue = new ArrayDeque<>();
        queue.add(new Node(starting));
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            WorldPoint currentData = current.getData();
            if (currentData.equals(p)) {
                List<WorldPoint> ret = new ArrayList<>();
                while (current != null) {
                    ret.add(current.getData());
                    current = current.getPrevious();
                }
                Collections.reverse(ret);
                ret.remove(0);
                System.out.println("Path took " + (System.currentTimeMillis() - start) + "ms");
                return ret;
            }
            // west
            if (west(currentData) && visited.add(currentData.dx(-1))) {
                queue.add(new Node(currentData.dx(-1), current));
            }
            // east
            if (east(currentData) && visited.add(currentData.dx(1))) {
                queue.add(new Node(currentData.dx(1), current));
            }
            // south
            if (south(currentData) && visited.add(currentData.dy(-1))) {
                queue.add(new Node(currentData.dy(-1), current));
            }
            // north
            if (north(currentData) && visited.add(currentData.dy(1))) {
                queue.add(new Node(currentData.dy(1), current));
            }
        }
        return null;
    }

    // Node class implementation
    private static class Node {
        private final WorldPoint data;
        private final Node previous;

        public Node(WorldPoint data) {
            this(data, null);
        }

        public Node(WorldPoint data, Node previous) {
            this.data = data;
            this.previous = previous;
        }

        public WorldPoint getData() {
            return data;
        }

        public Node getPrevious() {
            return previous;
        }
    }

    // Serialization methods for the GlobalCollisionMap
    public void serialize(DataOutputStream out) throws IOException {
        bitmap.serialize(out);
    }

    public void deserialize(DataInputStream in) throws IOException {
        bitmap = new RoaringBitmap();
        bitmap.deserialize(in);
    }

    public void addCollision(int x, int y, int plane) {
        int index = packed(x, y, plane);
        bitmap.add(index);
    }

    public boolean hasCollision(int x, int y, int plane) {
        int index = packed(x, y, plane);
        return bitmap.contains(index);
    }

    // Example usage (main method)
    public static void main(String[] args) throws IOException {
        GlobalCollisionMap map = new GlobalCollisionMap();
        map.addCollision(1, 2, 0);
        map.addCollision(3, 4, 0);

        // Serialize
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream("collisionMap.dat"))) {
            map.serialize(out);
        }

        // Deserialize
        try (DataInputStream in = new DataInputStream(new FileInputStream("collisionMap.dat"))) {
            GlobalCollisionMap newMap = new GlobalCollisionMap();
            newMap.deserialize(in);
            System.out.println(newMap.hasCollision(1, 2, 0)); // true
            System.out.println(newMap.hasCollision(3, 4, 0)); // true
            System.out.println(newMap.hasCollision(5, 6, 0)); // false
        }
    }
}
