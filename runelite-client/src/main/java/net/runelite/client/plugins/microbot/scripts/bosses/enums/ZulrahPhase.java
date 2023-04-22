package net.runelite.client.plugins.microbot.scripts.bosses.enums;

import net.runelite.api.Prayer;
import net.runelite.api.coords.LocalPoint;

import java.awt.*;

public class ZulrahPhase
{
	private static final Color RANGE_COLOR = new Color(150, 255, 0, 100);
	private static final Color MAGIC_COLOR = new Color(20, 170, 200, 100);
	private static final Color MELEE_COLOR = new Color(180, 50, 20, 100);
	private static final Color JAD_COLOR = new Color(255, 115, 0, 100);

	private final ZulrahLocation zulrahLocation;
	private final ZulrahType type;
	private final boolean jad;
	private final StandLocation standLocation;
	private final Prayer prayer;

	public ZulrahPhase(ZulrahLocation zulrahLocation, ZulrahType type, boolean jad, StandLocation standLocation, Prayer prayer)
	{
		this.zulrahLocation = zulrahLocation;
		this.type = type;
		this.jad = jad;
		this.standLocation = standLocation;
		this.prayer = prayer;
	}

	public static ZulrahPhase valueOf(net.runelite.api.NPC zulrah, LocalPoint start)
	{
		ZulrahLocation zulrahLocation = ZulrahLocation.valueOf(start, zulrah.getLocalLocation());
		ZulrahType zulrahType = ZulrahType.valueOf(zulrah.getId());
		if (zulrahLocation == null || zulrahType == null)
		{
			return null;
		}
		StandLocation standLocation = zulrahType == ZulrahType.MAGIC ? StandLocation.PILLAR_WEST_OUTSIDE : StandLocation.TOP_EAST;
		Prayer prayer = zulrahType == ZulrahType.MAGIC ? Prayer.PROTECT_FROM_MAGIC : null;
		return new ZulrahPhase(zulrahLocation, zulrahType, false, standLocation, prayer);
	}

	@Override
	public String toString()
	{
		return "ZulrahPhase{" +
			"zulrahLocation=" + zulrahLocation +
			", type=" + type +
			", jad=" + jad +
			", standLocation=" + standLocation +
			", prayer=" + prayer +
			'}';
	}

	// world location
	public LocalPoint getZulrahTile(LocalPoint startTile)
	{
		// NORTH doesn't need changing because it is the start
		switch (zulrahLocation)
		{
			case SOUTH:
				return new LocalPoint(startTile.getX(), startTile.getY() - (11 * 128));
			case EAST:
				return new LocalPoint(startTile.getX() + (10 * 128), startTile.getY() - (2 * 128));
			case WEST:
				return new LocalPoint(startTile.getX() - (10 * 128), startTile.getY() - (2 * 128));
		}
		return startTile;
	}

	// world location
	public LocalPoint getStandTile(LocalPoint startTile)
	{
		switch (standLocation)
		{
			case WEST:
				return new LocalPoint(startTile.getX() - (5 * 128), startTile.getY());
			case EAST:
				return new LocalPoint(startTile.getX() + (5 * 128), startTile.getY() - (2 * 128));
			case SOUTH:
				return new LocalPoint(startTile.getX(), startTile.getY() - (6 * 128));
			case SOUTH_WEST:
				return new LocalPoint(startTile.getX() - (4 * 128), startTile.getY() - (4 * 128));
			case SOUTH_EAST:
				return new LocalPoint(startTile.getX() + (2 * 128), startTile.getY() - (6 * 128));
			case TOP_EAST:
				return new LocalPoint(startTile.getX() + (6 * 128), startTile.getY() + (2 * 128));
			case TOP_WEST:
				return new LocalPoint(startTile.getX() - (4 * 128), startTile.getY() + (3 * 128));
			case PILLAR_WEST_INSIDE:
				return new LocalPoint(startTile.getX() - (4 * 128), startTile.getY() - (3 * 128));
			case PILLAR_WEST_OUTSIDE:
				return new LocalPoint(startTile.getX() - (5 * 128), startTile.getY() - (3 * 128));
			case PILLAR_EAST_INSIDE:
				return new LocalPoint(startTile.getX() + (4 * 128), startTile.getY() - (3 * 128));
			case PILLAR_EAST_OUTSIDE:
				return new LocalPoint(startTile.getX() + (4 * 128), startTile.getY() - (4 * 128));
		}
		return startTile;
	}

	public ZulrahType getType()
	{
		return type;
	}

	public boolean isJad()
	{
		return jad;
	}

	public StandLocation getStandLocation()
	{
		return standLocation;
	}

	public Prayer getPrayer()
	{
		return prayer;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}
		ZulrahPhase other = (ZulrahPhase) obj;
		return this.jad == other.jad && this.zulrahLocation == other.zulrahLocation && this.type == other.type;
	}

	public Color getColor()
	{
		if (jad)
		{
			return JAD_COLOR;
		}
		else
		{
			switch (type)
			{
				case RANGE:
					return RANGE_COLOR;
				case MAGIC:
					return MAGIC_COLOR;
				case MELEE:
					return MELEE_COLOR;
			}
		}
		return RANGE_COLOR;
	}
}