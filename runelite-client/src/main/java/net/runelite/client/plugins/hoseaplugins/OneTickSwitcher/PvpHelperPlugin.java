package net.runelite.client.plugins.hoseaplugins.OneTickSwitcher;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Players;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InventoryInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.PlayerPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.InventoryUtil;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PrayerUtil;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.SpellUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Player;
import net.runelite.api.PlayerComposition;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> PVP Helper</html>",
        description = ";)",
        enabledByDefault = false,
        tags = {"ethan", "piggy"}
)
@Slf4j
public class PvpHelperPlugin extends Plugin {

    private static final int SPEC_BAR = 38862885;

    @Inject
    private Client client;
    @Inject
    private PvpHelperConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ItemManager itemManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PvpHelperTargetOverlay targetOverlay;
    @Inject
    private PvpHelperValueOverlay valueOverlay;

    @Getter
    private Actor target;

    @Provides
    private PvpHelperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PvpHelperConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggleSpecListener);
        keyManager.registerKeyListener(gearOneListener);
        keyManager.registerKeyListener(gearTwoListener);
        keyManager.registerKeyListener(gearThreeListener);
        keyManager.registerKeyListener(gearFourListener);
        keyManager.registerKeyListener(gearFiveListener);
        keyManager.registerKeyListener(gearSixListener);
        keyManager.registerKeyListener(prayerOneListener);
        keyManager.registerKeyListener(prayerTwoListener);
        keyManager.registerKeyListener(prayerThreeListener);
        keyManager.registerKeyListener(spellOneListener);
        keyManager.registerKeyListener(spellTwoListener);
        keyManager.registerKeyListener(spellThreeListener);
        keyManager.registerKeyListener(tripleEatListener);
        keyManager.registerKeyListener(vengListener);
        overlayManager.add(targetOverlay);
        overlayManager.add(valueOverlay);
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggleSpecListener);
        keyManager.unregisterKeyListener(gearOneListener);
        keyManager.unregisterKeyListener(gearTwoListener);
        keyManager.unregisterKeyListener(gearThreeListener);
        keyManager.unregisterKeyListener(gearFourListener);
        keyManager.unregisterKeyListener(gearFiveListener);
        keyManager.unregisterKeyListener(gearSixListener);
        keyManager.unregisterKeyListener(prayerOneListener);
        keyManager.unregisterKeyListener(prayerTwoListener);
        keyManager.unregisterKeyListener(prayerThreeListener);
        keyManager.unregisterKeyListener(spellOneListener);
        keyManager.unregisterKeyListener(spellTwoListener);
        keyManager.unregisterKeyListener(spellThreeListener);
        keyManager.unregisterKeyListener(tripleEatListener);
        keyManager.unregisterKeyListener(vengListener);
        overlayManager.remove(targetOverlay);
        overlayManager.remove(valueOverlay);
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("OneTickSwitcher")) {
            // let's nest this, in case we want to add other items that rely on this event.

            if (event.getKey().equals("copyGear")) {
                clientThread.invoke(() -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Item item : this.client.getItemContainer(InventoryID.EQUIPMENT).getItems()) {
                        if (item != null && item.getId() != -1
                                && item.getId() != 6512) {
                            ItemComposition itemComposition = client.getItemDefinition(item.getId());
                            stringBuilder.append(itemComposition.getName()).append(",");
                        }
                    }
                    if (stringBuilder.length() > 0) {
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    }
                    StringSelection selection = new StringSelection(stringBuilder.toString());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                });
            }
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (target != null && !client.getPlayers().contains(target)) {
            target = null;
        }

        if (config.autoEat() && client.getBoostedSkillLevel(Skill.HITPOINTS) <= config.eatThreshold()) {
            // WARNING!!!
            // This will spam packets if you are on a cooldown and cannot eat, be careful using it.
            autoEat();
        }
    }

    @Subscribe
    private void onMenuEntryAdded(MenuEntryAdded event) {
        if (event.getType() == MenuAction.PLAYER_THIRD_OPTION.getId()) {
            if (target == null) {
                addFocusEntry(event, "Focus Target", this::focusTarget);
                return;
            }

            Player currentTarget = (Player) target;
            if (event.getIdentifier() == currentTarget.getId()) {
                addFocusEntry(event, "Reset Target", this::focusTarget);
                return;
            }

            addFocusEntry(event, "Focus Target", this::focusTarget);
        }
    }

    @Subscribe
    private void onActorDeath(ActorDeath event) {
        if (target != null & event.getActor().equals(target)) {
            target = null;
            if (config.showOverlay()) {
                valueOverlay.setItemValue(0);
                valueOverlay.setHidden(true);
            }
        }
    }

    @Subscribe
    private void onInteractingChanged(InteractingChanged event) {
        if (client.getGameState() != GameState.LOGGED_IN
                || event.getTarget() == null
                || !(event.getTarget() instanceof Player)
                || !config.autoFocusTarget()
                || (client.getLocalPlayer().isInteracting() && !event.getTarget().equals(client.getLocalPlayer().getInteracting()))) {
            return;
        }

        if (event.getSource().equals(client.getLocalPlayer())) {
            focusTarget((Player) event.getTarget());
        } else if (event.getTarget().equals(client.getLocalPlayer())) {
            focusTarget((Player) event.getSource());
        }
    }

    private void addFocusEntry(MenuEntryAdded event, String entryText, Consumer<MenuEntry> menuEntryConsumer) {
        client.createMenuEntry(client.getMenuEntries().length - 2)
                .setTarget(event.getTarget())
                .setOption(entryText)
                .setIdentifier(event.getIdentifier())
                .setType(MenuAction.RUNELITE)
                .onClick(menuEntryConsumer);
    }

    private void focusTarget(Player player) {
        if (player == null) {
            return;
        }

        target = player;
        EthanApiPlugin.sendClientMessage("[PVP Helper] "  + target.getName() + " focused.");
        if (config.showOverlay()) {
            // This might need to be done on the client thread, but unsure.
            // come back to it later I guess.
            int expectedValue = 0;
            Player target = (Player) this.target; // this.target is an Actor because we will add NPC support later.
            PlayerComposition playerComposition = target.getPlayerComposition();
            for (KitType kit : KitType.values()) {
                if (kit != KitType.HAIR && kit != KitType.JAW) {
                    int id = playerComposition.getEquipmentId(kit);
                    if (id != 1 && id != 6513) {
                        expectedValue += itemManager.getItemPrice(id);
                    }
                }
            }
            valueOverlay.setHidden(false);
            valueOverlay.setItemValue(expectedValue);
        }
    }

    private void focusTarget(MenuEntry entry) {
        if (entry.getOption().equals("Focus Target")) {
            // TODO add NPC target support, cba right now
            Optional<Player> targetPlayer = Players.search().filter(player -> player.getId() == entry.getIdentifier()).first();

            if (targetPlayer.isEmpty()) {
                if (config.showOverlay()) {
                    valueOverlay.setItemValue(0);
                    valueOverlay.setHidden(true);
                }
                EthanApiPlugin.sendClientMessage("[PVP Helper] Unable to focus target player");
                return;
            }

            focusTarget(targetPlayer.get());
        } else if (entry.getOption().equals("Reset Target")) {
            target = null;
            EthanApiPlugin.sendClientMessage("[PVP Helper] Target unfocused.");
            if (config.showOverlay()) {
                valueOverlay.setItemValue(0);
                valueOverlay.setHidden(true);
            }
        }
    }

    private void castSpellOnTarget(String spellName) {
        clientThread.invoke(() -> {
            Widget spellWidget = SpellUtil.getSpellWidget(client, spellName);

            if (spellWidget != null) {
                MousePackets.queueClickPacket();
                PlayerPackets.queueWidgetOnPlayer((Player) target, spellWidget);
                return;
            }
        });
    }

    private List<String> getGearNames(String gear) {
        return Arrays.stream(gear.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public static Prayer[] parsePrayers(String prayerString) {
        String[] prayerNames = prayerString.split(",");
        return Arrays.stream(prayerNames)
                .map(String::trim)
                .map(name -> name.replace(' ', '_'))
                .map(name -> Arrays.stream(Prayer.values())
                        .filter(prayer -> prayer.name().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null))
                .filter(prayer -> prayer != null)
                .toArray(Prayer[]::new);
    }

    private void swapGear(List<String> gearNames) {
        for (String gearName : gearNames) {
            InventoryUtil.nameContainsNoCase(gearName).first().ifPresent(item -> {
                InventoryInteraction.useItem(item, "Equip", "Wield", "Wear");
            });
        }
    }

    private void toggleSpec() {
        clientThread.invoke(() -> {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, SPEC_BAR, -1, -1);
        });
    }

    private final HotkeyListener toggleSpecListener = new HotkeyListener(() -> config.specToggle()) {
        @Override
        public void hotkeyPressed() {
            toggleSpec();
        }
    };

    private final HotkeyListener gearOneListener = new HotkeyListener(() -> config.oneToggle()) {
        @Override
        public void hotkeyPressed() {
            toggleGear(getGearNames(config.oneGear()));
        }
    };

    private final HotkeyListener gearTwoListener = new HotkeyListener(() -> config.twoToggle()) {
        @Override
        public void hotkeyPressed() {
            toggleGear(getGearNames(config.twoGear()));
        }
    };

    private final HotkeyListener gearThreeListener = new HotkeyListener(() -> config.threeToggle()) {
        @Override
        public void hotkeyPressed() {
            toggleGear(getGearNames(config.threeGear()));
        }
    };

    private final HotkeyListener gearFourListener = new HotkeyListener(() -> config.fourToggle()) {
        @Override
        public void hotkeyPressed() {
            toggleGear(getGearNames(config.fourGear()));
        }
    };

    private final HotkeyListener gearFiveListener = new HotkeyListener(() -> config.fiveToggle()) {
        @Override
        public void hotkeyPressed() {
            toggleGear(getGearNames(config.fiveGear()));
        }
    };

    private final HotkeyListener gearSixListener = new HotkeyListener(() -> config.sixToggle()) {
        @Override
        public void hotkeyPressed() {
            toggleGear(getGearNames(config.sixGear()));
        }
    };

    private final HotkeyListener prayerOneListener = new HotkeyListener(() -> config.onePrayerToggle()) {
        @Override
        public void hotkeyPressed() {
            clientThread.invoke(() -> PrayerUtil.toggleMultiplePrayers(parsePrayers(config.onePrayer())));
        }
    };

    private final HotkeyListener prayerTwoListener = new HotkeyListener(() -> config.twoPrayerToggle()) {
        @Override
        public void hotkeyPressed() {
            clientThread.invoke(() -> PrayerUtil.toggleMultiplePrayers(parsePrayers(config.twoPrayer())));
        }
    };

    private final HotkeyListener prayerThreeListener = new HotkeyListener(() -> config.threePrayerToggle()) {
        @Override
        public void hotkeyPressed() {
            clientThread.invoke(() -> PrayerUtil.toggleMultiplePrayers(parsePrayers(config.threePrayer())));
        }
    };

    private final HotkeyListener spellOneListener = new HotkeyListener(() -> config.spellOneToggle()) {
        @Override
        public void hotkeyPressed() {
            if (target == null) {
                clientThread.invoke(() -> {
                    EthanApiPlugin.sendClientMessage("[PVP Helper] Focus a target before casting a spell.");
                });
            }

            castSpellOnTarget(config.spellOne());
        }
    };

    private final HotkeyListener spellTwoListener = new HotkeyListener(() -> config.spellTwoToggle()) {
        @Override
        public void hotkeyPressed() {
            if (target == null) {
                clientThread.invoke(() -> {
                    EthanApiPlugin.sendClientMessage("[PVP Helper] Focus a target before casting a spell.");
                });
            }

            castSpellOnTarget(config.spellTwo());
        }
    };

    private final HotkeyListener spellThreeListener = new HotkeyListener(() -> config.spellThreeToggle()) {
        @Override
        public void hotkeyPressed() {
            if (target == null) {
                clientThread.invoke(() -> {
                    EthanApiPlugin.sendClientMessage("[PVP Helper] Focus a target before casting a spell.");
                });
            }

            castSpellOnTarget(config.spellThree());
        }
    };

    private final HotkeyListener tripleEatListener = new HotkeyListener(() -> config.tripleEatToggle()) {
        @Override
        public void hotkeyPressed() {
            autoEat();
        }
    };

    private final HotkeyListener vengListener = new HotkeyListener(() -> config.veng()) {
        @Override
        public void hotkeyPressed() {
            clientThread.invoke(() -> {
                Widget veng = SpellUtil.getSpellWidget(client, "Vengeance");
                if (veng != null) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetAction(veng, "Cast");
                }
            });
        }
    };


    private void autoEat() {
        String karamName = config.blightedKaram() ? "Blighted karambwan" : "Cooked karambwan";
        clientThread.invoke(() -> {
            InventoryUtil.nameContainsNoCase(config.foodName()).withAction("Eat").filter(item -> !item.getName().contains("Burnt") || !item.getName().contains("Raw")).first().ifPresent(item -> {
                InventoryInteraction.useItem(item, "Eat");
            });

            Inventory.search().nameContains("Saradomin brew").withAction("Drink").first().ifPresent(item -> {
                InventoryInteraction.useItem(item, "Drink");
            });

            Inventory.search().nameContains(karamName).withAction("Eat").first().ifPresent(item -> {
                InventoryInteraction.useItem(item, "Eat");
            });
        });
    }

    private void toggleGear(List<String> gearNames) {
        clientThread.invoke(() -> {
            if (client.getGameState() != GameState.LOGGED_IN) {
                return;
            }
            swapGear(gearNames);
        });
    }
}