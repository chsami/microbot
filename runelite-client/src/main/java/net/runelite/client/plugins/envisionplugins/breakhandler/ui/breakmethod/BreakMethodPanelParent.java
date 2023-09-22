package net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakmethod;

import javax.swing.*;
import java.awt.*;

public class BreakMethodPanelParent extends JPanel {


    public BreakMethodPanelParent() {
        BoxLayout boxLayoutBreakMethods = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayoutBreakMethods);

        final BreakingMethodPanelChild breakingMethodPanelChild = new BreakingMethodPanelChild();
        final BreakMethodContentPanel breakMethodContentPanel = new BreakMethodContentPanel();
        breakingMethodPanelChild.add(breakMethodContentPanel, BorderLayout.NORTH);

        add(breakingMethodPanelChild);
    }

}
