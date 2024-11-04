package net.runelite.client.plugins.microbot.bankjs.BanksBankStander;

import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BanksBankStanderOverlay extends OverlayPanel {
    private final BanksBankStanderConfig config;
    @Inject
    BanksBankStanderOverlay(BanksBankStanderPlugin plugin, BanksBankStanderConfig config) {
        super(plugin);
        this.config=config;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Bank's BankStander V" + BanksBankStanderScript.version)
                    .color(Color.GREEN)
                    .build());
            ///* Added by Storm
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("New features added by eXioStorm")
                    .leftColor(PluginDescriptor.stormColor)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Items processed : " + BanksBankStanderScript.itemsProcessed)
                    .leftColor(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Item 1 : " + BanksBankStanderScript.firstIdentity+", Quantity : "+BanksBankStanderScript.firstItemSum)
                    .leftColor(Color.GREEN)
                    .build());
            if(config.secondItemQuantity() > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Item 2 : " + BanksBankStanderScript.secondIdentity + ", Quantity : " + BanksBankStanderScript.secondItemSum)
                        .leftColor(Color.GREEN)
                        .build());
            }
            if(config.thirdItemQuantity() > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Item 3 : " + BanksBankStanderScript.thirdIdentity+", Quantity : "+BanksBankStanderScript.thirdItemSum)
                        .leftColor(Color.GREEN)
                        .build());
            }
            if(config.fourthItemQuantity() > 0 ) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Item 4 : " + BanksBankStanderScript.fourthIdentity+", Quantity : "+BanksBankStanderScript.fourthItemSum)
                        .leftColor(Color.GREEN)
                        .build());
            }//*/ Added by Storm
            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
