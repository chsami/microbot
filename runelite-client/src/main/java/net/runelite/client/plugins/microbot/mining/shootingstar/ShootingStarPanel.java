package net.runelite.client.plugins.microbot.mining.shootingstar;

import lombok.Getter;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.mining.shootingstar.model.ShootingStarTableHeader;
import net.runelite.client.plugins.microbot.mining.shootingstar.model.ShootingStarTableRow;
import net.runelite.client.plugins.microbot.mining.shootingstar.model.Star;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.PluginPanel;
import com.google.common.collect.Ordering;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;

import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.function.Function;

public class ShootingStarPanel extends PluginPanel {
    public static final int WORLD_WIDTH = 35;
    public static final int LOCATION_WIDTH = 125;
    public static final int TIER_WIDTH = 25;
    public static final int TIME_WIDTH = 70;

    private static final String SELECT_OPTION = "Select star";
    private static final String UNSELECT_OPTION = "Unselect Star";

    private final JPanel listContainer = new JPanel();
    private final ShootingStarPlugin plugin;

    private ShootingStarTableHeader worldHeader;
    private ShootingStarTableHeader locationHeader;
    private ShootingStarTableHeader tierHeader;
    private ShootingStarTableHeader timeLeftHeader;
    private Order orderIndex = Order.TIME_LEFT;
    private boolean ascendingOrder = false;
    @Getter
    private final List<Star> hiddenStars = new ArrayList<>();


    public ShootingStarPanel(ShootingStarPlugin plugin) {
        this.plugin = plugin;
        setBorder(null);

        setLayout(new DynamicGridLayout(0, 1));
        JPanel header = buildHeader();
        add(header, BorderLayout.NORTH);

        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        add(listContainer, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setBorder(new EmptyBorder(5, 5, 5, 5));
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.add(Box.createRigidArea(new Dimension(0, 5)));
        buttons.add(removeClipboardPanel());
        add(buttons);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));

        worldHeader = new ShootingStarTableHeader("W");
        worldHeader.setPreferredSize(new Dimension(WORLD_WIDTH, 20));
        worldHeader.addMouseListener(createHeaderMouseOptions(Order.WORLD));

        tierHeader = new ShootingStarTableHeader("T");
        tierHeader.setPreferredSize(new Dimension(TIER_WIDTH, 20));
        tierHeader.addMouseListener(createHeaderMouseOptions(Order.TIER));
        tierHeader.highlight(true, ascendingOrder);

        locationHeader = new ShootingStarTableHeader("Location");
        locationHeader.setPreferredSize(new Dimension(LOCATION_WIDTH, 20));
        locationHeader.addMouseListener(createHeaderMouseOptions(Order.LOCATION));

        timeLeftHeader = new ShootingStarTableHeader("Time Left");
        timeLeftHeader.setPreferredSize(new Dimension(TIME_WIDTH, 20));
        timeLeftHeader.addMouseListener(createHeaderMouseOptions(Order.TIME_LEFT));

