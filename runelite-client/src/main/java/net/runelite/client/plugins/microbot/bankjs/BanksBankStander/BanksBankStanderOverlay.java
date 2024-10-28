package net.runelite.client.plugins.microbot.bankjs.BanksBankStander;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class BanksBankStanderOverlay extends OverlayPanel {
    @Inject
    BanksBankStanderOverlay(BanksBankStanderPlugin plugin) {
        super(plugin);
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
                    .left("Items processed : " + BanksBankStanderScript.itemsProcessed)
                    .leftColor(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Item 1 : " + BanksBankStanderScript.firstIdentity)
                    .leftColor(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Item 2 : " + BanksBankStanderScript.secondIdentity)
                    .leftColor(Color.GREEN)
                    .build());
            if(BanksBankStanderScript.fourItems){
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Item 3 : " + BanksBankStanderScript.thirdIdentity)
                    .leftColor(Color.GREEN)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Item 4 : " + BanksBankStanderScript.fourthIdentity)
                    .leftColor(Color.GREEN)
                    .build()); }//*/ Added by Storm
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
