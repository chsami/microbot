package net.runelite.client.plugins.hoseaplugins.strategyexample.tasks;

import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.WidgetInfoExtended;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import com.google.inject.Inject;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy.AbstractTask;
import net.runelite.client.plugins.hoseaplugins.strategyexample.StrategySmithConfig;
import net.runelite.client.plugins.hoseaplugins.strategyexample.StrategySmithPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;

@Slf4j
public class DoSmithing extends AbstractTask<StrategySmithPlugin, StrategySmithConfig> {
//    @Inject
//    protected Client client;
//    @Inject
//    protected ClientThread clientThread;

    public DoSmithing(StrategySmithPlugin plugin, StrategySmithConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        Widget smithingInterface = plugin.getClient().getWidget(WidgetInfoExtended.SMITHING_INVENTORY_ITEMS_CONTAINER.getPackedId());
        return smithingInterface != null && plugin.hasEnoughBars() && plugin.hasHammer();
    }

    @Override
    public void execute() {
        log.info("Smithing");
        MousePackets.queueClickPacket();
        //interactWidget not implemented yet
        WidgetPackets.queueWidgetAction(plugin.getClient().getWidget(config.item().getWidgetInfo().getPackedId()), "Smith", "Smith set");
        plugin.isSmithing = true;
        plugin.timeout = 5 * (27 / config.item().getBarsRequired());
    }
}