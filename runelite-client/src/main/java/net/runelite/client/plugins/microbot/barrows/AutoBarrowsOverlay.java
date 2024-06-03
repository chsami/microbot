package net.runelite.client.plugins.microbot.barrows;

import net.runelite.api.Varbits;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.barrows.models.TheBarrowsBrothers;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

public class AutoBarrowsOverlay  extends OverlayPanel{

    private static final DecimalFormat REWARD_POTENTIAL_FORMATTER = new DecimalFormat("##0.00%");


    AutoBarrowsPlugin plugin;
    @Inject
    AutoBarrowsOverlay(AutoBarrowsPlugin plugin)
    {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }


    @Override
    public Dimension render(Graphics2D graphics) {

        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Auto Barrows V" + AutoBarrowsScript.version)
                .color(Color.blue)
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

        panelComponent.getChildren().add(LineComponent.builder()
                .left("In Crypt: ")
                .right(String.valueOf(plugin.isInCrypt()))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Prayer: ")
                .right(String.valueOf(plugin.recommendedPrayer.getAction()))
                .build());


        final int rewardPotential = rewardPotential();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Potential")
                .right(REWARD_POTENTIAL_FORMATTER.format(rewardPotential / 1012f))
                .rightColor(rewardPotential >= 756 && rewardPotential < 881 ? Color.GREEN : rewardPotential < 631 ? Color.WHITE : Color.YELLOW)
                .build());


        for (TheBarrowsBrothers brother : TheBarrowsBrothers.values())
        {
            final boolean brotherSlain = Microbot.getClient().getVarbitValue(brother.getKilledVarbit()) > 0;
            String slain = brotherSlain ? "\u2713" : "\u2717";
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(brother.getName())
                    .right(slain)
                    .rightFont(FontManager.getDefaultFont())
                    .rightColor(brotherSlain ? Color.GREEN : Color.RED)
                    .build());
        }

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(plugin.barrowsScript.state.toString())
                    .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left(Microbot.status.toString())
                .build());

        return panelComponent.render(graphics);
    }

    private int rewardPotential()
    {
        // this is from [proc,barrows_overlay_reward]
        int brothers = Microbot.getClient().getVarbitValue(Varbits.BARROWS_KILLED_AHRIM)
                + Microbot.getClient().getVarbitValue(Varbits.BARROWS_KILLED_DHAROK)
                + Microbot.getClient().getVarbitValue(Varbits.BARROWS_KILLED_GUTHAN)
                + Microbot.getClient().getVarbitValue(Varbits.BARROWS_KILLED_KARIL)
                + Microbot.getClient().getVarbitValue(Varbits.BARROWS_KILLED_TORAG)
                + Microbot.getClient().getVarbitValue(Varbits.BARROWS_KILLED_VERAC);
        return Microbot.getClient().getVarbitValue(Varbits.BARROWS_REWARD_POTENTIAL) + brothers * 2;
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
