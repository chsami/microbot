package net.runelite.client.plugins.microbot.scripts.movie;

import net.runelite.client.plugins.microbot.scripts.Scripts;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

public class UsernameHiderScript extends Scripts {
    @Override
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            //doesn't really work when the screen is refreshing (moving camera)
            Rs2Widget.changeWidgetText("Valeron", "Microbot Limited");
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
}
