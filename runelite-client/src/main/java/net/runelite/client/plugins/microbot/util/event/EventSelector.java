package net.runelite.client.plugins.microbot.util.event;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

public class EventSelector {

    private static final BufferedImage ENABLED_IMAGE, DISABLED_IMAGE;

    static {
        ENABLED_IMAGE = ImageUtil.loadImageResource(Microbot.class, "enabled_small.png");
        DISABLED_IMAGE = ImageUtil.loadImageResource(Microbot.class, "disabled_small.png");
    }

    private final ClientToolbar clientToolbar;
    private NavigationButton enableButton;
    private NavigationButton disableButton;

    public EventSelector(ClientToolbar clientToolbar) {
        this.clientToolbar = clientToolbar;
    }

    public void startUp() {
        enableButton = NavigationButton.builder().tab(false).icon(ENABLED_IMAGE).tooltip("Enable Input").onClick(this::enableClick).build();
        disableButton = NavigationButton.builder().tab(false).icon(DISABLED_IMAGE).tooltip("Disable Input").onClick(this::disableClick).build();
        addAndRemoveButtons();
    }


    public void shutDown() {
        clientToolbar.removeNavigation(enableButton);
        clientToolbar.removeNavigation(disableButton);
    }

    private void addAndRemoveButtons() {
        clientToolbar.removeNavigation(enableButton);
        clientToolbar.removeNavigation(disableButton);
        clientToolbar.addNavigation(Microbot.getEventHandler().isBlocked() ? enableButton : disableButton);
    }

    public void enableClick() {
        Microbot.getEventHandler().setBlocked(false);
        addAndRemoveButtons();
    }

    public void disableClick() {
        Microbot.getEventHandler().setBlocked(true);
        addAndRemoveButtons();
    }
}
