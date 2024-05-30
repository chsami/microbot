package net.runelite.client.plugins.microbot.barrows;

import net.runelite.api.NpcID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.derangedarchaeologist.DerangedAchaeologistPlugin;
import net.runelite.client.plugins.microbot.vorkath.VorkathScript;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class BarrowsOverlay  extends OverlayPanel{

    BarrowsPlugin plugin;
    @Inject
    BarrowsOverlay(BarrowsPlugin plugin)
    {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
        setPriority(OverlayPriority.HIGH);

    }


    @Override
    public Dimension render(Graphics2D graphics) {

        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Pumsters Barrows V" + BarrowsScript.version)
                .color(Color.GREEN)
                .build());


        // Add the current crypt information to the panel
        String currentCrypt = plugin.getCurrentCrypt();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current Crypt: ")
                .right(currentCrypt)
                .build());

        // Add the recommended prayer information to the panel
        String recommendedPrayer = plugin.getRecommendedPrayer();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Is fighting brother:")
                .right(String.valueOf(plugin.isPlayerFightingBrother()))
                .build());

        // Add the next crypt to visit information to the panel
        String nextCrypt = plugin.getNextCrypt();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Next Crypt: ")
                .right(nextCrypt)
                .build());

        // Add the status of each Barrows Brother to the panel
        Map<Integer, Boolean> barrowsBrothersStatus = plugin.getBarrowsBrothersStatus();

        addBrotherStatus(barrowsBrothersStatus, NpcID.AHRIM_THE_BLIGHTED, "Ahrim");
        addBrotherStatus(barrowsBrothersStatus, NpcID.DHAROK_THE_WRETCHED, "Dharok");
        addBrotherStatus(barrowsBrothersStatus, NpcID.GUTHAN_THE_INFESTED, "Guthan");
        addBrotherStatus(barrowsBrothersStatus, NpcID.KARIL_THE_TAINTED, "Karil");
        addBrotherStatus(barrowsBrothersStatus, NpcID.TORAG_THE_CORRUPTED, "Torag");
        addBrotherStatus(barrowsBrothersStatus, NpcID.VERAC_THE_DEFILED, "Verac");
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(plugin.barrowsScript.state.toString())
                    .build());

        return panelComponent.render(graphics);
    }

    private void addBrotherStatus(Map<Integer, Boolean> statusMap, int npcId, String name) {
        boolean isAlive = statusMap.getOrDefault(npcId, true);
        panelComponent.getChildren().add(LineComponent.builder()
                .left(name)
                .right(isAlive ? "Alive" : "Dead")
                .build());
    }

//    @Override
//    public Dimension render(Graphics2D graphics) {
//        try {
//            panelComponent.setPreferredSize(new Dimension(200, 300));
//            panelComponent.getChildren().add(TitleComponent.builder()
//                    .text("Pumsters Barrows V" + BarrowsScript.version)
//                    .color(Color.GREEN)
//                    .build());
//
//            panelComponent.getChildren().add(LineComponent.builder().build());
//
//            panelComponent.getChildren().add(LineComponent.builder()
//                    .left(Microbot.status)
//                    .build());
//
//            panelComponent.getChildren().add(LineComponent.builder()
//                    .left(plugin.barrowsScript.state.toString())
//                    .build());
//
//
//        } catch(Exception ex) {
//            System.out.println(ex.getMessage());
//        }
//        return super.render(graphics);
//    }
}
