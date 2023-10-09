package net.runelite.client.plugins.microbot.util.npc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Rs2NpcManager {
    private static final Set<Integer> blacklistXpMultiplier = Set.of(8026, 8058, 8059, 8060, 8061, 7850, 7852, 7853, 7884, 7885, 7849, 7851, 7854, 7855, 7882, 7883, 7886, 7887, 7888, 7889, 494, 6640, 6656, 2042, 2043, 2044);
    private static Map<Integer, Rs2NpcStats> statsMap;
    public static Map<Integer, String> attackStyleMap;
    public static Map<Integer, String> attackAnimationMap;


    public static void loadJson() {
        if (statsMap != null) {
            return;
        }
        Type statsTypeToken = new TypeToken<Map<Integer, Rs2NpcStats>>() {}.getType();
        statsMap = loadJsonFile("/npc/npc_stats.json", statsTypeToken);

        Type attackStyleTypeToken = new TypeToken<Map<Integer, String>>() {}.getType();
        attackStyleMap = loadJsonFile("/npc/npcs_attack_style.json", attackStyleTypeToken);

        Type attackAnimationTypeToken = new TypeToken<Map<Integer, String>>() {}.getType();
        attackAnimationMap = loadJsonFile("/npc/npcs_attack_animation.json", attackAnimationTypeToken);

    }

    private static <T> Map<Integer, T> loadJsonFile(String filename, Type typeToken) {
        Gson gson = new Gson();
        try (InputStream inputStream = Rs2NpcStats.class.getResourceAsStream(filename)) {
            if (inputStream == null) {
                System.out.println("Failed to load " + filename);
                return Collections.emptyMap();
            }
            return gson.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), typeToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static Rs2NpcStats getStats(int npcId) {
        return statsMap.get(npcId);
    }

    public static int getHealth(int npcId) {
        Rs2NpcStats s = statsMap.get(npcId);
        return s != null && s.getHitpoints() != -1 ? s.getHitpoints() : -1;
    }

    public static int getAttackSpeed(int npcId) {
        Rs2NpcStats s = statsMap.get(npcId);
        return s != null && s.getAttackSpeed() != -1 ? s.getAttackSpeed() : -1;
    }

    public static double getXpModifier(int npcId) {
        if (blacklistXpMultiplier.contains(npcId)) {
            return 1.0;
        } else {
            Rs2NpcStats s = statsMap.get(npcId);
            return s == null ? 1.0 : s.calculateXpModifier();
        }
    }
}
