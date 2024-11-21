package net.runelite.client.plugins.microbot.shortestpath;

import lombok.Getter;
import net.runelite.api.Quest;
import net.runelite.api.Skill;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class represents (conditional) restrictions for a WorldPoint to use in the pathfinder.
 */
@Getter
public class Restriction {
    /**
     * The skill levels required for restriction to be lifted.
     */
    @Getter
    private final int[] skillLevels = new int[Skill.values().length];
    /**
     * Any varbits to check for the restriction to be lifted.
     */
    @Getter
    private final Set<TransportVarbit> varbits = new HashSet<>();
    /**
     * The restricted point (packed)
     */
    private int packedWorldPoint;
    /**
     * The quests required to lift the restriction
     */
    private List<Quest> quests = new ArrayList<>();


    public Restriction(int x, int y, int z) {
        packedWorldPoint = WorldPointUtil.packWorldPoint(x, y, z);
    }

    public Restriction(Map<String, String> fieldMap) {
        final String DELIM = " ";
        final String DELIM_MULTI = ";";
        final String DELIM_STATE = "=";

        String value;
        if ((value = fieldMap.get("Origin")) != null) {
            String[] originArray = value.split(DELIM);
            packedWorldPoint = WorldPointUtil.packWorldPoint(
                    Integer.parseInt(originArray[0]),
                    Integer.parseInt(originArray[1]),
                    Integer.parseInt(originArray[2]));
        }

        if ((value = fieldMap.get("Quests")) != null) {
            this.quests = findQuests(value);
        }

        if ((value = fieldMap.get("Skills")) != null) {
            String[] skillRequirements = value.split(DELIM_MULTI);

            for (String requirement : skillRequirements) {
                String[] levelAndSkill = requirement.split(DELIM);

                if (levelAndSkill.length < 2) {
                    continue;
                }

                int level = Integer.parseInt(levelAndSkill[0]);
                String skillName = levelAndSkill[1];

                Skill[] skills = Skill.values();
                for (int i = 0; i < skills.length; i++) {
                    if (skills[i].getName().equals(skillName)) {
                        skillLevels[i] = level;
                        break;
                    }
                }
            }
        }

        if ((value = fieldMap.get("Varbits")) != null) {
            for (String varbitCheck : value.split(DELIM_MULTI)) {
                String[] parts;
                TransportVarbit.Operator operator;

                if (varbitCheck.contains(">")) {
                    parts = varbitCheck.split(">");
                    operator = TransportVarbit.Operator.GREATER_THAN;
                } else if (varbitCheck.contains("<")) {
                    parts = varbitCheck.split("<");
                    operator = TransportVarbit.Operator.LESS_THAN;
                } else if (varbitCheck.contains("=")) {
                    parts = varbitCheck.split("=");
                    operator = TransportVarbit.Operator.EQUAL;
                } else {
                    throw new IllegalArgumentException("Invalid varbit format: " + varbitCheck);
                }

                int varbitId = Integer.parseInt(parts[0]);
                int varbitValue = Integer.parseInt(parts[1]);
                varbits.add(new TransportVarbit(varbitId, varbitValue, operator));
            }
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
        final String DELIM_COLUMN = "\t";
        final String PREFIX_COMMENT = "#";

        try {
            String s = new String(Util.readAllBytes(ShortestPathPlugin.class.getResourceAsStream("restrictions.tsv")), StandardCharsets.UTF_8);
            Scanner scanner = new Scanner(s);
            String headerLine = scanner.nextLine();
            headerLine = headerLine.startsWith(PREFIX_COMMENT + " ") ? headerLine.replace(PREFIX_COMMENT + " ", PREFIX_COMMENT) : headerLine;
            headerLine = headerLine.startsWith(PREFIX_COMMENT) ? headerLine.replace(PREFIX_COMMENT, "") : headerLine;
            String[] headers = headerLine.split(DELIM_COLUMN);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith(PREFIX_COMMENT) || line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(DELIM_COLUMN);
                Map<String, String> fieldMap = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    if (i < fields.length) {
                        fieldMap.put(headers[i], fields[i]);
                    }
                }

                Restriction restriction = new Restriction(fieldMap);
                restrictions.add(restriction);
            }
            scanner.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return restrictions;
    }

}
