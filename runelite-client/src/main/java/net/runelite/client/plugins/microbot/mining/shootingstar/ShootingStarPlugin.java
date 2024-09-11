package net.runelite.client.plugins.microbot.mining.shootingstar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.mining.shootingstar.enums.ShootingStarLocation;
import net.runelite.client.plugins.microbot.mining.shootingstar.model.Star;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PluginDescriptor(
        name = PluginDescriptor.GZ + "ShootingStar",
        description = "Finds & Travels to shooting stars",
        tags = {"mining", "microbot", "skilling", "star", "shooting"},
        enabledByDefault = false
)
public class ShootingStarPlugin extends Plugin {
    private static final int TICKS_PER_MINUTE = 100;
    private static final int UPDATE_INTERVAL = 3;
    private static final ZoneId utcZoneId = ZoneId.of("UTC");
    public static String version = "1.0.0";
    @Getter
    public List<Star> starList = new ArrayList<>();
    @Inject
    ShootingStarScript shootingStarScript;
    private String httpEndpoint;
    private int apiTickCounter = 0;
    private int updateListTickCounter = 0;
    private int lastWorld;
    @Getter
    private boolean displayAsMinutes;
    @Getter
    private boolean hideMembersWorlds;
    @Getter
    private boolean hideF2PWorlds;
    @Getter
    private boolean hideWildernessLocations;
    private boolean useNearestHighTierStar;
    @Inject
    private WorldService worldService;
    @Inject
    private ShootingStarConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ShootingStarOverlay shootingStarOverlay;
    @Inject
    private ClientToolbar clientToolbar;
    private NavigationButton navButton;
    private ShootingStarPanel panel;

