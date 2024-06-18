package net.runelite.client.plugins.microbot.bankjs.BanksShopper;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.QuantityFormatter;

import javax.inject.Inject;
import java.awt.*;

public class BanksShopperOverlay extends OverlayPanel {
    private final BanksShopperConfig config;

    @Inject
    BanksShopperOverlay(BanksShopperPlugin plugin, BanksShopperConfig config) {
        super(plugin);
        this.config = config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Bank's Shopper V" + BanksShopperScript.version)
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());
            if (config.action() == Actions.BUY) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Total gp items bought " + formattedProfit())
                        .build());
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }

    public static String formattedProfit() {
        return QuantityFormatter.quantityToRSDecimalStack(BanksShopperScript.getProfit(), true);
    }
}
