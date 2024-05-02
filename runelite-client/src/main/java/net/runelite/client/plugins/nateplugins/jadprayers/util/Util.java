package net.runelite.client.plugins.nateplugins.jadprayers.util;

import net.runelite.api.NPC;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Util {

    public List<NPC> npcs;

    public Util(List<NPC> npcs) {
        this.npcs = new ArrayList<>(npcs.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }
    public Util filter(Predicate<? super NPC> predicate) {
        npcs = npcs.stream().filter(predicate).collect(Collectors.toList());
        return this;
    }
    public Util nameContains(String name) {
        npcs = npcs.stream().filter(npc -> npc.getName() != null && npc.getName().contains(name)).collect(Collectors.toList());
        return this;
    }

    public Util nameContainsNoCase(String name) {
        npcs = npcs.stream().filter(npc -> npc.getName() != null && npc.getName().toLowerCase().contains(name)).collect(Collectors.toList());
        return this;
    }


}