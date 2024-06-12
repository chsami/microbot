package net.runelite.client.plugins.ChinBreakHandler.ui;

import com.google.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.ChinBreakHandler.ChinBreakHandler;
import net.runelite.client.plugins.ChinBreakHandler.ChinBreakHandlerPlugin;
import net.runelite.client.plugins.ChinBreakHandler.util.DeferredDocumentChangedListener;
import net.runelite.client.plugins.ChinBreakHandler.util.ProfilesData;
import net.runelite.client.ui.PluginPanel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static net.runelite.client.ui.PluginPanel.PANEL_WIDTH;

public class ChinBreakHandlerAccountPanel extends JPanel
{
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(PANEL_WIDTH, super.getPreferredSize().height);
    }

    private final ConfigManager configManager;
    private final ChinBreakHandler chinBreakHandler;
    private final JPanel contentPanel = new JPanel(new GridLayout(0, 1));

    @Inject
    ChinBreakHandlerAccountPanel(ChinBreakHandlerPlugin chinBreakHandlerPluginPlugin, ChinBreakHandler chinBreakHandler)
    {
        this.configManager = chinBreakHandlerPluginPlugin.getConfigManager();
        this.chinBreakHandler = chinBreakHandler;

        setupDefaults();

        setLayout(new BorderLayout());
        setBackground(ChinBreakHandlerPanel.PANEL_BACKGROUND_COLOR);

        init();
    }

    private LoginMode getConfigValue()
    {
        String accountselection = configManager.getConfiguration("piggyBreakHandler", "accountselection");

        return LoginMode.parse(accountselection);
    }

    private void init() {
        contentPanel.setBorder(new EmptyBorder(10, 10, 0, 10));

        JPanel accountSelection = new JPanel(new GridLayout(0, 2));
        accountSelection.setBorder(new EmptyBorder(5, 0, 0, 0));
        ButtonGroup buttonGroup = new ButtonGroup();

        JCheckBox manualButton = new JCheckBox("Manual");
        JCheckBox profilesButton = new JCheckBox("Profiles plugin");
        JCheckBox jagexLauncherButton = new JCheckBox("Jagex Launcher");

        String profilesSalt = configManager.getConfiguration("piggyProfiles", "salt");
        boolean profilesSavePasswords = Boolean.parseBoolean(configManager.getConfiguration("piggyProfiles", "rememberPassword"));

        String jagexLauncherStr = configManager.getConfiguration("piggyBreakHandler", "jagexLauncher");
        if (jagexLauncherStr == null || jagexLauncherStr.isEmpty()) {
            configManager.setConfiguration("piggyBreakHandler", "jagexLauncher", false);
        }

        String accountSelectionString = configManager.getConfiguration("piggyBreakHandler", "accountselection");
        boolean jagexLauncher = configManager.getConfiguration("piggyBreakHandler", "jagexLauncher", Boolean.class);

        if (jagexLauncher && getConfigValue() == null) {
            configManager.setConfiguration("piggyBreakHandler", "accountselection", LoginMode.LAUNCHER);
        } else if (accountSelectionString.equalsIgnoreCase("true")) {
            configManager.setConfiguration("piggyBreakHandler", "accountselection", LoginMode.MANUAL);
        } else if (accountSelectionString.equalsIgnoreCase("false")) {
            configManager.setConfiguration("piggyBreakHandler", "accountselection", LoginMode.PROFILES);
        }

        if (profilesSalt == null || profilesSalt.length() == 0 || !profilesSavePasswords)
        {
            configManager.setConfiguration("piggyBreakHandler", "accountselection", LoginMode.MANUAL);
            profilesButton.setEnabled(false);
        }

        manualButton.addActionListener(e -> {
            configManager.setConfiguration("piggyBreakHandler", "accountselection", LoginMode.MANUAL);
            contentPanel(LoginMode.MANUAL);
        });

        profilesButton.addActionListener(e -> {
            configManager.setConfiguration("piggyBreakHandler", "accountselection", LoginMode.PROFILES);
            contentPanel(LoginMode.PROFILES);
        });

        jagexLauncherButton.addActionListener(e -> {
            configManager.setConfiguration("piggyBreakHandler", "accountselection", LoginMode.PROFILES);
            contentPanel(LoginMode.LAUNCHER);
        });

        buttonGroup.add(manualButton);
        buttonGroup.add(profilesButton);
        buttonGroup.add(jagexLauncherButton);

        LoginMode config = getConfigValue();

        manualButton.setSelected(config == LoginMode.MANUAL);
        profilesButton.setSelected(config == LoginMode.PROFILES);
        jagexLauncherButton.setSelected(config == LoginMode.LAUNCHER);

        accountSelection.add(manualButton);
        accountSelection.add(profilesButton);
        accountSelection.add(jagexLauncherButton);

        add(accountSelection, BorderLayout.NORTH);

        contentPanel(config);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void emptyContentPanel() {
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void contentPanel(LoginMode mode)
    {
        contentPanel.removeAll();

        if (mode == LoginMode.MANUAL)
        {
            contentPanel.add(new JLabel("Username"));

            final JTextField usernameField = new JTextField();
            usernameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            usernameField.setText(configManager.getConfiguration("piggyBreakHandler", "accountselection-manual-username"));
            DeferredDocumentChangedListener usernameListener = new DeferredDocumentChangedListener();
            usernameListener.addChangeListener(e ->
                    configManager.setConfiguration("piggyBreakHandler", "accountselection-manual-username", usernameField.getText()));
            usernameField.getDocument().addDocumentListener(usernameListener);

            contentPanel.add(usernameField);

            contentPanel.add(new JLabel("Password"));

            final JPasswordField passwordField = new JPasswordField();
            passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            passwordField.setText(configManager.getConfiguration("piggyBreakHandler", "accountselection-manual-password"));
            DeferredDocumentChangedListener passwordListener = new DeferredDocumentChangedListener();
            passwordListener.addChangeListener(e ->
                    configManager.setConfiguration("piggyBreakHandler", "accountselection-manual-password", String.valueOf(passwordField.getPassword())));
            passwordField.getDocument().addDocumentListener(passwordListener);

            contentPanel.add(passwordField);

            contentPanel.add(new JLabel("Bank Pin"));

            final JPasswordField pinField = new JPasswordField();
            pinField.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            pinField.setText(configManager.getConfiguration("piggyBreakHandler", "accountselection-manual-pin"));
            PlainDocument document = (PlainDocument) pinField.getDocument();
            document.setDocumentFilter(new DocumentFilter() {
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    String string = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;

                    if (string.length() <= 4) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });

            DeferredDocumentChangedListener pinListener = new DeferredDocumentChangedListener();
            pinListener.addChangeListener(e -> {
                configManager.setConfiguration("piggyBreakHandler", "accountselection-manual-pin", String.valueOf(pinField.getPassword()));
            });
            pinField.getDocument().addDocumentListener(pinListener);

            contentPanel.add(pinField);
        }
        else if (ChinBreakHandlerPlugin.data == null && mode == LoginMode.PROFILES)
        {
            contentPanel.add(new JLabel("Profiles plugin password"));
            final JPasswordField passwordField = new JPasswordField();
            passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel parsingLabel = new JLabel();
            parsingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            parsingLabel.setPreferredSize(new Dimension(PANEL_WIDTH, 15));

            DeferredDocumentChangedListener passwordListener = new DeferredDocumentChangedListener();
            passwordListener.addChangeListener(e ->
            {
                try
                {
                    ChinBreakHandlerPlugin.data = ProfilesData.getProfileData(configManager, passwordField.getPassword());
                    contentPanel(LoginMode.PROFILES);
                }
                catch (InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | NoSuchAlgorithmException ignored)
                {
                    parsingLabel.setText("Incorrect password!");
                }
            });
            passwordField.getDocument().addDocumentListener(passwordListener);

            contentPanel.add(passwordField);
            contentPanel.add(parsingLabel);
        } else if (mode == LoginMode.LAUNCHER) {
            emptyContentPanel();
        }
        else
        {
            ConfigChanged configChanged = new ConfigChanged();
            configChanged.setGroup("mock");
            configChanged.setKey("mock");
            chinBreakHandler.configChanged.onNext(configChanged);

            if (!ChinBreakHandlerPlugin.data.contains(":"))
            {
                contentPanel.add(new JLabel("No accounts found"));
            }
            else
            {
                contentPanel.add(new JLabel("Select account"));

                String[] accounts = Arrays.stream(ChinBreakHandlerPlugin.data.split("\\n"))
                        .map((s) -> s.split(":")[0])
                        .sorted()
                        .toArray(String[]::new);

                JComboBox<String> filterComboBox = new JComboBox<>(accounts);
                filterComboBox.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
                filterComboBox.addActionListener(e -> {
                    if (filterComboBox.getSelectedItem() != null)
                    {
                        configManager.setConfiguration("piggyBreakHandler", "accountselection-profiles-account", filterComboBox.getSelectedItem().toString());
                    }
                });

                String config = configManager.getConfiguration("piggyBreakHandler", "accountselection-profiles-account");

                if (config != null)
                {
                    int index = Arrays.asList(accounts).indexOf(config);

                    if (index != -1)
                    {
                        filterComboBox.setSelectedIndex(index);
                    }
                    else
                    {
                        filterComboBox.setSelectedIndex(0);
                    }
                }

                contentPanel.add(filterComboBox);
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void setupDefaults()
    {

        if (configManager.getConfiguration("piggyBreakHandler", "accountselection") == null)
        {
            configManager.setConfiguration("piggyBreakHandler", "accountselection", LoginMode.MANUAL);
        }

        if (configManager.getConfiguration("piggyBreakHandler", "accountselection-manual-username") == null)
        {
            configManager.setConfiguration("piggyBreakHandler", "accountselection-manual-username", "");
        }

        if (configManager.getConfiguration("piggyBreakHandler", "accountselection-manual-password") == null)
        {
            configManager.setConfiguration("piggyBreakHandler", "accountselection-manual-password", "");
        }

        if (configManager.getConfiguration("piggyBreakHandler", "accountselection-profiles-account") == null)
        {
            configManager.setConfiguration("piggyBreakHandler", "accountselection-profiles-account", "");
        }
    }
}