        header.add(worldHeader);
        header.add(tierHeader);
        header.add(locationHeader);
        header.add(timeLeftHeader);
        return header;
    }

    private MouseAdapter createHeaderMouseOptions(Order order) {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    return;
                }
                ascendingOrder = orderIndex != order || !ascendingOrder;
                orderBy(order);
            }
        };
    }

    void updateList(List<Star> starData) {
        listContainer.removeAll();
        if (starData.isEmpty()) {
            JLabel noStarsLabel = new JLabel("Please wait for data to be fetched");
            noStarsLabel.setFont(FontManager.getRunescapeSmallFont());
            noStarsLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            listContainer.add(noStarsLabel);
        } else {
            starData.sort((r1, r2) ->
            {
                switch (orderIndex) {
                    case WORLD:
                        return getCompareValue(r1, r2, star -> star.getWorldObject().getId());
                    case LOCATION:
                        return getCompareValue(r1, r2, Star::getShootingStarLocation);
                    case TIER:
                        return getCompareValue(r1, r2, Star::getTier);
                    case TIME_LEFT:
                        return getCompareValue(r1, r2, Star::getEndsAt);
                    default:
                        return 0;
                }
            });

            int i = 0;
            for (Star data : starData) {
                if (hiddenStars.contains(data)) continue;

                Color backgroundColor = i % 2 == 0 ? ColorScheme.DARK_GRAY_COLOR : ColorScheme.DARKER_GRAY_COLOR;
                ShootingStarTableRow r = new ShootingStarTableRow(data, plugin.isDisplayAsMinutes(), backgroundColor, Rs2Player.getWorld());
                r.setComponentPopupMenu(buildPopupMenu(r, data));
                listContainer.add(r);
                i++;
            }
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    void refreshList(List<Star> starData) {
        if (starData.isEmpty()) {
            return;
        }

        for (Component component : listContainer.getComponents()) {
            ShootingStarTableRow r = (ShootingStarTableRow) component;
            r.updateTime();
            r.updateSelectedBorder();
            r.updateLocationColor();
            r.updateTierColor();
        }

        listContainer.repaint();
    }

    private JPopupMenu buildPopupMenu(ShootingStarTableRow row, Star star) {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(new EmptyBorder(5, 5, 5, 5));

        JMenuItem hopEntryOption = new JMenuItem();
        hopEntryOption.setText("Hop to");
        hopEntryOption.setFont(FontManager.getRunescapeSmallFont());
        hopEntryOption.addActionListener(e -> Microbot.hopToWorld(star.getWorldObject().getId()));
        popupMenu.add(hopEntryOption);

        JMenuItem selectedEntryOption = new JMenuItem();
        if (star.isSelected()) {
            selectedEntryOption.setText(UNSELECT_OPTION);
        } else {
            selectedEntryOption.setText(SELECT_OPTION);
        }
        selectedEntryOption.setFont(FontManager.getRunescapeSmallFont());
        selectedEntryOption.addActionListener(e -> {
            if (star.isSelected()) {
                selectedEntryOption.setText(UNSELECT_OPTION);
            } else {
                selectedEntryOption.setText(SELECT_OPTION);
            }
            if (plugin.getSelectedStar() != null) {
                plugin.getSelectedStar().setSelected(false);
            }
            plugin.updateSelectedStar(star);
            row.updateSelectedBorder();
            selectedEntryOption.repaint();
        });
        popupMenu.add(selectedEntryOption);

        JMenuItem removeEntryOption = new JMenuItem();
        removeEntryOption.setText("Remove");
        removeEntryOption.setFont(FontManager.getRunescapeSmallFont());
        removeEntryOption.addActionListener(e -> {
            plugin.removeStar(star);
            plugin.updatePanelList(true);
        });
        popupMenu.add(removeEntryOption);

        return popupMenu;
    }

    private int getCompareValue(Star row1, Star row2, Function<Star, Comparable> compareFn) {
        Ordering<Comparable> ordering = Ordering.natural();
        if (!ascendingOrder) {
            ordering = ordering.reverse();
        }
        ordering = ordering.reverse();
        return ordering.compare(compareFn.apply(row1), compareFn.apply(row2));
    }

    private void orderBy(Order order) {
        worldHeader.highlight(false, ascendingOrder);
        locationHeader.highlight(false, ascendingOrder);
        tierHeader.highlight(false, ascendingOrder);
        timeLeftHeader.highlight(false, ascendingOrder);
        switch (order) {
            case WORLD:
                worldHeader.highlight(true, ascendingOrder);
                break;
            case LOCATION:
                locationHeader.highlight(true, ascendingOrder);
                break;
            case TIER:
                tierHeader.highlight(true, ascendingOrder);
                break;
            case TIME_LEFT:
                timeLeftHeader.highlight(true, ascendingOrder);
                break;
        }
        orderIndex = order;
        updateList(plugin.getStarList());
    }

    private JPanel removeClipboardPanel() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Remove clipboard worlds");
        panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        panel.setPreferredSize(new Dimension(60, 30));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    plugin.removeWorldsInClipboard();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            }
        });
        panel.add(label);
        return panel;
    }

    public void hideStars(List<Star> starList) {
        for (Star star : starList) {
            if (plugin.isHideMembersWorlds() && star.isMemberWorld()) {
                hiddenStars.add(star);
            }
            if (plugin.isHideF2PWorlds() && star.isF2PWorld()) {
                hiddenStars.add(star);
            }
            if (plugin.isHideWildernessLocations() && star.isInWilderness()) {
                hiddenStars.add(star);
            }
        }
    }

    public void showStars() {
        List<Star> starsToRemove = new ArrayList<>();
        for (Star star : hiddenStars) {
            if (!plugin.isHideMembersWorlds() && star.isMemberWorld()) {
                if (plugin.isHideF2PWorlds() && star.isF2PWorld()) continue;
                if (plugin.isHideWildernessLocations() && star.isInWilderness()) continue;
                starsToRemove.add(star);
            }
            if (!plugin.isHideF2PWorlds() && star.isF2PWorld()) {
                if (plugin.isHideMembersWorlds() && star.isMemberWorld()) continue;
                if (plugin.isHideWildernessLocations() && star.isInWilderness()) continue;
                starsToRemove.add(star);
            }
            if (!plugin.isHideWildernessLocations() && star.isInWilderness()) {
                if (plugin.isHideMembersWorlds() && star.isMemberWorld()) continue;
                if (plugin.isHideF2PWorlds() && star.isF2PWorld()) continue;
                starsToRemove.add(star);
            }
        }
        hiddenStars.removeAll(starsToRemove);
    }

    private enum Order {
        WORLD,
        LOCATION,
        TIER,
        TIME_LEFT
    }
}
