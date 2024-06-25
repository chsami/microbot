package net.runelite.client.plugins.microbot.shortestpath;

import lombok.Getter;
import net.runelite.api.Quest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents (conditional) restrictions for a WorldPoint to use in the pathfinder.
 */
@Getter
public class Restriction {
    /**
     * The restricted point (packed)
     */
    private final int packedWorldPoint;

    /**
     * The quests required to lift the restriction
     */
    private List<Quest> quests = new ArrayList<>();

    public Restriction(int x, int y, int z){
        packedWorldPoint = WorldPointUtil.packWorldPoint(x, y, z);
    }

    Restriction(final String line) {
        final String DELIM = " ";

        String[] parts = line.split("\t");

        String[] parts_point = parts[0].split(DELIM);

        packedWorldPoint = WorldPointUtil.packWorldPoint(
                Integer.parseInt(parts_point[0]),
                Integer.parseInt(parts_point[1]),
                Integer.parseInt(parts_point[2]));

        // Quest requirements
        if (!parts[1].isEmpty()) {
            this.quests = findQuests(parts[1]);
        }
    }

    private static List<Quest> findQuests(String questNamesCombined) {
        String[] questNames = questNamesCombined.split(";");
        List<Quest> quests = new ArrayList<>();
        for (String questName : questNames) {
            for (Quest quest : Quest.values()) {
                if (quest.getName().equals(questName)) {
                    quests.add(quest);
                    break;
                }
            }
        }
        return quests;
    }

    public static List<Restriction> loadAllFromResources() {
        List<Restriction> restrictions = new ArrayList<>();

        try {
            String s = new String(Util.readAllBytes(ShortestPathPlugin.class.getResourceAsStream("restrictions.tsv")), StandardCharsets.UTF_8);
            Scanner scanner = new Scanner(s);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("#") || line.isBlank()) {
                    continue;
                }

                restrictions.add(new Restriction(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return restrictions;
    }
}
