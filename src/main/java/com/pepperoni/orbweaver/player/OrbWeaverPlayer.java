package com.pepperoni.orbweaver.player;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

@Getter
@Setter
public class OrbWeaverPlayer
{
	private final String username;
	private short modelId;
	private byte modelType;
	private short orientation;
	private WorldPoint location;
	private byte team;
	private byte status;

	public OrbWeaverPlayer(String username)
	{
		this.username = username;
	}
}
