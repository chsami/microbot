package net.runelite.client.plugins.microbot.mixology;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.TileObject;
import net.runelite.api.VarbitComposition;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "AutoMixology",
        description = "Mixology plugin",
        tags = {"herblore", "microbot", "mixology"},
        enabledByDefault = false
)
@Slf4j
public class MixologyPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private MixologyConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private MixologyOverlay overlay;
    @Inject
    private InventoryPotionOverlay potionOverlay;
    private final Map<AlchemyObject, HighlightedObject> highlightedObjects = new LinkedHashMap();
    private boolean inLab = false;
    private PotionType alembicPotionType;
    private PotionType agitatorPotionType;
    private PotionType retortPotionType;

    @Inject
    MixologyScript mixologyScript;

    public MixologyPlugin() {
    }

    public Map<AlchemyObject, HighlightedObject> highlightedObjects() {
        return this.highlightedObjects;
    }

    @Provides
    MixologyConfig provideConfig(ConfigManager configManager) {
        return (MixologyConfig) configManager.getConfig(MixologyConfig.class);
    }

    protected void startUp() {
        mixologyScript.run(config);
        this.overlayManager.add(this.overlay);
        this.overlayManager.add(this.potionOverlay);
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.clientThread.invokeLater(this::initialize);
        }

    }

    protected void shutDown() {
        mixologyScript.shutdown();
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
    public void onVarbitChanged(VarbitChanged event) {
        int varbitId = event.getVarbitId();
        int value = event.getValue();
        if (varbitId == 11315) {
            if (value == 0) {
                // this.unHighlightAllStations();
            } else {
                this.clientThread.invokeAtTickEnd(this::updatePotionOrders);
            }
        } else if (varbitId == 11342) {
            if (value == 0) {
                this.tryFulfillOrder(this.alembicPotionType, PotionModifier.CRYSTALISED);
                this.alembicPotionType = null;
            } else {
                this.alembicPotionType = PotionType.fromIdx(value - 1);
            }
        } else if (varbitId == 11340) {
            if (value == 0) {
                this.tryFulfillOrder(this.agitatorPotionType, PotionModifier.HOMOGENOUS);
                this.agitatorPotionType = null;
            } else {
                this.agitatorPotionType = PotionType.fromIdx(value - 1);
            }
        } else if (varbitId == 11341) {
            if (value == 0) {
                this.tryFulfillOrder(this.retortPotionType, PotionModifier.CONCENTRATED);
                this.retortPotionType = null;
            } else {
                this.retortPotionType = PotionType.fromIdx(value - 1);
            }
        } else if (varbitId == 11330) {
            if (value == 1) {
                mixologyScript.digweed = AlchemyObject.DIGWEED_NORTH_EAST;
            } else {
                mixologyScript.digweed = null;
            }
        } else if (varbitId == 11331) {
            if (value == 1) {
                mixologyScript.digweed = AlchemyObject.DIGWEED_SOUTH_EAST;
            } else {
                mixologyScript.digweed = null;
            }
        } else if (varbitId == 11332) {
            if (value == 1) {
                mixologyScript.digweed = AlchemyObject.DIGWEED_SOUTH_WEST;
            } else {
                mixologyScript.digweed = null;
            }
        } else if (varbitId == 11333) {
            if (value == 1) {
                mixologyScript.digweed = AlchemyObject.DIGWEED_NORTH_WEST;
            } else {
                mixologyScript.digweed = null;
            }
        } else if (varbitId == 11329) {
            if (mixologyScript.agitatorQuickActionTicks == 2) {
                mixologyScript.agitatorQuickActionTicks = 0;
            }

            if (mixologyScript.agitatorQuickActionTicks == 1) {
                mixologyScript.agitatorQuickActionTicks = 2;
            }
        } else if (varbitId == 11328) {
            if (mixologyScript.alembicQuickActionTicks == 1) {
                mixologyScript.alembicQuickActionTicks = 0;
            }
        }
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        int spotAnimId = event.getGraphicsObject().getId();
        if (spotAnimId == 2955 && this.alembicPotionType != null) {
            mixologyScript.alembicQuickActionTicks = 1;
        }

        if (spotAnimId == 2954 && this.agitatorPotionType != null) {
            mixologyScript.agitatorQuickActionTicks = 1;
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
            for (int i = 0; i < mixologyScript.potionOrders.size(); ++i) {
                PotionOrder order = mixologyScript.potionOrders.get(i);
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
        int parentWidth = baseWidget.getWidth();
        int dx = parentWidth / 3;
        int x = dx / 2;
        this.addResinText(baseWidget.createChild(-1, 4), x, 4416, PotionComponent.MOX);
        this.addResinText(baseWidget.createChild(-1, 4), x + dx, 4415, PotionComponent.AGA);
        this.addResinText(baseWidget.createChild(-1, 4), x + dx * 2, 4414, PotionComponent.LYE);
    }

    private void initialize() {
        Widget ordersLayer = this.client.getWidget(882, 0);
        if (ordersLayer != null && !ordersLayer.isSelfHidden()) {
            this.inLab = true;
            this.updatePotionOrders();
        }
    }

    private void updatePotionOrders() {
        System.out.println("Updating potion orders");
        mixologyScript.potionOrders = this.getPotionOrders();
        // Desired order: CRYSTALISED > CONCENTRATED > HOMOGENOUS

        mixologyScript.potionOrders.sort(Comparator.comparingInt(mixologyScript.customOrder::indexOf));

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
    }

    private void tryFulfillOrder(PotionType potionType, PotionModifier modifier) {
        for (PotionOrder order : mixologyScript.potionOrders) {
            if (order.potionType() == potionType && order.potionModifier() == modifier && !order.fulfilled()) {
                order.setFulfilled(true);
                break;
            }
        }

    }

    private List<PotionOrder> getPotionOrders() {
        ArrayList<PotionOrder> potionOrders = new ArrayList(3);

        for (int orderIdx = 0; orderIdx < 3; ++orderIdx) {
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
