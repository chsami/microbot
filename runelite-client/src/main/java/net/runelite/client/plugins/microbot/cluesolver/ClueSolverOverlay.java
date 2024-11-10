package net.runelite.client.plugins.microbot.cluesolver;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

@Setter
@Slf4j
public class ClueSolverOverlay extends Overlay {

    private final ClueSolverPlugin plugin;
    private String currentTaskStatus = "Idle";  // Default message

    @Inject
    public ClueSolverOverlay(ClueSolverPlugin plugin) {
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);  // Position near bottom-left
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    /**
     * Updates the overlay message with the current task status.
     * @param status The current status message of the task.
     */
    public void updateTaskStatus(String status) {
        this.currentTaskStatus = status;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            graphics.setColor(Color.WHITE);
            graphics.drawString("Clue Solver Status:", 10, 20);

            // Display the current task status dynamically
            graphics.drawString(currentTaskStatus, 10, 40);

            // Placeholder for additional information (e.g., clue type, progress)
            // graphics.drawString("Clue Type: " + getClueType(), 10, 60);

            return null;
        } catch (Exception e) {
            log.error("Error rendering ClueSolverOverlay", e);
            return null;
        }
    }

    /**
     * Example method to fetch the current clue type or details (optional).
     * This method can be used to get more details from ClueSolverPlugin or tasks.
     */
    private String getClueType() {
        // Placeholder: logic to obtain current clue type, if available
        return "Example Clue Type";
    }
}
