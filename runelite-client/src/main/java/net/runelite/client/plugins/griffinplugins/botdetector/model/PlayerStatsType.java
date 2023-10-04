package net.runelite.client.plugins.griffinplugins.botdetector.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
public enum PlayerStatsType
{
	@SerializedName("manual")
	MANUAL("Manual", "Manual uploading statistics, uploads from manually flagging a player as a bot.", true),
	@SerializedName("passive")
	PASSIVE("Auto", "Passive uploading statistics, uploads from simply seeing other players in-game.", false),
	@SerializedName("total")
	TOTAL("Total", "Total uploading statistics, both passive and manual.", false)
	;

	private final String shorthand;
	private final String description;
	@Accessors(fluent = true)
	private final boolean canDisplayAccuracy;

	@Override
	public String toString()
	{
		return shorthand;
	}
}
