package net.runelite.client.plugins.hoseaplugins.lucidgauntlet;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.item.SlottedItem;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.entity.*;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.overlay.*;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.resource.ResourceManager;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=\"#32CD32\">Lucid </font>Gauntlet</html>",
        enabledByDefault = false,
        description = "Gauntlet Extended by xKylee updated with auto-features for Hunllef added in.",
        tags = {"gauntlet"}
)
@Singleton
public class LucidGauntletPlugin extends Plugin
{
    public static final int ONEHAND_SLASH_AXE_ANIMATION = 395;
    public static final int ONEHAND_CRUSH_PICKAXE_ANIMATION = 400;
    public static final int ONEHAND_CRUSH_AXE_ANIMATION = 401;
    public static final int UNARMED_PUNCH_ANIMATION = 422;
    public static final int UNARMED_KICK_ANIMATION = 423;
    public static final int BOW_ATTACK_ANIMATION = 426;
    public static final int ONEHAND_STAB_HALBERD_ANIMATION = 428;
    public static final int ONEHAND_SLASH_HALBERD_ANIMATION = 440;
    public static final int ONEHAND_SLASH_SWORD_ANIMATION = 390;
    public static final int ONEHAND_STAB_SWORD_ANIMATION = 386;
    public static final int HIGH_LEVEL_MAGIC_ATTACK = 1167;
    public static final int HUNLLEF_TORNADO = 8418;
    public static final int HUNLLEF_ATTACK_ANIM = 8419;
    public static final int HUNLLEF_STYLE_SWITCH_TO_MAGE = 8754;
    public static final int HUNLLEF_STYLE_SWITCH_TO_RANGE = 8755;

    public static final int[] MELEE_WEAPONS = {ItemID.CRYSTAL_HALBERD_PERFECTED, ItemID.CORRUPTED_HALBERD_PERFECTED, ItemID.CRYSTAL_HALBERD_ATTUNED, ItemID.CORRUPTED_HALBERD_ATTUNED, ItemID.CRYSTAL_HALBERD_BASIC, ItemID.CORRUPTED_HALBERD_BASIC};
    private static final int[] RANGE_WEAPONS = {ItemID.CRYSTAL_BOW_PERFECTED, ItemID.CORRUPTED_BOW_PERFECTED, ItemID.CRYSTAL_BOW_ATTUNED, ItemID.CORRUPTED_BOW_ATTUNED, ItemID.CRYSTAL_BOW_BASIC, ItemID.CORRUPTED_BOW_BASIC};
    private static final int[] MAGE_WEAPONS = {ItemID.CRYSTAL_STAFF_PERFECTED, ItemID.CORRUPTED_STAFF_PERFECTED, ItemID.CRYSTAL_STAFF_ATTUNED, ItemID.CORRUPTED_STAFF_ATTUNED, ItemID.CRYSTAL_STAFF_BASIC, ItemID.CORRUPTED_STAFF_BASIC};

    private static final Set<Integer> MELEE_ANIM_IDS = Set.of(
            ONEHAND_STAB_SWORD_ANIMATION, ONEHAND_SLASH_SWORD_ANIMATION,
            ONEHAND_SLASH_AXE_ANIMATION, ONEHAND_CRUSH_PICKAXE_ANIMATION,
            ONEHAND_CRUSH_AXE_ANIMATION, UNARMED_PUNCH_ANIMATION,
            UNARMED_KICK_ANIMATION, ONEHAND_STAB_HALBERD_ANIMATION,
            ONEHAND_SLASH_HALBERD_ANIMATION
    );

    private static final Set<Integer> ATTACK_ANIM_IDS = new HashSet<>();

    static
    {
        ATTACK_ANIM_IDS.addAll(MELEE_ANIM_IDS);
        ATTACK_ANIM_IDS.add(BOW_ATTACK_ANIMATION);
        ATTACK_ANIM_IDS.add(HIGH_LEVEL_MAGIC_ATTACK);
    }

    private static final Set<Integer> PROJECTILE_MAGIC_IDS = Set.of(
            ProjectileID.HUNLLEF_MAGE_ATTACK, ProjectileID.HUNLLEF_CORRUPTED_MAGE_ATTACK
    );

    private static final Set<Integer> PROJECTILE_RANGE_IDS = Set.of(
            ProjectileID.HUNLLEF_RANGE_ATTACK, ProjectileID.HUNLLEF_CORRUPTED_RANGE_ATTACK
    );

    private static final Set<Integer> PROJECTILE_PRAYER_IDS = Set.of(
            ProjectileID.HUNLLEF_PRAYER_ATTACK, ProjectileID.HUNLLEF_CORRUPTED_PRAYER_ATTACK
    );

