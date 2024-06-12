package net.runelite.client.plugins.hoseaplugins.AutoRifts;

import net.runelite.client.plugins.hoseaplugins.AutoRifts.data.Utility;
import net.runelite.client.plugins.hoseaplugins.AutoRifts.data.Constants;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Widgets;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GOTRState {
    public static final int AIR_ALTAR = 43701;
    public static final int WATER_ALTAR = 43702;
    public static final int EARTH_ALTAR = 43703;
    public static final int FIRE_ALTAR = 43704;
    public static final int MIND_ALTAR = 43705;
    public static final int CHAOS_ALTAR = 43706;
    public static final int DEATH_ALTAR = 43707;
    public static final int BLOOD_ALTAR = 43708;
    public static final int BODY_ALTAR = 43709;
    public static final int COSMIC_ALTAR = 43710;
    public static final int NATURE_ALTAR = 43711;
    public static final int LAW_ALTAR = 43712;
    @Getter
    private int elementalPoints = -1;
    @Getter
    private int catalyticPoints = -1;
    @Getter
    private boolean gameStarted;
    @Setter
    private boolean started;
    public boolean hasFirstPortalSpawned = false;
    @Inject
    private Client client;
    private final EventBus eventBus;
    private final AutoRiftsConfig config;
    private static final String CHECK_POINT_REGEX = "You have (\\d+) catalytic energy and (\\d+) elemental energy";
    private static final Pattern CHECK_POINT_PATTERN = Pattern.compile(CHECK_POINT_REGEX);
    private static final String REWARD_POINT_REGEX = "Total elemental energy:[^>]+>([\\d,]+).*Total catalytic energy:[^>]+>([\\d,]+).";
    private static final Pattern REWARD_POINT_PATTERN = Pattern.compile(REWARD_POINT_REGEX);
    private static final Pattern PERCENT_PATTERN = Pattern.compile("(\\d+)%");
    private Set<Integer> accessibleAltars = new HashSet<>();
    private static final Set<Integer> GOTR_REGIONS = ImmutableSet.of(14483, 14484);
    private static final int PORTAL_WIDGET_ID = 48889884;
    int timeout = 0;
    int gameEndTimeout = 10;
    public boolean isGameEnding = false;

    @Inject
    public GOTRState(EventBus eventBus, AutoRiftsConfig config) {
        eventBus.register(this);
        this.eventBus = eventBus;
        this.config = config;
    }

    public void register() {
        eventBus.register(this);
    }

    public void deregister() {
        eventBus.unregister(this);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }

        if (timeout > 0) {
            timeout--;
            return;
        }

        if (this.accessibleAltars.isEmpty()) {
            this.accessibleAltars = Utility.getAccessibleAltars(client.getRealSkillLevel(Skill.RUNECRAFT),
                    Quest.LOST_CITY.getState(client), Quest.TROLL_STRONGHOLD.getState(client),
                    Quest.MOURNINGS_END_PART_II.getState(client), Quest.SINS_OF_THE_FATHER.getState(client));
        }

        Optional<Widget> frags = Inventory.search().withId(ItemID.GUARDIAN_FRAGMENTS).first();

        if (!hasFirstPortalSpawned && (isPortalSpawned() || getPower() > 15 || (frags.isPresent() && frags.get().getItemQuantity() >= 250))) {
            hasFirstPortalSpawned = true;
        }

        if (!gameStarted && isWidgetVisible()) {
            gameStarted = true;
        }

        if (!isInAltar() && !isWidgetVisible()) {
            gameEndTimeout--;
            if (gameEndTimeout == 0) {
                log.info("Setting game to ended");
                gameStarted = false;
                isGameEnding = false;
                hasFirstPortalSpawned = false;
            }
        } else {
            gameEndTimeout = 10;
        }
    }

    private int getPower() {
        Optional<Widget> pWidget = Widgets.search().withId(48889874).first();
        if (pWidget.isEmpty()) {
            return 0;
        }

        Matcher matcher = PERCENT_PATTERN.matcher(pWidget.get().getText());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return 0;
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }

        if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.MESBOX) {
            return;
        }

        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (event.getMessage().contains(Constants.GAME_STARTED)) {
            gameStarted = true;
        }

        if (event.getType() == ChatMessageType.MESBOX) {
            Matcher checkPointMatcher = CHECK_POINT_PATTERN.matcher(event.getMessage());
            if (checkPointMatcher.find()) {
                catalyticPoints = Integer.parseInt(checkPointMatcher.group(1));
                elementalPoints = Integer.parseInt(checkPointMatcher.group(2));
            }
        }

        if (event.getMessage().contains("successfully closed the rift")) {
            gameStarted = false;
            hasFirstPortalSpawned = false;
            isGameEnding = false;
        }

        if (event.getMessage().contains("Guardian was defeated")) {
            gameStarted = false;
            hasFirstPortalSpawned = false;
            isGameEnding = false;
        }

        if (event.getMessage().contains("is fully charged")) {
            isGameEnding = true;
        }

        Matcher rewardPointMatcher = REWARD_POINT_PATTERN.matcher(event.getMessage());
        if (rewardPointMatcher.find()) {
            elementalPoints = Integer.parseInt(rewardPointMatcher.group(1).replaceAll(",", ""));
            catalyticPoints = Integer.parseInt(rewardPointMatcher.group(2).replaceAll(",", ""));
        }
    }

    private boolean isWidgetVisible() {
        Optional<Widget> widget = Widgets.search().withId(Constants.PARENT_WIDGET).first();
        return widget.isPresent() && !widget.get().isHidden();
    }


    public boolean isInAltar() {
        for (int region : client.getMapRegions()) {
            if (GOTR_REGIONS.contains(region)) {
                return false;
            }
        }
        return true;
    }

    public boolean isOutsideBarrier() {
        return client.getLocalPlayer().getWorldLocation().getY() <= Constants.OUTSIDE_BARRIER_Y && !isInAltar();
    }

    public boolean isInLargeMine() {
        return !isInAltar() && client.getLocalPlayer().getWorldLocation().getX() >= Constants.LARGE_MINE_X;
    }

    public boolean isInHugeMine() {
        return !isInAltar() && client.getLocalPlayer().getWorldLocation().getX() <= Constants.HUGE_MINE_X;
    }

    public boolean isGameBusy() {
        return !isInAltar() && isOutsideBarrier() && TileObjects.search().withId(Constants.BARRIER_BUSY_ID).nearestToPlayer().isPresent();
    }

    public boolean isPortalSpawned() {
        Widget portalWidget = client.getWidget(PORTAL_WIDGET_ID);
        return portalWidget != null && !portalWidget.isHidden();
    }

    public TileObject getNextAltar() {
        int catalytic;
        int elemental;
        TileObject catalyticAltar = null;
        TileObject elementalAltar = null;
        List<TileObject> guardians = TileObjects.search().nameContains("Guardian of").result();
        for (TileObject guardian : guardians) {
            GameObject gameObject = (GameObject) guardian;
            Animation animation = ((DynamicObject) gameObject.getRenderable()).getAnimation();
            //Active guardians
            if (animation.getId() == 9363) {
                if (isCatalytic(guardian)) {
                    if (accessibleAltars.contains(guardian.getId()) && isAltarBetter(catalyticAltar, guardian)) {
                        catalyticAltar = guardian;
                    }
                } else if (isAltarBetter(elementalAltar, guardian)) {
                    elementalAltar = guardian;
                }
            } else {
                if (!hasTalisman(guardian)) {
                    continue;
                }

                if (isCatalytic(guardian)) {
                    if (accessibleAltars.contains(guardian.getId()) && isAltarBetter(catalyticAltar, guardian)) {
                        catalyticAltar = guardian;
                    }
                } else {
                    if (isAltarBetter(elementalAltar, guardian)) {
                        elementalAltar = guardian;
                    }
                }
            }
        }

        if (catalyticPoints < 0 && elementalPoints < 0) {
            elemental = client.getVarbitValue(13686);
            catalytic = client.getVarbitValue(13685);
        } else {
            elemental = elementalPoints;
            catalytic = catalyticPoints;
            elemental += (int) Math.floor((double) client.getVarbitValue(13686) / 100);
            catalytic += (int) Math.floor((double) client.getVarbitValue(13685) / 100);
        }

        //log.info("Catalytic: " + catalytic + " Elemental: " + elemental);

        if (catalytic == 0 && elemental == 0) {
            elemental = 1;
        }

        Widget catalyticWidget = client.getWidget(48889879);
        Widget elementalWidget = client.getWidget(48889876);

        if (elementalWidget == null || catalyticWidget == null) {
            return null;
        }

        if (catalyticAltar == null) {
            return elementalAltar;
        }

        if (elementalAltar == null) {
            return catalyticAltar;
        }

        if (config.prioritizeBloodDeath() && isBloodDeath(catalyticAltar)) {
            return catalyticAltar;
        }

        if (isAltarIgnored(catalyticAltar)) {
            if (isAltarIgnored(elementalAltar)) {
                if (catalytic > elemental) {
                    return elementalAltar;
                } else {
                    return catalyticAltar;
                }
            }

            return elementalAltar;
        }

        if (isAltarIgnored(elementalAltar)) {
            return catalyticAltar;
        }

        if (catalytic > elemental) {
            return elementalAltar;
        }

        return catalyticAltar;
    }

    private boolean isBloodDeath(TileObject altar) {
        switch (altar.getId()) {
            case BLOOD_ALTAR:
            case DEATH_ALTAR:
                return true;
            default:
                return false;
        }
    }

    private boolean isAltarBetter(TileObject oldAltar, TileObject candidate) {
        if (oldAltar == null) {
            return true;
        }

        if (getAltarPriority(oldAltar) < getAltarPriority(candidate)) {
            return true;
        }

        if (getAltarPriority(oldAltar) == getAltarPriority(candidate) && hasTalisman(candidate)) {
            return true;
        }

        return false;
    }

    private boolean isAltarIgnored(TileObject altar) {
        switch (altar.getId()) {
            case AIR_ALTAR:
                return config.ignoreAir();
            case MIND_ALTAR:
                return config.ignoreMind();
            case BODY_ALTAR:
                return config.ignoreBody();
            case WATER_ALTAR:
                return config.ignoreWater();
            case COSMIC_ALTAR:
                return config.ignoreCosmic();
            case CHAOS_ALTAR:
                return config.ignoreChaos();
            case EARTH_ALTAR:
                return config.ignoreEarth();
            case NATURE_ALTAR:
                return config.ignoreNature();
            case LAW_ALTAR:
                return config.ignoreLaw();
            case FIRE_ALTAR:
                return config.ignoreFire();
            case DEATH_ALTAR:
                return config.ignoreDeath();
            case BLOOD_ALTAR:
                return config.ignoreBlood();
        }

        return false;
    }

    private int getAltarPriority(TileObject altar) {
        switch (altar.getId()) {
            case AIR_ALTAR:
            case MIND_ALTAR:
            case BODY_ALTAR:
                return 1;
            case WATER_ALTAR:
            case COSMIC_ALTAR:
            case CHAOS_ALTAR:
                return 2;
            case EARTH_ALTAR:
            case NATURE_ALTAR:
            case LAW_ALTAR:
                return 3;
            case FIRE_ALTAR:
            case DEATH_ALTAR:
                return 4;
            case BLOOD_ALTAR:
                return 5;
        }

        return -1;
    }

    private boolean hasTalisman(TileObject altar) {
        int talismanID = 0;
        switch (altar.getId()) {
            case AIR_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_AIR;
                break;
            case WATER_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_WATER;
                break;
            case EARTH_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_EARTH;
                break;
            case FIRE_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_FIRE;
                break;
            case MIND_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_MIND;
                break;
            case CHAOS_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_CHAOS;
                break;
            case DEATH_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_DEATH;
                break;
            case BLOOD_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_BLOOD;
                break;
            case BODY_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_BODY;
                break;
            case COSMIC_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_COSMIC;
                break;
            case NATURE_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_NATURE;
                break;
            case LAW_ALTAR:
                talismanID = ItemID.PORTAL_TALISMAN_LAW;
                break;
        }

        return Inventory.getItemAmount(talismanID) > 0;
    }

    private boolean isCatalytic(TileObject altar) {
        Set<Integer> catalyticAltars = Set.of(43705, 43709, 43706, 43710, 43711, 43708, 43712, 43707);
        return catalyticAltars.contains(altar.getId());
    }
}
