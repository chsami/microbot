package net.runelite.client.plugins.griffinplugins.worlddatacollection;

import net.runelite.api.ObjectID;
import net.runelite.client.plugins.microbot.util.Global;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldDataCollectionThread extends Thread {
    public final ExecutorService executor = Executors.newFixedThreadPool(100);
    public static int started = 0;
    public static int completed = 0;

    @Override
    public void run() {
        started = 0;
        completed = 0;
        List<Integer> operableObjectIds = gatherOperableObjectIds();

        while (true) {
            started++;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    TileCollector tileCollector = new TileCollector(operableObjectIds);
                    tileCollector.collect();
                    WorldDataCollectionThread.completed++;
                }
            });

            Global.sleep(3000);
        }
    }

    private List<Integer> gatherOperableObjectIds() {
        List<Integer> operableObjectIds = new ArrayList<>();
        Field[] fields = ObjectID.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getType() == int.class) {
                try {
                    int fieldValue = field.getInt(null);

                    if (field.getName().startsWith("DOOR")) {
                        operableObjectIds.add(fieldValue);
                    } else if (field.getName().startsWith("LARGE_DOOR")) {
                        operableObjectIds.add(fieldValue);
                    } else if (field.getName().startsWith("GATE")) {
                        operableObjectIds.add(fieldValue);
                    } else if (field.getName().startsWith("TRAPDOOR")) {
                        operableObjectIds.add(fieldValue);
                    } else if (field.getName().startsWith("STAIRCASE")) {
                        operableObjectIds.add(fieldValue);
                    } else if (field.getName().startsWith("CAVE_ENTRANCE")) {
                        operableObjectIds.add(fieldValue);
                    } else if (field.getName().startsWith("STAIRS")) {
                        operableObjectIds.add(fieldValue);
//                    } else if (field.getName().startsWith("ANCIENT_GATE")) {
//                        operableObjectIds.add(fieldValue);
//                    } else if (field.getName().startsWith("TEMPLE_DOOR")) {
//                        operableObjectIds.add(fieldValue);
                    } else if (field.getName().startsWith("LADDER")) {
                        operableObjectIds.add(fieldValue);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return operableObjectIds;
    }
}
