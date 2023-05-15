package net.runelite.client.plugins.microbot.agility;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.agility.models.AgilityObstacleModel;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

import static net.runelite.api.ObjectID.*;

@PluginDescriptor(
        name = "Micro Agility",
        description = "Microbot ag!lity plugin",
        tags = {"agility", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class MicroAgilityPlugin extends Plugin {
    @Inject
    private MicroAgilityConfig config;
    @Provides
    MicroAgilityConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MicroAgilityConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private MicroAgilityOverlay agilityOverlay;

    @Inject
    AgilityScript agilityScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(agilityOverlay);
        }
        //canafis
        agilityScript.canafisCourse.add(new AgilityObstacleModel(TALL_TREE_14843));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14844));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14845));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14848));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14846));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(POLEVAULT));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14847));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14897));

        agilityScript.faladorCourse.add(new AgilityObstacleModel(TIGHTROPE_14899));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(HAND_HOLDS_14901));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(GAP_14903));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(GAP_14904));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(TIGHTROPE_14905));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(TIGHTROPE_14911));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(GAP_14919));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(LEDGE_14920));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(LEDGE_14921));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(LEDGE_14922));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(LEDGE_14924));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(EDGE_14925));
        agilityScript.run(config);}

    protected void shutDown() {
        agilityScript.shutdown();
        agilityScript.canafisCourse = new ArrayList<>();
        agilityScript.faladorCourse = new ArrayList<>();
        overlayManager.remove(agilityOverlay);
    }
}