    public void fetchStars() {
        // Create HTTP request to pull in StarData from API
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpEndpoint))
                .build();
        String jsonResponse = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Star>>() {}.getType();
        List<Star> starData = gson.fromJson(jsonResponse, listType);

        ZonedDateTime now = ZonedDateTime.now(utcZoneId);

        // Format starData into Star Model
        for (Star star : starData) {
            // Filter out stars that ended longer than three mintues ago to avoid adding really old stars
            if (star.getEndsAt() < now.minusMinutes(UPDATE_INTERVAL).toInstant().toEpochMilli()) continue;

            // Set ObjectID & MiningLevel based on Shooting Star Tier
            star.setObjectID(star.getObjectIDBasedOnTier());
            star.setMiningLevel(star.getRequiredMiningLevel());

            // Populate ShootingStarLocation based on locationKey & rawLocation
            ShootingStarLocation location = findLocation(star.getLocationKey().toString(), star.getRawLocation());
            if (location == null) {
                System.out.printf("No match found for location: %s - %s%n", star.getLocationKey(), star.getRawLocation());
                continue;
            }

            star.setShootingStarLocation(location);
            star.setWorldObject(findWorld(star.getWorld()));

            if (star.isGameModeWorld()) continue;

            addToList(star);
        }
        filterPanelList(hideWildernessLocations || hideMembersWorlds || hideF2PWorlds);
        updatePanelList(true);
    }

    private ShootingStarLocation findLocation(String locationKey, String rawLocation) {
        for (ShootingStarLocation location : ShootingStarLocation.values()) {
            boolean enumName = locationKey.equalsIgnoreCase(location.name());
            boolean locationString = rawLocation.equalsIgnoreCase(location.getRawLocationName()) || locationKey.equalsIgnoreCase(location.getShortLocationName());
            if (enumName || locationString) {
                return location;
            }
        }
        return null;
    }

    private World findWorld(int worldID) {
        WorldResult worldResult = worldService.getWorlds();
        if (worldResult == null) return null;
        return worldResult.findWorld(worldID);
    }

    private void addToList(Star data) {
        // Find oldStar inside of starList
        Star oldStar = starList.stream()
                .filter(star -> data.getWorld() == star.getWorld())
                .filter(star -> data.getShootingStarLocation().equals(star.getShootingStarLocation()))
                .findFirst()
                .orElse(null);

        // If there is an oldStar in the same world & location
        if (oldStar != null) {
            updateStarInList(oldStar, data);
            return;
        }

        // If oldStar not found, add new star into the list
        starList.add(data);
    }

    private void updateStarInList(Star oldStar, Star newStar) {
        oldStar.setTier(newStar.getTier());
        oldStar.setObjectID(oldStar.getObjectIDBasedOnTier());
        oldStar.setEndsAt(newStar.getEndsAt());
        oldStar.setMiningLevel(oldStar.getRequiredMiningLevel());
    }
    
    private void checkDepletedStars() {
        List<Star> stars = new ArrayList<>(starList);
        ZonedDateTime now = ZonedDateTime.now(utcZoneId);
        boolean fullUpdate = false;

        for (Star star : stars) {
            if (star.getEndsAt() < now.minusMinutes(UPDATE_INTERVAL).toInstant().toEpochMilli()) {
                removeStar(star);
                fullUpdate = true;
            }
        }

        updatePanelList(fullUpdate);
    }

    public void removeWorldsInClipboard() {
        try {
            final String clipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString().trim();
            Matcher matcher = Pattern.compile("[wW]([3-5][0-9][0-9])").matcher(clipboard);
            Set<Integer> worlds = new HashSet<>();
            while (matcher.find()) {
                try {
                    worlds.add(Integer.parseInt(matcher.group(1)));
                } catch (NumberFormatException ignored) {

                }
            }
            if (worlds.isEmpty()) {
                Microbot.log("No worlds in format w451 found in clipboard.");
                return;
            }
            long timeNow = ZonedDateTime.now(utcZoneId).toInstant().toEpochMilli();
            // don't remove stars if it's impossible for them to have landed already.
            int sizeBefore = starList.size();
            starList.removeIf(s -> timeNow >= s.getCalledAt() && worlds.contains(s.getWorld()));
            int sizeAfter = starList.size();
            Microbot.log("Removed " + (sizeBefore - sizeAfter) + " worlds.");
        } catch (NumberFormatException | IOException | UnsupportedFlavorException | JsonSyntaxException ex) {
            Microbot.log(ex.getMessage());
            return;
        }
        updatePanelList(true);
    }

    @Provides
    ShootingStarConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ShootingStarConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        displayAsMinutes = config.isDisplayAsMinutes();
        hideMembersWorlds = config.isHideMembersWorlds();
        hideF2PWorlds = config.isHideF2PWorlds();
        useNearestHighTierStar = config.useNearestHighTierStar();
        hideWildernessLocations = config.isHideWildernessLocations();
        try {
            loadUrlFromProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fetchStars();
        createPanel();
        SwingUtilities.invokeLater(() -> panel.hideStars(starList));

        if (overlayManager != null) {
            overlayManager.add(shootingStarOverlay);
        }
        shootingStarScript.run(config);
    }

    protected void shutDown() {
        shootingStarScript.shutdown();
        removePanel();
        starList.clear();
        overlayManager.remove(shootingStarOverlay);
    }

    @Subscribe
    public void onConfigChanged(final ConfigChanged event) {
        if (!event.getGroup().equals(ShootingStarConfig.configGroup)) {
            return;
        }

        if (event.getKey().equals(ShootingStarConfig.displayAsMinutes)) {
            displayAsMinutes = config.isDisplayAsMinutes();
            updatePanelList(true);
        }

        if (event.getKey().equals(ShootingStarConfig.hideMembersWorlds)) {
            hideMembersWorlds = config.isHideMembersWorlds();
            filterPanelList(hideMembersWorlds);
            updatePanelList(true);
        }

        if (event.getKey().equals(ShootingStarConfig.hideF2PWorlds)) {
            hideF2PWorlds = config.isHideF2PWorlds();
            filterPanelList(hideF2PWorlds);
            updatePanelList(true);
        }

        if (event.getKey().equals(ShootingStarConfig.hideWildernessLocations)) {
            hideWildernessLocations = config.isHideWildernessLocations();
            filterPanelList(hideWildernessLocations);
            updatePanelList(true);
        }

        if (event.getKey().equals(ShootingStarConfig.useNearestHighTierStar)) {
            useNearestHighTierStar = config.useNearestHighTierStar();
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!Microbot.isLoggedIn()) return;

        if (updateListTickCounter >= TICKS_PER_MINUTE) {
            checkDepletedStars();
            updateListTickCounter = 0;
        }

        if (apiTickCounter >= (TICKS_PER_MINUTE * UPDATE_INTERVAL)) {
            fetchStars();
            apiTickCounter = 0;
        }

        updateListTickCounter++;
        apiTickCounter++;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN && Microbot.getClient().getWorld() != lastWorld) {
            lastWorld = Microbot.getClient().getWorld();
            updatePanelList(true);
        }
    }

    private void createPanel() {
        if (panel == null) {
            panel = new ShootingStarPanel(this);
            final BufferedImage icon = ImageUtil.loadImageResource(ShootingStarPlugin.class, "icon.png");

            navButton = NavigationButton.builder()
                    .tooltip("Shooting Stars")
                    .icon(icon)
                    .priority(7)
                    .panel(panel)
                    .build();
            clientToolbar.addNavigation(navButton);
        }
    }

    private void removePanel() {
        clientToolbar.removeNavigation(navButton);
        navButton = null;
        panel = null;
    }

    public Star getClosestHighestTierStar() {
        // Get the highest tier available
        int highestTier = starList.stream()
                .filter(Star::hasRequirements)
                .filter(s -> panel.getHiddenStars().stream().noneMatch(h -> h.equals(s)))
                .mapToInt(Star::getTier)
                .max()
                .orElse(-1);  // Return -1 if no star meets the requirements

        // If no star meets the requirements, return null
        if (highestTier == -1) {
            return null;
        }

        int minTier = Math.max(1, highestTier - 2); // The lowest tier to consider (at least 1)
        int maxTier = Math.min(9, highestTier + 1); // The highest tier to consider (up to 9)

        Map<Integer, List<Star>> distanceMap = new HashMap<>();

        // Iterate through all stars and categorize them by distance
        for (Star star : starList) {
            if (panel.getHiddenStars().stream().anyMatch(h -> h.equals(star))) continue;
            if (!star.hasRequirements()) continue;

            int starTier = star.getTier();
            if (starTier >= minTier && starTier <= maxTier) {
                WorldPoint starLocation = ShootingStarLocation.valueOf(star.getShootingStarLocation().name()).getWorldPoint();

                // Check if the star's location is already in the distanceMap
                Integer existingDistance = distanceMap.entrySet().stream()
                        .filter(entry -> entry.getValue().stream()
                                .anyMatch(s -> ShootingStarLocation.valueOf(s.getShootingStarLocation().name()).getWorldPoint().equals(starLocation)))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);
                
                // Set distance to the distance that is found in the map for the duplicate location or calculate shortest distance if not found
                int distance;
                distance = Objects.requireNonNullElseGet(existingDistance, () -> Rs2Player.distanceTo(starLocation));

                distanceMap.computeIfAbsent(distance, k -> new ArrayList<>()).add(star);
            }
        }

        // Find the closest stars
        Optional<Integer> closestDistanceOpt = distanceMap.keySet().stream().min(Integer::compare);

        if (closestDistanceOpt.isPresent()) {
            List<Star> closestStars = distanceMap.get(closestDistanceOpt.get());

            // Return the highest-tiered star among the closest ones
            return closestStars.stream()
                    .max(Comparator.comparingInt(Star::getTier))
                    .orElse(null);
        }

        // If no star is found
        return null;
    }

    public boolean useNearestHighTierStar() {
        return useNearestHighTierStar;
    }

    public void removeStar(Star star) {
        if (star.equals(getSelectedStar()))
            star.setSelected(false);
        starList.remove(star);
        panel.getHiddenStars().remove(star);
    }

    public void updateSelectedStar(Star star) {
        if (getSelectedStar() != null) {
            Star oldStar = getSelectedStar();
            oldStar.setSelected(false);
        }
        star.setSelected(!star.isSelected());
    }

    private void filterPanelList(boolean toggle) {
        List<Star> stars = new ArrayList<>(starList);
        
        if (toggle) {
            SwingUtilities.invokeLater(() -> panel.hideStars(stars));
        } else {
            SwingUtilities.invokeLater(() -> panel.showStars());
        }
    }

    public void updatePanelList(boolean fullUpdate) {
        List<Star> stars = new ArrayList<>(starList);

        if (fullUpdate) {
            SwingUtilities.invokeLater(() -> panel.updateList(stars));
        } else {
            SwingUtilities.invokeLater(() -> panel.refreshList(stars));
        }
    }

    public Star getSelectedStar() {
        return starList.stream().filter(Star::isSelected).findFirst().orElse(null);
    }

    private void loadUrlFromProperties() throws IOException {
        Properties properties = new Properties();

        try (InputStream input = ShootingStarPlugin.class.getResourceAsStream("shootingstar.properties")) {
            if (input == null) {
                System.out.println("unable to load shootingstar.properties");
                return;
            }

            properties.load(input);
            httpEndpoint = properties.getProperty("microbot.shootingstar.http");
        }
    }
}