    private static final Set<Integer> PROJECTILE_IDS = new HashSet<>();

    static
    {
        PROJECTILE_IDS.addAll(PROJECTILE_MAGIC_IDS);
        PROJECTILE_IDS.addAll(PROJECTILE_RANGE_IDS);
        PROJECTILE_IDS.addAll(PROJECTILE_PRAYER_IDS);
    }

    private static final Set<Integer> HUNLLEF_IDS = Set.of(
            NpcID.CRYSTALLINE_HUNLLEF, NpcID.CRYSTALLINE_HUNLLEF_9022,
            NpcID.CRYSTALLINE_HUNLLEF_9023, NpcID.CRYSTALLINE_HUNLLEF_9024,
            NpcID.CORRUPTED_HUNLLEF, NpcID.CORRUPTED_HUNLLEF_9036,
            NpcID.CORRUPTED_HUNLLEF_9037, NpcID.CORRUPTED_HUNLLEF_9038
    );

    private static final Set<Integer> TORNADO_IDS = Set.of(NullNpcID.NULL_9025, NullNpcID.NULL_9039);

    private static final Set<Integer> DEMIBOSS_IDS = Set.of(
            NpcID.CRYSTALLINE_BEAR, NpcID.CORRUPTED_BEAR,
            NpcID.CRYSTALLINE_DARK_BEAST, NpcID.CORRUPTED_DARK_BEAST,
            NpcID.CRYSTALLINE_DRAGON, NpcID.CORRUPTED_DRAGON
    );

    private static final Set<Integer> STRONG_NPC_IDS = Set.of(
            NpcID.CRYSTALLINE_SCORPION, NpcID.CORRUPTED_SCORPION,
            NpcID.CRYSTALLINE_UNICORN, NpcID.CORRUPTED_UNICORN,
            NpcID.CRYSTALLINE_WOLF, NpcID.CORRUPTED_WOLF
    );

    private static final Set<Integer> WEAK_NPC_IDS = Set.of(
            NpcID.CRYSTALLINE_BAT, NpcID.CORRUPTED_BAT,
            NpcID.CRYSTALLINE_RAT, NpcID.CORRUPTED_RAT,
            NpcID.CRYSTALLINE_SPIDER, NpcID.CORRUPTED_SPIDER
    );

    private static final Set<Integer> RESOURCE_IDS = Set.of(
            ObjectID.CRYSTAL_DEPOSIT, ObjectID.CORRUPT_DEPOSIT,
            ObjectID.PHREN_ROOTS, ObjectID.CORRUPT_PHREN_ROOTS,
            ObjectID.FISHING_SPOT_36068, ObjectID.CORRUPT_FISHING_SPOT,
            ObjectID.GRYM_ROOT, ObjectID.CORRUPT_GRYM_ROOT,
            ObjectID.LINUM_TIRINUM, ObjectID.CORRUPT_LINUM_TIRINUM
    );

    private static final Set<Integer> UTILITY_IDS = Set.of(
            ObjectID.SINGING_BOWL_35966, ObjectID.SINGING_BOWL_36063,
            ObjectID.RANGE_35980, ObjectID.RANGE_36077,
            ObjectID.WATER_PUMP_35981, ObjectID.WATER_PUMP_36078
    );

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidGauntletConfig config;

    @Inject
    private ResourceManager resourceManager;

    @Inject
    private SkillIconManager skillIconManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private OverlayTimer overlayTimer;

    @Inject
    private OverlayGauntlet overlayGauntlet;

    @Inject
    private OverlayHunllef overlayHunllef;

    @Inject
    private OverlayPrayerWidget overlayPrayerWidget;

    @Inject
    private OverlayPrayerBox overlayPrayerBox;

    private Set<Overlay> overlays;

    @Getter
    private final Set<Resource> resources = new HashSet<>();

    @Getter
    private final Set<GameObject> utilities = new HashSet<>();

    @Getter
    private final Set<Tornado> tornadoes = new HashSet<>();

    @Getter
    private final Set<Demiboss> demibosses = new HashSet<>();

    @Getter
    private final Set<NPC> strongNpcs = new HashSet<>();

    @Getter
    private final Set<NPC> weakNpcs = new HashSet<>();

    private final List<Set<?>> entitySets = Arrays.asList(resources, utilities, tornadoes, demibosses, strongNpcs, weakNpcs);

    @Getter
    private Missile missile;

    @Getter
    private Hunllef hunllef;

