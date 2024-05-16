package net.runelite.client.plugins.microbot;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

public class InputSelector {

    private static final BufferedImage ENABLED_IMAGE, DISABLED_IMAGE;

    static {
        ENABLED_IMAGE = ImageUtil.loadImageResource(Microbot.class, "enabled_small.png");
        DISABLED_IMAGE = ImageUtil.loadImageResource(Microbot.class, "disabled_small.png");
    }

    private final ClientToolbar clientToolbar;
    private NavigationButton enableButton;
    private NavigationButton disableButton;

    public InputSelector(ClientToolbar clientToolbar) {
        this.clientToolbar = clientToolbar;
        startUp();
    }

    public void startUp() {
        enableButton = NavigationButton.builder().tab(false).icon(ENABLED_IMAGE).tooltip("Enable Input").onClick(this::enableClick).build();
        disableButton = NavigationButton.builder().tab(false).icon(DISABLED_IMAGE).tooltip("Disable Input").onClick(this::disableClick).build();
        addAndRemoveButtons();
    }

    private void addAndRemoveButtons() {
        clientToolbar.removeNavigation(enableButton);
        clientToolbar.removeNavigation(disableButton);
        clientToolbar.addNavigation(!ClientUI.getClient().isEnabled() ? enableButton : disableButton);
    }

    public void enableClick() {
        ClientUI.getClient().setEnabled(true);
        Microbot.getClient().getCanvas().setFocusable(true);
        addAndRemoveButtons();
    }

    public void disableClick() {
        ClientUI.getClient().setEnabled(false);
        Microbot.getClient().getCanvas().setFocusable(false);
        addAndRemoveButtons();
    }
}
