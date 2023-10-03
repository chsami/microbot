package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.*;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.account.AccountParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakduration.BreakDurationParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breakmethod.BreakMethodParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.MaximumTimeAmount;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.common.timeamount.MinimumTimeAmount;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes.CurrentTimesParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.enums.TimeDurationType;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.error.ErrorParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.regeneratetimes.RegenerateTimesParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimeduration.RuntimeDurationParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.twofactorauth.TwoFactorAuthParentPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.worldhopping.WorldHoppingParentPanel;
import net.runelite.client.plugins.microbot.util.security.Encryption;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class BreakHandlerPanel extends PluginPanel {

    @Inject
    BreakHandlerScript breakHandlerScript;

    private final RuntimeDurationParentPanel runtimeDurationParentPanel;
    private final BreakDurationParentPanel breakDurationParentPanel;
    TwoFactorAuthParentPanel twoFactorAuthParentPanel = new TwoFactorAuthParentPanel();
    AccountParentPanel accountParentPanel = new AccountParentPanel();
    WorldHoppingParentPanel worldHoppingParentPanel = new WorldHoppingParentPanel();
    JPanel timers = new JPanel();
    CurrentTimesParentPanel currentTimesParentPanel = new CurrentTimesParentPanel();
    RegenerateTimesParentPanel regenerateTimesParentPanel = new RegenerateTimesParentPanel();

    ErrorParentPanel errorParentPanel = new ErrorParentPanel();

    @Inject
    BreakHandlerPanel() {
        super(false);

        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);
        setBorder(new EmptyBorder(6, 6, 6, 6));

        add(new HeaderPanel());
        JTabbedPane tabbedPane = new JTabbedPane();

        // Timers Panel
        BoxLayout boxLayoutTimers = new BoxLayout(timers, BoxLayout.Y_AXIS);
        timers.setLayout(boxLayoutTimers);
        timers.add(currentTimesParentPanel);
        timers.add(regenerateTimesParentPanel);
        tabbedPane.add("Timers", timers);

        // Account Panel
        JPanel account = new JPanel();
        BoxLayout boxLayoutAccount = new BoxLayout(account, BoxLayout.Y_AXIS);
        account.setLayout(boxLayoutAccount);
        account.add(accountParentPanel);
        account.add(twoFactorAuthParentPanel);
        tabbedPane.add("Account", account);

        // Settings Panel
        JPanel settings = new JPanel();
        BoxLayout boxLayoutSettings = new BoxLayout(settings, BoxLayout.Y_AXIS);
        settings.setLayout(boxLayoutSettings);
        breakDurationParentPanel = new BreakDurationParentPanel();
        runtimeDurationParentPanel = new RuntimeDurationParentPanel();
        settings.add(new BreakMethodParentPanel());
        settings.add(breakDurationParentPanel);
        settings.add(runtimeDurationParentPanel);
        settings.add(worldHoppingParentPanel);
        tabbedPane.add("Settings", settings);

        add(tabbedPane);

    }

    public MinimumTimeAmount getMinimumTimeAmount(TimeDurationType timeDurationType) throws Exception {
        MinimumTimeAmount minimumTimeAmount;
        switch (timeDurationType) {
            case RUNTIME_DURATION:
                minimumTimeAmount = runtimeDurationParentPanel.getMinimumTimeAmount();
                break;
            case BREAK_DURATION:
                minimumTimeAmount = breakDurationParentPanel.getMinimumTimeAmount();
                break;
            default:
                throw new Exception("Unknown TimeDurationType");
        }
        return minimumTimeAmount;
    }

    public MaximumTimeAmount getMaximumTimeAmount(TimeDurationType timeDurationType) throws Exception {
        MaximumTimeAmount maximumTimeAmount;
        switch (timeDurationType) {
            case RUNTIME_DURATION:
                maximumTimeAmount = runtimeDurationParentPanel.getMaximumTimeAmount();
                break;
            case BREAK_DURATION:
                maximumTimeAmount = breakDurationParentPanel.getMaximumTimeAmount();
                break;
            default:
                throw new Exception("Unknown TimeDurationType");
        }
        return maximumTimeAmount;
    }

    public String getBreakMethod() {
        return BreakMethodParentPanel.getBreakMethod();
    }

    public JTextField getUsername() {
        return accountParentPanel.getUsername();
    }

    private JPasswordField getPassword() {
        return accountParentPanel.getPassword();
    }

    public boolean isPasswordValid() {
        return accountParentPanel.getPassword().getPassword().length >= 5;
    }

    public String getPasswordEncryptedValue() throws Exception {
        return Encryption.encrypt(new String(getPassword().getPassword()));
    }

    public JPasswordField getF2A() {
        return twoFactorAuthParentPanel.getF2A();
    }

    public JPasswordField getPin() {
        return twoFactorAuthParentPanel.getPin();
    }

    public void showError(String failureMessage) {
        timers.remove(currentTimesParentPanel);
        timers.remove(regenerateTimesParentPanel);
        errorParentPanel.setText(failureMessage);
        timers.add(errorParentPanel);
    }

    public void redrawTimers() {
        timers.add(currentTimesParentPanel);
        timers.add(regenerateTimesParentPanel);
        timers.remove(errorParentPanel);
    }

}
