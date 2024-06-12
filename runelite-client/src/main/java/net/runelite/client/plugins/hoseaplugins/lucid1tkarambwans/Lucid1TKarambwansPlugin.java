package net.runelite.client.plugins.hoseaplugins.lucid1tkarambwans;

import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

@PluginDescriptor(name = PluginDescriptor.Lucid + "1T Karambwans</html>", description = "It 1 tick cooks karambwans, duh.", enabledByDefault = false)
public class Lucid1TKarambwansPlugin extends Plugin implements KeyListener
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private Lucid1TKarambwansPanelOverlay overlay;

    @Inject
    private KeyManager keyManager;

    @Inject
    private Lucid1TKarambwansConfig config;

    @Getter
    private boolean running = false;

    private boolean spaceDown = false;
    private int spaceDownTick = 0;

    private long clientTicks  = 0;

    private long lastBankTick = 0;

    @Getter
    private int ticksRunning = 0;

    private Random random = new Random();

    @Getter
    private int nextBreakTick = 0;

    @Getter
    private int breakTicks = 0;

    @Provides
    Lucid1TKarambwansConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(Lucid1TKarambwansConfig.class);
    }

    @Override
    protected void startUp()
    {
        clientThread.invoke(this::start);

        if (!overlayManager.anyMatch(p -> p == overlay))
        {
            overlayManager.add(overlay);
        }
    }

    @Override
    protected void shutDown()
    {
        clientThread.invoke(this::stop);

        if (overlayManager.anyMatch(p -> p == overlay))
        {
            overlayManager.remove(overlay);
        }
    }

    private void start()
    {
        resetToDefault();
        keyManager.registerKeyListener(this);
    }

    private void stop()
    {
        resetToDefault();
        keyManager.unregisterKeyListener(this);
        if (spaceDown)
        {
            spaceUp();
        }
    }

    @Subscribe
    private void onClientTick(final ClientTick event)
    {
        if (!running)
        {
            return;
        }

        if (breakTicks > 0)
        {
            if (spaceDown)
            {
                spaceUp();
            }
            return;
        }

        clientTicks++;

        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        if (client.getTickCount() <= lastBankTick)
        {
            return;
        }

        if (BankUtils.isOpen())
        {
            if (spaceDown)
            {
                spaceUp();
            }
            return;
        }

        if (!bankExists())
        {
            return;
        }

        TileObject range = getRange();
        if (range == null)
        {
            return;
        }

        if (!InventoryUtils.contains(ItemID.RAW_KARAMBWAN))
        {
            return;
        }

        if (InteractionUtils.isMoving())
        {
            if (spaceDown)
            {
                spaceUp();
            }
            return;
        }

        if (InteractionUtils.approxDistanceTo(range.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) != 1)
        {
            InteractionUtils.useLastIdOnWallObject(ItemID.RAW_KARAMBWAN, getRange());
            return;
        }

        if (!spaceDown)
        {
            spaceDown();
            spaceDownTick = client.getTickCount();
        }
        else if (client.getTickCount() != spaceDownTick)
        {
            spaceDown();
        }

        if (clientTicks % 18 == 0 && client.isKeyPressed(KeyCode.KC_SPACE))
        {
            if (config.randomlyMiss() && random.nextInt(5000) < config.missedPerHour() + 1)
            {
                return;
            }

            InteractionUtils.useLastIdOnWallObject(ItemID.RAW_KARAMBWAN, getRange());
        }
    }

    public TileObject getRange()
    {
        return config.cookingLocation() == Lucid1TKarambwansConfig.CookingLocation.CUSTOM ? GameObjectUtils.nearest(config.rangeId()) : GameObjectUtils.nearest(config.cookingLocation().getRangeId());
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {
        if (!running)
        {
            return;
        }

        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        if (!bankExists())
        {
            MessageUtils.addMessage("No nearby bank.", Color.RED);
            running = false;
            return;
        }

        TileObject range = getRange();
        if (range == null)
        {
            MessageUtils.addMessage("No nearby range.", Color.RED);
            running = false;
            return;
        }

        if (config.takeBreaks())
        {
            if (breakTicks > 0)
            {
                nextBreakTick = 0;
                breakTicks--;
                return;
            }

            if (nextBreakTick == 0)
            {
                int randomChunk = random.nextInt(config.breakAfter() / 10);
                nextBreakTick = client.getTickCount() + config.breakAfter() + (random.nextInt(2) == 0 ? randomChunk : -randomChunk);
            }
        }

        ticksRunning++;

        if (InteractionUtils.isMoving())
        {
            return;
        }

        clientTicks = 0;

        if (BankUtils.isOpen())
        {
            if (!InventoryUtils.contains(ItemID.RAW_KARAMBWAN) && InventoryUtils.getFreeSlots() != 28)
            {
                BankUtils.depositAll();
            }

            if (InventoryUtils.getFreeSlots() > 0)
            {
                if (InventoryUtils.getItemCount(ItemID.RAW_KARAMBWAN, 12, 13) > 0)
                {
                    BankUtils.withdrawAll(ItemID.RAW_KARAMBWAN);
                    BankUtils.close();
                    lastBankTick = client.getTickCount();
                }
                else
                {
                    MessageUtils.addMessage("Out of Karambwans to cook.", Color.RED);
                    resetToDefault();
                    BankUtils.close();
                    running = false;
                }
            }
        }
        else if (!InventoryUtils.contains(ItemID.RAW_KARAMBWAN))
        {
            if (config.takeBreaks() && client.getTickCount() > nextBreakTick && breakTicks == 0)
            {
                int randomChunk = random.nextInt(config.breakFor() / 10);
                breakTicks = config.breakFor() + (random.nextInt(2) == 0 ? randomChunk : -randomChunk);
                return;
            }

            openBank();
        }
    }

    private void resetToDefault()
    {
        ticksRunning = 0;
        breakTicks = 0;
        nextBreakTick = 0;
        clientTicks = 0;
    }

    public void openBank()
    {
        if (!bankExists())
        {
            MessageUtils.addMessage("There's no bank nearby with that config.", Color.RED);
            return;
        }

        if (InteractionUtils.isMoving())
        {
            return;
        }

        if (config.cookingLocation() == Lucid1TKarambwansConfig.CookingLocation.CUSTOM)
        {
            switch (config.bankType())
            {
                case OBJECT:
                    GameObjectUtils.interact(GameObjectUtils.nearest(config.bankName()), config.bankAction());
                    break;
                case NPC:
                    NpcUtils.interact(NpcUtils.getNearestNpc(config.bankName()), config.bankAction());
                    break;
            }
        }
        else
        {
            switch (config.cookingLocation().getBankingType())
            {
                case OBJECT:
                    GameObjectUtils.interact(GameObjectUtils.nearest(config.cookingLocation().getBankName()), config.cookingLocation().getBankAction());
                    break;
                case NPC:
                    NpcUtils.interact(NpcUtils.getNearestNpc(config.cookingLocation().getBankName()), config.cookingLocation().getBankAction());
                    break;
            }
        }
    }

    public boolean bankExists()
    {
        if (config.cookingLocation() == Lucid1TKarambwansConfig.CookingLocation.CUSTOM)
        {
            switch (config.bankType())
            {
                case OBJECT:
                    return GameObjectUtils.nearest(config.bankName()) != null;
                case NPC:
                    return NpcUtils.getNearestNpc(config.bankName()) != null;
            }
        }
        else
        {
            switch (config.cookingLocation().getBankingType())
            {
                case OBJECT:
                    return GameObjectUtils.nearest(config.cookingLocation().getBankName()) != null;
                case NPC:
                    return NpcUtils.getNearestNpc(config.cookingLocation().getBankName()) != null;
            }
        }

        return false;
    }

    private void spaceDown()
    {
        this.client.getCanvas().dispatchEvent(new KeyEvent(this.client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, 32, ' ', 1));
        this.client.getCanvas().dispatchEvent(new KeyEvent(this.client.getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, 0, ' ', 0));
        spaceDown = true;
    }

    private void spaceUp()
    {
        this.client.getCanvas().dispatchEvent(new KeyEvent(this.client.getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 32, ' ', 1));
        spaceDown = false;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (client == null || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        if (config.autoToggle().matches(e))
        {
            clientThread.invoke(() -> {
                if (spaceDown)
                {
                    spaceUp();
                }
                running = !running;
                resetToDefault();
            });
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
