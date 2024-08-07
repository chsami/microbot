package net.runelite.client.plugins.microbot.agility;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.agility.models.AgilityObstacleModel;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.plugins.microbot.util.misc.Operation;
import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

import static net.runelite.api.ObjectID.*;

@PluginDescriptor(

        name = PluginDescriptor.Mocrosoft + "Agility",
        description = "Microbot agility plugin",
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

        // Draynor
        agilityScript.draynorCourse.add(new AgilityObstacleModel(ROUGH_WALL));
        agilityScript.draynorCourse.add(new AgilityObstacleModel(TIGHTROPE));
        agilityScript.draynorCourse.add(new AgilityObstacleModel(TIGHTROPE_11406));
        agilityScript.draynorCourse.add(new AgilityObstacleModel(NARROW_WALL));
        agilityScript.draynorCourse.add(new AgilityObstacleModel(WALL_11630, -1, 3256, Operation.GREATER, Operation.GREATER_EQUAL));
        agilityScript.draynorCourse.add(new AgilityObstacleModel(GAP_11631, -1, 3255, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.draynorCourse.add(new AgilityObstacleModel(CRATE_11632));

        // Al Kharid
        agilityScript.alkharidCourse.add(new AgilityObstacleModel(ROUGH_WALL_11633));
        agilityScript.alkharidCourse.add(new AgilityObstacleModel(TIGHTROPE_14398));
        agilityScript.alkharidCourse.add(new AgilityObstacleModel(CABLE));
        agilityScript.alkharidCourse.add(new AgilityObstacleModel(ZIP_LINE_14403));
        agilityScript.alkharidCourse.add(new AgilityObstacleModel(TROPICAL_TREE_14404));
        agilityScript.alkharidCourse.add(new AgilityObstacleModel(ROOF_TOP_BEAMS));
        agilityScript.alkharidCourse.add(new AgilityObstacleModel(TIGHTROPE_14409));
        agilityScript.alkharidCourse.add(new AgilityObstacleModel(GAP_14399));

        // Varrock
        agilityScript.varrockCourse.add(new AgilityObstacleModel(ROUGH_WALL_14412));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(CLOTHES_LINE));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(GAP_14414));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(WALL_14832));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(GAP_14833)); // this obstacle doesn't always work for some reason
        agilityScript.varrockCourse.add(new AgilityObstacleModel(GAP_14834));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(GAP_14835, -1, 3402, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(LEDGE_14836, -1, 3408, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.varrockCourse.add(new AgilityObstacleModel(EDGE));

        //gnome stronghold
        agilityScript.gnomeStrongholdCourse.add(new AgilityObstacleModel(LOG_BALANCE_23145));
        agilityScript.gnomeStrongholdCourse.add(new AgilityObstacleModel(OBSTACLE_NET_23134));
        agilityScript.gnomeStrongholdCourse.add(new AgilityObstacleModel(TREE_BRANCH_23559));
        agilityScript.gnomeStrongholdCourse.add(new AgilityObstacleModel(BALANCING_ROPE_23557));
        agilityScript.gnomeStrongholdCourse.add(new AgilityObstacleModel(TREE_BRANCH_23560));
        agilityScript.gnomeStrongholdCourse.add(new AgilityObstacleModel(OBSTACLE_NET_23135));
        agilityScript.gnomeStrongholdCourse.add(new AgilityObstacleModel(OBSTACLE_PIPE_23138));


        //canafis
        agilityScript.canafisCourse.add(new AgilityObstacleModel(TALL_TREE_14843));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14844));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14845));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14848));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14846));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(POLEVAULT));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14847));
        agilityScript.canafisCourse.add(new AgilityObstacleModel(GAP_14897));

        // Falador
        agilityScript.faladorCourse.add(new AgilityObstacleModel(ROUGH_WALL_14898));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(TIGHTROPE_14899));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(HAND_HOLDS_14901));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(GAP_14903, -1, 3358, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(GAP_14904));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(TIGHTROPE_14905));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(TIGHTROPE_14911));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(GAP_14919));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(LEDGE_14920, 3016, -1, Operation.GREATER_EQUAL, Operation.GREATER));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(LEDGE_14921, -1, 3343, Operation.GREATER, Operation.GREATER_EQUAL));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(LEDGE_14922, -1, 3335, Operation.GREATER, Operation.GREATER_EQUAL));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(LEDGE_14924, 3017, -1, Operation.LESS, Operation.GREATER));
        agilityScript.faladorCourse.add(new AgilityObstacleModel(EDGE_14925));

        // Seers
        agilityScript.seersCourse.add(new AgilityObstacleModel(WALL_14927));
        agilityScript.seersCourse.add(new AgilityObstacleModel(GAP_14928));
        agilityScript.seersCourse.add(new AgilityObstacleModel(TIGHTROPE_14932));
        agilityScript.seersCourse.add(new AgilityObstacleModel(GAP_14929));
        agilityScript.seersCourse.add(new AgilityObstacleModel(GAP_14930));
        agilityScript.seersCourse.add(new AgilityObstacleModel(EDGE_14931));

        // Pollnivneach
        agilityScript.polnivCourse.add(new AgilityObstacleModel(BASKET_14935));
        agilityScript.polnivCourse.add(new AgilityObstacleModel(MARKET_STALL_14936, -1, 2968, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.polnivCourse.add(new AgilityObstacleModel(BANNER_14937, -1, 2976, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.polnivCourse.add(new AgilityObstacleModel(GAP_14938, 3362, -1, Operation.LESS_EQUAL, Operation.GREATER));
        agilityScript.polnivCourse.add(new AgilityObstacleModel(TREE_14939, 3366, -1, Operation.GREATER_EQUAL, Operation.GREATER));
        agilityScript.polnivCourse.add(new AgilityObstacleModel(ROUGH_WALL_14940, -1, 2982, Operation.GREATER, Operation.GREATER_EQUAL));
        agilityScript.polnivCourse.add(new AgilityObstacleModel(MONKEYBARS));
        agilityScript.polnivCourse.add(new AgilityObstacleModel(TREE_14944, -1, 2996, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.polnivCourse.add(new AgilityObstacleModel(DRYING_LINE));

        agilityScript.rellekkaCourse.add(new AgilityObstacleModel(ROUGH_WALL_14946));
        agilityScript.rellekkaCourse.add(new AgilityObstacleModel(GAP_14947, -1, 3672, Operation.GREATER, Operation.GREATER));
        agilityScript.rellekkaCourse.add(new AgilityObstacleModel(TIGHTROPE_14987));
        agilityScript.rellekkaCourse.add(new AgilityObstacleModel(GAP_14990));
        agilityScript.rellekkaCourse.add(new AgilityObstacleModel(GAP_14991, -1, 3653, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.rellekkaCourse.add(new AgilityObstacleModel(TIGHTROPE_14992));
        agilityScript.rellekkaCourse.add(new AgilityObstacleModel(PILE_OF_FISH));

        agilityScript.ardougneCourse.add(new AgilityObstacleModel(WOODEN_BEAMS));
        agilityScript.ardougneCourse.add(new AgilityObstacleModel(GAP_15609));
        agilityScript.ardougneCourse.add(new AgilityObstacleModel(PLANK_26635));
        agilityScript.ardougneCourse.add(new AgilityObstacleModel(GAP_15610));
        agilityScript.ardougneCourse.add(new AgilityObstacleModel(GAP_15611, -1, 3310, Operation.GREATER, Operation.GREATER_EQUAL));
        agilityScript.ardougneCourse.add(new AgilityObstacleModel(STEEP_ROOF));
        agilityScript.ardougneCourse.add(new AgilityObstacleModel(GAP_15612));

        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(LADDER_36221));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(TIGHTROPE_36225));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(CHIMNEY_36227));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(ROOF_EDGE));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(DARK_HOLE_36229));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(LADDER_36231));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(ROPE_BRIDGE_36233, 2265, -1, Operation.GREATER_EQUAL, Operation.GREATER));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(TIGHTROPE_36234, 2254, -1, Operation.GREATER_EQUAL, Operation.GREATER));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(ROPE_BRIDGE_36235, -1, 3398, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(TIGHTROPE_36236, -1, 3409, Operation.GREATER, Operation.LESS_EQUAL));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(TIGHTROPE_36237));
        agilityScript.prifddinasCourse.add(new AgilityObstacleModel(DARK_HOLE_36238));



        agilityScript.run(config);
    }

    protected void shutDown() {
        agilityScript.shutdown();
        agilityScript.canafisCourse = new ArrayList<>();
        agilityScript.faladorCourse = new ArrayList<>();
        agilityScript.alkharidCourse = new ArrayList<>();
        agilityScript.draynorCourse = new ArrayList<>();
        agilityScript.gnomeStrongholdCourse = new ArrayList<>();
        agilityScript.polnivCourse = new ArrayList<>();
        agilityScript.varrockCourse = new ArrayList<>();
        agilityScript.rellekkaCourse = new ArrayList<>();
        agilityScript.ardougneCourse = new ArrayList<>();
        agilityScript.prifddinasCourse = new ArrayList<>();

        overlayManager.remove(agilityOverlay);
    }
}