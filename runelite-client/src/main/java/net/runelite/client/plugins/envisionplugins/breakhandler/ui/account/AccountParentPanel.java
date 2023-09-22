package net.runelite.client.plugins.envisionplugins.breakhandler.ui.account;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration.BreakDurationTitlePanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.TimeAmountPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class AccountParentPanel extends JPanel {
    public AccountParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BreakHandlerPanel.BORDER);

        add(new AccountTitlePanel());
        add(new AccountPanel());
    }
}
