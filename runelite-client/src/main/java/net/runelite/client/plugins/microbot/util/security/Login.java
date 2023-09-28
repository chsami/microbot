package net.runelite.client.plugins.microbot.util.security;

import net.runelite.api.GameState;
import net.runelite.client.config.ConfigProfile;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.globval.GlobalWidgetInfo;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldRegion;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.math.Random.*;

public class Login {

    public static ConfigProfile activeProfile = null;
    private static final int MAX_PLAYER_COUNT = 1950;

    public Login() {
        this(getRandomWorld(true));
    }

    public Login(int world) {
        this(activeProfile.getName(), activeProfile.getPassword(), world);
    }

    public Login(String username, String password) {
        this(username, password, 360);
    }

    public Login(String username, String password, int world) {
        Microbot.getMouse().click(random(830, 850), random(294, 316)); //clicks a button "OK" when you've been disconnected
        sleep(300, 600);
        VirtualKeyboard.keyPress(KeyEvent.VK_ENTER);
        sleep(300, 600);
        try {
            setWorld(world);
        } catch (Exception e) {
            System.out.println("Changing world failed");
        }
        Microbot.getClient().setUsername(username);
        try {
            Microbot.getClient().setPassword(Encryption.decrypt(password));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sleep(300, 600);
        VirtualKeyboard.keyPress(KeyEvent.VK_ENTER);
    }

    public boolean activateCondition() {
        GameState idx = Microbot.getClient().getGameState();
        return ((Rs2Menu.getIndex("Play") == 0 || (idx == GameState.LOGIN_SCREEN || idx == GameState.LOGGING_IN)) && activeProfile.getName() != null)
                || (idx == GameState.LOGGED_IN && Rs2Widget.getWidget(GlobalWidgetInfo.LOGIN_MOTW_TEXT.getPackedId(), 0) != null);
    }

    public void setWorld(int worldNumber) {
        net.runelite.http.api.worlds.World world = Microbot.getWorldService().getWorlds().findWorld(worldNumber);
        final net.runelite.api.World rsWorld = Microbot.getClient().createWorld();
        rsWorld.setActivity(world.getActivity());
        rsWorld.setAddress(world.getAddress());
        rsWorld.setId(world.getId());
        rsWorld.setPlayerCount(world.getPlayers());
        rsWorld.setLocation(world.getLocation());
        rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
        Microbot.getClient().changeWorld(rsWorld);
    }

    public static int getRandomWorld(boolean isMembers) {
        return getRandomWorld(isMembers, null);
    }

    public static int getRandomWorld(boolean isMembers, WorldRegion region) {
        WorldResult worldResult = Microbot.getWorldService().getWorlds();

        List<World> worlds;
        if (worldResult != null) {
            worlds = worldResult.getWorlds();
            Random r = new Random();
            List<World> filteredWorlds = worlds
                    .stream()
                    .filter(x ->
                            (!x.getTypes().contains(WorldType.PVP) &&
                                    !x.getTypes().contains(WorldType.HIGH_RISK) &&
                                    !x.getTypes().contains(WorldType.BOUNTY) &&
                                    !x.getTypes().contains(WorldType.SKILL_TOTAL) &&
                                    !x.getTypes().contains(WorldType.LAST_MAN_STANDING) &&
                                    !x.getTypes().contains(WorldType.QUEST_SPEEDRUNNING) &&
                                    !x.getTypes().contains(WorldType.BETA_WORLD) &&
                                    !x.getTypes().contains(WorldType.DEADMAN) &&
                                    !x.getTypes().contains(WorldType.PVP_ARENA) &&
                                    !x.getTypes().contains(WorldType.TOURNAMENT) &&
                                    !x.getTypes().contains(WorldType.FRESH_START_WORLD)) &&
                                    x.getPlayers() < MAX_PLAYER_COUNT &&
                                    x.getPlayers() >= 0)
                    .collect(Collectors.toList());

            if (!isMembers) {
                filteredWorlds = filteredWorlds
                        .stream()
                        .filter(x -> !x.getTypes().contains(WorldType.MEMBERS)).collect(Collectors.toList());
            } else {
                filteredWorlds = filteredWorlds
                        .stream()
                        .filter(x -> x.getTypes().contains(WorldType.MEMBERS)).collect(Collectors.toList());
            }

            if (region != null)
                filteredWorlds = filteredWorlds
                    .stream()
                    .filter(x -> x.getRegion() == region).collect(Collectors.toList());

            World world =
                    filteredWorlds.stream()
                            .skip(r.nextInt(filteredWorlds.size()))
                            .findFirst()
                            .orElse(null);

            if (world != null) {
                return world.getId();
            }
        }

        return isMembers ? 360 : 383;
    }
}
