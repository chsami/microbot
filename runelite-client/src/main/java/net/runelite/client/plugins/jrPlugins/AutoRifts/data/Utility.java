package net.runelite.client.plugins.jrPlugins.AutoRifts.data;

import net.runelite.api.QuestState;

import java.util.HashSet;
import java.util.Set;

public class Utility {

    public static int getHighestLevelRuneIndex(int level) {
        if (level < 35) {
            return 7;
        } else if (level < 44) {
            return 8;
        } else if (level < 54) {
            return 9;
        } else if (level < 65) {
            return 10;
        } else if (level < 77) {
            return 11;
        }

        return 12;
    }

    public static Set<Altar> getAccessibleAltars(int level, QuestState city, QuestState troll, QuestState mep, QuestState sotf) {
        Set<Altar> accessibleAltars = new HashSet<>();
        for (int i = 0; i < getHighestLevelRuneIndex(level); i++) {
            Altar[] altars = Altar.values();
            if (altars[i] == Altar.COSMIC && city!=QuestState.FINISHED) {
                continue;
            }
            if (altars[i] == Altar.LAW && troll!=QuestState.FINISHED) {
                continue;
            }
            if (altars[i] == Altar.DEATH && mep!=QuestState.FINISHED) {
                continue;
            }
            if (altars[i] == Altar.BLOOD && sotf!=QuestState.FINISHED) {
                continue;
            }
            accessibleAltars.add(altars[i]);
        }

        return accessibleAltars;
    }



}
