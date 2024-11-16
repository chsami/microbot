package net.runelite.client.plugins.microbot.mixology;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;
import java.util.List;

@PluginDescriptor(
        name = PluginDescriptor.Default + "AutoMixology plugin",
        description = "Mixology plugin",
        tags = {"herblore", "microbot", "mixology"},
        enabledByDefault = false
)
@Slf4j
public class MixologyPlugin extends Plugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(MixologyPlugin.class);
    private static final int PROC_MASTERING_MIXOLOGY_BUILD_POTION_ORDERS = 7063;
    private static final int PROC_MASTERING_MIXOLOGY_BUILD_REAGENTS = 7064;
    private static final int VARBIT_POTION_ORDER_1 = 11315;
    private static final int VARBIT_POTION_MODIFIER_1 = 11316;
    private static final int VARBIT_POTION_ORDER_2 = 11317;
    private static final int VARBIT_POTION_MODIFIER_2 = 11318;
    private static final int VARBIT_POTION_ORDER_3 = 11319;
    private static final int VARBIT_POTION_MODIFIER_3 = 11320;
    private static final int VARP_LYE_RESIN = 4414;
    private static final int VARP_AGA_RESIN = 4415;
    private static final int VARP_MOX_RESIN = 4416;
    private static final int VARBIT_ALEMBIC_PROGRESS = 11328;
    private static final int VARBIT_AGITATOR_PROGRESS = 11329;
    private static final int VARBIT_AGITATOR_QUICKACTION = 11337;
    private static final int VARBIT_ALEMBIC_QUICKACTION = 11338;
    private static final int VARBIT_MIXING_VESSEL_POTION = 11339;
    private static final int VARBIT_AGITATOR_POTION = 11340;
    private static final int VARBIT_RETORT_POTION = 11341;
    private static final int VARBIT_ALEMBIC_POTION = 11342;
    private static final int VARBIT_DIGWEED_NORTH_EAST = 11330;
    private static final int VARBIT_DIGWEED_SOUTH_EAST = 11331;
    private static final int VARBIT_DIGWEED_SOUTH_WEST = 11332;
    private static final int VARBIT_DIGWEED_NORTH_WEST = 11333;
    private static final int SPOT_ANIM_AGITATOR = 2954;
    private static final int SPOT_ANIM_ALEMBIC = 2955;
    private static final int COMPONENT_POTION_ORDERS_GROUP_ID = 882;
    private static final int COMPONENT_POTION_ORDERS = 57802754;
    @Inject
    private Client client;
    @Inject
    private MixologyConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Notifier notifier;
    @Inject
    private ClientThread clientThread;
    @Inject
    private MixologyOverlay overlay;
    @Inject
    private InventoryPotionOverlay potionOverlay;
    private final Map<AlchemyObject, HighlightedObject> highlightedObjects = new LinkedHashMap();
    private java.util.List<PotionOrder> potionOrders = Collections.emptyList();
    private boolean inLab = false;
    private PotionType alembicPotionType;
    private PotionType agitatorPotionType;
    private PotionType retortPotionType;
    private int previousAgitatorProgess;
    private int previousAlembicProgress;
    private int agitatorQuickActionTicks = 0;
    private int alembicQuickActionTicks = 0;

    public MixologyPlugin() {
    }

    public Map<AlchemyObject, HighlightedObject> highlightedObjects() {
        return this.highlightedObjects;
    }

    public boolean isInLab() {
        return this.inLab;
    }

    @Provides
    MixologyConfig provideConfig(ConfigManager configManager) {
        return (MixologyConfig)configManager.getConfig(MixologyConfig.class);
    }

    protected void startUp() {
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.potionOverlay);
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.clientThread.invokeLater(this::initialize);
        }

    }

    protected void shutDown() {
        this.overlayManager.remove(this.overlay);
        this.overlayManager.remove(this.potionOverlay);
        this.inLab = false;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING) {
            this.highlightedObjects.clear();
        }

    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() == 882) {
            this.initialize();
        }
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed event) {
        if (event.getGroupId() == 882) {
            this.highlightedObjects.clear();
            this.inLab = false;
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("masteringmixology")) {
            if (event.getKey().equals("potionOrderSorting")) {
                this.clientThread.invokeLater(this::updatePotionOrders);
            }

            if (!this.config.highlightStations()) {
                this.unHighlightAllStations();
            }

            if (!this.config.highlightDigWeed()) {
                this.unHighlightObject(AlchemyObject.DIGWEED_NORTH_EAST);
                this.unHighlightObject(AlchemyObject.DIGWEED_SOUTH_EAST);
                this.unHighlightObject(AlchemyObject.DIGWEED_SOUTH_WEST);
                this.unHighlightObject(AlchemyObject.DIGWEED_NORTH_WEST);
            }

            if (this.config.highlightLevers()) {
                this.highlightLevers();
            } else {
                this.unHighlightLevers();
            }

        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (this.inLab && this.config.highlightStations() && event.getContainerId() == InventoryID.INVENTORY.getId()) {
            if (this.alembicPotionType == null && this.agitatorPotionType == null && this.retortPotionType == null) {
                ItemContainer inventory = event.getItemContainer();
                Item[] var3 = inventory.getItems();
                int var4 = var3.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Item item = var3[var5];
                    PotionType potionType = PotionType.fromItemId(item.getId());
                    if (potionType != null) {
                        Iterator var8 = this.potionOrders.iterator();

                        while(var8.hasNext()) {
                            PotionOrder order = (PotionOrder)var8.next();
                            if (order.potionType() == potionType && !order.fulfilled()) {
                                this.unHighlightAllStations();
                                this.highlightObject(order.potionModifier().alchemyObject(), this.config.stationHighlightColor());
                                return;
                            }
                        }
                    }
                }

            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        int varbitId = event.getVarbitId();
        int value = event.getValue();
        if (varbitId == 11315) {
            if (value == 0) {
                this.unHighlightAllStations();
            } else {
                this.clientThread.invokeAtTickEnd(this::updatePotionOrders);
            }
        } else if (varbitId == 11342) {
            if (value == 0) {
                this.unHighlightObject(AlchemyObject.ALEMBIC);
                this.tryFulfillOrder(this.alembicPotionType, PotionModifier.CRYSTALISED);
                this.tryHighlightNextStation();
                LOGGER.debug("Finished crystalising {}", this.alembicPotionType);
                this.alembicPotionType = null;
            } else {
                this.alembicPotionType = PotionType.fromIdx(value - 1);
                LOGGER.debug("Alembic potion type: {}", this.alembicPotionType);
            }
        } else if (varbitId == 11340) {
            if (value == 0) {
                this.unHighlightObject(AlchemyObject.AGITATOR);
                this.tryFulfillOrder(this.agitatorPotionType, PotionModifier.HOMOGENOUS);
                this.tryHighlightNextStation();
                LOGGER.debug("Finished homogenising {}", this.agitatorPotionType);
                this.agitatorPotionType = null;
            } else {
                this.agitatorPotionType = PotionType.fromIdx(value - 1);
                LOGGER.debug("Agitator potion type: {}", this.agitatorPotionType);
            }
        } else if (varbitId == 11341) {
            if (value == 0) {
                this.unHighlightObject(AlchemyObject.RETORT);
                this.tryFulfillOrder(this.retortPotionType, PotionModifier.CONCENTRATED);
                this.tryHighlightNextStation();
                LOGGER.debug("Finished concentrating {}", this.retortPotionType);
                this.retortPotionType = null;
            } else {
                this.retortPotionType = PotionType.fromIdx(value - 1);
                LOGGER.debug("Retort potion type: {}", this.retortPotionType);
            }
        } else if (varbitId == 11330) {
            if (value == 1) {
                if (this.config.highlightDigWeed()) {
                    this.highlightObject(AlchemyObject.DIGWEED_NORTH_EAST, this.config.digweedHighlightColor());
                }

                this.notifier.notify(this.config.notifyDigWeed(), "A digweed has spawned north east.");
            } else {
                this.unHighlightObject(AlchemyObject.DIGWEED_NORTH_EAST);
            }
        } else if (varbitId == 11331) {
            if (value == 1) {
                if (this.config.highlightDigWeed()) {
                    this.highlightObject(AlchemyObject.DIGWEED_SOUTH_EAST, this.config.digweedHighlightColor());
                }

                this.notifier.notify(this.config.notifyDigWeed(), "A digweed has spawned south east.");
            } else {
                this.unHighlightObject(AlchemyObject.DIGWEED_SOUTH_EAST);
            }
        } else if (varbitId == 11332) {
            if (value == 1) {
                if (this.config.highlightDigWeed()) {
                    this.highlightObject(AlchemyObject.DIGWEED_SOUTH_WEST, this.config.digweedHighlightColor());
                }

                this.notifier.notify(this.config.notifyDigWeed(), "A digweed has spawned south west.");
            } else {
                this.unHighlightObject(AlchemyObject.DIGWEED_SOUTH_WEST);
            }
        } else if (varbitId == 11333) {
            if (value == 1) {
                if (this.config.highlightDigWeed()) {
                    this.highlightObject(AlchemyObject.DIGWEED_NORTH_WEST, this.config.digweedHighlightColor());
                }

                this.notifier.notify(this.config.notifyDigWeed(), "A digweed has spawned north west.");
            } else {
                this.unHighlightObject(AlchemyObject.DIGWEED_NORTH_WEST);
            }
        } else if (varbitId == 11329) {
            if (this.agitatorQuickActionTicks == 2) {
                this.resetDefaultHighlight(AlchemyObject.AGITATOR);
                this.agitatorQuickActionTicks = 0;
            }

            if (this.agitatorQuickActionTicks == 1) {
                this.agitatorQuickActionTicks = 2;
            }

            if (value < this.previousAgitatorProgess) {
                this.resetDefaultHighlight(AlchemyObject.AGITATOR);
            }

            this.previousAgitatorProgess = value;
        } else if (varbitId == 11328) {
            if (this.alembicQuickActionTicks == 1) {
                this.resetDefaultHighlight(AlchemyObject.ALEMBIC);
                this.alembicQuickActionTicks = 0;
            }

            if (value < this.previousAlembicProgress) {
                this.resetDefaultHighlight(AlchemyObject.ALEMBIC);
            }

            this.previousAlembicProgress = value;
        } else if (varbitId == 11337) {
            this.resetDefaultHighlight(AlchemyObject.AGITATOR);
        } else if (varbitId == 11338) {
            this.resetDefaultHighlight(AlchemyObject.ALEMBIC);
        }

    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        int spotAnimId = event.getGraphicsObject().getId();
        if (this.config.highlightQuickActionEvents()) {
            if (spotAnimId == 2955 && this.alembicPotionType != null) {
                this.highlightObject(AlchemyObject.ALEMBIC, this.config.stationQuickActionHighlightColor());
                this.alembicQuickActionTicks = 1;
            }

            if (spotAnimId == 2954 && this.agitatorPotionType != null) {
                this.highlightObject(AlchemyObject.AGITATOR, this.config.stationQuickActionHighlightColor());
                this.agitatorQuickActionTicks = 1;
            }

        }
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        int scriptId = event.getScriptId();
        if (scriptId == 7063 || scriptId == 7064) {
            Widget baseWidget = this.client.getWidget(57802754);
            if (baseWidget != null) {
                if (scriptId == 7063) {
                    this.updatePotionOrdersComponent(baseWidget);
                } else {
                    this.appendResins(baseWidget);
                }

            }
        }
    }

    private void updatePotionOrdersComponent(Widget baseWidget) {
        Widget[] children = baseWidget.getChildren();
        if (children != null) {
            for(int i = 0; i < this.potionOrders.size(); ++i) {
                PotionOrder order = (PotionOrder)this.potionOrders.get(i);
                Widget orderGraphic = children[order.idx() * 2 + 1];
                Widget orderText = children[order.idx() * 2 + 2];
                if (orderGraphic.getType() == 5 && orderText.getType() == 4) {
                    StringBuilder builder = new StringBuilder(orderText.getText());
                    if (order.fulfilled()) {
                        builder.append(" (<col=00ff00>done!</col>)");
                    } else {
                        builder.append(" (").append(order.potionType().recipe()).append(")");
                    }

                    orderText.setText(builder.toString());
                    if (i != order.idx()) {
                        int y = 20 + i * 26 + 3;
                        orderGraphic.setOriginalY(y);
                        orderText.setOriginalY(y);
                        orderGraphic.revalidate();
                        orderText.revalidate();
                    }
                }
            }

        }
    }

    private void appendResins(Widget baseWidget) {
        if (this.config.displayResin()) {
            int parentWidth = baseWidget.getWidth();
            int dx = parentWidth / 3;
            int x = dx / 2;
            this.addResinText(baseWidget.createChild(-1, 4), x, 4416, PotionComponent.MOX);
            this.addResinText(baseWidget.createChild(-1, 4), x + dx, 4415, PotionComponent.AGA);
            this.addResinText(baseWidget.createChild(-1, 4), x + dx * 2, 4414, PotionComponent.LYE);
        }
    }

    private void initialize() {
        Widget ordersLayer = this.client.getWidget(882, 0);
        if (ordersLayer != null && !ordersLayer.isSelfHidden()) {
            LOGGER.debug("initialize plugin");
            this.inLab = true;
            this.updatePotionOrders();
            this.highlightLevers();
            this.tryHighlightNextStation();
        }
    }

    public void highlightObject(AlchemyObject alchemyObject, Color color) {
        WorldView worldView = this.client.getTopLevelWorldView();
        if (worldView != null) {
            LocalPoint localPoint = LocalPoint.fromWorld(worldView, alchemyObject.coordinate());
            if (localPoint != null) {
                Tile[][][] tiles = worldView.getScene().getTiles();
                Tile tile = tiles[worldView.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
                GameObject[] var7 = tile.getGameObjects();
                int var8 = var7.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    GameObject gameObject = var7[var9];
                    if (gameObject != null && gameObject.getId() == alchemyObject.objectId()) {
                        this.highlightedObjects.put(alchemyObject, new HighlightedObject(gameObject, color, this.config.highlightBorderWidth(), this.config.highlightFeather()));
                        return;
                    }
                }

                DecorativeObject decorativeObject = tile.getDecorativeObject();
                if (decorativeObject != null && decorativeObject.getId() == alchemyObject.objectId()) {
                    this.highlightedObjects.put(alchemyObject, new HighlightedObject(decorativeObject, color, this.config.highlightBorderWidth(), this.config.highlightFeather()));
                }

            }
        }
    }

    public void resetDefaultHighlight(AlchemyObject alchemyObject) {
        if (this.config.highlightStations()) {
            this.highlightObject(alchemyObject, this.config.stationHighlightColor());
        }

    }

    public void unHighlightObject(AlchemyObject alchemyObject) {
        this.highlightedObjects.remove(alchemyObject);
    }

    private void unHighlightAllStations() {
        this.unHighlightObject(AlchemyObject.RETORT);
        this.unHighlightObject(AlchemyObject.ALEMBIC);
        this.unHighlightObject(AlchemyObject.AGITATOR);
    }

    private void highlightLevers() {
        if (this.config.highlightLevers()) {
            this.highlightObject(AlchemyObject.LYE_LEVER, Color.decode("#" + PotionComponent.LYE.color()));
            this.highlightObject(AlchemyObject.AGA_LEVER, Color.decode("#" + PotionComponent.AGA.color()));
            this.highlightObject(AlchemyObject.MOX_LEVER, Color.decode("#" + PotionComponent.MOX.color()));
        }
    }

    private void unHighlightLevers() {
        this.unHighlightObject(AlchemyObject.LYE_LEVER);
        this.unHighlightObject(AlchemyObject.AGA_LEVER);
        this.unHighlightObject(AlchemyObject.MOX_LEVER);
    }

    private void updatePotionOrders() {
        LOGGER.debug("Updating potion orders");
        this.potionOrders = this.getPotionOrders();
        PotionOrderSorting potionOrderSorting = this.config.potionOrderSorting();
        if (potionOrderSorting != PotionOrderSorting.VANILLA) {
            LOGGER.debug("Orders pre-sort: {}", this.potionOrders);
            this.potionOrders.sort(potionOrderSorting.comparator());
            LOGGER.debug("Sorted orders: {}", this.potionOrders);
        }

        VarbitComposition varbitType = this.client.getVarbit(11315);
        if (varbitType != null) {
            this.client.queueChangedVarp(varbitType.getIndex());
        }

    }

    private void addResinText(Widget widget, int x, int varp, PotionComponent component) {
        int amount = this.client.getVarpValue(varp);
        int color = ColorUtil.fromHex(component.color()).getRGB();
        widget.setText("" + amount).setTextShadowed(true).setTextColor(color).setOriginalWidth(20).setOriginalHeight(15).setFontId(497).setOriginalY(0).setOriginalX(x).setYPositionMode(2).setXTextAlignment(1).setYTextAlignment(1);
        widget.revalidate();
        LOGGER.debug("adding resin text {} at {} with color {}", new Object[]{amount, x, color});
    }

    private void tryFulfillOrder(PotionType potionType, PotionModifier modifier) {
        Iterator var3 = this.potionOrders.iterator();

        while(var3.hasNext()) {
            PotionOrder order = (PotionOrder)var3.next();
            if (order.potionType() == potionType && order.potionModifier() == modifier && !order.fulfilled()) {
                LOGGER.debug("Order {} has been fulfilled", order);
                order.setFulfilled(true);
                break;
            }
        }

    }

    private void tryHighlightNextStation() {
        ItemContainer inventory = this.client.getItemContainer(InventoryID.INVENTORY);
        if (inventory != null) {
            Iterator var2 = this.potionOrders.iterator();

            while(var2.hasNext()) {
                PotionOrder order = (PotionOrder)var2.next();
                if (!order.fulfilled() && inventory.contains(order.potionType().itemId())) {
                    LOGGER.debug("Highlighting station for order {}", order);
                    this.highlightObject(order.potionModifier().alchemyObject(), this.config.stationHighlightColor());
                    break;
                }
            }

        }
    }

    private List<PotionOrder> getPotionOrders() {
        ArrayList<PotionOrder> potionOrders = new ArrayList(3);

        for(int orderIdx = 0; orderIdx < 3; ++orderIdx) {
            PotionType potionType = this.getPotionType(orderIdx);
            PotionModifier potionModifier = this.getPotionModifier(orderIdx);
            if (potionType != null && potionModifier != null) {
                potionOrders.add(new PotionOrder(orderIdx, potionType, potionModifier));
            }
        }

        return potionOrders;
    }

    private PotionType getPotionType(int orderIdx) {
        if (orderIdx == 0) {
            return PotionType.fromIdx(this.client.getVarbitValue(11315) - 1);
        } else if (orderIdx == 1) {
            return PotionType.fromIdx(this.client.getVarbitValue(11317) - 1);
        } else {
            return orderIdx == 2 ? PotionType.fromIdx(this.client.getVarbitValue(11319) - 1) : null;
        }
    }

    private PotionModifier getPotionModifier(int orderIdx) {
        if (orderIdx == 0) {
            return PotionModifier.from(this.client.getVarbitValue(11316) - 1);
        } else if (orderIdx == 1) {
            return PotionModifier.from(this.client.getVarbitValue(11318) - 1);
        } else {
            return orderIdx == 2 ? PotionModifier.from(this.client.getVarbitValue(11320) - 1) : null;
        }
    }

    public static class HighlightedObject {
        private final TileObject object;
        private final Color color;
        private final int outlineWidth;
        private final int feather;

        private HighlightedObject(TileObject object, Color color, int outlineWidth, int feather) {
            this.object = object;
            this.color = color;
            this.outlineWidth = outlineWidth;
            this.feather = feather;
        }

        public TileObject object() {
            return this.object;
        }

        public Color color() {
            return this.color;
        }

        public int outlineWidth() {
            return this.outlineWidth;
        }

        public int feather() {
            return this.feather;
        }
    }

}
