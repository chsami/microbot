package net.runelite.client.plugins.microbot.qualityoflife.scripts;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;

public class CameraScript {
    public static void fixPitch() {
        Microbot.log("Fixing camera pitch");
        Microbot.log("Current pitch: " + Rs2Camera.getPitch());
        Microbot.log("Target pitch: 280");
        // Set the camera pitch to 280
        if (Rs2Camera.getPitch() < 280)
            Rs2Camera.setPitch(280);
    }

    public static void fixZoom() {
        Microbot.log("Fixing camera zoom");
        Microbot.log("Current zoom: " + Rs2Camera.getZoom());
        Microbot.log("Target zoom: 200");
        // Set the camera zoom to 200
        if (Rs2Camera.getZoom() > 200)
            Rs2Camera.setZoom(200);
    }
}
