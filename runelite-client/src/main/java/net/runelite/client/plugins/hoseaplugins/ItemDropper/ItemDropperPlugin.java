package net.runelite.client.plugins.hoseaplugins.ItemDropper;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InventoryInteraction;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@PluginDescriptor(
		name = "<html><font color=\"#FF9DF9\">[PP]</font> Item Dropper</html>",
		description = "Automatically drops items on hotkey pressed or if the inventory is full",
		tags = {"ethan", "piggy"}
)
public class ItemDropperPlugin extends Plugin {
	@Inject private ItemDropperConfig config;
	@Inject private KeyManager keyManager;
	@Inject private ClientThread clientThread;
	private final List<Integer> itemIds = new ArrayList<>();
	private final List<String> itemNames = new ArrayList<>();
	private final ConcurrentLinkedQueue<Widget> itemsToDrop = new ConcurrentLinkedQueue<>();
	private boolean dropping = false;

	@Provides
	private ItemDropperConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(ItemDropperConfig.class);
	}

	@Override
	protected void startUp() {
		keyManager.registerKeyListener(dropItemsHotkey);
		itemsToDrop.clear();
		itemNames.clear();
		dropping = false;
		updateItemIds();
	}

	@Override
	protected void shutDown() {
		dropping = false;
		keyManager.unregisterKeyListener(dropItemsHotkey);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged configChanged) {
		if (!configChanged.getGroup().equals(config.GROUP)) return;
		if (!configChanged.getKey().equals("itemIds")) return;
		updateItemIds();
	}

	@Subscribe
	private void onGameTick(GameTick gameTick) {
		if (!dropping && config.dropIfInvFull() && Inventory.full()) {
			buildItemQueue();
			dropping = true;
		}

		if (dropping) {
			dropItems();
		}
	}

	private final HotkeyListener dropItemsHotkey = new HotkeyListener(() -> config.getHotkey()) {
		@Override
		public void hotkeyPressed() {
			if (dropping) return;
			clientThread.invoke(() -> {
				buildItemQueue();
				dropping = true;
			});
		}
	};

	private void buildItemQueue() {
		itemsToDrop.clear();
		itemsToDrop.addAll(Inventory.search().idInList(itemIds).result());
		for (String name : itemNames) {
			itemsToDrop.addAll(Inventory.search().matchesWildCardNoCase(name).filter(w -> !itemsToDrop.contains(w)).result());
		}
	}

	private void updateItemIds() {
		if (config.itemIdsOrNames().trim().isEmpty()) {
			itemIds.clear();
			itemNames.clear();
			return;
		}
		var parts = config.itemIdsOrNames().trim().split(", |,");
		for (String part : parts) {
			try {
				int id = Integer.parseInt(part.trim());
				itemIds.add(id);
			}
			catch (NumberFormatException ignored) {
				itemNames.add(part.trim());
			}
		}
	}

	private void dropItems() {
		int numOfItems = getRandomIntBetweenRange(2, config.maxPerTick());
		for (int i = 0; i < numOfItems; i++) {
			if (itemsToDrop.peek() == null) {
				dropping = false;
				return;
			}
			InventoryInteraction.useItem(itemsToDrop.poll(), "Drop");
		}
		if (itemsToDrop.isEmpty()) dropping = false;
	}

	public int getRandomIntBetweenRange(int min, int max) {
		return (int) ((Math.random() * ((max - min) + 1)) + min);
	}
}


