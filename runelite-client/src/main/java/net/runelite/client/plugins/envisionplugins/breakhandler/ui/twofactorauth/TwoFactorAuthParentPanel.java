package net.runelite.client.plugins.envisionplugins.breakhandler.ui.twofactorauth;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.account.AccountPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.account.AccountTitlePanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;

public class TwoFactorAuthParentPanel extends JPanel {
    public TwoFactorAuthParentPanel() {
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BreakHandlerPanel.BORDER);

        add(new TwoFactorAuthTitlePanel());
        add(new TwoFactorAuthPanel());
    }
}
