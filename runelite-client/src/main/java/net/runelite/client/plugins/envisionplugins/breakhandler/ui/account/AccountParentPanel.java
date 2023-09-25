package net.runelite.client.plugins.envisionplugins.breakhandler.ui.account;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration.BreakDurationTitlePanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.TimeAmountPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountParentPanel extends JPanel {
    public AccountParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(20, 0, 0, 0));
        Dimension d = new Dimension(300, 80);
        setSize(d);
        setPreferredSize(d);
        setMaximumSize(d);

        add(new AccountTitlePanel());
        add(new AccountPanel());
    }
}
