package net.runelite.client.plugins.microbot.qualityoflife.scripts;

import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;

public class CameraScript {
    public static void fixPitch() {
        // Set the camera pitch to 280
        if (Rs2Camera.getPitch() < 280)
            Rs2Camera.setPitch(280);
    }

    public static void fixZoom() {
        // Set the camera zoom to 200
        if (Rs2Camera.getZoom() > 200)
            Rs2Camera.setZoom(200);
    }
}
