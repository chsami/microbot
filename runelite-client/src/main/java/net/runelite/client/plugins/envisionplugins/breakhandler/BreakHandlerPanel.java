package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BreakHandlerPanel extends PluginPanel {

    @Inject
    BreakHandlerScript breakHandlerScript;

    private final JPanel mainPanel = new JPanel();
    private final JPanel breakMethodPanel = new JPanel();
    private final JPanel breakDurationPanel = new JPanel();
    private final JPanel playTimeDurationPanel = new JPanel();
    private final JPanel forceBreakPanel = new JPanel();


    private static final Border BORDER = new CompoundBorder(
            BorderFactory.createMatteBorder(2, 2, 2, 2, ColorScheme.LIGHT_GRAY_COLOR),
            BorderFactory.createLineBorder(ColorScheme.LIGHT_GRAY_COLOR));

    @Inject
    BreakHandlerPanel() {
        super(false);

        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        setBorder(new EmptyBorder(6, 6, 6, 6));

        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(5, 0, 0, 0, ColorScheme.DARK_GRAY_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        mainPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setVisible(true);

        /* Title Panel */
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titlePanel.setLayout(new BorderLayout());

        JLabel title = new JLabel();
        title.setText("Micro Break Handler V" + BreakHandlerScript.version);
        title.setForeground(Color.WHITE);
        titlePanel.add(title, BorderLayout.WEST);


        /* Break Method Panel */
        final JPanel breakingMethodPanelParent = new JPanel();
        BoxLayout boxLayoutBreakMethods = new BoxLayout(breakingMethodPanelParent, BoxLayout.Y_AXIS);
        breakingMethodPanelParent.setLayout(boxLayoutBreakMethods);
        breakingMethodPanelParent.setBorder(BORDER);

        final JPanel breakingMethodPanelChild = new JPanel();
        final JPanel breakMethodPanelContents = new JPanel();

        breakingMethodPanelChild.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        breakingMethodPanelChild.setLayout(new BorderLayout());
        breakingMethodPanelChild.setBorder(new EmptyBorder(0, 0, 0, 0));

        breakMethodPanelContents.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        breakMethodPanelContents.setLayout(new BorderLayout());
        breakMethodPanelContents.setBorder(new EmptyBorder(5, 5, 5, 10));

        JLabel breakMethodLabel = new JLabel();
        breakMethodLabel.setText("Break Method");
        breakMethodLabel.setForeground(Color.WHITE);
        breakMethodPanelContents.add(breakMethodLabel, BorderLayout.NORTH);

        String[] breakMethods = {"AFK", "LOGOUT"};
        JComboBox breakingMethodCombobox = new JComboBox(breakMethods);
        breakMethodPanelContents.add(breakingMethodCombobox, BorderLayout.SOUTH);

        breakingMethodPanelChild.add(breakMethodPanelContents, BorderLayout.NORTH);
        breakingMethodPanelParent.add(breakingMethodPanelChild);
        breakingMethodPanelParent.setSize(24, 50);




        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(breakingMethodPanelParent);

        add(mainPanel);
    }
}
