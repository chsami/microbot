 package net.runelite.client.plugins.microbot.PlayerAlarm;

 import java.awt.Color;
 import java.awt.Dimension;
 import java.awt.Graphics2D;
 import javax.inject.Inject;
 import net.runelite.api.Client;
 import net.runelite.client.ui.overlay.OverlayPanel;
 import net.runelite.client.ui.overlay.components.LineComponent;

 public class AlarmOverlay
   extends OverlayPanel {
   private final PlayerAlarmConfig config;
   private final Client client;
   private boolean playAlarm = false;

   @Inject
   private AlarmOverlay(PlayerAlarmConfig config, Client client) {
     this.config = config;
     this.client = client;
   }
   public Dimension render(Graphics2D graphics) {
     this.panelComponent.getChildren().clear();
     this.panelComponent.setPreferredSize(new Dimension(this.client.getCanvasWidth(), this.client.getCanvasHeight()));
     for (int i = 0; i < 100; i++)
     {
       this.panelComponent.getChildren().add(LineComponent.builder()
           .left(" ")
           .build());
     }
     if (this.client.getGameCycle() % 20 >= 10) {
    if (!playAlarm) {
        if(config.playSound()){ client.playSoundEffect(3929, 127); }
        playAlarm = true;
    }
       this.panelComponent.setBackgroundColor(this.config.flashColor());
     } else {
       if(playAlarm){
           playAlarm = false;
       }
       this.panelComponent.setBackgroundColor(new Color(0, 0, 0, 0));
     }
     return this.panelComponent.render(graphics);
   }
 }