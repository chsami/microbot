package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.resource;

import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.LucidGauntletPlugin;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

import java.awt.image.BufferedImage;

class ResourceCounter extends Counter
{
    private final Resource resource;

    private int count;
    private String text;

    ResourceCounter(final LucidGauntletPlugin plugin, final Resource resource, final BufferedImage bufferedImage, final int count)
    {
        super(bufferedImage, plugin, count);

        this.resource = resource;
        this.count = count;
        this.text = String.valueOf(count);

        setPriority(getPriority(resource));
    }

    @Override
    public String getText()
    {
        return text;
    }

    public void incrementCount(int count)
    {
        this.count += Math.max(0, count);
        this.text = String.valueOf(this.count);
    }

    public void decrementCount(int count)
    {
        this.count -= Math.max(0, count);
        this.text = String.valueOf(this.count);
    }

    private static InfoBoxPriority getPriority(final Resource resource)
    {
        switch (resource)
        {
            case CRYSTAL_ORE:
            case CORRUPTED_ORE:
            case PHREN_BARK:
            case CORRUPTED_PHREN_BARK:
            case LINUM_TIRINUM:
            case CORRUPTED_LINUM_TIRINUM:
                return InfoBoxPriority.HIGH;
            case GRYM_LEAF:
            case CORRUPTED_GRYM_LEAF:
                return InfoBoxPriority.MED;
            case CRYSTAL_SHARDS:
            case CORRUPTED_SHARDS:
            case RAW_PADDLEFISH:
                return InfoBoxPriority.NONE;
            default:
                return InfoBoxPriority.LOW;
        }
    }
}
