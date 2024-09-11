package net.runelite.client.plugins.microbot.mining.shootingstar.model;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import lombok.Getter;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.mining.shootingstar.ShootingStarPanel;
import net.runelite.client.ui.ColorScheme;

import net.runelite.client.ui.FontManager;

import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.ui.ColorScheme.*;

public class ShootingStarTableRow extends JPanel {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
    private static final ZoneId utcZoneId = ZoneId.of("UTC");
    private static final Color COLOR_NEGATIVE = new Color(255, 80, 80);

    @Getter
    private final Star starData;

    private final boolean displayAsMinutes;

    private JLabel locationLabel;
    private JLabel tierLabel;
    private JLabel timeLeftLabel;

    public ShootingStarTableRow(Star starData, boolean displayAsMinutes, Color backgroundColor, int curWorld) {
        this.starData = starData;
        this.displayAsMinutes = displayAsMinutes;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        updateSelectedBorder();
        setBackground(backgroundColor);

        // Create worldField & color it based off curWorld
        JPanel worldField = buildWorldField(curWorld);
        worldField.setPreferredSize(new Dimension(ShootingStarPanel.WORLD_WIDTH, 20));
        worldField.addMouseListener(createMouseOptions(starData, backgroundColor));
        worldField.setOpaque(false);
        add(worldField);


        // Create tierField & color it based off hasRequirements
        JPanel tierField = buildTierField();
        tierField.setPreferredSize(new Dimension(ShootingStarPanel.TIER_WIDTH, 20));
        tierField.addMouseListener(createMouseOptions(starData, backgroundColor));
        tierField.setOpaque(false);
        add(tierField);

        // Create locationField using ShootingStarLocation ShortName
        JPanel locationField = buildLocationField();
        locationField.setPreferredSize(new Dimension(ShootingStarPanel.LOCATION_WIDTH, 20));
        locationField.setToolTipText(starData.getShootingStarLocation().getRawLocationName());
        locationField.addMouseListener(createMouseOptions(starData, backgroundColor));
        locationField.setInheritsPopupMenu(true);
        this.setInheritsPopupMenu(true);
        locationField.setOpaque(false);
        add(locationField);

        // Create timeLeftField using time data
        JPanel timeLeftField = buildTimeField();
        timeLeftField.setPreferredSize(new Dimension(ShootingStarPanel.TIME_WIDTH, 20));
        timeLeftField.setOpaque(false);
        add(timeLeftField);
    }

    public static String convertTime(long epoch) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(epoch - Instant.now().toEpochMilli());
        boolean negative = seconds < 0;
        seconds = Math.abs(seconds);
        String time = negative ? "-" : "";
        long minutes = seconds / 60;
        seconds %= 60;
        if (minutes >= 100) {
            time += minutes + "m";
        } else {
            time += String.format("%d:%02d", minutes, seconds);
        }
        return time;
    }

    private MouseAdapter createMouseOptions(Star starData, Color backgroundColor) {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(getBackground().brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(backgroundColor);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // double click row hops to world
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    Microbot.hopToWorld(starData.getWorldObject().getId());
                }
            }
        };
    }

    private JPanel buildWorldField(int world) {
        JPanel panel = new JPanel(new BorderLayout(7, 0));
        panel.setBorder(new EmptyBorder(0, 5, 0, 5));
        JLabel worldLabel = new JLabel(Integer.toString(starData.getWorldObject().getId()));
        worldLabel.setFont(FontManager.getRunescapeSmallFont());
        worldLabel.setForeground(worldColor(world));
        panel.add(worldLabel);
        return panel;
    }

    private Color worldColor(int curWorld) {
        if (starData.getWorldObject().getId() == curWorld) {
            return BRAND_ORANGE;
        }
        return TEXT_COLOR;
    }

    private JPanel buildTierField() {
        JPanel panel = new JPanel(new BorderLayout(4, 0));
        panel.setBorder(new EmptyBorder(0, 5, 0, 5));
        tierLabel = new JLabel(Integer.toString(starData.getTier()));
        tierLabel.setFont(FontManager.getRunescapeSmallFont());
        updateTierColor();
        panel.add(tierLabel);
        return panel;
    }

    public void updateTierColor() {
        tierLabel.setForeground(tierColor());
    }

    private Color tierColor() {
        if (starData.hasMiningLevel()) {
            return TEXT_COLOR;
        }
        return COLOR_NEGATIVE;
    }

    private JPanel buildLocationField() {
        JPanel panel = new JPanel(new BorderLayout(7, 0));
        panel.setBorder(new EmptyBorder(0, 5, 0, 5));
        locationLabel = new JLabel(starData.getShootingStarLocation().getLocationName());
        locationLabel.setFont(FontManager.getRunescapeSmallFont());
        updateLocationColor();
        panel.add(locationLabel);
        return panel;
    }

    public void updateLocationColor() {
        locationLabel.setForeground(getLocationColor());
    }

    private Color getLocationColor() {
        long time = ZonedDateTime.now(utcZoneId).toInstant().toEpochMilli();
        boolean minimumPassed = time > starData.getCalledAt();
        boolean maximumPassed = time > starData.getEndsAt();
        if (maximumPassed && minimumPassed) {
            return ColorScheme.PROGRESS_COMPLETE_COLOR;
        }
        if (!starData.hasLocationRequirements()) {
            return COLOR_NEGATIVE;
        }
        return TEXT_COLOR;
    }

    private JPanel buildTimeField() {
        JPanel panel = new JPanel(new BorderLayout(7, 0));
        panel.setBorder(new EmptyBorder(0, 5, 0, 5));
        timeLeftLabel = new JLabel();
        timeLeftLabel.setFont(FontManager.getRunescapeSmallFont());
        panel.add(timeLeftLabel);
        updateTime();
        return panel;
    }

    public void updateTime() {
        String endTime;
        if (displayAsMinutes) {
            endTime = convertTime(starData.getEndsAt());
        } else {
            Instant endInstant = Instant.ofEpochMilli(starData.getEndsAt());
            endTime = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()).format(dtf);
        }
        timeLeftLabel.setText(endTime);

        timeLeftLabel.setForeground(getTimeColor(starData.getEndsAt()));
    }

    private Color getTimeColor(long time) {
        if (time < ZonedDateTime.now(utcZoneId).toInstant().toEpochMilli()) {
            return COLOR_NEGATIVE;
        }
        return TEXT_COLOR;
    }

    public void updateSelectedBorder() {
        if (starData.isSelected())
            setBorder(new LineBorder(BRAND_ORANGE, 1));
        else
            setBorder(new EmptyBorder(1, 1, 1, 1));
    }
}
