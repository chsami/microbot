package net.runelite.client.plugins.microbot.util.security;

import net.runelite.api.GameState;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigProfile;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.globval.GlobalWidgetInfo;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class Login {

    public static ConfigProfile activeProfile = null;
    private static final int MAX_PLAYER_COUNT = 1950;

    public Login() {
        this(360);
    }

    public Login(int world) {
        this(activeProfile.getName(), activeProfile.getPassword(), world);
    }

    public Login(String username, String password) {
        this(username, password, 360);
    }

    public Login(String username, String password, int world) {
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

    //TODO: this should be elsewhere
    private static final int INTERFACE_MAIN = 905;
    private static final int INTERFACE_MAIN_CHILD = 59;
    private static final int INTERFACE_MAIN_CHILD_COMPONENT_ID = 4;
    private static final int INTERFACE_LOGIN_SCREEN = 596;
    private static final int INTERFACE_USERNAME = 65;
    private static final int INTERFACE_USERNAME_WINDOW = 37;
    private static final int INTERFACE_PASSWORD = 71;
    private static final int INTERFACE_PASSWORD_WINDOW = 39;
    private static final int INTERFACE_BUTTON_LOGIN = 42;
    private static final int INTERFACE_TEXT_RETURN = 11;
    private static final int INTERFACE_BUTTON_BACK = 60;
    private static final int INTERFACE_WELCOME_SCREEN = 906;
    private static final int INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_1 = 160;
    private static final int INTERFACE_WELCOME_SCREEN_BUTTON_PLAY_2 = 171;
    //private static final int INTERFACE_WELCOME_SCREEN_BUTTON_LOGOUT = 193;
    private static final int INTERFACE_WELCOME_SCREEN_TEXT_RETURN = 221;
    private static final int INTERFACE_WELCOME_SCREEN_BUTTON_BACK = 218;
    private static final int INTERFACE_WELCOME_SCREEN_HIGH_RISK_WORLD_TEXT = 86;
    private static final int INTERFACE_WELCOME_SCREEN_HIGH_RISK_WORLD_LOGIN_BUTTON = 93;
    private static final int INTERFACE_GRAPHICS_NOTICE = 976;
    private static final int INTERFACE_GRAPHICS_LEAVE_ALONE = 6;
    private static final int INDEX_LOGGED_OUT = 3;
    private static final int INDEX_LOBBY = 7;

    private int invalidCount, worldFullCount;

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

    private boolean switchingWorlds() {
        return Rs2Widget.getWidget(INTERFACE_WELCOME_SCREEN, INTERFACE_WELCOME_SCREEN_TEXT_RETURN) != null
                && Rs2Widget.getWidget(INTERFACE_WELCOME_SCREEN, INTERFACE_WELCOME_SCREEN_TEXT_RETURN)
                .getText().contains("just left another world");
    }

    // Clicks past all of the letters
    private boolean atLoginInterface(Widget i) {
        if (i == null) {
            return false;
        }
        Rectangle pos = i.getBounds();
        if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
            return false;
        }
        int dy = (int) (pos.getHeight() - 4) / 2;
        int maxRandomX = (int) (pos.getMaxX() - pos.getCenterX());
        int midx = (int) (pos.getCenterX());
        int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
        if (i.getIndex() == INTERFACE_PASSWORD_WINDOW) {
            Microbot.getMouse().click(minX(i), midy + random(-dy, dy));
        } else {
            Microbot.getMouse().click(midx + random(1, maxRandomX), midy + random(-dy, dy));
        }
        return true;
    }

    /*
     * Returns x int based on the letters in a Child Only the password text is
     * needed as the username text cannot reach past the middle of the interface
     */
    private int minX(Widget a) {
        int x = 0;
        Rectangle pos = a.getBounds();
        int dx = (int) (pos.getWidth() - 4) / 2;
        int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
        if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
            return 0;
        }
        Widget widget = Rs2Widget.getWidget(INTERFACE_LOGIN_SCREEN, 0);
        for (int i = 0; i < Rs2Widget.getWidget(GlobalWidgetInfo.TO_GROUP(widget.getId()), INTERFACE_PASSWORD).getText().length(); i++) {
            x += 11;
        }
        if (x > 44) {
            return (int) (pos.getMinX() + x + 15);
        } else {
            return midx + random(-dx, dx);
        }
    }

    private boolean atLoginScreen() {
        return !Rs2Widget.getWidget(596, 0).isHidden();
    }

    private boolean isUsernameFilled() {
        String username = Login.activeProfile.getName();
        Widget widget = Rs2Widget.getWidget(INTERFACE_LOGIN_SCREEN, 0);
        return Rs2Widget.getWidget(GlobalWidgetInfo.TO_GROUP(widget.getId()), INTERFACE_USERNAME).getText().toLowerCase().equalsIgnoreCase(username);
    }

    public static int getRandomWorld(boolean isMembers) {
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
