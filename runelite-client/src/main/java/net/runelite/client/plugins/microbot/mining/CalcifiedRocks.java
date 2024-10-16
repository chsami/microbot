package net.runelite.client.plugins.microbot.mining;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;

public class CalcifiedRocks {

    // Constant for the water rock object ID
    private static final int WATER_ROCK_DECORATION_ID = 51493;
    private static boolean isInteractingWithCrack = false;

    // Method to interact with the water rock when cracks are detected
    public static void interactWithCracks() {
        if (!isInteractingWithCrack) {
            isInteractingWithCrack = true; // Set the flag to indicate we're interacting with the crack
            Microbot.log("Attempting to interact with water rock (ID: " + WATER_ROCK_DECORATION_ID + ").");

            // Attempt to interact with the water rock using the known ID
            if (Rs2GameObject.interact(WATER_ROCK_DECORATION_ID)) {
                Microbot.log("Successfully interacted with the water rock (ID: " + WATER_ROCK_DECORATION_ID + ").");
            } else {
                Microbot.log("Failed to interact with the water rock (ID: " + WATER_ROCK_DECORATION_ID + ").");
            }

            // After attempting interaction, reset the flag to allow normal mining
            isInteractingWithCrack = false;
        }
    }

    public static boolean isInteracting() {
        return isInteractingWithCrack;
    }
}
