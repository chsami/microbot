package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuperiorMonsterMapping {
    private static final Map<String, List<String>> superiorMonstersMap = new HashMap<>();

    static {
        superiorMonstersMap.put("Aberrant spectres", Arrays.asList("Abhorrent spectre"));
        superiorMonstersMap.put("Ankou", Arrays.asList("Anguished ankou"));
        superiorMonstersMap.put("Banshees", Arrays.asList("Screaming banshee", "Screaming twisted banshee"));
        superiorMonstersMap.put("Basilisks", Arrays.asList("Monstrous basilisk"));
        superiorMonstersMap.put("Basilisk Knight", Arrays.asList("Basilisk Sentinel"));
        superiorMonstersMap.put("Cave horror", Arrays.asList("Cave abomination"));
        superiorMonstersMap.put("Cave crawler", Arrays.asList("Chasm Crawler"));
        superiorMonstersMap.put("Dust devil", Arrays.asList("Choke devil"));
        superiorMonstersMap.put("Cockatrice", Arrays.asList("Cockathrice"));
        superiorMonstersMap.put("Hydra", Arrays.asList("Colossal Hydra"));
        superiorMonstersMap.put("Crawling Hand", Arrays.asList("Crushing hand"));
        superiorMonstersMap.put("Pyrefiend", Arrays.asList("Flaming pyrelord", "Infernal pyrelord"));
        superiorMonstersMap.put("Rock slug", Arrays.asList("Giant rockslug"));
        superiorMonstersMap.put("Abyssal demon", Arrays.asList("Greater abyssal demon"));
        superiorMonstersMap.put("Drake", Arrays.asList("Guardian Drake"));
        superiorMonstersMap.put("Bloodveld", Arrays.asList("Insatiable Bloodveld", "Insatiable mutated Bloodveld"));
        superiorMonstersMap.put("Kurask", Arrays.asList("King kurask"));
        superiorMonstersMap.put("Infernal Mage", Arrays.asList("Malevolent Mage"));
        superiorMonstersMap.put("Gargoyle", Arrays.asList("Marble gargoyle"));
        superiorMonstersMap.put("Warped Terrorbird", Arrays.asList("Mutated Terrorbird"));
        superiorMonstersMap.put("Warped Tortoise", Arrays.asList("Mutated Tortoise"));
        superiorMonstersMap.put("Nechryael", Arrays.asList("Nechryarch"));
        superiorMonstersMap.put("Greater Nechryael", Arrays.asList("Nechryarch"));
        superiorMonstersMap.put("Dark beast", Arrays.asList("Night beast"));
        superiorMonstersMap.put("Smoke devil", Arrays.asList("Nuclear smoke devil"));
        superiorMonstersMap.put("Deviant spectre", Arrays.asList("Repugnant spectre"));
        superiorMonstersMap.put("Twisted Banshee", Arrays.asList("Screaming twisted banshee"));
        superiorMonstersMap.put("Wyrm", Arrays.asList("Shadow Wyrm"));
        superiorMonstersMap.put("Turoth", Arrays.asList("Spiked Turoth"));
        superiorMonstersMap.put("Jelly", Arrays.asList("Vitreous Jelly", "Vitreous warped Jelly"));
    }

    public static List<String> getSuperiorNames(String taskName) {
        return superiorMonstersMap.getOrDefault(taskName, Arrays.asList());
    }
}
