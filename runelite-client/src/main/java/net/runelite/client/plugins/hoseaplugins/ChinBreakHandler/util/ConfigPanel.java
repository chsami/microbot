package net.runelite.client.plugins.ChinBreakHandler.util;

import com.google.common.collect.ComparisonChain;
import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigDescriptor;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigItemDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.ConfigObject;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.ConfigSectionDescriptor;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import net.runelite.client.plugins.ChinBreakHandler.ChinBreakHandlerPlugin;
import net.runelite.client.plugins.ChinBreakHandler.ui.ChinBreakHandlerPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.TitleCaseListCellRenderer;
import net.runelite.client.util.Text;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static net.runelite.client.ui.PluginPanel.PANEL_WIDTH;


@Slf4j
public class ConfigPanel extends FixedWidthPanel
{
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(PANEL_WIDTH, super.getPreferredSize().height);
    }

    private static final int SPINNER_FIELD_WIDTH = 6;
    public static final List<Disposable> DISPOSABLES = new ArrayList<>();

    private final ConfigManager configManager;

    private ConfigDescriptor pluginConfig = null;

    private final TitleCaseListCellRenderer listCellRenderer = new TitleCaseListCellRenderer();

    @Inject
    ConfigPanel(ChinBreakHandlerPlugin chinBreakHandlerPlugin)
    {
        this.configManager = chinBreakHandlerPlugin.getConfigManager();

        setBackground(ChinBreakHandlerPanel.PANEL_BACKGROUND_COLOR);
        setBorder(new EmptyBorder(5, 10, 0, 10));
        setLayout(new DynamicGridLayout(0, 1, 0, 5));
    }

    public void init(Config config)
    {
        pluginConfig = getConfigDescriptor(config);

        try
        {
            configManager.setDefaultConfiguration(config, false);
        }
        catch (ThreadDeath e)
        {
            throw e;
        }
        catch (Throwable ex)
        {
            log.warn("Unable to reset plugin configuration", ex);
        }

        rebuild();
    }

    private void rebuild()
    {
        removeAll();

        final Map<String, JPanel> titleWidgets = new HashMap<>();
        final Map<ConfigObject, JPanel> topLevelPanels = new TreeMap<>((a, b) ->
                ComparisonChain.start()
                        .compare(a.position(), b.position())
                        .compare(a.name(), b.name())
                        .result());

        for (ConfigSectionDescriptor ctd : pluginConfig.getSections())
        {
            ConfigSection ct = ctd.getSection();
            final JPanel title = new JPanel();
            title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
            title.setMinimumSize(new Dimension(PANEL_WIDTH, 0));

            final JPanel sectionHeader = new JPanel();
            sectionHeader.setLayout(new BorderLayout());
            sectionHeader.setMinimumSize(new Dimension(PANEL_WIDTH, 0));

            title.add(sectionHeader, BorderLayout.NORTH);

            String name = ct.name();
            final JLabel sectionName = new JLabel(name);
            sectionName.setForeground(ColorScheme.BRAND_ORANGE);
            sectionName.setFont(FontManager.getRunescapeBoldFont());
            sectionName.setToolTipText("<html>" + name + ":<br>" + ct.description() + "</html>");
            sectionName.setBorder(new EmptyBorder(0, 0, 3, 1));
            sectionHeader.add(sectionName, BorderLayout.CENTER);

            final JPanel sectionContents = new JPanel();
            sectionContents.setLayout(new DynamicGridLayout(0, 1, 0, 5));
            sectionContents.setMinimumSize(new Dimension(PANEL_WIDTH, 0));
            sectionContents.setBorder(new EmptyBorder(0, 5, 0, 0));
            title.add(sectionContents, BorderLayout.SOUTH);

            titleWidgets.put(ctd.getKey(), sectionContents);

            // would talk about adding a parent to itself idrk :(
//            JPanel titleSection = titleWidgets.get(ct.name());
//
//            if (titleSection != null)
//            {
//                titleSection.add(title);
//            }
//            else
//            {
//                topLevelPanels.put(ctd, title);
//            }
            topLevelPanels.put(ctd, title);
        }

        for (ConfigItemDescriptor cid : pluginConfig.getItems())
        {
            if (!hideUnhide(pluginConfig, cid))
            {
                continue;
            }

            JPanel item = new JPanel();
            item.setLayout(new BorderLayout());
            item.setMinimumSize(new Dimension(PANEL_WIDTH, 0));
            String name = cid.getItem().name();

            if (!name.isEmpty())
            {
                JLabel configEntryName = new JLabel(name);
                configEntryName.setForeground(Color.WHITE);
                configEntryName.setToolTipText("<html>" + name + ":<br>" + cid.getItem().description() + "</html>");
                item.add(configEntryName, BorderLayout.CENTER);
            }

            if (cid.getType() == boolean.class)
            {
                JCheckBox checkbox = new JCheckBox();
                checkbox.setBackground(ColorScheme.LIGHT_GRAY_COLOR);
                checkbox.setSelected(Boolean.parseBoolean(configManager.getConfiguration(pluginConfig.getGroup().value(), cid.getItem().keyName())));
                checkbox.addActionListener(ae -> changeConfiguration(checkbox, pluginConfig, cid));

                item.add(checkbox, BorderLayout.EAST);
            }

            if (cid.getType() == int.class)
            {
                int value = Integer.parseInt(configManager.getConfiguration(pluginConfig.getGroup().value(), cid.getItem().keyName()));

                Units units = cid.getUnits();
                Range range = cid.getRange();
                int min = 0, max = Integer.MAX_VALUE;
                if (range != null)
                {
                    min = range.min();
                    max = range.max();
                }

                // Config may previously have been out of range
                value = Ints.constrainToRange(value, min, max);

                SpinnerModel model = new SpinnerNumberModel(value, min, max, 1);
                JSpinner spinner = new JSpinner(model);
                Component editor = spinner.getEditor();
                JFormattedTextField spinnerTextField = ((JSpinner.DefaultEditor) editor).getTextField();
                spinnerTextField.setColumns(SPINNER_FIELD_WIDTH);
                spinner.addChangeListener(ce -> changeConfiguration(spinner, pluginConfig, cid));

                if (units != null)
                {
                    spinnerTextField.setFormatterFactory(new UnitFormatterFactory(units.value()));
                }

                item.add(spinner, BorderLayout.EAST);
            }

            if (cid.getType() instanceof Class && ((Class<?>) cid.getType()).isEnum())
            {
                Class<? extends Enum> type = (Class<? extends Enum>) cid.getType();

                JComboBox box = new JComboBox<Enum<?>>(type.getEnumConstants()); // NOPMD: UseDiamondOperator
                // set renderer prior to calling box.getPreferredSize(), since it will invoke the renderer
                // to build components for each combobox element in order to compute the display size of the
                // combobox
                box.setRenderer(listCellRenderer);
                if (!name.isEmpty())
                {
                    box.setPreferredSize(new Dimension(box.getPreferredSize().width, 25));
                }
                else
                {
                    box.setPreferredSize(new Dimension(PANEL_WIDTH - 10, 25));
                }
                box.setForeground(Color.WHITE);
                box.setFocusable(false);

                try
                {
                    Enum<?> selectedItem = Enum.valueOf(type, configManager.getConfiguration(pluginConfig.getGroup().value(), cid.getItem().keyName()));
                    box.setSelectedItem(selectedItem);
                    box.setToolTipText(Text.titleCase(selectedItem));
                }
                catch (IllegalArgumentException ex)
                {
                    log.debug("invalid seleced item", ex);
                }
                box.addItemListener(e ->
                {
                    if (e.getStateChange() == ItemEvent.SELECTED)
                    {
                        changeConfiguration(box, pluginConfig, cid);
                        box.setToolTipText(Text.titleCase((Enum<?>) box.getSelectedItem()));
                    }
                });
                item.add(box, BorderLayout.EAST);
            }

            JPanel title = titleWidgets.get(cid.getItem().section());

            if (title != null)
            {
                title.add(item);
            }
            else
            {
                topLevelPanels.put(cid, item);
            }
        }

        topLevelPanels.values().forEach(this::add);

        revalidate();
        repaint();
    }

    private void changeConfiguration(Component component, ConfigDescriptor cd, ConfigItemDescriptor cid)
    {
        if (component instanceof JCheckBox)
        {
            JCheckBox checkbox = (JCheckBox) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), "" + checkbox.isSelected());
        }
        else if (component instanceof JSpinner)
        {
            JSpinner spinner = (JSpinner) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), "" + spinner.getValue());
        }
        else if (component instanceof JComboBox)
        {
            JComboBox jComboBox = (JComboBox) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), ((Enum) jComboBox.getSelectedItem()).name());
        }

        rebuild();
    }

    private boolean hideUnhide(ConfigDescriptor cd, ConfigItemDescriptor cid)
    {
//        boolean unhide = cid.getItem().hidden();
//        boolean hide = !cid.getItem().hide().isEmpty();
//
//        if (unhide || hide)
//        {
//            boolean show = false;
//
//            List<String> itemHide = Splitter
//                    .onPattern("\\|\\|")
//                    .trimResults()
//                    .omitEmptyStrings()
//                    .splitToList(String.format("%s || %s", cid.getItem().unhide(), cid.getItem().hide()));
//
//            for (ConfigItemDescriptor cid2 : cd.getItems())
//            {
//                if (itemHide.contains(cid2.getItem().keyName()))
//                {
//                    if (cid2.getType() == boolean.class)
//                    {
//                        show = Boolean.parseBoolean(configManager.getConfiguration(cd.getGroup().value(), cid2.getItem().keyName()));
//                    }
//                    else if (cid2.getType() instanceof Class && ((Class<?>) cid2.getType()).isEnum())
//                    {
//                        Class<? extends Enum> type = (Class<? extends Enum>) cid2.getType();
//                        try
//                        {
//                            Enum selectedItem = Enum.valueOf(type, configManager.getConfiguration(cd.getGroup().value(), cid2.getItem().keyName()));
//                            if (!cid.getItem().unhideValue().equals(""))
//                            {
//                                List<String> unhideValue = Splitter
//                                        .onPattern("\\|\\|")
//                                        .trimResults()
//                                        .omitEmptyStrings()
//                                        .splitToList(cid.getItem().unhideValue());
//
//                                show = unhideValue.contains(selectedItem.toString());
//                            }
//                            else if (!cid.getItem().hideValue().equals(""))
//                            {
//                                List<String> hideValue = Splitter
//                                        .onPattern("\\|\\|")
//                                        .trimResults()
//                                        .omitEmptyStrings()
//                                        .splitToList(cid.getItem().hideValue());
//
//                                show = !hideValue.contains(selectedItem.toString());
//                            }
//                        }
//                        catch (IllegalArgumentException ignored)
//                        {
//                        }
//                    }
//                }
//            }
//
//            return (!unhide || show) && (!hide || !show);
//        }

        return true;
    }

    /**
     * Does DFS on a class's interfaces to find all of its implemented fields.
     */
    private Collection<Field> getAllDeclaredInterfaceFields(Class<?> clazz)
    {
        Collection<Field> methods = new HashSet<>();
        Stack<Class<?>> interfaces = new Stack<>();
        interfaces.push(clazz);

        while (!interfaces.isEmpty())
        {
            Class<?> interfaze = interfaces.pop();
            Collections.addAll(methods, interfaze.getDeclaredFields());
            Collections.addAll(interfaces, interfaze.getInterfaces());
        }

        return methods;
    }

    public ConfigDescriptor getConfigDescriptor(Config configurationProxy)
    {
        Class<?> inter = configurationProxy.getClass().getInterfaces()[0];
        ConfigGroup group = inter.getAnnotation(ConfigGroup.class);

        if (group == null)
        {
            throw new IllegalArgumentException("Not a config group");
        }

        final List<ConfigSectionDescriptor> sections = getAllDeclaredInterfaceFields(inter).stream()
                .filter(m -> m.isAnnotationPresent(ConfigSection.class) && m.getType() == String.class)
                .map(m ->
                {
                    try
                    {
                        return new ConfigSectionDescriptor(
                                String.valueOf(m.get(inter)),
                                m.getDeclaredAnnotation(ConfigSection.class)
                        );
                    }
                    catch (IllegalAccessException e)
                    {
                        log.warn("Unable to load section {}::{}", inter.getSimpleName(), m.getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> ComparisonChain.start()
                        .compare(a.getSection().position(), b.getSection().position())
                        .compare(a.getSection().name(), b.getSection().name())
                        .result())
                .collect(Collectors.toList());

        final List<ConfigSectionDescriptor> titles = getAllDeclaredInterfaceFields(inter).stream()
                .filter(m -> m.isAnnotationPresent(ConfigSection.class) && m.getType() == String.class)
                .map(m ->
                {
                    try
                    {
                        return new ConfigSectionDescriptor(
                                String.valueOf(m.get(inter)),
                                m.getDeclaredAnnotation(ConfigSection.class)
                        );
                    }
                    catch (IllegalAccessException e)
                    {
                        log.warn("Unable to load title {}::{}", inter.getSimpleName(), m.getName());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted((a, b) -> ComparisonChain.start()
                        .compare(a.getSection().position(), b.getSection().position())
                        .compare(a.getSection().name(), b.getSection().name())
                        .result())
                .collect(Collectors.toList());

        final List<ConfigItemDescriptor> items = Arrays.stream(inter.getMethods())
                .filter(m -> m.getParameterCount() == 0 && m.isAnnotationPresent(ConfigItem.class))
                .map(m -> new ConfigItemDescriptor(
                        m.getDeclaredAnnotation(ConfigItem.class),
                        m.getReturnType(),
                        m.getDeclaredAnnotation(Range.class),
                        m.getDeclaredAnnotation(Alpha.class),
                        m.getDeclaredAnnotation(Units.class)
                ))
                .sorted((a, b) -> ComparisonChain.start()
                        .compare(a.getItem().position(), b.getItem().position())
                        .compare(a.getItem().name(), b.getItem().name())
                        .result())
                .collect(Collectors.toList());

        return new ConfigDescriptor(group, sections, items);
    }
}
