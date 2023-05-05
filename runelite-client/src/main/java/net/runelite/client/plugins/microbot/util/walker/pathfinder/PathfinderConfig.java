package net.runelite.client.plugins.microbot.util.walker.pathfinder;

import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.walker.Transport;

import java.awt.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathfinderConfig {
    private static final WorldArea WILDERNESS_ABOVE_GROUND = new WorldArea(2944, 3523, 448, 448, 0);
    private static final WorldArea WILDERNESS_UNDERGROUND = new WorldArea(2944, 9918, 320, 442, 0);

    @Getter
    private final CollisionMap map;
    @Getter
    private final Map<WorldPoint, List<Transport>> transports;
    @Getter
    private Duration calculationCutoff;
    private boolean avoidWilderness;
    private boolean useAgilityShortcuts;
    private boolean useGrappleShortcuts;
    private boolean useBoats;
    private boolean useFairyRings;
    private boolean useTeleports;
    private int agilityLevel;
    private int rangedLevel;
    private int strengthLevel;
    private int prayerLevel;
    private int woodcuttingLevel;
    private Map<Quest, QuestState> questStates = new HashMap<>();

    public PathfinderConfig(CollisionMap map, Map<WorldPoint, List<Transport>> transports) {
        this.map = map;
        this.transports = transports;
        refresh();
    }

    public void refresh() {
        calculationCutoff = Duration.ofMillis(5 * Constants.GAME_TICK_LENGTH);
        avoidWilderness = true;
        useAgilityShortcuts = true;
        useGrappleShortcuts = true;
        useBoats = true;
        useFairyRings = true;
        useTeleports = true;

        if (GameState.LOGGED_IN.equals(Microbot.getClient().getGameState())) {
            agilityLevel = Microbot.getClient().getBoostedSkillLevel(Skill.AGILITY);
            rangedLevel = Microbot.getClient().getBoostedSkillLevel(Skill.RANGED);
            strengthLevel = Microbot.getClient().getBoostedSkillLevel(Skill.STRENGTH);
            prayerLevel = Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER);
            woodcuttingLevel = Microbot.getClient().getBoostedSkillLevel(Skill.WOODCUTTING);
            Microbot.getClientThread().invokeLater(this::refreshQuests);
        }
    }

    private void refreshQuests() {
        useFairyRings &= !QuestState.NOT_STARTED.equals(Quest.FAIRYTALE_II__CURE_A_QUEEN.getState(Microbot.getClient()));
        for (Map.Entry<WorldPoint, List<Transport>> entry : transports.entrySet()) {
            for (Transport transport : entry.getValue()) {
                if (transport.isQuestLocked()) {
                    try {
                        questStates.put(transport.getQuest(), transport.getQuest().getState(Microbot.getClient()));
                    } catch (NullPointerException ignored) {
                    }
                }
            }
        }
    }

    private boolean isInWilderness(WorldPoint p) {
        return WILDERNESS_ABOVE_GROUND.distanceTo(p) == 0 || WILDERNESS_UNDERGROUND.distanceTo(p) == 0;
    }

    public boolean avoidWilderness(WorldPoint position, WorldPoint neighbor, WorldPoint target) {
        return avoidWilderness && !isInWilderness(position) && isInWilderness(neighbor) && !isInWilderness(target);
    }

    public boolean isNear(WorldPoint location) {
        if (Microbot.getClient().getLocalPlayer() == null) {
            return true;
        }
        return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo2D(location) <= 10;
    }

    public boolean useTransport(Transport transport) {
        final int transportAgilityLevel = transport.getRequiredLevel(Skill.AGILITY);
        final int transportRangedLevel = transport.getRequiredLevel(Skill.RANGED);
        final int transportStrengthLevel = transport.getRequiredLevel(Skill.STRENGTH);
        final int transportPrayerLevel = transport.getRequiredLevel(Skill.PRAYER);
        final int transportWoodcuttingLevel = transport.getRequiredLevel(Skill.WOODCUTTING);

        final boolean isAgilityShortcut = transport.isAgilityShortcut();
        final boolean isGrappleShortcut = transport.isGrappleShortcut();
        final boolean isBoat = transport.isBoat();
        final boolean isFairyRing = transport.isFairyRing();
        final boolean isTeleport = transport.isTeleport();
        final boolean isCanoe = isBoat && transportWoodcuttingLevel > 1;
        final boolean isPrayerLocked = transportPrayerLevel > 1;
        final boolean isQuestLocked = transport.isQuestLocked();

        if (isAgilityShortcut) {
            if (!useAgilityShortcuts || agilityLevel < transportAgilityLevel) {
                return false;
            }

            if (isGrappleShortcut && (!useGrappleShortcuts || rangedLevel < transportRangedLevel || strengthLevel < transportStrengthLevel)) {
                return false;
            }
        }

        if (isBoat) {
            if (!useBoats) {
                return false;
            }

            if (isCanoe && woodcuttingLevel < transportWoodcuttingLevel) {
                return false;
            }
        }

        if (isFairyRing && !useFairyRings) {
            return false;
        }

        if (isTeleport && !useTeleports) {
            return false;
        }

        if (isPrayerLocked && prayerLevel < transportPrayerLevel) {
            return false;
        }

        if (isQuestLocked && !QuestState.FINISHED.equals(questStates.getOrDefault(transport.getQuest(), QuestState.NOT_STARTED))) {
            return false;
        }

        return true;
    }

    public Point mapWorldPointToGraphicsPoint(WorldPoint worldPoint) {
        WorldMap worldMap = Microbot.getClient().getWorldMap();

        float pixelsPerTile = worldMap.getWorldMapZoom();

        Widget map = Microbot.getClient().getWidget(WidgetInfo.WORLD_MAP_VIEW);
        if (map != null) {
            Rectangle worldMapRect = map.getBounds();

            int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
            int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

            Point worldMapPosition = worldMap.getWorldMapPosition();

            int yTileMax = worldMapPosition.getY() - heightInTiles / 2;
            int yTileOffset = (yTileMax - worldPoint.getY() - 1) * -1;
            int xTileOffset = worldPoint.getX() + widthInTiles / 2 - worldMapPosition.getX();

            int xGraphDiff = ((int) (xTileOffset * pixelsPerTile));
            int yGraphDiff = (int) (yTileOffset * pixelsPerTile);

            yGraphDiff -= pixelsPerTile - Math.ceil(pixelsPerTile / 2);
            xGraphDiff += pixelsPerTile - Math.ceil(pixelsPerTile / 2);

            yGraphDiff = worldMapRect.height - yGraphDiff;
            yGraphDiff += (int) worldMapRect.getY();
            xGraphDiff += (int) worldMapRect.getX();

            return new Point(xGraphDiff, yGraphDiff);
        }
        return null;
    }

    public WorldPoint calculateMapPoint(Point point) {
        WorldMap worldMap = Microbot.getClient().getWorldMap();
        float zoom = worldMap.getWorldMapZoom();
        final WorldPoint mapPoint = new WorldPoint(worldMap.getWorldMapPosition().getX(), worldMap.getWorldMapPosition().getY(), 0);
        final Point middle = mapWorldPointToGraphicsPoint(mapPoint);

        if (point == null || middle == null) {
            return null;
        }

        final int dx = (int) ((point.getX() - middle.getX()) / zoom);
        final int dy = (int) ((-(point.getY() - middle.getY())) / zoom);

        return mapPoint.dx(dx).dy(dy);
    }
}
