package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskNpcMapping {
    // Map to store the mapping between task names and NPC names
    private static final Map<String, List<String>> TASK_NPC_MAPPING = new HashMap<>();

    static {
        // WARNING: CASE SENSITIVE
        TASK_NPC_MAPPING.put("Aberrant Spectres", Arrays.asList("Aberrant spectre"));
        TASK_NPC_MAPPING.put("Banshees", Arrays.asList("Banshee"));
        TASK_NPC_MAPPING.put("Bats", Arrays.asList("Bat"));
        TASK_NPC_MAPPING.put("Bears", Arrays.asList("Grizzly bear"));
        TASK_NPC_MAPPING.put("Birds", Arrays.asList("Chicken"));
        TASK_NPC_MAPPING.put("Black Demons", Arrays.asList("Black demon"));
        TASK_NPC_MAPPING.put("Black Dragons", Arrays.asList("Black dragon"));
        TASK_NPC_MAPPING.put("Bloodveld", Arrays.asList("Bloodveld"));
        TASK_NPC_MAPPING.put("Brine Rats", Arrays.asList("Brine rat"));
        TASK_NPC_MAPPING.put("Cave Bugs", Arrays.asList("Cave bug"));
        TASK_NPC_MAPPING.put("Cave Crawlers", Arrays.asList("Cave crawler"));
        TASK_NPC_MAPPING.put("Cave Slimes", Arrays.asList("Cave slime"));
        TASK_NPC_MAPPING.put("Crawling Hands", Arrays.asList("Crawling hand"));
        TASK_NPC_MAPPING.put("Cows", Arrays.asList("Cow"));
        TASK_NPC_MAPPING.put("Dagannoth", Arrays.asList("Dagannoth"));
        TASK_NPC_MAPPING.put("Dwarves", Arrays.asList("Dwarf"));
        TASK_NPC_MAPPING.put("Elves", Arrays.asList("Iorwerth warrior", "Iorwerth archer"));
        TASK_NPC_MAPPING.put("Fire Giants", Arrays.asList("Fire giant"));
        TASK_NPC_MAPPING.put("Goblins", Arrays.asList("Goblin"));
        TASK_NPC_MAPPING.put("Greater Demons", Arrays.asList("Greater demon"));
        TASK_NPC_MAPPING.put("Icefiends", Arrays.asList("Icefiend"));
        TASK_NPC_MAPPING.put("Iron Dragons", Arrays.asList("Iron dragon"));
        TASK_NPC_MAPPING.put("Kalphite", Arrays.asList("Kalphite Worker"));
        TASK_NPC_MAPPING.put("Kurask", Arrays.asList("Kurask"));
        TASK_NPC_MAPPING.put("Lizards", Arrays.asList("Desert lizard"));
        TASK_NPC_MAPPING.put("Minotaurs", Arrays.asList("Minotaur"));
        TASK_NPC_MAPPING.put("Monkeys", Arrays.asList("Monkey"));
        TASK_NPC_MAPPING.put("Rats", Arrays.asList("Giant Rat"));
        TASK_NPC_MAPPING.put("Skeletal Wyverns", Arrays.asList("Skeletal Wyvern"));
        TASK_NPC_MAPPING.put("Skeletons", Arrays.asList("Skeleton"));
        TASK_NPC_MAPPING.put("Spiritual Creatures", Arrays.asList("Spiritual warrior"));
        TASK_NPC_MAPPING.put("Trolls", Arrays.asList("Mountain troll"));
        TASK_NPC_MAPPING.put("Turoth", Arrays.asList("Turoth"));
        TASK_NPC_MAPPING.put("TzHaar", Arrays.asList("TzHaar-Ket"));
        TASK_NPC_MAPPING.put("Wolves", Arrays.asList("Wolf"));
        TASK_NPC_MAPPING.put("Wyrms", Arrays.asList("Wyrm"));

        // Add more mappings as needed
    }

    // Method to get the NPC names for a given task name
    public static List<String> getNpcNames(String taskName) {
        return TASK_NPC_MAPPING.getOrDefault(taskName, Arrays.asList(taskName));
    }
}
