package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.*;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration.BreakDurationParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakmethod.BreakMethodParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer.BreakTimerParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimeduration.RuntimeDurationParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimer.RunTimerParentPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class BreakHandlerPanel extends PluginPanel {

    @Inject
    BreakHandlerScript breakHandlerScript;

    private final JPanel breakMethodPanel = new JPanel();
    private final JPanel breakDurationPanel = new JPanel();
    private final JPanel playTimeDurationPanel = new JPanel();
    private final JPanel forceBreakPanel = new JPanel();


    public static final Border BORDER = new CompoundBorder(
            BorderFactory.createMatteBorder(2, 2, 2, 2, ColorScheme.LIGHT_GRAY_COLOR),
            BorderFactory.createLineBorder(ColorScheme.LIGHT_GRAY_COLOR));

    @Inject
    BreakHandlerPanel() {
        super(false);

        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        setBorder(new EmptyBorder(6, 6, 6, 6));

        add(new TitlePanel());
        add(new BreakMethodParentPanel());
        add(new BreakDurationParentPanel());
        add(new BreakTimerParentPanel());
        add(new RuntimeDurationParentPanel());
        add(new RunTimerParentPanel());

    }
}
