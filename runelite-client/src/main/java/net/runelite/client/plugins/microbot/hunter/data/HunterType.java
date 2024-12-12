package net.runelite.client.plugins.microbot.hunter.data;

import java.util.Arrays;
import java.util.List;

public enum HunterType {
    BOX_TRAP("Box trap", 9380), // In-game ID for a placed box trap
    MAGIC_BOX("Magic box", 12934), // Magic Box ID for Imp trapping
    BIRD_SNARE("Bird snare", 9375), // Bird Snare object ID
    NET_TRAP(new String[]{"Small fishing net", "Rope"}, 9377), // Net Trap for Salamanders
    DEADFALL_TRAP(new String[]{"Logs", "Knife"}, 9385), // Deadfall trap ID
    PITFALL_TRAP(new String[]{"Logs", "Knife", "Teasing stick"}, 9401), // Pitfall trap ID
    TRACKING("Noose wand", -1), // No specific trap object ID for tracking
    FALCONRY("Falcon", -1), // No trap object ID for falconry
    BUTTERFLY_NET(new String[]{"Butterfly net", "Butterfly jar"}, -1), // No specific object for butterfly hunting
    IMPLINGS(new String[]{"Butterfly net", "Impling jar"}, -1); // No specific trap object for implings

    private final List<String> requiredItems;
    private final int interactableObjectId;

    HunterType(String item, int interactableObjectId) {
        this.requiredItems = List.of(item);
        this.interactableObjectId = interactableObjectId;
    }

    HunterType(String[] items, int interactableObjectId) {
        this.requiredItems = Arrays.asList(items);
        this.interactableObjectId = interactableObjectId;
    }

    public List<String> getRequiredItems() {
        return requiredItems;
    }

    public int getInteractableObjectId() {
        return interactableObjectId;
    }
}