    @Getter
    @Setter
    private boolean wrongAttackStyle;

    @Getter
    @Setter
    private boolean switchWeapon;

    private boolean inGauntlet;
    private boolean inHunllef;

    private int lastSwitchTick = 0;

    private int removeWepTick = 0;

    private int lastAttackTick = 0;

    private int lastDodgeTick = -1;

    private WorldPoint lastSafeTile;

    private WorldPoint secondLastSafeTile;

    @Inject
    private GauntletInstanceGrid instanceGrid;

    @Provides

    LucidGauntletConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidGauntletConfig.class);
    }

    @Override
    protected void startUp()
    {
        if (overlays == null)
        {
            overlays = Set.of(overlayTimer, overlayGauntlet, overlayHunllef, overlayPrayerWidget, overlayPrayerBox);
        }

        if (client.getGameState() == GameState.LOGGED_IN)
        {
            clientThread.invoke(this::pluginEnabled);
        }

    }

    @Override
    protected void shutDown()
    {
        overlays.forEach(o -> overlayManager.remove(o));

        inGauntlet = false;
        inHunllef = false;

        hunllef = null;
        missile = null;
        wrongAttackStyle = false;
        switchWeapon = false;

        overlayTimer.reset();
        resourceManager.reset();
        instanceGrid.reset();
        entitySets.forEach(Set::clear);
    }

    @Subscribe
    private void onConfigChanged(final ConfigChanged event)
    {
        if (!event.getGroup().equals("lucid-gauntlet"))
        {
            return;
        }

        switch (event.getKey())
        {
            case "resourceIconSize":
                if (!resources.isEmpty())
                {
                    resources.forEach(r -> r.setIconSize(config.resourceIconSize()));
                }
                break;
            case "resourceTracker":
                if (inGauntlet && !inHunllef)
                {
                    resourceManager.reset();
                    resourceManager.init();
                }
                break;
            case "projectileIconSize":
                if (missile != null)
                {
                    missile.setIconSize(config.projectileIconSize());
                }
                break;
            case "hunllefAttackStyleIconSize":
                if (hunllef != null)
                {
                    hunllef.setIconSize(config.hunllefAttackStyleIconSize());
                }
                break;
            case "mirrorMode":
                overlays.forEach(overlay -> {
                    overlay.determineLayer();
                    if (overlayManager.anyMatch(o -> o == overlay))
                    {
                        overlayManager.remove(overlay);
                        overlayManager.add(overlay);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Subscribe
    private void onVarbitChanged(final VarbitChanged event)
    {
        if (isHunllefVarbitSet())
        {
            if (!inHunllef)
            {
                initHunllef();
            }
        }
        else if (isGauntletVarbitSet())
        {
            if (!inGauntlet)
            {
                initGauntlet();
            }
        }
        else
        {
            if (inGauntlet || inHunllef)
            {
                shutDown();
            }
        }
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        NPC hun = NpcUtils.getNearestNpc(npc -> npc.getName() != null && npc.getName().contains("Hunllef"));
        if (hun != null && !instanceGrid.isInitialized())
        {
            instanceGrid.initialize();
        }

        if (hunllef == null)
        {
            return;
        }

        hunllef.decrementTicksUntilNextAttack();

        if (missile != null && missile.getProjectile().getRemainingCycles() <= 0)
        {
            missile = null;
        }

        if (!tornadoes.isEmpty())
        {
            tornadoes.forEach(Tornado::updateTimeLeft);
        }

        if (client.getTickCount() - lastAttackTick == 1)
        {
            if (hunllef.getPlayerAttackCount() == 6 && config.weaponSwitchMode() == LucidGauntletConfig.WeaponSwitchStyle.NORMAL)
            {
                swapWeaponNormal();
            }

            if ((hunllef.getPlayerAttackCount() == 6 || hunllef.getPlayerAttackCount() == 1) && config.weaponSwitchMode() == LucidGauntletConfig.WeaponSwitchStyle.RANGED_5_1 || config.weaponSwitchMode() == LucidGauntletConfig.WeaponSwitchStyle.MAGE_5_1)
            {
                swapWeapon51(hunllef.getPlayerAttackCount(), EthanApiPlugin.getHeadIcon(hunllef.getNpc()));
            }
        }

        if (client.getTickCount() == removeWepTick)
        {
            EquipmentUtils.removeWepSlotItem();
        }

        boolean attacked = false;

        if (client.getTickCount() - lastSwitchTick == 1)
        {
            final Item wep = EquipmentUtils.getWepSlotItem();

            if (wep != null)
            {
                ItemComposition composition = client.getItemDefinition(wep.getId());
                if (composition.getName() != null && (composition.getName().contains("bow") || composition.getName().contains("staff")))
                {
                    if (config.autoAttack())
                    {
                        attackHunllef();
                        attacked = true;
                    }
                }
                else
                {
                    if (config.autoAttackMelee())
                    {
                        attackHunllef();
                        attacked = true;
                    }
                }
            }
            else
            {
                if (config.autoAttackMelee())
                {
                    attackHunllef();
                    attacked = true;
                }
            }
        }

        if (!inHunllef)
        {
            return;
        }

        if (config.autoDodge())
        {
            WorldPoint safeTile = getToSafeTile();
        }
    }

    private WorldPoint getToSafeTile()
    {
        WorldPoint safeTile = getClosestSafeTile();

        if (tileUnderUsUnsafe(false))
        {
            if (ticksSinceLastDodge() > 1 && safeTile != null && !safeTile.equals(client.getLocalPlayer().getWorldLocation()))
            {
                InteractionUtils.walk(safeTile);
                secondLastSafeTile = lastSafeTile;
                lastSafeTile = safeTile;
                lastDodgeTick = client.getTickCount();
                return safeTile;
            }
        }
        else if (tileUnderUsUnsafe(true))
        {
            if (ticksSinceLastDodge() > 1 || tooCloseToTornado(client.getLocalPlayer().getWorldLocation(), 3))
            {
                if (safeTile != null && !safeTile.equals(client.getLocalPlayer().getWorldLocation()))
                {
                    InteractionUtils.walk(safeTile);
                    secondLastSafeTile = lastSafeTile;
                    lastSafeTile = safeTile;
                    lastDodgeTick = client.getTickCount();
                    return safeTile;
                }
            }
        }
        return null;
    }

    private boolean tileUnderUsUnsafe(boolean checkTornados)
    {
        Predicate<TileObject> unsafeTileFilter = (groundObject) -> (groundObject.getId() == 36150 || groundObject.getId() == 36151 ||
                groundObject.getId() == 36047 || groundObject.getId() == 36048) &&
                groundObject.getLocalLocation().equals(client.getLocalPlayer().getLocalLocation());

        boolean isTileSafe = GameObjectUtils.nearest(unsafeTileFilter) == null;

        boolean underHunllef = hunllef.getNpc().getWorldArea().contains(client.getLocalPlayer().getWorldLocation());

        boolean tooCloseToTornados = tooCloseToTornado(client.getLocalPlayer().getWorldLocation(), 3);

        /*if (!isTileSafe)
        {
            MessageUtils.addMessage(client, "Need to move from unsafe tile!");
        }

        if (underHunllef)
        {
            MessageUtils.addMessage(client, "Need to get out from under the beast!");
        }

        if (tooCloseToTornados)
        {
            MessageUtils.addMessage(client,"There's a tornado about to fuck us up");
        }*/

        return !isTileSafe || underHunllef || (checkTornados && tooCloseToTornados);
    }

    public WorldArea fromSwToNe(WorldPoint swLocation, WorldPoint neLocation)
    {
        return new WorldArea(swLocation.getX(), swLocation.getY(), neLocation.getX() - swLocation.getX() + 1, neLocation.getY() - swLocation.getY() + 1, swLocation.getPlane());
    }

    private boolean tooCloseToTornado(WorldPoint point, int range)
    {
        if (tornadoes.size() == 0)
        {
            return false;
        }

        for (Tornado t : tornadoes)
        {
            NPC tornado = t.getNpc();
            if (tornado.getWorldLocation().distanceTo2D(point) < range)
            {
                return true;
            }
        }
        return false;
    }

    private WorldPoint getClosestSafeTile()
    {
        //WorldArea biggerArea = fromSwToNe(hunllef.getNpc().getWorldLocation().dx(-2).dy(-2), hunllef.getNpc().getWorldLocation().dx(6).dy(6));
        if (lastSafeTile == null)
        {
            lastSafeTile = client.getLocalPlayer().getWorldLocation();
        }

        if (secondLastSafeTile == null)
        {
            secondLastSafeTile = client.getLocalPlayer().getWorldLocation();
        }

        WorldArea biggerArea = hunllef.getNpc().getWorldArea();
        Predicate<TileObject> filter1 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !biggerArea.contains(groundObject.getWorldLocation()) &&
                !tooCloseToTornado(groundObject.getWorldLocation(), 5) &&
                groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1 &&
                (groundObject.getWorldLocation().getX() == client.getLocalPlayer().getWorldLocation().getX() || groundObject.getWorldLocation().getY() == client.getLocalPlayer().getWorldLocation().getY()) &&
                isInsideArena(groundObject.getWorldLocation()) &&
                (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1 || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1) &&
                groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5;

        Predicate<TileObject> filter2 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !biggerArea.contains(groundObject.getWorldLocation()) &&
                !tooCloseToTornado(groundObject.getWorldLocation(), 4) &&
                groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1 &&
                (groundObject.getWorldLocation().getX() == client.getLocalPlayer().getWorldLocation().getX() || groundObject.getWorldLocation().getY() == client.getLocalPlayer().getWorldLocation().getY()) &&
                isInsideArena(groundObject.getWorldLocation()) &&
                (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1 || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1) &&
                groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5;


        Predicate<TileObject> filter3 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !biggerArea.contains(groundObject.getWorldLocation()) &&
                !tooCloseToTornado(groundObject.getWorldLocation(), 3) &&
                groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1 &&
                (groundObject.getWorldLocation().getX() == client.getLocalPlayer().getWorldLocation().getX() || groundObject.getWorldLocation().getY() == client.getLocalPlayer().getWorldLocation().getY()) &&
                isInsideArena(groundObject.getWorldLocation()) &&
                (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1 || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1) &&
                groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5;

        Predicate<TileObject> filter4 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !biggerArea.contains(groundObject.getWorldLocation())
                && !tooCloseToTornado(groundObject.getWorldLocation(), 2)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                && (groundObject.getWorldLocation().getX() == client.getLocalPlayer().getWorldLocation().getX() || groundObject.getWorldLocation().getY() == client.getLocalPlayer().getWorldLocation().getY())
                && isInsideArena(groundObject.getWorldLocation())
                && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5;


        Predicate<TileObject> filter5 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !biggerArea.contains(groundObject.getWorldLocation())
                && !tooCloseToTornado(groundObject.getWorldLocation(), 5)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                && isInsideArena(groundObject.getWorldLocation())
                && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5;

        Predicate<TileObject> filter6 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !biggerArea.contains(groundObject.getWorldLocation())
                && !tooCloseToTornado(groundObject.getWorldLocation(), 4)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                && isInsideArena(groundObject.getWorldLocation())
                && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5;

        Predicate<TileObject> filter7 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !biggerArea.contains(groundObject.getWorldLocation())
                && !tooCloseToTornado(groundObject.getWorldLocation(), 3)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                && isInsideArena(groundObject.getWorldLocation())
                && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5;

        Predicate<TileObject> filter8 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !biggerArea.contains(groundObject.getWorldLocation())
                && !tooCloseToTornado(groundObject.getWorldLocation(), 2)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                && isInsideArena(groundObject.getWorldLocation())
                && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) < 5;

        Predicate<TileObject> filter9 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !hunllef.getNpc().getWorldArea().contains(groundObject.getWorldLocation())
                && !tooCloseToTornado(groundObject.getWorldLocation(), 5)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                && isInsideArena(groundObject.getWorldLocation())
                && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1);

        Predicate<TileObject> filter10 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !hunllef.getNpc().getWorldArea().contains(groundObject.getWorldLocation())
                && !tooCloseToTornado(groundObject.getWorldLocation(), 4)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                && isInsideArena(groundObject.getWorldLocation())
                && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1);

        Predicate<TileObject> filter11 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !hunllef.getNpc().getWorldArea().contains(groundObject.getWorldLocation())
                && !tooCloseToTornado(groundObject.getWorldLocation(), 3)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                && isInsideArena(groundObject.getWorldLocation())
                && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1);

        Predicate<TileObject> filter12 = groundObject ->
                (groundObject.getId() == 36149 || groundObject.getId() == 36046) &&
                !hunllef.getNpc().getWorldArea().contains(groundObject.getWorldLocation())
                && !tooCloseToTornado(groundObject.getWorldLocation(), 2)
                && groundObject.getWorldLocation().distanceTo2D(client.getLocalPlayer().getWorldLocation()) >= 1
                && isInsideArena(groundObject.getWorldLocation())
                && (lastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1
                || secondLastSafeTile.distanceTo2D(groundObject.getWorldLocation()) > 1);

        List<Predicate<TileObject>> filters = List.of(filter1, filter2, filter3, filter4, filter5, filter6, filter7, filter8, filter9, filter10, filter11, filter12);

        for (Predicate<TileObject> filter : filters)
        {
            TileObject nearest = GameObjectUtils.nearest(filter);

            if (nearest != null)
            {
                return nearest.getWorldLocation();
            }
        }

        return null;
    }

    private int ticksSinceLastDodge()
    {
        return client.getTickCount() - lastDodgeTick;
    }

    private boolean isInsideArena(WorldPoint point)
    {
        final int hunllefBaseX = instanceGrid.getRoom(3, 3).getBaseX();
        final int hunllefBaseY = instanceGrid.getRoom(3, 3).getBaseY();
        final WorldPoint arenaSouthWest = new WorldPoint(hunllefBaseX + 2, hunllefBaseY - 13, client.getLocalPlayer().getWorldLocation().getPlane());
        final WorldPoint arenaNorthEast = new WorldPoint(hunllefBaseX + 13, hunllefBaseY - 2, client.getLocalPlayer().getWorldLocation().getPlane());

        return fromSwToNe(arenaSouthWest, arenaNorthEast).contains(point);
    }

    @Subscribe
    private void onGameStateChanged(final GameStateChanged event)
    {
        switch (event.getGameState())
        {
            case LOADING:
                resources.clear();
                utilities.clear();
                break;
            case LOGIN_SCREEN:
            case HOPPING:
                shutDown();
                break;
        }
    }

    @Subscribe
    private void onWidgetLoaded(final WidgetLoaded event)
    {
        if (event.getGroupId() == WidgetID.GAUNTLET_TIMER_GROUP_ID)
        {
            overlayTimer.setGauntletStart();
            resourceManager.init();
        }
    }

    @Subscribe
    private void onGameObjectSpawned(final GameObjectSpawned event)
    {
        final GameObject gameObject = event.getGameObject();

        final int id = gameObject.getId();

        if (RESOURCE_IDS.contains(id))
        {
            resources.add(new Resource(gameObject, skillIconManager, config.resourceIconSize()));
        }
        else if (UTILITY_IDS.contains(id))
        {
            utilities.add(gameObject);
        }
    }

    @Subscribe
    private void onGameObjectDespawned(final GameObjectDespawned event)
    {
        final GameObject gameObject = event.getGameObject();

        final int id = gameObject.getId();

        if (RESOURCE_IDS.contains(gameObject.getId()))
        {
            resources.removeIf(o -> o.getGameObject() == gameObject);
        }
        else if (UTILITY_IDS.contains(id))
        {
            utilities.remove(gameObject);
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        final NPC npc = event.getNpc();

        final int id = npc.getId();

        if (HUNLLEF_IDS.contains(id))
        {
            hunllef = new Hunllef(npc, skillIconManager, config.hunllefAttackStyleIconSize());
        }
        else if (TORNADO_IDS.contains(id))
        {
            tornadoes.add(new Tornado(npc));
        }
        else if (DEMIBOSS_IDS.contains(id))
        {
            demibosses.add(new Demiboss(npc));
        }
        else if (STRONG_NPC_IDS.contains(id))
        {
            strongNpcs.add(npc);
        }
        else if (WEAK_NPC_IDS.contains(id))
        {
            weakNpcs.add(npc);
        }
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event)
    {
        if (!inHunllef)
        {
            return;
        }
        if (event.getContainerId() == InventoryID.EQUIPMENT.getId())
        {
            if (config.autoOffense())
            {
                CombatUtils.togglePrayer(getPrayerBasedOnWeapon());
            }
        }
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        final NPC npc = event.getNpc();

        final int id = npc.getId();

        if (HUNLLEF_IDS.contains(id))
        {
            hunllef = null;
        }
        else if (TORNADO_IDS.contains(id))
        {
            tornadoes.removeIf(t -> t.getNpc() == npc);
        }
        else if (DEMIBOSS_IDS.contains(id))
        {
            demibosses.removeIf(d -> d.getNpc() == npc);
        }
        else if (STRONG_NPC_IDS.contains(id))
        {
            strongNpcs.remove(npc);
        }
        else if (WEAK_NPC_IDS.contains(id))
        {
            weakNpcs.remove(npc);
        }
    }

    @Subscribe
    private void onProjectileMoved(final ProjectileMoved event)
    {
        if (hunllef == null)
        {
            return;
        }

        final Projectile projectile = event.getProjectile();

        if (projectile.getRemainingCycles() != (projectile.getEndCycle() - projectile.getStartCycle()))
        {
            return;
        }

        final int id = projectile.getId();

        if (!PROJECTILE_IDS.contains(id))
        {
            return;
        }

        missile = new Missile(projectile, skillIconManager, config.projectileIconSize());

        if (PROJECTILE_PRAYER_IDS.contains(id) && config.hunllefPrayerAudio())
        {
            client.playSoundEffect(SoundEffectID.MAGIC_SPLASH_BOING);
        }
    }

    @Subscribe
    private void onChatMessage(final ChatMessage event)
    {
        final ChatMessageType type = event.getType();

        if (type == ChatMessageType.SPAM || type == ChatMessageType.GAMEMESSAGE)
        {
            resourceManager.parseChatMessage(event.getMessage());
        }

        if (event.getMessage().contains("prayers have been disabled"))
        {
            if (config.autoPrayer())
            {
                CombatUtils.togglePrayer(hunllef.getAttackPhase().getPrayer());
            }
            if (config.autoOffense())
            {
                CombatUtils.togglePrayer(getPrayerBasedOnWeapon());
            }
        }
    }

    @Subscribe
    private void onActorDeath(final ActorDeath event)
    {
        if (event.getActor() != client.getLocalPlayer())
        {
            return;
        }

        overlayTimer.onPlayerDeath();
    }

    @Subscribe
    private void onAnimationChanged(final AnimationChanged event)
    {
        if (!isHunllefVarbitSet() || hunllef == null)
        {
            return;
        }

        final Actor actor = event.getActor();

        if (actor == null)
        {
            return;
        }

        final int animationId = actor.getAnimation();

        if (actor instanceof Player)
        {
            if (!ATTACK_ANIM_IDS.contains(animationId))
            {
                return;
            }

            final boolean validAttack = isAttackAnimationValid(animationId);

            if (validAttack)
            {

                wrongAttackStyle = false;

                hunllef.updatePlayerAttackCount();

                if (hunllef.getPlayerAttackCount() == 1)
                {
                    switchWeapon = true;
                }

                lastAttackTick = client.getTickCount();
            }
            else
            {
                wrongAttackStyle = true;
            }
        }
        else if (actor instanceof NPC)
        {
            if (animationId == HUNLLEF_ATTACK_ANIM || animationId == HUNLLEF_TORNADO)
            {
                hunllef.updateAttackCount();
            }

            if (animationId == HUNLLEF_STYLE_SWITCH_TO_MAGE || animationId == HUNLLEF_STYLE_SWITCH_TO_RANGE)
            {
                hunllef.toggleAttackHunllefAttackStyle();

                if (config.autoPrayer())
                {
                    CombatUtils.togglePrayer(hunllef.getAttackPhase().getPrayer());
                }
            }
        }
    }

    private boolean isAttackAnimationValid(final int animationId)
    {
        final HeadIcon headIcon = EthanApiPlugin.getHeadIcon(hunllef.getNpc());

        if (headIcon == null)
        {
            return true;
        }

        switch (headIcon)
        {
            case MELEE:
                if (MELEE_ANIM_IDS.contains(animationId))
                {
                    return false;
                }
                break;
            case RANGED:
                if (animationId == BOW_ATTACK_ANIMATION)
                {
                    return false;
                }
                break;
            case MAGIC:
                if (animationId == HIGH_LEVEL_MAGIC_ATTACK)
                {
                    return false;
                }
                break;
        }

        return true;
    }

    private void pluginEnabled()
    {
        if (isGauntletVarbitSet())
        {
            overlayTimer.setGauntletStart();
            resourceManager.init();
            addSpawnedEntities();
            initGauntlet();
        }

        if (isHunllefVarbitSet())
        {
            initHunllef();
        }
    }

    private void addSpawnedEntities()
    {
        for (final GameObject gameObject : new GameObjectQuery().getGameObjectQuery(client))
        {
            GameObjectSpawned gameObjectSpawned = new GameObjectSpawned();
            gameObjectSpawned.setTile(null);
            gameObjectSpawned.setGameObject(gameObject);
            onGameObjectSpawned(gameObjectSpawned);
        }

        for (final NPC npc : client.getTopLevelWorldView().npcs())
        {
            onNpcSpawned(new NpcSpawned(npc));
        }
    }

    private void initGauntlet()
    {
        inGauntlet = true;

        overlayManager.add(overlayTimer);
        overlayManager.add(overlayGauntlet);
    }

    private void initHunllef()
    {
        inHunllef = true;

        overlayTimer.setHunllefStart();
        resourceManager.reset();
        overlayManager.remove(overlayGauntlet);
        overlayManager.add(overlayHunllef);
        overlayManager.add(overlayPrayerWidget);
        overlayManager.add(overlayPrayerBox);
    }

    private boolean isGauntletVarbitSet()
    {
        return client.getVarbitValue(9178) == 1;
    }

    private boolean isHunllefVarbitSet()
    {
        return client.getVarbitValue(9177) == 1;
    }

    private Prayer getPrayerBasedOnWeapon()
    {
        if (EquipmentUtils.contains(RANGE_WEAPONS))
        {
            return config.offenseRangePrayer().getPrayer();
        }

        if (EquipmentUtils.contains(MAGE_WEAPONS))
        {
            return config.offenseMagicPrayer().getPrayer();
        }
        if (EquipmentUtils.contains(MELEE_WEAPONS))
        {
            return config.offenseMeleePrayer().getPrayer();
        }
        return config.offenseMeleePrayer().getPrayer();
    }


    private void swapWeaponNormal()
    {
        if (InventoryUtils.contains(RANGE_WEAPONS))
        {
            SlottedItem wep = InventoryUtils.getFirstItemSlotted(RANGE_WEAPONS);

            if (wep != null)
            {
                InventoryUtils.wieldItem(wep.getItem().getId());
            }

            lastSwitchTick = client.getTickCount();
        }
        else if (InventoryUtils.contains(MAGE_WEAPONS))
        {
            SlottedItem wep = InventoryUtils.getFirstItemSlotted(MAGE_WEAPONS);
            if (wep != null)
            {
                InventoryUtils.wieldItem(wep.getItem().getId());
            }

            lastSwitchTick = client.getTickCount();
        }
        else if (InventoryUtils.contains(MELEE_WEAPONS))
        {
            SlottedItem wep = InventoryUtils.getFirstItemSlotted(MELEE_WEAPONS);
            if (wep != null)
            {
                InventoryUtils.wieldItem(wep.getItem().getId());
            }

            lastSwitchTick = client.getTickCount();
        }
        else
        {
            EquipmentUtils.removeWepSlotItem();

            lastSwitchTick = client.getTickCount();
        }
    }

    private void swapWeapon51(int attackCount, HeadIcon current)
    {
        if (attackCount == 1)
        {
            if (current == HeadIcon.RANGED || current == HeadIcon.MAGIC)
            {
                if (InventoryUtils.contains(MELEE_WEAPONS))
                {
                    SlottedItem wep = InventoryUtils.getFirstItemSlotted(MELEE_WEAPONS);
                    if (wep != null)
                    {
                        InventoryUtils.wieldItem(wep.getItem().getId());
                    }
                }
                else
                {
                    EquipmentUtils.removeWepSlotItem();
                }
                lastSwitchTick = client.getTickCount();
            }

            if (current == HeadIcon.MELEE)
            {
                if (config.weaponSwitchMode() == LucidGauntletConfig.WeaponSwitchStyle.MAGE_5_1)
                {
                    if (InventoryUtils.contains(RANGE_WEAPONS))
                    {
                        SlottedItem wep = InventoryUtils.getFirstItemSlotted(RANGE_WEAPONS);
                        if (wep != null)
                        {
                            InventoryUtils.wieldItem(wep.getItem().getId());
                        }

                        lastSwitchTick = client.getTickCount();
                    }
                }
                else
                {
                    if (InventoryUtils.contains(MAGE_WEAPONS))
                    {
                        SlottedItem wep = InventoryUtils.getFirstItemSlotted(MAGE_WEAPONS);
                        if (wep != null)
                        {
                            InventoryUtils.wieldItem(wep.getItem().getId());
                        }

                        lastSwitchTick = client.getTickCount();
                    }
                }
            }
        }

        if (attackCount == 6)
        {
            if (config.weaponSwitchMode() == LucidGauntletConfig.WeaponSwitchStyle.MAGE_5_1)
            {
                if (InventoryUtils.contains(MAGE_WEAPONS))
                {
                    SlottedItem wep = InventoryUtils.getFirstItemSlotted(MAGE_WEAPONS);
                    if (wep != null)
                    {
                        InventoryUtils.wieldItem(wep.getItem().getId());
                    }

                    lastSwitchTick = client.getTickCount();
                }

            }
            else
            {
                if (InventoryUtils.contains(RANGE_WEAPONS))
                {
                    SlottedItem wep = InventoryUtils.getFirstItemSlotted(RANGE_WEAPONS);
                    if (wep != null)
                    {
                        InventoryUtils.wieldItem(wep.getItem().getId());
                    }

                    lastSwitchTick = client.getTickCount();
                }

            }
        }
    }

    private void attackHunllef()
    {
        final NPC hunllef = NpcUtils.getNearestNpc(npc -> npc.getName() != null && npc.getName().contains("Hunllef"));
        if (hunllef == null)
        {
            return;
        }

        NpcUtils.attackNpc(hunllef);
    }

}