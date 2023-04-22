package net.runelite.client.plugins.microbot.scripts.bosses.enums;

import net.runelite.api.NpcID;

public enum ZulrahType
{
	RANGE, MAGIC, MELEE;

	private static final int ZULRAH_RANGE = NpcID.ZULRAH;
	private static final int ZULRAH_MELEE = NpcID.ZULRAH_2043;
	private static final int ZULRAH_MAGIC = NpcID.ZULRAH_2044;

	public static ZulrahType valueOf(int zulrahId)
	{
		switch (zulrahId)
		{
			case ZULRAH_RANGE:
				return ZulrahType.RANGE;
			case ZULRAH_MELEE:
				return ZulrahType.MELEE;
			case ZULRAH_MAGIC:
				return ZulrahType.MAGIC;
		}
		return null;
	}
}