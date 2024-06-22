package net.runelite.client.plugins.microbot.bankjs.development.other;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils.*;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.enums.SlayerMasters;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.enums.State;
import net.runelite.client.plugins.microbot.util.MicrobotInventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.security.Login;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.MicrobotInventorySetup.doesEquipmentMatch;
import static net.runelite.client.plugins.microbot.util.MicrobotInventorySetup.doesInventoryMatch;

public class BanksTestScript extends Script {
    public ScheduledFuture<?> mainScheduledFuture;


    private BanksTestConfig config;
    private OverlayManager overlayManager;
    private Client client;
    private ConfigManager configManager;
    private ClientThread clientThread;

    @Inject
    public BanksTestScript(OverlayManager overlayManager, ConfigManager configManager, ClientThread clientThread, Client client) {
        this.overlayManager = overlayManager;
        this.configManager = configManager;
        this.clientThread = clientThread;
        this.client = client;
    }

    @Inject
    private NPCManager npcManager;

    @Getter
    private String taskName;

    @Getter
    private AtomicReference<State> currentState = new AtomicReference<>(State.IDLE);

    public static String version = "0.2.4";

    private volatile boolean isActive = false;

    public boolean run(BanksTestConfig config) {

        this.config = config;



        Microbot.enableAutoRunOn = true;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (client.getGameState() != GameState.LOGGED_IN) {
                    return;
                }
                GameObject tunnel2 = Rs2GameObject.findObjectByImposter(27257, "Use");
                GameObject tunnel = Rs2GameObject.get("Tunnel");
                List<TileObject>  all = Rs2GameObject.getAll();

                for (TileObject tileObject : all) {
                    if (tileObject instanceof GameObject) {
                        GameObject gameObject = (GameObject) tileObject;
                        System.out.println("GameObject ID: " + gameObject.getId() + ", ID: " + gameObject.getId());
                    } else if (tileObject instanceof GroundObject) {
                        GroundObject groundObject = (GroundObject) tileObject;
                        System.out.println("GroundObject ID: " + groundObject.getId() + ", ID: " + groundObject.getId());
                    } else {
                        System.out.println("TileObject ID: " + tileObject.getId() + ", Type: " + tileObject.getClass().getSimpleName());
                    }
                }

                if (tunnel != null) {
                    System.out.println("Tunnel Found: " + tunnel);
                } else {
                    System.out.println("Tunnel not Found");
                }
;
            } catch (Exception ex) {
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        return true;
    }

        }
